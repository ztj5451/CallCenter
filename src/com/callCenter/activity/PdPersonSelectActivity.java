package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.adapter.SelectPersonAdapter;
import com.callCenter.adapter.SelectPersonAdapter.ViewHolder;
import com.callCenter.entity.KeyAndValue;
import com.callCenter.entity.SelectPerson;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ListView;

public class PdPersonSelectActivity extends Activity {
	private String id, loginName, hzryId = null;
	private SettingUtils settingUtils;
	private String jsonArray;
	private StringBuffer selectId;
	private ListView selectListView;
	private SelectPersonAdapter adapter;
	private List<SelectPerson> selectPersonList;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				Toast.makeText(PdPersonSelectActivity.this, "派单成功!",
						Toast.LENGTH_LONG).show();
				PdPersonSelectActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);
				break;
			case HandlerException.Fail:
				Toast.makeText(PdPersonSelectActivity.this, "派单失败!",
						Toast.LENGTH_LONG).show();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.SelectPerson:
				adapter = new SelectPersonAdapter(PdPersonSelectActivity.this,
						selectPersonList);
				selectListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
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
		setContentView(R.layout.pd_person_select);
		init();
		Event();

	}

	private void init() {
		loginName = getIntent().getExtras().getString("loginname");
		id = getIntent().getExtras().getString("id");
		jsonArray = getIntent().getExtras().getString("jsonArray");
		selectListView = (ListView) this.findViewById(R.id.selectListView);
		try {
			JSONArray array = new JSONArray(jsonArray);
			if (array.length() != 0) {
				selectPersonList = new ArrayList<SelectPerson>();
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					SelectPerson selectPerson = new SelectPerson();
					selectPerson.setUserId(object.getString("user_id"));
					selectPerson.setLoginName(object.getString("loginname"));
					selectPerson.setUserName(object.getString("username"));
					selectPerson.setZhiwei(object.getString("zhiwei"));
					selectPersonList.add(selectPerson);

				}
				handler.sendEmptyMessage(HandlerException.SelectPerson);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		settingUtils = new SettingUtils(PdPersonSelectActivity.this);
	}

	private void Event() {
		// 返回
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						PdPersonSelectActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 派单
		this.findViewById(R.id.pd).setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				HashSet<String> ids = SelectPersonAdapter.selectId;
				HashSet<String> names = SelectPersonAdapter.selectName;
				HashMap<String, String> selected = SelectPersonAdapter.selected;
				selectId = new StringBuffer();
				StringBuffer selectName = new StringBuffer();
				if (selected.size() != 0) {
					Set set = selected.keySet();

					for (Object key : set) {
						selectId.append(key).append("@");
						selectName.append(selected.get(key)).append("@");
					}

					final String[] tempIds = selectId.toString().split("@");
					String[] tempName = selectName.toString().split("@");
					if (tempIds.length == 1) {
						new Thread() {
							public void run() {
								pdSelectPersonAction(pdSelectPersonUrl(tempIds[0]));
							};
						}.start();
					} else {
						alertHzry(tempIds, tempName);
					}
				} else {
					Toast.makeText(PdPersonSelectActivity.this, "请选择接单人员!",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		// 人员选择
		selectListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				ViewHolder views = (ViewHolder) view.getTag();
				views.select.toggle();
				SelectPersonAdapter.selectMap.put(position,
						views.select.isChecked());

				adapter.notifyDataSetChanged();

			}
		});
	}

	private void alertHzry(final String[] tempId, String[] tempName) {
		View view = LayoutInflater.from(this).inflate(R.layout.gender, null);
		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

		for (int i = 0; i < tempId.length; i++) {
			RadioButton radio = new RadioButton(this);
			radio.setText(tempName[i]);
			radio.setId(i);
			radio.setHeight(50);
			radioGroup.addView(radio);
		}
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkId) {
				group.getChildCount();
				hzryId = tempId[checkId];
				// Toast.makeText(PdPersonSelectActivity.this, hzryId + "",
				// Toast.LENGTH_LONG).show();
				// Toast.makeText(PdPersonSelectActivity.this, checkId + "",
				// Toast.LENGTH_LONG).show();

			}
		});
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder.setCancelable(false);
		localBuilder.setTitle("请选择回执人员");
		localBuilder.setView(view);
		localBuilder.setPositiveButton("确认",
				new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						if (hzryId != null) {
							new Thread() {
								public void run() {
									pdSelectPersonAction(pdSelectPersonUrl(hzryId));
								};
							}.start();
						} else {
							Toast.makeText(PdPersonSelectActivity.this,
									"请选择回执人员!", Toast.LENGTH_LONG).show();
						}
					}
				});
		localBuilder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {

					}
				});
		localBuilder.show();
	}

	// 公文发起URL
	private StringBuffer pdSelectPersonUrl(String checkId) {
		StringBuffer pdSelectPersonUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		pdSelectPersonUrl.append(SettingUtils.get("gwpf_pf_url"))
				.append("gw_zb_id=").append(id).append("&uids=")
				.append(selectId).append("&checkid=").append(checkId)
				.append("&loginname=").append(loginName);
		System.out.println("派发Url:" + pdSelectPersonUrl);
		return pdSelectPersonUrl;
	}

	// 工单派发
	private void pdSelectPersonAction(StringBuffer pdSelectPersonUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					pdSelectPersonUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				if (message.equals("success")) {
					handler.sendEmptyMessage(HandlerException.Success);
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

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			PdPersonSelectActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
