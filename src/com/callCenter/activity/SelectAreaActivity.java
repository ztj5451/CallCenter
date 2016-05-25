package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.callCenter.adapter.AreaAdapter;
import com.callCenter.entity.Area;
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

/**
 * 选择地域
 * 
 * @author Administrator
 * 
 */
public class SelectAreaActivity extends Activity {
	private ListView areaListView;
	private List<Area> listArea;
	private AreaAdapter adapter;
	private SettingUtils settingUtils;
	private String url, id, flag, areaName, userId;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adapter = new AreaAdapter(SelectAreaActivity.this, listArea);
				areaListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.Fail:
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_area);
		init();
		Event();
		new Thread() {
			public void run() {
				selectAreaAction(selectAreaUrl(url, id, flag, areaName));
			};
		}.start();
	}

	// 初始化
	private void init() {
		settingUtils = new SettingUtils(SelectAreaActivity.this);
		areaListView = (ListView) this.findViewById(R.id.area);
		id = getIntent().getExtras().getString("id");
		url = getIntent().getExtras().getString("url");
		flag = getIntent().getExtras().getString("flag");
		areaName = getIntent().getExtras().getString("areaName");
		userId = getIntent().getExtras().getString("userId");
	}

	// 事件
	private void Event() {
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						SelectAreaActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		areaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				String areaId = listArea.get(position).getId();
				String name = listArea.get(position).getName();
				Intent intent = new Intent();
				intent.putExtra("name", name);
				intent.putExtra("areaId", areaId);
				setResult(RequestAndResultCode.SELECT_SHENG, intent);
				SelectAreaActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);

			}
		});
	}

	// url
	private StringBuffer selectAreaUrl(String url, String id, String flag,
			String areaName) {
		StringBuffer selectAreaUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
//		StringBuffer selectAreaUrl = new StringBuffer(
//				"http://192.168.1.103:8080/cl_temp");
		selectAreaUrl.append(SettingUtils.get("select_area_url")).append(url)
				.append("?uid=").append(userId).append("&").append(areaName)
				.append("=").append(flag);
		System.out.println("选择地域url:" + selectAreaUrl);
		return selectAreaUrl;
	}

	// 连接网络
	private void selectAreaAction(StringBuffer selectAreaUrl) {

		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					selectAreaUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message.replace("\r\n", ""));
				JSONArray array = new JSONArray(object.getString("area"));
				if (array.length() != 0) {
					listArea = new ArrayList<Area>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						Area area = new Area();
						area.setId(obj.getString("id"));
						area.setName(obj.getString("name"));
						listArea.add(area);

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

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SelectAreaActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
