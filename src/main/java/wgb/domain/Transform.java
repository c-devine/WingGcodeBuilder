package wgb.domain;

public enum Transform {

	ALL(0xFFFF), XYSCALE(0b1), YSCALE(0b10), TWIST(0b100), OFFSET(0b1000), KERF(0b10000);

	private final int mask;

	private Transform(int index) {
		this.mask = index;
	}

	public int getMask() {
		return mask;
	}

	public boolean isSet(int i) {
		return (getMask() & i) > 0;
	}

	public static int calcMask(Transform... ts) {
		int mask = 0;
		for (Transform t : ts) {
			mask |= t.getMask();
		}
		return mask;
	}

}
