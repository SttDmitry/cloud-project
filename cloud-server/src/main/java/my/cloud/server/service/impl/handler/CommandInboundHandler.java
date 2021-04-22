package my.cloud.server.service.impl.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import my.cloud.server.Factory.Factory;
import my.cloud.server.service.CommandDictionaryService;

public class CommandInboundHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object command) {
        CommandDictionaryService dictionaryService = Factory.getCommandDirectoryService();
        System.out.println(command);
        String commandResult = dictionaryService.processCommand(command.toString(), ctx.channel());
        System.out.println(commandResult);
        ctx.writeAndFlush(commandResult);
    }
}
