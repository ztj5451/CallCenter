package com.callCenter.activity;

import java.util.ArrayList;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.callCenter.location.Location;

/**
 * 查看企业详信息后 在地图上展示企业信息
 * 
 * @author Administrator
 * 
 */
public class QyDetailMapActivity extends Activity {
	// 企业位置
	private double lat, lon;
	// 地图覆盖物
	private MyOverlay mOverlay = null;
	private ArrayList<OverlayItem> mItems = null;
	// 定位图层
	locationOverlay myLocationOverlay = null;
	// 弹出泡泡图层
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MyMapView mMapView = null; // 地图View
	private MapController mMapController = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 初始化地图引擎，若初始化成功，则加载地图
		if (Location.m_bKeyRight == true) {

			setContentView(R.layout.qy_detail_map);

		} else {
			setContentView(R.layout.qy_detail_map);
		}
		init();
		// 企业位置
		lat = getIntent().getExtras().getDouble("lat");
		lon = getIntent().getExtras().getDouble("lon");
		// 地图初始化
		mMapView = (MyMapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(16);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		// 企业显示地图中央
		System.out.println(lat + "," + lon);
		GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		mMapController.setCenter(p);
		// 创建 弹出泡泡图层
		createPaopao();
		// 弹出企业信息
		initOverlay();

		// 定位图层初始化
		myLocationOverlay = new locationOverlay(mMapView);

		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();

	}

	// 初始化
	private void init() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						QyDetailMapActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
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
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@SuppressWarnings("unchecked")
	public void initOverlay() {
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_marka), mMapView);
		/**
		 * 准备overlay 数据
		 */
		GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		OverlayItem item = new OverlayItem(geoPoint, "", "");
		/**
		 * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
		 */
		item.setMarker(getResources().getDrawable(R.drawable.icon_marka));

		mOverlay.addItem(item);
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

	}

	@SuppressWarnings("rawtypes")
	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			return false;
		}

	}

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			QyDetailMapActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
