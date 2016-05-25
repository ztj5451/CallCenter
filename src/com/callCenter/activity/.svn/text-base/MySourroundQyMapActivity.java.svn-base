package com.callCenter.activity;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.map.LocationData;
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

/**
 * 在手机端展示地图
 * 
 * @author Administrator
 * 
 */
public class MySourroundQyMapActivity extends Activity {
	// 地图初始经纬度
	private double lat, lon;
	SharedPreferences sharedPreferences = null;
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
	LocationData locData = null;
	// public MyLocationListenner myListener = new MyLocationListenner();
	// 定位图层
	LocationOverlay myLocationOverlay = null;
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
		setContentView(R.layout.mysourround_qymap);
		init();
		Event();

	}

	// 初始化
	private void init() {
		// 获取传递过来的经纬度
		sharedPreferences = getSharedPreferences("location",
				Context.MODE_PRIVATE);
		lat = sharedPreferences.getFloat("lat", (float) 0.0);
		lon = sharedPreferences.getFloat("lon", (float) 0.0);
		requestLocButton = (Button) findViewById(R.id.button1);
		viewCache = getLayoutInflater()
				.inflate(R.layout.custom_text_view, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);

		mCurBtnType = Location.E_BUTTON_TYPE.LOC;
		// 地图初始化
		mMapView = (MyMapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(16);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		// 将当前位置显示在手机地图中心
		mMapController.setCenter(p);
		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
		group.setOnCheckedChangeListener(radioButtonListener);
		// 创建 弹出泡泡图层
		createPaopao();
		// 定位初始化
		locData = Location.locationData;
		// 定位图层初始化
		myLocationOverlay = new LocationOverlay(mMapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();

	}

	// 地图上弹出泡泡
	private void createPaopao() {
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
				Toast.makeText(MySourroundQyMapActivity.this, "paopao",
						Toast.LENGTH_LONG).show();

			}
		};
		pop = new PopupOverlay(mMapView, popListener);
		MyMapView.pop = pop;

	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MySourroundQyMapActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
		// 进入企业列表
		this.findViewById(R.id.enter).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MySourroundQyMapActivity.this,
						SurroundQyActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
		requestLocButton.setOnClickListener(new ButtonClick());
	}

	// 改变地图显示模式
	public class ButtonClick implements OnClickListener {

		@Override
		public void onClick(View view) {
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

	}

	// 改变地图展示模式
	public class ChangeModel implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (1) {
			case 1:
				mMapView.setSatellite(false);
				break;
			case 2:
				mMapView.setSatellite(true);
				break;
			case 3:
				mMapView.setTraffic(true);
				break;

			default:
				break;
			}

		}

	}

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		isRequest = true;
		Location.mLocationClient.requestLocation();
		// mLocClient.requestLocation();
		Toast.makeText(MySourroundQyMapActivity.this, "正在定位……",
				Toast.LENGTH_SHORT).show();
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

	// 继承MyLocationOverlay重写dispatchTap实现点击处理
	public class LocationOverlay extends MyLocationOverlay {

		public LocationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			// TODO Auto-generated method stub
			// 处理点击事件,弹出泡泡
			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText("我附近的企业个数为4个");
			pop.showPopup(BMapUtil.getBitmapFromView(popupText), new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)), 16);		
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

	// 返回键监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MySourroundQyMapActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
