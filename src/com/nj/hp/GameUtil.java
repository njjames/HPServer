package com.nj.hp;

public class GameUtil {
	public static final int ELEPHANT = 8, LION = 7, TIGER = 6, LEOPARD = 5, WOLF = 4, DOG = 3, CAT = 2, MOUSE = 1;

	// 默认地图（棋盘），什么都没有
	public static final int[][] DEFAULT_MAP = { 
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }
			};

	public static int[][] cloneMap(int[][] map) {
		int[][] m = new int[map.length][];
		for(int i = 0; i < m.length; i++) {
			//这里不能直接赋值，否则指向的是同一地址
			m[i] = map[i].clone();
		}
		return m;
	}
	
	/*
	 * 判断这一步棋是否能走
	 * 传入的参数是当前的地图和走棋的对象
	 */
	public static boolean canWalk(int[][] map, Walk walk) {
		//得到走棋前的牌属于哪方
		int color1 = getColor(map[walk.x1][walk.y1]);
		//得到走棋后的牌属于哪方
		int color2 = getColor(map[walk.x2][walk.y2]);
		//如果属于同一方，则不能移动
		if(color1 == color2) {
			return false;
		}
		//如果不是同一方，得到现在走棋前后牌的代码
		int code1 = map[walk.x1][walk.y1] % 100;
		int code2 = map[walk.x2][walk.y2] % 100;
		int x1 = walk.x1;
		int y1 = walk.y1;
		int x2 = walk.x2;
		int y2 = walk.y2;
		//如果横走或者竖走超过一个格子，return false
		if((x2 - x1) > 1 || (y2 - y1) > 1) {
			return false;
		}
		if(code1 == ELEPHANT) {
			//如果下一步的牌是鼠，false
			if(code2 == MOUSE) {
				return false;
			}
			return true;
		}else if(code1 == MOUSE) {
			//如果下一步比它大，并且不是象，就不能移动
			if(code2 > code1 && code2 != ELEPHANT) {
				return false;
			}
			return true;
		}else if(code1 != 0) {
			//如果下一步的比上一步的大，就不能动
			if(code2 > code1) {
				return false;
			}
			return true;
		}else {
			return false;
		}
	}

	/*
	 * 根据当前位置的代码，判断这个牌属于哪方，0表示没有，1和2分别表示一方
	 */
	private static int getColor(int code) {
		return code / 100;
	}
	
}
