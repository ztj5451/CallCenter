package com.callCenter.activity;

import com.callCenter.database.DBHelper;
import com.callCenter.database.MessageDB;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiveMessageActivity extends Activity {
	private String messageId;
	private TextView titleView, messageView, userView, timeView, bmView;
	private SQLiteDatabase database;
	private String title, messageContent, bm, user, time;
	private DBHelper dbHelper;
	private String flag;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				titleView.setText(title);
				messageView.setText(messageContent);
				bmView.setText(bm);
				userView.setText(user);
				timeView.setText(time);
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
		setContentView(R.layout.receive_message);
		// 初始化
		init();
		// 添加监听
		Event();
		getDataFromDatabase(messageId);
		updateHasReadMessage(messageId);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// 通知消息已读
		// if (flag.equals("yes")) {
		//
		// getDataFromDatabase(messageId);
		// // 通知消息未读
		// } else if (flag.equals("no")) {
		// // 从数据库中获取数据
		// new Thread() {
		// public void run() {
		// getMessageFromIntent2Database();
		// };
		// }.start();
		// }

	}

	// 初始化
	private void init() {
		// flag = getIntent().getExtras().getString("flag");
		messageId = getIntent().getExtras().getString("messageId");
		titleView = (TextView) this.findViewById(R.id.title);
		messageView = (TextView) this.findViewById(R.id.message);
		userView = (TextView) this.findViewById(R.id.user);
		timeView = (TextView) this.findViewById(R.id.time);
		bmView = (TextView) this.findViewById(R.id.bm);
		dbHelper = new DBHelper(ReceiveMessageActivity.this);

	}

	// 添加事件
	private void Event() {
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						ReceiveMessageActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
	}

	// 从数据库中获取对应的通知
	private void getDataFromDatabase(String messageId) {
		try {
			database = dbHelper.getWritableDatabase();
			Cursor data = database.rawQuery("SELECT * FROM " + DBHelper.MESSAGE
					+ " WHERE id=? ORDER BY " + MessageDB.TIME,
					new String[] { messageId });
			if (data.getCount() != 0) {
				while (data.moveToNext()) {
					titleView.setText(data.getString(data
							.getColumnIndex(MessageDB.TITLE)));
					messageView.setText(data.getString(data
							.getColumnIndex(MessageDB.MESSAGE)));
					userView.setText(data.getString(data
							.getColumnIndex(MessageDB.USER)));
					timeView.setText(data.getString(data
							.getColumnIndex(MessageDB.TIME)));
					bmView.setText(data.getString(data
							.getColumnIndex(MessageDB.BM)));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
	}

	// 更新已读通知库
	private void updateHasReadMessage(String messageId) {
		database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("flag", "yes");
		database.update(DBHelper.MESSAGE, values, "id=?",
				new String[] { messageId });

	}

	// 消息的URL
	// private StringBuffer getMessageUrl() {
	// StringBuffer messageUrl = new StringBuffer(SharePrefenceUtils.getValue(
	// "serverIp", ReceiveMessageActivity.this));
	// messageUrl.append(SettingUtils.get("message_detail_url")).append("id=")
	// .append(messageId);
	// System.out.println("URL:" + messageUrl.toString());
	// return messageUrl;
	//
	// }

	// 从网络上获取接受的消息 保存到数据库中
	// private void getMessageFromIntent2Database() {
	// try {
	//
	// HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
	// getMessageUrl().toString(), "GET");
	// if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	//
	// } else {
	// InputStream inputStream = httpURLConnection.getInputStream();
	// String message = HttpConnectUtil.getHttpStream(inputStream);
	// JSONObject object = new JSONObject(message);
	// id = object.getString("id");
	// title = object.getString("title");
	// messageContent = object.getString("mag");
	// bm = object.getString("bm");
	// user = object.getString("user");
	// time = object.getString("time");
	// ContentValues values = new ContentValues();
	// database = dbHelper.getWritableDatabase();
	// values.put(MessageDB.ID, id);
	// values.put(MessageDB.TITLE, title);
	// values.put(MessageDB.MESSAGE, messageContent);
	// values.put(MessageDB.BM, bm);
	// values.put(MessageDB.USER, user);
	// values.put(MessageDB.TIME, time);
	// values.put(MessageDB.FLAG, "yes");
	// database.update(DBHelper.MESSAGE, values, "id=?",
	// new String[] { id });
	// handler.sendEmptyMessage(0);
	//
	// }
	//
	// } catch (MalformedURLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ProtocolException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	// 返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ReceiveMessageActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
