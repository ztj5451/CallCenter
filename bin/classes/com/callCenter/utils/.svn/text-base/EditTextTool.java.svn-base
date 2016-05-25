package com.callCenter.utils;
import com.callCenter.activity.R;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

public class EditTextTool {
	// static Context context;
	public static void editShakeAndFocus(Context context, EditText editText,
			int message, int time) {
		// 输入框抖动
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.myanim);
		editText.startAnimation(anim);
		// 输入框聚焦
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		// Toast消息
		if (time == 0) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		} else if (time == 1) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}

	}

	// 输入框抖动
	public static void editShake(Context context, EditText editText) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.myanim);
		editText.startAnimation(anim);
	}

	// 显示消息
	public static void showMessage(Context context, int message, int time) {

		if (time == 0) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		} else if (time == 1) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}

	// 输入框聚焦
	public static void editFocus(EditText editText) {
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
	}
}
