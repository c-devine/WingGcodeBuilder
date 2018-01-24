package wgb.app;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import wgb.domain.Airfoil;

@Component
public class Project extends JsonPropertiesFile {

	private Airfoil root;
	private Airfoil tip;

	@Autowired
	private List<ProjectAware> listeners;

	@Override
	public boolean load() {

		if (super.load()) {
			listeners.forEach(l -> l.onProjectLoad(this));
			return true;
		}

		return false;
	}

	@Override
	public boolean save(File f) {

		listeners.forEach(l -> l.onProjectSave(this));
		return super.save(f);
	}

	public Airfoil getRoot() {
		return root;
	}

	public void setRoot(Airfoil root) {
		this.root = root;
	}

	public Airfoil getTip() {
		return tip;
	}

	public void setTip(Airfoil tip) {
		this.tip = tip;
	}

}
