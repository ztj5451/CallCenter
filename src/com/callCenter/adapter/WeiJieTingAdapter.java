package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.JieTing;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WeiJieTingAdapter extends BaseAdapter {
	private Context mContext;

	private List<JieTing> mJieTingList;

	public WeiJieTingAdapter(Context context, List<JieTing> jieTing) {
		mContext = context;
		mJieTingList = jieTing;
	}

	public int getCount() {
		return mJieTingList.size();
	}

	public Object getItem(int position) {
		return mJieTingList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.weijieting, null);
			holder = new ViewHolder();
			holder.kh_name = (TextView) convertView.findViewById(R.id.kh_name);
			holder.kh_sex = (TextView) convertView.findViewById(R.id.kh_sex);
			holder.cardId = (TextView) convertView.findViewById(R.id.cardId);
			holder.sheng = (TextView) convertView.findViewById(R.id.sheng);
			holder.shi = (TextView) convertView.findViewById(R.id.shi);
			holder.qu = (TextView) convertView.findViewById(R.id.qu);
			holder.jiedao = (TextView) convertView.findViewById(R.id.jiedao);
			holder.sq = (TextView) convertView.findViewById(R.id.sq);
			holder.zdy = (TextView) convertView.findViewById(R.id.zdy);
			holder.address = (TextView) convertView.findViewById(R.id.address);
			holder.jg = (TextView) convertView.findViewById(R.id.jg);
			holder.kh_phone = (TextView) convertView
					.findViewById(R.id.kh_phone);
			holder.zc_time = (TextView) convertView.findViewById(R.id.zc_time);
			holder.goutong = (TextView) convertView.findViewById(R.id.goutong);

			holder.email = (TextView) convertView.findViewById(R.id.email);
			holder.xl = (TextView) convertView.findViewById(R.id.xl);
			holder.isDb = (TextView) convertView.findViewById(R.id.isDb);
			holder.lastTime = (TextView) convertView
					.findViewById(R.id.lastTime);
			holder.ye = (TextView) convertView.findViewById(R.id.ye);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.kh_name.setText(mJieTingList.get(position).getKh_name());
		holder.kh_sex.setText(mJieTingList.get(position).getKh_sex());
		holder.cardId.setText(mJieTingList.get(position).getCardId());
		holder.sheng.setText(mJieTingList.get(position).getSheng());
		holder.shi.setText(mJieTingList.get(position).getShi());
		holder.qu.setText(mJieTingList.get(position).getQu());
		holder.jiedao.setText(mJieTingList.get(position).getJiedao());
		holder.sq.setText(mJieTingList.get(position).getSq());
		holder.zdy.setText(mJieTingList.get(position).getZdy());
		holder.address.setText(mJieTingList.get(position).getAddress());
		holder.jg.setText(mJieTingList.get(position).getJg());
		holder.kh_phone.setText(mJieTingList.get(position).getKh_phone());
		holder.zc_time.setText(mJieTingList.get(position).getZc_time());
		holder.goutong.setText(mJieTingList.get(position).getGoutong());
		holder.email.setText(mJieTingList.get(position).getEmail());
		holder.xl.setText(mJieTingList.get(position).getXl());
		holder.isDb.setText(mJieTingList.get(position).getIsDb());
		holder.lastTime.setText(mJieTingList.get(position).getLastTime());
		holder.ye.setText(mJieTingList.get(position).getYe());
		return convertView;
	}

	class ViewHolder {

		TextView kh_name, kh_sex, cardId, sheng, shi, qu, jiedao, sq, zdy,
				address, jg, kh_phone, zc_time, goutong, email, xl, isDb,
				lastTime, ye;

	}

}