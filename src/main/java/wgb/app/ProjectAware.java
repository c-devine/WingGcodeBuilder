package wgb.app;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public interface ProjectAware {

	public void onProjectLoad(Project project, JsonObject properties);

	public void onProjectSave(Project project, JsonObjectBuilder builder);
}
