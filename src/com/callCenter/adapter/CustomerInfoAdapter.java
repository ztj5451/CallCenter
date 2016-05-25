package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.Area;
import com.callCenter.entity.CustomerInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomerInfoAdapter extends BaseAdapter {
	private Context mContext;

	private List<CustomerInfo> mCustomerInfoList;

	public CustomerInfoAdapter(Context context, List<CustomerInfo> customerList) {
		mContext = context;
		mCustomerInfoList = customerList;
	}

	public int getCount() {
		return mCustomerInfoList.size();
	}

	public Object getItem(int position) {
		return mCustomerInfoList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.customer_info_item, null);
			holder = new ViewHolder();

			holder.kh_name = (TextView) convertView.findViewById(R.id.kh_name);
			holder.moneys = (TextView) convertView.findViewById(R.id.moneys);
			holder.kh_tel = (TextView) convertView.findViewById(R.id.kh_tel);
			holder.kh_cardId = (TextView) convertView
					.findViewById(R.id.kh_cardId);
			holder.kh_address = (TextView) convertView
					.findViewById(R.id.kh_address);
			holder.sex = (TextView) convertView.findViewById(R.id.sex);
			holder.id=(TextView) convertView.findViewById(R.id.id);
			holder.ssjg=(TextView) convertView.findViewById(R.id.ssjg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.id.setText((position + 1) + "");
		holder.kh_name.setText(mCustomerInfoList.get(position).getKh_name());
		holder.moneys.setText(mCustomerInfoList.get(position).getMoneys());
		holder.kh_tel.setText(mCustomerInfoList.get(position).getKh_tel());
		holder.kh_cardId
				.setText(mCustomerInfoList.get(position).getKh_cardId());
		holder.kh_address.setText(mCustomerInfoList.get(position).getAddress());
		holder.sex.setText(mCustomerInfoList.get(position).getKh_sex());
		holder.ssjg.setText(mCustomerInfoList.get(position).getJg());
		return convertView;
	}

	class ViewHolder {

		TextView moneys, kh_tel, kh_name, kh_cardId, kh_address, sex,id,ssjg;
	}
}