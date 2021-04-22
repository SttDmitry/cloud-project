package my.cloud.client.service.impl.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.CommandDictionaryService;

public class CommandInboundHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object command) {
        CommandDictionaryService dictionaryService = Factory.getCommandDirectoryService();
        System.out.println(command);
        String commandResult = dictionaryService.processCommand(command.toString(), ctx.channel());
        System.out.println(commandResult);
    }
}
