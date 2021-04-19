package my.cloud.server.service.impl.command;

import my.cloud.server.service.CommandService;

import java.io.File;

public class DownloadFileCommand implements CommandService {

    private File cloudDir = new File(System.getenv("LOCALAPPDATA")+"//CloudProject");

    @Override
    public String processCommand(String command) {
        return null;
    }

    @Override
    public String getCommand() {
        return null;
    }
}
