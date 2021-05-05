package my.cloud.client.service.impl.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import my.cloud.client.service.NetworkService;

import java.io.*;

public class BigFilesWriteHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final File fileToWrite;
    private final long fileSpace;
    private final NetworkService impl;
    private static boolean end = false;


    public BigFilesWriteHandler(File ftw, long fileSpace, NetworkService impl) {
        this.fileToWrite = ftw;
        this.fileSpace = fileSpace;
        this.impl = impl;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf bb) {
        System.out.println(fileToWrite+" "+ fileToWrite.length());

        ByteBuf byteBuf = bb.retain();

        String str = (String) byteBuf.toString(CharsetUtil.UTF_8);

        if(str.substring(str.length()-4).equals("/end")) {
            System.out.println("Finish download");
            ctx.pipeline().remove(BigFilesWriteHandler.class);
            ctx.pipeline().addLast(new ObjectEncoder());
            ctx.pipeline().addLast(new ObjectDecoder(150*1024*1024,ClassResolvers.cacheDisabled(null)));
            ctx.pipeline().addLast(new CommandInboundHandler());
            end = true;
        }
        checkWriteEnd(ctx);
        if (!end){outputFileWrite(ctx,byteBuf);}

        byteBuf.release();

    }

    private void outputFileWrite(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fileToWrite, true))) {
            while (byteBuf.isReadable()) {
                os.write(byteBuf.readByte());
            }
            System.out.println(fileToWrite+" "+ fileToWrite.length());
            checkWriteEnd(ctx);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void checkWriteEnd(ChannelHandlerContext ctx) {
//        if (!end && (Math.abs(fileSpace - fileToWrite.length()) <= fileSpace / 400 || fileSpace < 1024 && fileSpace+fileToWrite.length()/3 > Math.abs(fileSpace - fileToWrite.length()))) {
          if (!end && fileSpace == fileToWrite.length()) {
            System.out.println("Finish download");
            ctx.pipeline().addLast(new ObjectEncoder());
            ctx.pipeline().addLast(new ObjectDecoder(150*1024*1024,ClassResolvers.cacheDisabled(null)));
            ctx.pipeline().addLast(new CommandInboundHandler());
            ctx.pipeline().remove(BigFilesWriteHandler.class);
            impl.setFileTransactionFinished(true);
            end = true;
        }
    }
}
