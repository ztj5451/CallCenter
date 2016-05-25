package com.callCenter.adapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.callCenter.activity.R;
import com.callCenter.entity.Picture;
import com.callCenter.utils.SettingUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class SelectMediaAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Picture> mediaList;
	private String fromActivity;
	public static HashMap<Integer, Boolean> selectMap;// 保存每条记录是否被选中的状态
	public static HashSet<String> selectId;// 保存被选中人员对应的id
	public static HashSet<String> selectName;// 保存被选中人员的名字

	@SuppressLint("UseSparseArrays")
	public SelectMediaAdapter(Context context, List<Picture> mediaList,String from) {
		this.inflater = LayoutInflater.from(context);
		this.mediaList = mediaList;
		this.fromActivity=from;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.gwfj_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.image_sigle);
			holder.imageName = (TextView) convertView
					.findViewById(R.id.image_name);
			holder.select = (CheckBox) convertView.findViewById(R.id.select);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();

		}

		String imageName = mediaList.get(position).getImageName();
		holder.imageName.setText(imageName);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
		Bitmap bitmap = BitmapFactory.decodeFile(SettingUtils.get(fromActivity)
				+ "/" + imageName, options);
		options.inJustDecodeBounds = false;
		int be = options.outHeight / 100;
		if (be <= 0) {
			be = 10;
		}
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(SettingUtils.get(fromActivity) + "/"
				+ imageName, options);

		// Bitmap sourceBitmap = BitmapFactory
		// .decodeFile(SettingUtils.get("image_dir") + "/"
		// + imageName);
		// Bitmap bitmap = Thumbnail.extractMiniThumb(sourceBitmap, 100, 120);

		holder.imageView.setImageBitmap(bitmap);
		holder.select.setChecked(selectMap.get(position));// 设置checkBox的当前状态
		// 对当前状态进行判断 如果是选中状态 则加入set集合 如果是取消选中 则从set集合中移除
		if (selectMap.get(position)) {

			selectName.add(String.valueOf(mediaList.get(position)
					.getImageName()));
		} else {

			selectName.remove(String.valueOf(mediaList.get(position)
					.getImageName()));
		}

		return convertView;
	}

	public class ViewHolder {
		ImageView imageView;
		TextView imageName;
		public CheckBox select;
	}

}
