package wgb.app;

import wgb.domain.GcodeSettings;

public class GcodeSettingsFile extends JsonPropertiesFile {

	private GcodeSettings gcodeSettings;

	public GcodeSettings getGcodeSettings() {
		return gcodeSettings;
	}

	public void setGcodeSettings(GcodeSettings gcodeSettings) {
		this.gcodeSettings = gcodeSettings;
	}

}
