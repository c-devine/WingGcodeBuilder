package wgb.domain;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javafx.geometry.Point2D;

public class DensifierTest {

	private final static Logger logger = LogManager.getLogger();
	private SeligAirfoilReader afr = new SeligAirfoilReader();
	private Densifier sut = new Densifier();

	@Test
	public void test() throws Exception {

		Airfoil af = afr.read(new File("sampledata/mh45.dat"));
		List<Point2D> points = sut.densify(af.getXy(), 0.004);
		points.forEach(p -> logger.debug(p.getX() + "," + p.getY()));
	}

}
