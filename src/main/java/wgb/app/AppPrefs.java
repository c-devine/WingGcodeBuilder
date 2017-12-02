package wgb.app;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppPrefs extends JsonPropertiesFile {

	@Value("#{systemProperties['user.home'] ?: '.'}")
	private String userHome;
	@Value("${app.properties.folderName}")
	private String folderName = "";
	@Value("${app.properties.fileName}")
	private String fileName = "";

	private String octoPrintHost = "";
	private String octoPrintApiKey = "";
	private String octoPrintFilename = "";
	private String lastDirectory = ".";

	@EventListener
	void contextRefreshedEvent(ContextRefreshedEvent event) {

		this.setFile(getPrefsFile());
		if (this.getFile().exists())
			if (!super.load(getPrefsFile()))
				logger.error("Error or missing app preferences");

	}

	private File getPrefsFile() {

		File dir = new File(
				userHome + System.getProperty("file.separator") + folderName + System.getProperty("file.separator"));
		if (!dir.exists())
			dir.mkdirs();
		return new File(dir, fileName);
	}

	public String getUserHome() {
		return userHome;
	}

	public void setUserHome(String userHome) {
		this.userHome = userHome;
	}

	public String getOctoPrintHost() {
		return octoPrintHost;
	}

	public void setOctoPrintHost(String octoPrintHost) {
		this.octoPrintHost = octoPrintHost;
	}

	public String getOctoPrintApiKey() {
		return octoPrintApiKey;
	}

	public void setOctoPrintApiKey(String octoPrintApiKey) {
		this.octoPrintApiKey = octoPrintApiKey;
	}

	public String getOctoPrintFilename() {
		return octoPrintFilename;
	}

	public void setOctoPrintFilename(String octoPrintFilename) {
		this.octoPrintFilename = octoPrintFilename;
	}

	public String getLastDirectory() {
		return lastDirectory;
	}

	public void setLastDirectory(String lastDirectory) {
		this.lastDirectory = lastDirectory;
	}

}
