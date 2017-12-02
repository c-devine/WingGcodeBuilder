package wgb.app;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.stage.FileChooser;

@Component
public class FileChooserHelper {

	private final static Logger logger = LogManager.getLogger();

	@Autowired
	private AppPrefs prefs;

	public FileChooser getFileChooser() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(prefs.getLastDirectory()));
		return fileChooser;
	}

	public void saveLastFileLocation(File f) {
		if (f != null) {
			prefs.setLastDirectory(f.getParent());
			if (!prefs.save())
				logger.error("Error saving app prefs.");
		}

	}

}
