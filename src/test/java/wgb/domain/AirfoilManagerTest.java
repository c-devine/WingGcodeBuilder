package wgb.domain;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import wgb.test.WgbSpringBootTest;

public class AirfoilManagerTest extends WgbSpringBootTest {

	@Autowired
	AirfoilManager afManager;

	@Test
	public void test() {
		assertTrue(afManager.isSupported(new File("sampledata/mh45.dat")));
	}

}
