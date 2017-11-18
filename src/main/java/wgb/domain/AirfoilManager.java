package wgb.domain;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AirfoilManager {

	@Autowired
	private List<AirfoilReader> readers;

	public Airfoil read(File file) throws Exception {

		AirfoilReader afr = findReader(file);
		if (afr == null)
			return null;
		else
			return afr.read(file);

	}

	public boolean isSupported(File file) {

		return findReader(file) != null ? true : false;
	}

	private AirfoilReader findReader(File file) {

		for (AirfoilReader afr : readers) {
			if (afr.supportsFile(file))
				return afr;
		}

		return null;
	}

}
