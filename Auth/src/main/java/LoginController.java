import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;



public class LoginController {

    @FXML
    Label errorMsg;
    @FXML
    Button signUpBtn, logInBtn, closeBtn;
    @FXML
    TextField email;
    @FXML
    PasswordField password;


    private SocketChannel clientSocket;
    private ByteBuffer buffer;

    @FXML
    private void connect() throws SQLException, IOException {
        if (!checkUser()) {
            email.clear();
            email.requestFocus();
            showAlert("User is not registered");
        } else if (!checkPassword()){
            password.clear();
            password.requestFocus();
            showAlert("Wrong password");
        } else {
            try {
                clientSocket = SocketChannel.open(new InetSocketAddress(Configuration.SERVER_HOST,Configuration.SERVER_PORT));
                buffer = ByteBuffer.allocate(256);
                getLaunchScene();
            } catch (IOException e) {
                showAlert("Server connection maintenance");
            }
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

    private boolean checkUser() throws SQLException {
        return SQLHandler.checkUsername(email.getText());
    }

    private boolean checkPassword() throws SQLException {
        return SQLHandler.checkPassword(email.getText(), password.getText());
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
