package com.zxm.libclient;

import android.support.annotation.NonNull;

import com.zxm.libcommon.client.ISocketClient;
import com.zxm.libcommon.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by ZhangXinmin on 2018/9/10.
 * Copyright (c) 2018 . All rights reserved.
 * 非阻塞式I/O编程：
 * 一请求一应答通信模型-->
 * 1.服务端接收客户端连接请求之后为每个客户端创建一个线程进行链路处理，处理完成后通过输出流返回应答给客户端，
 * 线程销毁；
 * 2.服务端线程个数和客户端并发数为1：1；
 */
public class SocketClient extends ISocketClient.SimpleSocketClient {

    private String host;
    private int port;

    private static class Holder {
        private static SocketClient INSTANCE = new SocketClient();
    }

    public static SocketClient getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public ISocketClient onConfig(@NonNull String host, int port) {
        this.port = port;
        this.host = host;
        return super.onConfig(host, port);
    }

    @Override
    public ISocketClient onConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Socket socket = null;
                BufferedReader in = null;
                PrintWriter out = null;
                try {

                    socket = new Socket(host, port);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("QUERY TIME ORDER");
                    Logger.d("Send order to server succeed!");
                    final String resp = in.readLine();
                    Logger.d("Now is : " + resp);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                            out = null;
                        }

                        if (in != null) {
                            in.close();
                            in = null;
                        }

                        if (socket != null) {
                            socket.close();
                            socket = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return super.onConnect();
    }

}
