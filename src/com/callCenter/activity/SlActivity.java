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

public class SlActivity extends Activity {
	private EditText content;
	private String tempId;
	private String idStr, telStr, contentStr;
	private String loginname, userId;

	private SettingUtils settingUtils;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				content.setText(contentStr);
				break;
			case HandlerException.Fail:
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.SuccessToo:
				SlActivity.this.finish();
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
		setContentView(R.layout.sl);
		init();
		Event();
		new Thread() {
			public void run() {
				slAction(sltUrl());
			};
		}.start();

	}

	private void init() {
		tempId = getIntent().getExtras().getString("tempId");
		settingUtils = new SettingUtils(SlActivity.this);
		content = (EditText) this.findViewById(R.id.content);
		userId = getIntent().getExtras().getString("userId");
		loginname = getIntent().getExtras().getString("loginname");

	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						SlActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 受理按钮
		this.findViewById(R.id.sl).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Thread() {
					public void run() {
						slAction2(slUrl());
					};
				}.start();
			}
		});

	}

	// 公文发起URL
	private StringBuffer sltUrl() {
		StringBuffer sltUrl = new StringBuffer(SettingUtils.get("serverIp"));
		sltUrl.append(SettingUtils.get("shouli_pre_url")).append("id=")
				.append(tempId);
		System.out.println("受理pre url:" + sltUrl);
		return sltUrl;
	}

	private StringBuffer slUrl() {
		StringBuffer slUrl = new StringBuffer(SettingUtils.get("serverIp"));
		try {
			slUrl.append(SettingUtils.get("shouli_url"))
					.append("id=")
					.append(idStr)
					.append("&tel=")
					.append(telStr)
					.append("&contents=")
					.append(UrlEncode.Encode2(content.getText().toString()
							.trim())).append("&loginname=").append(loginname)
					.append("&loginid=").append(userId);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("受理 url:" + slUrl);
		return slUrl;
	}

	private void slAction(StringBuffer loginUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				String temp = message.replace("\r\n", "").replace(" ", "");
				JSONObject object = new JSONObject(temp);
				JSONArray array = new JSONArray(object.getString("lsjl"));
				if (array.length() != 0) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						idStr = obj.getString("id");
						telStr = obj.getString("tel");
						contentStr = obj.getString("contents");
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

	private void slAction2(StringBuffer loginUrl) {
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

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SlActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
