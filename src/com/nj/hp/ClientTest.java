package com.nj.hp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTest {
	public static void main(String[] args) {
		try {
			final Socket socket = new Socket("192.168.16.122", 9898);
			User user = new User();
			user.setName("小明");
			user.setPassword("1234");
			System.out.println(user.toString());
			OutputStream os = socket.getOutputStream();
			os.write(("sign:" + user.toString() + "\r\n").getBytes("utf8"));
			os.flush();
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(100);
						InputStream is = socket.getInputStream();
						BufferedReader bd = new BufferedReader(new InputStreamReader(is));
						String data = null;
						if((data = bd.readLine()) != null) {
							System.out.println(data);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
