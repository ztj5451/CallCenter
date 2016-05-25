package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONObject;

import com.callCenter.entity.CustomerInfoDetail;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.PhoneUtils;
import com.callCenter.utils.SettingUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 从周边企业查看企业详细信息
 * 
 * @author Administrator
 * 
 */
public class ForMapQyDetailsActivity extends Activity {
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private CustomerInfoDetail customerDetail;
	private String id;
	private LinearLayout weizhi;
	private ProgressBar progressBar;
	private TextView kh_nameText, kh_agetText, kh_sexText, kh_cardidText,
			kh_dwText, kh_addressText, kh_telText, kh_jinji_telText, kh_qqText,
			kh_jjText, kh_bzText, shengText, shiText, quText, jiedaoText,
			sqText, zdyText, jgouText, kh_goutongText, kh_sxText, hydText,
			emailText, zctimeText, jjlxrText, moneysText;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				progressBar.setVisibility(View.GONE);
				setValue();
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				weizhi.setEnabled(false);
				progressBar.setVisibility(View.GONE);
				Toast.makeText(ForMapQyDetailsActivity.this, "服务器没有响应!",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerException.Fail:
				weizhi.setEnabled(false);
				progressBar.setVisibility(View.GONE);
				Toast.makeText(ForMapQyDetailsActivity.this, "加载服务器错误!",
						Toast.LENGTH_SHORT).show();
				break;

			default:

				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formap_qy_details_info);
		// 初始化
		init();
		// 添加事件
		Event();
		// 获取企业详细信息
		new Thread() {
			public void run() {
				progressBar.setVisibility(View.VISIBLE);
				getQyDetail();
			};
		}.start();
	}

	// 初始化
	private void init() {
		id = getIntent().getExtras().getString("id");
		progressBar = (ProgressBar) this.findViewById(R.id.progress);
		kh_nameText = (TextView) this.findViewById(R.id.kh_name);
		kh_agetText = (TextView) this.findViewById(R.id.kh_age);
		kh_sexText = (TextView) this.findViewById(R.id.kh_sex);
		kh_cardidText = (TextView) this.findViewById(R.id.kh_cardid);
		kh_dwText = (TextView) this.findViewById(R.id.kh_dw);
		kh_addressText = (TextView) this.findViewById(R.id.kh_address);
		kh_telText = (TextView) this.findViewById(R.id.kh_tel);
		kh_jinji_telText = (TextView) this.findViewById(R.id.kh_jinji_tel);
		kh_qqText = (TextView) this.findViewById(R.id.kh_qq);
		kh_jjText = (TextView) this.findViewById(R.id.kh_jj);
		kh_bzText = (TextView) this.findViewById(R.id.kh_bz);
		shengText = (TextView) this.findViewById(R.id.sheng);
		shiText = (TextView) this.findViewById(R.id.shi);
		quText = (TextView) this.findViewById(R.id.qu);
		jiedaoText = (TextView) this.findViewById(R.id.jiedao);
		sqText = (TextView) this.findViewById(R.id.sq);
		zdyText = (TextView) this.findViewById(R.id.zdy);
		jgouText = (TextView) this.findViewById(R.id.jgou);
		kh_goutongText = (TextView) this.findViewById(R.id.kh_goutong);
		kh_sxText = (TextView) this.findViewById(R.id.kh_sx);
		hydText = (TextView) this.findViewById(R.id.hyd);
		emailText = (TextView) this.findViewById(R.id.email);
		zctimeText = (TextView) this.findViewById(R.id.zctime);
		jjlxrText = (TextView) this.findViewById(R.id.jjlxr);
		weizhi = (LinearLayout) this.findViewById(R.id.weizhi);
		moneysText = (TextView) this.findViewById(R.id.moneys);
		registerForContextMenu(kh_telText);
	}

	private void setValue() {
		kh_nameText.setText(customerDetail.getKh_name());
		kh_agetText.setText(customerDetail.getKh_age());
		kh_sexText.setText(customerDetail.getKh_sex());
		kh_cardidText.setText(customerDetail.getKh_cardid());
		kh_dwText.setText(customerDetail.getKh_dw());
		kh_addressText.setText(customerDetail.getKh_address());
		kh_telText.setText(customerDetail.getKh_tel());
		kh_jinji_telText.setText(customerDetail.getKh_jinji_tel());
		kh_qqText.setText(customerDetail.getKh_qq());
		kh_jjText.setText(customerDetail.getKh_jj());
		kh_bzText.setText(customerDetail.getKh_bz());
		shengText.setText(customerDetail.getSheng());
		shiText.setText(customerDetail.getShi());
		quText.setText(customerDetail.getQu());
		jiedaoText.setText(customerDetail.getJiedao());
		sqText.setText(customerDetail.getSq());
		zdyText.setText(customerDetail.getZdy());
		jgouText.setText(customerDetail.getJgou());
		kh_goutongText.setText(customerDetail.getKh_goutong());
		kh_sxText.setText(customerDetail.getKh_sx());
		hydText.setText(customerDetail.getHyd());
		emailText.setText(customerDetail.getEmail());
		zctimeText.setText(customerDetail.getZctime());
		jjlxrText.setText(customerDetail.getJjlxr());
		if (customerDetail.getMoneys().equals("")
				|| customerDetail.getMoneys().equals("null")
				|| customerDetail.getMoneys() == null) {
			moneysText.setText("0");

		} else {
			moneysText.setText(customerDetail.getMoneys());
		}
	}

	// 监听事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						ForMapQyDetailsActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		this.findViewById(R.id.weizhi).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(
								ForMapQyDetailsActivity.this,
								SurroundQyMapActivity.class);
						if (customerDetail.getKhjd().equals("")
								|| customerDetail.getKhwd().equals("")
								|| customerDetail.getKhjd() == null
								|| customerDetail.getKhwd() == null) {
							Toast.makeText(ForMapQyDetailsActivity.this,
									"当前客户没有地理位置信息 ！", Toast.LENGTH_SHORT)
									.show();
						} else {
							intent.putExtra("lat", customerDetail.getKhwd());
							intent.putExtra("lon", customerDetail.getKhjd());
							intent.putExtra("name", customerDetail.getKh_name());
							System.out.println("经度:" + customerDetail.getKhjd()
									+ "纬度:" + customerDetail.getKhwd());
							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						}

					}
				});

	}

	// 获取周边企业个数url
	private StringBuffer qyDetailUrl() {
		StringBuffer sourroundQyUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		sourroundQyUrl.append(SettingUtils.get("sourround_detail_url"))
				.append("id=").append(id);
		System.out.println("客户详细信息url:" + sourroundQyUrl);

		return sourroundQyUrl;

	}

	// 获取周边企业
	private void getQyDetail() {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					qyDetailUrl(), "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				message = message.replaceAll("null", "");
				JSONObject object = new JSONObject(message);
				customerDetail = new CustomerInfoDetail();
				customerDetail.setId(object.getString("id"));
				customerDetail.setKh_name(object.getString("kh_name"));
				customerDetail.setKh_age(object.getString("kh_age"));
				customerDetail.setKh_sex(object.getString("kh_sex"));
				customerDetail.setKh_cardid(object.getString("kh_cardid"));
				customerDetail.setKh_dw(object.getString("kh_dw"));
				customerDetail.setKh_zy(object.getString("kh_zy"));
				customerDetail.setKh_address(object.getString("kh_address"));
				customerDetail.setKh_tel(object.getString("kh_tel"));
				customerDetail
						.setKh_jinji_tel(object.getString("kh_jinji_tel"));
				customerDetail.setKh_qq(object.getString("kh_qq"));
				customerDetail.setKh_jj(object.getString("kh_jj"));
				customerDetail.setKh_bz(object.getString("kh_bz"));
				customerDetail.setSheng(object.getString("sheng"));
				customerDetail.setShi(object.getString("shi"));
				customerDetail.setQu(object.getString("qu"));
				customerDetail.setJiedao(object.getString("jiedao"));
				customerDetail.setSq(object.getString("sq"));
				customerDetail.setZdy(object.getString("zdy"));
				customerDetail.setJgou(object.getString("jgou"));
				customerDetail.setKh_goutong(object.getString("kh_goutong"));
				customerDetail.setKh_sx(object.getString("kh_sx"));
				customerDetail.setHyd(object.getString("hyd"));
				customerDetail.setEmail(object.getString("email"));
				customerDetail.setZctime(object.getString("zctime"));
				customerDetail.setJjlxr(object.getString("jjlxr"));
				customerDetail.setKhjd(object.getString("khjd"));
				customerDetail.setKhwd(object.getString("khwd"));
				customerDetail.setMoneys(object.getString("moneys"));
				handler.sendEmptyMessage(HandlerException.Success);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.Fail);
		}
	}

	// 上下文菜单，本例会通过长按条目激活上下文菜单
		@Override
		public void onCreateContextMenu(ContextMenu menu, View view,
				ContextMenuInfo menuInfo) {
			menu.setHeaderTitle("相关操作");
			// 添加菜单项
			menu.add(0, ITEM1, 0, "拨打电话");
			menu.add(0, ITEM2, 0, "复制号码");
		}

		// 菜单单击响应
		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		@Override
		public boolean onContextItemSelected(MenuItem item) {

			switch (item.getItemId()) {
			case ITEM1:
				// 在这里添加处理代码
				PhoneUtils.Tel(ForMapQyDetailsActivity.this, kh_telText.getText().toString()
						.trim());
				break;
			case ITEM2:
				// 在这里添加处理代码
				ClipboardManager cmb = (ClipboardManager) this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setText(kh_telText.getText().toString().trim());
				break;
			}
			return true;
		}
	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ForMapQyDetailsActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
