package wgb;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import wgb.app.AppEventType;
import wgb.domain.Airfoil;
import wgb.domain.Side;
import wgb.domain.WingCalculator;
import wgb.fx.FoilMeshHelper;

@Component
public class ThreeDController implements Initializable {

	private final static Logger logger = LogManager.getLogger();
	private final static DrawMode DRAW_MODE = DrawMode.FILL;
	@Autowired
	MainController mainController;
	@Autowired
	FoilMeshHelper meshHelper;
	private double mousePosX, mousePosY = 0;
	private Rotate rotateX, rotateY, rotateZ;
	final double TURN_FACTOR = 5;
	final double SCALE_DELTA = 1.1;

	private SubScene subScene;
	private Group root;
	private SubScene txtScene;
	private Group txtGroup;

	@FXML
	private Pane threeDPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		rotateX = new Rotate(0, Rotate.X_AXIS);
		rotateY = new Rotate(0, Rotate.Y_AXIS);
		rotateZ = new Rotate(180, Rotate.Z_AXIS);

		// Create the group
		root = new Group();

		// Add the Scene to the Stage
		subScene = createScene3D(root);
		subScene.widthProperty().bind(threeDPane.widthProperty());
		subScene.heightProperty().bind(threeDPane.heightProperty());
		threeDPane.getChildren().add(subScene);

		subScene.setOnMousePressed((MouseEvent me) -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});

		subScene.setOnMouseDragged((MouseEvent me) -> {
			double dx = (mousePosX - me.getSceneX());
			double dy = (mousePosY - me.getSceneY());

			if (me.isMiddleButtonDown()) {
				root.setTranslateX(root.getTranslateX() - dx);
				root.setTranslateY(root.getTranslateY() - dy);
			}

			if (me.isPrimaryButtonDown() && !me.isShiftDown()) {
				rotateX.setAngle(
						rotateX.getAngle() + ((dy * TURN_FACTOR) / (subScene.getHeight() / 2) * 360) * (Math.PI / 180));
				rotateY.setAngle(
						rotateY.getAngle() + ((dx * TURN_FACTOR) / (subScene.getWidth() / 2) * -360) * (Math.PI / 180));
			}

			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
		});

		subScene.setOnScroll((ScrollEvent event) -> {

			event.consume();

			if (event.getDeltaY() == 0) {
				return;
			}

			double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

			root.setScaleX(root.getScaleX() * scaleFactor);
			root.setScaleY(root.getScaleY() * scaleFactor);

		});

		txtGroup = new Group();
		txtScene = new SubScene(txtGroup, 100, 100);

		txtScene.widthProperty().bind(threeDPane.widthProperty());
		txtScene.heightProperty().bind(threeDPane.heightProperty());
		threeDPane.getChildren().add(txtScene);
		threeDPane.widthProperty().addListener(c -> redrawText());

	}

	public void clear() {

		root.getChildren().clear();
		txtGroup.getChildren().clear();
	}

	@EventListener
	private void onAppEvent(AppEventType type) {

		if (!type.equals(AppEventType.REFRESH))
			return;

		clear();
		if (mainController.getAirfoil(Side.ROOT).getName().equals(Airfoil.DEFAULT_NAME)
				|| mainController.getAirfoil(Side.TIP).getName().equals(Airfoil.DEFAULT_NAME))
			return;

		// Create a TriangleMesh
		TriangleMesh mesh = meshHelper.createMesh(mainController.getAirfoil(Side.ROOT),
				mainController.getAirfoil(Side.TIP), mainController.getMenuMirror().isSelected());

		final PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.DARKGREEN);
		material.setSpecularColor(Color.GREEN);

		// Create a MeshView
		MeshView meshView = new MeshView();
		meshView.setMesh(mesh);
		meshView.setMaterial(material);

		// Create a Light
		AmbientLight light = new AmbientLight(Color.WHITE);
		root.getChildren().add(light);

		PointLight pLight = new PointLight(Color.WHITE);
		pLight.setTranslateX(subScene.getWidth() / 2);
		pLight.setTranslateY(subScene.getHeight() / 4);
		pLight.setTranslateZ(100);
		root.getChildren().add(pLight);

		PointLight pLight2 = new PointLight(Color.WHITE);
		pLight2.setTranslateX(subScene.getWidth() / 2);
		pLight2.setTranslateY(subScene.getHeight());
		pLight2.setTranslateZ(100);
		root.getChildren().add(pLight2);

		meshView.setTranslateX(subScene.getWidth() / 2);
		meshView.setTranslateY(subScene.getHeight() / 2);
		meshView.setTranslateZ(100);

		meshView.setDrawMode(DRAW_MODE);
		// meshView.setCullFace(null);
		meshView.getTransforms().addAll(rotateZ, rotateY, rotateX);
		root.getChildren().add(meshView);

		redrawText();
	}

	private void redrawText() {

		if (mainController.getAirfoil(Side.ROOT).getName().equals(Airfoil.DEFAULT_NAME)
				|| mainController.getAirfoil(Side.TIP).getName().equals(Airfoil.DEFAULT_NAME))
			return;

		Airfoil root = mainController.getAirfoil(Side.ROOT);
		Airfoil tip = mainController.getAirfoil(Side.TIP);
		WingCalculator calc = new WingCalculator(root, tip);

		txtGroup.getChildren().clear();
		VBox vBox = new VBox(1);

		vBox.getChildren().add(createTxtHbox("Wing Area: ", calc.getWingArea().toFormattedString(MainController.unit)));
		vBox.getChildren()
				.add(createTxtHbox("MAC Distance : ", calc.getMacDistance().toFormattedString(MainController.unit)));
		vBox.getChildren()
				.add(createTxtHbox("MAC Length : ", calc.getMacLength().toFormattedString(MainController.unit)));
		vBox.getChildren()
				.add(createTxtHbox("CG 15% : ", calc.getCgDistance15().toFormattedString(MainController.unit)));
		vBox.getChildren()
				.add(createTxtHbox("CG 20% : ", calc.getCgDistance20().toFormattedString(MainController.unit)));
		vBox.getChildren()
				.add(createTxtHbox("CG 25% : ", calc.getCgDistance25().toFormattedString(MainController.unit)));

		txtGroup.getChildren().add(vBox);
		txtGroup.setTranslateX(threeDPane.getWidth() - 250);
	}

	private HBox createTxtHbox(String name, String value) {
		HBox hBox = new HBox(10);
		Label lbl1 = new Label(name);
		lbl1.setTextFill(Color.WHITE);
		lbl1.setAlignment(Pos.TOP_LEFT);

		Label lbl2 = new Label(value);
		lbl2.setTextFill(Color.WHITE);
		lbl2.setAlignment(Pos.TOP_LEFT);
		hBox.getChildren().addAll(lbl1, lbl2);
		return hBox;
	}

	private SubScene createScene3D(Group group) {

		SubScene scene3d = new SubScene(group, 100, 100, true, SceneAntialiasing.BALANCED);
		scene3d.setFill(Color.rgb(10, 10, 40));
		// scene3d.setFill(Color.WHITESMOKE);
		return scene3d;
	}

}
