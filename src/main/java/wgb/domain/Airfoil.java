package wgb.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javafx.geometry.Point2D;
import wgb.util.FoilUtil;

public class Airfoil {

	public final static String DEFAULT_NAME = "Empty";
	private final static Unit DEFAULT_UNIT = Unit.MM;
	private final static int DEFAULT_CHORD = 250;
	private final static int DEFAULT_OFFSET = 0;
	public final static int DEFAULT_SPAN = 500;
	private final static double DEFAULT_TWIST = 0;
	private final static double DEFAULT_YSCALE = 1.0;

	private String name = DEFAULT_NAME;
	private Length chord = new Length(DEFAULT_CHORD, DEFAULT_UNIT);
	private Length offset = new Length(DEFAULT_OFFSET, DEFAULT_UNIT);
	private Length span = new Length(0, DEFAULT_UNIT);
	private double yScale = DEFAULT_YSCALE;
	private double twist = DEFAULT_TWIST;

	@JsonDeserialize(using = Point2DDeserializer.class)
	private List<Point2D> xy = new ArrayList<Point2D>();

	public Airfoil() {

	}

	public Airfoil(Length span) {
		this.span = span;
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

	public Length getSpan() {
		return span;
	}

	public void setSpan(Length y) {
		this.span = y;
	}

	public double getTwist() {
		return twist;
	}

	public void setTwist(double twist) {
		this.twist = twist;
	}

	public double getyScale() {
		return yScale;
	}

	public void setyScale(double yScale) {
		this.yScale = yScale;
	}

	@JsonIgnore
	public Length getThickness() {

		List<Point2D> points = FoilUtil.scale(this.getXy(), new Point2D(0.0, 0.0), 1.0, this.getyScale());
		points = FoilUtil.scale(points, new Point2D(0.0, 0.0), this.getChord().asMM(), this.getChord().asMM());
		return new Length(FoilUtil.findMaxY(points) - FoilUtil.findMinY(points), Unit.MM);
	}

	/**
	 * From flying wing calculator - seems to be about right.
	 *
	 * @return
	 */
	@JsonIgnore
	public double getSweep() {

		return span.asMM() == 0 ? 0 : Math.atan(offset.asMM() / span.asMM()) * 180 / Math.PI;
	}

	@JsonIgnore
	public List<Point2D> getScaled(int transforms, Unit unit) {

		return getScaled(transforms, unit, new Length(0.0, Unit.MM));
	}

	@JsonIgnore
	public List<Point2D> getScaled(int transforms, Unit unit, Length kerf) {

		List<Point2D> pts = this.getXy();
		Point2D origin = new Point2D(0.0, 0.0);

		if (Transform.YSCALE.isSet(transforms)) {
			pts = FoilUtil.scale(pts, origin, 1.0, this.getyScale());
		}

		if (Transform.XYSCALE.isSet(transforms)) {
			pts = FoilUtil.scale(pts, origin, this.getChord().getLength(unit) + kerf.getLength(unit),
					this.getChord().getLength(unit) + kerf.getLength(unit));
		}

		if (Transform.TWIST.isSet(transforms)) {
			pts = FoilUtil.rotate(pts, origin, this.twist);
		}

		if (Transform.OFFSET.isSet(transforms)) {
			pts = FoilUtil.offset(pts, this.getOffset().getLength(unit), 0.0);
		}

		return pts;

	}

}
