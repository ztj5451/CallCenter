package com.callCenter.utils;

import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import android.content.Context;

public class SettingUtils {
	private static Properties prop;
	private static Context mContext;
	public static Map<String, String> ips;

	public SettingUtils(Context context) {
		this.mContext = context;
		prop = new Properties();
		try {
			prop.load(context.getResources().getAssets()
					.open("setting.properties"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//获取key值
	public static String get(String key) {
		String value = null;
		if (prop != null) {
			value = prop.getProperty(key);
			if (value == null)
				System.out.println("读取Key为：‘" + key + "’ 不存在！");

		}
		return value;
	}
	//设置key与value值
	public static void set(String key, String value) {
		if (prop != null) {
			prop.setProperty(key, value);
			try {
				OutputStream outputStream = mContext.getResources().getAssets()
						.openFd("settings.properties").createOutputStream();
				
				prop.store(outputStream, null);
				outputStream.flush();
				
				outputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	//字符串转int类型
	public int str2int(String str) {
		int result = 0;
		if (str != null && str.length() > 0)
			result = Integer.parseInt(str);

		return result;
	}
}
