package wgb.domain;

import java.io.File;

public interface AirfoilReader {

	public Airfoil read(File file) throws Exception;

	public boolean supportsFile(File file);

}
