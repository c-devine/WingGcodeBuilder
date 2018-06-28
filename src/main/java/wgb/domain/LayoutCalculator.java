package wgb.domain;

import javafx.geometry.Point2D;
import wgb.domain.measure.Length;
import wgb.domain.measure.Unit;
import wgb.util.FoilUtil;

public class LayoutCalculator {

	private Airfoil root;
	private Airfoil tip;

	private Point2D rootLe;
	private Point2D rootTe;
	private Point2D tipLe;
	private Point2D tipTe;

	private Length rootChord;
	private Length tipChord;
	private Length leSpan;

	private Point2D blockRootLe;
	private Point2D blockRootTe;
	private Point2D blockTipTe;
	private Point2D blockTipLe;
	private Length blockWidth;
	private Length blockHeight;

	public LayoutCalculator(Airfoil root, Airfoil tip) {

		this.root = root;
		this.tip = tip;
		calculate();
	}

	public void calculate() {

		rootLe = new Point2D(0.0, 0.0);
		rootTe = FoilUtil.calcPoint(rootLe, 90.0 - tip.getSweep(), root.getChord().asMM());

		leSpan = new Length(tip.getOffset().asMM() / Math.sin(Math.toRadians(tip.getSweep())), Unit.MM);
		tipLe = new Point2D(leSpan.asMM(), 0.0);
		tipTe = FoilUtil.calcPoint(tipLe, 90.0 - tip.getSweep(), tip.getChord().asMM());
		tipChord = new Length(tipTe.getY(), Unit.MM);

		// calculate the root length - project the trailing edge to root
		double teAng = 360.0 - FoilUtil.calcAngle(rootTe, tipTe);
		double extLen = Math.tan(Math.toRadians(teAng)) * rootTe.getX();

		blockRootLe = new Point2D(0.0, 0.0);
		blockRootTe = new Point2D(0.0, extLen + rootTe.getY());
		rootChord = new Length(blockRootTe.getY(), Unit.MM);
		blockTipTe = new Point2D(tipTe.getX(), blockRootTe.getY());
		blockTipLe = new Point2D(tipTe.getX(), 0.0);
		blockWidth = new Length(blockTipTe.getX(), Unit.MM);
		blockHeight = new Length(blockRootTe.getY(), Unit.MM);

	}

	public Airfoil[] getUnsweptAirfoils() {

		Airfoil uRoot = new Airfoil(root);
		uRoot.setChord(rootChord);
		uRoot.setOffset(new Length(0, Unit.MM));
		Airfoil uTip = new Airfoil(tip);
		uTip.setChord(tipChord);
		uTip.setOffset(new Length(0.0, Unit.MM));
		Airfoil[] foils = { uRoot, uTip };
		return foils;

	}

	public double getSweep() {
		return tip.getSweep();
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

	public Point2D getRootLe() {
		return rootLe;
	}

	public void setRootLe(Point2D rootLe) {
		this.rootLe = rootLe;
	}

	public Point2D getRootTe() {
		return rootTe;
	}

	public void setRootTe(Point2D rootTe) {
		this.rootTe = rootTe;
	}

	public Point2D getTipLe() {
		return tipLe;
	}

	public void setTipLe(Point2D tipLe) {
		this.tipLe = tipLe;
	}

	public Point2D getTipTe() {
		return tipTe;
	}

	public void setTipTe(Point2D tipTe) {
		this.tipTe = tipTe;
	}

	public Length getRootChord() {
		return rootChord;
	}

	public void setRootChord(Length rootChord) {
		this.rootChord = rootChord;
	}

	public Length getTipChord() {
		return tipChord;
	}

	public void setTipChord(Length tipChord) {
		this.tipChord = tipChord;
	}

	public Length getLeSpan() {
		return leSpan;
	}

	public void setLeSpan(Length leSpan) {
		this.leSpan = leSpan;
	}

	public Point2D getBlockRootTe() {
		return blockRootTe;
	}

	public void setBlockRootTe(Point2D blockRootTe) {
		this.blockRootTe = blockRootTe;
	}

	public Point2D getBlockTipTe() {
		return blockTipTe;
	}

	public void setBlockTipTe(Point2D blockTipTe) {
		this.blockTipTe = blockTipTe;
	}

	public Length getBlockWidth() {
		return blockWidth;
	}

	public void setBlockWidth(Length blockWidth) {
		this.blockWidth = blockWidth;
	}

	public Length getBlockHeight() {
		return blockHeight;
	}

	public void setBlockHeight(Length blockHeight) {
		this.blockHeight = blockHeight;
	}

	public Point2D getBlockTipLe() {
		return blockTipLe;
	}

	public void setBlockTipLe(Point2D blockTipLe) {
		this.blockTipLe = blockTipLe;
	}

	public Point2D getBlockRootLe() {
		return blockRootLe;
	}

	public void setBlockRootLe(Point2D blockRootLe) {
		this.blockRootLe = blockRootLe;
	}

}
