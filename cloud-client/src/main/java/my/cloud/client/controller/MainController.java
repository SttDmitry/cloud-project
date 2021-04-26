package my.cloud.client.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

    private final Map<String, File> localFiles = new HashMap<>();


    public void uploadFile() {
        if (!localFilesList.getItems().isEmpty() && !(localFilesList.getSelectionModel().getSelectedItem() == null)) {
            System.out.println(localFiles.get(localFilesList.getSelectionModel().getSelectedItem()));
            networkService.getChannel().writeAndFlush(Common.UPLOAD.toString() + localFiles.get(localFilesList.getSelectionModel().getSelectedItem()).getTotalSpace() + " " + localFilesList.getSelectionModel().getSelectedItem());
            System.out.println("Finish write");
            refreshFilesLists();
            // stage.showAll + close wait
        }

    }

    public void downloadFile() {
        if (!cloudFilesList.getItems().isEmpty() && !(cloudFilesList.getSelectionModel().getSelectedItem() == null)) {
            System.out.println(localFiles.get(localFilesList.getSelectionModel().getSelectedItem()));
            networkService.getChannel().writeAndFlush(Common.DOWNLOAD.toString() + Common.LOCAL_DIR + File.separator + cloudFilesList.getSelectionModel().getSelectedItem());
            //stage.hideAll + show wait
            // stage.showAll + close wait
            refreshFilesLists();

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
        networkService = Factory.getNetworkService();
        networkService.start();

        // Wait for load of resources
        System.out.println(networkService.getChannel());
        downButton.setDisable(true);
        uplButton.setDisable(true);
        refreshFilesLists();
        cloudPath.setText(new File(Common.CLOUD_DIR.toString()).getName());
        localPath.setText(new File(Common.LOCAL_DIR.toString()).getName());
    }


    private void refreshFilesLists() {
        localFilesList.getItems().clear();
        cloudFilesList.getItems().clear();
        networkService.getChannel().writeAndFlush(Common.LS.toString());
        for (File childFile : Objects.requireNonNull(new File(Common.LOCAL_DIR.toString()).listFiles())) {
            if (childFile.isFile()) {
                localFiles.put(childFile.getName(), childFile);
                localFilesList.getItems().add(childFile.getName());
            }
        }
        //Wait for ..
        try (BufferedReader reader = new BufferedReader(new FileReader(Common.FILES_LIST.toString()))) {
            String resultCommand = reader.readLine();
            String[] listOfFiles = resultCommand.split(", ");
            cloudFilesList.getItems().clear();
            cloudFilesList.getItems().addAll(listOfFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
