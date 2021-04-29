package my.cloud.client.service.impl.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.*;

public class BigFilesWriteHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final File fileToWrite;
    private final long fileSpace;

    public BigFilesWriteHandler(File ftw, long fileSpace) {
        this.fileToWrite = ftw;
        this.fileSpace = fileSpace;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf bb) {
        System.out.println(fileToWrite+" "+ fileToWrite.length());

        ByteBuf byteBuf = bb.retain();

        outputFileWrite(byteBuf);

        if (fileSpace - fileToWrite.length() < 65536) {
            System.out.println("Finish download");
            ctx.pipeline().addLast(new ObjectEncoder());
            ctx.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
            ctx.pipeline().addLast(new CommandInboundHandler());
            ctx.pipeline().remove(ChunkedWriteHandler.class);
            ctx.pipeline().remove(BigFilesWriteHandler.class);
        }

        byteBuf.release();

    }

    private void outputFileWrite(ByteBuf byteBuf) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fileToWrite, true))) {
            while (byteBuf.isReadable()) {
                os.write(byteBuf.readByte());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
