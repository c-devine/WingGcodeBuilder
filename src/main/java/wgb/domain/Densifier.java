package wgb.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javafx.geometry.Point2D;
import toxi.geom.Spline2D;
import toxi.geom.Vec2D;
import wgb.util.FoilUtil;

@Component
public class Densifier {

	private final static Logger logger = LogManager.getLogger();

	public List<Point2D> densify(List<Point2D> points, double segmentLength) {

		Spline2D spline = new Spline2D();
		points.forEach(p -> spline.add((float) p.getX(), (float) p.getY()));
		List<Vec2D> verts = spline.getDecimatedVertices((float) segmentLength);
		return verts.stream().map(v -> new Point2D(v.x, v.y)).collect(Collectors.toList());
	}

	public List<Point2D> densify(List<Point2D> points, int numPoints) {
		Spline2D spline = new Spline2D();
		points.forEach(p -> spline.add((float) p.getX(), (float) p.getY()));
		double segmentLength = FoilUtil.findRawLength(points) / numPoints;
		List<Vec2D> verts = spline.getDecimatedVertices((float) segmentLength);
		return verts.stream().map(v -> new Point2D(v.x, v.y)).collect(Collectors.toList());
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
