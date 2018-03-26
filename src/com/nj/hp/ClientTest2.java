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
			socket = new Socket("192.168.16.122", 9898);
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
						Thread.sleep(5000);
						OutputStream os2 = socket.getOutputStream();
						os2.write(("findgame:111" + "\r\n").getBytes("utf8"));
					} catch (Exception e) {
						e.printStackTrace();
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
						if((data = bd.readLine()) != null) {
							System.out.println(data);
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
