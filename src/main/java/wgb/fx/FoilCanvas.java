package wgb.fx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import wgb.domain.Airfoil;
import wgb.domain.Densifier;

public class FoilCanvas extends ResizableCanvas {

	private final static Logger logger = LogManager.getLogger();

	private final static int PADDING_TOP = 10;
	private final static int PADDING_BOTTOM = 10;
	private final static int PADDING_LEFT = 10;
	private final static int PADDING_RIGHT = 10;

	private Airfoil airFoil = null;

	@Autowired
	Densifier densifier;

	@Override
	public void paint() {

		super.paint();
		drawLine();
		if (airFoil != null) {
			drawFoil();
		}
	}

	private void drawLine() {

		GraphicsContext gc = this.getGraphicsContext2D();
		gc.setStroke(Color.WHITE);
		gc.setLineWidth(1);
		gc.beginPath();
		gc.moveTo(PADDING_LEFT, this.getHeight() / 2);
		gc.lineTo(this.getWidth() - PADDING_RIGHT, this.getHeight() / 2);
		gc.stroke();
	}

	private void drawFoil() {

		double width = this.getWidth() - (PADDING_LEFT + PADDING_RIGHT);
		// double height = this.getHeight() - (PADDING_TOP + PADDING_BOTTOM);
		GraphicsContext gc = this.getGraphicsContext2D();
		gc.setStroke(Color.WHITE);
		gc.setLineWidth(1);
		gc.beginPath();

		boolean firstCoord = true;

		for (Point2D xyz : airFoil.getXy()) {
			double x = (xyz.getX() * width) + PADDING_LEFT;
			// use the width to keep the same scale
			double y = (this.getHeight() / 2) - (xyz.getY() * width);

			if (firstCoord) {
				gc.moveTo(x, y);
				firstCoord = false;
			} else {
				gc.lineTo(x, y);

			}
		}
		gc.stroke();
	}

	public Airfoil getAirFoil() {
		return airFoil;
	}

	public void setAirFoil(Airfoil airFoil) {
		this.airFoil = airFoil;
	}

}
