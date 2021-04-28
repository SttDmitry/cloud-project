package my.cloud.server.service.impl.command;

import io.netty.channel.Channel;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.common.Common;
import my.cloud.server.service.CommandService;
import my.cloud.server.service.impl.handler.BigFilesWriteHandler;
import my.cloud.server.service.impl.handler.CommandInboundHandler;

import java.io.File;

public class UploadFileCommand implements CommandService {

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 3;

        String[] actualCommandParts = command.split("\\s", 3);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }
        File file = new File (Common.CLOUD_DIR + File.separator + actualCommandParts[2]);
        if (file.exists()) {
            file.renameTo(new File(Common.CLOUD_DIR + File.separator + "copy" + actualCommandParts[2]));
        }
        channel.pipeline().remove(CommandInboundHandler.class);
        channel.pipeline().remove(ObjectDecoder.class);
        channel.pipeline().remove(ObjectEncoder.class);
        channel.pipeline().addLast(new ChunkedWriteHandler());
        channel.pipeline().addLast(new BigFilesWriteHandler(file, Long.parseLong(actualCommandParts[1])));

        return actualCommandParts[0] + " " + actualCommandParts[1];
    }

    @Override
    public String getCommand() {
        return Common.UPLOAD.toString();
    }
}
