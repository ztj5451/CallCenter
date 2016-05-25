package com.callCenter.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTool {
	public static boolean Resolve(String message) throws JSONException {
		JSONObject object = new JSONObject(message);
		if (object.getString("flag").trim().equals("1")) {
			return true;

		} else {
			return false;
		}

	}
	//���ķ���ʹ��  ����ɹ���  ���ع���ID
	public static String gwId(String message) throws JSONException
	{
		JSONObject object=new JSONObject(message);
		if (object.getString("flag").trim().equals("1")) {
			return object.getString("gw_id");
		}else {
			return "";
		}
	}
}
