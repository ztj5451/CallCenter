package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.Area;
import com.callCenter.entity.RechargeCustom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RechargeAdapter extends BaseAdapter {
	private Context mContext;

	private List<RechargeCustom> mrechargeList;

	public RechargeAdapter(Context context, List<RechargeCustom> rechargeList) {
		mContext = context;
		mrechargeList = rechargeList;
	}

	public int getCount() {
		return mrechargeList.size();
	}

	public Object getItem(int position) {
		return mrechargeList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.recharge_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(mrechargeList.get(position).getName());
		return convertView;
	}

	class ViewHolder {

		TextView name;
	}
}