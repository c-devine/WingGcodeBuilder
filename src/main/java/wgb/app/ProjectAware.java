package wgb.app;

public interface ProjectAware {

	public void onProjectLoad(Project project);

	public void onProjectSave(Project project);
}
