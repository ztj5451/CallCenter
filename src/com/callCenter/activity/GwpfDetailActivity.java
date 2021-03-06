package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callCenter.entity.Bm;
import com.callCenter.entity.Gwpf;
import com.callCenter.entity.GwpfDetail;
import com.callCenter.entity.Type;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.PhoneUtils;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GwpfDetailActivity extends Activity {
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private TextView bsjbTextView, khNameTextView, ageTextView, sexTextView,
			sxTextView, addressTextView, telTextView, shengTextView,
			shiTextView, quTextView, jdTextView, sqTextView, zdyTextView,
			gtTextView, khjjTextView, gdhTextView, startTimeTextView,
			fwjjTextView, fwbzTextView, moneyTextView;
	private Spinner jdBmSpinner;
	private EditText remarksEditText;
	private List<Type> typeList, typeList2;
	private ArrayAdapter<String> categoryAdapter, categoryAdapter2;
	private String pfId, loginname, uid;
	private String[] spinner, spinner2;
	private SettingUtils settingUtils;
	private GwpfDetail gwpfDetail;
	private String remarks;
	private String jdbmId, pdbmId;
	private String jsonArray;
	private WebView waitWebView;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				setValue();

				// if (typeList != null) {
				// spinner = new String[typeList.size()];
				// for (int i = 0; i < typeList.size(); i++) {
				// spinner[i] = typeList.get(i).getName();
				// }
				// }
				// // spinner = new String[] { "选择类别", "普通", "紧急", "非常紧急" };
				// categoryAdapter = new ArrayAdapter<String>(
				// GwpfDetailActivity.this,
				// android.R.layout.simple_spinner_item, spinner);
				// categoryAdapter
				// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// pfBmSpinner.setAdapter(categoryAdapter);
				// pfBmSpinner.setSelection(0, false);
				break;
			case HandlerException.SuccessToo:
				if (typeList2 != null) {
					spinner2 = new String[typeList2.size()];
					for (int i = 0; i < typeList2.size(); i++) {
						spinner2[i] = typeList2.get(i).getName();
					}
				}
				// spinner = new String[] { "选择类别", "普通", "紧急", "非常紧急" };
				categoryAdapter2 = new ArrayAdapter<String>(
						GwpfDetailActivity.this,
						android.R.layout.simple_spinner_item, spinner2);
				categoryAdapter2
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				jdBmSpinner.setAdapter(categoryAdapter2);
				jdBmSpinner.setSelection(0, false);
				break;
			case HandlerException.Fail:
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.ZbSuccess:
				Toast.makeText(GwpfDetailActivity.this, "转办成功!",
						Toast.LENGTH_LONG).show();
				GwpfDetailActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);
				break;
			case HandlerException.ZbError:
				Toast.makeText(GwpfDetailActivity.this, "转办失败!",
						Toast.LENGTH_LONG).show();
				break;
			case HandlerException.PdSuccess:
				Intent intent = new Intent(GwpfDetailActivity.this,
						PdPersonSelectActivity.class);
				intent.putExtra("jsonArray", jsonArray);
				intent.putExtra("id", gwpfDetail.getId());
				intent.putExtra("loginname", loginname);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				GwpfDetailActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);
				break;
			case HandlerException.TdSuccess:
				Toast.makeText(GwpfDetailActivity.this, "退单成功!",
						Toast.LENGTH_LONG).show();
				GwpfDetailActivity.this.finish();

				break;
			case HandlerException.TdError:
				Toast.makeText(GwpfDetailActivity.this, "退单失败!",
						Toast.LENGTH_LONG).show();
				break;
			default:

				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gwpf_detail);
		init();
		Event();
		new Thread() {
			public void run() {
				gwpftAction(gwpftUrl(pfId, loginname));
			};
		}.start();
		new Thread() {
			public void run() {
				txlDataAction(txlUrl());
			};
		}.start();
	}

	// 初始化找到控件
	private void init() {
		settingUtils = new SettingUtils(GwpfDetailActivity.this);
		pfId = getIntent().getExtras().getString("pfId");
		loginname = getIntent().getExtras().getString("loginname");
		uid = getIntent().getExtras().getString("uid");
		bsjbTextView = (TextView) this.findViewById(R.id.bsjb);
		khNameTextView = (TextView) this.findViewById(R.id.khName);
		ageTextView = (TextView) this.findViewById(R.id.age);
		sexTextView = (TextView) this.findViewById(R.id.sex);
		sxTextView = (TextView) this.findViewById(R.id.sx);
		addressTextView = (TextView) this.findViewById(R.id.address);
		telTextView = (TextView) this.findViewById(R.id.tel);
		shengTextView = (TextView) this.findViewById(R.id.sheng);
		shiTextView = (TextView) this.findViewById(R.id.shi);
		quTextView = (TextView) this.findViewById(R.id.qu);
		jdTextView = (TextView) this.findViewById(R.id.jd);
		sqTextView = (TextView) this.findViewById(R.id.sq);
		zdyTextView = (TextView) this.findViewById(R.id.zdy);
		gtTextView = (TextView) this.findViewById(R.id.gt);
		khjjTextView = (TextView) this.findViewById(R.id.khjj);
		gdhTextView = (TextView) this.findViewById(R.id.gdh);
		startTimeTextView = (TextView) this.findViewById(R.id.detail_startTime);
		fwjjTextView = (TextView) this.findViewById(R.id.fwjj);
		fwbzTextView = (TextView) this.findViewById(R.id.fwbz);
		moneyTextView = (TextView) this.findViewById(R.id.money);
		remarksEditText = (EditText) this.findViewById(R.id.remarks);
		waitWebView = (WebView) this.findViewById(R.id.wait);
		waitWebView.getSettings().setDefaultTextEncodingName("utf-8");
		// pfBmSpinner = (Spinner) this.findViewById(R.id.pfBm);
		jdBmSpinner = (Spinner) this.findViewById(R.id.jdBm);
		registerForContextMenu(telTextView);

	}

	// 赋予值
	private void setValue() {
		sxTextView.setText(gwpfDetail.getJigou());
		bsjbTextView.setText(gwpfDetail.getJibie());
		khNameTextView.setText(gwpfDetail.getKhName());
		ageTextView.setText(gwpfDetail.getKhAge());
		sexTextView.setText(gwpfDetail.getKhSex());
//		sxTextView.setText(gwpfDetail.getKhSx());
		addressTextView.setText(gwpfDetail.getAddress());
		telTextView.setText(gwpfDetail.getTel());
		shengTextView.setText(gwpfDetail.getSheng());
		shiTextView.setText(gwpfDetail.getShi());
		quTextView.setText(gwpfDetail.getQu());
		jdTextView.setText(gwpfDetail.getJiedao());
		sqTextView.setText(gwpfDetail.getSq());
		zdyTextView.setText(gwpfDetail.getZdy());
		gtTextView.setText(gwpfDetail.getKhTg());
		khjjTextView.setText(gwpfDetail.getKhJj());
		gdhTextView.setText(gwpfDetail.getLsh());
		startTimeTextView.setText(gwpfDetail.getStartTime());
		fwjjTextView.setText(gwpfDetail.getJianjie());
		fwbzTextView.setText(gwpfDetail.getBeizhu());
		moneyTextView.setText(gwpfDetail.getMoney());
		StringBuffer html = new StringBuffer();
		html.append("<html><head></head><body>");
		html.append(gwpfDetail.getBsjj());
		html.append("</body></html>");
		System.out.println("打印:" + html.toString());
		waitWebView.loadDataWithBaseURL(null, html.toString(), "text/html",
				"utf-8", null);

		pdbmId = gwpfDetail.getBmid();

	}

	// 数据校验
	private boolean checkData() {
		remarks = remarksEditText.getText().toString().trim();
		// if (remarks.length() == 0) {
		// Toast.makeText(GwpfDetailActivity.this, "备注不能为空!",
		// Toast.LENGTH_LONG).show();
		// return false;
		// }
		return true;
	}

	private boolean checkData2() {
		remarks = remarksEditText.getText().toString().trim();
		if (remarks.length() == 0) {
			Toast.makeText(GwpfDetailActivity.this, "备注不能为空!",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (jdbmId.equals("0")) {
			Toast.makeText(GwpfDetailActivity.this, "请选择接单部门!",
					Toast.LENGTH_LONG).show();
			return false;
		} else {
			if (pdbmId.equals(jdbmId)) {
				Toast.makeText(GwpfDetailActivity.this, "派单与转办部门相同!",
						Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}

	private boolean checkData3() {
		remarks = remarksEditText.getText().toString().trim();
		if (remarks.length() == 0) {
			Toast.makeText(GwpfDetailActivity.this, "备注不能为空!",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!jdbmId.equals("0")) {
			Toast.makeText(GwpfDetailActivity.this, "不能选择转办部门!",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private void Event() {
		// 返回
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						GwpfDetailActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 派发
		this.findViewById(R.id.pfBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (checkData()) {
					new Thread() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();

							gwAction(gwpf_zbtUrl("1"));
						}
					}.start();
				}

			}
		});
		// 转办
		this.findViewById(R.id.zbBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (checkData2()) {
					new Thread() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();

							gwAction(gwpf_zbtUrl("2"));
						}
					}.start();
				}

			}
		});
		// 退单
		this.findViewById(R.id.tdBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (checkData3()) {
					new Thread() {
						public void run() {
							gwTdAction(gwTdUrl());
						};
					}.start();
				}
			}
		});

		jdBmSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				jdbmId = typeList2.get(position).getId();
				// Toast.makeText(GwpfDetailActivity.this, pdbmId + "",
				// Toast.LENGTH_LONG).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	// 获取详细
	private StringBuffer gwpftUrl(String id, String loginname) {
		StringBuffer gwpftUrl = new StringBuffer(SettingUtils.get("serverIp"));
		gwpftUrl.append(SettingUtils.get("gwpf_detail_url")).append("id=")
				.append(id).append("&loginname=").append(loginname);
		System.out.println("公文详细url:" + gwpftUrl);
		return gwpftUrl;
	}

	// 公文派发或转办
	private StringBuffer gwpf_zbtUrl(String flag) {
		StringBuffer gwpf_zbtUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		try {
			// 是派发公文
			if (flag.equals("1")) {
				gwpf_zbtUrl.append(SettingUtils.get("gwpf_zb_url"))
						.append("flag=").append(flag).append("&id=")
						.append(gwpfDetail.getId()).append("&jbmid=")
						.append("").append("&pbmid=").append(pdbmId)
						.append("&beizhu=").append(UrlEncode.Encode2(remarks))
						.append("&gwid=").append(gwpfDetail.getGwId());
				// 是转办公文
			} else if (flag.equals("2")) {
				gwpf_zbtUrl.append(SettingUtils.get("gwpf_zb_url"))
						.append("flag=").append(flag).append("&id=")
						.append(gwpfDetail.getId()).append("&jbmid=")
						.append(jdbmId).append("&pbmid=").append(pdbmId)
						.append("&beizhu=").append(UrlEncode.Encode2(remarks))
						.append("&gwid=").append(gwpfDetail.getGwId());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("公文派发url：" + gwpf_zbtUrl);
		return gwpf_zbtUrl;
	}

	// 工单派发
	@SuppressLint("NewApi")
	private void gwpftAction(StringBuffer gwpftUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwpftUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				message = message.replaceAll("null", "");
				JSONObject object = new JSONObject(message);
				if (!object.getString("contents1").isEmpty()) {

					JSONObject gw = new JSONObject(
							object.getString("contents1"));
					gwpfDetail = new GwpfDetail();
					gwpfDetail.setBsjj(gw.getString("bsjj"));
					gwpfDetail.setId(gw.getString("id"));
					gwpfDetail.setKhName(gw.getString("kh_name"));
					gwpfDetail.setKhAge(gw.getString("kh_age"));
					gwpfDetail.setKhSex(gw.getString("kh_sex"));
					gwpfDetail.setAddress(gw.getString("kh_address"));
					gwpfDetail.setTel(gw.getString("kh_tel"));
					gwpfDetail.setZbId(gw.getString("zbid"));
					gwpfDetail.setKhJj(gw.getString("kh_jj"));
					gwpfDetail.setKhTg(gw.getString("kh_goutong"));
					gwpfDetail.setStartTime(gw.getString("starttime"));
					gwpfDetail.setJianjie(gw.getString("jianjie"));
					gwpfDetail.setBeizhu(gw.getString("beizhu"));
					gwpfDetail.setGwId(gw.getString("gwid"));
					gwpfDetail.setKhSx(gw.getString("kh_sx"));
					gwpfDetail.setJibie(gw.getString("jibie"));
					gwpfDetail.setSheng(gw.getString("sheng"));
					gwpfDetail.setShi(gw.getString("shi"));
					gwpfDetail.setQu(gw.getString("qu"));
					gwpfDetail.setJiedao(gw.getString("jiedao"));
					gwpfDetail.setSq(gw.getString("sq"));
					gwpfDetail.setZdy(gw.getString("zdy"));
					gwpfDetail.setPic(gw.getString("kh_pic"));
					gwpfDetail.setLsh(gw.getString("lsh"));
					gwpfDetail.setMoney(gw.getString("moneys"));
					gwpfDetail.setBmid(gw.getString("bmid"));
					gwpfDetail.setKh_id(gw.getString("kh_id"));
					gwpfDetail.setLoginname(gw.getString("loginname"));
					gwpfDetail.setJigou(gw.getString("jigou"));
				}
				handler.sendEmptyMessage(HandlerException.Success);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	/*----------------------公文退单-------------------------*/
	// gwpf_zbtUrl.append(SettingUtils.get("gwpf_zb_url"))
	// .append("flag=").append(flag).append("&id=")
	// .append(gwpfDetail.getId()).append("&jbmid=")
	// .append(jdbmId).append("&pbmid=").append(pdbmId)
	// .append("&beizhu=").append(UrlEncode.Encode(remarks))
	// .append("&gwid=").append(gwpfDetail.getGwId());
	private StringBuffer gwTdUrl() {
		StringBuffer gwTdUrl = new StringBuffer(SettingUtils.get("serverIp"));
		try {
			gwTdUrl.append(SettingUtils.get("gwpf_zb_url")).append("&flag=3")
					.append("&id=").append(gwpfDetail.getId())
					.append("&jbmid=").append(jdbmId).append("&pbmid=")
					.append(pdbmId).append("&beizhu=")
					.append(UrlEncode.Encode2(remarks)).append("&gwid=")
					.append(gwpfDetail.getGwId()).append("&loginname=")
					.append(UrlEncode.Encode2(gwpfDetail.getLoginname()))
					.append("&khid=").append(gwpfDetail.getKh_id())
					.append("&jianjie=")
					.append(UrlEncode.Encode2(gwpfDetail.getJianjie()))
					.append("&jibie=")
					.append(UrlEncode.Encode2(gwpfDetail.getJibie()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return gwTdUrl;
	}

	private void gwTdAction(StringBuffer gwTdUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwTdUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				if (message.equals("success")) {
					handler.sendEmptyMessage(HandlerException.TdSuccess);
				} else if (message.equals("error")) {
					handler.sendEmptyMessage(HandlerException.TdError);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	/*---------------------------获取转办部门-----------------------*/
	// 通讯录 url
	private StringBuffer txlUrl() {
		StringBuffer txlUrl = new StringBuffer(SettingUtils.get("serverIp"));
		txlUrl.append(SettingUtils.get("query_bm2_url")).append("uid=")
				.append(uid);
		return txlUrl;
	}

	// 通讯录 action
	private void txlDataAction(StringBuffer txlUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					txlUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("contents"));
				if (array.length() != 0) {
					typeList2 = new ArrayList<Type>();
					Type type1 = new Type();
					type1.setId("0");
					type1.setName("--请选择--");
					typeList2.add(type1);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = (JSONObject) array.get(i);
						Type type = new Type();
						type.setId(obj.getString("id"));
						type.setName(obj.getString("bmname"));
						typeList2.add(type);
					}
					handler.sendEmptyMessage(HandlerException.SuccessToo);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);

		}

	}

	private void gwAction(StringBuffer gwpf_zbtUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					gwpf_zbtUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				if (message.equals("success")) {
					handler.sendEmptyMessage(HandlerException.ZbSuccess);
				} else if (message.equals("error")) {
					handler.sendEmptyMessage(HandlerException.ZbError);
				} else {
					JSONObject object = new JSONObject(message);
					jsonArray = object.getString("contents");
					handler.sendEmptyMessage(HandlerException.PdSuccess);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
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
			PhoneUtils.Tel(GwpfDetailActivity.this, telTextView.getText()
					.toString().trim());
			break;
		case ITEM2:
			// 在这里添加处理代码
			ClipboardManager cmb = (ClipboardManager) this
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(telTextView.getText().toString().trim());
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GwpfDetailActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
