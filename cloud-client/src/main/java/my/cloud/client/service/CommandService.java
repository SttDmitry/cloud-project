package my.cloud.client.service;

import io.netty.channel.Channel;

public interface CommandService {
    String processCommand(String command, Channel channel);

    String getCommand();
}
