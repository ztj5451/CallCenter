package com.callCenter.activity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.callCenter.adapter.PictureListAdapter;
import com.callCenter.dialog.MMAlert;
import com.callCenter.entity.GdFinish;
import com.callCenter.media.MediaManager;
import com.callCenter.utils.CheckUtils;
import com.callCenter.utils.CopyImages;
import com.callCenter.utils.FileUtils;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.TimeUtils;
import com.callCenter.utils.UrlEncode;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GdFinishActivity extends Activity {
	private String AlbumImageName = "", AlbumImagePath = "";
	private ProgressDialog progressDialog;
	private SettingUtils settingUtils;
	private List<String> pictureList;
	private PictureListAdapter adapter;
	private TextView gdhTextView, bsjbTextView, nameTextView,
			startTimeTextView, moneysTextView;
	private EditText fwq_moneyEdit, moneyEdit, remarksEdit;
	private LinearLayout first, second;
	private ListView pictureListView;
	private String id, fwq_money, money, remarks;
	private boolean isShow = false;
	private GdFinish gdFinish;
	private String tuidan, hz;
	private static final int MMAlert_Album = 1;
	private static final int MMAlert_CAMERA = 0;
	private static final int MMAlert_CANCEL = 2;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:

				setValue();

				break;
			case HandlerException.SuccessToo:
				progressDialog.dismiss();
				clearData();
				Toast.makeText(GdFinishActivity.this, "工单完成成功!",
						Toast.LENGTH_SHORT).show();
				GdFinishActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);
				break;
			case HandlerException.SuccessToo2:
				progressDialog.dismiss();
				Toast.makeText(GdFinishActivity.this, "工单完成成功!",
						Toast.LENGTH_SHORT).show();
				GdFinishActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);
				break;
			case HandlerException.Fail:
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
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
		setContentView(R.layout.gd_finish);
		init();
		Event();
		// 加载数据
		new Thread() {
			public void run() {
				gdFinishAction(gdFinishUrl(id));
			};
		}.start();

	}

	// 初始化
	private void init() {

		settingUtils = new SettingUtils(GdFinishActivity.this);
		pictureList = new ArrayList<String>();
		id = getIntent().getExtras().getString("gwId");
		tuidan = getIntent().getExtras().getString("tuidan");
		hz = getIntent().getExtras().getString("hz");
		first = (LinearLayout) this.findViewById(R.id.first);
		second = (LinearLayout) this.findViewById(R.id.second);
		gdhTextView = (TextView) this.findViewById(R.id.gdh);
		bsjbTextView = (TextView) this.findViewById(R.id.bsjb);
		nameTextView = (TextView) this.findViewById(R.id.name);
		startTimeTextView = (TextView) this.findViewById(R.id.startTime);
		fwq_moneyEdit = (EditText) this.findViewById(R.id.fwq_money);
		moneyEdit = (EditText) this.findViewById(R.id.money);
		remarksEdit = (EditText) this.findViewById(R.id.remarks);
		moneysTextView = (TextView) this.findViewById(R.id.moneys);
		pictureListView = (ListView) this.findViewById(R.id.pictureList);
	}

	// 点击事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						GdFinishActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 完成
		this.findViewById(R.id.finish).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (checkData()) {
							if (pictureList.size() != 0) {
								progressDialog = ProgressDialog.show(
										GdFinishActivity.this, "温馨提示",
										"正在提交数据中...", true, true);
								new Thread() {
									public void run() {
										uploadDataAction();
									};
								}.start();
							} else {
								progressDialog = ProgressDialog.show(
										GdFinishActivity.this, "温馨提示",
										"正在提交数据中...", true, true);
								new Thread() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										super.run();
										noImageUpLoad(uploadDataUrl());
									}
								}.start();
							}

						}
					}
				});
		// 图片采集
		this.findViewById(R.id.tackPicture).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
