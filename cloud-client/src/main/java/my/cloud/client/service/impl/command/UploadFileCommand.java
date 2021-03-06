package my.cloud.client.service.impl.command;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.client.service.CommandService;
import my.cloud.client.service.NetworkService;
import my.cloud.client.service.impl.handler.CommandInboundHandler;
import my.cloud.common.Common;

import java.io.File;
import java.io.IOException;

public class UploadFileCommand implements CommandService {

    private final NetworkService impl;

    public UploadFileCommand(NetworkService impl) {
        this.impl = impl;
    }

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s");
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }
        channelSetForUploading(channel, actualCommandParts[1]);

        return actualCommandParts[1];
    }

    private void channelSetForUploading(Channel channel, String actualCommandParts) {
        try {
            channel.pipeline().remove(ObjectDecoder.class);
            channel.pipeline().remove(ObjectEncoder.class);
            channel.pipeline().remove(CommandInboundHandler.class);
            channel.pipeline().addLast(new ChunkedWriteHandler());
            ChannelFuture future = channel.writeAndFlush(new ChunkedFile(new File(Common.LOCAL_DIR + File.separator + actualCommandParts)));
            future.addListener((ChannelFutureListener) channelFuture -> {
                System.out.println("Finish upload");
                channel.pipeline().remove(ChunkedWriteHandler.class);
                channel.pipeline().addLast(new ObjectEncoder());
                channel.pipeline().addLast(new ObjectDecoder(150 * 1024 * 1024, ClassResolvers.cacheDisabled(null)));
                channel.pipeline().addLast(new CommandInboundHandler());
                impl.setFileTransactionFinished(true);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getCommand() {
        return Common.UPLOAD.toString();
    }
}
