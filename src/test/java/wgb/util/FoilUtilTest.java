package wgb.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javafx.geometry.Point2D;

public class FoilUtilTest {

	private final static Logger logger = LogManager.getLogger();

	private double delta = 0.00001;

	@Test
	public void testcalcPoint() {
		assertEquals(10.1234, FoilUtil.roundDouble(10.123456789, 4), delta);

		Point2D pt = new Point2D(0.0, 0.0);
		assertEquals(10.0, FoilUtil.calcPoint(pt, 90, 10.0).getY(), delta);
		assertEquals(10.0, FoilUtil.calcPoint(pt, 0, 10.0).getX(), delta);

		assertEquals(3.0, FoilUtil.calcPoint(pt, 53.13, 5).getX(), delta);
		assertEquals(3, FoilUtil.calcPoint(pt, new Point2D(3, 4), 5).getX(), delta);
		assertEquals(-3, FoilUtil.calcPoint(pt, new Point2D(-3, -4), 5).getX(), delta);
		assertEquals(-3, FoilUtil.calcPoint(pt, new Point2D(-3, 4), 5).getX(), delta);
		assertEquals(3, FoilUtil.calcPoint(pt, new Point2D(3, -4), 5).getX(), delta);

	}

	@Test
	public void testCentroid() {

		List<Point2D> points = getTestPoints();
		Point2D centroid = FoilUtil.calcCentroid(points);
		assertEquals(new Point2D(.5, .5), centroid);
	}

	@Test
	public void testScale() {
		List<Point2D> points = getTestPoints();
		List<Point2D> scaled = FoilUtil.scale(points, new Point2D(0.0, 0.0), 2.0, 2.0);
		// scaled.forEach(p -> logger.debug(p));
		assertEquals(2.0, scaled.get(2).getX() - scaled.get(0).getX(), delta);
	}

	private List<Point2D> getTestPoints() {

		double[] values = { 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0 };
		List<Point2D> points = new ArrayList<Point2D>();

		for (int i = 0; i < values.length; i += 2) {
			points.add(new Point2D(values[i], values[i + 1]));
		}

		return points;
	}
}
