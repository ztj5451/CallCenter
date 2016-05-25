package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.adapter.JieTingAdapter;
import com.callCenter.adapter.WeiJieTingAdapter;
import com.callCenter.entity.JieTing;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class WjttjSearchActivity extends Activity {
	private ListView wdjtListView;
	private List<JieTing> jieTingList;
	private WeiJieTingAdapter adapter;
	private ListView wjttjListView;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adapter = new WeiJieTingAdapter(WjttjSearchActivity.this,
						jieTingList);
				wdjtListView.setAdapter(adapter);
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
		setContentView(R.layout.wjttj_search);
		init();
		Event();
		new Thread() {
			public void run() {
				wjttjAction(wjttjUrl());
			};
		}.start();

	}

	private void init() {
		wjttjListView = (ListView) this.findViewById(R.id.wjttjListView);
	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						WjttjSearchActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// listView
		wjttjListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Intent intent = new Intent(WjttjSearchActivity.this,
						HuiFangActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
	}

	private StringBuffer wjttjUrl() {
		StringBuffer gwStartUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwStartUrl.append(SettingUtils.get("gw_detail_url")).append("id=")
				.append("");
		return gwStartUrl;
	}

	private void wjttjAction(StringBuffer loginUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString(""));
				if (array.length() != 0) {
					jieTingList = new ArrayList<JieTing>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						JieTing jieTing = new JieTing();
						jieTing.setKh_name(obj.getString(""));
						jieTing.setKh_sex(obj.getString(""));
						jieTing.setCardId(obj.getString(""));
						jieTing.setSheng(obj.getString(""));
						jieTing.setShi(obj.getString(""));
						jieTing.setQu(obj.getString(""));
						jieTing.setJiedao(obj.getString(""));
						jieTing.setSq(obj.getString(""));
						jieTing.setZdy(obj.getString(""));
						jieTing.setAddress(obj.getString(""));
						jieTing.setJg(obj.getString(""));
						jieTing.setKh_phone(obj.getString(""));
						jieTing.setZc_time(obj.getString(""));
						jieTing.setGoutong(obj.getString(""));
						jieTing.setEmail(obj.getString(""));
						jieTing.setXh(obj.getString(""));
						jieTing.setIsDb(obj.getString(""));
						jieTing.setLastTime(obj.getString(""));
						jieTing.setYe(obj.getString(""));
						jieTingList.add(jieTing);
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
			WjttjSearchActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
