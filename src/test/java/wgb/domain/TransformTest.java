package wgb.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransformTest {

	@Test
	public void test() {

		assertTrue(Transform.ALL.isSet(Transform.OFFSET.getMask()));
		assertFalse(Transform.TWIST.isSet(0x1));
		assertTrue(Transform.TWIST.isSet(Transform.TWIST.getMask()));
	}

}
