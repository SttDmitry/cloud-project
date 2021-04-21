package my.cloud.server.service.impl.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BigFilesWriteHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private File fileToWrite;

    public BigFilesWriteHandler (File ftw){
        this.fileToWrite = ftw;
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
//        ctx.channel().pipeline().remove(ChunkedWriteHandler.class);
//        ctx.channel().pipeline().remove(BigFilesWriteHandler.class);
//        ctx.channel().pipeline().addLast(new CommandInboundHandler());
    }
}
