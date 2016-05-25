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
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
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
public class SurroundQyMapActivity extends Activity {
	SharedPreferences sharedPreferences = null;
	// 企业传递过来的经纬度
	private double qy_lat, qy_lon;
	// 企业名称
	private String qyName = null;
	// 地图覆盖物
	private MyOverlay mOverlay = null;
	private PopupOverlay pop2 = null;
	private ArrayList<OverlayItem> mItems = null;
	private Button button = null;
	private MapView.LayoutParams layoutParam = null;
	private OverlayItem mCurItem = null;
	LocationData locData = null;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedPreferences = getSharedPreferences("location",
				Context.MODE_PRIVATE);

		// 初始化地图引擎，若初始化成功，则加载地图
		if (Location.m_bKeyRight == true) {

			setContentView(R.layout.surround_qy_map);

		} else {
			setContentView(R.layout.surround_qy_map);
		}
		init();

	}

	// 初始化
	private void init() {

		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SurroundQyMapActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
		// 企业位置
		qy_lat = Double.parseDouble(getIntent().getExtras().getString("lat"));
		qy_lon = Double.parseDouble(getIntent().getExtras().getString("lon"));
		qyName = getIntent().getExtras().getString("name");
		System.out.println("经度:" + qy_lon + "纬度:" + qy_lat);
		// 地图初始化
		mMapView = (MyMapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(16);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		// 设置地图初始加载位置
		GeoPoint p = new GeoPoint((int) (qy_lat * 1E6), (int) (qy_lon * 1E6));
		mMapController.setCenter(p);
		// 创建 弹出泡泡图层
		createPaopao();
		// 弹出企业信息
		initOverlay();

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
			// popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText("我的位置");
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
		// 退出时销毁定位
		// if (mLocClient != null)
		// mLocClient.stop();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void initOverlay() {
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_marka), mMapView);
		GeoPoint geoPoint = new GeoPoint((int) (qy_lat * 1E6),
				(int) (qy_lon * 1E6));
		OverlayItem item = new OverlayItem(geoPoint, "", "");
		item.setMarker(getResources().getDrawable(R.drawable.icon_marka));
		mOverlay.addItem(item);
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());

		mMapView.getOverlays().add(mOverlay);

		/**
		 * 刷新地图
		 */
		mMapView.refresh();
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
				button.setText(qyName);
				GeoPoint pt = new GeoPoint((int) (qy_lat * 1E6),
						(int) (qy_lon * 1E6));
				// 弹出自定义View
				pop2.showPopup(button, pt, 16);
				// 点击按钮弹出事件
				// button.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View view) {
				// // TODO Auto-generated method stub
				// Intent intent = new Intent(SurroundQyMapActivity.this,
				// ForMapQyDetailsActivity.class);
				// intent.putExtra("lat", qy_lat);
				// intent.putExtra("lon", qy_lon);
				// intent.putExtra("qyName", qyName);
				// startActivity(intent);
				// overridePendingTransition(R.anim.in_from_right,
				// R.anim.out_to_left);
				//
				// }
				// });

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
			SurroundQyMapActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
