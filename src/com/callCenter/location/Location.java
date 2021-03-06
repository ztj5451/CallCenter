package com.callCenter.location;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.json.JSONObject;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.callCenter.activity.MainActivity;
import com.callCenter.utils.CrashHandler;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.JsonTool;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.TextView;
import android.os.Process;

public class Location extends Application {
	public static double lat, lon;
	public static boolean flag = false;
	private SettingUtils settingUtils = null;
	// 声明百度引擎管理
	private BMapManager mBMapManager = null;
	public static Location mInstance = null;
	// 初始化成功与否标示符
	public static boolean m_bKeyRight = true;
	private SharedPreferences sharedPreferences;
	public static LocationData locationData = null;
	public static boolean isGPS = false;
	public static String tag = "0";

	public static String addStreet = "";
	// 读取配置文件工具
	public static LocationClient mLocationClient = null;
	private String mData;
	public MyLocationListenner myListener = new MyLocationListenner();
	public TextView mTv;
	public static String TAG = "LocTestDemo";
	// 行驶速度 与当前卫星个数
	private float speed;
	private int satelliteNum;

	// 百度地图相关
	public enum E_BUTTON_TYPE {
		LOC, COMPASS, FOLLOW
	}

	public static Location getInstance() {
		return mInstance;
	}

	// 初始化百度引擎管理
	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}
		System.out.println("strKey:" + SettingUtils.get("strKey"));
		if (!mBMapManager.init(SettingUtils.get("strKey"),
				new MyGeneralListener())) {

		}
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {

			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			System.out.println("错误代码:" + iError);
			// 非零值表示key验证未通过
			if (iError != 0) {
				m_bKeyRight = false;
				System.out.println("地图初始化错误！");
			} else {
				m_bKeyRight = true;
				System.out.println("地图初始化成功！");
			}
		}
	}

	@Override
	public void onCreate() {
		settingUtils = new SettingUtils(Location.this);
		System.out.println("百度定位服务启动");
		// 初始化百度地图引擎

		sharedPreferences = getSharedPreferences("location",
				Context.MODE_PRIVATE);
		mLocationClient = new LocationClient(this);
		mLocationClient.setAK(SettingUtils.get("locationAk"));
		mLocationClient.registerLocationListener(myListener);
		locationData = new LocationData();
		super.onCreate();
		mInstance = this;
		initEngineManager(this);
		FrontiaApplication.initFrontiaApplication(this);
		Log.d(TAG, "... Application onCreate... pid=" + Process.myPid());

		CrashHandler handler = CrashHandler.getInstance();
		handler.init(getApplicationContext());
	}

	/**
	 * 显示字符串
	 * 
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			mData = str;
			if (mTv != null)
				mTv.setText(mData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			lat = location.getLatitude();
			lon = location.getLongitude();

			// 保存获取的经纬度
			Editor editor = sharedPreferences.edit();
			editor.putFloat("lat", (float) lat);
			editor.putFloat("lon", (float) lon);
			editor.commit();
			locationData.accuracy = location.getRadius();
			locationData.direction = location.getDerect();
			locationData.latitude = location.getLatitude();
			locationData.longitude = location.getLongitude();
			locationData.speed = location.getSpeed();
			if (location.getLocType() == BDLocation.TypeGpsLocation) {

				isGPS = true;
				// 运行速度
				speed = location.getSpeed();
				// 获取卫星个数
				satelliteNum = location.getSatelliteNumber();
				System.out.println("当前速度为:" + speed + ",获取卫星个数为:"
						+ satelliteNum);

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				isGPS = false;
				addStreet = location.getAddrStr().trim();
				System.out.println("获取到得地址:" + location.getAddrStr());
			}
			upload(lat, lon);

		}

		// 获取周边
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
			System.out.println(sb.toString());
		}
	}

	// 开启新线程开始上传
	private void upload(final Double lat, final Double lon) {
		new Thread() {
			public void run() {
				uploadLocation(lat + "", lon + "");
			};
		}.start();
	}

	// 上传轨迹信息
	private void uploadLocation(String lat, String lon) {
		StringBuffer reqeustUrl = new StringBuffer(SettingUtils.get("serverIp"));
		System.err.println("上传服务器IP：" + reqeustUrl);

		if (!flag && !addStreet.equals("")) {
			try {
				reqeustUrl.append(SettingUtils.get("upload_location_url"))
						.append("loginname=").append(MainActivity.LOGINNAME)
						.append("&jd=").append(lon).append("&wd=").append(lat)
						.append("&address=")
						.append(UrlEncode.Encode(addStreet));
				// tag = "1";
				// 保存考勤签到信息到数据库中
				// saveKaoQin(addStreet, lat, lon, "start");
				System.out.println("第一次签到上传URL：" + reqeustUrl);
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			flag = true;

		} else {
			reqeustUrl.append(SettingUtils.get("upload_location_url"))
					.append("loginname=").append(MainActivity.LOGINNAME)
					.append("&jd=").append(lon).append("&wd=").append(lat);

			System.out.println("非第一次上传地址:" + reqeustUrl);

		}
		HttpURLConnection httpURLConnection = null;
		try {

			httpURLConnection = HttpConnectUtil.locationConnect(reqeustUrl,
					"GET");

			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String message = HttpConnectUtil
						.getHttpStream(httpURLConnection);
				if (JsonTool.Resolve(message)) {
					JSONObject object = new JSONObject(message);
					tag = object.getString("qd_bs");
					System.out.println("tag=" + tag);
					System.err.println("位置信息上传成功!");
				} else {
					System.err.println("位置信息上传失败!");
				}

			} else {
				System.err.println("服务器相应异常！");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			httpURLConnection.disconnect();
		}

	}
}