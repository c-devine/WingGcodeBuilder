package wgb.domain.measure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = LengthSerializer.class)
@JsonDeserialize(using = LengthDeserializer.class)
public class Length {

	private static double MM_PER_INCH = 25.4;

	// length is stored internally as millimeters
	private double len;

	public Length(double length, Unit unit) {
		setLength(length, unit);
	}

	// recreate the length from the string passed in: "1.0 mm" or "1.0
	// millimeter"
	public Length(String sLength) {
		String[] split = sLength.split("\\s+");
		this.len = new Length(Double.parseDouble(split[0]), Unit.getEnum(split[1])).asMM();
	}

	public String toFormattedString(Unit unit) {

		String ext = "";
		switch (unit) {
		case INCH:
			ext = "inch(es)";
			break;
		case MM:
		default:
			ext = "mm(s)";
			break;
		}

		return String.format("%.2f %s", getLength(unit), ext);
	}

	// only support mm and inch
	public double getLength(Unit unit) {

		switch (unit) {
		case INCH:
			return asInch();
		case MM:
		default:
			return len;
		}
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
