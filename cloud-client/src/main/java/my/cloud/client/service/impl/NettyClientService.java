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

    protected boolean uploadFinish = false;
    protected boolean downloadFinish = false;

    public boolean isUploadFinish() {
        return uploadFinish;
    }

    public void setUploadFinish(boolean uploadFinish) {
        this.uploadFinish = uploadFinish;
    }

    public boolean isDownloadFinish() {
        return downloadFinish;
    }

    public void setDownloadFinish(boolean downloadFinish) {
        this.downloadFinish = downloadFinish;
    }

    private SocketChannel channel;

    public SocketChannel getChannel() {
        return channel;
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
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
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
}
