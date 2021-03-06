package com.callCenter.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import com.callCenter.adapter.GwStartAdapter;
import com.callCenter.adapter.SelectedMediaAdapter;
import com.callCenter.database.DBHelper;
import com.callCenter.database.Gw_Start;
import com.callCenter.dialog.MMAlert;
import com.callCenter.entity.CustomerGy;
import com.callCenter.entity.GwStart;
import com.callCenter.entity.LoginUser;
import com.callCenter.entity.Picture;
import com.callCenter.utils.CompressPicture;
import com.callCenter.utils.EditTextTool;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.JsonTool;
import com.callCenter.utils.PhoneUtils;
import com.callCenter.utils.RequestAndResultCode;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.TimeUtils;
import com.callCenter.utils.UrlEncode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class GWStartActivity extends Activity {
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private String bmId, bmName;
	private static final int MMALert_CAMERA = 0;
	private static final int MMALert_GALLERY = 1;
	private static final int MMALert_LOCAL_SDCARD = 2;
	private static final int MMALert_DEL = 0;
	private static final int MMALert_EXIT = 3;
	private SettingUtils settingUtils;
	private ProgressDialog progressDialog;
	private Calendar calendar;
	private TabHost tabHost;
	private LoginUser loginUser;
	private EditText receiverBm, receiverAttr, introduction, remarks,
			serverTime;
	private String bm, attr, intro, rem, server_time;
	private String kh_id, gdTitle, name, id, tempId, uId;
	private Spinner attrSpinners;
	private String[] spinner = { "--请选择属性--", "单位", "个人" };
	private ArrayAdapter<String> categoryAdapter;
	private TextView bzTextView, timeTextView, levelTextView, khjjTextView,
			gtndTextView, telTextView, addressTextView, ageTextView,
			sexTextView, nameTextView, moneyTextView, tdbeizhuTextView,
			shengTextView, shiTextView, quTextView, jiedaoTextView,
			shequTextView, zidingyiTextView, jjlxrTextView, jjlxrdhTextView;
	private LinearLayout tdLayout;
	private WebView waitWebView;
	private CustomerGy customer;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				// 成功清空数据
				clearData();
				// Toast.makeText(GWStartActivity.this, "工单派发成功!",
				// Toast.LENGTH_LONG).show();
				alert();
				break;
			case HandlerException.Fail:
				Toast.makeText(GWStartActivity.this, "该工单已派发，不能再次派发!",
						Toast.LENGTH_LONG).show();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.SuccessToo: {
				bzTextView.setText(customer.getBz());
				timeTextView.setText(customer.getTime());
				StringBuffer html = new StringBuffer();
				html.append("<html><head></head><body>");
				html.append(customer.getWait());
				html.append("</body></html>");
				System.out.println("打印:" + html.toString());
				waitWebView.loadDataWithBaseURL(null, html.toString(),
						"text/html", "utf-8", null);
				khjjTextView.setText(customer.getKhjj());
				gtndTextView.setText(customer.getGtnd());
				telTextView.setText(customer.getTel());
				addressTextView.setText(customer.getAddress());
				ageTextView.setText(customer.getAge());
				sexTextView.setText(customer.getSex());
				nameTextView.setText(customer.getName());
				moneyTextView.setText(customer.getMoney());
				tdbeizhuTextView.setText(customer.getTdbeizhu());
				shengTextView.setText(customer.getSheng());
				shiTextView.setText(customer.getShi());
				quTextView.setText(customer.getQu());
				jiedaoTextView.setText(customer.getJiedao());
				shequTextView.setText(customer.getShequ());
				zidingyiTextView.setText(customer.getZidingyi());
				jjlxrTextView.setText(customer.getJjlxr());
				jjlxrdhTextView.setText(customer.getJjlxrdh());
				if (customer.getTdtype().equals("0")) {
					tdLayout.setVisibility(View.GONE);
					levelTextView.setText(customer.getLevel());
				} else if (customer.getTdtype().equals("1")) {
					tdLayout.setVisibility(View.VISIBLE);
					levelTextView.setText(customer.getJibie());
				}

			}
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
		setContentView(R.layout.gwstart);
		// 初始化
		init();
		// 添加监听事件
		Event();
		new Thread() {
			public void run() {
				customDetailAction(customDetailUrl(kh_id));
			};
		}.start();
	}

	// 初始化
	private void init() {
		calendar = Calendar.getInstance();
		kh_id = getIntent().getExtras().getString("kh_id");
		// gdTitle = getIntent().getExtras().getString("gdTitle");
		name = getIntent().getExtras().getString("name");
		id = getIntent().getExtras().getString("id");
		loginUser = (LoginUser) getIntent().getExtras().getSerializable(
				"loginUser");

		settingUtils = new SettingUtils(GWStartActivity.this);
		// dbHelper = new DBHelper(GWStartActivity.this);
		receiverBm = (EditText) this.findViewById(R.id.receiverBm);
		receiverAttr = (EditText) this.findViewById(R.id.attribute);
		introduction = (EditText) this.findViewById(R.id.introduction);
		serverTime = (EditText) this.findViewById(R.id.serverTime);
		remarks = (EditText) this.findViewById(R.id.remarks);
		// 设置客户属性

		attrSpinners = (Spinner) this.findViewById(R.id.attrSpinner);
		categoryAdapter = new ArrayAdapter<String>(GWStartActivity.this,
				android.R.layout.simple_spinner_item, spinner);
		categoryAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		attrSpinners.setAdapter(categoryAdapter);
		attrSpinners.setSelection(0, false);
		bzTextView = (TextView) this.findViewById(R.id.bz);
		timeTextView = (TextView) this.findViewById(R.id.time);
		levelTextView = (TextView) this.findViewById(R.id.level);
		waitWebView = (WebView) this.findViewById(R.id.wait);
		waitWebView.getSettings().setDefaultTextEncodingName("utf-8");
		khjjTextView = (TextView) this.findViewById(R.id.khjj);
		gtndTextView = (TextView) this.findViewById(R.id.gtnd);
		telTextView = (TextView) this.findViewById(R.id.tel);
		addressTextView = (TextView) this.findViewById(R.id.address);
		ageTextView = (TextView) this.findViewById(R.id.age);
		sexTextView = (TextView) this.findViewById(R.id.sex);
		nameTextView = (TextView) this.findViewById(R.id.name);
		moneyTextView = (TextView) this.findViewById(R.id.money);
		tdbeizhuTextView = (TextView) this.findViewById(R.id.tdbeizhu);
		tdLayout = (LinearLayout) this.findViewById(R.id.tdLayout);
		shengTextView = (TextView) this.findViewById(R.id.sheng);
		shiTextView = (TextView) this.findViewById(R.id.shi);
		quTextView = (TextView) this.findViewById(R.id.qu);
		jiedaoTextView = (TextView) this.findViewById(R.id.jiedao);
		shequTextView = (TextView) this.findViewById(R.id.shequ);
		zidingyiTextView = (TextView) this.findViewById(R.id.zidingyi);
		jjlxrTextView = (TextView) this.findViewById(R.id.jjlxr);
		jjlxrdhTextView = (TextView) this.findViewById(R.id.jjlxrdh);
		registerForContextMenu(telTextView);

	}

	// 按钮点击事件
	private void Event() {
		attrSpinners.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				attr = spinner[position];
				receiverAttr.setText(spinner[position].toString());

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						GWStartActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
		// 公文发起
		this.findViewById(R.id.gwStart).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 数据进行校验
						if (dataCheck()) {
							new Thread() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									super.run();
									gwStartAction(gwStartUrl(kh_id));
								}
							}.start();
						}

					}
				});
		// 部门选择
		this.findViewById(R.id.selectBm).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(GWStartActivity.this,
								SelectTxlActivity.class);
						intent.putExtra("uid", loginUser.getId());
						startActivityForResult(intent, RequestAndResultCode.TXL);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					}
				});
		// 选择客户属性
		this.findViewById(R.id.selectAttr).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

					}
				});
		// 服务开始时间
		this.findViewById(R.id.selectTime).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						new DatePickerDialog(GWStartActivity.this,

						new DatePickerDialog.OnDateSetListener() {

							public void onDateSet(DatePicker view, int year,
									int monthOfYear,

									int dayOfMonth) {
								server_time = year + "-" + (monthOfYear + 1)
										+ "-" + dayOfMonth;
								serverTime.setText(year + "-"
										+ (monthOfYear + 1) + "-" + dayOfMonth);
								calendar.set(year, monthOfYear, dayOfMonth);

							}

						}, calendar.get(Calendar.YEAR),

						calendar.get(Calendar.MONTH), calendar
								.get(Calendar.DAY_OF_MONTH)).show();

					}
				});

	}

	// 数据校验
	private boolean dataCheck() {
		bm = receiverBm.getText().toString().trim();
		// attr = receiverAttr.getText().toString().trim();
		intro = introduction.getText().toString().trim();
		rem = remarks.getText().toString().trim();
		if (bm.length() == 0) {
			Toast.makeText(GWStartActivity.this, "接单部门不能为空!", Toast.LENGTH_LONG)
					.show();
			return false;

		}
		// else if (attr.length() == 0) {
		// Toast.makeText(GWStartActivity.this, "客户属性不能为空!", Toast.LENGTH_LONG)
		// .show();
		// return false;
		// }
		// else if (intro.length() == 0) {
		// Toast.makeText(GWStartActivity.this, "客户简介不能为空!", Toast.LENGTH_LONG)
		// .show();
		// return false;
		// } else if (rem.length() == 0) {
		// Toast.makeText(GWStartActivity.this, "客户备注不能为空!", Toast.LENGTH_LONG)
		// .show();
		// return false;
		// }
		return true;
	}

	// 发送成功后清空
	private void clearData() {
		receiverBm.setText("");
		receiverBm.setHint("请选择接收单位");
		receiverAttr.setText("");
		receiverAttr.setHint("客户属性");
		introduction.setText("");
		remarks.setText("");
		serverTime.setText("");
		serverTime.setHint("服务开始时间");
		attrSpinners.setSelection(0);
	}

	// 公文发起URLreceiverBm, receiverAttr, introduction, remarks,
	// /serverTime
	private StringBuffer gwStartUrl(String kh_id) {
		StringBuffer gwStartUrl = new StringBuffer(SettingUtils.get("serverIp"));
		try {
			gwStartUrl.append(SettingUtils.get("gw_start_url")).append("khid=")
					.append(customer.getId()).append("&jibie=")
					.append(UrlEncode.Encode2(gdTitle)).append("&name=")
					.append(UrlEncode.Encode2(name)).append("&bmid=")
					.append(bmId).append("&jianjie=")
					.append(UrlEncode.Encode2(intro)).append("&beizhu=")
					.append(UrlEncode.Encode2(rem)).append("&start=")
					.append(server_time).append("&loginname=")
					.append(loginUser.getLoginName()).append("&id=")
					.append(customer.getTempId()).append("&uid=")
					.append(customer.getuId());

		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("派单URL：" + gwStartUrl);
		return gwStartUrl;
	}

	// 工单派发
	private void gwStartAction(StringBuffer gwStartUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwStartUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);

				if (message.equals("success")) {
					handler.sendEmptyMessage(HandlerException.Success);
				} else if (message.equals("exsit")) {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	private StringBuffer customDetailUrl(String kh_id) {
		StringBuffer customDetailUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		customDetailUrl.append(SettingUtils.get("gw_detail_url")).append("id=")
				.append(kh_id);
		System.out.println("客服派单:" + customDetailUrl);
		return customDetailUrl;
	}

	@SuppressLint("NewApi")
	private void customDetailAction(StringBuffer customDetailUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					customDetailUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				message = message.replaceAll("null", "");
				JSONObject jsonObject = new JSONObject(message);
				// System.out.println("打印json:" + jsonObject);
				if (!jsonObject.getString("contents").isEmpty()) {
					String contents = jsonObject.getString("contents");
					JSONObject object = new JSONObject(contents);

					customer = new CustomerGy();
					customer.setTempId(object.getString("id"));
					customer.setuId(object.getString("uid"));
					customer.setId(object.getString("kh_id"));
					customer.setName(object.getString("kh_name"));
					customer.setAddress(object.getString("kh_address"));
					customer.setAge(object.getString("kh_age"));
					customer.setBz(object.getString("kh_bz"));
					customer.setGtnd(object.getString("kh_goutong"));
					customer.setKhjj(object.getString("kh_jj"));
					customer.setLevel(object.getString("gdtitle"));
					customer.setSex(object.getString("kh_sex"));
					customer.setTel(object.getString("kh_tel"));
					customer.setTime(object.getString("times"));
					customer.setWait(object.getString("bsjj"));
					customer.setMoney(object.getString("moneys"));
					customer.setTdbeizhu(object.getString("tdbeizhu"));
					customer.setTdtype(object.getString("tdtype"));
					customer.setSheng(object.getString("sheng"));
					customer.setShi(object.getString("shi"));
					customer.setQu(object.getString("qu"));
					customer.setJiedao(object.getString("jiedao"));
					customer.setShequ(object.getString("sq"));
					customer.setJjlxr(object.getString("jjlxr"));
					customer.setJjlxrdh(object.getString("kh_jinji_tel"));
					if (object.getString("tdtype").equals("1")) {
						gdTitle = object.getString("jibie");
					} else if (object.getString("tdtype").equals("0")) {
						gdTitle = object.getString("gdtitle");
					}
					customer.setJibie(object.getString("jibie"));
					handler.sendEmptyMessage(HandlerException.SuccessToo);
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

	private void alert() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder.setCancelable(false);
		localBuilder.setTitle("温馨提示");
		localBuilder.setMessage("公文派发成功，是否继续派发？");
		localBuilder.setPositiveButton("继续",
				new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {

					}
				});
		localBuilder.setNegativeButton("结束",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						new Thread() {
							public void run() {
								finishAction(finishUrl(id));

							};
						}.start();
						GWStartActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		localBuilder.show();
	}

	// 公文发起URL
	private StringBuffer finishUrl(String id) {
		StringBuffer finishUrl = new StringBuffer(SettingUtils.get("serverIp"));
		finishUrl.append(SettingUtils.get("gw_over_url")).append("id=")
				.append(id);
		System.out.println("完成URL：" + finishUrl);
		return finishUrl;
	}

	private void finishAction(StringBuffer finishUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					finishUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				// handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				System.out.println("返回信息:" + message);
				if (message.equals("success")) {
					// handler.sendEmptyMessage(HandlerException.Success);
					WorkOrderActivity.handler
							.sendEmptyMessage(HandlerException.Over);
				} else {
					// handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// handler.sendEmptyMessage(HandlerException.IOException);
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
				PhoneUtils.Tel(GWStartActivity.this, telTextView.getText().toString()
						.trim());
				break;
			case ITEM2:
				// 在这里添加处理代码
				ClipboardManager cmb = (ClipboardManager) this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setText(telTextView.getText().toString().trim());
				break;
			}
			return true;
		}
	
	// 重写父类的方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RequestAndResultCode.TXL) {
			bmId = data.getExtras().getString("bmId");
			bmName = data.getExtras().getString("bmName");
			receiverBm.setText(bmName);
		}

	}

}
