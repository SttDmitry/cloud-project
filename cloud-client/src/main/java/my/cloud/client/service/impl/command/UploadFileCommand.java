package my.cloud.client.service.impl.command;

import io.netty.channel.Channel;
import my.cloud.client.service.CommandService;

import java.io.File;

public class UploadFileCommand implements CommandService {

    private File localDir = new File(".");

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s");
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        return localDir+"//"+actualCommandParts[1];
    }

    @Override
    public String getCommand() {
        return "upload";
    }
}
