package my.cloud.server.service.impl.command;

import io.netty.channel.Channel;
import my.cloud.common.Common;
import my.cloud.server.service.CommandService;

import java.io.File;
import java.util.Objects;

public class ViewFilesInDirCommand implements CommandService {

    private static File cloudDir;

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 2;
        String[] actualCommandParts = command.split("\\s");
        cloudDir = new File(Common.CLOUD_DIR + File.separator + actualCommandParts[1]);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        return process();
    }

    private String process() {
        StringBuilder sb = new StringBuilder(" ");
        if (!cloudDir.exists()) {
            cloudDir.mkdirs();
        } else {
            for (File childFile : Objects.requireNonNull(cloudDir.listFiles())) {
                if (childFile.isFile()) {
                    sb.append(childFile.getName()).append(", ");
                }
            }
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }


        return Common.LS.toString() + sb;
    }


    @Override
    public String getCommand() {
        return Common.LS.toString();
    }
}
