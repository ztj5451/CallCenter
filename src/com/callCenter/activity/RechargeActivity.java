package com.callCenter.activity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import org.json.JSONObject;
import com.callCenter.adapter.RechargeAdapter;
import com.callCenter.entity.RechargeCustom;
import com.callCenter.utils.CheckUtils;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.PhoneUtils;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RechargeActivity extends Activity {
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private String loginname;
	private SettingUtils settingUtils;
	private EditText cardIdEditText, customerNameEdit, customerPhoneEdit;
	// private ListView customerListView;
	private String inputCardId, customerName, customerPhone;
	// private RechargeAdapter adapter;
	// private List<RechargeCustom> rechargeList;
	private RechargeCustom rechargeCustom;
	private TextView name, cardId, address, phone, hasMoney, khId;
	private EditText moneyEditText;
	private String money;
	private ScrollView khInfo;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				khInfo.setVisibility(View.VISIBLE);
				setValue();
				clearData();

				break;
			case HandlerException.SuccessToo:
				Toast.makeText(RechargeActivity.this, "充值成功!",
						Toast.LENGTH_SHORT).show();
				new Thread() {
					public void run() {
						searchAction(searchUrl());
					};
				}.start();
				clearMoney();
				break;
			case HandlerException.Fail:

				break;
			case HandlerException.NoResult:
				Toast.makeText(RechargeActivity.this, "请重新输入查询条件!",
						Toast.LENGTH_SHORT).show();
				khInfo.setVisibility(View.GONE);
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
		setContentView(R.layout.recharge);

		init();
		Event();
	}

	private void init() {
		loginname = getIntent().getExtras().getString("loginname");
		settingUtils = new SettingUtils(RechargeActivity.this);
		cardIdEditText = (EditText) this.findViewById(R.id.inputCardId);
		name = (TextView) this.findViewById(R.id.name);
		khId = (TextView) this.findViewById(R.id.kh_Id);
		cardId = (TextView) this.findViewById(R.id.cardId);
		address = (TextView) this.findViewById(R.id.address);
		phone = (TextView) this.findViewById(R.id.phone);
		hasMoney = (TextView) this.findViewById(R.id.hasMoney);
		moneyEditText = (EditText) this.findViewById(R.id.money);
		khInfo = (ScrollView) this.findViewById(R.id.khInfo);
		customerNameEdit = (EditText) this.findViewById(R.id.customerName);
		customerPhoneEdit = (EditText) this.findViewById(R.id.customerPhone);
		registerForContextMenu(phone);// 注册上下文菜单

	}

	private void Event() {
		// 返回
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						RechargeActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
		// 查询
		this.findViewById(R.id.search).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (checkData()) {
							new Thread() {
								public void run() {
									searchAction(searchUrl());
								};
							}.start();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.toggleSoftInput(0,
									InputMethodManager.HIDE_NOT_ALWAYS);
						}

					}
				});

		// 充值
		this.findViewById(R.id.chargeMoney).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (checkMoney()) {
							confirmCharge();
						}

					}
				});
		
	}

	private void setValue() {
		name.setText(rechargeCustom.getName());
		cardId.setText(rechargeCustom.getCardId());
		address.setText(rechargeCustom.getAddress());
		phone.setText(rechargeCustom.getTel());
		hasMoney.setText(rechargeCustom.getMoney());
		khId.setText(rechargeCustom.getId());
	}

	private boolean checkData() {
		inputCardId = cardIdEditText.getText().toString().trim();
		// if (!CheckUtils.isCardId(inputCardId)) {
		// Toast.makeText(RechargeActivity.this, "输入身份证号码不正确!",
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }
		customerName = customerNameEdit.getText().toString().trim();
		customerPhone = customerPhoneEdit.getText().toString().trim();
		if (customerName.equals("") && customerPhone.equals("")) {
			Toast.makeText(RechargeActivity.this, "请输入查询条件!",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		// else if (customerPhone.equals("")) {
		// Toast.makeText(RechargeActivity.this, "请输入客户电话!",
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }
		return true;
	}

	private boolean checkMoney() {
		money = moneyEditText.getText().toString().trim();
		if (!CheckUtils.isMoney(money)) {
			Toast.makeText(RechargeActivity.this, "您输入的充值金额不正确!",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void clearData() {
		cardIdEditText.setText("");
		cardIdEditText.setHint("输入证件号码");
		customerNameEdit.setText("");
		customerNameEdit.setHint("请输入客户姓名");
		customerPhoneEdit.setText("");
		customerPhoneEdit.setHint("请输入客户电话");
	}

	private StringBuffer searchUrl() {
		StringBuffer searchUrl = new StringBuffer(SettingUtils.get("serverIp"));
		try {
			searchUrl.append(SettingUtils.get("khcz_list_url"))
					.append("khname=").append(UrlEncode.Encode2(customerName))
					.append("&tel=").append(customerPhone);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("客户充值:" + searchUrl);
		return searchUrl;
	}

	private StringBuffer chargeUrl() {
		StringBuffer chargeUrl = new StringBuffer(SettingUtils.get("serverIp"));
		chargeUrl.append(SettingUtils.get("khcz_url")).append("id=")
				.append(rechargeCustom.getId()).append("&qian=").append(money)
				.append("&name=").append(rechargeCustom.getName())
				.append("&cardid=").append(rechargeCustom.getCardId())
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
					handler.sendEmptyMessage(HandlerException.SuccessToo);

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

	private void clearMoney() {
		moneyEditText.setText("");
	}

	// 用户设置密码
	private void confirmCharge() {

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				RechargeActivity.this);
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

	// 发送消息
	@SuppressLint("NewApi")
	private void searchAction(StringBuffer searchUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					searchUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				String temp = object.getString("contents");
				temp = temp.replaceAll("null", "");
				if (!temp.isEmpty()) {
					JSONObject obj = new JSONObject(temp);
					// rechargeList = new ArrayList<RechargeCustom>();
					rechargeCustom = new RechargeCustom();
					rechargeCustom.setId(obj.getString("khid"));
					rechargeCustom.setName(obj.getString("kh_name"));
					rechargeCustom.setAddress(obj.getString("kh_address"));
					rechargeCustom.setTel(obj.getString("kh_tel"));
					rechargeCustom.setCardId(obj.getString("kh_cardid"));
					rechargeCustom.setMoney(obj.getString("moneys"));
					// rechargeList.add(rechargeCustom);
					handler.sendEmptyMessage(HandlerException.Success);

				} else if (temp.equals("")) {
					handler.sendEmptyMessage(HandlerException.NoResult);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.NoResult);
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
			PhoneUtils.Tel(RechargeActivity.this, phone.getText().toString()
					.trim());
			break;
		case ITEM2:
			// 在这里添加处理代码
			ClipboardManager cmb = (ClipboardManager) this
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(phone.getText().toString().trim());
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			RechargeActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
