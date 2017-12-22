package wgb.app;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import wgb.domain.GcodeSettings;

@Component
public class GcodeSettingsManager {

	private final static Logger logger = LogManager.getLogger();

	@Value("#{systemProperties['user.home'] ?: '.'}")
	private String userHome;
	@Value("${app.gcodeSettings.folderName}")
	private String folderName = "";
	@Value("${app.gcodeSettings.defaultFileName}")
	private String defaultFileName = "";

	private File getDefaultFile() {
		File dir = new File(
				userHome + System.getProperty("file.separator") + folderName + System.getProperty("file.separator"));
		if (!dir.exists())
			dir.mkdirs();

		return new File(dir, defaultFileName);
	}

	public GcodeSettings loadDefault() {

		GcodeSettingsFile gcFile = new GcodeSettingsFile();
		if (!gcFile.load(getDefaultFile())) {
			logger.info("Default gcode settings file not found, saving new file.");
			GcodeSettings gcSettings = new GcodeSettings();
			gcFile.setGcodeSettings(gcSettings);
			gcFile.save(getDefaultFile());
			return gcSettings;
		}

		return gcFile.getGcodeSettings();
	}

	public void saveAsDefault(GcodeSettings gcSettings) {
		GcodeSettingsFile gcFile = new GcodeSettingsFile();
		gcFile.setGcodeSettings(gcSettings);
		gcFile.save(getDefaultFile());
	}

	public void save(GcodeSettings gcSettings, File file) {
		GcodeSettingsFile gcFile = new GcodeSettingsFile();
		gcFile.setGcodeSettings(gcSettings);
		gcFile.save(file);
	}

	public GcodeSettings load(File file) {
		GcodeSettingsFile gcFile = new GcodeSettingsFile();
		gcFile.load(file);
		return gcFile.getGcodeSettings();
	}

}
