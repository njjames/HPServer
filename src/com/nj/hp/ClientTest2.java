package com.nj.hp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTest2 {
	private static Socket socket;
	public static void main(String[] args) {
		try {
			socket = new Socket("124.239.181.34", 9898);
			final User user = new User();
			user.setName("小明哥");
			user.setPassword("3353255");
			System.out.println(user.toString());
//			Thread.sleep(5000);
//			os = socket.getOutputStream();
//			os.write(("findgame:\r\n").getBytes("utf8"));
//			os.flush();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
						OutputStream os = socket.getOutputStream();
						os.write(("login:" + user.toString() + "\r\n").getBytes("utf8"));
//						Thread.sleep(5000);
						os = socket.getOutputStream();
						os.write(("findgame:111" + "\r\n").getBytes("utf8"));
//						Thread.sleep(3000);
//						System.out.println("发送的线程结束了,时间是：" + System.currentTimeMillis());
						//发送的线程结束了,时间是：1522116147471
						//出现异常了，时间是：1522116147481
					} catch (Exception e) {
						e.printStackTrace();
					}
					while(true) {
						
					}
				}
			}).start();
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(2000);
						InputStream is = socket.getInputStream();
						BufferedReader bd = new BufferedReader(new InputStreamReader(is));
						String data = null;
						while((data = bd.readLine()) != null) {
							System.out.println(data);
							if(data.contains("askPeace")) {
								OutputStream os = socket.getOutputStream();
								os.write(("agreepeace:\r\n").getBytes("utf8"));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
