package wgb.domain.measure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import wgb.domain.measure.Unit;

public class UnitTest {

	@Test
	public void test() {
		Enum<Unit> u = Unit.getEnum("millimeter");
		assertEquals(Unit.MM, u);
	}

}
