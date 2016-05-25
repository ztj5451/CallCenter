package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.adapter.DetailAdapter;
import com.callCenter.entity.GwgzDetail;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;

public class GwgzDetailActivity extends Activity {
	private SettingUtils settingUtils;
	private List<GwgzDetail> gwgzDetailList;
	private ListView detailListView;
	private DetailAdapter adapter;
	private String gwId;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adapter = new DetailAdapter(GwgzDetailActivity.this,
						gwgzDetailList);
				detailListView.setAdapter(adapter);
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
		setContentView(R.layout.gwgz_detail);
		init();
		Event();
		new Thread() {
			public void run() {
				gwgzDetailAction(gwgzDetailUrl(gwId));
			};
		}.start();
	}

	private void init() {
		gwId = getIntent().getExtras().getString("gwId");
		settingUtils = new SettingUtils(GwgzDetailActivity.this);
		detailListView = (ListView) this.findViewById(R.id.detailListView);
	}

	private void Event() {
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						GwgzDetailActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
	}

	// 公文发起URL
	private StringBuffer gwgzDetailUrl(String id) {
		StringBuffer gwgzDetailUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		gwgzDetailUrl.append(SettingUtils.get("gwgz_detail_url")).append("id=")
				.append(id);
		return gwgzDetailUrl;
	}

	// 工单派发
	private void gwgzDetailAction(StringBuffer gwgzDetailUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwgzDetailUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);

				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					gwgzDetailList = new ArrayList<GwgzDetail>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						GwgzDetail gd = new GwgzDetail();
						gd.setBmName(obj.getString("bmname"));
						gd.setZt(obj.getString("zt"));
						gwgzDetailList.add(gd);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GwgzDetailActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
