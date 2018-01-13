package wgb.fx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;
import wgb.domain.Airfoil;
import wgb.domain.Densifier;
import wgb.domain.Transform;
import wgb.domain.Triangle;
import wgb.domain.Unit;
import wgb.util.FoilUtil;
import wgb.util.Triangulator;

@Component
public class FoilMeshHelper {

	private final static int NUM_POINTS = 3600;

	@Autowired
	Densifier densifier;

	public TriangleMesh createMesh(Airfoil left, Airfoil right, boolean mirror) {

		// slice the foil profile
		List<Point2D> lInterpolated = densifier.densify(left.getScaled(Transform.ALL.getMask(), Unit.MM), NUM_POINTS);
		List<Point2D> rInterpolated = densifier.densify(right.getScaled(Transform.ALL.getMask(), Unit.MM), NUM_POINTS);

		List<Triangle> leftFace = getFace(left, lInterpolated, false);
		List<Triangle> rightFace = getFace(right, rInterpolated, true);
		List<Triangle> surface = getSurface(left, lInterpolated, right, rInterpolated);

		List<Triangle> combined = new ArrayList<Triangle>();
		combined.addAll(leftFace);
		combined.addAll(rightFace);
		combined.addAll(surface);

		if (mirror) {
			combined.addAll(mirror(surface));
			combined.addAll(mirror(rightFace));
		}

		TriangleMeshInfo info = getMeshInfo(combined);

		TriangleMesh mesh = new TriangleMesh();
		mesh.getPoints().addAll(info.getPoints());
		mesh.getTexCoords().addAll(info.getTexCoords());
		mesh.getFaces().addAll(info.getFaces());

		return mesh;
	}

	public TriangleMeshInfo getMeshInfo(List<Triangle> tFace) {

		float[] points = new float[tFace.size() * 9];
		int[] faces = new int[tFace.size() * 6];
		float[] texCoords = new float[] { 0f, 0f };

		// vertex index
		int iP = 0;
		// face index
		int iF = 0;
		// triangle index
		int iT = 0;
		for (Triangle face : tFace) {
			points[iP++] = (float) face.getA().getX();
			points[iP++] = (float) face.getA().getY();
			points[iP++] = (float) face.getA().getZ();
			points[iP++] = (float) face.getB().getX();
			points[iP++] = (float) face.getB().getY();
			points[iP++] = (float) face.getB().getZ();
			points[iP++] = (float) face.getC().getX();
			points[iP++] = (float) face.getC().getY();
			points[iP++] = (float) face.getC().getZ();
			faces[iF++] = iT++;
			faces[iF++] = 0;
			faces[iF++] = iT++;
			faces[iF++] = 0;
			faces[iF++] = iT++;
			faces[iF++] = 0;
		}

		return new TriangleMeshInfo(points, faces, texCoords);
	}

	public List<Triangle> getFace(Airfoil foil, List<Point2D> interpolated, boolean tip) {

		List<Triangle> triangles = new ArrayList<Triangle>();
		Triangulator tlator = new Triangulator(interpolated);

		double z = -foil.getSpan().asMM();

		for (Triangle t : tlator.triangulate()) {
			t.setZ(z);
			if (tip)
				triangles.add(t.isOrientedCCW() ? FoilUtil.swap(t) : t);
			else
				triangles.add(t.isOrientedCCW() ? t : FoilUtil.swap(t));
		}

		return triangles;
	}

	public List<Triangle> getSurface(Airfoil left, List<Point2D> lInterpolated, Airfoil right,
			List<Point2D> rInterpolated) {

		List<Triangle> firstPass = IntStream.range(1, lInterpolated.size())
				.mapToObj(i -> new Triangle(getPoint3D(left, lInterpolated, i - 1),
						getPoint3D(right, rInterpolated, i - 1), getPoint3D(right, rInterpolated, i)))
				.collect(Collectors.toList());
		List<Triangle> secondPass = IntStream
				.range(1, lInterpolated.size()).mapToObj(i -> new Triangle(getPoint3D(left, lInterpolated, i - 1),
						getPoint3D(right, rInterpolated, i), getPoint3D(left, lInterpolated, i)))
				.collect(Collectors.toList());

		firstPass.addAll(secondPass);
		return firstPass;
	}

	// add the yPos offset and flip y value
	private Point3D getPoint3D(Airfoil foil, List<Point2D> interpolated, int pos) {

		Point2D pt = interpolated.get(pos);
		return new Point3D(pt.getX(), pt.getY(), -foil.getSpan().asMM());
	}

	private List<Triangle> mirror(List<Triangle> triangles) {

		return triangles.stream().map(t -> FoilUtil.swap(FoilUtil.transform(t, 1.0, 1.0, -1.0)))
				.collect(Collectors.toList());

	}

}
