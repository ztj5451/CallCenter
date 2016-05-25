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
 * 发送通知公告的Adapter
 * 
 * @author Administrator
 * 
 */
public class SendedMessageAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	List<TzMessage> messageList;

	// 构造方法
	public SendedMessageAdapter(Context context, List<TzMessage> messageList) {
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
			convertView = inflater.inflate(R.layout.sendmessage_item, null);
			viewHolder = new ViewHolder();

			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.message = (TextView) convertView
					.findViewById(R.id.message);
			viewHolder.user = (TextView) convertView.findViewById(R.id.user);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		viewHolder.title.setText(messageList.get(position).getTitle());
		viewHolder.message.setText(messageList.get(position).getMessage());
		viewHolder.user.setText(messageList.get(position).getUser());
		viewHolder.time.setText(messageList.get(position).getTime());
		return convertView;
	}

	/**
	 * 存放控件
	 */
	public final class ViewHolder {
		public TextView title, message, user, time;

	}

}
