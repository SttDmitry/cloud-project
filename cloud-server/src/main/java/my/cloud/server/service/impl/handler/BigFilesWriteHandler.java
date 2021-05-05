package my.cloud.server.service.impl.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BigFilesWriteHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final File fileToWrite;
    private final long fileSpace;
    private static boolean end = false;

    public BigFilesWriteHandler(File ftw, long l) {
        this.fileToWrite = ftw;
        this.fileSpace = l;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf bb) {
        System.out.println(fileToWrite);

        ByteBuf byteBuf = bb.retain();

        String str = (String) byteBuf.toString(CharsetUtil.UTF_8);

        if(str.substring(str.length()-4).equals("/end")) {
            System.out.println("Finish upload server");
            ctx.pipeline().remove(BigFilesWriteHandler.class);
            ctx.pipeline().addLast(new ObjectEncoder());
            ctx.pipeline().addLast(new ObjectDecoder(150*1024*1024,ClassResolvers.cacheDisabled(null)));
            ctx.pipeline().addLast(new CommandInboundHandler());
            end = true;
        }

        if (!end) {outputFileWriter(ctx, byteBuf);}

        byteBuf.release();

    }

    private void outputFileWriter(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fileToWrite, true))) {
            while (byteBuf.isReadable()) {
                os.write(byteBuf.readByte());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkWriteEnd(ChannelHandlerContext ctx) {
        if (!end && (Math.abs(fileSpace - fileToWrite.length()) <= fileSpace / 200 || fileSpace < 1024 && fileSpace+fileToWrite.length()/3 > Math.abs(fileSpace - fileToWrite.length()))) {
            System.out.println("Finish upload server");
            ctx.pipeline().remove(BigFilesWriteHandler.class);
            ctx.pipeline().addLast(new ObjectEncoder());
            ctx.pipeline().addLast(new ObjectDecoder(150*1024*1024,ClassResolvers.cacheDisabled(null)));
            ctx.pipeline().addLast(new CommandInboundHandler());
            end = true;
        }
    }
}
