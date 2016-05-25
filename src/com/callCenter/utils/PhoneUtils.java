package com.callCenter.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PhoneUtils {
	// 调用系统打电话
	public static void Tel(Context context, String telNumber) {

		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ telNumber));
		context.startActivity(intent);
	}

	// 调用系统发送短信
	public static void SendSMS(Context context, String telNumber) {
		Uri uri = Uri.parse("smsto:" + telNumber);
		Intent sendMessage = new Intent(Intent.ACTION_SENDTO, uri);
		sendMessage.putExtra("sms_body", "");
		context.startActivity(sendMessage);
	}

	// 发送邮件
	public static void SendEmail(Context context, String email, String title,
			String message) {
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setData(Uri.parse("mailto:" + email));
		data.putExtra(Intent.EXTRA_SUBJECT, title);
		data.putExtra(Intent.EXTRA_TEXT, message);
		context.startActivity(data);
	}

	// 打开并安装APK
	public static Intent getAPK(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(param)),
				"application/vnd.android.package-archive");

		return intent;

	}

	// android获取一个用于打开Word文件的intent
	public static Intent getWordFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// android获取一个用于打开视频文件的intent
	public static Intent getVideoFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// android获取一个用于打开PDF文件的intent
	public static Intent getPdfFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	// android获取一个用于打开图片文件的intent
	public static Intent getImageFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}
}
