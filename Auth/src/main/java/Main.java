import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.initStyle(StageStyle.UNDECORATED); //remove window decoration

        Parent rootLogin = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene sceneLogin = new Scene(rootLogin, 600, 400);

        primaryStage.setTitle("Cloud Storage");
        primaryStage.setScene(sceneLogin);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
