package wgb.domain;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class AirfoilTest {

	private final static Logger logger = LogManager.getLogger();
	private SeligAirfoilReader afr = new SeligAirfoilReader();

	@Test
	public void test() throws Exception {

		Airfoil af = afr.read(new File("sampledata/test.dat"));
		assertTrue(af.getXy().size() > 0);
		logger.debug("xy size = " + af.getXy().size());
		logger.debug("length = " + af.getRawLength());
	}

}
