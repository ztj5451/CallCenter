package com.callCenter.activity;

import java.util.ArrayList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
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
public class MapActivity extends Activity {
	private boolean mapFlag = false;
	// 地图初始经纬度
	private double lat, lon;
	SharedPreferences sharedPreferences = null;
	// 定位相关
	LocationData locData = null;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map);
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
		viewCache = getLayoutInflater()
				.inflate(R.layout.custom_text_view, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		// 地图初始化
		mMapView = (MyMapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(16);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		// 将当前位置显示在手机地图中心
		mMapController.setCenter(p);
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
						MapActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});

		// 切换地图模式
		this.findViewById(R.id.changeMode).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						if (!mapFlag) {
							mMapView.setSatellite(true);
							mapFlag = true;
						} else {
							mMapView.setSatellite(false);
							mapFlag = false;
						}
					}
				});
	}

	// 继承MyLocationOverlay重写dispatchTap实现点击处理
	public class LocationOverlay extends MyLocationOverlay {

		public LocationOverlay(MapView mapView) {
			super(mapView);

		}

		@SuppressLint("NewApi")
		@Override
		protected boolean dispatchTap() {

			// 处理点击事件,弹出泡泡
		//	popupText.setBackgroundResource(R.drawable.popup);
			if (Location.addStreet.isEmpty()) {
				popupText.setText("我的当前位置");
			} else {
				popupText.setText(" " + Location.addStreet + " ");
			}

			pop.showPopup(BMapUtil.getBitmapFromView(popupText), new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)), 8);
			return true;
		}

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

	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// mMapView.onSaveInstanceState(outState);
	//
	// }
	//
	// @Override
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// super.onRestoreInstanceState(savedInstanceState);
	// mMapView.onRestoreInstanceState(savedInstanceState);
	// }

	// 返回键监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MapActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
