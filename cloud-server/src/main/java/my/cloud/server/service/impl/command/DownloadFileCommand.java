package my.cloud.server.service.impl.command;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.server.service.CommandService;
import my.cloud.server.service.impl.handler.CommandInboundHandler;

import java.io.File;
import java.io.IOException;

public class DownloadFileCommand implements CommandService {

    private final File cloudDir = new File(System.getenv("LOCALAPPDATA") + "//CloudProject");

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s", 2);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        channelSetForDownloading(channel, actualCommandParts[1]);


        return actualCommandParts[1];
    }

    private void channelSetForDownloading(Channel channel, String actualCommandPart) {
        try {
            File File = new File(actualCommandPart);
            File newFile = new File(cloudDir + "//" + File.getName());
            channel.writeAndFlush("download " + newFile.getTotalSpace() + " " + File);
            channel.pipeline().remove(CommandInboundHandler.class);
            channel.pipeline().addLast(new ChunkedWriteHandler());
            ChannelFuture future = channel.writeAndFlush(new ChunkedFile(newFile));
            future.addListener((ChannelFutureListener) channelFuture -> {
                channel.pipeline().addLast(new CommandInboundHandler());
                channel.pipeline().remove(ChunkedWriteHandler.class);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return "download";
    }
}
