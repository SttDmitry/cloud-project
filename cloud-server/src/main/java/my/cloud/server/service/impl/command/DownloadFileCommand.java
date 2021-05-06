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
        final int requirementCountCommandParts = 3;

        String[] actualCommandParts = command.split("\\s", 3);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        channelSetForDownloading(channel, actualCommandParts);


        return actualCommandParts[2];
    }

    private void channelSetForDownloading(Channel channel, String[] actualCommandParts) {
        try {
            File File = new File(actualCommandParts[2]);
            File newFile = new File(Common.CLOUD_DIR + File.separator + actualCommandParts[1] + File.separator + File.getName());
            channel.writeAndFlush(Common.DOWNLOAD + " " + newFile.length() + " " + File);
            channel.pipeline().remove(CommandInboundHandler.class);
            channel.pipeline().addLast(new ChunkedWriteHandler());
            ChannelFuture future = channel.writeAndFlush(new ChunkedFile(newFile));
            future.addListener((ChannelFutureListener) channelFuture -> {
                System.out.println("Finish download server");
                future.channel().pipeline().remove(ChunkedWriteHandler.class);
                future.channel().pipeline().addLast(new CommandInboundHandler());
                future.channel().writeAndFlush("/end");
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
