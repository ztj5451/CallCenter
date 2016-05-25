package com.callCenter.activity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.frontia.api.FrontiaPushListener.DescribeMessageListener;
import com.callCenter.adapter.DdgwslAdapter;
import com.callCenter.adapter.ServerPersonJDAdapter;
import com.callCenter.entity.Ddgwsl;
import com.callCenter.entity.ServerPersonJD;
import com.callCenter.entity.Type;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Toast;

public class DdgwslActivity extends Activity {
	private String nameStr, phoneStr, startTimeStr, stateStr = "",
			bslbStr = "";

	private List<Type> bslbList;
	private ArrayAdapter<String> stateAdapter, bslbAdapter;
	private String[] stateArray, bslbArray;
	private Spinner state, bslb;
	private EditText kh_name, kh_phone;
	private TextView startTimeText;
	private Calendar calendar;
	private boolean isShow = false;
	private LinearLayout searchCondition;
	private ListView ddgwslListView;
	private SettingUtils settingUtils;
	private String loginname, uid;
	private DdgwslAdapter adapter;
	private List<Ddgwsl> ddgwslList;
	private int selectPosition = 0;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adapter = new DdgwslAdapter(DdgwslActivity.this, ddgwslList);
				ddgwslListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dataClean();
				if (selectPosition >= 4) {
					ddgwslListView.setSelection(selectPosition);
				}
				break;
			case HandlerException.Fail:
				adapter = new DdgwslAdapter(DdgwslActivity.this, ddgwslList);
				ddgwslListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dataClean();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.SuccessToo:
				if (bslbList != null) {
					bslbArray = new String[bslbList.size()];
					for (int i = 0; i < bslbList.size(); i++) {
						bslbArray[i] = bslbList.get(i).getName();
					}
				}
				bslbAdapter = new ArrayAdapter<String>(DdgwslActivity.this,
						android.R.layout.simple_spinner_item, bslbArray);
				bslbAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				bslb.setAdapter(bslbAdapter);
				bslb.setSelection(0, false);
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
		setContentView(R.layout.ddgwsl);
		init();
		Event();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread() {
			public void run() {
				ddgwslAction(ddgwslUrl());
			};
		}.start();
	}

	private void init() {
		stateArray = getResources().getStringArray(R.array.state2);
		state = (Spinner) this.findViewById(R.id.state);
		bslb = (Spinner) this.findViewById(R.id.bslb);
		kh_name = (EditText) this.findViewById(R.id.kh_name);
		kh_phone = (EditText) this.findViewById(R.id.kh_phone);
		startTimeText = (TextView) this.findViewById(R.id.startTimeText);
		calendar = Calendar.getInstance();
		searchCondition = (LinearLayout) this
				.findViewById(R.id.searchCondition);
		loginname = getIntent().getExtras().getString("loginname");
		uid = getIntent().getExtras().getString("uid");
		settingUtils = new SettingUtils(DdgwslActivity.this);
		ddgwslListView = (ListView) this.findViewById(R.id.ddgwslListView);
		ddgwslList = new ArrayList<Ddgwsl>();
		stateAdapter = new ArrayAdapter<String>(DdgwslActivity.this,
				android.R.layout.simple_spinner_item, stateArray);

		stateAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		state.setAdapter(stateAdapter);
		state.setSelection(0, false);

	}

	private void getCondition() {
		nameStr = kh_name.getText().toString().trim();
		phoneStr = kh_phone.getText().toString().trim();
		startTimeStr = startTimeText.getText().toString().trim();
		if (bslbStr.equals("0")) {
			bslbStr = "";
		}
		if (stateStr.equals("0")) {
			stateStr = "";
		}

	}

	private void dataClean() {
		nameStr = "";
		phoneStr = "";
		startTimeStr = "";
		stateStr = "";
		bslbStr = "";
		kh_name.setText("");
		kh_phone.setText("");
		startTimeText.setText("");
		bslb.setSelection(0, false);
		state.setSelection(0, false);
	}

	private boolean checkCondition() {
		if (nameStr.equals("") && phoneStr.equals("") && bslbStr.equals("")
				&& stateStr.equals("") && startTimeStr.equals("")) {
			return false;
		} else {
			return true;
		}

	}

	private void Event() {

		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DdgwslActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
		// 搜索按钮
		this.findViewById(R.id.search).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (!isShow) {
							searchCondition.setVisibility(View.VISIBLE);
							isShow = true;
							new Thread() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									super.run();
									bslbAction(bslbUrl());
								}
							}.start();
						} else {
							selectPosition = 0;
							searchCondition.setVisibility(View.GONE);
							isShow = false;
							getCondition();
							if (checkCondition()) {
								new Thread() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										super.run();
										ddgwslAction(conditionSearchUrl());
									}
								}.start();
							}

						}
					}
				});
		searchCondition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				searchCondition.setVisibility(View.GONE);
				isShow = false;

			}
		});
		// 服务开始时间
		this.findViewById(R.id.startTime).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						new DatePickerDialog(DdgwslActivity.this,
								new OnDateSetListener() {

									@Override
									public void onDateSet(
											DatePicker datePicker, int year,
											int monthOfYear, int dayOfMonth) {
										String tempMonth, tempDay;
										if ((monthOfYear + 1) <= 9) {
											tempMonth = "0" + (monthOfYear + 1);
										} else {
											tempMonth = (monthOfYear + 1) + "";
										}
										if (dayOfMonth <= 9) {
											tempDay = "0" + dayOfMonth;
										} else {
											tempDay = dayOfMonth + "";
										}
										final String temp = year + "-"
												+ tempMonth + "-" + tempDay;

										startTimeText.setText(temp);
									}
								}, calendar.get(Calendar.YEAR), calendar
										.get(Calendar.MONDAY), calendar
										.get(Calendar.DAY_OF_MONTH)).show();

					}
				});
		// 接单部门
		bslb.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				bslbStr = bslbList.get(position).getId();
				// Toast.makeText(DdgwslActivity.this, bslbStr + "",
				// Toast.LENGTH_LONG).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 状态
		state.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				String selectStr = stateArray[position];
				// String selectId;

				if (selectStr.equals("--请选择--")) {
					stateStr = "0";
				} else if (selectStr.equals("未完成")) {
					stateStr = "1";
				} else if (selectStr.equals("已完成")) {
					stateStr = "2";
				}
				Toast.makeText(DdgwslActivity.this, stateStr,
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 点击事件
		ddgwslListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectPosition = position;
				String gdId = ddgwslList.get(position).getId();
				Intent intent = new Intent(DdgwslActivity.this,
						JdDetailActivity.class);
				intent.putExtra("gdId", gdId);

				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
	}

	/*---------------------------办事类别-----------------------*/
	// 通讯录 url
	private StringBuffer bslbUrl() {
		StringBuffer bslbUrl = new StringBuffer(SettingUtils.get("serverIp"));
		bslbUrl.append(SettingUtils.get("bslb_url"));
		return bslbUrl;
	}

	// 通讯录 action
	private void bslbAction(StringBuffer bslbUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					bslbUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					bslbList = new ArrayList<Type>();
					Type type1 = new Type();
					type1.setId("0");
					type1.setName("--请选择--");
					bslbList.add(type1);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = (JSONObject) array.get(i);
						Type type = new Type();
						type.setId(obj.getString("id"));
						type.setName(obj.getString("name"));
						bslbList.add(type);
					}
					handler.sendEmptyMessage(HandlerException.SuccessToo);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);

		}

	}

	// 条件查询
	private StringBuffer conditionSearchUrl() {
		StringBuffer ddgwslUrl = new StringBuffer(SettingUtils.get("serverIp"));
		try {
			ddgwslUrl.append(SettingUtils.get("ddgwsl_list_url"))
					.append("uid=").append(uid).append("&khname=")
					.append(UrlEncode.Encode2(nameStr)).append("&kh_tel=")
					.append(phoneStr).append("&bslb=").append(bslbStr)
					.append("&starttime=").append(startTimeStr).append("&zt=")
					.append(stateStr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("调度公文受理条件查询url:" + ddgwslUrl);
		return ddgwslUrl;
	}

	// 公文发起URL
	private StringBuffer ddgwslUrl() {
		StringBuffer ddgwslUrl = new StringBuffer(SettingUtils.get("serverIp"));
		ddgwslUrl.append(SettingUtils.get("ddgwsl_list_url")).append("uid=")
				.append(uid);
		System.out.println("调度公文受理url:" + ddgwslUrl);
		return ddgwslUrl;
	}

	private void ddgwslAction(StringBuffer ddgwslUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					ddgwslUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				message = message.replaceAll("null", "");
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					ddgwslList.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						Ddgwsl ddgwsl = new Ddgwsl();
						ddgwsl.setId(obj.getString("id"));
						ddgwsl.setLsh(obj.getString("lsh"));
						ddgwsl.setKhName(obj.getString("kh_name"));
						ddgwsl.setAddress(obj.getString("kh_address"));
						ddgwsl.setTel(obj.getString("kh_tel"));
						ddgwsl.setTime(obj.getString("times"));
						ddgwsl.setStartTime(obj.getString("starttime"));
						ddgwsl.setHz(obj.getString("hz"));
						ddgwsl.setTuidan(obj.getString("tuidan"));
						ddgwsl.setZt(obj.getString("zt"));
						ddgwsl.setJibie(obj.getString("jibie"));
						ddgwslList.add(ddgwsl);
					}
					handler.sendEmptyMessage(HandlerException.Success);
				} else {
					ddgwslList.clear();
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		selectPosition = 0;
	}

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DdgwslActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
