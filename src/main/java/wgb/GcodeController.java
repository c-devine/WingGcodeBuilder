package wgb;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wgb.app.AppEventType;
import wgb.app.FileChooserHelper;
import wgb.app.Project;
import wgb.app.ProjectAware;
import wgb.domain.GcodeGenerator;
import wgb.domain.GcodeSettings;
import wgb.domain.Length;
import wgb.domain.Side;
import wgb.fx.MapEntry;
import wgb.fx.SpringFxmlLoader;

@Component
public class GcodeController implements Initializable, ProjectAware {

	private final static Logger logger = LogManager.getLogger();
	private ObservableList<MapEntry<String, Object>> oList = FXCollections.observableArrayList();

	private GcodeSettings gcSettings = new GcodeSettings();

	@Autowired
	MainController mainController;
	@Autowired
	GcodeGenerator generator;
	@Autowired
	FileChooserHelper fcHelper;

	@FXML
	private TableView<MapEntry<String, Object>> gcodePropTable;
	@FXML
	TextArea gcodeTextArea;
	@FXML
	TextArea preGcodeTextArea;
	@FXML
	TextArea postGcodeTextArea;
	@FXML
	CheckBox cbMirrored;

	@Autowired
	private SpringFxmlLoader loader;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		gcodeTextArea.setWrapText(false);
		oList.addAll(gcSettings.getEntries());

		TableColumn<MapEntry<String, Object>, String> keyColumn = new TableColumn<>("Name");
		keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));

		TableColumn<MapEntry<String, Object>, String> valueColumn = new TableColumn<>("Value");
		valueColumn.setCellValueFactory(
				(TableColumn.CellDataFeatures<MapEntry<String, Object>, String> param) -> new ReadOnlyStringWrapper(
						param.getValue().getValue() instanceof Length
								? String.valueOf(((Length) param.getValue().getValue()).getLength(MainController.unit))
								: String.valueOf(param.getValue().getValue())));

		valueColumn.setCellFactory(TextFieldTableCell.<MapEntry<String, Object>>forTableColumn());
		valueColumn.setOnEditCommit((CellEditEvent<MapEntry<String, Object>, String> t) -> {
			MapEntry<String, Object> me = ((MapEntry<String, Object>) t.getTableView().getItems()
					.get(t.getTablePosition().getRow()));
			if (me.getValue() instanceof Length)
				me.setValue(new Length(Double.valueOf(t.getNewValue()), MainController.unit));
			else
				me.setValue(t.getNewValue());
		});

		gcodePropTable.setEditable(true);
		gcodePropTable.getColumns().addAll(keyColumn, valueColumn);
		gcodePropTable.setItems(oList);

	}

	@EventListener
	private void onAppEvent(AppEventType type) {

		if (type.equals(AppEventType.REFRESH))
			gcodePropTable.refresh();
	}

	@FXML
	protected void onGenerateGcode(MouseEvent event) {

		gcSettings.setEntries(oList);
		List<String> gcodeList = generator.generateGcode(mainController.getAirfoil(Side.ROOT),
				mainController.getAirfoil(Side.TIP), gcSettings, cbMirrored.isSelected());

		gcodeList.addAll(0, getPrePostGcode(preGcodeTextArea));
		gcodeList.addAll(getPrePostGcode(postGcodeTextArea));
		gcodeTextArea.clear();
		gcodeList.forEach(s -> gcodeTextArea.appendText(s + System.lineSeparator()));

	}

	@FXML
	protected void onExportGcode(MouseEvent event) {

		FileChooser fileChooser = fcHelper.getFileChooser();
		ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GCODE (*.gcode)", "*.gcode");
		fileChooser.getExtensionFilters().add(extFilter);
		File f = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
		if (f != null) {
			fcHelper.saveLastFileLocation(f);
			try {
				if (!f.getName().endsWith(".gcode"))
					f.getName().concat(".gcode");
				exportGcode(f, gcodeTextArea.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	protected void onUploadGcode(MouseEvent event) throws Exception {

		Stage stage = new Stage();
		Parent root = (Parent) loader.load(getClass().getResource("/fx/OctoPrint.fxml").toURI());
		stage.setScene(new Scene(root));
		stage.setTitle("OctoPrint Settings");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(((Node) event.getSource()).getScene().getWindow());
		stage.show();
	}

	private List<String> getPrePostGcode(TextArea area) {

		if (area.getText().equals(""))
			return new ArrayList<String>();

		return Arrays.asList(area.getText().trim().split(System.lineSeparator()));
	}

	private void exportGcode(File file, String text) throws Exception {

		PrintWriter pw = new PrintWriter(new FileWriter(file));
		pw.print(text);
		pw.close();
	}

	@Override
	public void onProjectLoad(Project project) {

		oList.clear();
		gcSettings = project.getGcodeSettings();
		oList.addAll(gcSettings.getEntries());
		gcodePropTable.refresh();

	}

	@Override
	public void onProjectSave(Project project) {

		gcSettings.setEntries(oList);
		project.setGcodeSettings(gcSettings);

	}

}
