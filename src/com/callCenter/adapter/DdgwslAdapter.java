package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.GdFinishActivity;
import com.callCenter.activity.R;
import com.callCenter.entity.Area;
import com.callCenter.entity.Ddgwsl;
import com.callCenter.entity.Gwpf;
import com.callCenter.entity.ServerPersonJD;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DdgwslAdapter extends BaseAdapter {
	private Context mContext;

	private List<Ddgwsl> mDdgwslList;

	public DdgwslAdapter(Context context, List<Ddgwsl> dwgwslList) {
		mContext = context;
		mDdgwslList = dwgwslList;
	}

	public int getCount() {
		return mDdgwslList.size();
	}

	public Object getItem(int position) {
		return mDdgwslList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.ddgwsl_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.khName);
			holder.startTime = (TextView) convertView
					.findViewById(R.id.startTime);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.zt = (TextView) convertView.findViewById(R.id.zt);
			holder.jibie = (TextView) convertView.findViewById(R.id.jibie);
			holder.lsh = (TextView) convertView.findViewById(R.id.lsh);
			holder.tel = (TextView) convertView.findViewById(R.id.tel);
			holder.upload = (ImageView) convertView.findViewById(R.id.upload);
			holder.tuidan = (TextView) convertView.findViewById(R.id.tuidan);
			holder.sqId = (TextView) convertView.findViewById(R.id.sqId);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(mDdgwslList.get(position).getKhName());
		holder.startTime.setText(mDdgwslList.get(position).getStartTime());
		holder.time.setText(mDdgwslList.get(position).getTime());
		holder.jibie.setText(mDdgwslList.get(position).getJibie());
		holder.lsh.setText(mDdgwslList.get(position).getLsh());
		holder.tel.setText(mDdgwslList.get(position).getTel());
		holder.sqId.setText((position + 1) + "");
		holder.upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String gwId = mDdgwslList.get(position).getId();
				Intent intent = new Intent(mContext, GdFinishActivity.class);
				intent.putExtra("gwId", gwId);
				intent.putExtra("tuidan", mDdgwslList.get(position).getTuidan());
				intent.putExtra("hz", mDdgwslList.get(position).getHz());
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);
			}
		});

		if (mDdgwslList.get(position).getZt().equals("0")) {
			holder.zt.setText("未完成");
			holder.zt.setTextColor(Color.RED);
		} else if (mDdgwslList.get(position).getZt().equals("1")) {
			holder.zt.setText("已完成");
			holder.zt.setTextColor(Color.rgb(0, 119, 192));
			holder.upload.setVisibility(View.GONE);
		}
		// if (mDdgwslList.get(position).getZt().equals("1")) {
		//
		// }
		if (mDdgwslList.get(position).getTuidan().equals("0")) {
			holder.tuidan.setText("无");
		} else {
			holder.tuidan.setText("退单");
			holder.tuidan.setTextColor(Color.RED);
		}
		return convertView;
	}

	class ViewHolder {

		TextView name, startTime, time, zt, jibie, lsh, tel, tuidan, sqId;
		ImageView upload;
	}
}