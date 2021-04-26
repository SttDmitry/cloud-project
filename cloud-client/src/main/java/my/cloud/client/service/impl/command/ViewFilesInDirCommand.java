package my.cloud.client.service.impl.command;

import io.netty.channel.Channel;
import my.cloud.client.service.CommandService;

import java.io.*;

public class ViewFilesInDirCommand implements CommandService {

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s", 2);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }
        File file = new File("./Files/filesList.txt");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            fileCreate(file);
        }

        listToFileWriter(actualCommandParts[1], file);

        return actualCommandParts[1];
    }

    private void fileCreate(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listToFileWriter(String actualCommandPart, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(actualCommandPart);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getCommand() {
        return "ls";
    }
}
