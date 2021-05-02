package my.cloud.client.service;

import io.netty.channel.socket.SocketChannel;

public interface NetworkService {

    void start();

    SocketChannel getChannel();

    void shutdown();

    void setFileTransactionFinished(boolean isFinished);

    boolean getFileTransactionFinished();
}
