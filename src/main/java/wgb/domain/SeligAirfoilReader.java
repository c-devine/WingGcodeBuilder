package wgb.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javafx.geometry.Point2D;

/*
 * The files are in an x,y format starting from trailing edge, along the upper surface to the
 * leading edge and back around the lower surface to trailing edge.
 */
@Component
public class SeligAirfoilReader implements AirfoilReader {

	private final static Logger logger = LogManager.getLogger();

	@Override
	public Airfoil read(File file) throws Exception {

		Airfoil af = new Airfoil();

		BufferedReader reader;
		reader = new BufferedReader(new FileReader(file));
		String line;
		boolean firstLine = true;

		while ((line = reader.readLine()) != null) {

			line = line.trim();

			// first line is a description
			if (firstLine) {
				af.setName(line);
				firstLine = false;
				continue;
			}
			if (line == null)
				break;

			line.trim();

			if (line.isEmpty())
				continue;

			String[] sArray = line.split("\\s+");
			af.getXy().add(new Point2D(Double.valueOf(sArray[0]), Double.valueOf(sArray[1])));
		}

		reader.close();
		return af;

	}

	/**
	 * Checks for the first numeric line, looks for a value of <=1.0
	 */
	@Override
	public boolean supportsFile(File file) {

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {

				String[] sArray = line.trim().split("\\s+");
				if (NumberUtils.isCreatable(sArray[0])) {
					return Double.parseDouble(sArray[0]) <= 1.0;
				}
			}

		} catch (Exception e) {
			logger.error("Error reading file: " + file.getAbsolutePath());
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error("Error closing file: " + file.getAbsolutePath());
			}
		}
		return false;
	}

}
