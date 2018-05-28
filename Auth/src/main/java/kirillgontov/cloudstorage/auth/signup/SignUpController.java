package kirillgontov.cloudstorage.auth.signup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kirillgontov.cloudstorage.auth.login.LoginController;
import kirillgontov.cloudstorage.common.Command;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class SignUpController implements Initializable {
    @FXML
    TextField firstName, lastName, email, confirmEmail;
    @FXML
    PasswordField password, confirmPassword;
    @FXML
    Button submitButton, closeBtn, backBtn;
    @FXML
    Label errorMsg;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMsg.setVisible(false);
    }

    @FXML
    private void addNewUser(){
        if (isEmailConfirmed() && isPasswordConfirmed()){
            /*String response = LoginController.client.sendRequest(Command.REGISTER.getText() + " "
                        + firstName.getText() + " " + lastName.getText() + " "
                        + email.getText() + " " + password.getText().hashCode());

            if (response.equals(Command.REGISTER_SUCCESS.getText()))
                try {
                    getLoginScene();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Can't get new scene");
                }
            else if (response.equals(Command.USERNAME_EXISTS.getText())){
                showAlert("This email address is already registered");
                email.clear();
                email.requestFocus();
            }*/
        }
        if (!isEmailConfirmed()){
            showAlert("Email does not match");
            confirmEmail.clear();
            confirmEmail.requestFocus();
        } else if (!isPasswordConfirmed()){
            showAlert("Password does not match");
            confirmPassword.clear();
            confirmPassword.requestFocus();
        }
    }

    private boolean isPasswordConfirmed(){
        return password.getText().equals(confirmPassword.getText());
    }

    private boolean isEmailConfirmed(){
        return email.getText().equals(confirmEmail.getText());
    }

    private void showAlert(String msg) {
        errorMsg.setText(msg);
        errorMsg.setVisible(true);
    }

    @FXML
    private void getLoginScene() throws IOException {
        Parent rootSignUp = FXMLLoader.load(getClass().getResource("../login/login.fxml"));
        Scene sceneSignUp = new Scene(rootSignUp, 600, 400);
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.setScene(sceneSignUp);
    }

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }



}
