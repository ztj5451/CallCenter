package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.adapter.SurroundQyAdapter;
import com.callCenter.entity.CustomInfo;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SurroundQyActivity extends Activity {
	private ListView surroundQyListView;
	private List<CustomInfo> qyList;
	private SurroundQyAdapter adapter;
	private ProgressBar progressBar;
	private double lat, lon;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				progressBar.setVisibility(View.GONE);
				adapter = new SurroundQyAdapter(SurroundQyActivity.this, qyList);
				surroundQyListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				progressBar.setVisibility(View.GONE);
				Toast.makeText(SurroundQyActivity.this, "服务器没有响应!",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerException.Fail:
				progressBar.setVisibility(View.GONE);
				Toast.makeText(SurroundQyActivity.this, "加载服务器错误!",
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
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.surroundqy);
		// 初始化
		init();
		// 监听事件
		Event();
		// 加载企业列表
		new Thread() {
			public void run() {
				progressBar.setVisibility(View.VISIBLE);
				sourroundQyListAction();
			};
		}.start();

	}

	// 初始化
	private void init() {
		surroundQyListView = (ListView) this.findViewById(R.id.surroundQy);
		progressBar = (ProgressBar) this.findViewById(R.id.progress);
		lat = getIntent().getExtras().getDouble("lat");
		lon = getIntent().getExtras().getDouble("lon");
	}

	// 事件
	private void Event() {
		// 返回按钮事件
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						SurroundQyActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
		// 企业点击
		surroundQyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {

				Intent intent = new Intent(SurroundQyActivity.this,
						ForMapQyDetailsActivity.class);
				intent.putExtra("id", qyList.get(position).getId());
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

	}

	// 获取周边企业个数url
	private StringBuffer sourroundQyListUrl() {
		StringBuffer sourroundQyUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		sourroundQyUrl.append(SettingUtils.get("sourround_list_url"))
				.append("jd=").append(lon).append("&wd=").append(lat);
		System.out.println("周边企业列表:" + sourroundQyUrl);
		return sourroundQyUrl;

	}

	// 获取周边企业
	private void sourroundQyListAction() {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					sourroundQyListUrl(), "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					qyList = new ArrayList<CustomInfo>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						CustomInfo customInfo = new CustomInfo();
						customInfo.setId(obj.getString("id"));
						customInfo.setName(obj.getString("kh_name"));
						customInfo.setTel(obj.getString("kh_tel"));
						qyList.add(customInfo);

					}
					handler.sendEmptyMessage(HandlerException.Success);
				}
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
			SurroundQyActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
