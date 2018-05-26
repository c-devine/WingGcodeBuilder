package wgb.domain.measure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import wgb.domain.measure.Length;
import wgb.domain.measure.Unit;

public class LengthTest {

	private double delta = 0.0001;

	@Test
	public void test() {

		Length sut = new Length(0, Unit.MM);
		assertEquals(0, sut.asM(), delta);
		assertEquals(0, sut.asCM(), delta);

		sut = new Length(Length.fromInch(9.0), Unit.MM);
		assertEquals(9, sut.asInch(), delta);
		assertEquals(228.6, sut.asMM(), delta);

		sut = new Length(Length.fromMM(228.6), Unit.MM);
		assertEquals(9, sut.asInch(), delta);
		assertEquals(22.86, sut.asCM(), delta);
		assertEquals(.2286, sut.asM(), delta);

		assertEquals(228.6, sut.getLength(Unit.MM), delta);
		assertEquals(9, sut.getLength(Unit.INCH), delta);
	}

}
