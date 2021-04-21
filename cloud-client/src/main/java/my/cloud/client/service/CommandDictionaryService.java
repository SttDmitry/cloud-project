package my.cloud.client.service;


import io.netty.channel.Channel;

public interface CommandDictionaryService {
    String processCommand(String command, Channel channel);
}
