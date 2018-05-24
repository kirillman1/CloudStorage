import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    }


    @FXML
    private void upload(ActionEvent actionEvent){
        data.add("test");
    }

    @FXML
    public void delete(ActionEvent actionEvent) {
        if(view.getSelectionModel().getSelectedIndex() != -1){
            data.remove(view.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML
    public void download(ActionEvent actionEvent) {

    }

    @FXML
    public void rename(ActionEvent actionEvent) {

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
