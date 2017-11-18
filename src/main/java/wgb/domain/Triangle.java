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
