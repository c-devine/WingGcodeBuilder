package wgb;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import wgb.app.AppEventType;
import wgb.app.FileChooserHelper;
import wgb.domain.Airfoil;
import wgb.domain.AirfoilManager;
import wgb.domain.Side;
import wgb.fx.FoilCanvas;

@Component
public class TwoDController implements Initializable {

	private final static Logger logger = LogManager.getLogger();

	@Autowired
	MainController mainController;
	@Autowired
	AirfoilManager afManager;
	@Autowired
	FileChooserHelper fcHelper;

	@FXML
	private FoilCanvas canvasRoot;
	@FXML
	private FoilCanvas canvasTip;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setAirfoil(Airfoil af, Side side) {

		if (side == Side.ROOT)
			canvasRoot.setAirFoil(af);
		else
			canvasTip.setAirFoil(af);
	}

	@EventListener
	private void onAppEvent(AppEventType type) {

		if (type.equals(AppEventType.REFRESH)) {
			canvasRoot.autosize();
			canvasTip.autosize();
		}
	}

	private Airfoil getAirfoil() {

		FileChooser fileChooser = fcHelper.getFileChooser();
		File f = fileChooser.showOpenDialog(((Node) canvasRoot).getScene().getWindow());

		if (f != null) {
			fcHelper.saveLastFileLocation(f);
			try {
				if (!afManager.isSupported(f)) {
					Alert alert = new Alert(AlertType.WARNING, "Unrecognized file type.");
					alert.showAndWait();
					return null;
				}
				return afManager.read(f);

			} catch (Exception e) {
				logger.error("Exception reading " + f.getAbsolutePath(), e);
			}
		}

		return null;
	}

	@FXML
	public void onSelectFoilRoot(MouseEvent event) {
		Airfoil af = getAirfoil();
		if (af != null) {
			mainController.addAirfoil(af, Side.ROOT);
		}
	}

	@FXML
	public void onSelectFoilTip(MouseEvent event) {
		Airfoil af = getAirfoil();
		if (af != null) {
			mainController.addAirfoil(af, Side.TIP);
		}
	}

}
