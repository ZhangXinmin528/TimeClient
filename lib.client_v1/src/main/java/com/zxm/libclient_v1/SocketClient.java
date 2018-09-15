package com.zxm.libclient_v1;

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
 * 非阻塞式I/O--NIO
 * 1.适用于高负载，高并发的网络应用，可以使用NIO的非阻塞模式进行开发；
 * 2.同时适用于低负载，低并发的情况，可以选择同步阻塞I/O降低开发复杂度；
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
