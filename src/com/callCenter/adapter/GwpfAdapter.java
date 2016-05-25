package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.GdFinishActivity;
import com.callCenter.activity.R;
import com.callCenter.entity.Area;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class GwpfAdapter extends BaseAdapter {
	private Context mContext;

	private List<Gwpf> mjdList;

	public GwpfAdapter(Context context, List<Gwpf> jdList) {
		mContext = context;
		mjdList = jdList;
	}

	public int getCount() {
		return mjdList.size();
	}

	public Object getItem(int position) {
		return mjdList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.gwpf2_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.khName);
			holder.startTime = (TextView) convertView
					.findViewById(R.id.startTime);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.zt = (TextView) convertView.findViewById(R.id.zt);
			holder.jibie = (TextView) convertView.findViewById(R.id.jibie);
			holder.lsh = (TextView) convertView.findViewById(R.id.lsh);
			holder.jdbmname = (TextView) convertView
					.findViewById(R.id.jdbmname);
			holder.cuiban = (TextView) convertView.findViewById(R.id.cuiban);
			holder.tuidanLinear = (LinearLayout) convertView
					.findViewById(R.id.tuidanLinear);
			holder.fwpname = (TextView) convertView.findViewById(R.id.fwpname);
			holder.sqId = (TextView) convertView.findViewById(R.id.sqId);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(mjdList.get(position).getKhName());
		holder.startTime.setText(mjdList.get(position).getStartTime());
		holder.time.setText(mjdList.get(position).getTime());
		holder.jibie.setText(mjdList.get(position).getJibie());
		holder.lsh.setText(mjdList.get(position).getLsh());
		holder.jdbmname.setText(mjdList.get(position).getJdbmname());
		holder.cuiban.setText(mjdList.get(position).getCuiban());
		holder.fwpname.setText(mjdList.get(position).getFwpname());
		holder.sqId.setText((position + 1) + "");
		// holder.upload.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // if (mjdList.get(position).getZt().equals("0")) {
		// // String gwId = mjdList.get(position).getId();
		// // Intent intent = new Intent(mContext, GdFinishActivity.class);
		// // intent.putExtra("gwId", gwId);
		// // mContext.startActivity(intent);
		// // ((Activity) mContext).overridePendingTransition(
		// // R.anim.in_from_right, R.anim.out_to_left);
		// // } else {
		// // Toast.makeText(mContext, "该工单已完成！", Toast.LENGTH_SHORT)
		// // .show();
		// // }
		//
		// String gwId = mjdList.get(position).getId();
		// Intent intent = new Intent(mContext, GdFinishActivity.class);
		// intent.putExtra("gwId", gwId);
		// mContext.startActivity(intent);
		// ((Activity) mContext).overridePendingTransition(
		// R.anim.in_from_right, R.anim.out_to_left);
		// }
		// });
		if (mjdList.get(position).getBs().equals("1")) {
			holder.startTime.setTextColor(Color.RED);
		} else {
			holder.startTime.setTextColor(Color.BLACK);
		}

		if (mjdList.get(position).getZt().equals("0")) {
			holder.zt.setText("未受理");
			holder.zt.setTextColor(Color.RED);
		} else if (mjdList.get(position).getZt().equals("1")) {
			holder.zt.setText("已受理");
			holder.zt.setTextColor(Color.rgb(0, 119, 192));
		} else if (mjdList.get(position).getZt().equals("2")) {
			holder.zt.setText("服务完成");
			holder.zt.setTextColor(Color.rgb(0, 119, 192));
		}
		if (mjdList.get(position).getTuidan().equals("1")) {
			holder.tuidanLinear.setVisibility(View.VISIBLE);
		}
		// else if (mjdList.get(position).getZt().equals("2")) {
		// holder.zt.setText("已完成");
		// holder.zt.setTextColor(Color.BLUE);
		// } else if (mjdList.get(position).getZt().equals("3")) {
		// holder.zt.setText("已完成1");
		// holder.zt.setTextColor(Color.BLUE);
		// }

		return convertView;
	}

	class ViewHolder {

		TextView name, startTime, time, zt, jibie, lsh, jdbmname, cuiban, sqId,
				fwpname;
		LinearLayout tuidanLinear;

	}
}