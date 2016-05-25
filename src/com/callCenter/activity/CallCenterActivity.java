package com.callCenter.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.callCenter.adapter.CallCenterAdapter;
import com.callCenter.entity.CallPhone;
import com.callCenter.utils.HandlerException;

import android.R.integer;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CallCenterActivity extends Activity {
	// push
	private Notification n;
	// 声明NotificationManager
	private NotificationManager nm;
	// Notification标识ID
	private static int ID = 1;
	// private List<Customer> myCustomerList;
	private  List<CallPhone> phoneList = null, tempPhoneList;
	private CallCenterAdapter adapter = null;
	private ListView callListView;
	private boolean isBand = false;
	private String loginname, userId;
	// 获取列表服务
	private GetListService getListService;
	//private Intent serviceIntent;
	private static final String TAG = "OnLineService";
	private Timer timer = new Timer();
	private boolean flag = false;
	private SharedPreferences sharedPreferences;
	private MediaPlayer player;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				if (adapter != null) {
					callListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					//newPush();
					if (!player.isPlaying()) {
						player.setLooping(true);
						player.start();
					}
					
					
					if (flag) {
						if (phoneList.size() != 0) {
							//newPush();
//							if (!player.isLooping()) {
//								player.setLooping(true);
//								player.start();
//							}
//							player.setLooping(true);
//							player.start();
							flag = false;
						}else {
//							player.setLooping(true);
//							player.stop();
						}

					}
					if (phoneList.size() != 0) {
						flag = false;
//						player.setLooping(true);
//						player.start();
					}
					

				} else {
					adapter = new CallCenterAdapter(CallCenterActivity.this,
							phoneList);
					callListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					newPush();
					// if (!player.isLooping()) {
					// player.setLooping(true);
					// player.start();
					// }
					// player.setLooping(true);
					// player.start();
					// playBell();
					
				}
				break;
			case HandlerException.Over:
				adapter.notifyDataSetChanged();
				break;

			case 1:
				if (player.isLooping()) {
					player.setLooping(false);
				}
				if (adapter != null) {
					adapter.notifyDataSetChanged();
					flag = true;
				}
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
		setContentView(R.layout.callcenter);
		init();
		Event();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		new Thread() {
			public void run() {
				// 清空所有
				nm.cancelAll();
				timer.schedule(new ConnectiveTimerTask(), 1 * 1000, 5 * 1000);
				// nm.cancel(ID);
			};
		}.start();

	}

	@Override
	protected void onResume() {
		//startConnectionService();
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	private void newPush() {
		ID = ID + 1;
		// 实例化intent
		Intent intent = new Intent(CallCenterActivity.this, MainActivity.class);
		// 获取pendingIntent
		PendingIntent pi = PendingIntent.getActivity(CallCenterActivity.this,
				0, intent, 0);
		// 设置事件信息
		n.setLatestEventInfo(CallCenterActivity.this, "温馨提示", "您有新的呼叫，请及时处理！",
				pi);
		// 发出通知
		nm.notify(ID, n);
	
		playBell();
	}



	private class ConnectiveTimerTask extends TimerTask {
		@Override
		public void run() {
			System.out.println("获取list集合中的内容");
			phoneList = GetListService.customerList;
			System.out.println("获取list集合中的大小:" + phoneList.size());
			if (phoneList.size() != 0) {
				handler.sendEmptyMessage(0);
			} else {
				handler.sendEmptyMessage(1);
			}

		}

	}

	// 初始化
	private void init() {
		player = new MediaPlayer();
		
		// 获取坐席人员的userId
		userId = getIntent().getExtras().getString("userId");
		// 获取坐席人员的登录name
		loginname = getIntent().getExtras().getString("loginname");
		callListView = (ListView) this.findViewById(R.id.call_list);
		tempPhoneList = new ArrayList<CallPhone>();
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

	}

	// 按钮事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						CallCenterActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// listview 点击事件
		callListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				String customId = phoneList.get(position).getId();
				phoneList.remove(position);
				// GetListService.customerList.remove(position);
				handler.sendEmptyMessage(HandlerException.Over);
				if (player.isLooping()) {
					player.setLooping(false);
				}

				Intent intent = new Intent(CallCenterActivity.this,
						CallerDetailActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("loginname", loginname);
				intent.putExtra("customId", customId);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				nm.cancelAll();

			}
		});

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

	// 绑定的服务
	// 开启联接状态信号服务
//	private void startConnectionService() {
//		serviceIntent = new Intent(CallCenterActivity.this,
//				GetListService.class);
//		serviceIntent.putExtra("loginname", loginname);
//		serviceIntent.putExtra("userId", userId);
//
//		serviceIntent.putExtra("list_time", "5000");
//		// 对服务进行绑定
//		isBand = bindService(serviceIntent, serviceConnection,
//				Context.BIND_AUTO_CREATE);
//		System.out.println("开始绑定");
//
//	}

	//
//	private ServiceConnection serviceConnection = new ServiceConnection() {
//		// 绑定成功调用此方法
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			// TODO Auto-generated method stub
//			getListService = ((GetListService.MyBinder) service).getService();
//			System.out.println("服务绑定成功");
//		}
//
//		// 解除绑定调用此方法
//		public void onServiceDisconnected(ComponentName name) {
//			// TODO Auto-generated method stub
//			getListService = null;
//			System.out.println("取消绑定");
//		}
//
//	};
//
//	// 绑定服务
//	@Override
//	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
//		super.bindService(service, conn, flags);
//		System.out.println("服务绑定成功");
//		return true;
//	};
//
//	// 解除绑定
//	@Override
//	public void unbindService(ServiceConnection conn) {
//		if (isBand) {
//			super.unbindService(conn);
//
//			System.out.println("取消绑定");
//			isBand = false;
//		}
//	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		//unbindService(serviceConnection);
		player.stop();
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		nm.cancelAll();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 
	}

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CallCenterActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
