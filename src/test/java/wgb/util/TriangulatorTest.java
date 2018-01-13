package wgb.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import javafx.geometry.Point2D;
import wgb.domain.Triangle;

public class TriangulatorTest {

	@Test
	public void test() throws Exception {

		List<Point2D> points = new ArrayList<Point2D>();
		points.add(new Point2D(0, 0));
		points.add(new Point2D(1, 0));
		points.add(new Point2D(1, 1));
		points.add(new Point2D(0, 1));

		Triangulator tlator = new Triangulator(points);
		List<Triangle> triangles = tlator.triangulate();
		assertEquals(2, triangles.size());

	}

}
