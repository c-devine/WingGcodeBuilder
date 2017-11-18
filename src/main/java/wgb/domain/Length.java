package wgb.domain;

public class Length {

	private static double MM_PER_INCH = 25.4;

	// length is stored internally as millimeters
	private double len;

	public Length(double length, Unit unit) {
		setLength(length, unit);
	}

	// only support mm and inch
	public double getLength(Unit unit) {
		if (unit.getName().equals(Unit.MM.getName()))
			return len;
		else
			return asInch();
	}

	// only support mm and inch
	public void setLength(double length, Unit unit) {
		switch (unit) {
		case MM:
			len = length;
			break;
		case INCH:
			len = Length.fromInch(length);
			break;
		default:
			len = length;
		}
	}

	public static double fromInch(double inch) {
		return inch * MM_PER_INCH;
	}

	public double asInch() {
		return len / MM_PER_INCH;
	}

	public static double fromMM(double mm) {
		return mm;
	}

	public double asMM() {
		return len;
	}

	public static double fromCM(double cm) {
		return cm * 10;
	}

	public double asCM() {
		return len / 10;
	}

	public static double fromM(double m) {
		return m * 1000;
	}

	public double asM() {
		return len / 1000;
	}

}
