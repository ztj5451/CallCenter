package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.TzMessage;
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
public class ReceiveMessageAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	List<TzMessage> messageList;

	// 构造方法
	public ReceiveMessageAdapter(Context context, List<TzMessage> messageList) {
		inflater = LayoutInflater.from(context);
		this.messageList = messageList;
		this.context = context;

	}

	@Override
	public int getCount() {

		return messageList.size();
	}

	@Override
	public Object getItem(int position) {

		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentGroup) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.receivemessage_item, null);
			viewHolder = new ViewHolder();

			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.send_bm = (TextView) convertView
					.findViewById(R.id.send_bm);
			viewHolder.new_message=(TextView) convertView.findViewById(R.id.new_message);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		viewHolder.title.setText(messageList.get(position).getTitle());
		viewHolder.time.setText(messageList.get(position).getTime());
		viewHolder.send_bm.setText(messageList.get(position).getBm());
		if (messageList.get(position).getFlag().equals("yes")) {
			viewHolder.new_message.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * 存放控件
	 */
	public final class ViewHolder {
		public TextView title, time, send_bm,new_message;

	}

}
