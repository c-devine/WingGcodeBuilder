package wgb;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.briksoftware.updatefx.core.UpdateFX;
import com.briksoftware.updatefx.core.XMLRetrieverService;
import com.briksoftware.updatefx.model.Application;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import wgb.app.AppEventType;

@Component
public class UpdateController implements Initializable {

	private final static Logger logger = LogManager.getLogger();
	private Properties currentProperties;
	private UpdateFX updater;
	private URL updateURL;
	private Application remoteApplication;

	public UpdateController() {
		try {
			currentProperties = new Properties();
			currentProperties.load(new ClassPathResource("/update/app-info.properties").getInputStream());
			updater = new UpdateFX(currentProperties, new ClassPathResource("/update/update.css").getURL());
			updateURL = new URL(currentProperties.getProperty("app.updatefx.url"));
		} catch (Exception e) {
			logger.error("Error loading current properties file.", e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void processCheckUpdate(ActionEvent event) {

		XMLRetrieverService xmlRetriever = new XMLRetrieverService(updateURL);
		xmlRetriever.setOnSucceeded(w -> {
			remoteApplication = xmlRetriever.getValue();
			int remoteVersion = remoteApplication.getReleases().get(0).getId();
			int currentVersion = Integer.parseInt(currentProperties.getProperty("app.release"));
			logger.info(String.format("Current version = %d Remote version =  %d", currentVersion, remoteVersion));
			if (remoteVersion > currentVersion) {
				updater.checkUpdates();
			} else {
				showAlert("No updates found.");
			}
		});
		xmlRetriever.setOnFailed(w -> {
			showAlert("Error checking for updates, please see logs for details.");
			logger.error("Error checking for updates: ", xmlRetriever.getException());
		});

		xmlRetriever.start();
	}

	public void showAlert(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Check for Updates");
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}

	@EventListener
	private void onAppEvent(AppEventType type) {

		if (type.equals(AppEventType.AFTER_LOADED_EVENT)) {
			try {
				updater.checkUpdates();
			} catch (Exception e) {
				logger.error("Error checking for updates.", e);
			}
		}
	}

}
