package com.callCenter.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.callCenter.database.DBHelper;
import com.callCenter.database.MessageDB;
import com.callCenter.dialog.MMAlert;
import com.callCenter.entity.LoginUser;
import com.callCenter.utils.CircularImage;
import com.callCenter.utils.EditTextTool;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.JsonTool;
import com.callCenter.utils.PushUtils;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;
import com.callCenter.location.Location;
import com.callCenter.push.PushMessageReceiver;
import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主界面Activity
 * 
 * @author Administrator
 * 
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {
	//后台服务
	private Intent serviceIntent;
	private String ggContent = "";
	public static String url = "123";
	private DBHelper dbHelper;
	private SQLiteDatabase database;
	private String messageId;
	// 通知显示内容
	private TextView dateTime, latestMessage, notReadSize, gwCount;
	private SharedPreferences sharedPreferences = null;
	private String userId, channelId;
	private TextView userName, userBm, userZw, u_status;
	public static boolean running = false;
	private LocationManager locationManager;
	public static LocationClient mLocClient;
	private int keyDownCount = 0; // 按下系统返回(BACK)键的次数
	private AlertDialog alertDialog;
	private static final int MMAlert_KQ = 0;
	private static final int MMAlert_CANCEL = 1;
	private static final int MMAlert_EXIT = 0;
	private static final int MMAlert_SYS_EXIT = 1;
	private static final int MMAlert_SYS_LOGOUT = 0;
	private static final int MMAlert_SYS_CANCEL = 2;
	SettingUtils settingUtils = null;
	private LoginUser loginUser;
	private boolean flag = false, isBand = false;
	// private boolean isKeFu = false;
	public static String LOGINNAME = null;
	// 获取列表服务
	private GetListService getListService;
	private static final String TAG = "OnLineService";
	private ImageView userHeader;
	private String[] qx;
	private LinearLayout sendMessage, zxPerson, txl, gwstart, gwhandler,
			gwdone, about, dataAcquistion, qyinfo;
	private CircularImage circulalImage;
	private int place = 1;
	private boolean tempFlag=false,isKeFu=false;
	
	// 设置个人头像
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case 0:
				userHeader.setImageBitmap(HttpConnectUtil
						.getLoacalBitmap(SettingUtils.get("header_image_dir")
								+ "/userHeader.jpg"));
		
			case 40:
				
				latestMessage.setText(ggContent.substring(0, place));
				break;
			case 50:
				latestMessage.setText(ggContent.substring(place,
						ggContent.length()));
				
				break;

			default:
				break;
			}

		};
	};
	// 公告handler
	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				latestMessage.setText(ggContent);
				if (!tempFlag) {
					tempFlag=true;
					new Thread() {// 该线程用于标题栏跑马灯的实现
						public void run() {
							boolean control = true;
							while (true) {
								if (control) {// 出来时
									handler.sendEmptyMessage(40);
									try {
										Thread.sleep(300);// 睡觉300毫秒
									} catch (Exception e) {// 捕获异常
										e.printStackTrace();// 打印异常
									}
									if (place >= ggContent.length()) {
										place = 1;
										control = false;
									} else {
										place++;
									}
								} else {// 进去
									handler.sendEmptyMessage(50);// 发送Handler消息
									try {
										Thread.sleep(200);// 睡觉300毫秒
									} catch (Exception e) {// 捕获异常
										e.printStackTrace();// 打印异常
									}
									if (place >= ggContent.length()) {
										place = 1;
										control = true;
									} else {
										place++;// 将place加一
									}
								}
							}
						}
					}.start();
				}
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
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		// 初始化
		init();
		// 监听事件
		event();
		// 开启service服务
		// startConnectionService();
	}

	// 获取公告
	// 公文发起URL
	private StringBuffer getGongGaoUrl(String kh_id) {
		StringBuffer gwStartUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwStartUrl.append(SettingUtils.get("gonggao_url")).append("uid=")
				.append(kh_id);
		System.out.println("公告url:" + gwStartUrl);
		return gwStartUrl;
	}

	private void getGongGaoAction(StringBuffer getGongGaoUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					getGongGaoUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler2.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);

				JSONObject object = new JSONObject(message);

				if (!object.isNull("contents")) {
					ggContent = object.getString("contents");
					handler2.sendEmptyMessage(HandlerException.Success);
				} else {
					handler2.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	// 初始化
	private void init() {

		circulalImage = (CircularImage) this.findViewById(R.id.user_header);
		circulalImage.setImageResource(R.drawable.user_header);
		loginUser = (LoginUser) getIntent().getSerializableExtra("loginUsers");
		qx = loginUser.getDh().split("@");
		sendMessage = (LinearLayout) this.findViewById(R.id.sendMessage);
		txl = (LinearLayout) this.findViewById(R.id.txl);
		gwstart = (LinearLayout) this.findViewById(R.id.gwstart);
		gwhandler = (LinearLayout) this.findViewById(R.id.gwhandler);
		gwdone = (LinearLayout) this.findViewById(R.id.gwdone);
		about = (LinearLayout) this.findViewById(R.id.about);
		dataAcquistion = (LinearLayout) this.findViewById(R.id.dataAcquistion);
		qyinfo = (LinearLayout) this.findViewById(R.id.qyinfo);
		zxPerson = (LinearLayout) this.findViewById(R.id.zxPerson);
		if (loginUser.getUtype().equals("0")) {
			isKeFu=false;
//			sendMessage.setBackgroundColor(getResources().getColor(
//					R.color.huise));
//			zxPerson.setBackgroundColor(getResources().getColor(R.color.huise));
		}else {
			isKeFu=true;
		}
		
		
		// 进行权限判断
//		for (int i = 0; i < qx.length - 1; i++) {
//			switch (i) {
//			case 0:
//				if (qx[i].equals("0")) {
//					txl.setBackgroundColor(getResources().getColor(
//							R.color.huise));
//				}
//				break;
//			case 1:
//				if (qx[i].equals("0")) {
//					gwstart.setBackgroundColor(getResources().getColor(
//							R.color.huise));
//				}
//				break;
//			case 2:
//				if (qx[i].equals("0")) {
//					gwhandler.setBackgroundColor(getResources().getColor(
//							R.color.huise));
//				}
//				break;
//			case 3:
//				if (qx[i].equals("0")) {
//					gwdone.setBackgroundColor(getResources().getColor(
//							R.color.huise));
//				}
//				break;
//			case 4:
//				if (qx[i].equals("0")) {
//					about.setBackgroundColor(getResources().getColor(
//							R.color.huise));
//				}
//				break;
//			case 5:
//				if (qx[i].equals("0")) {
//					dataAcquistion.setBackgroundColor(getResources().getColor(
//							R.color.huise));
//				}
//				break;
//			case 6:
//				if (qx[i].equals("0")) {
//					qyinfo.setBackgroundColor(getResources().getColor(
//							R.color.huise));
//				}
//				break;
//
//			default:
//				break;
//			}
//		}

		userName = (TextView) this.findViewById(R.id.u_name);
		userBm = (TextView) this.findViewById(R.id.u_bm);
		userZw = (TextView) this.findViewById(R.id.u_zw);
		u_status = (TextView) this.findViewById(R.id.u_status);
		// 获取登陆用户信息
		System.out.println("传递过来的数值:" + loginUser.loginName);
		LOGINNAME = loginUser.getLoginName().toString();
		userName.setText(loginUser.getUserName().toString());
		if (!loginUser.getBm().isEmpty()) {
			if (loginUser.getBm().contains(",")) {
				userBm.setText(loginUser.getBm().split(",")[1].toString());
			} else {
				userBm.setText(loginUser.getBm());
			}

		} else {
			userBm.setText("");
		}
		if (!loginUser.getZhiwei().isEmpty()) {
			if (loginUser.getZhiwei().contains(",")) {
				userZw.setText(loginUser.getZhiwei().split(",")[1].split("/")[0]);
			} else {
				userZw.setText(loginUser.getZhiwei());
			}
		} else {
			userZw.setText("");
		}

		/*********************************************/
		dateTime = (TextView) this.findViewById(R.id.dateTime);
		latestMessage = (TextView) this.findViewById(R.id.latestMessage);
		notReadSize = (TextView) this.findViewById(R.id.notReadSize);
		gwCount = (TextView) this.findViewById(R.id.gwCount);
		// 设置个人头像
		setUserHeader();

		PushMessageReceiver.running = true;
		sharedPreferences = getSharedPreferences("pushInfo", MODE_PRIVATE);
		userId = sharedPreferences.getString("userId", "");
		channelId = sharedPreferences.getString("channelId", "");
		System.err.println("获取到的push:" + "userId:" + userId + ","
				+ "channelId:" + channelId);

		locationManager = (LocationManager) MainActivity.this
				.getSystemService(Context.LOCATION_SERVICE);
		settingUtils = new SettingUtils(MainActivity.this);
		mLocClient = ((Location) getApplication()).mLocationClient;
		// setLocationOption();
		// mLocClient.start();
		// 初始化push
		dbHelper = new DBHelper(MainActivity.this);
		initPush();
		// 初始化api key
		initWithApiKey();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// 到主页面 强制隐藏键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (running) {
			String msg[] = PushUtils.logStringCache.split(",");
			System.out.println("传递过来的push:" + msg.toString());
			if (msg[0].equals("gg")) {
				// 消息的id
				messageId = msg[1];
				// 消息时间
				dateTime.setText(msg[2]);
				// 消息内容
				latestMessage.setText(msg[3]);
			}
			

		} else {
			// 当用户没有登录 接收到通知之后 获取最新一条的通知内容进行显示
			getMessageFromDatabase(messageId, latestMessage, dateTime);
		}
		// 开启新线程获取公告内容
		new Thread() {
			public void run() {
				getGongGaoAction(getGongGaoUrl(loginUser.getId()));
			};
		}.start();
	
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 未读通知
		// getMessageFromDatabase(messageId, latestMessage, dateTime);
		notReadSize.setText(getNotReadMessageSize() + "");
		// 待办公文
		gwCount.setText(getGwCount() + "");
		if (isKeFu) {
			startConnectionService();
		}
		
	}
	
	// 当push不好用，service自动更新列表时
	private void getMessageFromDB(String id, TextView title, TextView time) {
		database = dbHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery("SELECT id,title,time FROM "
				+ DBHelper.MESSAGE + " WHERE FLAG=? ORDER BY time DESC",
				new String[] { "no" });
		if (cursor.getCount() != 0) {
			while (cursor.moveToNext()) {
				id = cursor.getString(cursor.getColumnIndex(MessageDB.ID));
				title.setText(cursor.getString(cursor
						.getColumnIndex(MessageDB.TITLE)));
				time.setText(cursor.getString(cursor
						.getColumnIndex(MessageDB.TIME)));
			}

		}
	}
	// 绑定的服务
	// 开启联接状态信号服务
	private void startConnectionService() {
		serviceIntent = new Intent(MainActivity.this,
				GetListService.class);
		serviceIntent.putExtra("loginname", loginUser.loginName);
		serviceIntent.putExtra("userId", loginUser.id);

		serviceIntent.putExtra("list_time", "5000");
		// 对服务进行绑定
		isBand = bindService(serviceIntent, serviceConnection,
				Context.BIND_AUTO_CREATE);
		System.out.println("开始绑定");

	}
	private ServiceConnection serviceConnection = new ServiceConnection() {
		// 绑定成功调用此方法
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			getListService = ((GetListService.MyBinder) service).getService();
			System.out.println("服务绑定成功");
		}

		// 解除绑定调用此方法
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			getListService = null;
			System.out.println("取消绑定");
		}

	};

	// 绑定服务
	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		super.bindService(service, conn, flags);
		System.out.println("服务绑定成功");
		return true;
	};

	// 解除绑定
	@Override
	public void unbindService(ServiceConnection conn) {
		if (isBand) {
			super.unbindService(conn);

			System.out.println("取消绑定");
			isBand = false;
		}
	}
	// 当程序未登录时
	private void getMessageFromDatabase(String id, TextView title, TextView time) {
		database = dbHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery("SELECT id,title,time FROM "
				+ DBHelper.MESSAGE + " WHERE FLAG=? ORDER BY time DESC",
				new String[] { "no" });
		if (cursor.getCount() != 0) {
			while (cursor.moveToNext()) {
				id = cursor.getString(cursor.getColumnIndex(MessageDB.ID));
				title.setText(cursor.getString(cursor
						.getColumnIndex(MessageDB.TITLE)));
				time.setText(cursor.getString(cursor
						.getColumnIndex(MessageDB.TIME)));
				return;
			}

		}

	}

	/**
	 * 通知公告
	 * 
	 * @return
	 */

	// 获取有多少条未读通知
	private int getNotReadMessageSize() {
		database = dbHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery("SELECT FLAG FROM "
				+ DBHelper.MESSAGE + " WHERE FLAG=?", new String[] { "no" });
		// Cursor cursor = database.rawQuery("SELECT * FROM " +
		// DBHelper.MESSAGE,
		// null);
		return cursor.getCount();
	}

	// 保存新消息到未读数据库
	private void saveMessage2NotRead(String id, String time, String title) {
		database = dbHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("SELECT id FROM " + DBHelper.MESSAGE
				+ " WHERE id=?", new String[] { id });
		if (cursor.getCount() == 0) {
			ContentValues values = new ContentValues();
			values.put(MessageDB.ID, id);
			values.put(MessageDB.TIME, time);
			values.put(MessageDB.TITLE, title);
			values.put(MessageDB.FLAG, "no");
			database.insert(DBHelper.MESSAGE, null, values);
		}

	}

	/**
	 * 公文
	 */
	private int getGwCount() {
		database = dbHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery("SELECT FLAG FROM "
				+ DBHelper.GW_START + " WHERE FLAG=?",
				new String[] { "waiting" });
		return cursor.getCount();
	}

	// 保存新消息到未读数据库
	private void saveGw2Waiting(String id, String time, String title) {
		database = dbHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("SELECT id FROM " + DBHelper.GW_START
				+ " WHERE id=?", new String[] { id });
		if (cursor.getCount() == 0) {
			ContentValues values = new ContentValues();
			values.put(MessageDB.ID, id);
			values.put(MessageDB.TIME, time);
			values.put(MessageDB.TITLE, title);
			values.put(MessageDB.FLAG, "waiting");
			database.insert(DBHelper.GW_START, null, values);
		}

	}

	// 添加监听事件
	private void event() {
		// 登陆用户头像
//		this.findViewById(R.id.user_header).setOnClickListener(
//				new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//
//						new Thread() {
//							@Override
//							public void run() {
//								super.run();
//								getHeaderImageView();
//							}
//						}.start();
//					}
//				});
		// 查看个人信息
		this.findViewById(R.id.personInfo).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this,
								PersonalInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("loginUser", loginUser);
						intent.putExtras(bundle);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);

					}
				});
		// 客户充值
		this.findViewById(R.id.sendMessage).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (loginUser.getUtype().equals("1")) {
							Intent intent = new Intent(MainActivity.this,
									RechargeActivity.class);

							intent.putExtra("loginname",
									loginUser.getLoginName());
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						} else if (loginUser.getUtype().equals("0")) {
							Toast.makeText(MainActivity.this, "您没有该权限!",
									Toast.LENGTH_SHORT).show();
						}

					}
				});
		// 工单审批
		this.findViewById(R.id.txl).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				if (qx[0].equals("0")) {
					Toast.makeText(MainActivity.this, "您没有该权限!",
							Toast.LENGTH_SHORT).show();
				} else if (qx[1].equals("1")) {
					Intent intent = new Intent(MainActivity.this,
							GwspActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("loginname", loginUser.getLoginName());
					intent.putExtra("uid", loginUser.getId());

					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				}

			}
		});
		// 坐席人员
		this.findViewById(R.id.zxPerson).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						// if (isKeFu) {

						// } else {
						// Toast.makeText(MainActivity.this, "您没有改权限!",
						// Toast.LENGTH_SHORT).show();
						// }
						if (loginUser.getUtype().equals("1")) {
							Intent intent = new Intent(MainActivity.this,
									CallCenterActivity.class);
							intent.putExtra("loginname",
									loginUser.getLoginName());
							intent.putExtra("userId", loginUser.getId());
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						} else if (loginUser.getUtype().equals("0")) {
							Toast.makeText(MainActivity.this, "您没有该权限!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		// 考勤签到
		this.findViewById(R.id.kqqd).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!flag) {
					// 判断GPS 是否开启
					if (isGPSOpen()) {
						kqqd_start();
					}

				} else {
					kqqd_stop();
				}

			}
		});
		// 接收消息
		this.findViewById(R.id.message).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						if (!ggContent.isEmpty()) {
							Intent intent = new Intent(MainActivity.this,
									MessageActivity.class);
							intent.putExtra("ggContent", ggContent);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						} else {
							Toast.makeText(MainActivity.this, "当前没有最新公告!",
									Toast.LENGTH_SHORT).show();
						}

					}
				});

		// 我的接听
		this.findViewById(R.id.wdjt).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						WdjtActivity.class);
				intent.putExtra("uid", loginUser.getId());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
		// 未接听统计
		this.findViewById(R.id.wjttj).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						WjttjActivity.class);
				intent.putExtra("uid", loginUser.getId());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});

		// 未派工单
		this.findViewById(R.id.gwstart).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						if (qx[1].equals("0")) {
							Toast.makeText(MainActivity.this, "您没有该权限!",
									Toast.LENGTH_SHORT).show();
						} else if (qx[1].equals("1")) {
							Intent intent = new Intent(MainActivity.this,
									WorkOrderActivity.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("loginUser", loginUser);
							intent.putExtras(bundle);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						}

					}
				});
		// 总工单
		this.findViewById(R.id.gwhandler).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						if (qx[2].equals("0")) {
							Toast.makeText(MainActivity.this, "您没有该权限!",
									Toast.LENGTH_SHORT).show();
						} else if (qx[2].equals("1")) {
							Intent intent = new Intent(MainActivity.this,
									GWHandlerActivity.class);
							intent.putExtra("uid", loginUser.getId());
							intent.putExtra("loginname",
									loginUser.getLoginName());
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						}

					}
				});
		// 公文跟踪
		this.findViewById(R.id.gwdone).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (qx[3].equals("0")) {
							Toast.makeText(MainActivity.this, "您没有该权限!",
									Toast.LENGTH_SHORT).show();
						} else if (qx[3].equals("1")) {
							Intent intent = new Intent(MainActivity.this,
									GWDoneActivity.class);
							intent.putExtra("loginname",
									loginUser.getLoginName());
							intent.putExtra("uid", loginUser.getId());
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						}

					}
				});
		// 周边企业
		this.findViewById(R.id.zbqy).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flag) {
					Intent intent = new Intent(MainActivity.this,
							SourroundMapActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				} else {
					Toast.makeText(MainActivity.this, "请打开位置开关!",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		// 公文催办
		this.findViewById(R.id.dataAcquistion).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub

						if (qx[5].equals("0")) {
							Toast.makeText(MainActivity.this, "您没有该权限!",
									Toast.LENGTH_SHORT).show();
						} else if (qx[5].equals("1")) {
							Intent intent = new Intent(MainActivity.this,
									GwcbActivity.class);
							intent.putExtra("loginname",
									loginUser.getLoginName());
							intent.putExtra("userId", loginUser.getId());
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						}

					}
				});
		// 服务人员
		this.findViewById(R.id.qyinfo).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (qx[6].equals("0")) {
							Toast.makeText(MainActivity.this, "您没有该权限!",
									Toast.LENGTH_SHORT).show();
						} else if (qx[6].equals("1")) {
							Intent intent = new Intent(MainActivity.this,
									ServerPersonJDActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("userId", loginUser.getId());
							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						}

					}
				});
		// 地图
		this.findViewById(R.id.map).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (flag) {
					Intent intent = new Intent(MainActivity.this,
							MapActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				} else {
					Toast.makeText(MainActivity.this, "请打开位置开关!",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		// 系统设置
		this.findViewById(R.id.system_set).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						Intent intent = new Intent(MainActivity.this,
								SettingActivity.class);

						Bundle bundle = new Bundle();
						bundle.putSerializable("loginUser", loginUser);
						intent.putExtras(bundle);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);

					}
				});
		// 调度公文受理
		this.findViewById(R.id.about).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				if (qx[4].equals("0")) {
					Toast.makeText(MainActivity.this, "您没有该权限!",
							Toast.LENGTH_SHORT).show();
				} else if (qx[4].equals("1")) {
					Intent intent = new Intent(MainActivity.this,
							DdgwslActivity.class);
					intent.putExtra("uid", loginUser.getId());
					intent.putExtra("loginname", loginUser.getLoginName());
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				}

			}
		});

		// 客户信息管理
		this.findViewById(R.id.customerInfo).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						// if (loginUser.getUtype().equals("1")) {
						Intent intent = new Intent(MainActivity.this,
								CustomerInfoActivity.class);
						intent.putExtra("loginname", loginUser.getLoginName());
						intent.putExtra("userId", loginUser.getId());
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						// } else if (loginUser.getUtype().equals("0")) {
						// Toast.makeText(MainActivity.this, "您没有该权限!",
						// Toast.LENGTH_SHORT).show();
						// }

					}
				});

	}

	/* +++++++++++++++++++++++++++++++++++++++++++++++++++ */
	// 设置个人头像
	private void setUserHeader() {
		if (new File(SettingUtils.get("header_image_dir") + "/userHeader.jpg")
				.exists()) {
			userHeader.setImageBitmap(HttpConnectUtil
					.getLoacalBitmap(SettingUtils.get("header_image_dir")
							+ "/userHeader.jpg"));
		}
	}

	// 获取登陆头像图片
	public void getHeaderImageView() {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUser.getPic(), "GET");

			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = httpURLConnection.getInputStream();

				HttpConnectUtil.saveIntentImage(inputStream,
						SettingUtils.get("header_image_dir"), "userHeader.jpg");
				inputStream.close();

				handler.sendEmptyMessage(0);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * =========================================================================
	 */
	// 向服务器发送考勤签到
	@SuppressLint({ "SimpleDateFormat", "NewApi" })
	private void user_QD(int flag) {
		StringBuffer buffer = new StringBuffer(SettingUtils.get("serverIp"));

		if (flag == 1) {
			if (Location.addStreet.isEmpty()) {
				buffer.append(SettingUtils.get("upload_location_url"))
						.append("loginname=").append(loginUser.getLoginName())
						.append("&jd=").append(Location.lon).append("&wd=")
						.append(Location.lat).append("&address=").append("0");
			} else {
				buffer.append(SettingUtils.get("upload_location_url"))
						.append("loginname=").append(loginUser.getLoginName())
						.append("&jd=").append(Location.lon).append("&wd=")
						.append(Location.lat).append("&address=")
						.append(Location.addStreet);
			}

			// .append("&sj_num=")
			// .append(userId).append("&sj_tdh=").append(channelId);
			System.out.println("签到falg=1：" + buffer.toString());
		} else if (flag == 0) {
			try {
				buffer.append(SettingUtils.get("end_location_url"))
						.append("loginname=").append(loginUser.getLoginName())
						.append("&jd=").append(Location.lon).append("&wd=")
						.append(Location.lat).append("&address=")
						// .append("&sj_num=")
						// .append(userId).append("&sj_tdh=").append(channelId)
						// .append("&qd_dz=")
						.append(UrlEncode.Encode(Location.addStreet));
				// 结束考勤签到 保存到数据库中
				// Location.saveKaoQin(Location.addStreet, Location.lat + "",
				// Location.lon + "", "end");
				System.out.println("签到falg=0：" + buffer.toString());
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}

		}

		System.err.println("签到URL：" + buffer.toString());
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					buffer, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {

			} else {
				String message = HttpConnectUtil
						.getHttpStream(httpURLConnection);
				if (JsonTool.Resolve(message)) {
					System.err.println("签到成功！");
				} else {
					System.err.println("签到失败！");
				}
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 判断GPS是否开启
	private boolean isGPSOpen() {
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(MainActivity.this, "请开启GPS导航...", Toast.LENGTH_SHORT)
					.show();
			// 返回开启GPS导航设置界面
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			MainActivity.this.startActivityForResult(intent, 0);
			return false;
		}
		return true;
	}

	// 开始签到
	private void kqqd_start() {
		MMAlert.showAlert(
				MainActivity.this,
				getString(R.string.kqqd_menu),
				MainActivity.this.getResources().getStringArray(
						R.array.kaoqin_item_no), null,
				new MMAlert.OnAlertSelectId() {
					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case MMAlert_KQ: {
							Location.tag = "0";
							// 考勤签到
							new Thread() {
								public void run() {
									setLocationOption();
									mLocClient.start();
									user_QD(1);
								};
							}.start();
							u_status.setText(R.string.user_kq);
							flag = true;
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

	// 结束签到
	private void kqqd_stop() {
		MMAlert.showAlert(
				MainActivity.this,
				getString(R.string.kqqd_menu),
				MainActivity.this.getResources().getStringArray(
						R.array.kaoqin_item_yes), null,
				new MMAlert.OnAlertSelectId() {
					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case MMAlert_KQ: {
							// 结束签到

							new Thread() {
								public void run() {
									mLocClient.stop();
									user_QD(0);
									Location.flag = false;
								};

							}.start();
							u_status.setText(R.string.user_kq_no);
							flag = false;
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

	/**/// //////////////////////////////////////////////////////////////////////////*/
			// 添加微信样式系统菜单
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// 点击菜单按钮 弹出退出按钮
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			MMAlert.showAlert(
					MainActivity.this,
					getString(R.string.system_exit),
					MainActivity.this.getResources().getStringArray(
							R.array.system_item),

					null, new MMAlert.OnAlertSelectId() {

						@Override
						public void onClick(int whichButton) {
							switch (whichButton) {
							case MMAlert_SYS_LOGOUT: {
								Intent intent = new Intent(MainActivity.this,
										LoginActivity.class);
								startActivity(intent);
								MainActivity.this.finish();
								overridePendingTransition(R.anim.in_to_right,
										R.anim.out_from_left);
							}

								break;
							case MMAlert_SYS_EXIT: {

								// 弹出提示框
								alertExit();
								break;
							}

							case MMAlert_SYS_CANCEL: {

								break;
							}

							default:
								break;
							}

						}
					});
			// 连续两下返回按钮 弹出退出提示
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			keyDownCount++;
			if (keyDownCount == 2) {
				alertExit();
			} else if (keyDownCount == 3) {
				keyDownCount = 1;
			} else {
				EditTextTool
						.showMessage(MainActivity.this, R.string.backToo, 0);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// 弹出退出提示
	private void alertExit() {
		alertDialog = new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.logo)
				.setTitle(R.string.tip)
				.setMessage(R.string.tip_message)
				.setNegativeButton(R.string.app_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								keyDownCount = 0;
							}
						})

				.setPositiveButton(R.string.app_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 当前没有结束签到
								if (flag) {
									new Thread() {
										public void run() {
											mLocClient.stop();
											user_QD(0);

										};

									}.start();
									// 结束后台服务
									// unbindService(serviceConnection);
									MainActivity.this.finish();
									overridePendingTransition(
											R.anim.in_to_right,
											R.anim.out_from_left);
									// 已经结束签到
								} else {
									// 结束后台服务
									// unbindService(serviceConnection);
									MainActivity.this.finish();
									overridePendingTransition(
											R.anim.in_to_right,
											R.anim.out_from_left);
								}

							}
						}).create();
		alertDialog.show();
	}

	// 百度定位SDK 设置相关参数
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll");// 设置坐标类型
		option.setServiceName("com.callCenter");// 设置服务名称
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setScanSpan(Integer.parseInt(SettingUtils.get("scanspan")));// GPS数据采集时间间隔
		option.setPriority(LocationClientOption.GpsFirst);// 设置GPS优先
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(300000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		option.disableCache(true);// 禁止启用缓存定位
		mLocClient.setLocOption(option);
	}

	// 停止百度SDK GPS服务

	public void onDestroy() {
		if (isKeFu) {
			unbindService(serviceConnection);
		}
		
		mLocClient.stop();
		PushMessageReceiver.running = false;
		// unbindService(serviceConnection);
		super.onDestroy();
	}

	// ---------------------------------push------------------
	public void initPush() {
		PushUtils.logStringCache = PushUtils
				.getLogText(getApplicationContext());

		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		if (!PushUtils.hasBind(MainActivity.this.getApplicationContext())) {
			PushManager.startWork(getApplicationContext(),
					PushConstants.LOGIN_TYPE_API_KEY,
					PushUtils.getMetaValue(MainActivity.this, "api_key"));

			PushManager.enableLbs(getApplicationContext());
		}
		CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
				getApplicationContext(), resource.getIdentifier(
						"notification_custom_builder", "layout", pkgName),
				resource.getIdentifier("notification_icon", "id", pkgName),
				resource.getIdentifier("notification_title", "id", pkgName),
				resource.getIdentifier("notification_text", "id", pkgName));
		cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE);
		cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		cBuilder.setLayoutDrawable(resource.getIdentifier(
				"simple_notification_icon", "drawable", pkgName));
		PushManager.setNotificationBuilder(this, 1, cBuilder);
	}

	// 获取API key
	private void initWithApiKey() {

		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				PushUtils.getMetaValue(MainActivity.this, "api_key"));

	}

}
