package com.callCenter.adapter;

import java.util.List;
import com.callCenter.activity.R;
import com.callCenter.entity.CallPhone;
import com.callCenter.entity.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 接收消息Adapter
 * 
 * @author Administrator
 * 
 */
public class CallCenterAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	List<CallPhone> customerList;

	// 构造方法
	public CallCenterAdapter(Context context, List<CallPhone> customerList) {
		inflater = LayoutInflater.from(context);
		this.customerList = customerList;
		this.context = context;

	}

	@Override
	public int getCount() {

		return customerList.size();
	}

	@Override
	public Object getItem(int position) {

		return customerList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentGroup) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.callcenter_item, null);
			viewHolder = new ViewHolder();

			viewHolder.kh_name = (TextView) convertView
					.findViewById(R.id.kh_name);
			viewHolder.sex = (TextView) convertView.findViewById(R.id.sex);
			viewHolder.kh_address = (TextView) convertView
					.findViewById(R.id.kh_address);
			viewHolder.sheng = (TextView) convertView.findViewById(R.id.sheng);
			viewHolder.shi = (TextView) convertView.findViewById(R.id.shi);
			viewHolder.qu = (TextView) convertView.findViewById(R.id.qu);
			viewHolder.jiedao = (TextView) convertView
					.findViewById(R.id.jiedao);
			viewHolder.sq = (TextView) convertView.findViewById(R.id.sq);
			viewHolder.zdy = (TextView) convertView.findViewById(R.id.zdy);
			viewHolder.jg = (TextView) convertView.findViewById(R.id.jg);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		try {
			if (customerList.size() != 0) {
				viewHolder.kh_name.setText(customerList.get(position)
						.getKh_name());
				viewHolder.sex.setText(customerList.get(position).getSex());
				viewHolder.kh_address.setText(customerList.get(position)
						.getKh_address());
				viewHolder.sheng.setText(customerList.get(position).getSheng());
				viewHolder.shi.setText(customerList.get(position).getShi());
				viewHolder.qu.setText(customerList.get(position).getQu());
				viewHolder.jiedao.setText(customerList.get(position)
						.getJiedao());
				viewHolder.sq.setText(customerList.get(position).getSq());
				viewHolder.zdy.setText(customerList.get(position).getZdy());
				viewHolder.jg.setText(customerList.get(position).getJg());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return convertView;
	}

	/**
	 * 存放控件
	 */
	public final class ViewHolder {
		public TextView sex, kh_name, kh_address, sheng, shi, qu, jiedao, sq,
				zdy, jg;

	}

}
