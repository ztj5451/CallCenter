package com.callCenter.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.callCenter.location.Location;
import com.callCenter.utils.BMapUtil;
import com.callCenter.utils.SettingUtils;

/**
 * 在手机端展示地图
 * 
 * @author Administrator
 * 
 */
public class QyInfoMapActivity extends Activity {
	SharedPreferences sharedPreferences=null;
	//企业传递过来的经纬度
	private double lat,lon;
	private MyOverlay mOverlay = null;
	private PopupOverlay pop2 = null;
	private ArrayList<OverlayItem> mItems = null;
	private TextView popupText2 = null;
	private View viewCache2 = null;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	private Button button = null;
	private MapView.LayoutParams layoutParam = null;
	private OverlayItem mCurItem = null;


	private Location.E_BUTTON_TYPE mCurBtnType;

	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	
	public MyLocationListenner myListener = new MyLocationListenner();

	// 定位图层
	locationOverlay myLocationOverlay = null;
	// 弹出泡泡图层
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用
	private TextView popupText = null;// 泡泡view
	private View viewCache = null;

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MyMapView mMapView = null; // 地图View
	private MapController mMapController = null;

	// UI相关
	OnCheckedChangeListener radioButtonListener = null;
	Button requestLocButton = null;
	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedPreferences=getSharedPreferences("location", Context.MODE_PRIVATE);
		double lat=sharedPreferences.getFloat("lat", (float) 0.0);
		double lon=sharedPreferences.getFloat("lon", (float) 0.0);
		// 初始化地图引擎，若初始化成功，则加载地图
		if (Location.m_bKeyRight == true) {

			setContentView(R.layout.qy_detail_map);

		} else {
			setContentView(R.layout.qy_detail_map);
		}
		lat=getIntent().getExtras().getDouble("lat");
		lon=getIntent().getExtras().getDouble("lon");
		CharSequence titleLable = "定位功能";
		setTitle(titleLable);
		requestLocButton = (Button) findViewById(R.id.button1);
		mCurBtnType = Location.E_BUTTON_TYPE.LOC;
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurBtnType) {
				case LOC:
					// 手动定位请求
					requestLocClick();
					break;
				case COMPASS:
					myLocationOverlay.setLocationMode(LocationMode.NORMAL);
					requestLocButton.setText("定位");
					mCurBtnType = Location.E_BUTTON_TYPE.LOC;
					break;
				case FOLLOW:
					myLocationOverlay.setLocationMode(LocationMode.COMPASS);
					requestLocButton.setText("罗盘");
					mCurBtnType = Location.E_BUTTON_TYPE.COMPASS;
					break;
				}
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);

		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
		radioButtonListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.defaulticon) {
					// 传入null则，恢复默认图标
					modifyLocationOverlayIcon(null);
				}
				if (checkedId == R.id.customicon) {
					// 修改为自定义marker
					modifyLocationOverlayIcon(getResources().getDrawable(
							R.drawable.icon_geo));
				}
				// 设置地图显示是普通地图还是卫星地图
				if (checkedId == R.id.normal) {
					mMapView.setSatellite(false);
				} else if (checkedId == R.id.satellite) {
					mMapView.setSatellite(true);

				}
				if (checkedId == R.id.traffic) {
					mMapView.setTraffic(true);
				}

			}
		};

		group.setOnCheckedChangeListener(radioButtonListener);

		// 地图初始化
		mMapView = (MyMapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(16);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		// 设置地图初始加载位置
		System.out.println(lat+","+lon);
//		double cLat = lat
//		double cLon = Float.parseFloat(SettingUtils.get("init_lon"));
		GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		mMapController.setCenter(p);
		// 创建 弹出泡泡图层
		createPaopao();
		// 弹出企业信息
		initOverlay();

		// 定位初始化
//		mLocClient = new LocationClient(this);
//		locData = new LocationData();
//		mLocClient.registerLocationListener(myListener);
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);// 打开gps
//		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(1000);
//		mLocClient.setLocOption(option);
//		mLocClient.start();

		// 定位图层初始化
		myLocationOverlay = new locationOverlay(mMapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();

	}

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		isRequest = true;
		mLocClient.requestLocation();
		Toast.makeText(QyInfoMapActivity.this, "正在定位……", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 修改位置图标
	 * 
	 * @param marker
	 */
	public void modifyLocationOverlayIcon(Drawable marker) {
		// 当传入marker为null时，使用默认图标绘制
		myLocationOverlay.setMarker(marker);
		// 修改图层，需要刷新MapView生效
		mMapView.refresh();
	}

	/**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao() {
		viewCache = getLayoutInflater()
				.inflate(R.layout.custom_text_view, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
		};
		pop = new PopupOverlay(mMapView, popListener);
		MyMapView.pop = pop;
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = location.getRadius();
			// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
			locData.direction = location.getDerect();
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mMapView.refresh();
			// 是手动触发请求或首次定位时，移动到定位点
			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				Log.d("LocationOverlay", "receive location, animate to it");
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				isRequest = false;
				myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
				requestLocButton.setText("跟随");
				mCurBtnType = Location.E_BUTTON_TYPE.FOLLOW;
			}
			// 首次定位完成
			isFirstLoc = false;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	// 继承MyLocationOverlay重写dispatchTap实现点击处理
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			// TODO Auto-generated method stub
			// 处理点击事件,弹出泡泡
			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText("我的位置");
			pop.showPopup(BMapUtil.getBitmapFromView(popupText), new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)), 8);
			return true;
		}

	}

	/**
	 * 设置是否显示交通图
	 * 
	 * @param view
	 */
	public void setTraffic(View view) {
		mMapView.setTraffic(((CheckBox) view).isChecked());
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void initOverlay() {
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_marka), mMapView);
		/**
		 * 准备overlay 数据
		 */
		GeoPoint p1 = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		OverlayItem item1 = new OverlayItem(p1, "覆盖物1", "");
		/**
		 * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
		 */
		item1.setMarker(getResources().getDrawable(R.drawable.icon_marka));

		mOverlay.addItem(item1);
		// mOverlay.addItem(item2);
		// mOverlay.addItem(item3);
		// mOverlay.addItem(item4);
		/**
		 * 保存所有item，以便overlay在reset后重新添加
		 */
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());

		/**
		 * 将overlay 添加至MapView中
		 */
		mMapView.getOverlays().add(mOverlay);

		/**
		 * 刷新地图
		 */
		mMapView.refresh();

		/**
		 * 向地图添加自定义View.
		 */

		viewCache2 = getLayoutInflater().inflate(R.layout.custom_text_view,
				null);
		popupInfo = (View) viewCache2.findViewById(R.id.popinfo);
		popupLeft = (View) viewCache2.findViewById(R.id.popleft);
		popupRight = (View) viewCache2.findViewById(R.id.popright);
		popupText2 = (TextView) viewCache2.findViewById(R.id.textcache);

		button = new Button(this);
		button.setBackgroundResource(R.drawable.popup);

		/**
		 * 创建一个popupoverlay
		 */
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				if (index == 0) {
					// 更新item位置
					pop2.hidePop();
					GeoPoint p = new GeoPoint(mCurItem.getPoint()
							.getLatitudeE6() + 5000, mCurItem.getPoint()
							.getLongitudeE6() + 5000);
					mCurItem.setGeoPoint(p);
					mOverlay.updateItem(mCurItem);
					mMapView.refresh();
				} else if (index == 2) {
					// 更新图标
					mCurItem.setMarker(getResources().getDrawable(
							R.drawable.nav_turn_via_1));
					mOverlay.updateItem(mCurItem);
					mMapView.refresh();
				}
			}
		};
		pop2 = new PopupOverlay(mMapView, popListener);

	}

	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			OverlayItem item = getItem(index);
			mCurItem = item;
			if (index == 0) {
				button.setText("这是一个系统控件");
				GeoPoint pt = new GeoPoint((int) (lat * 1E6),
						(int) (lon * 1E6));
				// 弹出自定义View
				pop2.showPopup(button, pt, 16);
				//点击按钮弹出事件
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(QyInfoMapActivity.this,
								QyInfoDetailsActivity.class);
						startActivity(intent);

					}
				});

			} else {
				popupText2.setText(getItem(index).getTitle());
				Bitmap[] bitMaps = { BMapUtil.getBitmapFromView(popupLeft),
						BMapUtil.getBitmapFromView(popupInfo),
						BMapUtil.getBitmapFromView(popupRight) };
				pop2.showPopup(bitMaps, item.getPoint(), 32);
			}
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			if (pop2 != null) {
				pop2.hidePop();
				mMapView.removeView(button);
			}
			return false;
		}

	}
	// 返回键
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				QyInfoMapActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
			}
			return super.onKeyDown(keyCode, event);
		}

}
