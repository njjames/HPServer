package com.nj.hp;

public class Game {
	//步数
	private int step; 
	//步时
	private long walktime;
	
	//玩家1
	private ServerClient client1;
	
	//玩家2
	private ServerClient client2;
	
	//地图(也是当前的地图)
	private int[][] map;
	
	//上一步棋牌的地图
	private int[][] lastmap;
	
	public Game(ServerClient client1, ServerClient client2) {
		this.client1 = client1;
		this.client2 = client2;
		startGame();
	}

	private void startGame() {
		this.step = 0;
		this.lastmap = null;
		this.map = GameUtil.cloneMap(GameUtil.DEFAULT_MAP);
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
		int color = client.game.getMap()[walk.x1][walk.x2];
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
			//更新走棋后的地图为当前地图
			map[walk.x2][walk.y2] = map[walk.x1][walk.y1];
			map[walk.x1][walk.y1] = 0;
			this.step++;
			return true;
		}else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "第" + step + "步";
	}
}
