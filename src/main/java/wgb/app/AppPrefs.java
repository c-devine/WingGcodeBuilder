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

	@Value("#{systemProperties['user.home'] ?: '.'}")
	private String userHome;
	@Value("${app.folderName:.wgb}")
	private String folderName;

	private String lastDirectory = ".";

	@EventListener
	void contextRefreshedEvent(ContextRefreshedEvent event) {

		try {
			File file = getPrefsFile();
			if (file.exists()) {
				JsonReader jsonReader = Json.createReader(new FileInputStream(file));
				JsonObject obj = jsonReader.readObject();
				this.setLastDirectory(obj.getString(AppPrefs.LAST_DIR, "."));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void savePrefs() throws Exception {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add(AppPrefs.LAST_DIR, this.getLastDirectory());

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
		dir.mkdirs();
		return new File(dir, PREFS_FILE);
	}

	public String getLastDirectory() {
		return lastDirectory;
	}

	public void setLastDirectory(String lastDirectory) {
		this.lastDirectory = lastDirectory;
	}

}
