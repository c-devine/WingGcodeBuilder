package wgb;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import wgb.app.AppPrefs;

@Component
public class OctoPrintController implements Initializable {

	private final static Logger logger = LogManager.getLogger();

	@Autowired
	AppPrefs appPrefs;
	@Autowired
	GcodeController gcodeController;

	@FXML
	TextField host;
	@FXML
	TextField apikey;
	@FXML
	TextField filename;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		host.setText(appPrefs.getOctoPrintHost());
		apikey.setText(appPrefs.getOctoPrintApiKey());
		filename.setText(appPrefs.getOctoPrintFilename());
		filename.requestFocus();
	}

	@FXML
	protected void onUpload(MouseEvent event) throws UnsupportedEncodingException {

		if (gcodeController.gcodeTextArea.getText().isEmpty()) {
			((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
			return;
		}

		appPrefs.setOctoPrintHost(host.getText());
		appPrefs.setOctoPrintApiKey(apikey.getText());
		appPrefs.setOctoPrintFilename(filename.getText());

		try {
			appPrefs.savePrefs();
		} catch (Exception e) {
			logger.error("Error saving OctoPrint settings.", e);
		}

		((Stage) (((Button) event.getSource()).getScene().getWindow())).close();

		String gcode = gcodeController.gcodeTextArea.getText();

		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();

		ByteArrayResource contents = new ByteArrayResource(gcode.getBytes("UTF-8")) {
			@Override
			public String getFilename() {
				return filename.getText();
			}
		};

		parameters.add("file", contents);
		parameters.add("filename", filename.getText());

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "multipart/form-data");
		headers.set("Accept", "text/plain");
		headers.set("X-Api-Key", apikey.getText());

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.postForObject("http://localhost:5000/api/files/local",
				new HttpEntity<MultiValueMap<String, Object>>(parameters, headers), String.class);
		logger.info(result);
	}

	@FXML
	protected void onCancel(MouseEvent event) {
		((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
	}

}
