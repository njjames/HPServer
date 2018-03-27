package com.nj.hp;

import java.sql.ResultSet;

public class UserDao {
	/*
	 * 注册
	 */
	public void sign(User user) throws Exception {
		String sql = "insert into tb_user(username,password,head,join_time) values('" + user.getName() + "','" 
				+ user.getPassword() + "'," + user.getHead() + ",getdate())" ;
		DbUtil db = new DbUtil();
		try {
			db.execute(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			db.close();
		}
	}
	
	/*
	 * 登录
	 */
	public void login(User user) throws Exception {
		String sql = "select username,head,score,victory_count,defeat_count,draw_count from tb_user where username='"
				+ user.getName() + "' and password='" + user.getPassword() + "'";
		System.out.println(sql);
		DbUtil db = new DbUtil();
		try {
			ResultSet resultSet = db.executeQuery(sql);
			if(resultSet.next()) {
				user.setName(resultSet.getString(1));
				user.setPassword("*");
				user.setHead(resultSet.getInt(2) + "");
				user.setScore(resultSet.getInt(3));
				user.setViCount(resultSet.getInt(4));
				user.setDeCount(resultSet.getInt(5));
				user.setDrCount(resultSet.getInt(6));
			}else {
				throw new Exception();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			db.close();
		}
	}

	/*
	 * 更新玩家信息
	 */
	public void update(User user) throws Exception {
		String sql = "update tb_user set head='" + user.getHead() + 
				"',score=" + user.getScore() + 
				",victory_count=" + user.getViCount() + 
				",defeat_count=" + user.getDeCount() + 
				",draw_count=" + user.getDrCount() + 
				" where username='"+ user.getName() + "'";
		System.out.println(sql);
		DbUtil db = new DbUtil();
		try {
			db.execute(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			db.close();
		}
	}
}
