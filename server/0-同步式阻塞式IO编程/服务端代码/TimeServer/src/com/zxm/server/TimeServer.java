package com.zxm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 同步阻塞式I/O TimeServer
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
		
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("The time server is start in port:"+port);
			Socket socket = null;
			while(true) {
				socket = server.accept();
				//create a new thread
				new Thread(new TimeServerHandler(socket)).start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (server!=null) {
				System.out.println("The time server is going to close!");
				try {
					server.close();
					server = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
}
