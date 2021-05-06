package my.cloud.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    @FXML
    private ListView<String> localFilesList;
    @FXML
    private ListView<String> cloudFilesList;
    @FXML
    private Button uplButton;
    @FXML
    private Button downButton;
    @FXML
    private TextField localPath;
    @FXML
    private TextField cloudPath;

    private NetworkService networkService;

    private Stage stage;
    private Stage authStage;

    private AuthController authController;

    private final Map<String, File> localFiles = new HashMap<>();


    public void uploadFile() {
        if (!localFilesList.getItems().isEmpty() && !(localFilesList.getSelectionModel().getSelectedItem() == null)) {
            System.out.println(localFiles.get(localFilesList.getSelectionModel().getSelectedItem()));
            networkService.getChannel().writeAndFlush(Common.UPLOAD + " " + cloudPath.getText() + " " + localFiles.get(localFilesList.getSelectionModel().getSelectedItem()).length() + " " + localFilesList.getSelectionModel().getSelectedItem());
            networkService.setFileTransactionFinished(false);
            while (!networkService.getFileTransactionFinished()) {
                waiting();
            }
            networkService.getChannel().writeAndFlush("/end");
            waitingFinished();
            refreshFilesLists();
        }

    }

    public void downloadFile() {
        if (!cloudFilesList.getItems().isEmpty() && !(cloudFilesList.getSelectionModel().getSelectedItem() == null)) {
            System.out.println(Common.DOWNLOAD + " " + Common.LOCAL_DIR + File.separator + cloudFilesList.getSelectionModel().getSelectedItem());
            networkService.getChannel().writeAndFlush(Common.DOWNLOAD + " " + cloudPath.getText() + " " + Common.LOCAL_DIR + File.separator + cloudFilesList.getSelectionModel().getSelectedItem());
            networkService.setFileTransactionFinished(false);
            while (!networkService.getFileTransactionFinished()) {
                waiting();
            }
            waitingFinished();
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
        Platform.runLater(() -> {
            stage = (Stage) localPath.getScene().getWindow();
        });
        networkService = Factory.getNetworkService();
        networkService.start();
        Platform.runLater(() -> {
            createLoginWindow();
            authStage.show();
        });
        long mil = System.currentTimeMillis();
        while (System.currentTimeMillis() - mil < 1000) {
            waiting();
        }
        waitingFinished();
        System.out.println(networkService.getChannel());
        downButton.setDisable(true);
        uplButton.setDisable(true);
        cloudPath.setText(new File(Common.CLOUD_DIR.toString()).getName());
        localPath.setText(new File(Common.LOCAL_DIR.toString()).getName());
    }


    private void refreshFilesLists() {
        long mil = System.currentTimeMillis();
        while (System.currentTimeMillis() - mil < 300) {
            waiting();
        }
        waitingFinished();
        localFilesList.getItems().clear();
        cloudFilesList.getItems().clear();
        networkService.getChannel().writeAndFlush(Common.LS + " " + cloudPath.getText());
        for (File childFile : Objects.requireNonNull(new File(Common.LOCAL_DIR.toString()).listFiles())) {
            if (childFile.isFile()) {
                localFiles.put(childFile.getName(), childFile);
                localFilesList.getItems().add(childFile.getName());
            }
        }
        while (System.currentTimeMillis() - mil < 1500) {
            waiting();
        }
        waitingFinished();
        try (BufferedReader reader = new BufferedReader(new FileReader(Common.FILES_LIST.toString()))) {
            String resultCommand = reader.readLine();
            if (!(resultCommand == null)) {
                String[] listOfFiles = resultCommand.split(", ");
                cloudFilesList.getItems().clear();
                cloudFilesList.getItems().addAll(listOfFiles);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waiting() {
        if (stage != null) {
            stage.setOpacity(0.3f);
            localFilesList.setDisable(true);
            cloudFilesList.setDisable(true);
            cloudFilesList.setMouseTransparent(true);
            localFilesList.setMouseTransparent(true);
            downButton.setDisable(true);
            uplButton.setDisable(true);

        }
    }

    public void waitingFinished() {
        if (stage != null) {
            stage.setOpacity(1f);
            localFilesList.setDisable(false);
            cloudFilesList.setDisable(false);
            cloudFilesList.setMouseTransparent(false);
            localFilesList.setMouseTransparent(false);
        }
    }

    private void createLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/authView.fxml"));
            Parent root = fxmlLoader.load();
            authStage = new Stage();
            authStage.setTitle("Log in");
            authStage.setScene(new Scene(root, 300, 210));
            authController = fxmlLoader.getController();
            authController.setController(this);
            authStage.initModality(Modality.APPLICATION_MODAL);
            authStage.initStyle(StageStyle.UTILITY);
            stage.hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        networkService.shutdown();
    }

    public void tryToAuth(String login, String pass) {
        networkService.setAuthResult(null);
        networkService.getChannel().writeAndFlush(Common.AUTH + " " + login + " " + pass);
        while (networkService.getAuthResult() == null) {
            waiting();
        }
        String[] result = networkService.getAuthResult().split("\\s", 2);
        if (result[0].equals("Success")) {
            waitingFinished();
            stage.show();
            authStage.hide();
            cloudPath.setText(result[1]);
            refreshFilesLists();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Login failed..");

            alert.showAndWait();
        }
    }
}
