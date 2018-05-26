package kirillgontov.cloudstorage.client.launch;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LaunchController implements Initializable {

    @FXML
    Button closeBtn, logOutBtn, uploadBtn, downloadBtn, deleteBtn, renameBtn;
    @FXML
    ListView<String> view;
    private ObservableList<String> data;



    public void initialize(URL location, ResourceBundle resources) {
        data = FXCollections.observableArrayList();
        view.setItems(data);

        //drag and drop
        view.setOnDragOver(event -> {
            if (event.getGestureSource() != view && event.getDragboard().hasFiles()){
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        view.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasFiles()){
//                sendFile(dragboard.getFiles().get(0).getAbsolutePath());
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

    }


    @FXML
    private void upload(){
        data.add("test");
    }

    @FXML
    public void delete() {
        if(view.getSelectionModel().getSelectedIndex() != -1){
            data.remove(view.getSelectionModel().getSelectedIndex());
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
}
