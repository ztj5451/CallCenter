package com.callCenter.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import org.json.JSONException;
import org.json.JSONObject;
import com.callCenter.entity.LoginUser;
import com.callCenter.utils.EditTextTool;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 系统登陆Activity
 * 
 * @author Administrator
 * 
 */
public class LoginActivity extends Activity {
	private EditText userNameEditText, passWordEditText;
	private String userName, passWord;
	private Button rem_name, rem_password;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private SharedPreferences sPreferences;
	private SettingUtils settingUtils = null;
	private LoginUser loginUsers;
	private boolean save_n = false, save_p = false;
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case HandlerException.AllSuccess:
				progressDialog.dismiss();
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundle = new Bundle();
				bundle.putSerializable("loginUsers", (Serializable) loginUsers);
				intent.putExtras(bundle);
				startActivity(intent);
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.in_from_down, R.anim.out_to_up);

				break;
			case HandlerException.Success:
				progressDialog.dismiss();
				alertDialog();
				break;
			case HandlerException.NOUsername:
				progressDialog.dismiss();
				Toast.makeText(LoginActivity.this, "用户名不存在!",
						Toast.LENGTH_SHORT).show();

				break;
			case HandlerException.PassWordError:
				progressDialog.dismiss();
				Toast.makeText(LoginActivity.this, "密码错误!", Toast.LENGTH_SHORT)
						.show();
				break;
			case HandlerException.TellAdmin:
				progressDialog.dismiss();
				Toast.makeText(LoginActivity.this, "请联系管理员!",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				progressDialog.dismiss();
				Toast.makeText(LoginActivity.this, "登陆失败请重试！",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerException.ImeiOK:
				Toast.makeText(LoginActivity.this, "绑定成功!", Toast.LENGTH_SHORT)
						.show();
				Intent intent2 = new Intent(LoginActivity.this,
						MainActivity.class);
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundle2 = new Bundle();
				bundle2.putSerializable("loginUsers", (Serializable) loginUsers);
				intent2.putExtras(bundle2);
				startActivity(intent2);
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.in_from_down, R.anim.out_to_up);
				break;
			case HandlerException.ImeiFail:
				Toast.makeText(LoginActivity.this, "绑定失败!", Toast.LENGTH_SHORT)
						.show();
				break;
			case HandlerException.Fail:
				progressDialog.dismiss();
				Toast.makeText(LoginActivity.this, "登陆失败请重试！",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		// 初始化基本组件信息
		init();
		// check事件
		RemUserInfo();
		// 按钮事件
		event();
	}

	// 初始化方法
	@SuppressLint("NewApi")
	private void init() {
		settingUtils = new SettingUtils(LoginActivity.this);
		userNameEditText = (EditText) this.findViewById(R.id.user_name);
		passWordEditText = (EditText) this.findViewById(R.id.user_password);
		rem_name = (Button) this.findViewById(R.id.rem_username);
		rem_password = (Button) this.findViewById(R.id.rem_password);
		sPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		// 获取已保存的登陆用户名和密码
		get_userData();
		if (sPreferences.getString("imei", "").isEmpty()) {
			getIMEI();
		}

	}

	// 获取手机唯一标示IMEI
	private void getIMEI() {
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		Editor editor = sPreferences.edit();
		editor.putString("imei", telephonyManager.getDeviceId());
		editor.commit();

	}

	// 记住用户名和密码按钮事件
	private class CheckButtonClick implements OnClickListener {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.rem_username:

				if (!save_n) {
					save_n = true;
					rem_name.setBackgroundResource(R.drawable.check_checked);

				} else {
					save_n = false;
					rem_name.setBackgroundResource(R.drawable.check_normal);
				}

				break;
			case R.id.rem_password:
				if (!save_p) {
					save_p = true;
					rem_password
							.setBackgroundResource(R.drawable.check_checked);
				} else {
					save_p = false;
					rem_password.setBackgroundResource(R.drawable.check_normal);
				}

				break;

			default:
				break;
			}

		}

	}

	// 数据校验
	private Boolean check() {
		// 获取用户名与密码
		userName = userNameEditText.getText().toString().trim();
		passWord = passWordEditText.getText().toString().trim();
		if (userName == null || userName.equals("")) {

			EditTextTool.editShakeAndFocus(LoginActivity.this,
					userNameEditText, R.string.username_empty, 0);

			return false;
		} else if (passWord == null || passWord.equals("")) {

			EditTextTool.editShakeAndFocus(LoginActivity.this,
					passWordEditText, R.string.password_empty, 0);
			return false;
		}
		return true;

	}

	// 保存账户和密码
	private void save_userData() {
		Editor editor = sPreferences.edit();
		if (save_n) {
			editor.putString("userName", userName);
		} else {
			editor.putString("userName", "");
		}
		if (save_p) {
			editor.putString("passWord", passWord);
		} else {
			editor.putString("passWord", "");
		}
		editor.commit();

	}

	// 获取账户和密码

	private void get_userData() {
		if (!sPreferences.getString("userName", "").equals("")) {
			userNameEditText.setText(sPreferences.getString("userName", ""));
			rem_name.setBackgroundResource(R.drawable.check_checked);
			save_n = true;
		} else {
			rem_name.setBackgroundResource(R.drawable.check_normal);
			save_n = false;
		}
		if (!sPreferences.getString("passWord", "").equals("")) {
			passWordEditText.setText(sPreferences.getString("passWord", ""));
			rem_password.setBackgroundResource(R.drawable.check_checked);
			save_p = true;
		} else {
			rem_password.setBackgroundResource(R.drawable.check_normal);
			save_p = false;
		}

	}

	// 记住用户名
	public void RemUserInfo() {

		rem_name.setOnClickListener(new CheckButtonClick());
		rem_password.setOnClickListener(new CheckButtonClick());

	}

	// 登录URL
	private StringBuffer loginUrl() {
		StringBuffer loginUrl = new StringBuffer();
		loginUrl.append(SettingUtils.get("serverIp"))
				.append(SettingUtils.get("login_url")).append("loginname=")
				.append(userName).append("&password=").append(passWord)
				.append("&imei=").append(sPreferences.getString("imei", ""));
		System.out.println("登陆URL：" + loginUrl.toString());
		return loginUrl;
	}

	// IMEI URL
	private StringBuffer imeiUrl() {
		StringBuffer imeiUrl = new StringBuffer();
		imeiUrl.append(SettingUtils.get("serverIp"))
				.append(SettingUtils.get("imei_url")).append("loginname=")
				.append(userName).append("&imei=")
				.append(sPreferences.getString("imei", ""));
		return imeiUrl;
	}

	// 登陆actioni
	private void LoginAction(StringBuffer loginUrl) {

		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUrl, "GET");

			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				if (object.get("flag").equals(4)) {
					loginUsers = new LoginUser();
					loginUsers.setId(object.getString("id"));
					loginUsers.setUserName(object.getString("username"));
					loginUsers.setLoginName(object.getString("loginname"));
					loginUsers.setBm(object.getString("bmname"));
					loginUsers.setPic(object.getString("pic"));
					loginUsers.setTel(object.getString("tel"));
					loginUsers.setQq(object.getString("qq"));
					loginUsers.setAddress(object.getString("address"));
					loginUsers.setZhiwei(object.getString("zhiwei"));
					loginUsers.setGongsi(object.getString("gongsi"));
					loginUsers.setSex(object.getString("sx"));
					loginUsers.setUtype(object.getString("utype"));
					loginUsers.setDh(object.getString("dh"));

					handler.sendEmptyMessage(HandlerException.AllSuccess);

				} else if (object.get("flag").equals(3)) {
					handler.sendEmptyMessage(HandlerException.TellAdmin);
				} else if (object.get("flag").equals(2)) {
					loginUsers = new LoginUser();
					loginUsers.setId(object.getString("id"));
					loginUsers.setUserName(object.getString("username"));
					loginUsers.setLoginName(object.getString("loginname"));
					loginUsers.setBm(object.getString("bmname"));
					loginUsers.setPic(object.getString("pic"));
					loginUsers.setTel(object.getString("tel"));
					loginUsers.setQq(object.getString("qq"));
					loginUsers.setAddress(object.getString("address"));
					loginUsers.setZhiwei(object.getString("zhiwei"));
					loginUsers.setGongsi(object.getString("gongsi"));
					loginUsers.setSex(object.getString("sx"));
					loginUsers.setDh(object.getString("dh"));
					loginUsers.setUtype(object.getString("utype"));

					handler.sendEmptyMessage(HandlerException.Success);

				} else if (object.get("flag").equals(1)) {
					handler.sendEmptyMessage(HandlerException.PassWordError);
				} else if (object.get("flag").equals(0)) {
					handler.sendEmptyMessage(HandlerException.NOUsername);
				}

			}
		} catch (MalformedURLException e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		} catch (IOException e) {
			handler.sendEmptyMessage(HandlerException.Fail);
			e.printStackTrace();

		} catch (JSONException e) {
			handler.sendEmptyMessage(HandlerException.Fail);
			e.printStackTrace();
		}

	}

	// 绑定手机
	private void imeiAction(StringBuffer imeiUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					imeiUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {

			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				if (object.get("flag").equals(1)) {
					handler.sendEmptyMessage(HandlerException.ImeiOK);

				} else if (object.get("flag").equals(0)) {
					handler.sendEmptyMessage(HandlerException.ImeiFail);

				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 监听事件
	private void event() {
		// 登陆按钮
		this.findViewById(R.id.login_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						if (check()) {
							save_userData();

							progressDialog = ProgressDialog.show(
									LoginActivity.this, "登录中", "正在登录请稍后...",
									true, true);
							new Thread() {
								public void run() {
									LoginAction(loginUrl());
								};

							}.start();
							// Intent intent = new Intent(LoginActivity.this,
							// MainActivity.class);
							// startActivity(intent);
							// LoginActivity.this.finish();
							// overridePendingTransition(R.anim.in_from_down,
							// R.anim.out_to_up);

						}

					}
				});
		// 退出按钮
		this.findViewById(R.id.exit_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						LoginActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});

	}

	// 弹出是否进行手机绑定
	private void alertDialog() {
		alertDialog = new AlertDialog.Builder(LoginActivity.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.login_tip)
				.setMessage(R.string.login_tip_message)
				.setNegativeButton(R.string.app_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				.setPositiveButton(R.string.app_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 绑定imei
								new Thread() {
									public void run() {
										imeiAction(imeiUrl());
									};
								}.start();
								Intent intent = new Intent(LoginActivity.this,
										MainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								Bundle bundle = new Bundle();
								bundle.putSerializable("loginUsers",
										(Serializable) loginUsers);
								intent.putExtras(bundle);
								startActivity(intent);
								LoginActivity.this.finish();
								overridePendingTransition(R.anim.in_from_down,
										R.anim.out_to_up);
							}
						}).create();
		alertDialog.show();
	}

}
