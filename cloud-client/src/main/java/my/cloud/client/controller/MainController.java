package my.cloud.client.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class MainController {

    public ListView<String> localFilesList;
    public ListView<String> cloudFilesList;

    public void create() {
        localFilesList.getItems().addAll("1","2","3");
        cloudFilesList.getItems().addAll("a","b","c");
    }

    public void uploadFile(ActionEvent actionEvent) {
        String temp = localFilesList.getSelectionModel().getSelectedItem();
        cloudFilesList.getItems().addAll(temp);
    }

    public void downloadFile(ActionEvent actionEvent) {
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
        cloudFilesList.getSelectionModel().select(-1);
    }

    public void cloudClick(MouseEvent mouseEvent) {
        localFilesList.getSelectionModel().select(-1);
    }

    public void createNewFolder(ActionEvent actionEvent) {
        //Создание новой папки на облаке
    }
}
