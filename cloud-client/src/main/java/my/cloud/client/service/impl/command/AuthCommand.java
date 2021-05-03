package my.cloud.client.service.impl.command;

import io.netty.channel.Channel;
import my.cloud.client.service.CommandService;
import my.cloud.client.service.NetworkService;
import my.cloud.common.Common;

public class AuthCommand implements CommandService {
    private NetworkService impl;

    public AuthCommand(NetworkService impl) {
        this.impl = impl;
    }
    @Override
    public String processCommand(String command, Channel channel) {
        String[] split = command.split("\\s", 3);
        if (split[1].equals("Success")) {
            impl.setAuthResult(split[1]+" "+split[2]);
            System.out.println("Login success");
            return "";
        } else {
            impl.setAuthResult(split[1]);
            System.out.println("Login fail");
            return "";
        }
    }

    @Override
    public String getCommand() {
        return Common.AUTH.toString();
    }
}
