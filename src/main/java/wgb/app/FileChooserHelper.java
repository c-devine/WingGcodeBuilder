package wgb.app;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.stage.FileChooser;

@Component
public class FileChooserHelper {

	@Autowired
	AppPrefs prefs;

	public FileChooser getFileChooser() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(prefs.getLastDirectory()));
		return fileChooser;
	}

	public void saveLastFileLocation(File f) {
		if (f != null) {
			prefs.setLastDirectory(f.getParent());
			try {
				prefs.savePrefs();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
