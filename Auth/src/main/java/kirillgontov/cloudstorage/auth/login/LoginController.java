package kirillgontov.cloudstorage.auth.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kirillgontov.cloudstorage.client.ClientNIO;
import kirillgontov.cloudstorage.common.Command;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    @FXML
    Label errorMsg;
    @FXML
    Button signUpBtn, logInBtn, closeBtn;
    @FXML
    TextField email;
    @FXML
    PasswordField password;

    public ClientNIO client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMsg.setVisible(false);
        connect();
    }

    @FXML
    private void login() {
        String response = client.sendRequest(Command.LOGIN.getText() + " " + email.getText() + " " + password.getText().hashCode());

        if (response.equals(Command.LOGIN_SUCCESS.getText()))
            try {
                getLaunchScene();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Can't get new scene");
            }
        else if (response.equals(Command.USERNAME_EMPTY.getText())){
            showAlert("User does not exist");
            email.clear();
            password.clear();

        } else if (response.equals(Command.PASSWORD_INCORRECT.getText())){
            showAlert("Incorrect password");
            password.clear();
            password.requestFocus();
        }



    }

    private void connect(){
        try {
            client = new ClientNIO();
        } catch (IOException e) {
            showAlert("Server connection maintenance");
        }
    }

    @FXML
    private void getSignUpScene() throws IOException {
        Parent rootSignUp = FXMLLoader.load(getClass().getResource( "../signup/signup.fxml"));
        Scene sceneSignUp = new Scene(rootSignUp, 600, 400);
        Stage stage = (Stage) signUpBtn.getScene().getWindow();
        stage.setScene(sceneSignUp);
    }

    private void getLaunchScene() throws IOException {
        Parent rootLaunch = FXMLLoader.load(getClass().getResource("/kirillgontov/cloudstorage/client/ui/launch.fxml"));
        Scene sceneLaunch = new Scene(rootLaunch, 600, 600);
        Stage stage = (Stage) logInBtn.getScene().getWindow();
        stage.setScene(sceneLaunch);
    }

    private void showAlert(String msg) {
        errorMsg.setText(msg);
        errorMsg.setVisible(true);
    }

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

}
