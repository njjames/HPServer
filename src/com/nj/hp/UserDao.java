package com.nj.hp;

public interface UserDao {
	public void sign(User user) throws Exception;
	
	public void login(User user) throws Exception;
	
	public void update(User user) throws Exception;
}
