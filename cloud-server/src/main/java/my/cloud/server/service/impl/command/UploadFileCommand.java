package my.cloud.server.service.impl.command;

import io.netty.channel.Channel;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import my.cloud.common.Common;
import my.cloud.server.service.CommandService;
import my.cloud.server.service.impl.handler.BigFilesWriteHandler;
import my.cloud.server.service.impl.handler.CommandInboundHandler;

import java.io.File;

public class UploadFileCommand implements CommandService {

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 4;

        String[] actualCommandParts = command.split("\\s", 4);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }
        File file = new File(Common.CLOUD_DIR + File.separator + actualCommandParts[1] + File.separator + actualCommandParts[3]);
        while (file.exists()) {
            file = new File(Common.CLOUD_DIR + File.separator + actualCommandParts[1] + File.separator + "copy" + file.getName());
        }
        channel.writeAndFlush(actualCommandParts[0] + " " + actualCommandParts[3]);
        channel.pipeline().remove(CommandInboundHandler.class);
        channel.pipeline().remove(ObjectDecoder.class);
        channel.pipeline().remove(ObjectEncoder.class);
        channel.pipeline().addLast(new BigFilesWriteHandler(file, Long.parseLong(actualCommandParts[2])));

        return "";
    }

    @Override
    public String getCommand() {
        return Common.UPLOAD.toString();
    }
}
