package com.callCenter.activity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.callCenter.adapter.PictureListAdapter;
import com.callCenter.media.MediaManager;
import com.callCenter.utils.FileUtils;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.TimeUtils;
import com.callCenter.utils.UrlEncode;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 数据采集
 * 
 * @author Administrator
 * 
 */

public class DataAcquisitionActivity extends Activity {
	private ProgressDialog progressDialog;
	private ListView pictureListView;
	private EditText receiverEdit, remarksEdit;
	private String receiver, remarks;
	private List<String> pictureList;
	private SettingUtils settingUtils;
	private PictureListAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				progressDialog.dismiss();
				Toast.makeText(DataAcquisitionActivity.this, "上传成功!",
						Toast.LENGTH_LONG).show();
				clearData();
				break;
			case HandlerException.Fail:
				progressDialog.dismiss();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				progressDialog.dismiss();
				break;
			case HandlerException.IOException:
				progressDialog.dismiss();
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
		setContentView(R.layout.data_acquisition);
		init();
		Event();

	}

	// 初始化
	private void init() {
		receiverEdit = (EditText) this.findViewById(R.id.receiver);
		remarksEdit = (EditText) this.findViewById(R.id.remarks);
		pictureListView = (ListView) this.findViewById(R.id.pictureList);
		settingUtils = new SettingUtils(DataAcquisitionActivity.this);
		pictureList = new ArrayList<String>();
	}

	// 监听事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						DataAcquisitionActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 上传
		this.findViewById(R.id.uploadData).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (checkData()) {
							if (pictureList.size() != 0) {
								progressDialog = ProgressDialog.show(
										DataAcquisitionActivity.this, "温馨提示",
										"正在提交数据中...", true, false);
								new Thread() {
									public void run() {
										uploadDataAction();
									};
								}.start();
							}

						}
					}
				});
		// 获取图片
		this.findViewById(R.id.takePicture).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						MediaManager.cameraMethod(DataAcquisitionActivity.this,
								SettingUtils.get("image_dir"));

					}
				});
		// 图片列表
		pictureListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				String imageName = pictureList.get(position);
				// 判断map集合是否为空
				if (!pictureList.isEmpty()) {
					// 判断key是否存在
					if (pictureList.contains(imageName)) {
						// 若存在 则删除此map中和key对应的值
						pictureList.remove(imageName);
						FileUtils.deleteFile(new File(SettingUtils
								.get("image_dir"), imageName));
					}
				}
				// 点击该文件 怎进行删除
				// selectedMediaList.remove(position);
				adapter.notifyDataSetChanged();

			}
		});

	}

	private boolean checkData() {
		receiver = receiverEdit.getText().toString().trim();
		remarks = remarksEdit.getText().toString().trim();
		if (receiver.length() == 0) {
			Toast.makeText(DataAcquisitionActivity.this, "单位不能为空!",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (remarks.length() == 0) {
			Toast.makeText(DataAcquisitionActivity.this, "备注信息不能为空!",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (pictureList.size() == 0) {
			Toast.makeText(DataAcquisitionActivity.this, "请进行图片采集!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private void clearData() {
		receiverEdit.setText("");
		remarksEdit.setText("");

		FileUtils.deleteFile(new File(SettingUtils.get("image_dir")));
		pictureList.clear();
		adapter.notifyDataSetChanged();
	}

	// 公文发起URL
	private StringBuffer uploadDataUrl(String fileName, String sendTime) {

//		StringBuffer uploadDataUrl = new StringBuffer(
//				SettingUtils.get("serverIp"));
		//uploadDataUrl.append(SettingUtils.get("upload_image_url"));
		 StringBuffer uploadDataUrl = new StringBuffer(
		 "http://192.168.1.103:8080/MyIbatis/upload.jsp?");
		try {
			//uploadDataUrl.append(SettingUtils.get("upload_image_url"));
			uploadDataUrl.append("receiver=")
					.append(UrlEncode.Encode(receiver)).append("&remarks=")
					.append(UrlEncode.Encode(remarks)).append("&fileName=")
					.append(fileName).append("&flag=").append(sendTime);
		} catch (Exception e) {

		}
		System.out.println("上传图片url:" + uploadDataUrl);
		return uploadDataUrl;
	}

	private void uploadDataAction() {

		try {
			// 发送时间
			String sendTime = TimeUtils.getDateTime();
			for (int i = 0; i < pictureList.size();) {
				String imageName = pictureList.get(i);
				final Map<String, String> params = new HashMap<String, String>();
				final Map<String, File> files = new HashMap<String, File>();
				files.put(imageName, new File(SettingUtils.get("image_dir")
						+ "/" + imageName));
				params.put("file", imageName);
				String state = HttpConnectUtil.imageFilePost(
						uploadDataUrl(pictureList.get(i), sendTime).toString(),
						params, files);
				System.out.println("上传图片返回:" + state);
				// 判断是否是最后一张图片
				i++;
				if (i == pictureList.size()) {
					handler.sendEmptyMessage(HandlerException.Success);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			pictureList.add(MediaManager.image_name);
			adapter = new PictureListAdapter(DataAcquisitionActivity.this,
					pictureList);
			pictureListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DataAcquisitionActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
