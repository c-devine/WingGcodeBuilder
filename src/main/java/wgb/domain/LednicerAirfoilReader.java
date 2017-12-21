package wgb.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javafx.geometry.Point2D;

/*
 *  Lednicer's files are upper surface points leading edge to trailing edge and then lower surface leading edge
 *  to trailing edge.
 */
@Component
public class LednicerAirfoilReader implements AirfoilReader {

	private final static Logger logger = LogManager.getLogger();

	static IntStream revRange(int from, int to) {
		return IntStream.range(from, to).map(i -> to - i + from - 1);
	}

	@Override
	public Airfoil read(File file) throws Exception {

		Airfoil af = new Airfoil();

		BufferedReader reader;
		reader = new BufferedReader(new FileReader(file));
		String line;
		boolean firstLine = true;
		boolean elementCount = false;
		int topCount = 0;
		int bottomCount = 0;
		List<Point2D> top = new ArrayList<Point2D>();
		List<Point2D> bottom = new ArrayList<Point2D>();

		while ((line = reader.readLine()) != null) {

			line = line.trim();

			// first line is a description
			if (firstLine) {
				af.setName(line);
				firstLine = false;
				continue;
			}

			if (line.isEmpty())
				continue;

			String[] sArray = line.split("\\s+");

			if (!elementCount) {
				topCount = Integer.parseInt(StringUtils.substringBefore(sArray[0], "."));
				bottomCount = Integer.parseInt(StringUtils.substringBefore(sArray[1], "."));
				elementCount = true;
				continue;
			}

			// at this point we should be reading in coordinates
			Point2D pt = new Point2D(Double.valueOf(sArray[0]), Double.valueOf(sArray[1]));
			if (top.size() != topCount)
				top.add(pt);
			else if (bottom.size() != bottomCount)
				bottom.add(pt);
		}

		// reorder and add the coordinates
		revRange(0, top.size()).forEachOrdered(i -> af.getXy().add(top.get(i)));
		IntStream.range(1, bottom.size()).forEachOrdered(i -> af.getXy().add(bottom.get(i)));
		if (af.getXy().get(0) != af.getXy().get(af.getXy().size() - 1))
			af.getXy().add(af.getXy().get(0));

		reader.close();
		return af;

	}

	/**
	 * Checks to see if the count of coordinates is the first set of numeric
	 * values.
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
					return Double.parseDouble(sArray[0]) > 1.0 && Double.parseDouble(sArray[1]) > 1.0;
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
