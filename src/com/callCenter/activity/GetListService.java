package com.callCenter.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.callCenter.database.DBHelper;
import com.callCenter.database.MessageDB;
import com.callCenter.entity.CallPhone;
import com.callCenter.entity.Customer;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

/**
 * 向服务器发送当前用户的在线状态
 * 
 * @author Administrator
 * 
 */
public class GetListService extends Service {

	public static List<CallPhone> customerList = null;
	public static String url = "123";
	private static final String TAG = "OnLineService";
	// push
	private Notification n;
	// 声明NotificationManager
	private NotificationManager nm;
	// Notification标识ID
		private static int ID = 1;
	private SettingUtils settingUtils;
	private MediaPlayer player;
	private DBHelper dbHelper;
	private SQLiteDatabase database;
	private SharedPreferences sharedPreferences;
	private String loginname = null, userId = null;
	private Integer list_time = 0;
	Timer timer = new Timer();
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:

				break;

			default:
				break;
			}
		};
	};
	public MyBinder myBinder = new MyBinder();

	public class MyBinder extends Binder {
		public GetListService getService() {
			return GetListService.this;
		}
	}

	// 绑定服务
	@Override
	public IBinder onBind(Intent intent) {
		player = new MediaPlayer();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String service = NOTIFICATION_SERVICE;
		nm = (NotificationManager) getSystemService(service);

		// 实例化Notification
		n = new Notification();
		// 设置显示图标，该图标会在状态状态栏显示
		int icon = n.icon = R.drawable.logo;
		// 设置显示提示信息，该信息会在状态栏显示
		String tickerText = "您有新的呼叫";
		// 显示时间
		long when = System.currentTimeMillis();
		n.icon = icon;
		n.tickerText = tickerText;
		n.when = when;
		loginname = intent.getExtras().getString("loginname");
		userId = intent.getExtras().getString("userId");
		customerList = new ArrayList<CallPhone>();
		settingUtils = new SettingUtils(GetListService.this);
		list_time = Integer.parseInt(intent.getExtras().getString("list_time"));
		timer.schedule(new ConnectiveTimerTask(), 1 * 1000, list_time);
		Log.d(TAG, "onBind");
		return myBinder;
	}

	// 服务启动
	@Override
	public void onCreate() {

		Log.d(TAG, "onCreate");
		super.onCreate();
		dbHelper = new DBHelper(GetListService.this);
		// player = MediaPlayer.create(GetListService.this, R.raw.newblogtoast);

	}

	// 服务销毁
	@Override
	public void onDestroy() {
		timer.cancel();
		Log.d(TAG, "onDestory");
		super.onDestroy();
	}

	// 内存低
	@Override
	public void onLowMemory() {
		Log.d(TAG, "onLowMemory");
		super.onLowMemory();
	}

	// 重新绑定
	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG, "onRebind");
		super.onRebind(intent);
	}

	// 服务运行
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	// 取消绑定
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		timer.cancel();
		nm.cancelAll();
		if (player.isLooping()) {
			player.setLooping(false);
		}
		return super.onUnbind(intent);
	}

	// 向服务器上传链接状态信息
	private class ConnectiveTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("发送链接信号到服务器");
			updateData();

		}

	}

	// 获取最新列表
	private void updateData() {
		System.out.println(System.currentTimeMillis());
		System.out.println("后台服务已经调用");

		// getMessageList();
		// getGwList();
		getCallList();

	}

	// 通知url
	private StringBuffer getCallListUrl() {
		StringBuffer getCallListUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		getCallListUrl.append(SettingUtils.get("zuoxi_list_url")).append("id=")
				.append(userId);
		System.out.println("获取呼叫中心列表:" + getCallListUrl);
		return getCallListUrl;

	}
	private void newPush() {
		ID = ID + 1;
		// 实例化intent
		Intent intent = new Intent(GetListService.this, MainActivity.class);
		// 获取pendingIntent
		PendingIntent pi = PendingIntent.getActivity(GetListService.this,
				0, intent, 0);
		// 设置事件信息
		n.setLatestEventInfo(GetListService.this, "温馨提示", "您有新的呼叫，请及时处理！",
				pi);
		// 发出通知
		nm.notify(ID, n);
	
		playBell();
	}
	private void playBell() {

		try {
			AssetFileDescriptor fileDescriptor = getAssets().openFd(
					sharedPreferences.getString("myBell", "bell.mp3"));
			player.setDataSource(fileDescriptor.getFileDescriptor(),
					fileDescriptor.getStartOffset(), fileDescriptor.getLength());
			player.setLooping(true);
			player.prepare();
			player.start();

		} catch (Exception e) {

			// TODO: handle exception
			e.printStackTrace();
		}
	}
	// 获取通知的网络连接
	private void getCallList() {
		try {

			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					getCallListUrl(), "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {

			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream)
						.replace("null", "");
				JSONObject object = new JSONObject(message);
				System.out.println(object.toString());
				JSONArray array = object.getJSONArray("index");
				if (array.length() != 0) {
					nm.cancelAll();
					newPush() ;
					if (!player.isPlaying()) {
						player.setLooping(true);
						player.start();
					}
					customerList.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = (JSONObject) array.get(i);
						CallPhone customer = new CallPhone();
						customer.setId(obj.getString("id"));
						customer.setSex(obj.getString("sex"));
						customer.setKh_name(obj.getString("kh_name"));
						customer.setKh_address(obj.getString("kh_address"));
						customer.setSheng(obj.getString("sheng"));
						customer.setShi(obj.getString("shi"));
						customer.setQu(obj.getString("qu"));
						customer.setJiedao(obj.getString("jiedao"));
						customer.setSq(obj.getString("sq"));
						customer.setZdy(obj.getString("zdy"));
						customer.setJg(obj.getString("jg"));
						customerList.add(customer);

					}

				} else {
					nm.cancelAll();
					if (player.isLooping()) {
						player.setLooping(false);
					}
					customerList.clear();
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

}
