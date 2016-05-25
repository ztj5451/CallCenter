package com.callCenter.activity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

public class HeBingActivity extends Activity {
	private EditText content;
	private SettingUtils settingUtils;
	private String ids, tel;
	private String userId, urlIds;
	private StringBuffer sbContent, sbIds;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				content.setText(sbContent.toString());
				break;
			case HandlerException.Fail:
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.SuccessToo:
				HeBingActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);
				break;
			default:

				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hebing);
		init();
		Event();
		new Thread() {
			public void run() {
				preHebingAction(preHebingUrl());
			};
		}.start();
	}

	private void init() {
		userId = getIntent().getExtras().getString("userId");
		ids = getIntent().getExtras().getString("ids");
		ids = ids.substring(0, ids.length() - 1);
		content = (EditText) this.findViewById(R.id.content);
		settingUtils = new SettingUtils(HeBingActivity.this);

	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						HeBingActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 合并
		this.findViewById(R.id.hebing).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String tempIds = sbIds.toString();
						urlIds = tempIds.substring(0, tempIds.length() - 1);
						new Thread() {
							public void run() {
								hebingAction(hebingUrl());
							};
						}.start();
					}
				});
	}

	private StringBuffer hebingUrl() {
		StringBuffer preHebingUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		try {
			preHebingUrl.append(SettingUtils.get("hebing_url")).append("id=")
					.append(urlIds).append("&contents=")
					.append(UrlEncode.Encode2(sbContent.toString()))
					.append("&tel=").append(tel).append("&uid=").append(userId);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("欲合并url:" + preHebingUrl.toString());

		return preHebingUrl;
	}

	private StringBuffer preHebingUrl() {
		StringBuffer preHebingUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		preHebingUrl.append(SettingUtils.get("hebing_pre_url")).append("id=")
				.append(ids);
		System.out.println("欲合并url:" + preHebingUrl.toString());

		return preHebingUrl;
	}

	private void hebingAction(StringBuffer loginUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				handler.sendEmptyMessage(HandlerException.SuccessToo);
			}
		} catch (Exception e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	private void preHebingAction(StringBuffer loginUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("hbindo"));
				if (array.length() != 0) {
					sbContent = new StringBuffer();
					sbIds = new StringBuffer();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						sbContent.append(obj.getString("contents"));
						sbIds.append(obj.getString("id")).append("@");
						tel = obj.getString("tel");
					}
					handler.sendEmptyMessage(HandlerException.Success);
				} else {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			HeBingActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
