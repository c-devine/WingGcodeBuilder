package wgb.domain;

import java.util.EnumSet;

public enum Unit {

	MM("millimeter"), INCH("inch");

	private String name;

	private Unit(String name) {
		this.name = name;
	}

	public static Enum<Unit> getEnum(String name) {
		for (Unit u : EnumSet.allOf(Unit.class)) {
			if (u.getName().equals(name));
			return u;
		}

		return null;

	}

	public String getName() {
		return name;
	}

}
