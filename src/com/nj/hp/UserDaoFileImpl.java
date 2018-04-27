package com.nj.hp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class UserDaoFileImpl implements UserDao {

	@Override
	public void sign(User user) throws Exception {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try{
			File file = new File("userconfig.txt");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			br.readLine();
	        String line = null;
	        while((line = br.readLine()) != null) {
	        	String[] split = line.split(",");
	        	if(user.getName().equals(split[0])) {
	        		throw new Exception("用户名已经存在");
	        	}
	        } 
			FileOutputStream fos = new FileOutputStream(file, true);
	        bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
	        bw.write("\r\n" + user.toString());
		} catch(Exception e) {
			throw e;
		} finally {
			if(bw != null) {
				bw.close();
			}
			if(br != null) {
				br.close();
			}
		}
	}

	@Override
	public void login(User user) throws Exception {
		boolean isFound = false;
		BufferedReader br = null;
		try{
			File file = new File("userconfig.txt");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			br.readLine();
			String line = null;
			while((line = br.readLine()) != null) {
				String[] split = line.split(",");
				if(user.getName().equals(split[0]) && user.getPassword().equals(split[1])) {
					user.setName(split[0]);
					user.setPassword(split[1]);
					user.setScore(Integer.parseInt(split[2]));
					user.setHead(split[3]);
					user.setViCount(Integer.parseInt(split[4]));
					user.setDeCount(Integer.parseInt(split[5]));
					user.setDrCount(Integer.parseInt(split[6]));
					isFound = true;
					break;
				}
			}
			//如果找不到就抛异常
			if(!isFound) {
				throw new Exception();
			}
		} catch(Exception e) {
			throw e;
		} finally {
			if(br != null) {
				br.close();
			}
		}
	}

	@Override
	public void update(User user) throws Exception {
		File file = new File("userconfig.txt");
		File tmpfile = new File("userconfig.tmp"); 
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
	        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpfile, true), "utf-8"));
	        br.readLine();
	        bw.write("name,pass,score,head,viCount,deCount,drCount\r\n");
	        String line = null;
	        while((line = br.readLine()) != null) {
	        	String[] split = line.split(",");
	        	if(user.getName().equals(split[0])) {
	        		bw.write(user.toString() + "\r\n");
	        	} else {
	        		bw.write(line + "\r\n");
	        	}
	        }        
		} catch (Exception e) {
			throw e;
		} finally {
			if(bw != null) {
				bw.flush();
				bw.close();
			}
			if(br != null) {
				br.close();
			}
			file.delete();
			boolean renameTo = tmpfile.renameTo(file);
			System.out.println(renameTo + "");
		}
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File("userconfig.txt");
		System.out.println(file.getAbsolutePath());
		User user = new User("小明哥", "3353255", "5");
		user.setScore(10);
		user.setViCount(4);
		user.setDeCount(3);
		user.setDrCount(2);
		UserDao dao = new UserDaoFileImpl();
//		dao.sign(user);
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//		br.readLine();
//		String line = null;
//		while((line = br.readLine()) != null) {
//			System.out.println(line);
//		}
//		System.out.println("=============");
		dao.update(user);
		
	}
	
}
