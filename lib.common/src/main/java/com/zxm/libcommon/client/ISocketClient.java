package com.zxm.libcommon.client;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * Created by ZhangXinmin on 2018/9/15.
 * Copyright (c) 2018 . All rights reserved.
 */
public interface ISocketClient {
    /**
     * Configure the host and port to build socket connection.
     *
     * @param host host
     * @param port port
     * @return ISocketClient
     */
    ISocketClient onConfig(@NonNull String host,
                           @IntRange(from = 0, to = Integer.MAX_VALUE) int port);

    /**
     * Client build the connection with the server;
     *
     * @return ISocketClient
     */
    ISocketClient onConnect();

    /**
     * Client build the connection with the server;
     *
     * @return ISocketClient
     */
    ISocketClient onReconnect();

    /**
     * send command to server
     *
     * @param command
     * @return ISocketClient
     */
    ISocketClient onPostCommand(int command);

    /**
     * initiatively cut down the connection between the C/S
     *
     * @return ISocketClient
     */
    ISocketClient onClose();

    /**
     * Shut down the event loop to terminate all threads.
     * <p>
     * Notice:You must do this.
     * </p>
     */
    void onShutDown();

    class SimpleSocketClient implements ISocketClient {

        @Override
        public ISocketClient onConfig(@NonNull String host, int port) {
            return null;
        }

        @Override
        public ISocketClient onConnect() {
            return this;
        }

        @Override
        public ISocketClient onReconnect() {
            return this;
        }

        @Override
        public ISocketClient onPostCommand(int command) {
            return this;
        }

        @Override
        public ISocketClient onClose() {
            return this;
        }

        @Override
        public void onShutDown() {

        }
    }
}
