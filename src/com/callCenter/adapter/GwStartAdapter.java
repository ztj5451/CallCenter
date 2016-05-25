package com.callCenter.adapter;

import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.GwStart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 公文发起Adapter
 * 
 * @author Administrator
 * 
 */
public class GwStartAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	List<GwStart> gwList;

	// 构造函数
	public GwStartAdapter(Context context, List<GwStart> gwList) {
		inflater = LayoutInflater.from(context);
		this.gwList = gwList;
		this.context = context;

	}

	@Override
	public int getCount() {

		return gwList.size();
	}

	@Override
	public Object getItem(int position) {

		return gwList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parentGroup) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.gwstart_item, null);
			viewHolder = new ViewHolder();

			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.content);
			viewHolder.receiver = (TextView) convertView
					.findViewById(R.id.receiver);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.attachment = (TextView) convertView
					.findViewById(R.id.attachment);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		viewHolder.title.setText(gwList.get(position).getTitle());
		viewHolder.content.setText(gwList.get(position).getContent());
		viewHolder.receiver.setText(gwList.get(position).getReceiver());
		viewHolder.time.setText(gwList.get(position).getTime());
		// ����
		if (!gwList.get(position).getAttachment().isEmpty()) {
			if (gwList.get(position).getAttachment().contains(",")) {
				String attach[] = gwList.get(position).getAttachment()
						.split(",");
				StringBuffer temp = new StringBuffer();
				for (int i = 0; i < attach.length; i++) {

					temp.append(attach[i]).append("\n");
				}
				viewHolder.attachment.setText(temp);
			} else {
				viewHolder.attachment.setText(gwList.get(position)
						.getAttachment());
			}

		} else {
			viewHolder.attachment.setText("��");
		}

		return convertView;
	}

	/**
	 * ��ſؼ�
	 */
	public final class ViewHolder {
		public TextView title, content, receiver, time, attachment;

	}

}
