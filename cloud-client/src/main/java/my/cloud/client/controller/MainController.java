package my.cloud.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;
import my.cloud.common.Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public ListView<String> localFilesList;
    public ListView<String> cloudFilesList;
    public Button uplButton;
    public Button downButton;
    public TextField localPath;
    public TextField cloudPath;

    public NetworkService networkService;

    private Stage stage;

    private final Map<String, File> localFiles = new HashMap<>();


    public void uploadFile() {
        if (!localFilesList.getItems().isEmpty() && !(localFilesList.getSelectionModel().getSelectedItem() == null)) {
//            System.out.println(localFiles.get(localFilesList.getSelectionModel().getSelectedItem()));
            networkService.getChannel().writeAndFlush(Common.UPLOAD + " " + localFiles.get(localFilesList.getSelectionModel().getSelectedItem()).length() + " " + localFilesList.getSelectionModel().getSelectedItem());

            Platform.runLater(()-> {
                refreshFilesLists();
            });
            // stage.showAll + close wait
        }

    }

    public void downloadFile() {
        if (!cloudFilesList.getItems().isEmpty() && !(cloudFilesList.getSelectionModel().getSelectedItem() == null)) {
            System.out.println(localFiles.get(localFilesList.getSelectionModel().getSelectedItem()));
            networkService.getChannel().writeAndFlush(Common.DOWNLOAD + " " + Common.LOCAL_DIR + File.separator + cloudFilesList.getSelectionModel().getSelectedItem());
            //stage.hideAll + show wait
            // stage.showAll + close wait
            Platform.runLater(()-> {
                refreshFilesLists();
            });
        }
    }


    public void localClick() {
        downButton.setDisable(true);
        uplButton.setDisable(false);
        cloudFilesList.getSelectionModel().select(-1);
    }

    public void cloudClick() {
        downButton.setDisable(false);
        uplButton.setDisable(true);
        localFilesList.getSelectionModel().select(-1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()->{
            stage = (Stage) localPath.getScene().getWindow();
        });
        networkService = Factory.getNetworkService();
        networkService.start();
        long mil = System.currentTimeMillis();
        while (System.currentTimeMillis() - mil < 1000){
            waiting();}
        waitFinished();
        System.out.println(networkService.getChannel());
        downButton.setDisable(true);
        uplButton.setDisable(true);
        cloudPath.setText(new File(Common.CLOUD_DIR.toString()).getName());
        localPath.setText(new File(Common.LOCAL_DIR.toString()).getName());
        refreshFilesLists();
    }


    private void refreshFilesLists() {
        long mil = System.currentTimeMillis();
        while (System.currentTimeMillis() - mil < 300){
            waiting();}
        waitFinished();
        localFilesList.getItems().clear();
        cloudFilesList.getItems().clear();
        networkService.getChannel().writeAndFlush(Common.LS.toString());
        for (File childFile : Objects.requireNonNull(new File(Common.LOCAL_DIR.toString()).listFiles())) {
            if (childFile.isFile()) {
                localFiles.put(childFile.getName(), childFile);
                localFilesList.getItems().add(childFile.getName());
            }
        }
        while (System.currentTimeMillis() - mil < 1000){
        waiting();}
        waitFinished();
        try (BufferedReader reader = new BufferedReader(new FileReader(Common.FILES_LIST.toString()))) {
            String resultCommand = reader.readLine();
            String[] listOfFiles = resultCommand.split(", ");
            cloudFilesList.getItems().clear();
            cloudFilesList.getItems().addAll(listOfFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waiting() {
        if (stage != null) {
            stage.setOpacity(0.1f);
        }
    }

    public void waitFinished(){
        if (stage != null) {
            stage.setOpacity(1f);
        }
    }


}
