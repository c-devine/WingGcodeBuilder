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

	public List<String> generateGcode(Airfoil left, Airfoil right, GcodeSettings settings, boolean mirrored) {

		// try to create a point list of equal size and ~1mm in length
		int numSegments = (int) (FoilUtil.findRawLength(left.getXy()) * left.getChord().asMM());

		List<Point2D> lPts = densifier.densify(left.getXy(), numSegments);
		List<Point2D> rPts = densifier.densify(right.getXy(), numSegments);

		// scale + kerf
		double kerf = settings.getKerf();
		lPts = FoilUtil.scale(lPts, new Point2D(0.0, 0.0), left.getChord().asMM() + kerf,
				left.getChord().asMM() + kerf);
		rPts = FoilUtil.scale(rPts, new Point2D(0.0, 0.0), right.getChord().asMM() + kerf,
				right.getChord().asMM() + kerf);

		// offset
		lPts = FoilUtil.offset(lPts, left.getOffset().asMM(), 0);
		rPts = FoilUtil.offset(rPts, right.getOffset().asMM(), 0);

		// flip x axis
		double maxX = Math.max(FoilUtil.findMaxX(lPts), FoilUtil.findMaxX(rPts));
		lPts = FoilUtil.flipX(lPts, maxX);
		rPts = FoilUtil.flipX(rPts, maxX);
		// add leadin
		lPts = FoilUtil.offset(lPts, settings.getLeadin(), 0);
		rPts = FoilUtil.offset(rPts, settings.getLeadin(), 0);

		List<String> retList = new ArrayList<String>(getMainPrefix(settings));

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

	private List<String> getMainPrefix(GcodeSettings settings) {

		List<String> prefix = new ArrayList<String>();
		prefix.add("G21 ; set units to millimeters");
		prefix.add("G90 ; use absolute coordinates");
		prefix.add("M204 S" + settings.getAcc() + " ; set default acceleration");
		prefix.add(getGcodeString("G92", new Point2D(0, 0), new Point2D(0, 0), settings) + " ; reset axis");
		prefix.add("G1 F" + settings.getF() + " ; feed rate");
		return prefix;

	}

}
