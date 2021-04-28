package my.cloud.server.service.impl.command;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.common.Common;
import my.cloud.server.service.CommandService;
import my.cloud.server.service.impl.handler.CommandInboundHandler;

import java.io.File;
import java.io.IOException;

public class DownloadFileCommand implements CommandService {

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
            File newFile = new File(Common.CLOUD_DIR + File.separator + File.getName());
            channel.writeAndFlush(Common.DOWNLOAD + " " + newFile.length() + " " + File);
            channel.pipeline().remove(CommandInboundHandler.class);
            channel.pipeline().addLast(new ChunkedWriteHandler());
            ChannelFuture future = channel.writeAndFlush(new ChunkedFile(newFile));
            future.addListener((ChannelFutureListener) channelFuture -> {
                System.out.println("Finish download server");
                channel.pipeline().addLast(new CommandInboundHandler());
                channel.pipeline().remove(ChunkedWriteHandler.class);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return Common.DOWNLOAD.toString();
    }
}
