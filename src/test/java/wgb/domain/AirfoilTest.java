package wgb.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javafx.geometry.Point2D;
import wgb.util.FoilUtil;

public class AirfoilTest {

	private final static Logger logger = LogManager.getLogger();
	private SeligAirfoilReader afr = new SeligAirfoilReader();

	@Test
	public void test() throws Exception {

		Airfoil af = afr.read(new File("sampledata/test.dat"));
		assertTrue(af.getXy().size() > 0);
		logger.debug("xy size = " + af.getXy().size());
	}

	@Test
	public void testGetScaled() {

		Point2D centroid = new Point2D(.5, .5);
		List<Point2D> pts = IntStream.rangeClosed(0, 360).mapToObj(i -> FoilUtil.calcPoint(centroid, i, 0.5))
				.collect(Collectors.toList());
		assertEquals(1.0, FoilUtil.findMaxY(pts), 0.00001);

		Airfoil af = new Airfoil();
		af.setXy(pts);
		af.setChord(new Length(10, Unit.MM));
		assertEquals(10.0, FoilUtil.findMaxX(af.getScaled(Transform.XSCALE.getMask(), Unit.MM)), 0.00001);
		af.setyScale(2.0);
		assertEquals(20.0,
				FoilUtil.findMaxY(af.getScaled(Transform.calcMask(Transform.XSCALE, Transform.YSCALE), Unit.MM)),
				0.00001);

	}

}
