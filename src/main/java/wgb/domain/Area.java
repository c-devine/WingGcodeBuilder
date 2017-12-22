package wgb.domain;

public class Area {

	private static double SMM_PER_SINCH = 645.16;

	// area is stored internally as squared millimeters
	private double sqArea;

	public Area(double sqArea, Unit unit) {
		setSqArea(sqArea, unit);
	}

	// only support mm and inch
	public void setSqArea(double sqArea, Unit unit) {
		switch (unit) {
		case MM:
			this.sqArea = sqArea;
			break;
		case INCH:
			this.sqArea = Area.fromSqInch(sqArea);
			break;
		default:
			this.sqArea = sqArea;
		}
	}

	public String toFormattedString(Unit unit) {
		return String.format("%.2f %s", getArea(unit), unit.equals(Unit.MM) ? " sq mm(s)" : " sq inch(es)");
	}

	public double getArea(Unit unit) {
		if (unit.getName().equals(Unit.MM.getName()))
			return sqArea;
		else
			return asSqInch();
	}

	public static double fromSqInch(double sqInch) {
		return sqInch * SMM_PER_SINCH;
	}

	public static double fromSqMM(double sqMM) {
		return sqMM;
	}

	public double asSqInch() {
		return sqArea / SMM_PER_SINCH;
	}

	public double asSqMM() {
		return sqArea;
	}

}
