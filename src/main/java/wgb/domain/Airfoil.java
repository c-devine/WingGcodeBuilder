package wgb.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Point2D;
import wgb.util.FoilUtil;

public class Airfoil {

	private final static Logger logger = LogManager.getLogger();
	public final static String DEFAULT_NAME = "Empty";
	private final static Unit DEFAULT_UNIT = Unit.MM;
	private final static int DEFAULT_CHORD = 100;
	private final static int DEFAULT_OFFSET = 0;
	private final static int DEFAULT_YPOS = 0;
	private final static double DEFAULT_TWIST = 0;

	private String name = DEFAULT_NAME;
	private Length chord = new Length(DEFAULT_CHORD, DEFAULT_UNIT);
	private Length offset = new Length(DEFAULT_OFFSET, DEFAULT_UNIT);
	private Length yPos = new Length(DEFAULT_YPOS, DEFAULT_UNIT);
	private double twist = DEFAULT_TWIST;

	private List<Point2D> xy = new ArrayList<Point2D>();

	public Airfoil() {

	}

	public Airfoil(Length yPos) {
		this.yPos = yPos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Point2D> getXy() {
		return xy;
	}

	public void setXy(List<Point2D> points) {
		this.xy = points;
	}

	public Length getOffset() {
		return offset;
	}

	public void setOffset(Length offset) {
		this.offset = offset;
	}

	public Length getChord() {
		return chord;
	}

	public void setChord(Length chord) {
		this.chord = chord;
	}

	public Length getyPos() {
		return yPos;
	}

	public void setyPos(Length y) {
		this.yPos = y;
	}

	public double getTwist() {
		return twist;
	}

	public void setTwist(double twist) {
		this.twist = twist;
	}

	public Length getThickness() {
		return new Length(chord.asMM() * FoilUtil.findMaxY(xy) - FoilUtil.findMinY(xy), Unit.MM);
	}

}
