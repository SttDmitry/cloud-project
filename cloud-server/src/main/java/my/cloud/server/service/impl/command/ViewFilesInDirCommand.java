package my.cloud.server.service.impl.command;

import my.cloud.server.service.CommandService;

import java.io.File;

public class ViewFilesInDirCommand implements CommandService {

    private File cloudDir = new File(System.getenv("LOCALAPPDATA")+"//CloudProject");

    @Override
    public String processCommand(String command) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s");
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        return process(actualCommandParts[1]);
    }

    private String process(String dirPath) {
        StringBuilder sb = new StringBuilder();
        if (!cloudDir.exists()) {
            cloudDir.mkdirs();
        } else {
            for (File childFile : cloudDir.listFiles()) {
                if (childFile.isFile()){
                    sb.append(childFile.getName()).append(", ");
                }
            }
        }
        sb.setLength(sb.length()-2);

        return sb.toString();
    }


    @Override
    public String getCommand() {
        return "ls";
    }
}
