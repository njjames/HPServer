package com.nj.hp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class UserFileDao implements Dao {

	@Override
	public void sign(User user) throws Exception {
		BufferedWriter bw = null;
		try{
			File file = new File("userconfig.txt");
			FileOutputStream fos = new FileOutputStream(file, true);
	        bw = new BufferedWriter(new OutputStreamWriter(fos));
	        bw.write("\n" + user.toString());
		} catch(Exception e) {
			throw e;
		} finally {
			if(bw != null) {
				bw.close();
			}
		}
		
	}

	@Override
	public void login(User user) throws Exception {

	}

	@Override
	public void update(User user) throws Exception {
		
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File("userconfig.txt");
		System.out.println(file.getAbsolutePath());
		User user = new User("小明哥2", "3353255", "1");
		Dao dao = new UserFileDao();
		dao.sign(user);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		br.readLine();
		String line = null;
		while((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}
	
}
