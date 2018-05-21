import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


public class LoginController {

    @FXML
    Button signUpBtn;

    @FXML
    Button logInBtn;

    @FXML
    Button closeBtn;

    @FXML
    TextField email;

    @FXML
    TextField password;

    @FXML
    private void launch() throws SQLException, IOException {
        if (SQLHandler.checkUsernamePassword(email.getText(), password.getText())){
            getLaunchScene();
        }
    }


    @FXML
    private void getSignUpScene() throws IOException {
        Parent rootSignUp = FXMLLoader.load(getClass().getResource("signup.fxml"));
        Scene sceneSignUp = new Scene(rootSignUp, 600, 400);
        Stage stage = (Stage) signUpBtn.getScene().getWindow();
        stage.setScene(sceneSignUp);
    }

    private void getLaunchScene() throws IOException {
        Parent rootLaunch = FXMLLoader.load(getClass().getResource("launch.fxml"));
        Scene sceneLaunch = new Scene(rootLaunch, 600, 600);
        Stage stage = (Stage) logInBtn.getScene().getWindow();
        stage.setScene(sceneLaunch);
    }



    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

}
