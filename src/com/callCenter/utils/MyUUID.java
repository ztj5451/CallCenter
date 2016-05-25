package com.callCenter.utils;

import java.util.UUID;

/*
 * 获取uuid
 */
public class MyUUID {
	public static String get() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
