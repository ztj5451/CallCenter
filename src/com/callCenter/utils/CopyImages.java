package com.callCenter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CopyImages {
	public static void copy(String src, String desc, String imageName) {
		try {
			// 判断路径是否存在
			File dir = new File(desc);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 判断文件是否存在
			dir = new File(dir, imageName);
			if (dir.exists()) {
				dir.delete();
			}

			FileInputStream inputStream = new FileInputStream(new File(src));
			OutputStream outputStream = new FileOutputStream(dir);
			byte[] buffer = new byte[1024 * 10];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);

			}
			inputStream.close();
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
