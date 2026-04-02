
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        Scene scene = new Scene(root,1000,650);
        scene.getStylesheets().add(getClass().getResource("loginDesign.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Aino");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setResizable(true);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
