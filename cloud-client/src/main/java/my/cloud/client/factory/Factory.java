package my.cloud.client.factory;

import my.cloud.client.service.CommandDictionaryService;
import my.cloud.client.service.CommandService;
import my.cloud.client.service.NetworkService;
import my.cloud.client.service.impl.CommandDictionaryServiceImpl;
import my.cloud.client.service.impl.NettyClientService;
import my.cloud.client.service.impl.command.DownloadFileCommand;
import my.cloud.client.service.impl.command.UploadFileCommand;
import my.cloud.client.service.impl.command.ViewFilesInDirCommand;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static NetworkService getNetworkService() {
        return NettyClientService.getInstance();
    }

    public static CommandDictionaryService getCommandDirectoryService() {
        return new CommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new ViewFilesInDirCommand(), new DownloadFileCommand(getNetworkService()), new UploadFileCommand(getNetworkService()));
    }

}
