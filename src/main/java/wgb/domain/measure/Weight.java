package wgb.domain.measure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = WeightSerializer.class)
@JsonDeserialize(using = WeightDeserializer.class)
public class Weight {

	private static double GM_PER_OZ = 28.3495;

	private double w = 0;

	public Weight(double weight, Unit unit) {
		setWeight(weight, unit);
	}

	// recreate the weight from the string passed in: "1.0 gm" or "1.0 gram"
	public Weight(String sWeight) {
		String[] split = sWeight.split("\\s+");
		w = new Weight(Double.parseDouble(split[0]), Unit.getEnum(split[1])).asGM();
	}

	public String toFormattedString(Unit unit) {

		String ext = "";
		switch (unit) {
		case OZ:
			ext = "ounce(s)";
			break;
		case GM:
		default:
			ext = "gm(s)";
			break;
		}

		return String.format("%.2f %s", getWeight(unit), ext);
	}

	// only support gm and oz
	public double getWeight(Unit unit) {

		switch (unit) {
		case OZ:
			return asOunce();
		case GM:
		default:
			return w;
		}

	}

	// only support gm and oz
	public void setWeight(double weight, Unit unit) {

		switch (unit) {
		case OZ:
			w = Weight.fromOunce(weight);
			break;
		case GM:
		default:
			w = weight;
			break;
		}
	}

	public static double fromOunce(double ounce) {
		return ounce * GM_PER_OZ;
	}

	public double asOunce() {
		return w / GM_PER_OZ;
	}

	public static double fromGM(double gm) {
		return gm;
	}

	public double asGM() {
		return w;
	}

}
