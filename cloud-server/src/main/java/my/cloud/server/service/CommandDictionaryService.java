package my.cloud.server.service;


import io.netty.channel.Channel;

public interface CommandDictionaryService {
    String processCommand(String command, Channel channel);
}
