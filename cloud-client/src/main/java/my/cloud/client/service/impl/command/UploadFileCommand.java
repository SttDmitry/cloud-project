package my.cloud.client.service.impl.command;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.client.service.CommandService;
import my.cloud.client.service.impl.handler.CommandInboundHandler;

import java.io.File;
import java.io.IOException;

public class UploadFileCommand implements CommandService {

    private File localDir = new File(".");

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s");
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        channel.pipeline().remove(CommandInboundHandler.class);
        channel.pipeline().addLast(new ChunkedWriteHandler());
        ChannelFuture future = null;
        try {
            future = channel.writeAndFlush(new ChunkedFile(new File(actualCommandParts[1])));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //stage.hideAll + show wait
        future.addListener((ChannelFutureListener) channelFuture -> {
            channel.pipeline().addLast(new CommandInboundHandler());
            channel.pipeline().remove(ChunkedWriteHandler.class);
        });

        return actualCommandParts[1];
    }

    @Override
    public String getCommand() {
        return "upload";
    }
}
