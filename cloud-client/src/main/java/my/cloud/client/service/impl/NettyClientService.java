package my.cloud.client.service.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import my.cloud.client.service.NetworkService;
import my.cloud.client.service.impl.handler.CommandInboundHandler;


public class NettyClientService implements NetworkService {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private static NettyClientService instance;
    private static String authResult;

    private boolean fileTransactionFinished = false;

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NettyClientService();
        }
        return instance;
    }

    NettyClientService(){}

    private SocketChannel channel;

    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public void shutdown() {
        channel.close();
    }


    public void start() {
        Thread t = new Thread(() -> {
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new ObjectEncoder(),
                                        new ObjectDecoder(150 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                        new CommandInboundHandler()
                                );
                            }
                        });
                ChannelFuture future = b.connect(HOST, PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.start();
    }

    @Override
    public void setFileTransactionFinished(boolean isFinished) {
        fileTransactionFinished = isFinished;
    }

    @Override
    public boolean getFileTransactionFinished() {
        return fileTransactionFinished;
    }

    public String getAuthResult() {
        return authResult;
    }

    public void setAuthResult(String authResult) {
        NettyClientService.authResult = authResult;
    }
}
