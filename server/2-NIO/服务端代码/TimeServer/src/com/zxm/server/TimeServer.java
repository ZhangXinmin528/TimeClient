package com.zxm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 异步非阻塞式I/O-->AIO TimeServer
 * @author wistron_zxm
 *
 */
public class TimeServer {

	public static void main(String[] args) {
		
		int port = 8080;
		if (args!=null && args.length >0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				// TODO: handle exception
				//采用默认值
			}
		}
		
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		
		new Thread(timeServer,"NIO-MultiplexerTimeServer-001").start();
		
	}
}
