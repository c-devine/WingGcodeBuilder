package wgb.domain;

public class WingCalculator {

	private Airfoil root;
	private Airfoil tip;
	private Area wingArea;
	private Length macDistance;
	private Length macLength;
	private Length cgDistance15;
	private Length cgDistance20;
	private Length cgDistance25;

	public WingCalculator(Airfoil root, Airfoil tip) {
		this.root = root;
		this.tip = tip;
		calculate();
	}

	public void calculate() {
		wingArea = new Area((root.getChord().asMM() + tip.getChord().asMM()) * tip.getSpan().asMM(), Unit.MM);

		// Find LE and TE line formula
		double le_b = 0;
		double le_a = (tip.getOffset().asMM() - le_b) / (tip.getSpan().asMM());
		double te_b = root.getChord().asMM();
		double te_a = ((tip.getOffset().asMM() + tip.getChord().asMM()) - te_b) / (tip.getSpan().asMM());

		// Find helper line formula
		double mac_b0 = -tip.getChord().asMM();
		double mac_a0 = ((tip.getOffset().asMM() + tip.getChord().asMM() + root.getChord().asMM()) - mac_b0)
				/ (tip.getSpan().asMM());
		double mac_b1 = root.getChord().asMM() + tip.getChord().asMM();
		double mac_a1 = ((tip.getOffset().asMM() - root.getChord().asMM()) - mac_b1) / (tip.getSpan().asMM());

		// Determine MAC using intersection of helper lines
		double mac_x = (mac_b1 - mac_b0) / (mac_a0 - mac_a1);

		// Compute MAC intersection with LE and TE
		double le_mac_y = le_a * mac_x + le_b;
		double te_mac_y = te_a * mac_x + te_b;

		// Compute CG
		cgDistance15 = new Length(le_mac_y + (te_mac_y - le_mac_y) * 15 / 100, Unit.MM);
		cgDistance20 = new Length(le_mac_y + (te_mac_y - le_mac_y) * 20 / 100, Unit.MM);
		cgDistance25 = new Length(le_mac_y + (te_mac_y - le_mac_y) * 25 / 100, Unit.MM);

		macDistance = new Length(mac_x, Unit.MM);
		macLength = new Length(te_mac_y - le_mac_y, Unit.MM);

	}

	public Airfoil getRoot() {
		return root;
	}

	public void setRoot(Airfoil root) {
		this.root = root;
	}

	public Airfoil getTip() {
		return tip;
	}

	public void setTip(Airfoil tip) {
		this.tip = tip;
	}

	public Area getWingArea() {
		return wingArea;
	}

	public void setWingArea(Area wingArea) {
		this.wingArea = wingArea;
	}

	public Length getMacDistance() {
		return macDistance;
	}

	public void setMacDistance(Length macDistance) {
		this.macDistance = macDistance;
	}

	public Length getMacLength() {
		return macLength;
	}

	public void setMacLength(Length macLength) {
		this.macLength = macLength;
	}

	public Length getCgDistance15() {
		return cgDistance15;
	}

	public void setCgDistance15(Length cgDistance15) {
		this.cgDistance15 = cgDistance15;
	}

	public Length getCgDistance20() {
		return cgDistance20;
	}

	public void setCgDistance20(Length cgDistance20) {
		this.cgDistance20 = cgDistance20;
	}

	public Length getCgDistance25() {
		return cgDistance25;
	}

	public void setCgDistance25(Length cgDistance25) {
		this.cgDistance25 = cgDistance25;
	}

}
