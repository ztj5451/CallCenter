package com.callCenter.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.adapter.TxlAdapter;
import com.callCenter.entity.Bm;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.RequestAndResultCode;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/*
 *通讯录Activity 
 * 
 */
public class SelectTxlActivity extends Activity {
	private TxlAdapter adapter;
	private ListView txlList;
	private List<Bm> bmList = null;
	private SettingUtils settingUtils;
	private String uid;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				int size = bmList.size();
				System.out.println("" + size);
				if (bmList != null) {
					adapter = new TxlAdapter(SelectTxlActivity.this, bmList);
					txlList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.Fail:
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
		setContentView(R.layout.select_txl);
		init();
		Event();
		new Thread() {
			public void run() {
				txlDataAction(txlUrl());
			};
		}.start();

	}

	// 初始化
	private void init() {
		settingUtils = new SettingUtils(SelectTxlActivity.this);
		txlList = (ListView) this.findViewById(R.id.txlList);
		uid = getIntent().getExtras().getString("uid");

	}

	// 监听事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						SelectTxlActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// listView点击事件
		txlList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				String bmId = bmList.get(position).getId();
				String bmName = bmList.get(position).getBmName();
				Intent intent = new Intent();
				intent.putExtra("bmId", bmId);
				intent.putExtra("bmName", bmName);
				setResult(RequestAndResultCode.TXL, intent);
				SelectTxlActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);

			}

		});

	}

	// 通讯录 url
	private StringBuffer txlUrl() {
		StringBuffer txlUrl = new StringBuffer(SettingUtils.get("serverIp"));
		txlUrl.append(SettingUtils.get("query_bm_url")).append("uid=")
				.append(uid);
		System.out.println("获取部门url:" + txlUrl);
		return txlUrl;
	}

	// 通讯录 action
	private void txlDataAction(StringBuffer txlUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					txlUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					bmList = new ArrayList<Bm>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = (JSONObject) array.get(i);
						Bm bm = new Bm();
						bm.setId(obj.getString("id"));
						bm.setBmName(obj.getString("bmname"));
						bmList.add(bm);
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
			SelectTxlActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
