package my.cloud.client.service.impl.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BigFilesWriteHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private File fileToWrite;
    private long fileSpace;

    public BigFilesWriteHandler(File ftw, long fileSpace) {
        this.fileToWrite = ftw;
        this.fileSpace = fileSpace;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf bb) throws Exception {
        System.out.println(fileToWrite);

        ByteBuf byteBuf = bb.retain();

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fileToWrite, true))) {
            while (byteBuf.isReadable()) {
                os.write(byteBuf.readByte());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        byteBuf.release();

        if (fileToWrite.getTotalSpace() == fileSpace) {
            ctx.pipeline().addLast(new ObjectEncoder());
            ctx.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
            ctx.pipeline().addLast(new CommandInboundHandler());
            ctx.pipeline().remove(ChunkedWriteHandler.class);
            ctx.pipeline().remove(BigFilesWriteHandler.class);
        }

    }
}
