package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;

public class SourroundMapActivity extends Activity {

	private SharedPreferences sharedPreferences;
	private String totalCountStart = "您附近共有";
	private String totalCountEnd = "位客户";
	private String totalCount = "0";
	private double lat, lon;
	private SettingUtils settingUtils;
	private MapView mMapView = null;
	private MapController mMapController = null;
	private MyOverlay mOverlay = null;
	private PopupOverlay pop = null;
	private ArrayList<OverlayItem> mItems = null;
	private Button button = null;
	// private MapView.LayoutParams layoutParam = null;
	// private OverlayItem mCurItem = null;
	/**
	 * overlay 位置坐标
	 */

	private ProgressBar progressBar;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				progressBar.setVisibility(View.GONE);
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				progressBar.setVisibility(View.GONE);
				Toast.makeText(SourroundMapActivity.this, "服务器没有响应!",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerException.Fail:
				progressBar.setVisibility(View.GONE);
				Toast.makeText(SourroundMapActivity.this, "加载服务器错误!",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sourround_map);
		mMapView = (MapView) findViewById(R.id.bmapView);
		// 初始化
		init();
		// 添加事件
		Event();
		// 获取传递过来的经纬度
		sharedPreferences = getSharedPreferences("location",
				Context.MODE_PRIVATE);
		lat = sharedPreferences.getFloat("lat", (float) 0.0);
		lon = sharedPreferences.getFloat("lon", (float) 0.0);
		System.out.println("lat:" + lat + ",lon:" + lon);

		// 初始化地图
		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(16);
		mMapView.setBuiltInZoomControls(true);
		initOverlay();
		// 设置地图初始加载位置
		GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		mMapController.setCenter(p);
		// 加载当前周边企业
		new Thread() {
			public void run() {
				progressBar.setVisibility(View.VISIBLE);
				sourroundQyAction();
			};
		}.start();
	}

	// 初始化
	private void init() {
		settingUtils = new SettingUtils(SourroundMapActivity.this);
		progressBar = (ProgressBar) this.findViewById(R.id.progress);
	}

	// 按钮添加监听
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						SourroundMapActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});

	}

	@SuppressWarnings("unchecked")
	public void initOverlay() {
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_marka), mMapView);
		GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		OverlayItem item = new OverlayItem(geoPoint, "", "");
		item.setMarker(getResources().getDrawable(R.drawable.icon_gcoding));
		/**
		 * 将item 添加到overlay中 注意： 同一个itme只能add一次
		 */
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
		button = new Button(this);
		button.setBackgroundResource(R.drawable.popup);

		/**
		 * 创建一个popupoverlay
		 */
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
			}
		};
		pop = new PopupOverlay(mMapView, popListener);

	}

	/**
	 * 清除所有Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view) {
		mOverlay.removeAll();
		if (pop != null) {
			pop.hidePop();
		}
		mMapView.removeView(button);
		mMapView.refresh();

	}

	/**
	 * 重新添加Overlay
	 * 
	 * @param view
	 */
	public void resetOverlay(View view) {
		// 重新add overlay
		mOverlay.addItem(mItems);
		mMapView.refresh();

	}

	@Override
	protected void onPause() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
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

	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			OverlayItem item = getItem(index);
			// mCurItem = item;
			if (index == 0) {
				
				totalCountStart = totalCountStart + totalCount + totalCountEnd;
				button.setText(totalCountStart);
				totalCountStart = "您附近共有";
				GeoPoint pt = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
				// 弹出自定义View
				pop.showPopup(button, pt, 24);
				if (totalCount.equals("0")) {
					Toast.makeText(SourroundMapActivity.this, "您当前位置没有可查看的用户！",
							Toast.LENGTH_SHORT).show();
				} else {
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(
									SourroundMapActivity.this,
									SurroundQyActivity.class);
							intent.putExtra("lat", lat);
							intent.putExtra("lon", lon);
							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);

						}
					});
				}

			}
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			if (pop != null) {
				pop.hidePop();
				mMapView.removeView(button);
			}
			return false;
		}

	}

	// 获取周边企业个数url
	private StringBuffer sourroundQyUrl() {
		StringBuffer sourroundQyUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		sourroundQyUrl.append(SettingUtils.get("sourround_count_url"))
				.append("jd=").append(lon).append("&wd=").append(lat);
		System.out.println("周边用户数url:" + sourroundQyUrl);
		return sourroundQyUrl;

	}

	// 获取周边企业
	private void sourroundQyAction() {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					sourroundQyUrl(), "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				totalCount = object.getString("num");
				handler.sendEmptyMessage(HandlerException.Success);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		}
	}

	// 返回键监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SourroundMapActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
