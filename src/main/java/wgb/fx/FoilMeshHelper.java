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

@Component
public class FoilMeshHelper {

	private final static int NUM_POINTS = 360;

	@Autowired
	Densifier densifier;
	private List<Point2D> lInterpolated, rInterpolated;

	public TriangleMesh createMesh(Airfoil left, Airfoil right, boolean mirror) {

		// slice the foil profile
		lInterpolated = densifier.densify(left.getScaled(Transform.ALL.getMask(), Unit.MM), NUM_POINTS);
		rInterpolated = densifier.densify(right.getScaled(Transform.ALL.getMask(), Unit.MM), NUM_POINTS);

		List<Triangle> leftFace = getFaces(left, lInterpolated);
		List<Triangle> rightFace = getFaces(right, rInterpolated);
		List<Triangle> surface = getSurface(left, right);

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

	public List<Triangle> getFaces(Airfoil foil, List<Point2D> interpolated) {

		Point2D centroid2D = FoilUtil.calcCentroid(interpolated);
		Point3D centroid = new Point3D(centroid2D.getX(), centroid2D.getY(), -foil.getSpan().asMM());
		return IntStream.range(1, interpolated.size()).mapToObj(
				i -> new Triangle(getPoint3D(foil, interpolated, i - 1), centroid, getPoint3D(foil, interpolated, i)))
				.collect(Collectors.toList());
	}

	public List<Triangle> getSurface(Airfoil left, Airfoil right) {

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
