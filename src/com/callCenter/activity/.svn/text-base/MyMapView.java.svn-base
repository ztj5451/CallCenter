package com.callCenter.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupOverlay;

/**
 * 自定义地图图层
 * 
 * @author Administrator
 * 
 */
class MyMapView extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

	public MyMapView(Context context) {
		super(context);
		
	}

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			
			try {
				if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
					pop.hidePop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
}
