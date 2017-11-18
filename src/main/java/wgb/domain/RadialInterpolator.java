package wgb.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javafx.geometry.Point2D;
import toxi.geom.Line2D;
import toxi.geom.Vec2D;
import wgb.util.FoilUtil;

@Component
public class RadialInterpolator {

	private final static Logger logger = LogManager.getLogger();
	private final static int DEFAULT_SEGMENTS = 360;

	public List<Point2D> interpolate(List<Point2D> points, int numSegments) {

		List<Line2D> lines = createPolyline(points);
		Point2D centroid = new Point2D(.5, 0);

		List<Point2D> intersections = new ArrayList<Point2D>();

		for (int i = 0; i < numSegments; i++) {

			Line2D ray = createLine(centroid,
					FoilUtil.calcPoint(centroid, (360.0 / (double) (numSegments - 1)) * i, 2.0));

			List<Point2D> intersection = getIntersection(ray, lines);
			if (intersection != null) {

				intersections.add(intersection.get(0));
			}

		}

		// add the first point as the last point
		intersections.add(points.get(0));

		return intersections;
	}

	public List<Point2D> interpolate(List<Point2D> points) {

		return this.interpolate(points, DEFAULT_SEGMENTS);
	}

	public List<Point2D> getIntersection(Line2D l1, List<Line2D> lines) {

		List<Line2D> found = lines.stream()
				.filter(l -> l.intersectLine(l1).getType().equals(Line2D.LineIntersection.Type.INTERSECTING))
				.collect(Collectors.toList());

		if (!found.isEmpty())
			return found.stream().map(f -> f.intersectLine(l1)).map(li -> new Point2D(li.getPos().x, li.getPos().y))
					.collect(Collectors.toList());

		return null;
	}

	public Point2D findMinX(List<Point2D> points) {

		Point2D min = points.get(0);

		for (Point2D pt : points) {
			if (pt.getX() < min.getX())
				min = pt;
		}

		return min;

	}

	public Point2D findMaxX(List<Point2D> points) {

		Point2D max = points.get(0);

		for (Point2D pt : points) {
			if (pt.getX() > max.getX())
				max = pt;
		}

		return max;

	}

	public List<Line2D> createPolyline(List<Point2D> points) {

		return IntStream.range(1, points.size()).mapToObj(i -> createLine(points.get(i - 1), points.get(i)))
				.collect(Collectors.toList());
	}

	public Line2D createLine(Point2D pt1, Point2D pt2) {

		return new Line2D(new Vec2D((float) pt1.getX(), (float) pt1.getY()),
				new Vec2D((float) pt2.getX(), (float) pt2.getY()));

	}

	public List<Vec2D> createVecList(List<Point2D> points) {
		return points.stream().map(p -> new Vec2D((float) p.getX(), (float) p.getY())).collect(Collectors.toList());

	}

}
