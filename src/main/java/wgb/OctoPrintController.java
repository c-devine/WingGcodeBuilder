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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javafx.concurrent.Task;
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

		String sGcode = gcodeController.gcodeTextArea.getText();
		String sHost = host.getText();
		String sApiKey = apikey.getText();
		String sFilename = filename.getText();

		((Stage) (((Button) event.getSource()).getScene().getWindow())).close();

		if (sGcode.isEmpty())
			return;

		appPrefs.setOctoPrintHost(sHost);
		appPrefs.setOctoPrintApiKey(sApiKey);
		appPrefs.setOctoPrintFilename(sFilename);

		try {
			appPrefs.savePrefs();
		} catch (Exception e) {
			logger.error("Error saving OctoPrint settings.", e);
		}

		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();

		ByteArrayResource contents = new ByteArrayResource(sGcode.getBytes("UTF-8")) {
			@Override
			public String getFilename() {
				return sFilename;
			}
		};

		parameters.add("file", contents);
		parameters.add("filename", sFilename);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "multipart/form-data");
		headers.set("Accept", "text/plain");
		headers.set("X-Api-Key", sApiKey);

		RestTemplate restTemplate = new RestTemplate();
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(5000);

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() {

				String result = "";
				try {
					result = restTemplate.postForObject(sHost + "/api/files/local",
							new HttpEntity<MultiValueMap<String, Object>>(parameters, headers), String.class);
				} catch (Exception e) {
					logger.error("Error sending gcode data to OctoPrint.", e);
				}

				logger.info(result);
				return null;
			}

		};

		new Thread(task).start();

	}

	@FXML
	protected void onCancel(MouseEvent event) {
		((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
	}

}
