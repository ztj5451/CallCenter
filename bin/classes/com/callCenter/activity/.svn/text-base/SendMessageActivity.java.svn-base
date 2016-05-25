package com.callCenter.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import com.callCenter.adapter.SendedMessageAdapter;
import com.callCenter.database.DBHelper;
import com.callCenter.database.MessageDB;
import com.callCenter.dialog.MMAlert;
import com.callCenter.entity.LoginUser;
import com.callCenter.entity.TzMessage;
import com.callCenter.utils.EditTextTool;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.TimeUtils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

public class SendMessageActivity extends Activity {
	private static final int MMAlert_DELETE_ALL = 0;
	private static final int MMAlert_EXIT = 1;
	private int is2Mobile = 1;
	private TabWidget tabWidget;
	private Button send;
	private ListView sendListView;
	private List<TzMessage> messageList;
	private SendedMessageAdapter adapter;
	private boolean flag = true;
	private boolean sendListFlag = false;
	private TabHost tabHost;
	private EditText receiver, messageContent, messageTitle;
	private String receiverPerson, message_Content, message_title, personId,
			personName, bmId, bmName;
	private Button sendToMobile;
	private LoginUser loginUser;
	private DBHelper dbHelper;
	private SQLiteDatabase database;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				saveMessage2Database();
				// 更新工作日志表中的 发送消息字段
				updateWorkLogDatabase();
				clearData();
				EditTextTool.showMessage(SendMessageActivity.this,
						R.string.sendMessageSuccess, 0);
				break;
			case HandlerException.Fail:
				EditTextTool.showMessage(SendMessageActivity.this,
						R.string.sendMessageFail, 0);
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
		setContentView(R.layout.sendmessage);
		// 初始化
		init();
		// 事件监听
		Event();

	}

	// 初始化
	private void init() {
		send = (Button) this.findViewById(R.id.sendMessage);
		dbHelper = new DBHelper(SendMessageActivity.this);
		loginUser = (LoginUser) getIntent().getExtras().getSerializable(
				"loginUser");

		receiver = (EditText) this.findViewById(R.id.receiver);
		messageContent = (EditText) this.findViewById(R.id.messageContent);
		messageTitle = (EditText) this.findViewById(R.id.messageTitle);
		sendToMobile = (Button) this.findViewById(R.id.sendToMobile);
		sendListView = (ListView) this.findViewById(R.id.send_list);
		tabHost = (TabHost) this.findViewById(R.id.tabhost);
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("sending").setIndicator("发送通知")
				.setContent(R.id.sending));
		tabHost.addTab(tabHost.newTabSpec("sended").setIndicator("已发送")
				.setContent(R.id.sended));
		tabWidget = tabHost.getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			// 修改显示字体大小
			TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(
					android.R.id.title);
			tv.setTextSize(15);
			tv.setTextColor(this.getResources().getColorStateList(
					android.R.color.white));
		}

	}

	// 按钮点击事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SendMessageActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
		// 标签点击事件
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("sending")) {
					send.setVisibility(View.VISIBLE);
					sendListFlag = false;

				} else if (tabId.equals("sended")) {
					sendListFlag = true;
					send.setVisibility(View.GONE);
					messageList = getSendedMessagetFromDatabase();
					if (messageList != null) {
						adapter = new SendedMessageAdapter(
								SendMessageActivity.this, messageList);
						sendListView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					} else {
						EditTextTool.showMessage(SendMessageActivity.this,
								R.string.sendList, 0);
					}
				}

			}
		});
		// 发送通知
		this.findViewById(R.id.sendMessage).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						if (sendMessageCheck()) {
							new Thread() {
								@Override
								public void run() {
									sendMessage(getSendMessageUrl());
								}
							}.start();
						}

					}
				});
		// 是否发送到手机
		sendToMobile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!flag) {
					sendToMobile
							.setBackgroundResource(R.drawable.check_checked);
					is2Mobile = 1;
					flag = true;

				} else {
					sendToMobile.setBackgroundResource(R.drawable.check_normal);
					is2Mobile = 0;
					flag = false;
				}

			}
		});

		// 人员选择
		this.findViewById(R.id.select).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Intent intent = new Intent(SendMessageActivity.this,
						// PersonSelectActivity.class);
						// startActivityForResult(intent,
						// RequestAndResultCode.PERSON_REQUEST);
						// overridePendingTransition(R.anim.in_from_right,
						// R.anim.out_to_left);

					}
				});
	}

	// 数据校验
	private boolean sendMessageCheck() {
		receiverPerson = receiver.getText().toString();
		message_Content = messageContent.getText().toString();
		message_title = messageTitle.getText().toString();

		if (receiverPerson.length() == 0) {
			EditTextTool.showMessage(SendMessageActivity.this,
					R.string.messageSelect, 0);
			return false;
		} else if (message_title.length() == 0) {
			EditTextTool.showMessage(SendMessageActivity.this,
					R.string.messageTitle, 0);
			return false;
		} else if (message_Content.length() == 0) {
			EditTextTool.showMessage(SendMessageActivity.this,
					R.string.messageContent, 0);
			return false;
		}
		return true;

	}

	// 发送通知url

	private StringBuffer getSendMessageUrl() {
		// StringBuffer messageUrl = new
		// StringBuffer(SharePrefenceUtils.getValue(
		// "serverIp", SendMessageActivity.this));
		// try {
		// messageUrl.append(SettingUtils.get("send_message_url"))
		// .append("bm_ids=").append(bmId).append("&user_ids=")
		// .append(personId).append("&user_id=")
		// .append(userDetail.getId()).append("&tel_bs=")
		// .append(is2Mobile).append("&title=")
		// .append(UrlEncode.Encode(message_title)).append("&mag=")
		// .append(UrlEncode.Encode(message_Content))
		// .append("&send_time=").append(TimeUtils.getDateTime());
		// } catch (UnsupportedEncodingException e) {
		//
		// e.printStackTrace();
		// }

		return new StringBuffer();
	}

	// 发送消息
	private void sendMessage(StringBuffer sendUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					sendUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {

			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				if (object.getString("flag").trim().equals("1")) {
					handler.sendEmptyMessage(HandlerException.Success);

				} else {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		}

	}

	// 发送通知保存到数据库中
	private void saveMessage2Database() {
		database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MessageDB.TITLE, message_title);
		values.put(MessageDB.MESSAGE, message_Content);
		values.put(MessageDB.USER, receiverPerson);
		values.put(MessageDB.TIME, TimeUtils.getLocationTime());
		values.put(MessageDB.FLAG, "send");
		database.insert(DBHelper.MESSAGE, null, values);
	}

	// 获取发送的通知
	private List<TzMessage> getSendedMessagetFromDatabase() {
		try {
			database = dbHelper.getReadableDatabase();
			Cursor message = database.rawQuery(
					"SELECT title,message,user,time,flag FROM "
							+ DBHelper.MESSAGE
							+ " WHERE flag= ? ORDER BY TABLE_ID DESC ",
					new String[] { "send" });
			if (message.getCount() != 0) {
				List<TzMessage> list = new ArrayList<TzMessage>();
				while (message.moveToNext()) {
					TzMessage msg = new TzMessage();
					msg.setTime(message.getString(message
							.getColumnIndex(MessageDB.TIME)));
					msg.setTitle(message.getString(message
							.getColumnIndex(MessageDB.TITLE)));
					msg.setMessage(message.getString(message
							.getColumnIndex(MessageDB.MESSAGE)));
					msg.setUser(message.getString(message
							.getColumnIndex(MessageDB.USER)));
					list.add(msg);
				}
				return list;

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			database.close();

		}
		return null;

	}

	// 发送成功后清空
	private void clearData() {
		receiver.setText("");
		receiver.setHint(R.string.messageSelect);
		messageContent.setText("");
		messageContent.setHint(R.string.messageContent);
		messageTitle.setText("");
		messageTitle.setHint(R.string.messageTitle);
		personId = "";
		personName = "";
		is2Mobile = 1;
		sendToMobile.setBackgroundResource(R.drawable.check_checked);

	}

	// 刷新
	private void refresh() {
		messageList.clear();
		getSendedMessagetFromDatabase();
		adapter.notifyDataSetChanged();

	}

	// 清空数据库
	private void deleteAllData() {
		database = dbHelper.getWritableDatabase();
		database.delete(DBHelper.MESSAGE, null, null);
		// 刷新
		refresh();

	}

	// 发送消息的数量保存到工作日志表中
	private void updateWorkLogDatabase() {
		database = dbHelper.getWritableDatabase();
		// Cursor cursor = database.rawQuery("SELECT id,send_message FROM "
		// + DBHelper.WORK_LOG, null);
		// if (cursor.getCount() != 0) {
		// cursor.moveToLast();
		// // 找到对应的ID
		// String id = cursor.getString(cursor.getColumnIndex(Work_Log.ID));
		// int num = cursor.getInt(cursor
		// .getColumnIndex(Work_Log.SEND_MESSAGE));
		// // 对此消息数量进行增加
		// num = num + 1;
		// // 使用ID 更新此条记录
		// ContentValues values = new ContentValues();
		// values.put(Work_Log.SEND_MESSAGE, num);
		// database.update(DBHelper.WORK_LOG, values, "id=?",
		// new String[] { id });

		// }
	}

	// 重写父类的方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// 选择人员
		// if (resultCode == RequestAndResultCode.PERSON_RESULT) {
		// personId = data.getExtras().getString("selectId");
		// personName = data.getExtras().getString("selectName");
		// bmId = data.getExtras().getString("bmId");
		// bmName = data.getExtras().getString("bmName");
		// receiver.setText(personName + bmName);
		// }

	}

	// 底部菜单
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// 当前标签是否切换到了发送列表
			if (sendListFlag) {
				// 打开数据库
				database = dbHelper.getWritableDatabase();
				if (database
						.rawQuery(
								"SELECT id FROM " + DBHelper.MESSAGE
										+ " WHERE FLAG=? ",
								new String[] { "send" }).getCount() != 0) {
					MMAlert.showAlert(SendMessageActivity.this,
							getString(R.string.sendMessage_manager),
							SendMessageActivity.this.getResources()
									.getStringArray(R.array.message_del_all),

							null, new MMAlert.OnAlertSelectId() {

								@Override
								public void onClick(int whichButton) {
									switch (whichButton) {
									case MMAlert_EXIT: {
										break;
									}
									case MMAlert_DELETE_ALL: {
										deleteAllData();
										break;
									}

									default:
										break;
									}

								}
							});

				}

			}

		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			SendMessageActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);

	}

}
