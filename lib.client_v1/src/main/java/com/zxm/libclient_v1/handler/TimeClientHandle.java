package com.zxm.libclient_v1.handler;

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
 * Created by ZhangXinmin on 2018/9/17.
 * Copyright (c) 2018 . All rights reserved.
 * 存在bug,无法接收到消息
 */
public class TimeClientHandle implements Runnable {

    private String host;
    private int port;

    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandle(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            Logger.e("Socket init error ：" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        doConnect();

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

    private void doConnect() {
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
}
