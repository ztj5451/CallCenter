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

public class PictureListAdapter extends BaseAdapter {
	private Context mContext;

	private List<String> mpictureList;

	public PictureListAdapter(Context context, List<String> pictureList) {
		mContext = context;
		mpictureList = pictureList;
	}

	public int getCount() {
		return mpictureList.size();
	}

	public Object getItem(int position) {
		return mpictureList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.picture_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.id = (TextView) convertView.findViewById(R.id.id);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(mpictureList.get(position));
		
		for (int i = 0; i < mpictureList.size(); i++) {
			holder.id.setText("# "+i);

		}

		return convertView;
	}

	class ViewHolder {

		TextView name, id;
	}
}