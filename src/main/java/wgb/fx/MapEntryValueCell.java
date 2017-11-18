package wgb.fx;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MapEntryValueCell extends TableCell<MapEntry<String, Object>, Object> {

	private TextField textField;

	@Override
	public void startEdit() {
		if (!isEmpty()) {
			super.startEdit();
			createTextField();
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();

		setText(String.valueOf(super.getItem()));
		setGraphic(null);
	}

	private void createTextField() {
		textField = new TextField(getString());
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		textField.setOnAction((e) -> commitEdit(textField.getText()));
		textField.focusedProperty()
				.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					if (!newValue) {
						commitEdit(textField.getText());
					}
				});
	}

	private String getString() {
		return getItem() == null ? "" : String.valueOf(super.getItem());
	}

	@Override
	protected void updateItem(Object item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null) {
			if (item instanceof String) {
				setText((String) item);
				setGraphic(null);
			} else if (item instanceof Number) {
				setText(String.valueOf(item));
				setGraphic(null);
			} else if (item instanceof Boolean) {
				CheckBox checkBox = new CheckBox();
				checkBox.setSelected((boolean) item);
				setGraphic(checkBox);
			} else if (item instanceof Image) {
				setText(null);
				ImageView imageView = new ImageView((Image) item);
				imageView.setFitWidth(100);
				imageView.setPreserveRatio(true);
				imageView.setSmooth(true);
				setGraphic(imageView);
			} else {
				setText("N/A");
				setGraphic(null);
			}
		} else {
			setText(null);
			setGraphic(null);
		}
	}
}
