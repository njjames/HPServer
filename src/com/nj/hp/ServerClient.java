package com.nj.hp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 服务器端处理客户端发来数据，以及给客户端发送数据的类
 * @ClassName: ServerClient 
 * @Description:  
 * @author niejun 
 * @date 2018年3月26日 下午1:51:49 
 *
 */
public class ServerClient implements Runnable {
	//在线的用户
	private static ArrayList<ServerClient> onLineUsers = new ArrayList<>();
	private Socket socket;
	private User user;

	public ServerClient(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			if(onLineUsers.size() > Server.ONLINE_LIMIT) {
				sendLine("failed:在线人数超出服务器限制。");
				try{
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//如果没有超出人数限制，则进行数据的操作，获取到数据的输入流
			InputStream is = socket.getInputStream();
			BufferedReader bd = new BufferedReader(new InputStreamReader(is));
			String data = null;
			if((data = bd.readLine()) != null) {
				System.out.println(data);
				//如果客户端发送归来的数据不是null，就按照：分割为数组，第一位是命令，第二位是内容
				String[] tag = data.split(":");
				String cmd = tag[0];
				String content = null;
				if(tag.length == 2) {
					content = tag[1];
				}
				//根据不同的命令，来进行不同的操作
				switch (cmd) {
				case "sign":   //注册
					sign(content);
					break;
				case "login":  //登录
					login(content);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void login(String content) {
		UserDao userDao = new UserDao();
		try {
			user = User.fromString(content);
			userDao.login(user);
			enLine();
			sendLine("loginback:" + user.toString());
		} catch (Exception e) {
			try {
				sendLine("loginback:failed");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void sign(String content) {
		UserDao userDao = new UserDao();
		try {
			//根据客户端发过来的用户信息，生成user对象
			user = User.fromString(content);
			//插入到数据库中
			userDao.sign(user);
			//把用户设置为上线
			enLine();
			//给客户端返回数据，包括指令和内容，内容是生成的user的信息
			sendLine("signback:" + user.toString());
		} catch (Exception e) {
			//出现异常表示注册失败，返回响应的失败数据
			try {
				sendLine("signback:failed");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * 给客户端发动数据
	 */
	private void sendLine(String data) throws Exception {
		try{
			OutputStream os = socket.getOutputStream();
			os.write((data + "\r\n").getBytes("utf8"));
		} catch (Exception e){
			//出现异常，说明在给客户端发送数据的时候，客户端断开了连接，次数要把这个用户下线
			deLine();
			throw new Exception("用户掉线了");
		}
	}
	
	/*
	 * 上线
	 */
	private void enLine() {
		synchronized (onLineUsers) {
			onLineUsers.add(this);
		}
		System.out.println("用户:【" + user.getName() + "】进入了，当前在线人数：" + onLineUsers.size());
	}
	
	private void deLine() {
		synchronized (onLineUsers) {
			onLineUsers.remove(this);
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(user != null) {
			System.out.println("用户：【" + user.getName() + "】退出了，当前在线人数：" + onLineUsers.size());
		}
		user = null;
	}

}
