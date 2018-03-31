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
	public User user;
	public Game game;
	private boolean isOK;

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
//			if((data = bd.readLine()) != null) {这就是为什么只能获取到一条数据，因为是if,fuck
			while((data = bd.readLine()) != null) {
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
				case "askpeace":  //请求和棋
					askPeace();
					break;
				case "agreepeace": //同意和棋
					agreePeace();
					break;
				case "giveup":  //投降
					giveUp();
					break;
				case "canclefind":
					cancleFind();
					break;
				case "walk": //走棋
					walk(content);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("出现异常了，时间是：" + System.currentTimeMillis());
			e.printStackTrace();
		}
	}

	/*
	 * 走棋
	 */
	private void walk(String content) {
		if(game == null) {
			return;
		}
		//发过来的内容是包含走棋之前和之后位置的字符串，转化为Walk
		Walk walk = Walk.fromString(content);
		if(game.walk(this, walk)) {
			try {
				sendLine("game:" + game.toString());
				game.getOther(this).sendLine("game:" + game.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			int whoWin = game.whoWin();
			if(whoWin == 0) {
				gameOver(0, "双方都没有牌了，和棋");
			}else if(whoWin == 1) {
				gameOver(whoWin, "玩家1胜利");
			}else if(whoWin == 2) {
				gameOver(whoWin, "玩家2胜利");
			}
		}else {
			try {
				sendLine("game:" + game.toString());
				game.getOther(this).sendLine("game:" + game.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 取消匹配，把匹配状态设置为false
	 */
	private void cancleFind() {
		isOK = false;
	}

	/*
	 * 投降
	 */
	private void giveUp() {
		if(game == null) {
			return;
		}
		if(game.getUser1().equals(user)) {
			gameOver(2, "玩家投降");
		}else {
			gameOver(1, "玩家投降");
		}
	}

	/*
	 * 同意和棋
	 */
	private void agreePeace() {
		gameOver(0, "同意和棋");
	}

	/*
	 * 结束游戏，n表示哪方胜利，0表示和棋，1表示玩家1胜利，2表示玩家2胜利
	 */
	private void gameOver(int n, String reason) {
		if(game == null) {
			return;
		}
		User user1 = game.getUser1();
		User user2 = game.getUser2();
		UserDao userDao = new UserDao();
		if(n == 0) { //和棋
			user1.setDrCount(user1.getDrCount() + 1);
			user2.setDrCount(user2.getDrCount() + 1);
		}else if(n == 1) { //玩家1胜利
			user1.setScore(user1.getScore() + 2);
			user2.setScore(user2.getScore() - 2);
			user1.setViCount(user1.getViCount() + 1);
			user2.setDeCount(user2.getDeCount() + 1);
		}else if(n == 2) {  //玩家2胜利
			user1.setScore(user1.getScore() - 2);
			user2.setScore(user2.getScore() + 2);
			user2.setViCount(user2.getViCount() + 1);
			user1.setDeCount(user1.getDeCount() + 1);
		}
		//更新到数据库中
		try {
			userDao.update(user1);
			userDao.update(user2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//发送数据到客户端
		try {
			sendLine("gameover:" + n + ";" + reason + ";");
			game.getOther(this).sendLine("gameover:" + n + ";" + reason + ";");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//把两个玩家的游戏对象设置为null
		game.getOther(this).game = null;
		game = null;
	}

	/*
	 * 请求和棋
	 */
	private void askPeace() {
		if(game == null) {
			return;
		}else {
			try {
				game.getOther(this).sendLine("askPeace");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 进行匹配
	 * 
	 */
	private void findGame() {
		System.out.println("正在匹配");
		//把自己设置为就绪状态，可以被匹配到
		isOK = true;
		ServerClient other = null;
		boolean noFound = true;
		synchronized (findGameUsers) {
			Iterator<ServerClient> iterator = findGameUsers.iterator();
			System.out.println("正在匹配的人数是：" + findGameUsers.size());
			while(iterator.hasNext()) {
				other = iterator.next();
				//如果玩家不是就绪状态，就把他从集合中清除
				if(!other.isOK) {
					iterator.remove();
					continue;
				}
				//如果是自己，就删除
				if(other.equals(this)) {
					iterator.remove();
					continue;
				}
				//如果不是自己，并且还有其他人，就匹配，也就是说运行到这里就匹配成功了
				//把是否匹配的标志设置为false
				noFound = false;
				//通过两个客户端，得到一个游戏对象，并设置给两个客户端
				game = new Game(this, other);
				other.game = game;
				iterator.remove();
				//找到之后就不用循环再找了
				break;
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
				sendLine("user:" + game.getUser());
				sendLine("game:" + game.toString());
				other.sendLine("user:" + other.game.getUser());
				other.sendLine("game:" + game.toString());
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
				client.game = null;
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
		//下线的时候把匹配状态修改为false，这样其他玩家匹配时就把这个下线的玩家从可以匹配的集合中删除了
		isOK = false;
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
