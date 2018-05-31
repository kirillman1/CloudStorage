package kirillgontov.cloudstorage.client.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kirillgontov.cloudstorage.client.Client;
import kirillgontov.cloudstorage.common.Command;
import kirillgontov.cloudstorage.common.Message;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class LaunchController implements Initializable {

    @FXML
    Button closeBtn, logOutBtn, uploadBtn, downloadBtn, deleteBtn, renameBtn;
    @FXML
    Label errorMsg, username;
    @FXML
    ListView<Path> listView;
    private ObservableList<Path> observableList;


    private static Client client;
    public static void setClient(Client client) {
        LaunchController.client = client;
    }
    private static String usernameText;
    public static void setUsernameText(String usernameText) {
        LaunchController.usernameText = usernameText;
    }



    public void initialize(URL location, ResourceBundle resources) {
        username.setText(usernameText);
        errorMsg.setVisible(false);
        observableList = FXCollections.observableArrayList();
        listView.setItems(observableList);

        //drag and drop
        listView.setOnDragOver(event -> {
            if (event.getGestureSource() != listView && event.getDragboard().hasFiles()){
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        listView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasFiles()){
                upload(dragboard.getFiles().get(0).getAbsoluteFile());
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }


    private void upload(File file) {
        try {
            if (file != null) {
                String fileName = file.getName();
                byte[] fileBytes = Files.readAllBytes(file.toPath());

                client.sendMessage(new Message.MessageBuilder().setCommand(Command.UPLOAD)
                        .setUsername(username.getText())
                        .setFileName(fileName)
                        .setFileBytes(fileBytes)
                        .create());
                Message message = client.receiveMessage();
                switch (message.getCommand()) {
                    case UPLOAD_SUCCESS:
                        //TODO setNewFileList();
                        break;
                    case UPLOAD_FAILD:
                        showAlert("File already exists");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void chooseFileAndUpload () {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());
        upload(file);
    }

    @FXML
    public void delete() {
        if(listView.getSelectionModel().getSelectedIndex() != -1){
            //TODO
            observableList.remove(listView.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML
    public void download() {


    }

    @FXML
    public void rename() {

    }


    @FXML
    private void disconnect() throws IOException {




        getLoginScene();
    }




    private void getLoginScene() throws IOException {
        Parent rootSignUp = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene sceneSignUp = new Scene(rootSignUp, 600, 400);
        Stage stage = (Stage) logOutBtn.getScene().getWindow();
        stage.setScene(sceneSignUp);
    }

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        errorMsg.setText(msg);
        errorMsg.setVisible(true);
    }
}
