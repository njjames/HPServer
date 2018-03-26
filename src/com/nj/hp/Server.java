package com.nj.hp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread{
	public static int PORT = 9898;
	public static ServerSocket serverSocket;
	public static boolean isRunning = false;
	public static ExecutorService es;
	public static int ONLINE_LIMIT=1000;
	
	@Override
	public void run() {
		System.out.println("服务器正在启动");
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("服务器已经启动");
			es = Executors.newCachedThreadPool();
			isRunning = true;
			while(isRunning) {
				//serverSocket只有一个所以设置为静态变量，Socket每个客户端的连接都不一样，不能设置为静态的
				Socket socket = serverSocket.accept();
				System.out.println("收到一个数据");
				//与客户端连接成功后，就开启一个线程，来处理服务器与客户端之间传递的数据，当然要把这个连接socket传递进去
				es.execute(new ServerClient(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("服务器结束");
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
}
