package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.Area;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AreaAdapter extends BaseAdapter {
	private Context mContext;

	private List<Area> mareaList;

	public AreaAdapter(Context context, List<Area> areaList) {
		mContext = context;
		mareaList = areaList;
	}

	public int getCount() {
		return mareaList.size();
	}

	public Object getItem(int position) {
		return mareaList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.area_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.area);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(mareaList.get(position).getName());
		return convertView;
	}

	class ViewHolder {

		TextView name;
	}
}