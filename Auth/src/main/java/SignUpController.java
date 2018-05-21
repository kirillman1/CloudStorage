import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


public class SignUpController {

    @FXML
    TextField firstName;

    @FXML
    TextField lastName;

    @FXML
    TextField email;

    @FXML
    TextField confirmEmail;

    @FXML
    PasswordField password;

    @FXML
    PasswordField confirmPassword;

    @FXML
    Button submitButton;

    @FXML
    Button closeBtn;

    @FXML
    Button backBtn;

    @FXML
    Label errorMsg;



    @FXML
    private void addNewUser() throws IOException{
        errorMsg.setVisible(false);
        if (isEmailConfirmed() && isPasswordConfirmed()){
            try {
                SQLHandler.addNewUser(firstName.getText(),lastName.getText(),email.getText(),password.getText());
                getLoginScene();
            } catch (SQLException e) {
                showAlert("This email address is already registered");
                email.clear();
                email.requestFocus();
            }
        } else if (isPasswordConfirmed()){
            showAlert("Email does not match");
            email.clear();
            confirmEmail.clear();
        } else if (isEmailConfirmed()){
            showAlert("Password does not match");
            password.clear();
            confirmPassword.clear();
        } else {
            showAlert("Email does not match. Password does not match");
            email.clear();
            confirmEmail.clear();
            password.clear();
            confirmPassword.clear();
        }

    }

    private boolean isPasswordConfirmed(){
        return password.getText().equals(confirmPassword.getText());
    }

    private boolean isEmailConfirmed(){
        return email.getText().equals(confirmEmail.getText());
    }

    public void showAlert(String msg) {
        errorMsg.setText(msg);
        errorMsg.setVisible(true);
    }

    /*public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }*/



    @FXML
    private void getLoginScene() throws IOException {
        Parent rootSignUp = FXMLLoader.load(getClass().getResource("login.fxml"));
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
