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
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import wgb.app.AppEventType;
import wgb.app.FileChooserHelper;
import wgb.app.Project;
import wgb.app.ProjectAware;
import wgb.domain.Airfoil;
import wgb.domain.Length;
import wgb.domain.Side;
import wgb.domain.Unit;

@Component
public class MainController implements Initializable, ProjectAware {

	public static Unit unit = Unit.MM;
	private final static Logger logger = LogManager.getLogger();
	private ObservableList<Airfoil> airFoilList = FXCollections.observableArrayList(new Airfoil(),
			new Airfoil(new Length(200, Unit.MM)));

	@Value("${app.debug}")
	private boolean DEBUG;
	@Value("${app.version}")
	private String version;
	@Value("${app.ui.title}")
	private String title;

	@Autowired
	Project project;

	@FXML
	private MenuBar menuBar;

	@FXML
	private RadioMenuItem menuMirror;

	@FXML
	private TableView<Airfoil> tvSections;

	@Autowired
	private ThreeDController threeDController;
	@Autowired
	private TwoDController twoDController;
	@Autowired
	FileChooserHelper fcHelper;

	@Autowired
	private ApplicationEventPublisher publisher;

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
				String.valueOf(param.getValue().getChord().getLength(unit))));

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
						String.valueOf(param.getValue().getOffset().getLength(unit))));

		offsetCol.setCellFactory(TextFieldTableCell.<Airfoil>forTableColumn());
		offsetCol.setOnEditCommit((CellEditEvent<Airfoil, String> t) -> {
			((Airfoil) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setOffset(new Length(Double.parseDouble(t.getNewValue()), unit));
			publisher.publishEvent(AppEventType.REFRESH);
		});

		TableColumn<Airfoil, String> yCol = new TableColumn<Airfoil, String>("Y");
		yCol.setMinWidth(100);
		yCol.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
				String.valueOf(param.getValue().getyPos().getLength(unit))));

		yCol.setCellFactory(TextFieldTableCell.<Airfoil>forTableColumn());
		yCol.setOnEditCommit((CellEditEvent<Airfoil, String> t) -> {
			((Airfoil) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setyPos(new Length(Double.parseDouble(t.getNewValue()), unit));
			publisher.publishEvent(AppEventType.REFRESH);
		});

		TableColumn<Airfoil, String> twistCol = new TableColumn<Airfoil, String>("Twist");
		twistCol.setMinWidth(100);
		twistCol.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
				String.valueOf(param.getValue().getTwist())));

		twistCol.setCellFactory(TextFieldTableCell.<Airfoil>forTableColumn());
		twistCol.setOnEditCommit((CellEditEvent<Airfoil, String> t) -> {
			((Airfoil) t.getTableView().getItems().get(t.getTablePosition().getRow()))
					.setTwist(Double.valueOf(t.getNewValue()));
			publisher.publishEvent(AppEventType.REFRESH);
		});

		TableColumn<Airfoil, String> thicknessCol = new TableColumn<Airfoil, String>("Thickness");
		thicknessCol.setMinWidth(100);
		thicknessCol
				.setCellValueFactory((TableColumn.CellDataFeatures<Airfoil, String> param) -> new ReadOnlyStringWrapper(
						String.valueOf(param.getValue().getThickness().getLength(unit))));

		tvSections.setEditable(true);
		tvSections.getColumns().addAll(posCol, nameCol, yCol, chordCol, offsetCol, twistCol, thicknessCol);
		tvSections.setItems(airFoilList);
	}

	public Airfoil getAirfoil(Side side) {

		if (side == Side.ROOT)
			return airFoilList.get(0);

		return airFoilList.get(1);
	}

	public void addAirfoil(Airfoil af, Side side) {

		Airfoil existing = airFoilList.get(side.getIndex());
		if (!existing.getName().equals(Airfoil.DEFAULT_NAME)) {
			af.setChord(existing.getChord());
			af.setOffset(existing.getOffset());
			af.setyPos(existing.getyPos());
		}

		if (Side.TIP.equals(side)) {
			if (af.getyPos().asMM() == 0)
				af.setyPos(new Length(200, Unit.MM));
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
		addAirfoil(new Airfoil(), Side.ROOT);
		addAirfoil(new Airfoil(new Length(200, Unit.MM)), Side.TIP);
		publisher.publishEvent(AppEventType.REFRESH);
		threeDController.clear();
	}

	@FXML
	protected void processSave(ActionEvent event) {

		if (!(project.getFile() == null)) {
			try {
				project.save(project.getFile());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			try {
				if (!f.getName().endsWith(".json"))
					f.getName().concat(".json");
				project.save(f);
			} catch (Exception e) {
				e.printStackTrace();
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
	protected void ontvSectionsMouseClicked(MouseEvent event) {

		if (event.getClickCount() == 2) {

			if (tvSections.getSelectionModel().getSelectedIndex() == 0)
				twoDController.onSelectFoilRoot(event);
			else
				twoDController.onSelectFoilTip(event);
		}

	}

	@Override
	public void onProjectLoad(Project project) {

		addAirfoil(project.getRoot(), Side.ROOT);
		addAirfoil(project.getTip(), Side.TIP);
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
					logger.error("Error loading sample data file: " + dbg.getAbsolutePath());
					e.printStackTrace();
				}
			}
		}
	}

	public RadioMenuItem getMenuMirror() {
		return menuMirror;
	}

	public void setMenuMirror(RadioMenuItem menuMirror) {
		this.menuMirror = menuMirror;
	}
}
