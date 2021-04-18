package my.cloud.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public ListView<String> localFilesList;
    public ListView<String> cloudFilesList;
    public Button uplButton;
    public Button downButton;

    public NetworkService networkService;

    public void create() {
        localFilesList.getItems().clear();
        cloudFilesList.getItems().clear();
        localFilesList.getItems().addAll("1","2","3");
        cloudFilesList.getItems().addAll("a","b","c");
    }

    public void uploadFile(ActionEvent actionEvent) {
        //networkService.sendCommand(commandTextField.getText().trim());
        String temp = localFilesList.getSelectionModel().getSelectedItem();
        cloudFilesList.getItems().addAll(temp);
    }

    public void downloadFile(ActionEvent actionEvent) {
        //networkService.sendCommand(commandTextField.getText().trim());
        String temp = cloudFilesList.getSelectionModel().getSelectedItem();
        localFilesList.getItems().addAll(temp);
    }


    public void deleteFile(ActionEvent actionEvent) {
        if (localFilesList.getSelectionModel().getSelectedItems().size()>0) {
            localFilesList.getItems().removeAll(localFilesList.getSelectionModel().getSelectedItem());
        }  else if (cloudFilesList.getSelectionModel().getSelectedItems().size()>0) {
            cloudFilesList.getItems().removeAll(cloudFilesList.getSelectionModel().getSelectedItem());
        }
    }

    public void localClick(MouseEvent mouseEvent) {
        downButton.setDisable(true);
        uplButton.setDisable(false);
        cloudFilesList.getSelectionModel().select(-1);
    }

    public void cloudClick(MouseEvent mouseEvent) {
        downButton.setDisable(false);
        uplButton.setDisable(true);
        localFilesList.getSelectionModel().select(-1);
    }

    public void createNewFolder(ActionEvent actionEvent) {
        //Создание новой папки на облаке
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();

        downButton.setDisable(false);
        uplButton.setDisable(false);

        createCommandResultHandler();
    }

    private void createCommandResultHandler() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (true) {
                int countReadBytes = networkService.readCommandResult(buffer);
                String resultCommand = new String(buffer, 0, countReadBytes);
                Platform.runLater(() -> localFilesList.getItems().addAll(resultCommand + System.lineSeparator()));
            }
        }).start();
    }

    public void shutdown() {
        networkService.closeConnection();
    }
}
