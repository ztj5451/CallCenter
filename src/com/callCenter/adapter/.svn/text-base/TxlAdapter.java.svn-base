package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.Bm;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TxlAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	List<Bm> bmList;

	// 构造函数
	public TxlAdapter(Context context, List<Bm> bmList) {
		inflater = LayoutInflater.from(context);
		this.bmList = bmList;
		this.context = context;

	}

	@Override
	public int getCount() {

		return bmList.size();
	}

	@Override
	public Object getItem(int position) {

		return bmList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentGroup) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.txl_item, null);
			viewHolder = new ViewHolder();

			viewHolder.bmName = (TextView) convertView
					.findViewById(R.id.bmName);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		viewHolder.bmName.setText(bmList.get(position).getBmName());

		return convertView;
	}

	/**
	 * adapter中的属性
	 * 
	 * @author Administrator
	 * 
	 */
	public final class ViewHolder {
		public TextView bmName;

	}

}
