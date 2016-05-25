package com.callCenter.adapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import org.json.JSONObject;

import com.callCenter.activity.GdFinishActivity;
import com.callCenter.activity.GwspActivity;
import com.callCenter.activity.R;
import com.callCenter.activity.ServerFinishActivity;
import com.callCenter.entity.Area;
import com.callCenter.entity.Gwpf;
import com.callCenter.entity.Gwsp;
import com.callCenter.entity.ServerPersonJD;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.SettingUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GwspAdapter extends BaseAdapter {
	private Context mContext;
	private SettingUtils settingUtils;

	private List<Gwsp> mgwspList;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				Toast.makeText(mContext, "公文审批成功!", Toast.LENGTH_LONG).show();
				GwspActivity.handler.sendEmptyMessage(HandlerException.Over);
				break;
			case HandlerException.Fail:
				Toast.makeText(mContext, "公文审批失败!", Toast.LENGTH_LONG).show();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			default:

				break;
			}
		};
	};

	public GwspAdapter(Context context, List<Gwsp> gwspList) {
		mContext = context;
		mgwspList = gwspList;
		settingUtils = new SettingUtils(context);
	}

	public int getCount() {
		return mgwspList.size();
	}

	public Object getItem(int position) {
		return mgwspList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.gwsp_item, null);
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
			holder.wcTime = (TextView) convertView.findViewById(R.id.wcTime);
			holder.username = (TextView) convertView
					.findViewById(R.id.username);
			holder.finish = (ImageView) convertView.findViewById(R.id.sp);
			holder.fwpname = (TextView) convertView.findViewById(R.id.fwpname);
			holder.sqId = (TextView) convertView.findViewById(R.id.sqId);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(mgwspList.get(position).getKhName());
		holder.startTime.setText(mgwspList.get(position).getStartTime());
		holder.time.setText(mgwspList.get(position).getTimes());
		holder.jibie.setText(mgwspList.get(position).getJibie());
		holder.lsh.setText(mgwspList.get(position).getLsh());
		holder.jdbmname.setText(mgwspList.get(position).getJdbmname());
		holder.wcTime.setText(mgwspList.get(position).getWcTimes());
		holder.username.setText(mgwspList.get(position).getUsername());
		holder.fwpname.setText(mgwspList.get(position).getFwpname());
		holder.sqId.setText((position + 1) + "");
		holder.finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// new Thread() {
				// public void run() {
				// gwSpAction(gwSpUrl(mgwspList.get(position).getId(),
				// mgwspList.get(position).getGwid(),
				// GwspActivity.loginname));
				// };
				// }.start();
				Intent intent = new Intent(mContext, ServerFinishActivity.class);
				intent.putExtra("id", mgwspList.get(position).getId());
				intent.putExtra("gwId", mgwspList.get(position).getGwid());
				// intent.putExtra("bmId", mgwspList.get(position).getBmId());
				intent.putExtra("loginname", GwspActivity.loginname);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);
			}
		});

		if (mgwspList.get(position).getZt().equals("0")) {
			holder.zt.setText("未受理");
			holder.zt.setTextColor(Color.RED);
		} else if (mgwspList.get(position).getZt().equals("1")) {
			holder.zt.setText("已受理");
			holder.zt.setTextColor(Color.rgb(0, 119, 192));
		} else if (mgwspList.get(position).getZt().equals("2")) {
			holder.zt.setText("服务完成");
			holder.zt.setTextColor(Color.rgb(0, 119, 192));
		} else if (mgwspList.get(position).getZt().equals("3")) {
			holder.zt.setText("已完成1");
			holder.zt.setTextColor(Color.rgb(0, 119, 192));
		}

		return convertView;
	}

	// 公文发起URL
	private StringBuffer gwSpUrl(String id, String gwid, String loginname) {
		StringBuffer gwSpUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwSpUrl.append(SettingUtils.get("gwsp_sp_url")).append("id=")
				.append(id).append("&gwid=").append(gwid).append("&loginname=")
				.append(loginname);
		System.out.println("工单审批:" + gwSpUrl);
		return gwSpUrl;
	}

	// 工单派发
	private void gwSpAction(StringBuffer gwSpUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwSpUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				if (message.equals("success")) {
					handler.sendEmptyMessage(HandlerException.Success);
				} else if (message.equals("error")) {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

				// JSONObject object = new JSONObject(message);

				// if (message.equals("")) {
				// handler.sendEmptyMessage(HandlerException.Success);
				// } else {
				// handler.sendEmptyMessage(HandlerException.Fail);
				// }

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	class ViewHolder {

		TextView name, startTime, time, zt, jibie, lsh, jdbmname, wcTime,
				username, fwpname, sqId;
		ImageView finish;
	}
}
