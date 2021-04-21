package my.cloud.client.controller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.NetworkService;
import my.cloud.client.service.impl.handler.CommandInboundHandler;

import java.io.File;
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
                ChannelFuture futurePreload = networkService.getChannel().writeAndFlush("upload "+localFilesList.getSelectionModel().getSelectedItem());
                try {
                    futurePreload.sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                networkService.getChannel().pipeline().remove(CommandInboundHandler.class);
                networkService.getChannel().pipeline().addLast(new ChunkedWriteHandler());
                ChannelFuture future = networkService.getChannel().writeAndFlush(new ChunkedFile(localFiles.get(localFilesList.getSelectionModel().getSelectedItem())));
                //stage.hideAll + show wait
                future.addListener((ChannelFutureListener) channelFuture -> System.out.println("Finish write"));
                // stage.showAll + close wait
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void downloadFile(ActionEvent actionEvent) {
        if(!cloudFilesList.getItems().isEmpty() && !(cloudFilesList.getSelectionModel().getSelectedItem() == null)) {
            try {
                networkService.getChannel().writeAndFlush(new ChunkedFile(new File(localDir+"//"+localFilesList.getSelectionModel().getSelectedItem())));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
//        createCommandResultHandler();
    }

    private void createCommandResultHandler() {

        new Thread(() -> {
            int x=0;
            while (x!=1) {
                ByteBuf bb = networkService.getChannel().alloc().buffer();
                int countReadBytes = bb.readableBytes();
                System.out.println(countReadBytes);
                byte[] buffer = new byte[bb.readableBytes()];

//                bb.readBytes(buffer);

//                String resultCommand = new String(buffer, 0, countReadBytes);
                System.out.println("BB: "+bb.toString(CharsetUtil.UTF_8));
                String resultCommand = bb.toString(CharsetUtil.UTF_8);

                String[] listOfFiles = resultCommand.split(", ");
                Platform.runLater(() -> cloudFilesList.getItems().clear());
                Platform.runLater(() -> cloudFilesList.getItems().addAll(listOfFiles));
                bb.release();
                x++;
            }}).start();
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
        networkService.getChannel().writeAndFlush("ls .");
        for (File childFile : localDir.listFiles()) {
            if (childFile.isFile()){
                localFiles.put(childFile.getName(), childFile);
                localFilesList.getItems().add(childFile.getName());
            }
        }
//        createCommandResultHandler();
    }


}
