package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONObject;

import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RechargeDetailActivity extends Activity {
	private String loginname;
	private TextView name, cardId, address, phone, hasMoney, khId;
	private EditText moneyEditText;
	private String money;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				Toast.makeText(RechargeDetailActivity.this, "充值成功!",
						Toast.LENGTH_SHORT).show();
				clearData();
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
		setContentView(R.layout.recharge_detail);
		init();
		setValue();
		Event();
	}

	private void init() {
		loginname = getIntent().getExtras().getString("loginname");
		name = (TextView) this.findViewById(R.id.name);
		khId = (TextView) this.findViewById(R.id.kh_Id);
		cardId = (TextView) this.findViewById(R.id.cardId);
		address = (TextView) this.findViewById(R.id.address);
		phone = (TextView) this.findViewById(R.id.phone);
		hasMoney = (TextView) this.findViewById(R.id.hasMoney);
		moneyEditText = (EditText) this.findViewById(R.id.money);

	}

	private void setValue() {
		name.setText(getIntent().getExtras().getString("name"));
		khId.setText(getIntent().getExtras().getString("id"));
		cardId.setText(getIntent().getExtras().getString("cardid"));
		address.setText(getIntent().getExtras().getString("address"));
		phone.setText(getIntent().getExtras().getString("tel"));
		hasMoney.setText(getIntent().getExtras().getString("money"));
	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						RechargeDetailActivity.this.finish();

						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 充值
		this.findViewById(R.id.chargeMoney).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (checkData()) {
							confirmCharge();
						}

					}
				});
	}

	private boolean checkData() {
		money = moneyEditText.getText().toString().trim();
		if (money.length() == 0) {
			Toast.makeText(RechargeDetailActivity.this, "充值金额不能为空!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private void clearData() {
		moneyEditText.setText("");
	}

	private StringBuffer chargeUrl() {
		StringBuffer chargeUrl = new StringBuffer(SettingUtils.get("serverIp"));
		chargeUrl.append(SettingUtils.get("khcz_url")).append("id=")
				.append(getIntent().getExtras().getString("id"))
				.append("&qian=").append(money).append("&name=")
				.append(getIntent().getExtras().getString("name"))
				.append("&cardid=")
				.append(getIntent().getExtras().getString("cardid"))
				.append("&loginname=").append(loginname);
		System.out.println("充值url：" + chargeUrl);
		return chargeUrl;
	}

	// 发送消息
	private void chargeAction(StringBuffer chargeUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					chargeUrl, "GET");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}

	}

	// 用户设置密码
	private void confirmCharge() {

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				RechargeDetailActivity.this);
		builder.setCancelable(false);
		builder.setIcon(R.drawable.logo);
		builder.setTitle("温馨提示");
		builder.setMessage("您确定要进行该操作吗？");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int whichButton) {
				new Thread() {
					public void run() {
						chargeAction(chargeUrl());
					};
				}.start();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		builder.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			RechargeDetailActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
