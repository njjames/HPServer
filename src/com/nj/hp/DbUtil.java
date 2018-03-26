package com.nj.hp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUtil {
	private String driver;
	private String jdbcUrl;
	private String user;
	private String pwd;
	private Connection conn;
	
	public static void main(String[] args) {
		User user = new User("小明哥", "3353255", "1");
		
		User user1 = new User("小明哥2", "3353255", "1");
		
		UserDao  udDao = new UserDao();
//		try {
//			udDao.sign(user);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		try {
			udDao.login(user1);
			System.out.println(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 默认构造方法，获取数据路的连接
	 */
	public DbUtil() {
		driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		jdbcUrl = "jdbc:sqlserver://localhost:1433;databaseName=HP";
		user = "sa";
		pwd = "";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(jdbcUrl, user, pwd);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void execute(String sql) throws Exception {
		PreparedStatement psmt = conn.prepareStatement(sql);
		psmt.execute();
	}
	
	public ResultSet executeQuery(String sql) throws Exception {
		PreparedStatement psmt = conn.prepareStatement(sql);
		ResultSet resultSet = psmt.executeQuery();
		return resultSet;
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
}
