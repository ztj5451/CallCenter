package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.entity.Myd;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TdActivity extends Activity {
	private ArrayAdapter<String> adapter;
	private EditText tdbzEdit;
	private String tdbz;
	private Spinner tdlySpinner;
	private SettingUtils settingUtils;
	private List<Myd> mydList;
	private String[] mydArray;
	private String mydId, id, gwId, bmId, loginname;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				Toast.makeText(TdActivity.this, "退单完成!", Toast.LENGTH_SHORT)
						.show();
				clearData();
				TdActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);
				break;
			case HandlerException.Fail:
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.AllSuccess:
				if (mydList != null) {
					mydArray = new String[mydList.size()];
					for (int i = 0; i < mydList.size(); i++) {
						mydArray[i] = mydList.get(i).getName();
					}
				}
				adapter = new ArrayAdapter<String>(TdActivity.this,
						android.R.layout.simple_spinner_item, mydArray);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				tdlySpinner.setAdapter(adapter);
				tdlySpinner.setSelection(0, false);

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
		setContentView(R.layout.td);
		init();
		Event();
		new Thread() {
			public void run() {
				mydAction(mydUrl());
			};
		}.start();
	}

	private void init() {
		settingUtils = new SettingUtils(TdActivity.this);
		tdlySpinner = (Spinner) this.findViewById(R.id.tdly);
		tdbzEdit = (EditText) this.findViewById(R.id.tdbz);
		id = getIntent().getExtras().getString("id");
		gwId = getIntent().getExtras().getString("gwId");
		bmId = getIntent().getExtras().getString("bmId");
		loginname = getIntent().getExtras().getString("loginname");
	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						TdActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 退单按钮
		this.findViewById(R.id.tuidan).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						new Thread() {
							public void run() {
								if (checkData()) {
									gwtdAction(gwtdUrl());
								}
							};
						}.start();
					}
				});
		tdlySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				mydId = mydList.get(position).getId();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private void clearData() {
		tdbzEdit.setText("");
		tdbzEdit.setHint("请输入退单备注!");
	}

	private boolean checkData() {
		tdbz = tdbzEdit.getText().toString().trim();
		if (tdbz.length() == 0) {
			Toast.makeText(TdActivity.this, "请输入退单备注!", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	// 公文退单url
	private StringBuffer gwtdUrl() {
		StringBuffer gwtdUrl = new StringBuffer(SettingUtils.get("serverIp"));
		try {
			gwtdUrl.append(SettingUtils.get("gwsp_td_url")).append("id=")
					.append(id).append("&gwid=").append(gwId)
					.append("&loginname=").append(loginname).append("&bmid=")
					.append(bmId).append("&myd=").append(mydId)
					.append("&tdbeizhu=").append(UrlEncode.Encode2(tdbz));
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("公文退单url:" + gwtdUrl);
		return gwtdUrl;
	}

	private void gwtdAction(StringBuffer gwtdUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwtdUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);

				if (message.equals("success")) {
					handler.sendEmptyMessage(HandlerException.Success);
				} else if (message.equals("error")) {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	// 满意度url
	private StringBuffer mydUrl() {
		StringBuffer mydUrl = new StringBuffer(SettingUtils.get("serverIp"));
		mydUrl.append(SettingUtils.get("cxmyd_url"));
		System.out.println("满意度url:" + mydUrl);
		return mydUrl;
	}

	private void mydAction(StringBuffer mydUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					mydUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					mydList = new ArrayList<Myd>();
					Myd temMyd = new Myd();
					temMyd.setId("0");
					temMyd.setName("--请选择服务满意度--");
					mydList.add(temMyd);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						Myd myd = new Myd();
						myd.setId(obj.getString("id"));
						myd.setName(obj.getString("name"));
						mydList.add(myd);

					}
					handler.sendEmptyMessage(HandlerException.AllSuccess);

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
			TdActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
