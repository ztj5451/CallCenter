package com.callCenter.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.callCenter.adapter.FirstAdapter;
import com.callCenter.adapter.SecondAdapter;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.RequestAndResultCode;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectMapActivity extends Activity {
	private double lat, lng;
	private double mLat = 0.0, mLng = 0.0;
	private SettingUtils settingUtils;
	JSONObject jsonObject;
	String[] provinceData = null;
	Map<String, String[]> cityMapData = new HashMap<String, String[]>();
	private Map<String, String[]> city;
	private LinearLayout firstLayout;
	private ListView firstListView, secondListView;
	private List<String> firstList = null, secondList = null, order = null;
	private FirstAdapter adapter;
	private SecondAdapter seconAdapter;
	private boolean isOpen = false;
	private int checkPosition;
	private String selp, selc;
	private EditText edit;
	private boolean flag = false;
	private String tel;

	/** --------------- **/

	private MapView mMapView = null;
	private MapController mMapController = null;

	MKMapViewListener mMapListener = null;
	/**
	 * 用于截获屏坐标
	 */
	MKMapTouchListener mapTouchListener = null;
	/**
	 * 当前地点击点
	 */
	private GeoPoint currentPt = null;

	private String touchType = null;

	private TextView mStateBar = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case HandlerException.Success:
				GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
				mMapController.setCenter(p);
				mMapView.refresh();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.Fail:
				break;
			case HandlerException.AllSuccess:
				SelectMapActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_map);
		settingUtils = new SettingUtils(SelectMapActivity.this);
		Event();
		tel = getIntent().getExtras().getString("tel");
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(14);
		mStateBar = (TextView) findViewById(R.id.state);
		initListener();
		double cLat = 43.88;
		double cLon = 125.35;
		GeoPoint p = new GeoPoint((int) (cLat * 1E6), (int) (cLon * 1E6));
		mMapController.setCenter(p);

	}

	// 定位事件
	private void Event() {
		// 确定
		this.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (mLat != 0.0 && mLng != 0.0) {
					final StringBuffer infoBuffer = new StringBuffer();
					infoBuffer.append(mLng).append("@").append(mLat)
							.append("@").append(tel);
					new Thread() {
						public void run() {
							uploadAction(uploadUrl(infoBuffer.toString()));
						};
					}.start();
				} else {
					Toast.makeText(SelectMapActivity.this, "您没有选择地图上的任何位置!",
							Toast.LENGTH_SHORT).show();
				}

				// Intent intent = new Intent();
				// intent.putExtra("lat", mLat);
				// intent.putExtra("lng", mLng);
				// setResult(RequestAndResultCode.SELECT_MAP, intent);

			}
		});
		// 选择地域
		this.findViewById(R.id.location).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (!isOpen) {
							initJsonData();
							initDatas();
							init();
							OnClick();
							isOpen = true;
						} else {
							firstLayout.setVisibility(View.GONE);
							if (firstList != null) {
								firstList.clear();
							}
							if (secondList != null) {
								secondList.clear();
							}

							isOpen = false;
						}

					}
				});
	}

	// 公文发起URL
	private StringBuffer uploadUrl(String info) {
		StringBuffer gwStartUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwStartUrl.append(SettingUtils.get("upload_ladAndLng_url"))
				.append("info=").append(info);
		return gwStartUrl;
	}

	private void uploadAction(StringBuffer loginUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				handler.sendEmptyMessage(HandlerException.AllSuccess);
			}
		} catch (Exception e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	private void initListener() {
		/**
		 * 设置地图点击事件监听
		 */
		mapTouchListener = new MKMapTouchListener() {
			@Override
			public void onMapClick(GeoPoint point) {
				touchType = "单击";
				currentPt = point;
				updateMapState();

			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {
				touchType = "双击";
				currentPt = point;
				updateMapState();
			}

			@Override
			public void onMapLongClick(GeoPoint point) {
				touchType = "长按";
				currentPt = point;
				updateMapState();
			}
		};
		mMapView.regMapTouchListner(mapTouchListener);
		/**
		 * 设置地图事件监听
		 */
		mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * 在此处理地图移动完成回调 缩放，平移等操作完成后，此回调被触发
				 */
				updateMapState();
			}

			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件 显示底图poi名称并移动至该点 设置过：
				 * mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */

			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 * 地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
				updateMapState();
			}

			@Override
			public void onMapLoadFinish() {
				// TODO Auto-generated method stub

			}
		};

	}

	/**
	 * 更新地图状态显示面板
	 */
	private void updateMapState() {
		if (mStateBar == null) {
			return;
		}
		String state = "";
		if (currentPt == null) {
			state = "点击、长按、双击地图以获取经纬度和地图状态";
		} else {
			state = String.format(touchType + ",当前经度 ： %f 当前纬度：%f",
					currentPt.getLongitudeE6() * 1E-6,
					currentPt.getLatitudeE6() * 1E-6);
			mLat = currentPt.getLatitudeE6() * 1E-6;
			mLng = currentPt.getLongitudeE6() * 1E-6;
		}
		// state += "\n";
		// state += String
		// .format("zoom level= %.1f    rotate angle= %d   overlaylook angle=  %d",
		// mMapView.getZoomLevel(), mMapView.getMapRotation(),
		// mMapView.getMapOverlooking());
		mStateBar.setText(state);
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

	/** -------------------------- **/
	private void init() {

		firstListView = (ListView) this.findViewById(R.id.first);
		secondListView = (ListView) this.findViewById(R.id.second);
		firstLayout = (LinearLayout) this.findViewById(R.id.firstLayout);
		firstLayout.setVisibility(View.VISIBLE);
		city = new HashMap<String, String[]>();
		firstList = new ArrayList<String>();
		Set<String> prov = cityMapData.keySet();
		Iterator<String> it = prov.iterator();
		while (it.hasNext()) {
			firstList.add(it.next());

		}

		adapter = new FirstAdapter(SelectMapActivity.this, firstList);
		firstListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

	}

	// 点击事件
	private void OnClick() {
		firstListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {

				if (checkPosition == position) {
					secondListView.setVisibility(View.GONE);
					checkPosition = 100;
				} else {
					checkPosition = position;
					selp = firstList.get(position);

					String temp[] = cityMapData.get(selp);
					secondList = new ArrayList<String>();
					for (int i = 0; i < temp.length; i++) {
						secondList.add(temp[i]);
						seconAdapter = new SecondAdapter(
								SelectMapActivity.this, secondList);
						secondListView.setAdapter(seconAdapter);
						seconAdapter.notifyDataSetChanged();

						secondListView.setVisibility(View.VISIBLE);
					}
				}

			}
		});
		secondListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selc = secondList.get(position);
				// Toast.makeText(SelectMapActivity.this, selp + selc,
				// Toast.LENGTH_LONG).show();
				firstList.clear();
				secondList.clear();
				firstLayout.setVisibility(View.GONE);
				new Thread() {
					public void run() {
						addressParse();
					};
				}.start();

			}
		});

	}

	private void addressParse() {
		try {
			StringBuffer geoUrl = new StringBuffer(
					SettingUtils.get("address_to_geo"));
			geoUrl.append(UrlEncode.Encode(selp.trim() + selc.trim()));
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					geoUrl.toString(), "GET");

			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);

				message = message.replace("renderOption&&renderOption(", "")
						.replace(")", "");
				JSONObject object = new JSONObject(message);
				String result = object.getString("result");
				String status = object.getString("status");
				if (status.equals("0")) {
					JSONObject temp = new JSONObject(result);
					String ss = temp.getString("location");
					JSONObject location = new JSONObject(ss);
					lat = location.getDouble("lat");
					lng = location.getDouble("lng");
					handler.sendEmptyMessage(HandlerException.Success);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	// 读取json文件
	private void initJsonData() {
		try {
			StringBuffer sb = new StringBuffer();
			InputStream is = SelectMapActivity.this.getAssets().open(
					"city.json");
			int len = -1;
			byte[] buf = new byte[1024];
			while ((len = is.read(buf)) != -1) {
				sb.append(new String(buf, 0, len, "gbk"));
			}
			is.close();
			jsonObject = new JSONObject(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 解析文件
	private void initDatas() {
		try {
			JSONArray jsonArray = jsonObject.getJSONArray("citylist");
			provinceData = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
				String province = jsonP.getString("p");// 省名字

				provinceData[i] = province;

				JSONArray jsonCs = null;
				try {

					jsonCs = jsonP.getJSONArray("c");
				} catch (Exception e1) {
					continue;
				}
				String[] mCitiesDatas = new String[jsonCs.length()];
				for (int j = 0; j < jsonCs.length(); j++) {
					JSONObject jsonCity = jsonCs.getJSONObject(j);
					String city = jsonCity.getString("n");// 市名字
					mCitiesDatas[j] = city;
					JSONArray jsonAreas = null;
					try {
						jsonAreas = jsonCity.getJSONArray("a");
					} catch (Exception e) {
						continue;
					}

				}

				cityMapData.put(province, mCitiesDatas);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		jsonObject = null;
	}

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SelectMapActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
