package wgb;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import wgb.app.FileChooserHelper;
import wgb.app.Project;
import wgb.app.ProjectAware;
import wgb.domain.GcodeGenerator;
import wgb.domain.GcodeSettings;
import wgb.domain.Side;
import wgb.fx.MapEntry;

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
	CheckBox cbMirrored;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		gcodeTextArea.setWrapText(false);
		oList.addAll(gcSettings.getEntries());

		TableColumn<MapEntry<String, Object>, String> keyColumn = new TableColumn<>("Name");
		keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));

		TableColumn<MapEntry<String, Object>, String> valueColumn = new TableColumn<>("Value");
		valueColumn.setCellValueFactory(
				(TableColumn.CellDataFeatures<MapEntry<String, Object>, String> param) -> new ReadOnlyStringWrapper(
						String.valueOf(param.getValue().getValue())));

		valueColumn.setCellFactory(TextFieldTableCell.<MapEntry<String, Object>>forTableColumn());
		valueColumn.setOnEditCommit((CellEditEvent<MapEntry<String, Object>, String> t) -> {
			MapEntry<String, Object> me = ((MapEntry<String, Object>) t.getTableView().getItems()
					.get(t.getTablePosition().getRow()));
			if (me.getValue() instanceof Double)
				me.setValue(Double.valueOf(t.getNewValue()).doubleValue());
			else
				me.setValue(t.getNewValue());
		});

		gcodePropTable.setEditable(true);
		gcodePropTable.getColumns().addAll(keyColumn, valueColumn);
		gcodePropTable.setItems(oList);

	}

	@FXML
	protected void onGenerateGcode(MouseEvent event) {

		gcSettings.setEntries(oList);
		List<String> gcodeList = generator.generateGcode(mainController.getAirfoil(Side.ROOT),
				mainController.getAirfoil(Side.TIP), gcSettings, cbMirrored.isSelected());

		gcodeList.addAll(0, getPreGcode());
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

	private List<String> getPreGcode() {

		if (preGcodeTextArea.getText().equals(""))
			return new ArrayList<String>();

		return Arrays.asList(preGcodeTextArea.getText().trim().split(System.lineSeparator()));
	}

	private void exportGcode(File file, String text) throws Exception {

		PrintWriter pw = new PrintWriter(new FileWriter(file));
		pw.print(text);
		pw.close();
	}

	@Override
	public void onProjectLoad(Project project, JsonObject properties) {

		oList.clear();
		JsonObject jo = properties.getJsonObject("GCODE");
		List<MapEntry<String, Object>> pList = new ArrayList<MapEntry<String, Object>>();

		jo.entrySet().forEach(es -> pList
				.add(new MapEntry<String, Object>(es.getKey(), es.getValue().toString().replaceAll("\"", ""))));
		oList.addAll(pList);
		gcSettings.setEntries(pList);
		gcodePropTable.refresh();

	}

	@Override
	public void onProjectSave(Project project, JsonObjectBuilder builder) {

		JsonObjectBuilder job = Json.createObjectBuilder();
		oList.forEach(p -> job.add(p.getKey(), String.valueOf(p.getValue())));
		builder.add("GCODE", job.build());

	}

}
