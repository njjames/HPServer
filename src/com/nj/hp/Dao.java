package com.nj.hp;

public interface Dao {
	public void sign(User user) throws Exception;
	
	public void login(User user) throws Exception;
	
	public void update(User user) throws Exception;
}
