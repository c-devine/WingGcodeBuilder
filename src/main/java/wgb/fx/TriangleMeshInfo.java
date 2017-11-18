package wgb.fx;

public class TriangleMeshInfo {

	private float[] points;
	private int[] faces;
	private float[] texCoords;

	public TriangleMeshInfo(float[] points, int[] faces, float[] texCoords) {
		this.points = points;
		this.faces = faces;
		this.texCoords = texCoords;
	}

	public float[] getPoints() {
		return points;
	}

	public void setPoints(float[] points) {
		this.points = points;
	}

	public int[] getFaces() {
		return faces;
	}

	public void setFaces(int[] faces) {
		this.faces = faces;
	}

	public float[] getTexCoords() {
		return texCoords;
	}

	public void setTexCoords(float[] texCoords) {
		this.texCoords = texCoords;
	}

}
