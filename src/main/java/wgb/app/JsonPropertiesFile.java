package wgb.app;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonIgnoreProperties({ "file" })
public abstract class JsonPropertiesFile {

	protected final static Logger logger = LogManager.getLogger();
	private File file;
	private ObjectMapper mapper;

	public JsonPropertiesFile() {

		mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	public JsonPropertiesFile(File file) {

		this.file = file;

	}

	public boolean load(File f) {
		file = f;
		return this.load();
	}

	public boolean load() {

		if (!file.exists())
			return false;

		try {
			BeanUtils.copyProperties(mapper.readValue(file, getClass()), this);
		} catch (Exception e) {
			logger.error("Error loading properties file.", e);
			return false;
		}

		return true;
	}

	public boolean save(File f) {
		this.file = f;
		return save();
	}

	public boolean save() {

		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		try {
			mapper.writeValue(file, this);
		} catch (Exception e) {
			logger.error("Error saving properties file.", e);
			return false;
		}

		return true;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		if (file != null)
			this.file = file;
	}

}
