package wgb;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import wgb.domain.Airfoil;
import wgb.domain.LayoutCalculator;
import wgb.domain.Side;
import wgb.domain.measure.Length;

@Component
public class LayoutController implements Initializable {

	private final static Logger logger = LogManager.getLogger();
	private final static int BUFFER = 50;
	private final static double MIN_SCALE = 1.0;

	@FXML
	private VBox vBox;

	@FXML
	private Canvas canvas;

	@Autowired
	MainController mainController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		canvas.widthProperty().bind(vBox.widthProperty());
		canvas.heightProperty().bind(vBox.heightProperty());
		canvas.widthProperty().addListener(cl -> draw());
		canvas.heightProperty().addListener(cl -> draw());

	}

	private void draw() {

		if (canvas.getWidth() == 0.0 || canvas.getHeight() == 0.0)
			return;

		GraphicsContext gc = canvas.getGraphicsContext2D();

		if (mainController.getAirfoil(Side.ROOT).getName().equals(Airfoil.DEFAULT_NAME)
				|| mainController.getAirfoil(Side.TIP).getName().equals(Airfoil.DEFAULT_NAME)) {
			gc.setFont(Font.font("Arial", 35));
			drawText("Please select root and tip.", canvas.getWidth() / 2 - 200, canvas.getHeight() / 2, gc,
					Color.WHITE, Color.NAVY);
			return;
		}

		LayoutCalculator lc = new LayoutCalculator(mainController.getAirfoil(Side.ROOT),
				mainController.getAirfoil(Side.TIP));

		gc.clearRect(-BUFFER, -BUFFER, canvas.getWidth() + BUFFER, canvas.getHeight() + BUFFER);
		gc.setTransform(getScale(lc), 0, 0, getScale(lc),
				(canvas.getWidth() - (lc.getBlockWidth().asMM() * getScale(lc))) / 2,
				(canvas.getHeight() - (lc.getBlockHeight().asMM() * getScale(lc))) / 2);

		// draw block
		double[] xPoints1 = { lc.getBlockRootLe().getX(), lc.getBlockTipLe().getX(), lc.getBlockTipTe().getX(),
				lc.getBlockRootTe().getX() };
		double[] yPoints1 = { lc.getBlockRootLe().getY(), lc.getBlockTipLe().getY(), lc.getBlockTipTe().getY(),
				lc.getBlockRootTe().getY() };
		gc.setStroke(Color.BLACK);
		gc.setFill(Color.SKYBLUE);
		gc.strokePolygon(xPoints1, yPoints1, 4);
		gc.fillPolygon(xPoints1, yPoints1, 4);

		// draw wing
		gc.setFill(Color.LIGHTGRAY);
		gc.setLineWidth(2);
		double[] xPoints2 = { lc.getRootLe().getX(), lc.getTipLe().getX(), lc.getTipTe().getX(),
				lc.getRootTe().getX() };
		double[] yPoints2 = { lc.getRootLe().getY(), lc.getTipLe().getY(), lc.getTipTe().getY(),
				lc.getRootTe().getY() };

		gc.strokePolygon(xPoints2, yPoints2, 4);
		gc.fillPolygon(xPoints2, yPoints2, 4);

		// draw chords
		gc.setStroke(Color.BLACK);
		gc.setLineDashes(10.0);
		gc.beginPath();
		gc.moveTo(lc.getRootLe().getX() - 10, lc.getRootLe().getY());
		gc.lineTo(lc.getBlockRootTe().getX() - 10, lc.getBlockRootTe().getY());

		gc.moveTo(lc.getBlockRootTe().getX(), lc.getBlockRootTe().getY() + 10);
		gc.lineTo(lc.getBlockTipTe().getX(), lc.getBlockTipTe().getY() + 10);

		gc.moveTo(lc.getTipTe().getX() + 10, lc.getTipTe().getY());
		gc.lineTo(lc.getBlockTipLe().getX() + 10, lc.getBlockTipLe().getY());
		gc.stroke();

		// draw root extension
		gc.setLineDashes(3.0);
		gc.beginPath();
		gc.moveTo(lc.getBlockRootTe().getX(), lc.getBlockRootTe().getY());
		gc.lineTo(lc.getRootTe().getX(), lc.getRootTe().getY());
		gc.stroke();

		// draw labels
		gc.setLineDashes(0.0);
		gc.setFont(Font.font("Arial", 15));
		gc.setFill(Color.BLACK);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1.0);

		String sSweep = String.format("%.2f deg", lc.getSweep());
		drawText(sSweep, 10, 50, gc);
		drawText(getLenString(lc.getRootChord()), -10, lc.getRootChord().asMM() / 2.0, gc);
		drawText(getLenString(lc.getBlockWidth()), lc.getBlockWidth().asMM() / 2.0, lc.getBlockHeight().asMM() + 15,
				gc);
		drawText(getLenString(lc.getTipChord()), lc.getBlockWidth().asMM() - 70, lc.getTipTe().getY() / 2.0, gc);

	}

	private void drawText(String val, double x, double y, GraphicsContext gc) {

		drawText(val, x, y, gc, Color.WHITE, Color.GRAY);
	}

	private void drawText(String val, double x, double y, GraphicsContext gc, Paint txtPaint, Paint bgPaint) {

		Paint fill = gc.getFill();

		Text text = new Text(val);
		text.setFont(gc.getFont());
		double width = text.getBoundsInLocal().getWidth();
		double height = text.getBoundsInLocal().getHeight();
		gc.setFill(bgPaint);
		gc.fillRect(x, y - height + 3, width, height);
		gc.setFill(txtPaint);
		gc.fillText(val, x, y);

		gc.setFill(fill);
	}

	private String getLenString(Length len) {
		return len.toFormattedString(MainController.unit);
	}

	private double getScale(LayoutCalculator lc) {

		return Math.max(Math.min((canvas.getWidth() - (2 * BUFFER)) / lc.getBlockWidth().asMM(),
				(canvas.getHeight() - (2 * BUFFER)) / lc.getBlockHeight().asMM()), MIN_SCALE);
	}

}
