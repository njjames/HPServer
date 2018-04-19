package com.nj.hp;

import java.util.Random;

public class GameUtil {
	public static final int ELEPHANT = 8, LION = 7, TIGER = 6, LEOPARD = 5, WOLF = 4, DOG = 3, CAT = 2, MOUSE = 1;
	
	public static final int MODEL_SIMPLE = 1;
	public static final int MODEL_CLASSICS = 2;
	// 默认地图（棋盘），什么都没有
	public static final int[][] DEFAULT_MAP_MODEL2 = { 
			{ 107, 0, 0, 0, 0, 0, 106 },
			{ 0, 103, 0, 0, 0, 102, 0 }, 
			{ 101, 0, 105, 0, 104, 0, 108 }, 
			{ 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0 },
			{ 208, 0, 204, 0, 205, 0, 201 },
			{ 0, 202, 0, 0, 0, 203, 0 }, 
			{ 206, 0, 0, 0, 0, 0, 207 }
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
	 * 判断这一步棋是否能走（注意，这个方法只判断能不能走，不判断其他逻辑）
	 * 传入的参数是当前的地图和走棋的对象
	 */
	public static boolean canWalk(int[][] map, Walk walk, int model) {
		if(model == 1) {
			//得到走棋前的牌属于哪方
			int color1 = getColor(map[walk.x1][walk.y1]);
			//得到走棋后的牌属于哪方
			int color2 = getColor(map[walk.x2][walk.y2]);
			//如果属于同一方，则不能移动
			if(color1 == color2) {
				return false;
			}
			//如果移动后的位置的没有翻开的牌，则不能移动
			if(color2 < 0) {
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
			if(Math.abs(x2 - x1) > 1 || Math.abs(y2 - y1) > 1 || (Math.abs(x2 - x1) == 1 && Math.abs(y2 - y1) == 1)) {
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
			}else if(code1 > 0) {
				//如果下一步的比上一步的大，就不能动
				if(code2 > code1) {
					return false;
				}
				return true;
			}else { //这里包括移动前是没有翻开的牌，或者没有牌
				return false;
			}
		}else if(model == 2) {
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
			//下一步是自己的老巢，不能走
			if(isMyBoss(x2, y2, color1)) {
				return false;
			}
			if(code1 == ELEPHANT) {
				if(Math.abs(x2 - x1) > 1 || Math.abs(y2 - y1) > 1 || (Math.abs(x2 - x1) == 1 && Math.abs(y2 - y1) == 1)) {
					return false;
				}
				//下一步是河，不能走
				if(isInRiver(x2, y2)) {
					return false;
				}
				//下一步是陷阱，肯定能走
				if(isInTrap(x2, y2)) {
					return true;
				}
				//下一步是老鼠，不能走
				if(code2 == MOUSE) {
					return false;
				}
				return true;
			}else if(code1 == LION || code1 == TIGER) {//狮子和老虎的走法是一样的
				//判断是不是在跳河
				if(isJumpRiver(x1, y1, x2, y2)) {
					//如果是在跳河判断能不能跳成功
					if(isMouseInRiver(x1, y1, x2, y2, map)) {
						return false;
					}
					if(code2 > code1) {
						return false;
					}
					return true;
				}else {//如果不是在跳河，就正常判断
					if(Math.abs(x2 - x1) > 1 || Math.abs(y2 - y1) > 1 || (Math.abs(x2 - x1) == 1 && Math.abs(y2 - y1) == 1)) {
						return false;
					}
					//下一步是河，不能走
					if(isInRiver(x2, y2)) {
						return false;
					}
					//下一步是陷阱，肯定能走
					if(isInTrap(x2, y2)) {
						return true;
					}
					if(code2 > code1) {
						return false;
					}
					return true;
				}
			}else if(code1 == LEOPARD || code1 == WOLF || code1 == DOG || code1 == CAT) { //豹、狼、狗、猫的走法是一样的
				if(Math.abs(x2 - x1) > 1 || Math.abs(y2 - y1) > 1 || (Math.abs(x2 - x1) == 1 && Math.abs(y2 - y1) == 1)) {
					return false;
				}
				//下一步是河，不能走
				if(isInRiver(x2, y2)) {
					return false;
				}
				//下一步是陷阱，肯定能走
				if(isInTrap(x2, y2)) {
					return true;
				}
				if(code2 > code1) {
					return false;
				}
				return true;
			}else if(code1 == MOUSE) {
				if(Math.abs(x2 - x1) > 1 || Math.abs(y2 - y1) > 1 || (Math.abs(x2 - x1) == 1 && Math.abs(y2 - y1) == 1)) {
					return false;
				}
				//下一步是陷阱，肯定能走
				if(isInTrap(x2, y2)) {
					return true;
				}
				if(code2 > code1 && code2 != ELEPHANT) {
					return false;
				}
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}

	private static boolean isMouseInRiver(int x1, int y1, int x2, int y2, int[][] map) {
		//如果是横着跳
		if(x1 == x2) {
			if(map[x1][(y2 + y1)/2] > 0 || map[x1][(y2 + y1)/2 + 1] > 0) {
				return true;
			}
		}else { //否则是竖着跳
			if(map[(x1 + x2)/2 - 1][y1] > 0 || map[(x1 + x2)/2][y1] > 0 || map[(x1 + x2)/2 + 1][y1] > 0) {
				return true;
			}
		}
		return false;
	}

	private static boolean isJumpRiver(int x1, int y1, int x2, int y2) {
		if((x1 == 2 && y1 == 1 && x2 == 6 && y2 == 1) || (x1 == 6 && y1 == 1 && x2 == 2 && y2 == 1)) {
			return true;
		}
		if((x1 == 2 && y1 == 2 && x2 == 6 && y2 == 2) || (x1 == 6 && y1 == 2 && x2 == 2 && y2 == 2)) {
			return true;
		}
		if((x1 == 2 && y1 == 4 && x2 == 6 && y2 == 4) || (x1 == 6 && y1 == 4 && x2 == 2 && y2 == 4)) {
			return true;
		}
		if((x1 == 2 && y1 == 5 && x2 == 6 && y2 == 5) || (x1 == 6 && y1 == 5 && x2 == 2 && y2 == 5)) {
			return true;
		}
		if((x1 == 3 && y1 == 0 && x2 == 3 && y2 == 3) || (x1 == 3 && y1 == 3 && x2 == 3 && y2 == 0)) {
			return true;
		}
		if((x1 == 3 && y1 == 6 && x2 == 3 && y2 == 3) || (x1 == 3 && y1 == 3 && x2 == 3 && y2 == 6)) {
			return true;
		}
		if((x1 == 4 && y1 == 0 && x2 == 4 && y2 == 3) || (x1 == 4 && y1 == 3 && x2 == 4 && y2 == 0)) {
			return true;
		}
		if((x1 == 4 && y1 == 6 && x2 == 4 && y2 == 3) || (x1 == 4 && y1 == 3 && x2 == 4 && y2 == 6)) {
			return true;
		}
		if((x1 == 5 && y1 == 0 && x2 == 5 && y2 == 3) || (x1 == 5 && y1 == 3 && x2 == 5 && y2 == 0)) {
			return true;
		}
		if((x1 == 5 && y1 == 6 && x2 == 5 && y2 == 3) || (x1 == 5 && y1 == 3 && x2 == 5 && y2 == 6)) {
			return true;
		}
		return false;
	}

	private static boolean isInRiverSide(int x, int y) {
		if((x == 2 && y == 1) || (x == 2 && y == 2) || (x == 2 && y == 4) || (x == 2 && y == 5)
				|| (x == 3 && y == 0) || (x == 3 && y == 3) || (x == 3 && y == 6)
				|| (x == 4 && y == 0) || (x == 4 && y == 3) || (x == 4 && y == 6)
				|| (x == 5 && y == 0) || (x == 5 && y == 3) || (x == 5 && y == 6)
				|| (x == 6 && y == 1) || (x == 6 && y == 2) || (x == 6 && y == 4) || (x == 6 && y == 5)) {
			return true;
		}
		return false;
	}

	private static boolean isMyBoss(int x, int y, int color) {
		if(color == 1) {
			if(x == 0 && y == 3) {
				return true;
			}
		}else{
			if(x == 8 && y == 3) {
				return true;
			}
		}
		return false;
	}

	private static boolean isInRiver(int x, int y) {
		if((x >= 3 && x <= 5) && ((y >= 1 && y <=2) || (y >= 4 && y <=5))) {
			return true;
		}
		return false;
	}
	private static boolean isInTrap(int x, int y) {
		if((x == 0 && y == 2) || (x == 0 && y == 4) || (x == 1 && y == 3)
				|| (x == 7 && y == 3) || (x == 8 && y == 2) || (x == 8 && y == 4)) {
			return true;
		}
		return false;
	}

	/*
	 * 根据当前位置的代码，判断这个牌属于哪方，0表示没有，1和2分别表示一方
	 */
	private static int getColor(int code) {
		return code / 100;
	}

	public static int[][] initMap() {
		int[] pai = { 
				-101, -102, -103, -104 ,-105, -106, -107, -108 , -201, -202, -203, -204, -205, -206, -207, -208
			};
		int[][] map = new int[4][4];
		Random random = new Random();
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				int nextInt = random.nextInt(16 - 4 * i - j);
				map[i][j] = pai[nextInt];
				pai[nextInt] = pai[15 - 4 * i - j];
			}
		}
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				System.out.print(map[i][j] + ",");
			}
		}
		return map;
	}
	
}
