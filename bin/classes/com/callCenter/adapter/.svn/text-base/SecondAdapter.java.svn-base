package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SecondAdapter extends BaseAdapter {
	private Context mContext;

	private List<String> msecondList;

	public SecondAdapter(Context context, List<String> secondList) {
		mContext = context;
		msecondList = secondList;
	}

	public int getCount() {
		return msecondList.size();
	}

	public Object getItem(int position) {
		return msecondList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.second_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.second);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(msecondList.get(position));
		return convertView;
	}

	class ViewHolder {

		TextView name;
	}
}