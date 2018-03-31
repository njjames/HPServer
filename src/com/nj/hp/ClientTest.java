package com.nj.hp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTest {
	private static OutputStream os;

	public static void main(String[] args) {
		try {
			final Socket socket = new Socket("124.239.181.34", 9898);
			User user = new User();
			user.setName("小明");
			user.setPassword("1234");
			System.out.println(user.toString());
			os = socket.getOutputStream();
			os.write(("login:" + user.toString() + "\r\n").getBytes("utf8"));
//			Thread.sleep(3000);
			os.write(("findgame:\r\n").getBytes("utf8"));
			os.flush();
			Thread.sleep(10000);
			os.write(("askpeace:\r\n").getBytes("utf8"));
			os.flush();
			
			
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(100);
						InputStream is = socket.getInputStream();
						BufferedReader bd = new BufferedReader(new InputStreamReader(is));
						String data = null;
						while((data = bd.readLine()) != null) {
							System.out.println(data);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			while(true) {}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
