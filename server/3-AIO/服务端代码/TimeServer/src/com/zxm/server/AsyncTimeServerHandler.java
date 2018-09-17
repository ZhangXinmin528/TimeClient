package com.zxm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.Port;

public class AsyncTimeServerHandler implements Runnable {

	private int port;
	CountDownLatch latch;
	AsynchronousServerSocketChannel socketChannel;

	public AsyncTimeServerHandler(int port) {
		this.port = port;
		try {
			socketChannel = AsynchronousServerSocketChannel.open();
			socketChannel.bind(new InetSocketAddress(port));
			System.out.println("The Time server is start in port : " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		latch = new CountDownLatch(1);
		doAccept();
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doAccept() {
		socketChannel.accept(this,new AcceptCompletionHandler());
	}

}
