package my.cloud.client.service.impl.command;

import io.netty.channel.Channel;
import my.cloud.client.service.CommandService;

import java.io.File;

public class ViewFilesInDirCommand implements CommandService {

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s", 2);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        return actualCommandParts[1];
    }

//    private String process(String dirPath) {
//        StringBuilder sb = new StringBuilder();
//        if (!cloudDir.exists()) {
//            cloudDir.mkdirs();
//        } else {
//            for (File childFile : cloudDir.listFiles()) {
//                if (childFile.isFile()){
//                    sb.append(childFile.getName()).append(", ");
//                }
//            }
//        }
//        sb.setLength(sb.length()-2);
//
//        return "ls "+sb.toString();
//    }


    @Override
    public String getCommand() {
        return "ls";
    }
}
