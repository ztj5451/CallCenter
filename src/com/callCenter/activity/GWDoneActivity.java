package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.location.ad;
import com.callCenter.adapter.GwGzAdapter;
import com.callCenter.entity.GwGz;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class GWDoneActivity extends Activity {
	private String loginname,uid;
	private SettingUtils settingUtils;
	private List<GwGz> gwGzList;
	private ListView gwGzListView;
	private GwGzAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adapter = new GwGzAdapter(GWDoneActivity.this, gwGzList);
				gwGzListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();

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
		setContentView(R.layout.gwdone);
		init();
		Event();

		new Thread() {
			public void run() {
				gwGzAction(gwGzUrl(loginname));
			};
		}.start();

	}

	private void init() {
		loginname = getIntent().getExtras().getString("loginname");
		uid=getIntent().getExtras().getString("uid");
		settingUtils = new SettingUtils(GWDoneActivity.this);
		gwGzListView = (ListView) this.findViewById(R.id.gwGzListView);
	}

	// 按钮事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						GWDoneActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// listview 点击事件
		gwGzListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Intent intent = new Intent(GWDoneActivity.this,
						GwgzDetailActivity.class);
				String gwId = gwGzList.get(position).getId();
				intent.putExtra("gwId", gwId);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});

	}

	// 公文发起URL
	private StringBuffer gwGzUrl(String loginname) {
		StringBuffer gwGzUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwGzUrl.append(SettingUtils.get("gwgz_list_url")).append("uid=")
				.append(uid);
		System.out.println("公文跟踪URL："+gwGzUrl);
		return gwGzUrl;
	}

	// 工单派发
	@SuppressLint("NewApi")
	private void gwGzAction(StringBuffer gwGzUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwGzUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				JSONObject object = new JSONObject(message);
				
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					gwGzList = new ArrayList<GwGz>();
					gwGzList.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						GwGz gwGz = new GwGz();
						gwGz.setId(obj.getString("id"));
						gwGz.setKhName(obj.getString("kh_name"));
						gwGz.setLsh(obj.getString("lsh"));
						gwGz.setPdName(obj.getString("pdname"));
						gwGz.setTime(obj.getString("times"));
						gwGzList.add(gwGz);
					}
					handler.sendEmptyMessage(HandlerException.Success);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GWDoneActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
