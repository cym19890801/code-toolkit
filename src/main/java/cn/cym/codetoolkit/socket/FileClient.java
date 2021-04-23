package cn.cym.codetoolkit.socket;

import cn.cym.codetoolkit.log.LogUtils;
import cn.cym.codetoolkit.utils.IoUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.net.InetSocketAddress;

public class FileClient {

    private String ip;

    private int port;

    public FileClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void downloadFile(String downloadFileName, File downloadFile) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(ip, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) {
                                    ctx.writeAndFlush(Unpooled.copiedBuffer(downloadFileName, CharsetUtil.UTF_8));
                                }

                                @Override
                                public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
//                                    System.out.println("client received:" + msg.toString(CharsetUtil.UTF_8));
                                    LogUtils.println("channelRead0 msg...");
                                    if (downloadFile.exists())
                                        downloadFile.delete();
                                    else {
                                        try {
                                            downloadFile.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream(downloadFile);
                                        byte[] b = new byte[msg.capacity()];
                                        for (int i = 0; i < msg.capacity(); i++) {
                                            b[i] = msg.getByte(i);
                                        }
                                        out.write(b, 0, b.length);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (out != null) {
                                            try {
                                                out.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    })
            ;
            ChannelFuture future = b.connect().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        new FileClient("127.0.0.1", 9999).downloadFile("generateInfo.json");
    }

}
