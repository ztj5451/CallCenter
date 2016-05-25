package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.adapter.WorkOrderAdapter;
import com.callCenter.entity.GD;
import com.callCenter.entity.LoginUser;
import com.callCenter.entity.WorkOrder;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 工单列表
 * 
 * @author Administrator
 * 
 */
public class WorkOrderActivity extends Activity {
	private static Context context;
	private static ListView workOrderListView;
	private List<WorkOrder> workOrderList;
	private static LoginUser loginUser;
	private static WorkOrderAdapter adapter;
	private static List<GD> gdList;
	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adapter = new WorkOrderAdapter(context, gdList);
				workOrderListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case HandlerException.Fail:
				adapter = new WorkOrderAdapter(context, gdList);
				workOrderListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.Over:
				// Toast.makeText(context, "派发成功!", Toast.LENGTH_LONG).show();

				new Thread() {
					public void run() {
						gwListAction(gwListUrl());
					};
				}.start();

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
		setContentView(R.layout.work_order);
		context = this;
		init();
		Event();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		new Thread() {
			public void run() {
				gwListAction(gwListUrl());
			};
		}.start();
	}

	// 初始化
	private void init() {
		loginUser = (LoginUser) getIntent().getExtras().getSerializable(
				"loginUser");
		workOrderListView = (ListView) this
				.findViewById(R.id.workerOrderListView);
		gdList = new ArrayList<GD>();
	}

	// 监听事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						WorkOrderActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 列表点击
		workOrderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Intent intent = new Intent(WorkOrderActivity.this,
						GWStartActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("loginUser", loginUser);
				intent.putExtras(bundle);
				String kh_id = gdList.get(position).getId();
				String gdTitle = gdList.get(position).getGdTitle();
				String name = gdList.get(position).getUserName();
				intent.putExtra("kh_id", kh_id);
				intent.putExtra("gdTitle", gdTitle);
				intent.putExtra("name", name);
				intent.putExtra("id", gdList.get(position).getId());
				WorkOrderActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
	}

	// 工单列表URL
	private static StringBuffer gwListUrl() {
		StringBuffer gwListUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwListUrl.append(SettingUtils.get("gd_list_url")).append("uid=")
				.append(loginUser.getId());
		System.out.println("工单列表:" + gwListUrl);
		return gwListUrl;
	}

	// 工单列表
	private static void gwListAction(StringBuffer gwListUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwListUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				message = message.replaceAll("null", "");
				message=message.replaceAll("<", "lt");
				message=message.replaceAll(">", "gt");
				message=message.replaceAll(" ", "");
				message=message.replaceAll("\r", "");
				message=message.replaceAll("\n", "");
				JSONObject object = new JSONObject(message);
				System.out.println("打印输出:" + object.toString());
				JSONArray array = new JSONArray(object.getString("contents"));
				gdList.clear();
				if (array.length() != 0) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						GD gd = new GD();
						gd.setId(obj.getString("id"));
						gd.setName(obj.getString("kh_name"));
						gd.setTel(obj.getString("kh_tel"));
						//gd.setBsjj(obj.getString("bsjj"));
						gd.setTimes(obj.getString("times"));
						gd.setUserName(obj.getString("username"));
						gd.setGdTitle(obj.getString("gdtitle"));
						gd.setTdtype(obj.getString("tdtype"));
						gd.setTdbeizhu(obj.getString("tdbeizhu"));
						gd.setJibie(obj.getString("jibie"));
						gd.setBmname(obj.getString("bmname"));
						gdList.add(gd);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			WorkOrderActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
