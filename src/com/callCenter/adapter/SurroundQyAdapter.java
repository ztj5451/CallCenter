package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.CustomInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SurroundQyAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	List<CustomInfo> qyInfo;

	// 构造函数
	public SurroundQyAdapter(Context context, List<CustomInfo> qyInfo) {
		inflater = LayoutInflater.from(context);
		this.qyInfo = qyInfo;
		this.context = context;

	}

	@Override
	public int getCount() {

		return qyInfo.size();
	}

	@Override
	public Object getItem(int position) {

		return qyInfo.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentGroup) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.qyinfo_item, null);
			viewHolder = new ViewHolder();

			viewHolder.qyName = (TextView) convertView
					.findViewById(R.id.qyName);
			viewHolder.tel = (TextView) convertView.findViewById(R.id.tel);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		viewHolder.qyName.setText(qyInfo.get(position).getName());
		viewHolder.tel.setText(qyInfo.get(position).getTel());

		return convertView;
	}

	/**
	 * adapter中的属性
	 * 
	 * @author Administrator
	 * 
	 */
	public final class ViewHolder {
		public TextView qyName, tel;

	}

}
