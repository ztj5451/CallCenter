package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONObject;

import com.callCenter.activity.R.id;
import com.callCenter.entity.JdDetail;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.PhoneUtils;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.TimeUtils;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.io.GpDevice;
import com.gprinter.sample.PrinterConnectDialog;
import com.gprinter.service.GpPrintService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JdDetailActivity extends Activity {
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private TextView bsjbTextView, nameTextView, ageTextView, sexTextView,
			sxTextView, addressTextView, telTextView, shengTextView,
			shiTextView, quTextView, jiedaoTextView, sqTextView, zdyTextView,
			gtndTextView, khjjTextView, moneyTextView, lshTextView,
			startTimeTextView, fwjjTextView, fwbzTextView, jdbmbzTextView,
			fwMoneyTextView;
	private LinearLayout fwLinear, printLayout;
	private SettingUtils settingUtils;
	private String gdId, status;
	private WebView waitWebView;
	private JdDetail jdDetail;
	// 蓝牙打印
	private GpService mGpService = null;
	public static final String CONNECT_STATUS = "connect.status";
	private static final String DEBUG_TAG = "MainActivity";
	private PrinterServiceConnection conn = null;
	private int mPrinterIndex = 0;
	private int mTotalCopies = 0;

	class PrinterServiceConnection implements ServiceConnection {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("ServiceConnection", "onServiceDisconnected() called");
			mGpService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mGpService = GpService.Stub.asInterface(service);
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				setValue();
				break;
			case HandlerException.Fail:
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.jd_detail);
		init();
		Event();
		new Thread() {
			public void run() {
				jdDetailAction(jdDetailUrl(gdId));
			};
		}.start();
		connection();
	}

	private void connection() {
		conn = new PrinterServiceConnection();
		Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
		bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
	}

	public boolean[] getConnectState() {
		boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
		for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
			state[i] = false;
		}
		for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
			try {
				if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
					state[i] = true;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return state;
	}

	public void openPortDialogueClicked() {
		Log.d(DEBUG_TAG, "openPortConfigurationDialog ");
		Intent intent = new Intent(this, PrinterConnectDialog.class);
		boolean[] state = getConnectState();
		intent.putExtra(CONNECT_STATUS, state);
		this.startActivity(intent);
	}

	// 获取打印机状态
	public void getPrinterStatusClicked() {
		try {
			mTotalCopies = 0;
			int status = mGpService.queryPrinterStatus(mPrinterIndex, 500);
			String str = new String();
			if (status == GpCom.STATE_NO_ERR) {
				myPrint();
			} else {
				str = "打印机 ";
				if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
					str += "脱机";
					openPortDialogueClicked();
				}
				if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
					str += "缺纸";
				}
				if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
					str += "打印机开盖";
				}
				if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
					str += "打印机出错";
				}
				if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
					str += "查询超时";
				}
			}
			Toast.makeText(getApplicationContext(),
					"打印机：" + mPrinterIndex + " 状态：" + str, Toast.LENGTH_SHORT)
					.show();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void myPrint() {
		EscCommand esc = new EscCommand();
		esc.addPrintAndFeedLines((byte) 3);
		esc.addText("长春市精诚居家养老服务中心\n");
		esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON,
				ENABLE.OFF);// 设置为倍高倍宽
		esc.addPrintAndFeedLines((byte) 1);
		esc.addText("服务结算单\n"); // 打印文字
		esc.addPrintAndLineFeed();
		/* 打印文字 */
		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF,
				ENABLE.OFF);// 取消倍高倍宽
		esc.addSelectJustification(JUSTIFICATION.LEFT);// 设置打印左对齐
		esc.addText("------------------------------\n\n");
		esc.addText("单号: " + jdDetail.getLsh() + "\n"); // 打印文字
		esc.addText("姓名: " + jdDetail.getKhName() + "\n"); // 打印文字
		esc.addText("电话: " + jdDetail.getTel() + "\n");
		esc.addText("地址: " + jdDetail.getAddress() + "\n");
		esc.addText("所在地: " + jdDetail.getSheng() + jdDetail.getShi()
				+ jdDetail.getQu() + jdDetail.getJiedao() + jdDetail.getSq()
				+ jdDetail.getZdy() + "\n\n");
		esc.addText("------------------------------\n");
		esc.addText("服务内容: " + jdDetail.getFwjj() + "\n");
		esc.addText("服务金额: " + jdDetail.getKfnum() + "元\n");
		esc.addText("服务人员: " + jdDetail.getFwry() + "\n");
		esc.addText("------------------------------\n");
		esc.addPrintAndFeedLines((byte) 1);
		esc.addText("客户签名:_________________\n\n");
		esc.addText("服务时间:" + TimeUtils.getPrintDateTime() + "\n\n");
		esc.addText("账户余额: " + jdDetail.getMoney() + "元\n");
		esc.addText("------------------------------\n");
		esc.addText("备注:\n\n\n\n");
		esc.addText("------------------------------\n");
		esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
		esc.addText("播洒爱心    践行公益 \n");
		esc.addText("体验高尚    促进和谐\n");
		esc.addPrintAndFeedLines((byte) 1);
		esc.addText("服务电话:  4008653789\n");
		esc.addPrintAndFeedLines((byte) 4);
		Vector<Byte> datas = esc.getCommand(); // 发送数据
		Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
		byte[] bytes = ArrayUtils.toPrimitive(Bytes);
		String str = Base64.encodeToString(bytes, Base64.DEFAULT);
		int rel;
		try {
			rel = mGpService.sendEscCommand(mPrinterIndex, str);
			GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
			if (r != GpCom.ERROR_CODE.SUCCESS) {
				Toast.makeText(getApplicationContext(), GpCom.getErrorText(r),
						Toast.LENGTH_SHORT).show();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() {
		settingUtils = new SettingUtils(JdDetailActivity.this);
		gdId = getIntent().getExtras().getString("gdId");
		status = getIntent().getExtras().getString("status");
		bsjbTextView = (TextView) this.findViewById(R.id.bsjb);
		nameTextView = (TextView) this.findViewById(R.id.name);
		ageTextView = (TextView) this.findViewById(R.id.age);
		sexTextView = (TextView) this.findViewById(R.id.kh_sex);
		sxTextView = (TextView) this.findViewById(R.id.sx);
		addressTextView = (TextView) this.findViewById(R.id.address);
		telTextView = (TextView) this.findViewById(R.id.tel);
		shengTextView = (TextView) this.findViewById(R.id.sheng);
		shiTextView = (TextView) this.findViewById(R.id.shi);
		quTextView = (TextView) this.findViewById(R.id.qu);
		jiedaoTextView = (TextView) this.findViewById(R.id.jiedao);
		sqTextView = (TextView) this.findViewById(R.id.shequ);
		zdyTextView = (TextView) this.findViewById(R.id.zdy);
		gtndTextView = (TextView) this.findViewById(R.id.gtnd);
		khjjTextView = (TextView) this.findViewById(R.id.khjj);
		moneyTextView = (TextView) this.findViewById(R.id.money);
		lshTextView = (TextView) this.findViewById(R.id.lsh);
		startTimeTextView = (TextView) this.findViewById(R.id.startTime);
		fwjjTextView = (TextView) this.findViewById(R.id.fwjj);
		fwbzTextView = (TextView) this.findViewById(R.id.fwbz);
		jdbmbzTextView = (TextView) this.findViewById(R.id.jdbmbz);
		waitWebView = (WebView) this.findViewById(R.id.wait);
		fwMoneyTextView = (TextView) this.findViewById(R.id.fwMoney);
		fwLinear = (LinearLayout) this.findViewById(R.id.fwLinear);
		printLayout = (LinearLayout) this.findViewById(R.id.printLayout);
		waitWebView.getSettings().setDefaultTextEncodingName("utf-8");
		registerForContextMenu(telTextView);
		if (status.equals("0")) {
			fwLinear.setVisibility(View.GONE);
			printLayout.setVisibility(View.GONE);
		}
	}

	private void setValue() {
		bsjbTextView.setText(jdDetail.getJibie());
		nameTextView.setText(jdDetail.getKhName());
		ageTextView.setText(jdDetail.getAge());
		sexTextView.setText(jdDetail.getSex());
		sxTextView.setText(jdDetail.getJigou());
		addressTextView.setText(jdDetail.getAddress());
		telTextView.setText(jdDetail.getTel());
		shengTextView.setText(jdDetail.getSheng());
		shiTextView.setText(jdDetail.getShi());
		quTextView.setText(jdDetail.getQu());
		jiedaoTextView.setText(jdDetail.getJiedao());
		sqTextView.setText(jdDetail.getSq());
		zdyTextView.setText(jdDetail.getZdy());
		gtndTextView.setText(jdDetail.getKhgt());
		khjjTextView.setText(jdDetail.getKhjj());
		moneyTextView.setText(jdDetail.getMoney());
		lshTextView.setText(jdDetail.getLsh());
		startTimeTextView.setText(jdDetail.getStartTime());
		fwjjTextView.setText(jdDetail.getFwjj());
		fwbzTextView.setText(jdDetail.getFwbz());
		jdbmbzTextView.setText(jdDetail.getBz());
		fwMoneyTextView.setText(jdDetail.getKfnum());
		StringBuffer html = new StringBuffer();
		html.append("<html><head></head><body>");
		html.append(jdDetail.getBsjj());
		html.append("</body></html>");
		System.out.println("打印:" + html.toString());
		waitWebView.loadDataWithBaseURL(null, html.toString(), "text/html",
				"utf-8", null);
	}

	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						JdDetailActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 打印结算单
		this.findViewById(R.id.printBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						getPrinterStatusClicked();
					}
				});
	}

	// 公文发起URL
	private StringBuffer jdDetailUrl(String gdId) {
		StringBuffer jdDetailUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		jdDetailUrl.append(SettingUtils.get("serverPerson_detail_url"))
				.append("id=").append(gdId);
		System.out.println("接单详细:" + jdDetailUrl);
		return jdDetailUrl;
	}

	// 工单派发
	private void jdDetailAction(StringBuffer jdDetailUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					jdDetailUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				message = message.replaceAll("null", "");
				JSONObject object = new JSONObject(message);
				String temp = object.getString("contents");
				JSONObject obj = new JSONObject(temp);
				jdDetail = new JdDetail();
				jdDetail.setBsjj(obj.getString("bsjj"));
				jdDetail.setJibie(obj.getString("jibie"));
				jdDetail.setKhName(obj.getString("kh_name"));
				jdDetail.setAge(obj.getString("kh_age"));
				jdDetail.setSex(obj.getString("kh_sex"));
				jdDetail.setSx(obj.getString("kh_sx"));
				jdDetail.setAddress(obj.getString("kh_address"));
				jdDetail.setTel(obj.getString("kh_tel"));
				jdDetail.setSheng(obj.getString("sheng"));
				jdDetail.setShi(obj.getString("shi"));
				jdDetail.setQu(obj.getString("qu"));
				jdDetail.setJiedao(obj.getString("jiedao"));
				jdDetail.setSq(obj.getString("sq"));
				jdDetail.setZdy(obj.getString("zdy"));
				jdDetail.setKhgt(obj.getString("kh_goutong"));
				jdDetail.setKhjj(obj.getString("kh_jj"));
				jdDetail.setMoney(obj.getString("moneys"));
				jdDetail.setLsh(obj.getString("lsh"));
				jdDetail.setStartTime(obj.getString("starttime"));
				jdDetail.setFwjj(obj.getString("bsjj"));
				jdDetail.setFwbz(obj.getString("beizhu"));
				jdDetail.setBz(obj.getString("zbbeizhu"));
				jdDetail.setJigou(obj.getString("jigou"));
				jdDetail.setKfnum(obj.getString("kfnum"));
				jdDetail.setFwry(obj.getString("fwry"));
				handler.sendEmptyMessage(HandlerException.Success);

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
			PhoneUtils.Tel(JdDetailActivity.this, telTextView.getText()
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
	public void onDestroy() {
		Log.e(DEBUG_TAG, "onDestroy");
		super.onDestroy();
		if (conn != null) {
			unbindService(conn); // unBindService
		}
	}

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			JdDetailActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
