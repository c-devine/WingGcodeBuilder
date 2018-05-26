package wgb.domain.measure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import wgb.domain.measure.Unit;
import wgb.domain.measure.Weight;

public class WeightTest {

	private double delta = 0.0001;

	@Test
	public void test() {

		Weight sut = new Weight(1.0, Unit.GM);

		assertEquals(1.0, sut.asGM(), delta);
		assertEquals(1.0 / 28.3495, sut.asOunce(), delta);

		sut = new Weight(1.0, Unit.OZ);
		assertEquals(1.0 * 28.3495, sut.asGM(), delta);
		assertEquals(1.0 * 28.3495, Weight.fromOunce(1.0), delta);
	}

}
