package com.nj.hp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

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
	//正在匹配的用户
	private static ArrayList<ServerClient> findGameUsers = new ArrayList<>();
	
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
				case "findgame":  //进行匹配
					findGame();
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 进行匹配
	 * 
	 */
	private void findGame() {
		System.out.println("正在匹配");
		ServerClient other = null;
		boolean noFound = true;
		synchronized (findGameUsers) {
			Iterator<ServerClient> iterator = findGameUsers.iterator();
			while(iterator.hasNext()) {
				other = iterator.next();
				//如果是自己，就删除
				if(other.equals(this)) {
					iterator.remove();
					continue;
				}
				//如果不是自己，并且还有其他人，就匹配
				noFound = false;
				iterator.remove();
			}
			//如果集合中循环一遍都没有可以匹配的，就把自己加入到匹配的集合中
			if(noFound) {
				if(!findGameUsers.contains(this)) {
					findGameUsers.add(this);
				}
			}
		}
		if(!noFound) {
			try {
				sendLine("当前用户【" + user.getName() +"】匹配到了用户:【" + other.user.getName() + "】");
				other.sendLine("当前用户【" + other.user.getName() +"】匹配到了用户:【" + user.getName() + "】");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 登录
	 */
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
	/*
	 * 注册
	 */
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
			//判断这个用户当前是否在线
			int pos = onLineUsers.indexOf(this);
			if(pos != -1) {
				System.out.println("重复登陆");
				ServerClient client = onLineUsers.get(pos);
				//提示异地登录
				try {
					sendLine("failed:异地登录");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//把之前的用户下线
				client.deLine();
			}
			onLineUsers.add(this);
		}
		System.out.println("用户:【" + user.getName() + "】进入了，当前在线人数：" + onLineUsers.size());
	}
	
	/*
	 * 下线
	 */
	private void deLine() {
		//在线集合中移除
		synchronized (onLineUsers) {
			onLineUsers.remove(this);
		}
		//关闭socket
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//把当前用户设置为null
		if(user != null) {
			System.out.println("用户:【" + user.getName() + "】退出了，当前在线人数：" + onLineUsers.size());
		}
		user = null;
	}
	
	/*
	 * 需要重写equals方法，否则集合中的元素都是不相等的，就会出现同一用户重复登录
	 */
	@Override
	public boolean equals(Object obj) {
		return user.equals(((ServerClient)obj).user);
	}
}
