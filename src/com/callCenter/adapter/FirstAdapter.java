package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FirstAdapter extends BaseAdapter {
	private Context mContext;

	private List<String> mfirstList;

	public FirstAdapter(Context context, List<String> firstList) {
		mContext = context;
		mfirstList = firstList;
	}

	public int getCount() {
		return mfirstList.size();
	}

	public Object getItem(int position) {
		return mfirstList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.first_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.first);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(mfirstList.get(position));
		return convertView;
	}

	class ViewHolder {

		TextView name;
	}
}