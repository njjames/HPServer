package com.nj.hp;

import com.sun.org.apache.regexp.internal.recompile;

public class User {
	// 用户名
	private String name;
	// 密码
	private String password;
	// 分数
	private int score;
	// 头像
	private String head;
	// 胜利次数
	private int viCount;
	// 失败次数
	private int deCount;
	// 和棋次数
	private int drCount;
	
	public User() {
		super();
	}
	public User(String name, String password, String head) {
		super();
		this.name = name;
		this.password = password;
		this.head = head;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public int getViCount() {
		return viCount;
	}
	public void setViCount(int viCount) {
		this.viCount = viCount;
	}
	public int getDeCount() {
		return deCount;
	}
	public void setDeCount(int deCount) {
		this.deCount = deCount;
	}
	public int getDrCount() {
		return drCount;
	}
	public void setDrCount(int drCount) {
		this.drCount = drCount;
	}
	@Override
	public String toString() {
		return encode(name) + "," + encode(password) + "," + score + "," + head + "," + viCount + "," + deCount + ","
				+ drCount;
	}
	
	/**
	 *  不包含" "、":"、";"、","以及"\n"
	 */
	public static String encode(String str) {
		return str.replace(" ", "<space>").replace(":", "<maohao>").replace(";", "<fenhao>").replace(",", "<douhao>")
				.replace("\n", "<br>");
	}
	
	public static String decode(String str) {
		return str.replace("<space>", " ").replace("<maohao>", ":").replace("<fenhao>", ";").replace("<douhao>", ",")
				.replace("<br>", "\n");
	}
	
	
	
	
}
