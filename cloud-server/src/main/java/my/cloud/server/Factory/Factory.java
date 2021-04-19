package my.cloud.server.Factory;

import my.cloud.server.service.CommandDictionaryService;
import my.cloud.server.service.CommandService;
import my.cloud.server.service.ServerService;
import my.cloud.server.service.impl.CommandDictionaryServiceImpl;
import my.cloud.server.service.impl.NettyServerService;
import my.cloud.server.service.impl.command.DownloadFileCommand;
import my.cloud.server.service.impl.command.UploadFileCommand;
import my.cloud.server.service.impl.command.ViewFilesInDirCommand;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static ServerService getServerService() {
        return new NettyServerService();
    }

    public static CommandDictionaryService getCommandDirectoryService() {
        return new CommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new ViewFilesInDirCommand(), new DownloadFileCommand(), new UploadFileCommand());
    }
}
