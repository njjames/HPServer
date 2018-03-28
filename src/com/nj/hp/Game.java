package com.nj.hp;

import java.util.ArrayList;

public class Game {
	//步数
	private int step; 
	//步时
	private long walktime;
	
	//玩家1
	private ServerClient client1;
	
	//玩家2
	private ServerClient client2;
	
	//玩家1的牌数
	public int count1;
	
	//玩家2的牌数
	public int count2;
	
	//地图(也是当前的地图)
	private int[][] map;
	
	//上一步棋牌的地图
	private int[][] lastmap;
	
	//走棋的步骤
	private ArrayList<Walk> walks;
	
	public Game(ServerClient client1, ServerClient client2) {
		this.client1 = client1;
		this.client2 = client2;
		startGame();
	}

	private void startGame() {
		this.count1 = 8;
		this.count2 = 8;
		this.step = 0;
		this.lastmap = null;
		this.map = GameUtil.cloneMap(GameUtil.DEFAULT_MAP);
		this.walks = new ArrayList<>();
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public long getWalktime() {
		return walktime;
	}

	public void setWalktime(long walktime) {
		this.walktime = walktime;
	}

	public ServerClient getClient1() {
		return client1;
	}

	public void setClient1(ServerClient client1) {
		this.client1 = client1;
	}

	public ServerClient getClient2() {
		return client2;
	}

	public void setClient2(ServerClient client2) {
		this.client2 = client2;
	}
	
	public int[][] getMap() {
		return map;
	}

	public void setMap(int[][] map) {
		this.map = map;
	}
	
	/*
	 * 获取当前玩家
	 */
	public String getUser() {
		return client1.user.toString() + ";" + client2.user.toString();
	}

	/*
	 * 获取对方玩家
	 */
	public ServerClient getOther(ServerClient self) {
		if(client1.equals(self)) {
			return client2;
		}else {
			return client1;
		}
	}

	public User getUser1() {
		return this.client1.user;
	}
	
	public User getUser2() {
		return this.client2.user;
	}

	/*
	 * 走棋
	 */
	public synchronized boolean walk(ServerClient client, Walk walk) {
		//判断不是轮到自己走了
		//获取走棋属于哪一方
		int color = map[walk.x1][walk.y1] /100;
		if(color == 0) {
			return false;
		}
		//颜色1，表示玩家1走
		if(color == 1) {
			//如果当前走的不是玩家1，false
			if(!client.equals(client1)) {
				return false;
			}
			//如果步数是偶数，也不应该是玩家1走，false
			if(step % 2 == 0) {
				return false;
			}
		}
		if(color == 2) {
			if(!client.equals(client2)) {
				return false;
			}
			if(step % 2 == 1) {
				return false;
			}
		}
		//运行到这里，表示轮到自己走了，然后判断能否走这一步
		if(GameUtil.canWalk(this.map, walk)) {
			//如果可以走
			//把当前的地图更新为上一次的地图
			this.lastmap = GameUtil.cloneMap(map);
			//得到移动前后牌的代码
			int code1 = map[walk.x1][walk.y1] % 100;
			int code2 = map[walk.x2][walk.y2] % 100;
			//如果code2不是0，说明牌1和牌2会死掉一个，或者都死掉
			if(code2 != 0) {
				//两个相等，也就是对了，都去掉
				if(code1 == code2) {
					count1--;
					count2--;
					//对掉
					map[walk.x2][walk.y2] = 0;
				}else{ //这说明code1会吃掉code2
					//如果这一步是玩家1走的，那么玩家2的牌数减1,否则玩家1牌数减1
					if(color == 1) {
						count2--;
					}else {
						count1--;
					}
					//吃掉
					map[walk.x2][walk.y2] = map[walk.x1][walk.y1];
				}
			}
			//之前的一步肯定是变成0
			map[walk.x1][walk.y1] = 0;
			this.step++;
			this.walks.add(walk);
			return true;
		}else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		Walk w = null;
		//如果走过棋了，就取出最后一次的一步，否则初始化一个
		if(walks.size() > 0) {
			w = walks.get(walks.size() - 1);
		}else {
			w = new Walk(-1, -1, -1, -1);
		}
		return step + ";" + w + ";" + getMapString();
	}
	
	/*
	 * 得到地图的字符串
	 */
	private String getMapString() {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 9; j++) {
				s.append(map[i][j] + ",");
			}
		}
		//删除最后一个逗号
		s.delete(s.length() - 1, s.length());
		return s.toString();
	}

	/*
	 * 判断谁胜利，0表示和棋，1表示玩家1,2表示玩家2,-1表示未结束
	 */
	public int whoWin() {
		if(count1 ==0 && count2 == 0) {
			return 0;
		}
		if(count1 == 0 && count2 > 0) {
			return 2;
		}
		if(count2 == 0 && count1 > 0) {
			return 1;
		}
		return -1;
		
	}
}
