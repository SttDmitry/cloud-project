package my.cloud.server.service.impl.command;

import io.netty.channel.Channel;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.server.service.CommandService;
import my.cloud.server.service.impl.handler.FileServerHandler;

import java.io.File;

public class DownloadFileCommand implements CommandService {

    private File cloudDir = new File(System.getenv("LOCALAPPDATA")+"//CloudProject");

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s");
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }
        channel.pipeline().addLast(new ChunkedWriteHandler());

        return actualCommandParts[1];
    }

    @Override
    public String getCommand() {
        return "download";
    }
}
