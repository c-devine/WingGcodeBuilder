package wgb.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import wgb.domain.Triangle;

public class FoilUtil {

	public static double roundDouble(double d, int precision) {

		int i = (int) (d * Math.pow(10, precision));
		return i / Math.pow(10, precision);
	}

	public static Point2D calcPoint(Point2D p1, double angle, double distance) {

		double x2 = p1.getX() + (Math.cos(Math.toRadians(angle)) * distance);
		double y2 = p1.getY() + (Math.sin(Math.toRadians(angle)) * distance);

		return new Point2D(x2, y2);
	}

	public static Point2D calcPoint(Point2D p1, Point2D p2, double distance) {

		return calcPoint(p1, calcAngle(p1, p2), distance);
	}

	public static double calcAngle(Point2D p1, Point2D p2) {

		double deltaX = p2.getX() - p1.getX();
		double deltaY = p2.getY() - p1.getY();

		double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
		return (angle < 0) ? (360.0 + angle) : angle;
	}

	public static double findMaxX(List<Point2D> points) {

		return points.stream().mapToDouble(p -> p.getX()).max().orElse(0.0);
	}

	public static double findMinX(List<Point2D> points) {

		return points.stream().mapToDouble(p -> p.getX()).min().orElse(0.0);
	}

	public static double findMaxY(List<Point2D> points) {

		return points.stream().mapToDouble(p -> p.getY()).max().orElse(0.0);
	}

	public static double findMinY(List<Point2D> points) {

		return points.stream().mapToDouble(p -> p.getX()).min().orElse(0.0);
	}

	public static double findRawLength(List<Point2D> points) {

		return IntStream.range(1, points.size()).mapToDouble(i -> points.get(i).distance(points.get(i - 1))).sum();
	}

	public static Triangle transform(Triangle t, double x, double y, double z) {

		Point3D a = new Point3D(t.getA().getX() * x, t.getA().getY() * y, t.getA().getZ() * z);
		Point3D b = new Point3D(t.getB().getX() * x, t.getB().getY() * y, t.getB().getZ() * z);
		Point3D c = new Point3D(t.getC().getX() * x, t.getC().getY() * y, t.getC().getZ() * z);

		return new Triangle(a, b, c);
	}

	public static Triangle swap(Triangle t) {

		return new Triangle(t.getA(), t.getC(), t.getB());
	}

	public static List<Point2D> scale(List<Point2D> points, Point2D scaleAxis, double scaleX, double scaleY) {

		return points.stream().map(p -> {
			double x = ((p.getX() - scaleAxis.getX()) * scaleX) + scaleAxis.getX();
			double y = ((p.getY() - scaleAxis.getY()) * scaleY) + scaleAxis.getY();
			return new Point2D(x, y);
		}).collect(Collectors.toList());
	}

	public static List<Point2D> offset(List<Point2D> points, double offsetX, double offsetY) {

		return points.stream().map(p -> p.add(offsetX, offsetY)).collect(Collectors.toList());
	}

	public static List<Point2D> flipX(List<Point2D> points, double xAxis) {

		return points.stream().map(p -> new Point2D(xAxis - p.getX(), p.getY())).collect(Collectors.toList());
	}

	public static Point2D calcCentroid(List<Point2D> points) {

		double xsum = 0, ysum = 0, A = 0;
		for (int i = 0; i < points.size(); i++) {

			int iPlusOne = (i == points.size() - 1) ? 0 : i + 1;

			xsum += (points.get(i).getX() + points.get(iPlusOne).getX())
					* (points.get(i).getX() * points.get(iPlusOne).getY()
							- points.get(iPlusOne).getX() * points.get(i).getY());
			ysum += (points.get(i).getY() + points.get(iPlusOne).getY())
					* (points.get(i).getX() * points.get(iPlusOne).getY()
							- points.get(iPlusOne).getX() * points.get(i).getY());
			A += (points.get(i).getX() * points.get(iPlusOne).getY()
					- points.get(iPlusOne).getX() * points.get(i).getY());
		}
		A = A / 2;

		double x = xsum / (6 * A);
		double y = ysum / (6 * A);

		return new Point2D(x, y);
	}

	public static List<Point2D> rotate(List<Point2D> points, Point2D axis, double deg) {

		return points.stream().map(p -> rotate(p, axis, deg)).collect(Collectors.toList());
	}

	public static Point2D rotate(Point2D point, Point2D axis, double deg) {

		double rad = Math.toRadians(deg);
		double co = Math.cos(rad);
		double si = Math.sin(rad);

		Point2D pt = point.subtract(axis);

		return new Point2D(co * pt.getX() - si * pt.getY(), si * pt.getX() + co * pt.getY()).add(axis);
	}

}
