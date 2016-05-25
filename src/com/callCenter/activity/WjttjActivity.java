package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONObject;

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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class WjttjActivity extends Activity {
	private EditText kh_nameEditText, kh_jpEditText, kh_qpEditText,
			cardIdEditText, kh_phoneEditText;
	private String kh_name, kh_jp, kh_qp, cardId, kh_phone;
	private TextView shengText, shiText, quText, jdText, sqText, zdyText,
			jgText, cjlbText, cjdjText, startTimeText, endTimeText;

	private String uid;
	private SettingUtils settingUtils;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:

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
		setContentView(R.layout.wjttj);
		init();
		Event();
	}

	private void init() {
		settingUtils = new SettingUtils(WjttjActivity.this);
		uid = getIntent().getExtras().getString("uid");
		kh_nameEditText = (EditText) this.findViewById(R.id.kh_name);
		kh_jpEditText = (EditText) this.findViewById(R.id.kh_jp);
		kh_qpEditText = (EditText) this.findViewById(R.id.kh_qp);
		cardIdEditText = (EditText) this.findViewById(R.id.cardId);
		kh_phoneEditText = (EditText) this.findViewById(R.id.kh_phone);

		shengText = (TextView) this.findViewById(R.id.shengText);
		shiText = (TextView) this.findViewById(R.id.shiText);
		quText = (TextView) this.findViewById(R.id.quText);
		jdText = (TextView) this.findViewById(R.id.jdText);
		sqText = (TextView) this.findViewById(R.id.sqText);
		zdyText = (TextView) this.findViewById(R.id.zdyText);
		jgText = (TextView) this.findViewById(R.id.jgText);
		cjlbText = (TextView) this.findViewById(R.id.cjlbText);
		cjdjText = (TextView) this.findViewById(R.id.cjdjText);
		startTimeText = (TextView) this.findViewById(R.id.startTimeText);
		endTimeText = (TextView) this.findViewById(R.id.endTimeText);
	}

	private void getValue() {
		kh_name = kh_nameEditText.getText().toString().trim();
		kh_jp = kh_jpEditText.getText().toString().trim();
		kh_qp = kh_qpEditText.getText().toString().trim();
		cardId = cardIdEditText.getText().toString().trim();
		kh_phone = kh_phoneEditText.getText().toString().trim();

	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						WjttjActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 搜索
		this.findViewById(R.id.search).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(WjttjActivity.this,
								WjttjSearchActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);

					}
				});
		// 省
		this.findViewById(R.id.sheng).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 市
		this.findViewById(R.id.shi).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 区
		this.findViewById(R.id.qu).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 街道
		this.findViewById(R.id.jiedao).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

					}
				});
		// 社区
		this.findViewById(R.id.sq).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 自定义
		this.findViewById(R.id.zdy).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 机构
		this.findViewById(R.id.jg).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 残疾类别
		this.findViewById(R.id.cjlb).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 残疾等级
		this.findViewById(R.id.cjdj).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 开始时间
		this.findViewById(R.id.startTime).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

					}
				});
		// 结束时间
		this.findViewById(R.id.endTime).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

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
				JSONObject object = new JSONObject("contents");

				if (message.equals("")) {
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
			WjttjActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
