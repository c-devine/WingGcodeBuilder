package wgb.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import javafx.geometry.Point2D;
import toxi.geom.Spline2D;
import toxi.geom.Vec2D;
import wgb.util.FoilUtil;

@Component
public class Densifier {

	public List<Point2D> densify(List<Point2D> points, double segmentLength) {

		Spline2D spline = new Spline2D();
		spline.setTightness(0.00001f);
		points.forEach(p -> spline.add((float) p.getX(), (float) p.getY()));
		List<Vec2D> verts = spline.getDecimatedVertices((float) segmentLength);
		return verts.stream().map(v -> new Point2D(v.x, v.y)).collect(Collectors.toList());
	}

	public List<Point2D> densify(List<Point2D> points, int numPoints) {
		Spline2D spline = new Spline2D();
		spline.setTightness(0.00001f);
		points.forEach(p -> spline.add((float) p.getX(), (float) p.getY()));
		double segmentLength = FoilUtil.findRawLength(points) / (numPoints - 1);
		List<Vec2D> verts = spline.getDecimatedVertices((float) segmentLength);

		// extra check to make sure number of points equals numPoints passed in
		List<Point2D> dPoints = verts.stream().map(v -> new Point2D(v.x, v.y)).collect(Collectors.toList());

		while (dPoints.size() != numPoints) {
			if (dPoints.size() > numPoints) {
				dPoints.remove(1);
			} else {
				dPoints.add(1, FoilUtil.calcPoint(dPoints.get(0), dPoints.get(1),
						dPoints.get(0).distance(dPoints.get(1)) / 2));
			}

		}
		return dPoints;
	}

	public List<Point2D> densifyOld(List<Point2D> points, Length segmentLength) {

		double segLength = segmentLength.asMM();

		return IntStream.range(1, points.size())
				.mapToObj(i -> findInterpolated(points.get(i - 1), points.get(i), segLength, i != 1))
				.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);

	}

	private List<Point2D> findInterpolated(Point2D start, Point2D end, double segLength, boolean skipFirst) {

		List<Point2D> retList = new ArrayList<Point2D>();
		int numSamples = (int) (start.distance(end) / segLength);
		if (!skipFirst)
			retList.add(start);
		for (int i = 0; i < numSamples; i++) {
			retList.add(FoilUtil.calcPoint(start, end, segLength * (i + 1)));
		}
		retList.add(end);

		return retList;
	}

}
