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
import kirillgontov.cloudstorage.client.Configuration;
import kirillgontov.cloudstorage.common.Command;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ResourceBundle;


public class LoginController{

    @FXML
    Label errorMsg;
    @FXML
    Button signUpBtn, logInBtn, closeBtn;
    @FXML
    TextField email;
    @FXML
    PasswordField password;


    private SocketChannel clientSocket;

    @FXML
    private void login() {
        try {
            clientSocket = SocketChannel.open(new InetSocketAddress(Configuration.SERVER_HOST, Configuration.SERVER_PORT));
        } catch (IOException e) {
            showAlert("Server connection maintenance");
        }
        ByteBuffer buffer = ByteBuffer.wrap((Command.LOGIN.getText()+ " " + email.getText() + " " + password.getText().hashCode()).getBytes());
        String response;
        try {
            clientSocket.write(buffer);
            buffer.clear();
            clientSocket.read(buffer);
            response = new String(buffer.array()).trim();
            buffer.clear();
            if (response.equals(Command.LOGIN_SUCCESS.getText()))
                getLaunchScene();
            else if (response.equals(Command.USERNAME_EMPTY.getText())){
                showAlert("User does not exist");
                email.clear();
                password.clear();
            } else if (response.equals(Command.PASSWORD_INCORRECT.getText())){
                showAlert("Incorrect password");
                password.clear();
                password.requestFocus();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void getSignUpScene() throws IOException {
        Parent rootSignUp = FXMLLoader.load(getClass().getResource("I:\\GitHub\\CloudStorage\\Auth\\src\\main\\java\\kirillgontov\\cloudstorage\\auth\\signup\\signup.fxml"));
        Scene sceneSignUp = new Scene(rootSignUp, 600, 400);
        Stage stage = (Stage) signUpBtn.getScene().getWindow();
        stage.setScene(sceneSignUp);
    }

    private void getLaunchScene() throws IOException {
        Parent rootLaunch = FXMLLoader.load(getClass().getResource("../../../../../../Client/src/main/java/kirillgontov/cloudstorage/client/launch/launch.fxml"));
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
