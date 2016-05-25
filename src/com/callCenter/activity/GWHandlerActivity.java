package com.callCenter.activity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.callCenter.adapter.GwpfAdapter;
import com.callCenter.entity.Gwpf;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 待办公文
 * 
 * @author Administrator
 * 
 */
public class GWHandlerActivity extends Activity {
	private String nameStr, phoneStr, cbcsStr, startTimeStr, stateStr = "",
			isTdStr = "", jdbmStr = "";
	private EditText kh_name, kh_phone, cbcs;
	private TextView startTimeText;
	private Spinner state, isTd, jdbm;
	private String[] stateArray, isTdArray, jdbmArray;
	private ArrayAdapter<String> stateAdapter, isTdAdapter, jdbmAdapter;
	private LinearLayout searchCondition;
	private Calendar calendar;
	private boolean isShow = false;
	private String jdbmId;
	private List<Type> jdbmList;
	private SettingUtils settingUtils;
	private List<Gwpf> gwpfList;
	private ListView gwpfListView;
	private GwpfAdapter adapter;
	private String uid, loginname;
	private static int selectPosition = 0;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adapter = new GwpfAdapter(GWHandlerActivity.this, gwpfList);
				gwpfListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dataClean();
				if (selectPosition >= 4) {
					gwpfListView.setSelection(selectPosition);
				}
				break;
			case HandlerException.Fail:
				adapter = new GwpfAdapter(GWHandlerActivity.this, gwpfList);
				gwpfListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dataClean();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.SuccessToo:
				if (jdbmList != null) {
					jdbmArray = new String[jdbmList.size()];
					for (int i = 0; i < jdbmList.size(); i++) {
						jdbmArray[i] = jdbmList.get(i).getName();
					}
				}
				jdbmAdapter = new ArrayAdapter<String>(GWHandlerActivity.this,
						android.R.layout.simple_spinner_item, jdbmArray);
				jdbmAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				jdbm.setAdapter(jdbmAdapter);
				jdbm.setSelection(0, false);
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
		setContentView(R.layout.gwhandler);
		init();
		Event();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		new Thread() {
			public void run() {
				gwpfAction(gwpfUrl(uid));
			};
		}.start();
	}

	private void init() {
		calendar = Calendar.getInstance();
		settingUtils = new SettingUtils(GWHandlerActivity.this);
		uid = getIntent().getExtras().getString("uid");
		loginname = getIntent().getExtras().getString("loginname");
		gwpfListView = (ListView) this.findViewById(R.id.gwpfListView);
		searchCondition = (LinearLayout) this
				.findViewById(R.id.searchCondition);
		gwpfList = new ArrayList<Gwpf>();

		kh_name = (EditText) this.findViewById(R.id.kh_name);
		kh_phone = (EditText) this.findViewById(R.id.kh_phone);
		cbcs = (EditText) this.findViewById(R.id.cbcs);
		jdbm = (Spinner) this.findViewById(R.id.jdbm);
		startTimeText = (TextView) this.findViewById(R.id.startTimeText);
		state = (Spinner) this.findViewById(R.id.state);
		isTd = (Spinner) this.findViewById(R.id.isTd);
		stateArray = getResources().getStringArray(R.array.state);
		isTdArray = getResources().getStringArray(R.array.isTd);
		stateAdapter = new ArrayAdapter<String>(GWHandlerActivity.this,
				android.R.layout.simple_spinner_item, stateArray);
		isTdAdapter = new ArrayAdapter<String>(GWHandlerActivity.this,
				android.R.layout.simple_spinner_item, isTdArray);
		stateAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		state.setAdapter(stateAdapter);
		state.setSelection(0, false);
		isTdAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		isTd.setAdapter(isTdAdapter);
		isTd.setSelection(0, false);

	}

	private void getCondition() {
		nameStr = kh_name.getText().toString().trim();
		phoneStr = kh_phone.getText().toString().trim();
		cbcsStr = cbcs.getText().toString().trim();
		startTimeStr = startTimeText.getText().toString().trim();
		if (jdbmStr.equals("0")) {
			jdbmStr = "";
		}
		if (stateStr.equals("-1")) {
			stateStr = "";
		}
		if (isTdStr.equals("-1")) {
			isTdStr = "";
		}

	}

	private boolean checkCondition() {
		if (nameStr.equals("") && phoneStr.equals("") && cbcsStr.equals("")
				&& jdbmStr.equals("") && stateStr.equals("")
				&& isTdStr.equals("") && startTimeStr.equals("")) {
			return false;
		} else {
			return true;
		}

	}

	private void dataClean() {
		nameStr = "";
		phoneStr = "";
		cbcsStr = "";
		startTimeStr = "";
		stateStr = "";
		isTdStr = "";
		jdbmStr = "";
		kh_name.setText("");
		kh_phone.setText("");
		cbcs.setText("");
		startTimeText.setText("");
		jdbm.setSelection(0, false);
		isTd.setSelection(0, false);
		state.setSelection(0, false);
	}

	// 按钮事件
	private void Event() {

		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						GWHandlerActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 搜索
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
									txlDataAction(txlUrl());
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
										gwpfAction(gwpfSearchUrl(uid));
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
		// 接单部门
		jdbm.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				jdbmStr = jdbmList.get(position).getId();
				// Toast.makeText(GWHandlerActivity.this, jdbmId + "",
				// Toast.LENGTH_LONG).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 开始时间
		this.findViewById(R.id.startTime).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						new DatePickerDialog(GWHandlerActivity.this,
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
		// 状态
		state.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				String selectStr = stateArray[position];
				// String selectId;

				if (selectStr.equals("--请选择--")) {
					stateStr = "-1";
				} else if (selectStr.equals("未受理")) {
					stateStr = "0";
				} else if (selectStr.equals("已受理")) {
					stateStr = "1";
				} else {
					stateStr = "2";
				}
				// Toast.makeText(GWHandlerActivity.this, selectId,
				// Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 是否是退单
		isTd.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				String selectStr = isTdArray[position];
				// String selectId;
				if (selectStr.equals("--请选择--")) {
					isTdStr = "-1";
				} else if (selectStr.equals("是")) {
					isTdStr = "1";
				} else {
					isTdStr = "0";
				}
				// Toast.makeText(GWHandlerActivity.this, selectId,
				// Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// listView 点击
		gwpfListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectPosition = position;
				String pfId = gwpfList.get(position).getId();
				String zt = gwpfList.get(position).getZt();
				if (zt.equals("0")) {
					Intent intent = new Intent(GWHandlerActivity.this,
							GwpfDetailActivity.class);
					intent.putExtra("pfId", pfId);
					intent.putExtra("uid", uid);
					intent.putExtra("loginname", loginname);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				} else if (zt.equals("1")) {
					Toast.makeText(GWHandlerActivity.this, "该公文已被派发!",
							Toast.LENGTH_LONG).show();
				}

			}
		});

	}

	/*---------------------------获取转办部门-----------------------*/
	// 通讯录 url
	private StringBuffer txlUrl() {
		StringBuffer txlUrl = new StringBuffer(SettingUtils.get("serverIp"));
		txlUrl.append(SettingUtils.get("query_bm2_url")).append("uid=")
				.append(uid);
		return txlUrl;
	}

	// 通讯录 action
	private void txlDataAction(StringBuffer txlUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					txlUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					jdbmList = new ArrayList<Type>();
					Type type1 = new Type();
					type1.setId("0");
					type1.setName("--请选择--");
					jdbmList.add(type1);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = (JSONObject) array.get(i);
						Type type = new Type();
						type.setId(obj.getString("id"));
						type.setName(obj.getString("bmname"));
						jdbmList.add(type);
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
	private StringBuffer gwpfSearchUrl(String uid) {
		StringBuffer gwpfUrl = new StringBuffer(SettingUtils.get("serverIp"));
		try {
			gwpfUrl.append(SettingUtils.get("gwpf_list_url")).append("uid=")
					.append(uid).append("&khname=")
					.append(UrlEncode.Encode2(nameStr)).append("&kh_tel=")
					.append(phoneStr).append("&starttime=")
					.append(startTimeStr).append("&bmid=").append(jdbmStr)
					.append("&zt=").append(stateStr).append("&tuidan=")
					.append(isTdStr).append("&cuiban=").append(cbcsStr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("公文收发条件搜索url:" + gwpfUrl);
		return gwpfUrl;
	}

	// 公文发起URL
	private StringBuffer gwpfUrl(String uid) {
		StringBuffer gwpfUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwpfUrl.append(SettingUtils.get("gwpf_list_url")).append("uid=")
				.append(uid);
		System.out.println("公文收发url:" + gwpfUrl);
		return gwpfUrl;
	}

	// 工单派发
	private void gwpfAction(StringBuffer gwpfUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwpfUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				message = message.replaceAll("null", "");
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {

					gwpfList.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						Gwpf gwpf = new Gwpf();
						gwpf.setId(obj.getString("id"));
						gwpf.setKhName(obj.getString("kh_name"));
						gwpf.setStartTime(obj.getString("starttime"));
						gwpf.setTime(obj.getString("times"));
						gwpf.setZt(obj.getString("zt"));
						gwpf.setJibie(obj.getString("jibie"));
						gwpf.setCuiban(obj.getString("cuiban"));
						gwpf.setLsh(obj.getString("lsh"));
						gwpf.setJdbmname(obj.getString("jdbmname"));
						gwpf.setBs(obj.getString("bs"));
						gwpf.setTuidan(obj.getString("tuidan"));
						gwpf.setFwpname(obj.getString("fwpname"));
						gwpfList.add(gwpf);
					}
					handler.sendEmptyMessage(HandlerException.Success);
				} else {
					gwpfList.clear();
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GWHandlerActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
