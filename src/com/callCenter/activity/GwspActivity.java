package com.callCenter.activity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.adapter.GwspAdapter;
import com.callCenter.entity.Gwsp;
import com.callCenter.entity.Type;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

public class GwspActivity extends Activity {
	private static EditText lsh;
	private static EditText kh_name;
	private static EditText kh_phone;
	private static EditText fwpname;
	private static String lshStr, nameStr, phoneStr, bslbStr = "",
			startTimeStr, endTimeStr, fwpnameStr;
	private static List<Type> bslbList;
	private static ArrayAdapter<String> bslbAdapter;
	private static String[] bslbArray;
	private static Spinner bslb;
	private static TextView startTimeText;
	private static TextView endTimeText;
	private Calendar calendar;
	private boolean isShow = false;
	private LinearLayout searchCondition;
	private static Context mContext;
	public static String loginname, uid;
	private SettingUtils settingUtils;
	private static ListView gwspListView;
	private static List<Gwsp> gwspList;
	private static GwspAdapter adpter;
	private static int selectPosition = 0;
	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				adpter = new GwspAdapter(mContext, gwspList);
				gwspListView.setAdapter(adpter);
				adpter.notifyDataSetChanged();
				dataClean();
				if (selectPosition >= 4) {
					gwspListView.setSelection(selectPosition);
				}
				break;
			case HandlerException.Fail:
				adpter = new GwspAdapter(mContext, gwspList);
				gwspListView.setAdapter(adpter);
				adpter.notifyDataSetChanged();
				dataClean();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.Over:
				new Thread() {
					public void run() {
						gwspAction(gwspUrl());
					};
				}.start();
				adpter = new GwspAdapter(mContext, gwspList);
				gwspListView.setAdapter(adpter);
				adpter.notifyDataSetChanged();
				break;

			case HandlerException.SuccessToo:
				if (bslbList != null) {
					bslbArray = new String[bslbList.size()];
					for (int i = 0; i < bslbList.size(); i++) {
						bslbArray[i] = bslbList.get(i).getName();
					}
				}
				bslbAdapter = new ArrayAdapter<String>(mContext,
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
		mContext = this;
		setContentView(R.layout.gwsp);
		init();
		Event();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		new Thread() {
			public void run() {
				gwspAction(gwspUrl());
			};
		}.start();
	}

	private void init() {
		lsh = (EditText) this.findViewById(R.id.lsh);
		kh_name = (EditText) this.findViewById(R.id.kh_name);
		kh_phone = (EditText) this.findViewById(R.id.kh_phone);
		fwpname = (EditText) this.findViewById(R.id.fwpname);
		bslb = (Spinner) this.findViewById(R.id.bslb);
		startTimeText = (TextView) this.findViewById(R.id.startTimeText);
		endTimeText = (TextView) this.findViewById(R.id.endTimeText);
		calendar = Calendar.getInstance();
		searchCondition = (LinearLayout) this
				.findViewById(R.id.searchCondition);
		settingUtils = new SettingUtils(GwspActivity.this);
		gwspListView = (ListView) this.findViewById(R.id.gwspListView);
		loginname = getIntent().getExtras().getString("loginname");
		uid = getIntent().getExtras().getString("uid");
		gwspList = new ArrayList<Gwsp>();
	}

	private void getCondition() {
		lshStr = lsh.getText().toString().trim();
		nameStr = kh_name.getText().toString().trim();
		phoneStr = kh_phone.getText().toString().trim();
		fwpnameStr = fwpname.getText().toString().trim();
		startTimeStr = startTimeText.getText().toString().trim();
		endTimeStr = endTimeText.getText().toString().trim();
		if (bslbStr.equals("0")) {
			bslbStr = "";
		}

	}

	private static void dataClean() {
		lshStr = "";
		nameStr = "";
		phoneStr = "";
		fwpnameStr = "";
		startTimeStr = "";
		endTimeStr = "";
		bslbStr = "";
		lsh.setText("");
		kh_name.setText("");
		kh_phone.setText("");
		fwpname.setText("");
		startTimeText.setText("");
		endTimeText.setText("");
		bslb.setSelection(0, false);

	}

