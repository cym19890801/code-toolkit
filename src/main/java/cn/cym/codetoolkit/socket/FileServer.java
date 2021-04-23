package cn.cym.codetoolkit.socket;

import cn.cym.codetoolkit.entity.IdeaProject;
import cn.cym.codetoolkit.log.LogUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

public class FileServer {

    private int port;

    private NioEventLoopGroup group = null;

    private boolean isStart = false;

    public FileServer(int port) {
        this.port = port;
    }

    public boolean isShutdown() {
        LogUtils.println("group.isShutdown():" + group.isShutdown());
        if (group.isShutdown())
            return true;

        return false;
    }

    public boolean isStart() {
        return isStart;
    }

    public void shutdown() {
        group.shutdownGracefully();
        isStart = false;
    }

    public void start(IdeaProject ideaProject) {
        try {
            isStart = true;
            group = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer() {
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ideaProject.zipBakFile();

                                    ByteBuf in = (ByteBuf) msg;
                                    String fileName = in.toString(CharsetUtil.UTF_8);
                                    System.out.println("Server received:" + fileName);

                                    File file = new File(ideaProject.findProjectConfigRootFile(), fileName);
                                    if (file.exists()) {
                                        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                                        ctx.writeAndFlush(new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length()));
                                    }
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("=== channelReadComplete ===");
                                    // 将未决消息冲刷到远程节点，并且关闭该Channel
                                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
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
        new FileServer(9999).start(null);
    }
}
