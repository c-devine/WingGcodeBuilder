package wgb.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import wgb.domain.Triangle;

public class Triangulator {
	private final static double EPSILON = 0.0000001;
	private List<Point2D> m_points = new ArrayList<Point2D>();

	public Triangulator(List<Point2D> points) {
		m_points = new ArrayList<Point2D>(points);
		if (m_points.get(0).equals(m_points.get(m_points.size() - 1))) {
			m_points.remove(m_points.size() - 1);
		}
	}

	public List<Triangle> triangulate() {
		List<Integer> indices = new ArrayList<Integer>();

		int n = m_points.size();
		if (n < 3)
			return convertToTriangles(indices);

		int[] V = new int[n];
		if (area() > 0) {
			for (int v = 0; v < n; v++)
				V[v] = v;
		} else {
			for (int v = 0; v < n; v++)
				V[v] = (n - 1) - v;
		}

		int nv = n;
		int count = 2 * nv;
		for (int v = nv - 1; nv > 2;) {
			if ((count--) <= 0)
				return convertToTriangles(indices);

			int u = v;
			if (nv <= u)
				u = 0;
			v = u + 1;
			if (nv <= v)
				v = 0;
			int w = v + 1;
			if (nv <= w)
				w = 0;

			if (snip(u, v, w, nv, V)) {
				int a, b, c, s, t;
				a = V[u];
				b = V[v];
				c = V[w];
				indices.add(a);
				indices.add(b);
				indices.add(c);

				for (s = v, t = v + 1; t < nv; s++, t++)
					V[s] = V[t];
				nv--;
				count = 2 * nv;
			}
		}

		Collections.reverse(indices);

		return convertToTriangles(indices);
	}

	private List<Triangle> convertToTriangles(List<Integer> indices) {
		List<Triangle> triangles = new ArrayList<Triangle>();

		for (int i = 0; i < indices.size(); i += 3) {
			triangles.add(new Triangle(
					new Point3D(m_points.get(indices.get(i)).getX(), m_points.get(indices.get(i)).getY(), 0),
					new Point3D(m_points.get(indices.get(i + 1)).getX(), m_points.get(indices.get(i + 1)).getY(), 0),
					new Point3D(m_points.get(indices.get(i + 2)).getX(), m_points.get(indices.get(i + 2)).getY(), 0)));
		}

		return triangles;
	}

	private float area() {
		int n = m_points.size();
		float A = 0.0f;
		for (int p = n - 1, q = 0; q < n; p = q++) {
			Point2D pval = m_points.get(p);
			Point2D qval = m_points.get(q);
			A += pval.getX() * qval.getY() - qval.getX() * pval.getY();
		}
		return (A * 0.5f);
	}

	private boolean snip(int u, int v, int w, int n, int[] V) {
		int p;
		Point2D A = m_points.get(V[u]);
		Point2D B = m_points.get(V[v]);
		Point2D C = m_points.get(V[w]);
		if (EPSILON > (((B.getX() - A.getX()) * (C.getY() - A.getY()))
				- ((B.getY() - A.getY()) * (C.getX() - A.getX()))))
			return false;
		for (p = 0; p < n; p++) {
			if ((p == u) || (p == v) || (p == w))
				continue;
			Point2D P = m_points.get(V[p]);
			if (insideTriangle(A, B, C, P))
				return false;
		}
		return true;
	}

	private boolean insideTriangle(Point2D A, Point2D B, Point2D C, Point2D P) {
		float ax, ay, bx, by, cx, cy, apx, apy, bpx, bpy, cpx, cpy;
		float cCROSSap, bCROSScp, aCROSSbp;

		ax = (float) (C.getX() - B.getX());
		ay = (float) (C.getY() - B.getY());
		bx = (float) (A.getX() - C.getX());
		by = (float) (A.getY() - C.getY());
		cx = (float) (B.getX() - A.getX());
		cy = (float) (B.getY() - A.getY());
		apx = (float) (P.getX() - A.getX());
		apy = (float) (P.getY() - A.getY());
		bpx = (float) (P.getX() - B.getX());
		bpy = (float) (P.getY() - B.getY());
		cpx = (float) (P.getX() - C.getX());
		cpy = (float) (P.getY() - C.getY());

		aCROSSbp = ax * bpy - ay * bpx;
		cCROSSap = cx * apy - cy * apx;
		bCROSScp = bx * cpy - by * cpx;

		return ((aCROSSbp >= 0.0f) && (bCROSScp >= 0.0f) && (cCROSSap >= 0.0f));
	}
}
