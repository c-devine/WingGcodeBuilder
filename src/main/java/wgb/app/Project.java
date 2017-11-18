package wgb.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Project {

	private final static Logger logger = LogManager.getLogger();

	private File file;
	private JsonObject properties;

	@Autowired
	private List<ProjectAware> listeners;

	public void save(File file) throws Exception {

		this.file = file;

		JsonObjectBuilder builder = Json.createObjectBuilder();

		for (ProjectAware listener : listeners) {
			listener.onProjectSave(this, builder);
		}

		Map<String, Boolean> config = new HashMap<>();
		config.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory jwf = Json.createWriterFactory(config);
		StringWriter sw = new StringWriter();
		try (JsonWriter jsonWriter = jwf.createWriter(sw)) {
			jsonWriter.writeObject(builder.build());
		}

		FileWriter fw = new FileWriter(file);
		fw.write(sw.toString());
		fw.close();

	}

	public void load(File file) throws Exception {

		this.file = file;

		JsonReader jsonReader = Json.createReader(new FileInputStream(file));
		JsonObject obj = jsonReader.readObject();
		jsonReader.close();

		for (ProjectAware listener : listeners) {
			listener.onProjectLoad(this, obj);
		}

	}

	public JsonObjectBuilder jsonObjectToBuilder(JsonObject jo) {
		JsonObjectBuilder job = Json.createObjectBuilder();

		for (Entry<String, JsonValue> entry : jo.entrySet()) {
			job.add(entry.getKey(), entry.getValue());
		}

		return job;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public JsonObject getProperties() {
		return properties;
	}

	public void setProperties(JsonObject properties) {
		this.properties = properties;
	}

}
