package com.callCenter.adapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.Picture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 媒体文件选择 adapter
 * 
 * @author Administrator
 * 
 */
public class SelectedMediaAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Picture> mediaList;
	public static HashMap<Integer, Boolean> selectMap;// 保存每条记录是否被选中的状态
	public static HashSet<String> selectId;// 保存被选中人员对应的id
	public static HashSet<String> selectName;// 保存被选中人员的名字

	public SelectedMediaAdapter(Context context, List<Picture> mediaList) {
		this.inflater = LayoutInflater.from(context);
		this.mediaList = mediaList;

		selectMap = new HashMap<Integer, Boolean>();
		selectId = new HashSet<String>();
		selectName = new HashSet<String>();
		for (int i = 0; i < mediaList.size(); i++) {
			selectMap.put(i, false);
		}
	}

	@Override
	public int getCount() {
		return mediaList.size();
	}

	@Override
	public Object getItem(int position) {
		return mediaList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.select_media_item, null);
			holder = new ViewHolder();
			holder.imageSequence = (TextView) convertView
					.findViewById(R.id.imageSequence);
			holder.imageName = (TextView) convertView
					.findViewById(R.id.imageName);
			holder.imageView = (ImageView) convertView.findViewById(R.id.del);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();

		}

		String imageName = mediaList.get(position).getImageName();
		holder.imageName.setText(imageName);
		holder.imageView.setImageResource(R.drawable.del);
		holder.imageSequence.setText("");

		return convertView;
	}

	public class ViewHolder {
		ImageView imageView;
		TextView imageName, imageSequence;
	}

}
