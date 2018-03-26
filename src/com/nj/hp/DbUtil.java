package com.nj.hp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
	private String driver;
	private String jdbcUrl;
	private String user;
	private String pwd;
	private Connection conn;
	
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
}
