package wgb.domain;

import org.springframework.core.style.ToStringCreator;

import javafx.geometry.Point3D;

public class Triangle {

	private Point3D a, b, c;

	public Triangle(Point3D a, Point3D b, Point3D c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public void setZ(Double z) {
		this.a = new Point3D(a.getX(), a.getY(), z);
		this.b = new Point3D(b.getX(), b.getY(), z);
		this.c = new Point3D(c.getX(), c.getY(), z);
	}

	public boolean isOrientedCCW() {

		double a11 = a.getX() - c.getX();
		double a21 = b.getX() - c.getX();

		double a12 = a.getY() - c.getY();
		double a22 = b.getY() - c.getY();

		double det = a11 * a22 - a12 * a21;

		return det > 0.0d;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("a", getA()).append("b", getB()).append("c", getC()).toString();
	}

	public Point3D getA() {
		return a;
	}

	public void setA(Point3D a) {
		this.a = a;
	}

	public Point3D getB() {
		return b;
	}

	public void setB(Point3D b) {
		this.b = b;
	}

	public Point3D getC() {
		return c;
	}

	public void setC(Point3D c) {
		this.c = c;
	}

}
