package my.cloud.server.service.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import my.cloud.server.service.ServerService;
import my.cloud.server.service.impl.handler.CommandInboundHandler;

public class NettyServerService implements ServerService {

    static final int PORT = 8189;
    private static NettyServerService instance;

    public static ServerService getInstance() {
        if (instance == null) {
            instance = new NettyServerService();
        }
        return instance;
    }

    NettyServerService(){}

    @Override
    public void startServer() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(150*1024*1024,ClassResolvers.cacheDisabled(null)),
                                    new CommandInboundHandler()
                            );

                        }
                    });

            ChannelFuture future = bootstrap.bind(PORT).sync();
            System.out.println("???????????? ??????????????");
            future.channel().closeFuture().sync(); // block
        } catch (Exception e) {
            System.out.println("???????????? ????????");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
