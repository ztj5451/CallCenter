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

public class JieTingAdapter extends BaseAdapter {
	private Context mContext;

	private List<JieTing> mJieTingList;

	public JieTingAdapter(Context context, List<JieTing> jieTing) {
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
					R.layout.jieting, null);
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
			holder.jrfs = (TextView) convertView.findViewById(R.id.jrfs);
			holder.jr_time = (TextView) convertView.findViewById(R.id.jr_time);
			holder.jtr = (TextView) convertView.findViewById(R.id.jtr);
			holder.jrnr = (TextView) convertView.findViewById(R.id.jrnr);

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
		holder.jrfs.setText(mJieTingList.get(position).getJrfs());
		holder.jr_time.setText(mJieTingList.get(position).getJr_time());
		holder.jtr.setText(mJieTingList.get(position).getJtr());
		holder.jrnr.setText(mJieTingList.get(position).getJrnr());
		return convertView;
	}

	class ViewHolder {

		TextView kh_name, kh_sex, cardId, sheng, shi, qu, jiedao, sq, zdy,
				address, jg, kh_phone, zc_time, goutong, jrfs, jr_time, jtr,
				jrnr;

	}
}