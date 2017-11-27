package wgb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wgb.app.AppEventType;
import wgb.fx.AbstractJavaFxApplicationSupport;
import wgb.fx.SpringFxmlLoader;

@SpringBootApplication
public class Main extends AbstractJavaFxApplicationSupport {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Value("${app.ui.title}")
	private String windowTitle;

	@Autowired
	private SpringFxmlLoader loader;

	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = (VBox) loader.load(getClass().getResource("/fx/Main.fxml").toURI());
			Scene scene = new Scene(root, 1200, 800);
			scene.getStylesheets().add(getClass().getResource("/fx/application.css").toExternalForm());
			primaryStage.setTitle(windowTitle);
			primaryStage.setScene(scene);
			primaryStage.show();
			publisher.publishEvent(AppEventType.AFTER_LOADED_EVENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launchApp(Main.class, args);
	}
}
