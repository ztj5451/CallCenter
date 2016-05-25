package com.callCenter.activity;

import java.util.ArrayList;
import java.util.List;

import com.callCenter.adapter.ReceiveMessageAdapter;
import com.callCenter.database.DBHelper;
import com.callCenter.database.MessageDB;
import com.callCenter.entity.TzMessage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MessageActivity extends Activity {
	private TextView ggContentTextView;
	private DBHelper dbHelper;
	private SQLiteDatabase database;
	private ListView receiveList;
	private ReceiveMessageAdapter adapter;
	private List<TzMessage> tzList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message);
		init();
		Event();

		if (getMessageList() != null) {
			adapter = new ReceiveMessageAdapter(MessageActivity.this, tzList);
			receiveList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		} else {
//			Toast.makeText(MessageActivity.this, "您没有未读的通知!", Toast.LENGTH_LONG)
//					.show();
		}

	}

	// 初始化
	private void init() {
		ggContentTextView = (TextView) this.findViewById(R.id.ggContent);
		dbHelper = new DBHelper(MessageActivity.this);
		receiveList = (ListView) this.findViewById(R.id.receive_list);
		ggContentTextView.setText(getIntent().getExtras()
				.getString("ggContent"));

	}

	// 获取通知公告列表
	public List<TzMessage> getMessageList() {
		database = dbHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery("SELECT * FROM " + DBHelper.MESSAGE,
				null);
		if (cursor.getCount() != 0) {
			tzList = new ArrayList<TzMessage>();
			while (cursor.moveToNext()) {
				TzMessage message = new TzMessage();
				message.setId(cursor.getString(cursor
						.getColumnIndex(MessageDB.ID)));
				message.setTitle(cursor.getString(cursor
						.getColumnIndex(MessageDB.TITLE)));
				message.setTime(cursor.getString(cursor
						.getColumnIndex(MessageDB.TIME)));
				message.setBm(cursor.getString(cursor
						.getColumnIndex(MessageDB.BM)));
				message.setFlag(cursor.getString(cursor
						.getColumnIndex(MessageDB.FLAG)));
				tzList.add(message);

			}
			return tzList;

		}
		return null;
	}

	// 监听事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						MessageActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});

		// 添加通知点击事件
		receiveList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				String gg_id = tzList.get(position).getId();
				startActivity(new Intent(MessageActivity.this,
						ReceiveMessageActivity.class).putExtra("messageId",
						gg_id));

				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
	}

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MessageActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
