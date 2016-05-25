package com.callCenter.adapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import com.callCenter.activity.GdFinishActivity;
import com.callCenter.activity.GwcbActivity;
import com.callCenter.activity.R;
import com.callCenter.entity.Area;
import com.callCenter.entity.Cuiban;
import com.callCenter.entity.Gwpf;
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

public class GwcbAdapter extends BaseAdapter {
	private int cbPosition=0;
	private String cbTimes="0";
	private SettingUtils settingUtils;
	private Context mContext;

	private List<Cuiban> mcbList;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				
				Toast.makeText(mContext, "催办成功!", Toast.LENGTH_SHORT).show();
				mcbList.get(cbPosition).setCuiban((Integer.parseInt(cbTimes)+1)+"");
				GwcbActivity.handler.sendEmptyMessage(100);
				break;
			case HandlerException.Fail:
				Toast.makeText(mContext, "催办失败!", Toast.LENGTH_SHORT).show();
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
	

	public GwcbAdapter(Context context, List<Cuiban> cbList) {
		mContext = context;
		mcbList = cbList;
		settingUtils = new SettingUtils(mContext);
	}

	public int getCount() {
		return mcbList.size();
	}

	public Object getItem(int position) {
		return mcbList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.gwcb_item, null);
			holder = new ViewHolder();
			holder.kh_name = (TextView) convertView.findViewById(R.id.kh_name);
			holder.kh_tel = (TextView) convertView.findViewById(R.id.kh_tel);
			holder.jibie = (TextView) convertView.findViewById(R.id.jibie);
			holder.starttime = (TextView) convertView
					.findViewById(R.id.starttime);
			holder.bmname = (TextView) convertView.findViewById(R.id.bmname);
			holder.jdbmname = (TextView) convertView
					.findViewById(R.id.jdbmname);
			holder.jdtimes = (TextView) convertView.findViewById(R.id.jdtimes);
			holder.zt = (TextView) convertView.findViewById(R.id.zt);
			holder.cuiban = (TextView) convertView.findViewById(R.id.cuiban);
			holder.cb = (ImageView) convertView.findViewById(R.id.cb);
			holder.sqId = (TextView) convertView.findViewById(R.id.sqId);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.kh_name.setText(mcbList.get(position).getKh_name());
		holder.kh_tel.setText(mcbList.get(position).getKh_tel());
		holder.starttime.setText(mcbList.get(position).getStarttime());
		holder.jibie.setText(mcbList.get(position).getJibie());
		if (mcbList.get(position).getBmname() == null) {
			holder.bmname.setText("无");
		} else {
			holder.bmname.setText(mcbList.get(position).getBmname());
		}

		holder.jdbmname.setText(mcbList.get(position).getJdbmname());
		holder.jdtimes.setTag(mcbList.get(position).getJdtimes());
		holder.cuiban.setText(mcbList.get(position).getCuiban());
		holder.sqId.setText((position + 1) + "");
		holder.cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Thread() {
					public void run() {
						cbPosition=position;
						cbTimes=mcbList.get(position).getCuiban();
						gwcbAction(gwcbUrl(mcbList.get(position).getId()));
					};
				}.start();
			}
		});

		if (mcbList.get(position).getBs().equals("1")) {
			holder.starttime.setTextColor(Color.RED);
		} else {
			holder.starttime.setTextColor(Color.BLACK);
		}

		if (mcbList.get(position).getZt().equals("0")) {
			holder.zt.setText("未完成");
			holder.zt.setTextColor(Color.RED);
		} else if (mcbList.get(position).getZt().equals("1")) {
			holder.zt.setText("已受理");
			holder.zt.setTextColor(Color.rgb(0, 119, 192));
		} else if (mcbList.get(position).getZt().equals("2")) {
			holder.zt.setText("已完成");
			holder.zt.setTextColor(Color.rgb(0, 119, 192));
		} else if (mcbList.get(position).getZt().equals("3")) {
			holder.zt.setText("服务完成");
			holder.zt.setTextColor(Color.rgb(0, 119, 192));
		}

		return convertView;
	}

	private StringBuffer gwcbUrl(String id) {
		StringBuffer gwcbUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwcbUrl.append(SettingUtils.get("gwcb_cb_url")).append("id=")
				.append(id).append("&loginname=")
				.append(GwcbActivity.loginname);
		System.out.println("公文催办url:" + gwcbUrl);
		return gwcbUrl;
	}

	private void gwcbAction(StringBuffer gwcbUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwcbUrl, "GET");
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

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	class ViewHolder {

		TextView kh_name, kh_tel, jibie, times, starttime, bmname, jdbmname,
				jdtimes, zt, bs, cuiban, sqId;
		ImageView cb;
	}
}