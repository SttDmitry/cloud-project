package my.cloud.client.service.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import my.cloud.client.service.NetworkService;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NettyClientService implements NetworkService {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private SocketChannel channel;

//    ChannelFuture future = clientMain.channel.writeAndFlush(new ChunkedFile(new File("input-data/2021-04-12 19-05-19.mkv")));
//        future.addListener((ChannelFutureListener) channelFuture -> System.out.println("Finish write"));


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
                                        new ChunkedWriteHandler()
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
}