//						if (pictureList.size() <= 2) {
//							MediaManager.cameraMethod(GdFinishActivity.this,
//									SettingUtils.get("image_dir"));
//						} else {
//							Toast.makeText(GdFinishActivity.this,
//									"上传图片不能超过3张！", Toast.LENGTH_LONG).show();
//						}
						Select_IdCardFrom();

					}
				});
		// 图片删除
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
	private void Select_IdCardFrom() {
		// 图片选择列表
		MMAlert.showAlert(GdFinishActivity.this,
				getString(R.string.idCard_from), GdFinishActivity.this
						.getResources().getStringArray(R.array.select_idCard),

				null, new MMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case MMAlert_CAMERA: {
						
								MediaManager.cameraMethod(
										GdFinishActivity.this,
										SettingUtils.get("image_dir"));
							

						}

							break;
						case MMAlert_Album: {
							
								MediaManager
										.Image_From_Album(GdFinishActivity.this);
							

							break;
						}

						case MMAlert_CANCEL: {

							break;
						}

						default:
							break;
						}

					}
				});

	}
	// 设置值
	private void setValue() {
		gdhTextView.setText(gdFinish.getLsh());
		bsjbTextView.setText(gdFinish.getJibie());
		nameTextView.setText(gdFinish.getKh_name());
		startTimeTextView.setText(gdFinish.getStartTime());
		moneysTextView.setText(gdFinish.getMoneys());
		// if (gdFinish.getXsum().equals("1")) {
		// first.setVisibility(View.GONE);
		// second.setVisibility(View.GONE);
		// isShow = true;
		// }

		// moneyEdit.setText(gdFinish.getXsum());
		// if (gdFinish.getTuidan().equals("1")) {
		// fwq_moneyEdit.setEnabled(false);
		// moneyEdit.setEnabled(false);
		// fwq_moneyEdit.setText(gdFinish.getQsum());
		// moneyEdit.setText(gdFinish.getXsum());
		// } else {
		fwq_moneyEdit.setText("");
		moneyEdit.setText("");
		// }
	}

	private boolean checkData() {
		fwq_money = fwq_moneyEdit.getText().toString();
		money = moneyEdit.getText().toString();
		remarks = remarksEdit.getText().toString();
		if (!isShow) {
			if (fwq_money.length() == 0&&money.length() == 0) {
				Toast.makeText(GdFinishActivity.this, "金额不能为空!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else if (!CheckUtils.isMoney(fwq_money)) {
				Toast.makeText(GdFinishActivity.this, "金额填写错误!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else if (!CheckUtils.isMoney(money)) {
				Toast.makeText(GdFinishActivity.this, "金额填写错误!",
						Toast.LENGTH_SHORT).show();
				return false;
			} 
//			else if (remarks.length() == 0) {
//				Toast.makeText(GdFinishActivity.this, "备注信息不能为空!",
//						Toast.LENGTH_SHORT).show();
//				return false;
//			}
			// else if (pictureList.size() == 0) {
			// Toast.makeText(GdFinishActivity.this, "请现场拍照!",
			// Toast.LENGTH_SHORT).show();
			// return false;
			// }
		} else {
			if (remarks.length() == 0) {
				Toast.makeText(GdFinishActivity.this, "备注信息不能为空!",
						Toast.LENGTH_SHORT).show();
				return false;
			}
			// else if (pictureList.size() == 0) {
			// Toast.makeText(GdFinishActivity.this, "请现场拍照!",
			// Toast.LENGTH_SHORT).show();
			// return false;
			// }
		}
		return true;
	}

	private void clearData() {
		remarksEdit.setText("");
		remarksEdit.setHint("请输入反馈信息");
		fwq_moneyEdit.setText("");
		moneyEdit.setText("");
		FileUtils.deleteFile(new File(SettingUtils.get("image_dir")));
		pictureList.clear();
		adapter.notifyDataSetChanged();
	}

	// 公文发起URL
	private StringBuffer gdFinishUrl(String id) {
		StringBuffer gdFinishUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		gdFinishUrl.append(SettingUtils.get("serverPerson_finish_url"))
				.append("id=").append(id).append("&hz=").append(hz);
		System.out.println("公文完成：" + gdFinishUrl);
		return gdFinishUrl;
	}

	// 工单派发
	private void gdFinishAction(StringBuffer gdFinishUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gdFinishUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				String temp = object.getString("contents");
				JSONObject obj = new JSONObject(temp);
				gdFinish = new GdFinish();
				gdFinish.setId(obj.getString("id"));
				gdFinish.setGw_zb_id(obj.getString("gw_zb_id"));
				gdFinish.setKh_name(obj.getString("kh_name"));
				gdFinish.setStartTime(obj.getString("starttime"));
				gdFinish.setJibie(obj.getString("jibie"));
				gdFinish.setKh_pic(obj.getString("kh_pic"));
				gdFinish.setLsh(obj.getString("lsh"));
				gdFinish.setKh_id(obj.getString("kh_id"));
				gdFinish.setXsum(obj.getString("xsum"));
				gdFinish.setQsum(obj.getString("qsum"));
				gdFinish.setTuidan(obj.getString("tuidan"));
				gdFinish.setMoneys(obj.getString("moneys"));
				handler.sendEmptyMessage(HandlerException.Success);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	// 上传数据url
	private StringBuffer uploadDataUrl(String fileName, String flag) {

		StringBuffer uploadDataUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		try {
			uploadDataUrl.append(SettingUtils.get("serverPerson_upload_url"));
			uploadDataUrl.append("id=").append(gdFinish.getId())
					.append("&gw_zb_id=").append(gdFinish.getGw_zb_id())
					.append("&kh_id=").append(gdFinish.getKh_id())
					.append("&qsum=").append(fwq_money).append("&xsum=")
					.append(money).append("&contents=")
					.append(UrlEncode.Encode2(remarks)).append("&tuidan=")
					.append(UrlEncode.Encode2(gdFinish.getTuidan()))
					.append("&fileName=").append(fileName).append("&flag=")
					.append(flag);
		} catch (Exception e) {

		}
		System.out.println("上传图片url:" + uploadDataUrl);
		return uploadDataUrl;
	}

	// 上传数据url2
	private StringBuffer uploadDataUrl() {

		StringBuffer uploadDataUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		try {
			uploadDataUrl.append(SettingUtils.get("serverPerson_upload_url"));
			uploadDataUrl.append("id=").append(gdFinish.getId())
					.append("&gw_zb_id=").append(gdFinish.getGw_zb_id())
					.append("&kh_id=").append(gdFinish.getKh_id())
					.append("&qsum=").append(fwq_money).append("&xsum=")
					.append(money).append("&contents=")
					.append(UrlEncode.Encode2(remarks)).append("&tuidan=")
					.append(UrlEncode.Encode2(gdFinish.getTuidan()));
		} catch (Exception e) {

		}
		System.out.println("无图片url:" + uploadDataUrl);
		return uploadDataUrl;
	}

	// 无照片进行上传
	private void noImageUpLoad(StringBuffer uploadDataUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					uploadDataUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				// JSONObject object = new JSONObject(message);

				if (message.equals("success")) {
					handler.sendEmptyMessage(HandlerException.SuccessToo2);
				} else if (message.equals("error")) {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	// 上传数据action
	private void uploadDataAction() {

		try {
			// 发送时间
			// String sendTime = TimeUtils.getDateTime();
			for (int i = 0; i < pictureList.size();) {
				String imageName = pictureList.get(i);
				final Map<String, String> params = new HashMap<String, String>();
				final Map<String, File> files = new HashMap<String, File>();
				files.put(imageName, new File(SettingUtils.get("image_dir")
						+ "/" + imageName));
				params.put("file", imageName);
				String state = HttpConnectUtil.imageFilePost(
						uploadDataUrl(pictureList.get(i), (i + 1) + "")
								.toString(), params, files);
				System.out.println("上传图片返回:" + state);
				// 判断是否是最后一张图片
				i++;
				if (i == pictureList.size()) {
					handler.sendEmptyMessage(HandlerException.SuccessToo);
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
		switch (requestCode) {
		case MediaManager.RESULT_CAPTURE_IMAGE:
			pictureList.add(MediaManager.image_name);
			adapter = new PictureListAdapter(GdFinishActivity.this, pictureList);
			pictureListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			break;
		case MediaManager.REQUEST_CODE_ALBUM:
			Uri uri = data.getData();
			Cursor cursor = this.getContentResolver().query(uri, null,
					null, null, null);
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				if (cursor.getColumnName(i).equals("_data")) {
					AlbumImagePath = cursor.getString(i);
				} else if (cursor.getColumnName(i).equals("_display_name")) {
					AlbumImageName = cursor.getString(i);
				}
			}
			CopyImages.copy(AlbumImagePath, SettingUtils.get("image_dir"),
					AlbumImageName);
			pictureList.add(AlbumImageName);
			adapter = new PictureListAdapter(GdFinishActivity.this, pictureList);
			pictureListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
//		if (resultCode == RESULT_OK) {
//			pictureList.add(MediaManager.image_name);
//			adapter = new PictureListAdapter(GdFinishActivity.this, pictureList);
//			pictureListView.setAdapter(adapter);
//			adapter.notifyDataSetChanged();
//		}
	}

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GdFinishActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
