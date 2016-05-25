package com.callCenter.adapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import com.callCenter.activity.R;
import com.callCenter.activity.WorkOrderActivity;
import com.callCenter.entity.CallPhone;
import com.callCenter.entity.Customer;
import com.callCenter.entity.GD;
import com.callCenter.entity.WorkOrder;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 接收消息Adapter
 * 
 * @author Administrator
 * 
 */
public class WorkOrderAdapter extends BaseAdapter {
	LayoutInflater inflater;
	Context context;
	List<GD> mworkOrderList;
	private SettingUtils settingUtils;

	// 构造方法
	public WorkOrderAdapter(Context context, List<GD> workOrderList) {
		inflater = LayoutInflater.from(context);
		this.mworkOrderList = workOrderList;
		this.context = context;
		settingUtils = new SettingUtils(context);

	}

	@Override
	public int getCount() {

		return mworkOrderList.size();
	}

	@Override
	public Object getItem(int position) {

		return mworkOrderList.get(position);
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
			convertView = inflater.inflate(R.layout.work_order_item, null);
			viewHolder = new ViewHolder();

			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.level = (TextView) convertView.findViewById(R.id.level);

			viewHolder.dateTime = (TextView) convertView
					.findViewById(R.id.dateTime);
			viewHolder.bmname = (TextView) convertView
					.findViewById(R.id.bmname);
			viewHolder.beizhu = (TextView) convertView
					.findViewById(R.id.beizhu);
			viewHolder.tel=(TextView) convertView.findViewById(R.id.tel);
			viewHolder.username=(TextView) convertView.findViewById(R.id.username);
			
			viewHolder.finish = (ImageView) convertView
					.findViewById(R.id.finish);
			viewHolder.sqId=(TextView) convertView.findViewById(R.id.sqId);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}
		if (mworkOrderList.get(position).getTdtype().equals("0")) {
			viewHolder.level.setText(mworkOrderList.get(position).getGdTitle());
			viewHolder.bmname.setText("无");
			viewHolder.beizhu.setText(mworkOrderList.get(position).getBsjj());
		} else if (mworkOrderList.get(position).getTdtype().equals("1")) {
			viewHolder.level.setText(mworkOrderList.get(position).getJibie());
			viewHolder.bmname.setText(mworkOrderList.get(position).getBmname());
			viewHolder.beizhu.setText(mworkOrderList.get(position)
					.getTdbeizhu());
		}
		viewHolder.name.setText(mworkOrderList.get(position).getName());
		viewHolder.dateTime.setText(mworkOrderList.get(position).getTimes());
		viewHolder.tel.setText(mworkOrderList.get(position).getTel());
		viewHolder.username.setText(mworkOrderList.get(position).getUserName());
		viewHolder.sqId.setText((position+1)+"");
		viewHolder.finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				System.out.println("公文列表ID："
						+ mworkOrderList.get(position).getId());
				final String gwId = mworkOrderList.get(position).getId();
				new Thread() {
					public void run() {
						finishAction(finishUrl(gwId));
					};
				}.start();

			}
		});
		return convertView;
	}

	// 公文发起URL
	private StringBuffer finishUrl(String id) {
		StringBuffer finishUrl = new StringBuffer(SettingUtils.get("serverIp"));
		finishUrl.append(SettingUtils.get("gw_over_url")).append("id=")
				.append(id);
		System.out.println("完成URL：" + finishUrl);
		return finishUrl;
	}

	private void finishAction(StringBuffer finishUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					finishUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				// handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				System.out.println("返回信息:" + message);
				if (message.equals("success")) {
					// handler.sendEmptyMessage(HandlerException.Success);
					WorkOrderActivity.handler
							.sendEmptyMessage(HandlerException.Over);
				} else {
					// handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	/**
	 * 存放控件
	 */
	public final class ViewHolder {
		public TextView name, level, dateTime, beizhu, bmname,tel,username,sqId;
		public ImageView finish;

	}

}
