package com.callCenter.adapter;

import java.util.List;
import com.callCenter.activity.R;
import com.callCenter.entity.GwGz;
import com.callCenter.utils.SettingUtils;
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
public class GwGzAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	List<GwGz> mGwGzList;

	// 构造方法
	public GwGzAdapter(Context context, List<GwGz> gwGzList) {
		inflater = LayoutInflater.from(context);
		this.mGwGzList = gwGzList;
		this.context = context;

	}

	@Override
	public int getCount() {

		return mGwGzList.size();
	}

	@Override
	public Object getItem(int position) {

		return mGwGzList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parentGroup) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.gwgz_item, null);
			viewHolder = new ViewHolder();

			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.pdName = (TextView) convertView
					.findViewById(R.id.pdName);

			viewHolder.dateTime = (TextView) convertView
					.findViewById(R.id.dateTime);
			viewHolder.lsh = (TextView) convertView.findViewById(R.id.lsh);
			viewHolder.sqId = (TextView) convertView.findViewById(R.id.sqId);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		viewHolder.name.setText(mGwGzList.get(position).getKhName());
		viewHolder.dateTime.setText(mGwGzList.get(position).getTime());
		viewHolder.lsh.setText(mGwGzList.get(position).getLsh());
		viewHolder.pdName.setText(mGwGzList.get(position).getPdName());
		viewHolder.sqId.setText((position + 1) + "");
		return convertView;
	}

	/**
	 * 存放控件
	 */
	public final class ViewHolder {
		public TextView name, pdName, lsh, dateTime, sqId;

	}

}
