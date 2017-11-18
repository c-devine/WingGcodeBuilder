package wgb.domain;

import java.io.File;

import org.junit.Test;

public class RadialInterpolatorTest {

	private RadialInterpolator seg = new RadialInterpolator();
	private SeligAirfoilReader afr = new SeligAirfoilReader();

	@Test
	public void test() throws Exception {
		Airfoil af = afr.read(new File("sampledata/mh45.dat"));
		seg.interpolate(af.getXy(), 200);
	}

}
