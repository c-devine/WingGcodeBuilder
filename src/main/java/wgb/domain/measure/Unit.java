package wgb.domain.measure;

import java.util.EnumSet;

public enum Unit {

	MM("millimeter", "mm"), INCH("inch", "in"), GM("gram", "gm"), OZ("ounce", "oz");

	private String name;
	private String shortName;

	private Unit(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
	}

	public static Unit getEnum(String name) {

		for (Unit u : EnumSet.allOf(Unit.class)) {
			if (u.getName().equals(name) || u.getShortName().equals(name))
				return u;
		}

		return null;

	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

}
