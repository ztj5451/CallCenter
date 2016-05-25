package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.adapter.GwcbAdapter;
import com.callCenter.entity.Cuiban;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.PhoneUtils;
import com.callCenter.utils.SettingUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class GwcbActivity extends Activity {
	private String phoneNumber="";
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private String userId;
	private static Context context;
	public static String loginname;
	private SettingUtils settingUtils;
	private static ListView gwcbListView;
	private static List<Cuiban> cuibanList;
	private static GwcbAdapter adapter;
	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adapter = new GwcbAdapter(context, cuibanList);
				gwcbListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case HandlerException.Fail:
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.AllSuccess:

				adapter = new GwcbAdapter(context, cuibanList);
				gwcbListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case 100:
				adapter.notifyDataSetChanged();
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
		setContentView(R.layout.gwcb);
		context = this;
		init();
		Event();
		new Thread() {
			public void run() {
				gwcbAction(gwcbUrl());
			};
		}.start();
	}

	private void init() {
		userId = getIntent().getExtras().getString("userId");
		loginname = getIntent().getExtras().getString("loginname");
		settingUtils = new SettingUtils(GwcbActivity.this);
		gwcbListView = (ListView) this.findViewById(R.id.gwcbListView);
		registerForContextMenu(gwcbListView);
	}

	private void Event() {
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						GwcbActivity.this.finish();

						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		gwcbListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				phoneNumber=cuibanList.get(position).getKh_tel();
				
			}
		});
		
	}

	// 公文发起URL
	private StringBuffer gwcbUrl() {
		StringBuffer gwcbUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwcbUrl.append(SettingUtils.get("gwcb_list_url")).append("uid=")
				.append(userId);
		System.out.println("公文催办url:" + gwcbUrl);
		return gwcbUrl;
	}

	// 工单派发
	private void gwcbAction(StringBuffer gwcbUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwcbUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				message = message.replaceAll("null", "");
				JSONObject object = new JSONObject(message);
				String temp = object.getString("contents");
				JSONArray array = new JSONArray(temp);
				if (array.length() != 0) {
					cuibanList = new ArrayList<Cuiban>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						Cuiban cuiban = new Cuiban();
						cuiban.setId(obj.getString("id"));
						cuiban.setKh_name(obj.getString("kh_name"));
						cuiban.setKh_tel(obj.getString("kh_tel"));
						cuiban.setJibie(obj.getString("jibie"));
						cuiban.setTimes(obj.getString("times"));
						cuiban.setStarttime(obj.getString("starttime"));
						cuiban.setBmname(obj.getString("bmname"));
						cuiban.setJdbmname(obj.getString("jdbmname"));
						cuiban.setJdtimes(obj.getString("jdtimes"));
						cuiban.setZt(obj.getString("zt"));
						cuiban.setBs(obj.getString("bs"));
						cuiban.setCuiban(obj.getString("cuiban"));
						cuibanList.add(cuiban);

					}
					handler.sendEmptyMessage(HandlerException.Success);
				} else {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	// 上下文菜单，本例会通过长按条目激活上下文菜单
		@Override
		public void onCreateContextMenu(ContextMenu menu, View view,
				ContextMenuInfo menuInfo) {
			menu.setHeaderTitle("相关操作");
			// 添加菜单项
			menu.add(0, ITEM1, 0, "拨打电话");
			menu.add(0, ITEM2, 0, "复制号码");
		}

		// 菜单单击响应
		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		@Override
		public boolean onContextItemSelected(MenuItem item) {

			switch (item.getItemId()) {
			case ITEM1:
				// 在这里添加处理代码
				PhoneUtils.Tel(GwcbActivity.this, phoneNumber);
				break;
			case ITEM2:
				// 在这里添加处理代码
				ClipboardManager cmb = (ClipboardManager) this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setText(phoneNumber);
				break;
			}
			return true;
		}
	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GwcbActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
