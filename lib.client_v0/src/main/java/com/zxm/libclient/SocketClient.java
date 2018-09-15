package com.zxm.libclient;

import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.zxm.libcommon.client.ISocketClient;
import com.zxm.libcommon.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by ZhangXinmin on 2018/9/10.
 * Copyright (c) 2018 . All rights reserved.
 * 传统的同步阻塞式I/O编程：
 * 一请求一应答通信模型-->
 * 1.服务端接收客户端连接请求之后为每个客户端创建一个线程进行链路处理，处理完成后通过输出流返回应答给客户端，
 * 线程销毁；
 * 2.服务端线程个数和客户端并发数为1：1；
 */
public class SocketClient extends ISocketClient.SimpleSocketClient {

    private String host;
    private int port;

    private Selector selector;
    private SocketChannel socketChannel;

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
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

        } catch (IOException e) {
            Logger.e("Socket init error ：" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return super.onConfig(host, port);
    }

    @Override
    public ISocketClient onConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //如果直接连接成功，则注册到多路复用器上，发送请求信息，读应答
                try {
                    if (socketChannel.connect(new InetSocketAddress(host, port))) {
                        socketChannel.register(selector, SelectionKey.OP_READ);
//                        doWrite(socketChannel);
                    } else {
                        socketChannel.register(selector, SelectionKey.OP_CONNECT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.e("Socket build connection failed : " + e.getMessage());
                    System.exit(1);
                }
            }
        }).start();
        return super.onConnect();
    }

    @Override
    public ISocketClient onClose() {
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onClose();
    }
}
