package wgb.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.springframework.stereotype.Component;

import javafx.geometry.Point2D;

/*
 * The files are in an x,y format starting from trailing edge, along the upper surface to the
 * leading edge and back around the lower surface to trailing edge.
 * Just looks for file ending in .dat for now.
 */
@Component
public class SeligAirfoilReader implements AirfoilReader {

	@Override
	public Airfoil read(File file) throws Exception {

		Airfoil af = new Airfoil();

		BufferedReader reader;
		reader = new BufferedReader(new FileReader(file));
		boolean firstLine = true;

		while (true) {

			String line = reader.readLine();
			// first line is a description
			if (firstLine) {
				af.setName(line.trim());
				firstLine = false;
				continue;
			}
			if (line == null)
				break;

			if (line.isEmpty())
				continue;

			String[] sArray = line.trim().split("\\s+");
			af.getXy().add(new Point2D(Double.valueOf(sArray[0]), Double.valueOf(sArray[1])));
		}

		reader.close();
		return af;

	}

	@Override
	public boolean supportsFile(File file) {

		return file.getName().endsWith(".dat");
	}

}
