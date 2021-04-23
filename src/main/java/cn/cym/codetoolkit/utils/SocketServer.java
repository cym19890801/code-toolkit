package cn.cym.codetoolkit.utils;

import cn.cym.codetoolkit.constant.ProjectConstants;
import cn.cym.codetoolkit.log.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SocketServer {

    private boolean shutdown = false;

    private int port;

    public SocketServer(int port) {
        this.port = port;
    }

    public void shutdown() {
        shutdown = true;
    }

    public void start(File file) {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        BufferedInputStream bis = null;
        int len = 0;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);// 非阻塞

            bis = new BufferedInputStream(new FileInputStream(file));
            while (true) {
                socketChannel = serverSocketChannel.accept();

                if (socketChannel != null) {
                    byte[] b = new byte[512];
                    while ((len = bis.read(b)) != -1) {
                        socketChannel.write(ByteBuffer.wrap(b));
                    }
                    socketChannel.shutdownOutput();
                    break ;
//                    LogUtils.println("write end!" );
                }
//                if (shutdown) {
//                    LogUtils.println("shutdown：" + shutdown);
//                    socketChannel.shutdownOutput();
//                    break ;
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.println(e.getMessage());
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.println(e.getMessage());
                }
            }

            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.println(e.getMessage());
                }
            }

            if (serverSocketChannel != null) {
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        new SocketServer(ProjectConstants.CONFIG_PORT).start(new File("E:\\develop\\workspace.zjkj\\jdp-ui\\.idea\\CODE_TOOLKIT\\generateInfo.json"));
//        start(new File("C:\\Users\\chenyouming\\Desktop\\server\\template.zip"), ProjectConstants.DEFAULT_PORT);
//        System.out.println("do...");
    }
}
