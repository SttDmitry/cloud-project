package my.cloud.client.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;

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
    private final File cloudDir = new File(System.getenv("LOCALAPPDATA") + "//CloudProject");
    private final File localDir = new File(".");

    public NetworkService networkService;

    private final Map<String, File> localFiles = new HashMap<>();


    public void uploadFile() {
        if (!localFilesList.getItems().isEmpty() && !(localFilesList.getSelectionModel().getSelectedItem() == null)) {
            System.out.println(localFiles.get(localFilesList.getSelectionModel().getSelectedItem()));
            networkService.getChannel().writeAndFlush("upload " + localFiles.get(localFilesList.getSelectionModel().getSelectedItem()).getTotalSpace() + " " + localFilesList.getSelectionModel().getSelectedItem());
            System.out.println("Finish write");
            refreshFilesLists();
            // stage.showAll + close wait
        }

    }

    public void downloadFile() {
        if (!cloudFilesList.getItems().isEmpty() && !(cloudFilesList.getSelectionModel().getSelectedItem() == null)) {
            System.out.println(localFiles.get(localFilesList.getSelectionModel().getSelectedItem()));
            networkService.getChannel().writeAndFlush("download " + localDir + "//" + cloudFilesList.getSelectionModel().getSelectedItem());
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
        cloudPath.setText(cloudDir.getName());
        localPath.setText(localDir.getName());
    }


    private void refreshFilesLists() {
        localFilesList.getItems().clear();
        cloudFilesList.getItems().clear();
        networkService.getChannel().writeAndFlush("ls");
        for (File childFile : Objects.requireNonNull(localDir.listFiles())) {
            if (childFile.isFile()) {
                localFiles.put(childFile.getName(), childFile);
                localFilesList.getItems().add(childFile.getName());
            }
        }
        //Wait for ..
        try (BufferedReader reader = new BufferedReader(new FileReader("./Files/filesList.txt"))) {
            String resultCommand = reader.readLine();
            String[] listOfFiles = resultCommand.split(", ");
            cloudFilesList.getItems().clear();
            cloudFilesList.getItems().addAll(listOfFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
