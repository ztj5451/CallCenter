package com.callCenter.adapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.callCenter.activity.R;
import com.callCenter.entity.SelectPerson;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SelectPersonAdapter extends BaseAdapter {

	LayoutInflater inflater;
	Context context;
	List<SelectPerson> mSelectPersonList;
	public static HashMap<Integer, Boolean> selectMap;// 保存每条记录是否被选中的状态
	public static HashSet<String> selectId;// 保存被选中人员对应的id
	public static HashSet<String> selectName;// 保存被选中人员的名字
	public static String selectPersonId;
	public static HashMap<String, String> selected;
	private boolean flag = false;
	private int checkPosition;

	// 构造函数
	@SuppressLint("UseSparseArrays")
	public SelectPersonAdapter(Context context,
			List<SelectPerson> SelectPersonList) {
		inflater = LayoutInflater.from(context);
		this.mSelectPersonList = SelectPersonList;
		this.context = context;
		selectMap = new HashMap<Integer, Boolean>();
		selectId = new HashSet<String>();
		selectName = new HashSet<String>();
		selected = new HashMap<String, String>();
		for (int i = 0; i < mSelectPersonList.size(); i++) {
			selectMap.put(i, false);// 默认情况下 没有被选中

		}

	}

	@Override
	public int getCount() {

		return mSelectPersonList.size();
	}

	@Override
	public Object getItem(int position) {

		return mSelectPersonList.get(position);
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
			convertView = inflater.inflate(R.layout.select_person_item, null);
			viewHolder = new ViewHolder();

			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.userName);
			viewHolder.zhiwei = (TextView) convertView
					.findViewById(R.id.zhiwei);
			viewHolder.select = (CheckBox) convertView
					.findViewById(R.id.select);
			viewHolder.fzhzLinear = (LinearLayout) convertView
					.findViewById(R.id.fzhzLinear);
			viewHolder.fzhz = (CheckBox) convertView.findViewById(R.id.fzhz);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		viewHolder.userName.setText(mSelectPersonList.get(position)
				.getUserName());
		viewHolder.zhiwei.setText(mSelectPersonList.get(position).getZhiwei());
		viewHolder.select.setChecked(selectMap.get(position));
		// viewHolder.select.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		//
		//
		// }
		// });
		// 对当前状态进行判断 如果是选中状态 则加入set集合 如果是取消选中 则从set集合中移除
		if (selectMap.get(position)) {
			// 添加ID
			selectId.add(String.valueOf(mSelectPersonList.get(position)
					.getUserId()));
			// 添加名字
			selectName.add(String.valueOf(mSelectPersonList.get(position)
					.getUserName()));
			selected.put(String.valueOf(mSelectPersonList.get(position)
					.getUserId()), String.valueOf(mSelectPersonList.get(
					position).getUserName()));
			selectMap.put(position, true);

			// viewHolder.fzhzLinear.setVisibility(View.VISIBLE);

		} else {
			// 移除ID
			selectId.remove(String.valueOf(mSelectPersonList.get(position)
					.getUserId()));
			// 移除名字
			selectName.remove(String.valueOf(mSelectPersonList.get(position)
					.getUserName()));
			selected.remove(String.valueOf(mSelectPersonList.get(position)
					.getUserId()));
			selectMap.put(position, false);

			// viewHolder.fzhzLinear.setVisibility(View.GONE);
		}
		// viewHolder.fzhz.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// checkPosition = position;
		// if (checkPosition == position) {
		// if (!flag) {
		// selectPersonId = String.valueOf(mSelectPersonList.get(
		// position).getUserId());
		// Toast.makeText(context, selectPersonId,
		// Toast.LENGTH_LONG).show();
		// flag = true;
		// } else {
		// selectPersonId = "";
		// Toast.makeText(context, selectPersonId,
		// Toast.LENGTH_LONG).show();
		// flag = false;
		// }
		// } else {
		// viewHolder.fzhz.setChecked(false);
		// }
		//
		// }
		// });
		// viewHolder.cs1.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// viewHolder.cs2.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		return convertView;
	}

	/**
	 * adapter中的属性
	 * 
	 * @author Administrator
	 * 
	 */
	public final class ViewHolder {
		public TextView userName, zhiwei;
		public CheckBox select;
		public LinearLayout fzhzLinear;
		public CheckBox fzhz, cs2;

	}

}
