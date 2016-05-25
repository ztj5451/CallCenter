package com.callCenter.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.callCenter.adapter.CustomerInfoAdapter;
import com.callCenter.entity.CustomerInfo;
import com.callCenter.entity.JG;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.RequestAndResultCode;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerInfoActivity extends Activity {
	private ProgressDialog progressDialog;
	private EditText kh_name, kh_phone, kh_cardId, kh_name_jp, moneys;
	private Button sheng, shi, qu, jiedao, sq, zdy, pre, next;
	private Spinner cjlbSpinner, cjdjSpinner, ssjgSpinner;
	private String nameStr, phoneStr, cardIdStr, name_jpStr, moneysStr,
			cjlbStr = "", cjdjStr = "";
	private boolean isShow = false;
	private boolean isSheng = false, isShi = false, isQu = false,
			isJiedao = false, isSq = false, isZdy;
	private String shengId = "", shiId = "", quId = "", jiedaoId = "",
			sqId = "", zdyId = "", jgId;
	private String areaId = "0";
	private String userId;
	private LinearLayout searchCondition;
	private SettingUtils settingUtils;
	private ListView customerInfoListView;
	private CustomerInfoAdapter adapter;
	private List<CustomerInfo> customerList;
	private int count, zongyeshu, pager;
	private ArrayAdapter<String> cjlbAdapter, cjdjAdapter, ssjgAdapter;
	private String[] cjlbArray, cjdjArray, lb, dj, ssjgArray, ssjgIdArray;
	private LinearLayout fenye;
	private int currentPage = 1;
	private LinearLayout countLayout;
	private TextView customerCount;
	private List<JG> jgList;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1000:
				ssjgAdapter = new ArrayAdapter<String>(
						CustomerInfoActivity.this,
						android.R.layout.simple_spinner_item, ssjgArray);
				ssjgAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				ssjgSpinner.setAdapter(ssjgAdapter);
				ssjgSpinner.setSelection(0, false);
				break;
			case HandlerException.Success:

				fenye.setVisibility(View.GONE);

				customerInfoListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				if (zongyeshu > 1) {
					fenye.setVisibility(View.VISIBLE);
					if (pager == 1) {
						pre.setEnabled(false);
					} else {
						pre.setEnabled(true);
					}
					if (zongyeshu == pager) {
						next.setEnabled(false);
					} else {
						next.setEnabled(true);
					}
				}

				progressDialog.dismiss();
				customerCount.setText(count + "");
				countLayout.setVisibility(View.VISIBLE);
				break;
			case HandlerException.Fail:
				fenye.setVisibility(View.GONE);
				customerList.clear();
				adapter.notifyDataSetChanged();
				Toast.makeText(CustomerInfoActivity.this, "没有查询结果!",
						Toast.LENGTH_SHORT).show();

				progressDialog.dismiss();
				customerCount.setText("0");
				countLayout.setVisibility(View.VISIBLE);
				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				fenye.setVisibility(View.GONE);
				customerList.clear();
				adapter.notifyDataSetChanged();
				Toast.makeText(CustomerInfoActivity.this, "无查询结果，请重试",
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				customerCount.setText("0");
				countLayout.setVisibility(View.VISIBLE);
				break;
			case HandlerException.IOException:
				fenye.setVisibility(View.GONE);
				customerList.clear();
				adapter.notifyDataSetChanged();
				Toast.makeText(CustomerInfoActivity.this, "无查询结果，请重试",
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				customerCount.setText("0");
				countLayout.setVisibility(View.VISIBLE);
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
		setContentView(R.layout.customer_info);
		init();
		Event();
	}

	private void init() {
		// 加载机构
		new Thread() {
			public void run() {
				loadJG();
			};
		}.start();

		customerList = new ArrayList<CustomerInfo>();
		adapter = new CustomerInfoAdapter(CustomerInfoActivity.this,
				customerList);
		fenye = (LinearLayout) this.findViewById(R.id.fenye);
		cjlbSpinner = (Spinner) this.findViewById(R.id.cjlb);
		cjdjSpinner = (Spinner) this.findViewById(R.id.cjdj);
		ssjgSpinner = (Spinner) this.findViewById(R.id.ssjg);
		cjlbArray = new String[] { "请选择", "视力", "听力", "语言", "肢体", "智力", "精神",
				"多重", "其他" };
		lb = new String[] { "", "st", "tl", "yy", "zt", "zl", "js", "dc", "qt" };
		cjdjArray = new String[] { "请选择", "一级", "二级", "三级", "四级" };
		dj = new String[] { "", "一级", "二级", "三级", "四级" };
		cjlbAdapter = new ArrayAdapter<String>(CustomerInfoActivity.this,
				android.R.layout.simple_spinner_item, cjlbArray);
		cjlbAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cjlbSpinner.setAdapter(cjlbAdapter);
		cjlbSpinner.setSelection(0, false);

		cjdjAdapter = new ArrayAdapter<String>(CustomerInfoActivity.this,
				android.R.layout.simple_spinner_item, cjdjArray);
		cjdjAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cjdjSpinner.setAdapter(cjdjAdapter);
		cjdjSpinner.setSelection(0, false);

		settingUtils = new SettingUtils(CustomerInfoActivity.this);
		customerInfoListView = (ListView) this
				.findViewById(R.id.customer_info_listView);

		userId = getIntent().getExtras().getString("userId");
		searchCondition = (LinearLayout) this
				.findViewById(R.id.searchCondition);
		kh_name = (EditText) this.findViewById(R.id.kh_name);
		kh_phone = (EditText) this.findViewById(R.id.kh_phone);
		kh_cardId = (EditText) this.findViewById(R.id.kh_cardId);
		kh_name_jp = (EditText) this.findViewById(R.id.kh_name_jp);
		moneys = (EditText) this.findViewById(R.id.moneys);
		sheng = (Button) this.findViewById(R.id.sheng);
		shi = (Button) this.findViewById(R.id.shi);
		qu = (Button) this.findViewById(R.id.qu);
		jiedao = (Button) this.findViewById(R.id.jiedao);
		sq = (Button) this.findViewById(R.id.sq);
		zdy = (Button) this.findViewById(R.id.zdy);
		pre = (Button) this.findViewById(R.id.pre);
		next = (Button) this.findViewById(R.id.next);
		cjlbSpinner = (Spinner) this.findViewById(R.id.cjlb);
		cjdjSpinner = (Spinner) this.findViewById(R.id.cjdj);
		countLayout = (LinearLayout) this.findViewById(R.id.countLayout);
		customerCount = (TextView) this.findViewById(R.id.customerCount);
	}

	private void getCondition() {
		nameStr = kh_name.getText().toString().trim();
		phoneStr = kh_phone.getText().toString().trim();
		cardIdStr = kh_cardId.getText().toString().trim();
		name_jpStr = kh_name_jp.getText().toString().trim();
		moneysStr = moneys.getText().toString().trim();
		// shengStr
		// shiStr,
		// quStr,
		// jiedaoStr,
		// sqStr,
		// zdyStr,
		// cjlbStr,
		// cjdjStr

	}

	private boolean checkCondition() {
		if (nameStr.equals("") && phoneStr.equals("") && cardIdStr.equals("")
				&& name_jpStr.equals("") && moneysStr.equals("")
				&& cjdjStr.equals("") && cjlbStr.equals("")
				&& shengId.equals("") && shiId.equals("")
				&& jiedaoId.equals("") && sqId.equals("") && zdyId.equals("")&&jgId.equals("")) {
			Toast.makeText(CustomerInfoActivity.this, "请输入查询条件",
					Toast.LENGTH_SHORT).show();
			return false;

		} else {
			return true;
		}

	}

	private void clearSearchCondition() {
		kh_name.setText("");
		kh_phone.setText("");
		kh_cardId.setText("");
		kh_name_jp.setText("");
		moneys.setText("");
		nameStr = "";
		phoneStr = "";
		cardIdStr = "";
		name_jpStr = "";
		moneysStr = "";
		cjlbStr = "";
		cjdjStr = "";
		shengId = "";
		shiId = "";
		quId = "";
		jiedaoId = "";
		sqId = "";
		zdyId = "";
		cjlbSpinner.setSelection(0, false);
		cjdjSpinner.setSelection(0, false);
		ssjgSpinner.setSelection(0, false);
		sheng.setText("选择");
		shi.setText("选择");
		qu.setText("选择");
		jiedao.setText("选择");
		sq.setText("选择");
		zdy.setText("选择");

	}

	private void Event() {
		searchCondition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				searchCondition.setVisibility(View.GONE);
				isShow = false;
					
				

			}
		});
		pre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 强制隐藏键盘
				progressDialog = ProgressDialog.show(CustomerInfoActivity.this,
						"温馨提示", "正在查询数据中...", true, true);
				if (currentPage == 2) {
					Toast.makeText(CustomerInfoActivity.this, "当前已是第一页",
							Toast.LENGTH_SHORT).show();
				}
				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						customerListAction(customerListUrl(currentPage = currentPage - 1));
					}
				}.start();

			}
		});
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 强制隐藏键盘
				progressDialog = ProgressDialog.show(CustomerInfoActivity.this,
						"温馨提示", "正在查询数据中...", true, true);
				if (currentPage == zongyeshu - 1) {
					Toast.makeText(CustomerInfoActivity.this, "当前已是最后一页",
							Toast.LENGTH_SHORT).show();
				}
				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						customerListAction(customerListUrl(currentPage = currentPage + 1));
					}
				}.start();

			}
		});
		cjlbSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int position, long id) {
				cjlbStr = lb[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		cjdjSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				cjdjStr = cjdjArray[position];

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		ssjgSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				jgId=ssjgIdArray[position];

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						CustomerInfoActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 清空按钮
		this.findViewById(R.id.clear).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				clearSearchCondition();
			}
		});
		// 搜索按钮
		this.findViewById(R.id.search).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						currentPage = 1;

						if (!isShow) {
							searchCondition.setVisibility(View.VISIBLE);
							isShow = true;
					
							

						} else {
							// 强制隐藏键盘

							searchCondition.setVisibility(View.GONE);
							isShow = false;
							getCondition();
							if (checkCondition()) {
								progressDialog = ProgressDialog.show(
										CustomerInfoActivity.this, "温馨提示",
										"正在查询数据中...", true, true);
								new Thread() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										super.run();
										customerListAction(customerListUrl(currentPage));
									}
								}.start();
							}

						}

					}
				});
		sheng.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CustomerInfoActivity.this,
						SelectAreaActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("flag", "");
				intent.putExtra("areaName", "guo");
				intent.putExtra("url", "hj_sheng.jsp");
				startActivityForResult(intent,
						RequestAndResultCode.SELECT_SHENG);
				isSheng = true;
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
		shi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CustomerInfoActivity.this,
						SelectAreaActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("flag", areaId);
				intent.putExtra("areaName", "sheng");
				intent.putExtra("url", "hj_shi.jsp");
				startActivityForResult(intent,
						RequestAndResultCode.SELECT_SHENG);
				isShi = true;
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
		qu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CustomerInfoActivity.this,
						SelectAreaActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("flag", areaId);
				intent.putExtra("areaName", "shi");
				intent.putExtra("url", "hj_qu.jsp");
				startActivityForResult(intent,
						RequestAndResultCode.SELECT_SHENG);
				isQu = true;
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
		jiedao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CustomerInfoActivity.this,
						SelectAreaActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("flag", areaId);
				intent.putExtra("areaName", "qu");
				intent.putExtra("url", "hj_jiedao.jsp");
				startActivityForResult(intent,
						RequestAndResultCode.SELECT_SHENG);
				isJiedao = true;
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
		sq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CustomerInfoActivity.this,
						SelectAreaActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("flag", areaId);
				intent.putExtra("areaName", "jiedao");
				intent.putExtra("url", "hj_sq.jsp");
				startActivityForResult(intent,
						RequestAndResultCode.SELECT_SHENG);
				isSq = true;
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
		zdy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CustomerInfoActivity.this,
						SelectAreaActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("flag", areaId);
				intent.putExtra("areaName", "sq");
				intent.putExtra("url", "hj_zdy.jsp");
				startActivityForResult(intent,
						RequestAndResultCode.SELECT_SHENG);
				isZdy = true;
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
		customerInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Intent intent = new Intent(CustomerInfoActivity.this,
						CustomerInfoDetailActivity.class);
				intent.putExtra("id", customerList.get(position).getId());
				CustomerInfoActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});

	}

	// 客户信息列表url
	private StringBuffer customerListUrl(int currentPage) {
		StringBuffer customerListUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
	
		try {
			customerListUrl.append(SettingUtils.get("customer_list_url"))
					.append("khname=").append(UrlEncode.Encode2(nameStr))
					.append("&khnd=").append("").append("&khpy=")
					.append(name_jpStr).append("&khpying=").append("")
					.append("&khcardid=").append(cardIdStr).append("&sheng=")
					.append(shengId).append("&shi=").append(shiId)
					.append("&qu=").append(quId).append("&jiedao=")
					.append(jiedaoId).append("&sq=").append(sqId)
					.append("&zdy=").append(zdyId).append("&jg=").append(jgId)
					.append("&cjlb=").append(cjlbStr).append("&cjdj=")
					.append(UrlEncode.Encode2(cjdjStr)).append("&sfdb=")
					.append("").append("&kh_tel=").append(phoneStr)
					.append("&moneys=").append(moneysStr).append("&pager=")
					.append(currentPage);
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("客户管理:" + customerListUrl.toString());
		return customerListUrl;
	}

	// 客户信息列表action
	private void customerListAction(StringBuffer loginUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loginUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				JSONObject object = new JSONObject(message.replace("null", ""));
				JSONArray array = object.getJSONArray("contents");
				count = object.getInt("count");
				zongyeshu = object.getInt("zongyeshu");
				pager = object.getInt("pager");
				if (array.length() != 0) {
					customerList.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						CustomerInfo customer = new CustomerInfo();
						customer.setId(obj.getString("id"));
						customer.setAddress(obj.getString("qu")
								+ obj.getString("jiedao") + obj.getString("sq"));
						customer.setKh_tel(obj.getString("kh_tel"));
						customer.setKh_name(obj.getString("kh_name"));
						customer.setKh_sex(obj.getString("kh_sex"));
						customer.setKh_cardId(obj.getString("kh_cardid"));
						customer.setJg(obj.getString("jgname"));
						if (obj.getString("moneys").equals("")) {
							customer.setMoneys("0");
						} else {
							customer.setMoneys(obj.getString("moneys"));
						}

						customerList.add(customer);

					}
					handler.sendEmptyMessage(HandlerException.Success);
				} else {
					handler.sendEmptyMessage(HandlerException.Fail);
				}

			}
		} catch (Exception e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	private StringBuffer loadJGUrl() {
		StringBuffer loadJGUrl = new StringBuffer(SettingUtils.get("serverIp"));
		loadJGUrl.append(SettingUtils.get("jigou_url"));
		return loadJGUrl;

	}

	// 加载机构
	private void loadJG() {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					loadJGUrl(), "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream2(inputStream);
				JSONObject object = new JSONObject(message.replace("\r\n", ""));
				JSONArray array = object.getJSONArray("contents");
				if (array.length() != 0) {
					jgList = new ArrayList<JG>();
					JG temp = new JG();
					temp.setId("");
					temp.setName("---请选择---");
					jgList.add(temp);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						JG jg = new JG();
						jg.setId(obj.getString("id"));

						jg.setName(obj.getString("name"));
						jgList.add(jg);
					}
					int count = jgList.size();
					ssjgArray = new String[count];
					ssjgIdArray = new String[count];

					for (int i = 0; i < count; i++) {

						ssjgArray[i] = jgList.get(i).getName();
						ssjgIdArray[i] = jgList.get(i).getId();

					}
				}
				handler.sendEmptyMessage(1000);
			}
		} catch (Exception e) {

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (requestCode == RequestAndResultCode.SELECT_SHENG) {
				String name = data.getExtras().getString("name");
				areaId = data.getExtras().getString("areaId");
				if (isSheng) {
					sheng.setText(name);
					shengId = areaId;
					isSheng = false;
				} else if (isShi) {
					shiId = areaId;
					shi.setText(name);
					isShi = false;

				} else if (isQu) {
					quId = areaId;
					qu.setText(name);
					isQu = false;

				} else if (isJiedao) {
					jiedaoId = areaId;
					jiedao.setText(name);
					isJiedao = false;

				} else if (isSq) {
					sqId = areaId;
					sq.setText(name);
					isSq = false;

				} else if (isZdy) {
					zdyId = areaId;
					zdy.setText(name);
					isZdy = false;

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// 按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CustomerInfoActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
