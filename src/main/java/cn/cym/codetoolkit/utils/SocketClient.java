package cn.cym.codetoolkit.utils;

import cn.cym.codetoolkit.constant.ProjectConstants;
import cn.cym.codetoolkit.log.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class SocketClient {

    private String host;

    private int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void downloadFile(File downloadFile) {
        LogUtils.println("socket client download file from host " + host);
        Socket socket = null;
        BufferedInputStream socketIn = null;
        FileOutputStream fileOutputStream = null;
        int len = 0;
        try {
            socket = new Socket(host, port);
            socketIn = new BufferedInputStream(socket.getInputStream());
            fileOutputStream = new FileOutputStream(downloadFile);
            byte[] b = new byte[512];
            while ((len = socketIn.read(b)) != -1) {
                fileOutputStream.write(b, 0, len);
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            LogUtils.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.println(e.getMessage());
        } finally {
            LogUtils.println("download file end... ");
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (socketIn != null) {
                try {
                    socketIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.println(e.getMessage());
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.println(e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        new SocketClient("127.0.0.1", ProjectConstants.CONFIG_PORT).downloadFile(new File("E:\\develop\\workspace.zjkj\\jdp-ui\\.idea\\CODE_TOOLKIT\\generateInfo2.json"));
    }
}
