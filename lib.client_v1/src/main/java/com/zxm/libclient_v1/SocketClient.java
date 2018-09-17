package com.zxm.libclient_v1;

import android.support.annotation.NonNull;

import com.zxm.libcommon.client.ISocketClient;
import com.zxm.libcommon.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

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

    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

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
                        doWrite(socketChannel);
                    } else {
                        socketChannel.register(selector, SelectionKey.OP_CONNECT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.e("Socket build connection failed : " + e.getMessage());
                    System.exit(1);
                }

                while (!stop) {
                    try {
                        selector.select(1000);
                        Set<SelectionKey> selectedKeys = selector.selectedKeys();
                        Iterator<SelectionKey> it = selectedKeys.iterator();
                        SelectionKey key = null;
                        while (it.hasNext()) {
                            key = it.next();
                            it.remove();
                            try {
                                handleInput(key);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Logger.e("Socket init error ：" + e.getMessage());
                                if (key != null) {
                                    key.cancel();
                                    if (key.channel() != null) {
                                        key.channel().close();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }).start();
        return super.onConnect();
    }

    private void handleInput(SelectionKey key) throws IOException {

        if (key.isValid()) {
            //判断是否判断成功
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    sc.register(selector, SelectionKey.OP_READ);
                    doWrite(sc);
                } else {
                    //连接失败，进程退出
                    System.exit(1);
                    Logger.e("Socket build connection failed");
                }
                Logger.d("Socket isReadable :" + key.isReadable());
                if (key.isReadable()) {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    int readBytes = sc.read(readBuffer);
                    if (readBytes > 0) {
                        readBuffer.flip();
                        byte[] bytes = new byte[readBuffer.remaining()];
                        readBuffer.get(bytes);
                        String body = new String(bytes, "UTF-8");
                        Logger.d("Now is :" + body);
                        this.stop = true;
                    } else if (readBytes < 0) {
                        //对端链路关闭
                        key.cancel();
                        sc.close();
                    } else {
                        //读到0字节
                    }
                }
            }

        }
    }

    private void doWrite(SocketChannel channel) throws IOException {

        byte[] bytes = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        channel.write(writeBuffer);
        if (!writeBuffer.hasRemaining()) {
            Logger.d("Socket send message to server");
        }
    }

    @Override
    public ISocketClient onClose() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (selector != null) {
                    try {
                        selector.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Logger.e("Socket close error ：" + e.getMessage());
                    }
                }
            }
        }).start();
        return super.onClose();
    }

}
