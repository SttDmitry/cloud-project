package my.cloud.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public ListView<String> localFilesList;
    public ListView<String> cloudFilesList;
    public Button uplButton;
    public Button downButton;
    public TextField localPath;
    public TextField cloudPath;
    private File cloudDir = new File(System.getenv("LOCALAPPDATA")+"//CloudProject");
    private File localDir = new File(".");

    public NetworkService networkService;



    public void uploadFile(ActionEvent actionEvent) {
        //
        if(!localFilesList.getItems().isEmpty() && !(localFilesList.getSelectionModel().getSelectedItem() == null)) {
//            String temp = localFilesList.getSelectionModel().getSelectedItem();
//            cloudFilesList.getItems().addAll(temp);
            networkService.sendCommand(localDir+"/"+localFilesList.getSelectionModel().getSelectedItem());
        }

    }

    public void downloadFile(ActionEvent actionEvent) {
        //networkService.sendCommand(commandTextField.getText().trim());
        if(!cloudFilesList.getItems().isEmpty() && !(cloudFilesList.getSelectionModel().getSelectedItem() == null)) {
//        String temp = cloudFilesList.getSelectionModel().getSelectedItem();
//        localFilesList.getItems().addAll(temp);
        }
    }


    public void deleteFile(ActionEvent actionEvent) {
//        if (localFilesList.getSelectionModel().getSelectedItems().size()>0) {
//            localFilesList.getItems().removeAll(localFilesList.getSelectionModel().getSelectedItem());
//        }  else if (cloudFilesList.getSelectionModel().getSelectedItems().size()>0) {
//            cloudFilesList.getItems().removeAll(cloudFilesList.getSelectionModel().getSelectedItem());
//        }
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

        downButton.setDisable(true);
        uplButton.setDisable(true);
        refreshFilesLists();
        cloudPath.setText(cloudDir.getName());
        localPath.setText(localDir.getName());


        createCommandResultHandler();
    }

    private void createCommandResultHandler() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (true) {
                int countReadBytes = networkService.readCommandResult(buffer);
                String resultCommand = new String(buffer, 0, countReadBytes);
                String[] listOfFiles = resultCommand.split(", ");
                Platform.runLater(() -> cloudFilesList.getItems().clear());
                Platform.runLater(() -> cloudFilesList.getItems().addAll(listOfFiles));
            }
        }).start();
    }

    private void refreshFilesLists(){
//        if (!cloudDir.exists()) {
//            cloudDir.mkdirs();
//        } else {
//            for (File childFile : cloudDir.listFiles()) {
//                if (childFile.isFile()){
//                    cloudFilesList.getItems().add(childFile.getName());
//                }
//            }
//        }
        for (File childFile : localDir.listFiles()) {
            if (childFile.isFile()){
                localFilesList.getItems().add(childFile.getName());
            }
        }
    }

    public void shutdown() {
        networkService.closeConnection();
    }
}
