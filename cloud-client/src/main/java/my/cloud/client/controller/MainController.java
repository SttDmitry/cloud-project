package my.cloud.client.controller;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;
import my.cloud.client.service.impl.handler.CommandInboundHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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

    private Map<String, File> localFiles = new HashMap<>();




    public void uploadFile(ActionEvent actionEvent) {
        if(!localFilesList.getItems().isEmpty() && !(localFilesList.getSelectionModel().getSelectedItem() == null)) {
            try {
                System.out.println(localFiles.get(localFilesList.getSelectionModel().getSelectedItem()));
                networkService.getChannel().writeAndFlush("upload "+localFiles.get(localFilesList.getSelectionModel().getSelectedItem()).getTotalSpace()+" "+localFilesList.getSelectionModel().getSelectedItem());
                networkService.getChannel().pipeline().remove(CommandInboundHandler.class);
                networkService.getChannel().pipeline().addLast(new ChunkedWriteHandler());
                ChannelFuture future = networkService.getChannel().writeAndFlush(new ChunkedFile(localFiles.get(localFilesList.getSelectionModel().getSelectedItem())));
                //stage.hideAll + show wait
                future.addListener((ChannelFutureListener) channelFuture -> {
                    networkService.getChannel().pipeline().addLast(new CommandInboundHandler());
                    networkService.getChannel().pipeline().remove(ChunkedWriteHandler.class);
                    System.out.println("Finish write");
                });

                    Thread.sleep(300);
                refreshFilesLists();
                // stage.showAll + close wait
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void downloadFile(ActionEvent actionEvent) {
        if(!cloudFilesList.getItems().isEmpty() && !(cloudFilesList.getSelectionModel().getSelectedItem() == null)) {

                System.out.println(localFiles.get(localFilesList.getSelectionModel().getSelectedItem()));
                networkService.getChannel().writeAndFlush("download "+ localDir+"//"+cloudFilesList.getSelectionModel().getSelectedItem());
                //stage.hideAll + show wait
                // stage.showAll + close wait
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            refreshFilesLists();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();
        networkService.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(networkService.getChannel());
        downButton.setDisable(true);
        uplButton.setDisable(true);
        refreshFilesLists();
        cloudPath.setText(cloudDir.getName());
        localPath.setText(localDir.getName());
    }



    private void refreshFilesLists(){
        localFilesList.getItems().clear();
        cloudFilesList.getItems().clear();
        networkService.getChannel().writeAndFlush("ls .");
        for (File childFile : localDir.listFiles()) {
            if (childFile.isFile()){
                localFiles.put(childFile.getName(), childFile);
                localFilesList.getItems().add(childFile.getName());
            }
        }
        try {
            Thread.sleep(500);}
        catch (InterruptedException e){
            e.printStackTrace();
        }
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
