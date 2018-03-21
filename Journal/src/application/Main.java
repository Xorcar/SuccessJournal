package application;
	
import java.io.File;
import java.io.FileInputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	
	/**
	 * TODO check why on Windows dont work, need hardkodong into '/'
	 * Character that separates path folders in current OS. ('/' in Windows)
	 */
	public static char separator = File.separatorChar;
	
	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			String fxmlDocPath = view.viewClass.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			fxmlDocPath = fxmlDocPath.substring(0, fxmlDocPath.length()- 4);
			fxmlDocPath += "src" + separator + "view" + separator +
					"MainWindow.fxml";
			System.out.println(fxmlDocPath);
			
			FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);

			AnchorPane root = (AnchorPane) loader.load(fxmlStream);
			
			// Create the Scene
			Scene scene = new Scene(root);
			// Set the Scene to the Stage
			stage.setScene(scene);
			// Set the Title to the Stage
			stage.setTitle("Журнал");
			// Display the Stage
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
