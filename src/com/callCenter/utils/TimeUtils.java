package com.callCenter.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	// ��ȡ����ʱ�� _�ָ�
	@SuppressLint("SimpleDateFormat")
	public static String getDateTime() {
		return new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
	}
	@SuppressLint("SimpleDateFormat")
	public static String getPrintDateTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	// ��ȡʹ���û�ID��ɵ�ʱ��
	@SuppressLint("SimpleDateFormat")
	public static String getDateTime(String id) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(id)
				.append("_")
				.append(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
						.format(new Date()));
		return buffer.toString();
	}

	// ��ȡͼƬ�����ص�ʱ��
	@SuppressLint("SimpleDateFormat")
	public static String getImageName() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

	}

	// ��ȡ��λ����ص�ʱ��
	@SuppressLint("SimpleDateFormat")
	public static String getLocationTime() {
		return new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new Date());
	}

	// ��ȡ���ʱ��
	@SuppressLint("SimpleDateFormat")
	public static String getNormalDataTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
	}
}
