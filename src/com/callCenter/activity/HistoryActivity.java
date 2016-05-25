package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.adapter.HistoryAdapter;
import com.callCenter.adapter.HistoryAdapter.ViewHolder;
import com.callCenter.entity.History;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IInterface;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HistoryActivity extends Activity {
	private SettingUtils settingUtils;
	private String kh_tel, userId, loginname;
	private ListView historyListView;
	private List<History> historyList;
	private HistoryAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adapter = new HistoryAdapter(HistoryActivity.this, historyList);
				historyListView.setAdapter(adapter);
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
		setContentView(R.layout.history);
		init();
		Event();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		new Thread() {
			public void run() {
				historyAction(historyUrl());
			};
		}.start();
	}

	private void init() {
		settingUtils = new SettingUtils(HistoryActivity.this);
		kh_tel = getIntent().getExtras().getString("kh_tel");
		userId = getIntent().getExtras().getString("userId");
		loginname = getIntent().getExtras().getString("loginname");
		historyListView = (ListView) this.findViewById(R.id.historyListView);
		historyList = new ArrayList<History>();
	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						HistoryActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 合并
		this.findViewById(R.id.hb).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				HashSet<String> ids = HistoryAdapter.selectId;
				StringBuffer selectId = new StringBuffer();
				if (!ids.isEmpty()) {
					for (String id : ids) {
						selectId.append("'").append(id).append("'").append(",");
					}
				}
//				Toast.makeText(HistoryActivity.this, selectId.toString(),
//						Toast.LENGTH_SHORT).show();
				if (ids.size() != 0) {
					Intent intent = new Intent(HistoryActivity.this,
							HeBingActivity.class);
					intent.putExtra("ids", selectId.toString());
					intent.putExtra("userId", userId);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				} else {
					Toast.makeText(HistoryActivity.this, "没有可合并内容!",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		// 历史记录listView
		historyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// Intent intent = new Intent(HistoryActivity.this,
				// SlActivity.class);
				// startActivity(intent);
				// overridePendingTransition(R.anim.in_from_right,
				// R.anim.out_to_left);
				ViewHolder views = (ViewHolder) view.getTag();
				views.select.toggle();
				HistoryAdapter.selectMap.put(position, views.select.isChecked());
				adapter.notifyDataSetChanged();
			}
		});
		// 长按事件
		historyListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> adapterView,
							View view, int position, long id) {
						// Toast.makeText(HistoryActivity.this, position + "",
						// Toast.LENGTH_SHORT).show();
						String tempId = historyList.get(position).getId();
						Intent intent = new Intent(HistoryActivity.this,
								SlActivity.class);
						intent.putExtra("tempId", tempId);
						intent.putExtra("loginname", loginname);
						intent.putExtra("userId", userId);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						return false;
					}
				});
	}

	// 公文发起URL
	private StringBuffer historyUrl() {
		StringBuffer historyUrl = new StringBuffer(SettingUtils.get("serverIp"));
		historyUrl.append(SettingUtils.get("zuoxi_history_url")).append("tel=")
				.append(kh_tel);
		System.out.println("呼叫历史url:" + historyUrl);
		return historyUrl;
	}

	private void historyAction(StringBuffer loginUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("jtls"));
				if (array.length() != 0) {
					historyList.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						History history = new History();
						history.setId(obj.getString("id"));
						history.setKh_name(obj.getString("kh_name"));
						history.setKh_tel(obj.getString("kh_tel"));
						history.setContents(obj.getString("contents"));
						history.setTimes(obj.getString("times"));
						historyList.add(history);
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
			HistoryActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
