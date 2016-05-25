package com.callCenter.adapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.callCenter.activity.R;
import com.callCenter.entity.History;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {
	private Context mContext;

	private List<History> mHistoryList;
	public static HashMap<Integer, Boolean> selectMap;// 保存每条记录是否被选中的状态
	public static HashSet<String> selectId;// 保存被选中人员对应的id

	public HistoryAdapter(Context context, List<History> historyList) {
		mContext = context;
		mHistoryList = historyList;
		selectMap = new HashMap<Integer, Boolean>();
		selectId = new HashSet<String>();
		for (int i = 0; i < mHistoryList.size(); i++) {
			selectMap.put(i, false);// 默认情况下 没有被选中
		}
	}

	public int getCount() {
		return mHistoryList.size();
	}

	public Object getItem(int position) {
		return mHistoryList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.history_item, null);
			holder = new ViewHolder();

			holder.kh_name = (TextView) convertView.findViewById(R.id.kh_name);
			holder.kh_tel = (TextView) convertView.findViewById(R.id.kh_tel);
			holder.contents = (TextView) convertView
					.findViewById(R.id.contents);
			holder.times = (TextView) convertView.findViewById(R.id.times);
			holder.select = (CheckBox) convertView
					.findViewById(R.id.select_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// holder.id.setText(mHistoryList.get(position).getId());
		holder.kh_name.setText(mHistoryList.get(position).getKh_name());
		holder.kh_tel.setText(mHistoryList.get(position).getKh_tel());
		holder.contents.setText(mHistoryList.get(position).getContents());
		holder.times.setText(mHistoryList.get(position).getTimes());
		holder.select.setChecked(selectMap.get(position));
		if (selectMap.get(position)) {
			// 添加ID
			selectId.add(String.valueOf(mHistoryList.get(position).getId()));

		} else {
			// 移除ID
			selectId.remove(String.valueOf(mHistoryList.get(position).getId()));

		}
		return convertView;
	}

	public class ViewHolder {

		TextView id, kh_name, kh_tel, contents, times;
		public CheckBox select;

	}
}