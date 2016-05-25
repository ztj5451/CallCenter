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
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 企业详细信息
 * 
 * @author Administrator
 * 
 */
public class QyInfoDetailsActivity extends Activity {
	private double lat, lon;
	private TextView qyName;
	private ProgressBar progressBar;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				progressBar.setVisibility(View.GONE);
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				progressBar.setVisibility(View.GONE);
				Toast.makeText(QyInfoDetailsActivity.this, "服务器没有响应!",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerException.Fail:
				progressBar.setVisibility(View.GONE);
				Toast.makeText(QyInfoDetailsActivity.this, "加载服务器错误!",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qy_details_info);
		// 初始化
		init();
		// 添加事件
		Event();
		// 获取企业详细信息
		// new Thread() {
		// public void run() {
		// progressBar.setVisibility(View.VISIBLE);
		// getQyDetail();
		// };
		// }.start();
	}

	// 初始化
	private void init() {
		try {
			lat = getIntent().getExtras().getDouble("lat");
			lon = getIntent().getExtras().getDouble("lon");

			qyName.setText(getIntent().getExtras().getString("qyName"));

		} catch (Exception e) {
			// TODO: handle exception
		}
		qyName = (TextView) this.findViewById(R.id.qyName);
		progressBar = (ProgressBar) this.findViewById(R.id.progress);
	}

	// 监听事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						QyInfoDetailsActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 显示企业位置按钮
		this.findViewById(R.id.mapLocation).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(QyInfoDetailsActivity.this,
								QyDetailMapActivity.class);
						intent.putExtra("lat", lat);
						intent.putExtra("lon", lon);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);

					}
				});
	}

	// 获取周边企业个数url
	private StringBuffer qyDetailUrl() {
		StringBuffer sourroundQyUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		sourroundQyUrl.append(SettingUtils.get("qy_detail_url"));

		return sourroundQyUrl;

	}

	// 获取周边企业
	private void getQyDetail() {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					qyDetailUrl(), "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		}
	}

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			QyInfoDetailsActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
