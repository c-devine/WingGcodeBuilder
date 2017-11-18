package wgb.fx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import wgb.domain.Airfoil;

@Component
public class ThreeDPane {

	private final static Logger logger = LogManager.getLogger();
	private final static DrawMode DRAW_MODE = DrawMode.FILL;
	@Autowired
	FoilMeshHelper meshHelper;
	private double mousePosX, mousePosY = 0;
	private Rotate rotateX, rotateY, rotateZ;
	final double TURN_FACTOR = 2;
	final double SCALE_DELTA = 1.1;

	private Pane pane;
	private SubScene subScene;
	private Group root;

	public void init(Pane p) {
		this.pane = p;

		rotateX = new Rotate(0, Rotate.X_AXIS);
		rotateY = new Rotate(0, Rotate.Y_AXIS);
		rotateZ = new Rotate(180, Rotate.Z_AXIS);

		// Create the group
		root = new Group();

		// Add the Scene to the Stage
		subScene = createScene3D(root);
		subScene.widthProperty().bind(pane.widthProperty());
		subScene.heightProperty().bind(pane.heightProperty());
		pane.getChildren().add(subScene);

		pane.setOnMousePressed((MouseEvent me) -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});

		pane.setOnMouseDragged((MouseEvent me) -> {
			double dx = (mousePosX - me.getSceneX()) * 5;
			double dy = (mousePosY - me.getSceneY()) * 5;
			if (me.isPrimaryButtonDown()) {
				rotateX.setAngle(rotateX.getAngle() + (dx / pane.getWidth() * 360) * (Math.PI / 180));
				rotateY.setAngle(rotateY.getAngle() + (dy / pane.getHeight() * -360) * (Math.PI / 180));
			}
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});

		pane.setOnScroll((ScrollEvent event) -> {
			event.consume();

			if (event.getDeltaY() == 0) {
				return;
			}

			double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

			root.setScaleX(root.getScaleX() * scaleFactor);
			root.setScaleY(root.getScaleY() * scaleFactor);
		});
	}

	public void createWing(Airfoil left, Airfoil right, boolean mirror) {

		root.getChildren().clear();

		// Create a TriangleMesh
		TriangleMesh mesh = meshHelper.createMesh(left, right, mirror);

		final PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.DARKGREEN);
		material.setSpecularColor(Color.GREEN);

		// Create a MeshView
		MeshView meshView = new MeshView();
		meshView.setMesh(mesh);
		meshView.setMaterial(material);

		// Create a Light
		PointLight light = new PointLight();
		light.setTranslateX(pane.getWidth() / 2);
		light.setTranslateY(pane.getHeight() / 4);
		light.setTranslateZ(100);
		root.getChildren().add(light);

		meshView.setTranslateX(pane.getWidth() / 2);
		meshView.setTranslateY(pane.getHeight() / 2);
		meshView.setTranslateZ(100);

		meshView.setDrawMode(DRAW_MODE);
		// meshView.setCullFace(null);
		meshView.getTransforms().addAll(rotateZ, rotateY, rotateX);
		root.getChildren().add(meshView);

		// rotateY.setAngle(-40);

	}

	private SubScene createScene3D(Group group) {
		SubScene scene3d = new SubScene(group, 100, 100, true, SceneAntialiasing.BALANCED);
		scene3d.setFill(Color.rgb(10, 10, 40));
		// scene3d.setFill(Color.WHITESMOKE);

		return scene3d;
	}

	private void buildAxes() {

		final PhongMaterial redMaterial = new PhongMaterial();
		redMaterial.setDiffuseColor(Color.DARKRED);
		redMaterial.setSpecularColor(Color.RED);

		final PhongMaterial greenMaterial = new PhongMaterial();
		greenMaterial.setDiffuseColor(Color.DARKGREEN);
		greenMaterial.setSpecularColor(Color.GREEN);

		final PhongMaterial blueMaterial = new PhongMaterial();
		blueMaterial.setDiffuseColor(Color.DARKBLUE);
		blueMaterial.setSpecularColor(Color.BLUE);

		final Box xAxis = new Box(240.0, 1, 1);
		final Box yAxis = new Box(1, 240.0, 1);
		final Box zAxis = new Box(1, 1, 240.0);

		xAxis.setMaterial(redMaterial);
		yAxis.setMaterial(greenMaterial);
		zAxis.setMaterial(blueMaterial);

		Group axisGroup = new Group();
		axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
		root.getChildren().addAll(axisGroup);
	}

	public Pane getPane() {
		return pane;
	}

	public void setPane(Pane pane) {
		this.pane = pane;
	}

}
