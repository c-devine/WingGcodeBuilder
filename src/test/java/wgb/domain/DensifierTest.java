package wgb.domain;

import java.io.File;
import java.util.List;

import org.junit.Test;

import javafx.geometry.Point2D;

public class DensifierTest {

	private SeligAirfoilReader afr = new SeligAirfoilReader();
	private Densifier sut = new Densifier();

	@Test
	public void test() throws Exception {

		Airfoil af = afr.read(new File("sampledata/mh45.dat"));
		List<Point2D> points = sut.densify(af.getXy(), 0.004);
		// points.forEach(p -> logger.debug(p.getX() + "," + p.getY()));
	}

}
