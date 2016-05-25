package com.callCenter.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.callCenter.adapter.*;
import com.callCenter.adapter.SelectMediaAdapter.ViewHolder;
import com.callCenter.database.DBHelper;
import com.callCenter.entity.Picture;
import com.callCenter.utils.SettingUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class QzclActivity extends Activity {
	private ListView mediaListView;
	private List<Picture> mediaList;
	private SelectMediaAdapter adapter;
	private String filePath;
	private SQLiteDatabase database;
	private DBHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.qzcl);
		// 初始化
		init();
		// 添加监听事件
		Event();

		getLocalMediaFile();
		if (mediaList != null) {
			adapter = new SelectMediaAdapter(QzclActivity.this, mediaList,
					filePath);
			mediaListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}

	}

	// 初始化
	private void init() {
		dbHelper = new DBHelper(QzclActivity.this);
		filePath = getIntent().getExtras().getString("dir_name").trim();
		mediaListView = (ListView) this.findViewById(R.id.mediaList);

	}

	// 事件监听
	private void Event() {
		// 返回按钮事件
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						QzclActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 确定按钮事件
		this.findViewById(R.id.select).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						HashSet<String> names = SelectMediaAdapter.selectName;
						StringBuffer imageNames = new StringBuffer();
						for (String name : names) {
							imageNames.append(name).append(",");

						}
						// Intent intent = new Intent();
						// intent.putExtra("imageNames", imageNames.toString());
						// setResult(RequestAndResultCode.QZCL_RESULT, intent);
						// QzclActivity.this.finish();
						// overridePendingTransition(R.anim.in_to_right,
						// R.anim.out_from_left);
					}
				});
		// 媒体列表添加点击事件
		mediaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ViewHolder viewHolder = (ViewHolder) view.getTag();

				viewHolder.select.toggle();
				SelectMediaAdapter.selectMap.put(position,
						viewHolder.select.isChecked());
				adapter.notifyDataSetChanged();

			}
		});
	}

	// 得到本地图片
	private void getLocalMediaFile() {
		File file = new File(SettingUtils.get(filePath));
		if (file.exists() && file.isDirectory()) {
			mediaList = new ArrayList<Picture>();
			// 列出所有图片
			File[] images = file.listFiles();
			for (File image : images) {

				if (queryDatabase(image)) {
					Picture picture = new Picture();
					picture.setImageName(image.getName());

					mediaList.add(picture);
				}
			}

		}

	}

	private boolean queryDatabase(File image) {
		database = dbHelper.getReadableDatabase();
		// if (database.rawQuery(
		// "SELECT * FROM " + DBHelper.PICTURE_TRAIL
		// + " WHERE image_name=?",
		// new String[] { image.getName() }).getCount() != 0) {
		// return true;
		// } else {
		// return false;
		// }
		return true;

	}
}
