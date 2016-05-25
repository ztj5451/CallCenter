package com.callCenter.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.callCenter.entity.LoginUser;
import com.callCenter.utils.EditTextTool;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.Md5Encrypt;
import com.callCenter.utils.PhoneUtils;
import com.callCenter.utils.SettingUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 系统设置Activity
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends Activity {
	private String downLoadFileName;
	private String version, url, filename;
	private SettingUtils settingUtils;
	private LoginUser loginUser;
	private EditText newPwd, confirmPwd, oldPwd;
	private String new_pwd, confirm_pwd, old_pwd;

	private boolean flag = false;
	private ProgressDialog progressDialog;
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case HandlerException.Success:
				progressDialog.dismiss();
				Toast.makeText(SettingActivity.this, "密码修改成功!",
						Toast.LENGTH_LONG).show();
				// 清空数据
				clearData();
				break;
			case HandlerException.Fail:
				progressDialog.dismiss();
				Toast.makeText(SettingActivity.this, "密码修改失败，请重试!",
						Toast.LENGTH_LONG).show();
				break;

			case 30:
				Toast.makeText(SettingActivity.this, "当前没有需要更新的版本",
						Toast.LENGTH_LONG).show();
				break;
			case 31:
				updateSoftWare();
				break;
			case 32:

				progressDialog.dismiss();
				Intent install = PhoneUtils.getAPK(SettingUtils.get("apk_dir")
						+ "/" + downLoadFileName);
				SettingActivity.this.startActivity(install);

				break;
			default:
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.systemsetting);
		init();
		Event();
	}

	// 初始化
	private void init() {
		progressDialog = new ProgressDialog(SettingActivity.this);
		loginUser = (LoginUser) getIntent().getExtras().getSerializable(
				"loginUser");
		newPwd = (EditText) this.findViewById(R.id.newPwd);
		confirmPwd = (EditText) this.findViewById(R.id.confirmPwd);
		oldPwd = (EditText) this.findViewById(R.id.oldPwd);
		settingUtils = new SettingUtils(SettingActivity.this);
	}

	// 密码修改成功 清空数据
	private void clearData() {
		newPwd.setText("");
		confirmPwd.setText("");
		oldPwd.setText("");
	}

	// 用户密码校验
	private boolean checkPwd() {
		new_pwd = newPwd.getText().toString().trim();
		confirm_pwd = confirmPwd.getText().toString().trim();
		old_pwd = oldPwd.getText().toString().trim();
		if (new_pwd.length() == 0) {
			EditTextTool.editShakeAndFocus(SettingActivity.this, newPwd,
					R.string.newPwd, 1);
			return false;
		} else if (confirm_pwd.length() == 0) {
			EditTextTool.editShakeAndFocus(SettingActivity.this, confirmPwd,
					R.string.confirmPwd, 1);
			return false;
		} else if (!confirm_pwd.equals(new_pwd)) {
			EditTextTool.editShakeAndFocus(SettingActivity.this, newPwd,
					R.string.pwdneq, 1);
			return false;
		} else if (old_pwd.length() == 0) {
			EditTextTool.editShakeAndFocus(SettingActivity.this, oldPwd,
					R.string.oldPwd, 1);
			return false;
		}
		return true;
	}

	// 按钮点击事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SettingActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
		// 修改密码
		this.findViewById(R.id.changePwd).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						// 显示或消失 输入修改密码
						if (!flag) {
							SettingActivity.this.findViewById(R.id.input)
									.setVisibility(View.VISIBLE);
							flag = true;
						} else {
							SettingActivity.this.findViewById(R.id.input)
									.setVisibility(View.GONE);
							flag = false;
						}
					}
				});
		// 修改
		this.findViewById(R.id.change).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (checkPwd()) {
							progressDialog = ProgressDialog
									.show(SettingActivity.this, "温馨提示",
											"密码修改中请稍候...");
							new Thread() {
								public void run() {
									changeUserPWD(getChangePwdUrl());
								};
							}.start();
						}
					}
				});
		// 取消
		this.findViewById(R.id.cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!flag) {
							SettingActivity.this.findViewById(R.id.input)
									.setVisibility(View.VISIBLE);
							flag = true;
						} else {
							SettingActivity.this.findViewById(R.id.input)
									.setVisibility(View.GONE);
							flag = false;
						}
					}
				});
		// 更新安装包
		this.findViewById(R.id.update_software).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						new Thread() {
							public void run() {
								softWareVersionCompared();
							};
						}.start();
					}
				});
		// 系统设置
		this.findViewById(R.id.system_setting).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(SettingActivity.this,
								SystemSettingActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);

					}
				});
	}

	// 修改密码
	private StringBuffer getChangePwdUrl() {
		StringBuffer pwdUrl = new StringBuffer(SettingUtils.get("serverIp"));
		pwdUrl.append(SettingUtils.get("change_pwd_url"))
				.append("oldPassword=").append(Md5Encrypt.md5(old_pwd))
				.append("&newPassword=").append(Md5Encrypt.md5(new_pwd))
				.append("&uid=").append(loginUser.getId());
		return pwdUrl;
	}

	// 修改密码网络连接
	private void changeUserPWD(StringBuffer pwdUrl) {

		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					pwdUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {

				// String message = HttpConnectUtil
				// .getHttpStream(httpURLConnection);
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				if (object.getString("flag").equals("1")) {
					handler.sendEmptyMessage(HandlerException.Success);
				} else {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		}

	}

	/******************* 手机端软件版本更新 ******************/
	// 获取更新软件版本url
	private StringBuffer getUpdateAPK_Url() {
		StringBuffer url = new StringBuffer(SettingUtils.get("serverIp"));
		url.append(SettingUtils.get("update_software_url"));
		return url;
	}

	// 软件版本比较
	private void softWareVersionCompared() {
		try {
			System.out.println("更新安装包URL:" + getUpdateAPK_Url());
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					getUpdateAPK_Url(), "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {

			} else {
				String message = HttpConnectUtil
						.getHttpStream(httpURLConnection);
				JSONObject object = new JSONObject(message);
				String newVersion = object.getString("version");
				url = object.getString("path");
				if (newVersion.compareToIgnoreCase(version) > 0) {
					String temp[] = url.split("/");
					filename = temp[temp.length - 1];
					System.out.println("文件名称：" + filename);
					handler.sendEmptyMessage(31);
				} else {
					handler.sendEmptyMessage(30);
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

	// 更新软件并安装
	private void updateSoftWare() {
		// progressDialog = ProgressDialog.show(SettingActivity.this, "提示",
		// "正在更新应用程序...");
		progressDialog(ProgressDialog.STYLE_SPINNER, "温馨提示", "正在下载最新安装包...");
		new Thread() {
			public void run() {
				downLoadSoftWare(filename);
			};
		}.start();
	}

	@SuppressWarnings("deprecation")
	private void progressDialog(int style, String title, String message) {

		progressDialog = new ProgressDialog(SettingActivity.this);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setProgressStyle(style);
		progressDialog.setCancelable(false);
		progressDialog.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.dismiss();
			}
		});
		progressDialog.show();

	}

	// 软件版本更新
	private void downLoadSoftWare(String fileName) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(url,
					"GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {

			} else {
				System.out.println("开始下载新安装包");
				InputStream inputStream = httpURLConnection.getInputStream();
				boolean success = HttpConnectUtil.downLoadFile(inputStream,
						fileName);
				if (success) {
					downLoadFileName = fileName;
					handler.sendEmptyMessage(32);

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(16);
		}

	}

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SettingActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
