package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.callCenter.adapter.ServerPersonJDAdapter;
import com.callCenter.entity.ServerPersonJD;
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
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ServerPersonJDActivity extends Activity {
	private String userId;
	private ListView jdListView;
	private ServerPersonJDAdapter adapter;
	private List<ServerPersonJD> jdList;
	private ProgressBar progressBar;
	private int selectPosition=0;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				// progressBar.setVisibility(View.GONE);
				adapter = new ServerPersonJDAdapter(
						ServerPersonJDActivity.this, jdList);
				jdListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				if (selectPosition>=4) {
					jdListView.setSelection(selectPosition);
				}
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				progressBar.setVisibility(View.GONE);
				Toast.makeText(ServerPersonJDActivity.this, "服务器没有响应!",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerException.Fail:
				progressBar.setVisibility(View.GONE);
				// Toast.makeText(ServerPersonJDActivity.this, "加载服务器错误!",
				// Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.server_person_jd);
		// 初始化
		init();
		// 监听事件
		Event();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// 获取企业列表
		new Thread() {
			public void run() {
				// progressBar.setVisibility(View.VISIBLE);
				getQyList();
			};
		}.start();
	}

	// 初始化
	private void init() {
		jdListView = (ListView) this.findViewById(R.id.allList);
		progressBar = (ProgressBar) this.findViewById(R.id.progress);
		userId = getIntent().getExtras().getString("userId");

	}

	// 事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ServerPersonJDActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 企业列表
		jdListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectPosition=position;
				String gdId = jdList.get(position).getId();
				Intent intent = new Intent(ServerPersonJDActivity.this,
						JdDetailActivity.class);
				intent.putExtra("gdId", gdId);
				intent.putExtra("status", jdList.get(position).getZt());
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

	}

	// 获取周边企业个数url
	private StringBuffer serverPersonJDListUrl() {
		StringBuffer serverPersonJDListUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		serverPersonJDListUrl.append(SettingUtils.get("serverPerson_jd_list"))
				.append("uid=").append(userId);
		System.out.println("服务人员接单地址:" + serverPersonJDListUrl);

		return serverPersonJDListUrl;

	}

	// 获取周边企业
	private void getQyList() {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					serverPersonJDListUrl(), "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				message = message.replaceAll("null", "");
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					jdList = new ArrayList<ServerPersonJD>();
					jdList.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						ServerPersonJD jd = new ServerPersonJD();
						jd.setId(obj.getString("id"));
						jd.setLsh(obj.getString("lsh"));
						jd.setKhName(obj.getString("kh_name"));
						jd.setAddress(obj.getString("kh_address"));
						jd.setTel(obj.getString("kh_tel"));
						jd.setTime(obj.getString("times"));
						jd.setStartTime(obj.getString("starttime"));
						jd.setTuidan(obj.getString("tuidan"));
						jd.setHz(obj.getString("hz"));
						jd.setZt(obj.getString("zt"));
						jd.setJibie(obj.getString("jibie"));
						jdList.add(jd);
					}
					handler.sendEmptyMessage(HandlerException.Success);
				} else {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		selectPosition=0;
	}
	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ServerPersonJDActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
