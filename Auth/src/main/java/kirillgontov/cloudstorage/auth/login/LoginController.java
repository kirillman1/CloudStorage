package kirillgontov.cloudstorage.auth.login;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kirillgontov.cloudstorage.client.Configuration;
import kirillgontov.cloudstorage.common.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class LoginController{

    @FXML
    Label errorMsg;
    @FXML
    Button signUpBtn, logInBtn, closeBtn;
    @FXML
    TextField email;
    @FXML
    PasswordField password;

    public TextField getEmail() {
        return email;
    }


//    private SocketChannel clientSocket;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;


    public void connect() {
        try {
            socket = new Socket(Configuration.SERVER_HOST, Configuration.SERVER_PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String str = inputStream.readUTF();
                        if (str.startsWith(Command.LOGIN_SUCCESS.getText())) {
                            System.out.println(str);
                            getLaunchScene();
                            break;
                        }
                        if (str.startsWith(Command.USERNAME_EMPTY.getText())){
                            System.out.println(str);
                            showAlert("User does not exist");
                            email.clear();
                            password.clear();
                        }
                        if (str.startsWith(Command.PASSWORD_INCORRECT.getText())){
                            System.out.println(str);
                            showAlert("Incorrect password");
                            password.clear();
                            password.requestFocus();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    showAlert("Server disconnected");
                    try {
                        socket.close();
                        inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            t.setDaemon(true);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Connection maintenance");
        }
    }

    public void login (ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            outputStream.writeUTF(Command.LOGIN.getText() + " " + email.getText() + " " + password.getText());
            email.clear();
            password.clear();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Connection maintenance");
        }
    }


    /*@FXML
    private void login() {
        connect();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.put((Command.LOGIN.getText()+ " " + email.getText() + " " + password.getText().hashCode()).getBytes());
        buffer.flip();
        String response;
        try {
            clientSocket.write(buffer);
            buffer.clear();
            buffer.flip();
            clientSocket.read(buffer);
            response = new String(buffer.array()).trim();
            buffer.clear();
            System.outputStream.println(response);
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

    private void connect(){
        try {
            //clientSocket = SocketChannel.open(new InetSocketAddress(Configuration.SERVER_HOST, Configuration.SERVER_PORT));
        } catch (IOException e) {
            showAlert("Server connection maintenance");
        }
    }*/

    @FXML
    private void getSignUpScene(ActionEvent actionEvent) throws IOException {
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
    private void closeButtonAction(ActionEvent actionEvent){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

}
