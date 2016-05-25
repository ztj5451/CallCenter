package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONObject;

import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class JieTingDetailActivity extends Activity {
	private SettingUtils settingUtils;
	private TextView jcsjText, nrText;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:

				break;
			case HandlerException.Fail:
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
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
		setContentView(R.layout.jieting_detail);
		init();
		Event();

	}

	private void init() {
		settingUtils = new SettingUtils(JieTingDetailActivity.this);
		jcsjText = (TextView) this.findViewById(R.id.jcsj);
		nrText = (TextView) this.findViewById(R.id.nr);
	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						JieTingDetailActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 派单
		this.findViewById(R.id.pd).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(JieTingDetailActivity.this,
						PfActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
		// 受理
		this.findViewById(R.id.sl).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(JieTingDetailActivity.this,
						SlActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});

	}

	private StringBuffer jieTingDetailUrl() {
		StringBuffer gwStartUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwStartUrl.append(SettingUtils.get("gw_detail_url")).append("id=")
				.append("");
		return gwStartUrl;
	}

	private void jieTingDetailAction(StringBuffer loginUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject("contents");

				if (message.equals("")) {
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
			JieTingDetailActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
