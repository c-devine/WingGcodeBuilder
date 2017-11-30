package wgb.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppPrefs {

	private static String LAST_DIR = "lastDirectory";
	private static String PREFS_FILE = "prefs.json";
	private static String OP_HOST = "OctoPrintHost";
	private static String OP_API_KEY = "OctoPrintApiKey";
	private static String OP_FILENAME = "OctoPrintFilename";

	@Value("#{systemProperties['user.home'] ?: '.'}")
	private String userHome;
	@Value("${app.folderName:.wgb}")
	private String folderName;
	private String octoPrintHost = "";
	private String octoPrintApiKey = "";
	private String octoPrintFilename = "";
	private String lastDirectory = ".";

	@EventListener
	void contextRefreshedEvent(ContextRefreshedEvent event) {

		try {
			File file = getPrefsFile();
			if (file.exists()) {
				JsonReader jsonReader = Json.createReader(new FileInputStream(file));
				JsonObject obj = jsonReader.readObject();
				this.setLastDirectory(obj.getString(LAST_DIR, "."));
				this.setOctoPrintHost(obj.getString(OP_HOST, ""));
				this.setOctoPrintApiKey(obj.getString(OP_API_KEY, ""));
				this.setOctoPrintFilename(obj.getString(OP_FILENAME, ""));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void savePrefs() throws Exception {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add(LAST_DIR, this.getLastDirectory());
		builder.add(OP_HOST, this.getOctoPrintHost());
		builder.add(OP_API_KEY, this.getOctoPrintApiKey());
		builder.add(OP_FILENAME, this.getOctoPrintFilename());

		Map<String, Boolean> config = new HashMap<>();
		config.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory jwf = Json.createWriterFactory(config);
		StringWriter sw = new StringWriter();
		try (JsonWriter jsonWriter = jwf.createWriter(sw)) {
			jsonWriter.writeObject(builder.build());
		}

		FileWriter fw = new FileWriter(getPrefsFile());
		fw.write(sw.toString());
		fw.close();
	}

	private File getPrefsFile() {

		File dir = new File(userHome + System.getProperty("file.separator") + folderName);
		if (!dir.exists())
			dir.mkdirs();
		return new File(dir, PREFS_FILE);
	}

	public String getLastDirectory() {
		return lastDirectory;
	}

	public void setLastDirectory(String lastDirectory) {
		this.lastDirectory = lastDirectory;
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

}
