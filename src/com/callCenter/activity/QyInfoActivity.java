package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import org.json.JSONObject;
import com.callCenter.adapter.SurroundQyAdapter;
import com.callCenter.entity.CustomInfo;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class QyInfoActivity extends Activity {
	private ListView qyListView;
	private SurroundQyAdapter adapter;
	private List<CustomInfo> qyList;
	private ProgressBar progressBar;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				progressBar.setVisibility(View.GONE);
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				progressBar.setVisibility(View.GONE);
				Toast.makeText(QyInfoActivity.this, "服务器没有响应!",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerException.Fail:
				progressBar.setVisibility(View.GONE);
				Toast.makeText(QyInfoActivity.this, "加载服务器错误!",
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
		setContentView(R.layout.qyinfo);
		// 初始化
		init();
		// 监听事件
		Event();
		//获取企业列表
//		new Thread(){
//			public void run() {
//				progressBar.setVisibility(View.VISIBLE);
//				getQyList();
//			};
//		}.start();
	}

	// 初始化
	private void init() {
		progressBar = (ProgressBar) this.findViewById(R.id.progress);
//		qyList = new ArrayList<QyInfo>();
//
//		qyList = new ArrayList<QyInfo>();
//		QyInfo qyInfo1 = new QyInfo("吉林省长春市人民大街1", 43.881, 125.35);
//		QyInfo qyInfo2 = new QyInfo("吉林省长春市人民大街2", 43.882, 125.124);
//		QyInfo qyInfo3 = new QyInfo("吉林省长春市人民大街3", 43.883, 125.125);
//		QyInfo qyInfo4 = new QyInfo("吉林省长春市人民大街4", 43.884, 125.126);
//		qyList.add(qyInfo1);
//		qyList.add(qyInfo2);
//		qyList.add(qyInfo3);
//		qyList.add(qyInfo4);
//		qyListView = (ListView) this.findViewById(R.id.allQyList);
//		adapter = new SurroundQyAdapter(QyInfoActivity.this, qyList);
//		qyListView.setAdapter(adapter);
//		adapter.notifyDataSetChanged();

	}

	// 事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						QyInfoActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 企业列表
		qyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				double lat = qyList.get(position).getLat();
//				double lon = qyList.get(position).getLon();
//				String qyName = qyList.get(position).getQyName();
//				Intent intent = new Intent(QyInfoActivity.this,
//						QyInfoDetailsActivity.class);
//				intent.putExtra("lat", lat);
//				intent.putExtra("lon", lon);
//				intent.putExtra("qyName", qyName);
//				startActivity(intent);
//				overridePendingTransition(R.anim.in_from_right,
//						R.anim.out_to_left);

			}
		});
	}

	// 获取周边企业个数url
	private StringBuffer qyListUrl() {
		StringBuffer sourroundQyUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		sourroundQyUrl.append(SettingUtils.get("qy_list_url"));

		return sourroundQyUrl;

	}

	// 获取周边企业
	private void getQyList() {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					qyListUrl(), "GET");
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
			QyInfoActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
