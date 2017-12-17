package wgb.domain;

public enum Side {

	ROOT(0), TIP(1);

	private final int index;

	private Side(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
