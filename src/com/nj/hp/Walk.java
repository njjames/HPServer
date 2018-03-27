package com.nj.hp;

public class Walk {
	public int x1,y1,x2,y2;

	public Walk(int x1, int y1, int x2, int y2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public String toString() {
		return x1 + "," + y1 + "," + x2 + "," + y2;
	}
	
	public static Walk fromString(String str) {
		String[] walksteps = str.split(",");
		return new Walk(Integer.parseInt(walksteps[0]),
				Integer.parseInt(walksteps[1]),
				Integer.parseInt(walksteps[2]),
				Integer.parseInt(walksteps[3]));
	}
}
