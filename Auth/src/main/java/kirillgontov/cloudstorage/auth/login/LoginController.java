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
import kirillgontov.cloudstorage.auth.signup.SignUpController;
import kirillgontov.cloudstorage.client.Client;
import kirillgontov.cloudstorage.client.ui.LaunchController;
import kirillgontov.cloudstorage.common.Command;
import kirillgontov.cloudstorage.common.Message;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    @FXML
    Label errorMsg;
    @FXML
    Button signUpBtn, logInBtn, closeBtn;
    @FXML
    TextField username;
    @FXML
    PasswordField password;

    private Client client;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMsg.setVisible(false);
        connect();
    }

    @FXML
    private void login() throws IOException, ClassNotFoundException {
        username.setDisable(true);
        password.setEditable(false);
        client.sendMessage(new Message.MessageBuilder().setCommand(Command.LOGIN)
                                                        .setUsername(username.getText())
                                                        .setPasswordHash(password.getText().hashCode())
                                                        .create());

        Message message = client.receiveMessage();
        switch (message.getCommand()){
            case LOGIN_SUCCESS:
                try {
                    LaunchController.setClient(client);
                    LaunchController.setUsernameText(username.getText());
                    LaunchController.setObservableList(message.getFileList());
                    getLaunchScene();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Can't get new scene");
                }
                break;
            case USERNAME_EMPTY:
                showAlert("User does not exist");
                username.setDisable(false);
                password.setDisable(false);
                username.clear();
                password.clear();
                break;
            case PASSWORD_INCORRECT:
                showAlert("Incorrect password");
                username.setDisable(false);
                password.setDisable(false);
                password.clear();
                password.requestFocus();
                break;
        }
    }

    private void connect(){
        try {
            client = new Client();
        } catch (IOException e) {
            client.finishConnection();
            showAlert("Server connection maintenance");
        }
    }

    @FXML
    private void getSignUpScene() throws IOException {
        SignUpController.setClient(client);
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
