package my.cloud.server.service.impl.command;

import io.netty.channel.Channel;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.server.service.CommandService;
import my.cloud.server.service.impl.handler.BigFilesWriteHandler;
import my.cloud.server.service.impl.handler.CommandInboundHandler;

import java.io.File;

public class UploadFileCommand implements CommandService {

    private File cloudDir = new File(System.getenv("LOCALAPPDATA")+"//CloudProject");

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 3;

        String[] actualCommandParts = command.split("\\s",3);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }
        channel.pipeline().remove(CommandInboundHandler.class);
        channel.pipeline().remove(ObjectDecoder.class);
        channel.pipeline().remove(ObjectEncoder.class);
        channel.pipeline().addLast(new ChunkedWriteHandler());
        channel.pipeline().addLast(new BigFilesWriteHandler(new File(cloudDir+"//"+actualCommandParts[2]),Long.parseLong(actualCommandParts[1])));

        return actualCommandParts[2];
    }

    @Override
    public String getCommand() {
        return "upload";
    }
}
