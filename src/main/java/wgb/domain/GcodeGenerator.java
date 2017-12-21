package wgb.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.geometry.Point2D;
import wgb.util.FoilUtil;

@Component
public class GcodeGenerator {

	@Autowired
	Densifier densifier;

	public List<String> generateGcode(Airfoil left, Airfoil right, GcodeSettings settings, Unit unit,
			boolean mirrored) {

		// try to create a point list of equal size and ~1mm in length
		int numSegments = (int) (FoilUtil.findRawLength(left.getXy()) * left.getChord().asMM());

		List<Point2D> lPts = densifier.densify(left.getXy(), numSegments);
		List<Point2D> rPts = densifier.densify(right.getXy(), numSegments);

		// scale + kerf
		double kerf = settings.getKerf().getLength(unit);
		lPts = FoilUtil.scale(lPts, new Point2D(0.0, 0.0), left.getChord().getLength(unit) + kerf,
				left.getChord().getLength(unit) + kerf);
		rPts = FoilUtil.scale(rPts, new Point2D(0.0, 0.0), right.getChord().getLength(unit) + kerf,
				right.getChord().getLength(unit) + kerf);

		// apply twist
		Point2D rotateAxis = new Point2D(1.0, 0.0);
		lPts = FoilUtil.rotate(lPts, rotateAxis, left.getTwist());
		rPts = FoilUtil.rotate(rPts, rotateAxis, right.getTwist());

		// offset
		lPts = FoilUtil.offset(lPts, left.getOffset().getLength(unit), 0);
		rPts = FoilUtil.offset(rPts, right.getOffset().getLength(unit), 0);

		// project foil coordinates to the axis of the machine
		List<List<Point2D>> projected = FoilUtil.projectPoints(lPts, rPts, settings.getTowerWidth(),
				settings.getTowerOffset(), settings.getBlockWidth());
		lPts = projected.get(0);
		rPts = projected.get(1);

		// flip x axis
		double maxX = Math.max(FoilUtil.findMaxX(lPts), FoilUtil.findMaxX(rPts));
		lPts = FoilUtil.flipX(lPts, maxX);
		rPts = FoilUtil.flipX(rPts, maxX);

		// add leadin
		lPts = FoilUtil.offset(lPts, settings.getLeadin().getLength(unit), 0);
		rPts = FoilUtil.offset(rPts, settings.getLeadin().getLength(unit), 0);

		List<String> retList = new ArrayList<String>();
		// add the summary info
		retList.addAll(getSummaryInfo(left, lPts, right, rPts, unit));
		retList.addAll(getMainPrefix(settings, unit));

		for (int i = 0; i < Math.min(lPts.size(), rPts.size()); i++) {

			if (!mirrored)
				retList.add(getGcodeString("G1", lPts.get(i), rPts.get(i), settings));
			else
				retList.add(getGcodeString("G1", rPts.get(i), lPts.get(i), settings));

		}

		retList.add(getGcodeString("G1", new Point2D(0, 0), new Point2D(0, 0), settings));

		// turn off the motors
		retList.add("M18 ; turn stepper motors off");

		return retList;

	}

	private String getGcodeString(String code, Point2D sLeft, Point2D sRight, GcodeSettings settings) {

		StringBuffer sb = new StringBuffer();
		sb.append(code + " " + settings.getX1() + String.format("%.6f", sLeft.getX()) + " " + settings.getY1()
				+ String.format("%.6f", sLeft.getY()) + " ");
		sb.append(settings.getX2() + String.format("%.6f", sRight.getX()) + " " + settings.getY2()
				+ String.format("%.6f", sRight.getY()));

		return sb.toString();
	}

	private List<String> getMainPrefix(GcodeSettings settings, Unit unit) {

		List<String> prefix = new ArrayList<String>();
		prefix.add(unit.equals(Unit.MM) ? "G21 ; set units to millimeters" : "G20 ; set units to inches");
		prefix.add("G90 ; use absolute coordinates");
		prefix.add(String.format("M204 S%.6f ; set default acceleration", settings.getAcc().getLength(unit)));
		prefix.add(getGcodeString("G92", new Point2D(0, 0), new Point2D(0, 0), settings) + " ; reset axis");
		prefix.add(String.format("G1 F%.6f ; feed rate", settings.getF().getLength(unit)));
		return prefix;

	}

	private List<String> getSummaryInfo(Airfoil left, List<Point2D> leftPoints, Airfoil right,
			List<Point2D> rightPoints, Unit unit) {

		List<String> summary = new ArrayList<String>();

		summary.add(String.format("; Left: airfoil: %s chord: %s", left.getName(), getMI(left.getChord())));
		summary.add(String.format("; Right: airfoil: %s chord: %s", right.getName(), getMI(right.getChord())));
		summary.add(
				String.format("; Left minX: %s Left maxX: %s", getMI(new Length(FoilUtil.findMinX(leftPoints), unit)),
						getMI(new Length(FoilUtil.findMaxX(leftPoints), unit))));
		summary.add(
				String.format("; Left minY: %s Left maxY: %s", getMI(new Length(FoilUtil.findMinY(leftPoints), unit)),
						getMI(new Length(FoilUtil.findMaxY(leftPoints), unit))));
		summary.add(String.format("; Right minX: %s Right maxX: %s",
				getMI(new Length(FoilUtil.findMinX(rightPoints), unit)),
				getMI(new Length(FoilUtil.findMaxX(rightPoints), unit))));
		summary.add(String.format("; Right minY: %s Right maxY: %s",
				getMI(new Length(FoilUtil.findMinY(rightPoints), unit)),
				getMI(new Length(FoilUtil.findMaxY(rightPoints), unit))));

		return summary;
	}

	private String getMI(Length len) {
		return String.format("%.2f mm / %.2f inch(es)", len.asMM(), len.asInch());
	}

}
