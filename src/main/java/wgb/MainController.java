package wgb;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import wgb.app.AppEventType;
import wgb.app.FileChooserHelper;
import wgb.app.Project;
import wgb.app.ProjectAware;
import wgb.domain.Airfoil;
import wgb.domain.Side;
import wgb.domain.measure.Length;
import wgb.domain.measure.Unit;
import wgb.fx.SpringFxmlLoader;

@Component
public class MainController implements Initializable, ProjectAware {

	public static Unit unit = Unit.MM;
	private final static Logger logger = LogManager.getLogger();
	private ObservableList<Airfoil> airFoilList = FXCollections.observableArrayList(new Airfoil(),
			new Airfoil(new Length(Airfoil.DEFAULT_SPAN, Unit.MM)));

	@Value("${app.debug}")
	private boolean DEBUG;
	@Value("${app.version}")
	private String version;
	@Value("${app.ui.title}")
	private String title;

	@Autowired
	Project project;
	@Autowired
	private ThreeDController threeDController;
	@Autowired
	private TwoDController twoDController;
	@Autowired
	private GcodeController gcController;
	@Autowired
	private UpdateController updController;
	@Autowired
	FileChooserHelper fcHelper;
	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private SpringFxmlLoader loader;

	@FXML
	private MenuBar menuBar;
	@FXML
	private RadioMenuItem menuMirror;
	@FXML
	private RadioMenuItem setMM;
	@FXML
	private RadioMenuItem setIn;
	@FXML
	private TableView<Airfoil> tvSections;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		TableColumn<Airfoil, String> posCol = new TableColumn<Airfoil, String>("Position");
		posCol.setMinWidth(50);
		posCol.setCellValueFactory(new Callback<CellDataFeatures<Airfoil, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Airfoil, String> p) {
				int index = airFoilList.indexOf(p.getValue());
				return new SimpleStringProperty(index != -1 ? index == 0 ? "Root" : "Tip" : "");
			}
		});

		TableColumn<Airfoil, String> nameCol = new TableColumn<Airfoil, String>("Name");
		nameCol.setMinWidth(200);
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		nameCol.setCellFactory(TextFieldTableCell.<Airfoil>forTableColumn());
		nameCol.setOnEditCommit((CellEditEvent<Airfoil, String> t) -> {
			((Airfoil) t.getTableView().getItems().get(t.getTablePosition().getRow())).setName(t.getNewValue());
		});

		TableColumn<Airfoil, String> chordCol = new TableColumn<Airfoil, String>("Chord");
		chordCol.setMinWidth(100);
		chordCol.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
				String.format("%.2f", param.getValue().getChord().getLength(unit))));

		chordCol.setCellFactory(TextFieldTableCell.<Airfoil>forTableColumn());
		chordCol.setOnEditCommit((CellEditEvent<Airfoil, String> t) -> {
			((Airfoil) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setChord(new Length(Double.parseDouble((t.getNewValue())), unit));
			publisher.publishEvent(AppEventType.REFRESH);
		});

		TableColumn<Airfoil, String> offsetCol = new TableColumn<Airfoil, String>("Offset");
		offsetCol.setMinWidth(100);
		offsetCol
				.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
						String.format("%.2f", param.getValue().getOffset().getLength(unit))));

		offsetCol.setCellFactory(TextFieldTableCell.<Airfoil>forTableColumn());
		offsetCol.setOnEditCommit((CellEditEvent<Airfoil, String> t) -> {
			((Airfoil) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setOffset(new Length(Double.parseDouble(t.getNewValue()), unit));
			publisher.publishEvent(AppEventType.REFRESH);
		});

		TableColumn<Airfoil, String> spanCol = new TableColumn<Airfoil, String>("Span");
		spanCol.setMinWidth(100);
		spanCol.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
				String.format("%.2f", param.getValue().getSpan().getLength(unit))));

		spanCol.setCellFactory(TextFieldTableCell.<Airfoil>forTableColumn());
		spanCol.setOnEditCommit((CellEditEvent<Airfoil, String> t) -> {
			((Airfoil) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setSpan(new Length(Double.parseDouble(t.getNewValue()), unit));
			publisher.publishEvent(AppEventType.REFRESH);
		});

		TableColumn<Airfoil, String> twistCol = new TableColumn<Airfoil, String>("Twist");
		twistCol.setMinWidth(100);
		twistCol.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
				String.format("%.2f", param.getValue().getTwist())));

		twistCol.setCellFactory(TextFieldTableCell.<Airfoil>forTableColumn());
		twistCol.setOnEditCommit((CellEditEvent<Airfoil, String> t) -> {
			((Airfoil) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setTwist(Double.valueOf(t.getNewValue()));
			publisher.publishEvent(AppEventType.REFRESH);
		});

		TableColumn<Airfoil, String> yScaleCol = new TableColumn<Airfoil, String>("Y Scale");
		yScaleCol.setMinWidth(100);
		yScaleCol
				.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
						String.format("%.2f", param.getValue().getyScale())));

		yScaleCol.setCellFactory(TextFieldTableCell.<Airfoil>forTableColumn());
		yScaleCol.setOnEditCommit((CellEditEvent<Airfoil, String> t) -> {
			((Airfoil) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setyScale(Double.valueOf(t.getNewValue()));
			tvSections.refresh();
			publisher.publishEvent(AppEventType.REFRESH);
		});

		TableColumn<Airfoil, String> sweepCol = new TableColumn<Airfoil, String>("Calc Sweep");
		sweepCol.setMinWidth(125);
		sweepCol.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
				String.format("%.2f%%", param.getValue().getSweep())));

		TableColumn<Airfoil, String> thicknessCol = new TableColumn<Airfoil, String>("Calc Thickness");
		thicknessCol.setMinWidth(125);
		thicknessCol
				.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
						String.format("%.2f", param.getValue().getThickness().getLength(unit))));

		tvSections.setEditable(true);
		tvSections.getColumns().addAll(posCol, nameCol, spanCol, chordCol, offsetCol, twistCol, yScaleCol, sweepCol,
				thicknessCol);
		tvSections.setItems(airFoilList);

		setMM.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
		setIn.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));
	}

	public Airfoil getAirfoil(Side side) {

		if (side == Side.ROOT)
			return airFoilList.get(0);

		return airFoilList.get(1);
	}

	public void addAirfoil(Airfoil af, Side side, boolean replace) {

		Airfoil existing = airFoilList.get(side.getIndex());
		if (!existing.getName().equals(Airfoil.DEFAULT_NAME) && !replace) {
			af.setChord(existing.getChord());
			af.setOffset(existing.getOffset());
			af.setSpan(existing.getSpan());
		}

		if (Side.TIP.equals(side)) {
			if (af.getSpan().asMM() == 0)
				af.setSpan(new Length(Airfoil.DEFAULT_SPAN, Unit.MM));
		}

		twoDController.setAirfoil(af, side);
		airFoilList.set(side.getIndex(), af);
		tvSections.refresh();
		publisher.publishEvent(AppEventType.REFRESH);

	}

	@FXML
	protected void processAbout(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText(null);
		alert.setContentText(title + " version = " + version);

		alert.showAndWait();
	}

	@FXML
	protected void processCheckUpdate(ActionEvent event) {
		updController.processCheckUpdate(event);
	}

	@FXML
	protected void processSetMillimeters(ActionEvent event) {
		unit = Unit.MM;
		tvSections.refresh();
		publisher.publishEvent(AppEventType.REFRESH);
	}

	@FXML
	protected void processSetInches(ActionEvent event) {
		unit = Unit.INCH;
		tvSections.refresh();
		publisher.publishEvent(AppEventType.REFRESH);
	}

	@FXML
	protected void processExit(ActionEvent event) {
		try {
			logger.info("exit");
			Platform.exit();
			System.exit(0);
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	@FXML
	protected void processNew(ActionEvent event) {
		addAirfoil(new Airfoil(), Side.ROOT, true);
		addAirfoil(new Airfoil(new Length(Airfoil.DEFAULT_SPAN, Unit.MM)), Side.TIP, true);
		publisher.publishEvent(AppEventType.REFRESH);
		threeDController.clear();
	}

	@FXML
	protected void processSave(ActionEvent event) {

		if (!(project.getFile() == null)) {
			try {
				project.save(project.getFile());
			} catch (Exception e) {
				logger.error("Error saving project : " + project.getFile().getAbsolutePath(), e);
			}
		} else {
			processSaveAs(event);
		}
	}

	@FXML
	protected void processSaveAs(ActionEvent event) {

		FileChooser fileChooser = fcHelper.getFileChooser();
		ExtensionFilter extFilter = new FileChooser.ExtensionFilter("WGB files (*.json)", "*.json");
		fileChooser.getExtensionFilters().add(extFilter);
		File f = fileChooser.showSaveDialog(((Node) menuBar).getScene().getWindow());
		if (f != null) {
			fcHelper.saveLastFileLocation(f);
			try {
				if (!f.getName().endsWith(".json"))
					f.getName().concat(".json");
				project.save(f);
			} catch (Exception e) {
				logger.error("Error saving project: " + f.getAbsolutePath(), e);
			}
		}
	}

	@FXML
	protected void processOpen(ActionEvent event) {

		FileChooser fileChooser = fcHelper.getFileChooser();
		ExtensionFilter extFilter = new FileChooser.ExtensionFilter("WGB files (*.json)", "*.json");
		fileChooser.getExtensionFilters().add(extFilter);
		File f = fileChooser.showOpenDialog(((Node) menuBar).getScene().getWindow());
		if (f != null)
			fcHelper.saveLastFileLocation(f);
		try {
			project.load(f);
		} catch (Exception e) {
			logger.error("Error loading project.", e);
		}
	}

	@FXML
	protected void processMirrorWing(ActionEvent event) {
		publisher.publishEvent(AppEventType.REFRESH);
	}

	@FXML
	protected void processLayout(ActionEvent event) throws Exception {
		Stage stage = new Stage();
		Parent root = (Parent) loader.load(getClass().getResource("/fx/Layout.fxml").toURI());
		stage.setScene(new Scene(root));
		stage.setTitle("Layout");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(((Node) menuBar).getScene().getWindow());
		stage.show();
	}

	@FXML
	protected void processModelProperties(ActionEvent event) throws Exception {
		Stage stage = new Stage();
		Parent root = (Parent) loader.load(getClass().getResource("/fx/Model.fxml").toURI());
		stage.setScene(new Scene(root));
		stage.setTitle("Model Properties");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(((Node) menuBar).getScene().getWindow());
		stage.show();
	}

	@FXML
	protected void ontvSectionsMouseClicked(MouseEvent event) {

		if (event.getClickCount() == 2) {

			if (tvSections.getSelectionModel().getSelectedIndex() == 0)
				twoDController.onSelectFoilRoot(event);
			else
				twoDController.onSelectFoilTip(event);
		}

	}

	/*
	 * Start Gcode Settings Menu Items)
	 */

	@FXML
	protected void processSaveDefaultGCS(ActionEvent event) {
		gcController.processSaveDefaultGCS(event);
	}

	@FXML
	protected void processLoadDefaultGCS(ActionEvent event) {
		gcController.processLoadDefaultGCS(event);
	}

	@FXML
	protected void processSaveGCS(ActionEvent event) {
		gcController.processSaveGCS(event);
	}

	@FXML
	protected void processLoadGCS(ActionEvent event) {
		gcController.processLoadGCS(event);
	}

	/*
	 * End Gcode Settings Menu Items)
	 */

	@Override
	public void onProjectLoad(Project project) {

		addAirfoil(project.getRoot(), Side.ROOT, true);
		addAirfoil(project.getTip(), Side.TIP, true);
		publisher.publishEvent(AppEventType.REFRESH);
	}

	@Override
	public void onProjectSave(Project project) {

		project.setRoot(getAirfoil(Side.ROOT));
		project.setTip(getAirfoil(Side.TIP));

	}

	@EventListener
	private void onAppEvent(AppEventType type) {

		if (type.equals(AppEventType.AFTER_LOADED_EVENT)) {
			if (DEBUG) {
				File dbg = null;
				try {
					dbg = new File("sampledata/project.json");
					project.load(dbg);
				} catch (Exception e) {
					logger.error("Error loading sample data file: " + dbg.getAbsolutePath(), e);
				}
			}
		}

		if (type.equals(AppEventType.REFRESH)) {
			tvSections.refresh();
			Main.primaryStage.setTitle(String.format("%s %s", title,
					project.getModelName() == null ? "" : "(" + project.getModelName() + ")"));
		}

	}

	public RadioMenuItem getMenuMirror() {
		return menuMirror;
	}

	public void setMenuMirror(RadioMenuItem menuMirror) {
		this.menuMirror = menuMirror;
	}
}
