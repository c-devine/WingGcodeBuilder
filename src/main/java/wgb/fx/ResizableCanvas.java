package wgb.fx;

import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {

	public void paint() {

	}

	@Override
	public double minHeight(double width) {
		return 0;
	}

	@Override
	public double maxHeight(double width) {
		return Double.MAX_VALUE;
	}

	@Override
	public double prefHeight(double width) {
		return minHeight(width);
	}

	@Override
	public double minWidth(double height) {
		return 0;
	}

	@Override
	public double maxWidth(double height) {
		return Double.MAX_VALUE;
	}

	@Override
	public double prefWidth(double height) {
		return minWidth(height);
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public void resize(double width, double height) {
		super.setWidth(width);
		super.setHeight(height);
		paint();

	}
}
