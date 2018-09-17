package com.zxm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * �첽������ʽI/O-->AIO TimeServer
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
				//����Ĭ��ֵ
			}
		}
		
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		
		new Thread(timeServer,"NIO-MultiplexerTimeServer-001").start();
		
	}
}
