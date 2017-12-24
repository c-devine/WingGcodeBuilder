package wgb;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

@Component
public class StatusBarController implements Initializable {

	private final static Logger logger = LogManager.getLogger();

	@FXML
	private Label lblMessage;
	@FXML
	private ProgressBar pBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setMessage(String message) {

		if (Platform.isFxApplicationThread())
			lblMessage.setText(message);
		else
			Platform.runLater(() -> lblMessage.setText(message));
	}

	public void setMessageDelay(String message, long millis) {
		new Thread(() -> {
			try {
				Thread.sleep(millis);
				setMessage(message);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}).start();
	}

	public void clearMessage() {

		if (Platform.isFxApplicationThread())
			lblMessage.setText("");
		else
			Platform.runLater(() -> lblMessage.setText(""));
	}

	/**
	 * Set progress between 0 and 1
	 *
	 * @param value
	 */
	public void setProgress(double value) {

		if (Platform.isFxApplicationThread())
			pBar.setProgress(value);
		else
			Platform.runLater(() -> pBar.setProgress(value));

	}

	public void setProgressDelay(Double progress, long millis) {
		new Thread(() -> {
			try {
				Thread.sleep(millis);
				setProgress(progress);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}).start();
	}

}
