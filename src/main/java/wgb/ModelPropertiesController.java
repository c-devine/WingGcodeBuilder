package wgb;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import wgb.app.AppEventType;
import wgb.app.Project;
import wgb.domain.measure.Unit;
import wgb.domain.measure.Weight;

@Component
public class ModelPropertiesController implements Initializable {

	private final static Logger logger = LogManager.getLogger();

	@Autowired
	Project project;

	@Autowired
	StatusBarController sbc;
	@Autowired
	private ApplicationEventPublisher publisher;

	@FXML
	TextField name;
	@FXML
	TextField weight;
	@FXML
	Label weightLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Unit weightUnit = (MainController.unit.equals(Unit.INCH) ? Unit.OZ : Unit.GM);

		String label = "Model Weight (" + (weightUnit.equals(Unit.OZ) ? "oz):" : "gm):");

		name.setText(project.getModelName());
		weightLabel.setText(label);

		if (project.getModelWeight() == null)
			weight.setText("");
		else
			weight.setText(String.format("%.2f", project.getModelWeight().getWeight(weightUnit)));

	}

	@FXML
	protected void onSave(MouseEvent event) throws UnsupportedEncodingException {

		String sName = name.getText();
		String sWeight = weight.getText();

		Unit weightUnit = (MainController.unit.equals(Unit.INCH) ? Unit.OZ : Unit.GM);

		project.setModelName(sName);

		if (sWeight.isEmpty())
			project.setModelWeight(null);
		else
			project.setModelWeight(new Weight(Double.valueOf(sWeight), weightUnit));

		publisher.publishEvent(AppEventType.REFRESH);

		((Stage) (((Button) event.getSource()).getScene().getWindow())).close();

	}

	@FXML
	protected void onCancel(MouseEvent event) {
		((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
	}

}