	private boolean checkCondition() {
		if (lshStr.equals("") && nameStr.equals("") && phoneStr.equals("")
				&& bslbStr.equals("") && endTimeStr.equals("")
				&& startTimeStr.equals("") && fwpnameStr.equals("")) {
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
					public void onClick(View arg0) {
						GwspActivity.this.finish();

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
										gwspAction(conditionSearchUrl());
									}
								}.start();
							}

						}
					}
				});
		// 开始时间
		this.findViewById(R.id.startTime).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						new DatePickerDialog(GwspActivity.this,
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
		// 办事类别
		bslb.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				bslbStr = bslbList.get(position).getId();
				// Toast.makeText(GwspActivity.this, bslbStr + "",
				// Toast.LENGTH_LONG).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 结束时间
		this.findViewById(R.id.endTime).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						new DatePickerDialog(GwspActivity.this,
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

										endTimeText.setText(temp);
									}
								}, calendar.get(Calendar.YEAR), calendar
										.get(Calendar.MONDAY), calendar
										.get(Calendar.DAY_OF_MONTH)).show();

					}
				});

		searchCondition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				searchCondition.setVisibility(View.GONE);
				isShow = false;

			}
		});
		// listview 点击事件
		gwspListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectPosition = position;
				String tempId = gwspList.get(position).getId();
				String tempGwid = gwspList.get(position).getGwid();
				String bmId = gwspList.get(position).getBmId();
				Intent intent = new Intent(GwspActivity.this,
						GwspDetailActivity.class);
				intent.putExtra("id", tempId);
				intent.putExtra("gwid", tempGwid);
				intent.putExtra("loginname", loginname);
				intent.putExtra("uid", uid);
				intent.putExtra("bmId", bmId);
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

	// 公文审批条件查询
	private static StringBuffer conditionSearchUrl() {
		StringBuffer gwsptUrl = new StringBuffer(SettingUtils.get("serverIp"));
		try {
			gwsptUrl.append(SettingUtils.get("gwsp_list_url")).append("uid=")
					.append(uid).append("&lsh=").append(lshStr)
					.append("&khname=").append(UrlEncode.Encode2(nameStr))
					.append("&kh_tel=").append(phoneStr).append("&bslb=")
					.append(bslbStr).append("&starttime=").append(startTimeStr)
					.append("&wctimes=").append(endTimeStr).append("&fwpname=")
					.append(UrlEncode.Encode2(fwpnameStr));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("公文审批条件查询url:" + gwsptUrl);
		return gwsptUrl;
	}

	// 公文发起URL
	private static StringBuffer gwspUrl() {
		StringBuffer gwsptUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwsptUrl.append(SettingUtils.get("gwsp_list_url")).append("uid=")
				.append(uid);
		System.out.println("公文审批url:" + gwsptUrl);
		return gwsptUrl;
	}

	// 工单派发
	private static void gwspAction(StringBuffer gwsptUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwsptUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				message = message.replaceAll("null", "");
				JSONObject object = new JSONObject(message);
				JSONArray array = object.getJSONArray("contents");
				if (array.length() != 0) {

					gwspList.clear();
					for (int i = 0; i < array.length(); i++) {
						Gwsp gwsp = new Gwsp();
						JSONObject obj = array.getJSONObject(i);
						gwsp.setId(obj.getString("id"));
						gwsp.setGwid(obj.getString("gwid"));
						gwsp.setLsh(obj.getString("lsh"));
						gwsp.setKhName(obj.getString("kh_name"));
						gwsp.setKhAddress(obj.getString("kh_address"));
						gwsp.setTel(obj.getString("kh_tel"));
						gwsp.setJibie(obj.getString("jibie"));
						gwsp.setStartTime(obj.getString("starttime"));
						gwsp.setUsername(obj.getString("username"));
						gwsp.setJdbmname(obj.getString("jdbmname"));
						gwsp.setTimes(obj.getString("times"));
						gwsp.setWcTimes(obj.getString("wctimes"));
						gwsp.setZt(obj.getString("zt"));
						gwsp.setBmId(obj.getString("bmid"));
						gwsp.setFwpname(obj.getString("fwpname"));
						gwspList.add(gwsp);
					}
					handler.sendEmptyMessage(HandlerException.Success);
				} else {
					gwspList.clear();
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

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GwspActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
