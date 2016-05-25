package com.callCenter.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.callCenter.entity.Customer;
import com.callCenter.entity.CustomerDetail;
import com.callCenter.entity.Type;
import com.callCenter.utils.HandlerException;
import com.callCenter.utils.HttpConnectPostUtil;
import com.callCenter.utils.HttpConnectUtil;
import com.callCenter.utils.PhoneUtils;
import com.callCenter.utils.RequestAndResultCode;
import com.callCenter.utils.SettingUtils;
import com.callCenter.utils.UrlEncode;
import com.tencent.mm.sdk.platformtools.PhoneUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CallerDetailActivity extends Activity {
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private double lat = 0.0, lng = 0.0;
	private Button editButton;
	private boolean isEdit = false;
	private EditText bsjj, name, sex, cardId, birthday, goutong, one_name,
			one_khgx, one_tel, one_phone, two_name, two_khgx, address, two_tel,
			two_phone, wtr_one, wtr_two, bz, sheng, shi, qu, jiedao, sq, jgou,
			zdy;
	private TextView callPhone;
	private CustomerDetail customer;
	private SettingUtils settingUtils;
	private Spinner gtSpinner, sexSpinner, category;
	private ArrayAdapter<String> gtAdapter, sexAdapter, categoryAdapter;
	private String[] gtArray, sexArray;
	private String areaId = "0";
	private boolean isSheng = false, isShi = false, isQu = false,
			isJiedao = false, isSq = false, isZdy = false, isJg = false;
	private String shengId = "", shiId = "", quId = "", jiedaoId = "",
			sqId = "", zdyId, jgId;
	private String userId, loginname, customId, customerTel;
	private String gtId, sexId;
	private List<Type> typeList;
	private String typeContent = "";
	private String typeInt = "";
	private Button historyBtn;
	private Map<String, String> params;
	private String[] spinner;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerException.Success:
				if (typeList != null) {
					spinner = new String[typeList.size()];
					for (int i = 0; i < typeList.size(); i++) {
						spinner[i] = typeList.get(i).getName();
					}
				}
				// spinner = new String[] { "请选择", "普通", "紧急", "非常紧急" };
				categoryAdapter = new ArrayAdapter<String>(
						CallerDetailActivity.this,
						android.R.layout.simple_spinner_item, spinner);
				categoryAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				category.setAdapter(categoryAdapter);
				category.setSelection(0, false);

				break;
			case HandlerException.HttpURLConnection_not_HTTP_OK:
				break;
			case HandlerException.IOException:
				break;
			case HandlerException.Fail:
				break;
			case HandlerException.AllSuccess:
				setValue();
				break;
			case HandlerException.CommitSuccess:
				Toast.makeText(CallerDetailActivity.this, "提交成功!",
						Toast.LENGTH_SHORT).show();
				CallerDetailActivity.this.finish();
				overridePendingTransition(R.anim.in_to_right,
						R.anim.out_from_left);
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
		setContentView(R.layout.caller_detail);
		init();
		findById();
		// setValue();
		// registerForContextMenu(callPhone);
		Event();
		new Thread() {
			public void run() {
				customDetailAction(customDetailUrl());
			};
		}.start();
		// customDetailUrl();

	}

	private void init() {
		settingUtils = new SettingUtils(CallerDetailActivity.this);
		// customer = (Customer) getIntent().getSerializableExtra("customer");
		// System.out.println("传递过来的对象:" + customer.getName());
		userId = getIntent().getExtras().getString("userId");
		loginname = getIntent().getExtras().getString("loginname");
		customId = getIntent().getExtras().getString("customId");
		historyBtn = (Button) this.findViewById(R.id.history);
		gtArray = getResources().getStringArray(R.array.goutong);
		sexArray = getResources().getStringArray(R.array.sex);
		gtSpinner = (Spinner) this.findViewById(R.id.gtSp);
		sexSpinner = (Spinner) this.findViewById(R.id.sexSp);
		category = (Spinner) this.findViewById(R.id.category);
		gtAdapter = new ArrayAdapter<String>(CallerDetailActivity.this,
				android.R.layout.simple_spinner_item, gtArray);
		gtAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		gtSpinner.setAdapter(gtAdapter);
		gtSpinner.setSelection(0, false);

		sexAdapter = new ArrayAdapter<String>(CallerDetailActivity.this,
				android.R.layout.simple_spinner_item, sexArray);
		sexAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sexSpinner.setAdapter(sexAdapter);
		sexSpinner.setSelection(0, false);

		// 从网络上获取选项
		new Thread() {
			public void run() {
				typeList = typeAction(typeUrl());
			};
		}.start();

	}

	private void Event() {
		// 处理事件类别

		category.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				String temp = spinner[position];
				String tempId = typeList.get(position).getId();
				typeInt = tempId;
				// Toast.makeText(CallerDetailActivity.this, temp + "," +
				// tempId,
				// Toast.LENGTH_LONG).show();
				typeContent = temp;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 性别选择
		sexSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				sexId = sexArray[position];

				if (sexId.equals("--请选择--")) {
					sex.setText("");
				} else {
					sex.setText(sexId);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 沟通选择
		gtSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				gtId = gtArray[position];

				if (gtId.equals("--请选择--")) {
					goutong.setText("");
				} else {
					goutong.setText(gtId);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 选择省
		this.findViewById(R.id.shengBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(CallerDetailActivity.this,
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
		// 选择市
		this.findViewById(R.id.shiBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (!areaId.equals("0")) {
							Intent intent = new Intent(
									CallerDetailActivity.this,
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

					}
				});
		// 选择区
		this.findViewById(R.id.quBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!areaId.equals("0")) {
					Intent intent = new Intent(CallerDetailActivity.this,
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

			}
		});
		// 街道
		this.findViewById(R.id.jiedaoBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (!areaId.equals("0")) {
							Intent intent = new Intent(
									CallerDetailActivity.this,
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
					}
				});
		// 社区
		this.findViewById(R.id.sqBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!areaId.equals("0")) {
					Intent intent = new Intent(CallerDetailActivity.this,
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

			}
		});
		// 自定义
		this.findViewById(R.id.zdyBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (!areaId.equals("0")) {
							Intent intent = new Intent(
									CallerDetailActivity.this,
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

					}
				});
		// 机构
		this.findViewById(R.id.jgBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!areaId.equals("0")) {
					Intent intent = new Intent(CallerDetailActivity.this,
							SelectAreaActivity.class);
					intent.putExtra("userId", userId);
					intent.putExtra("flag", areaId);
					intent.putExtra("areaName", "");
					intent.putExtra("url", "hj_jg.jsp");
					startActivityForResult(intent,
							RequestAndResultCode.SELECT_SHENG);
					isJg = true;
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				}

			}
		});
		// 取消
		this.findViewById(R.id.cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						CallerDetailActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
		// 提交到服务器
		this.findViewById(R.id.commit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// new Thread() {
						// public void run() {
						// // 保存
						// saveAction(saveUrl());
						// };
						// }.start();
						// 初始化提交参数
						// Toast.makeText(CallerDetailActivity.this, "提交到服务器",
						// Toast.LENGTH_SHORT).show();
						if (checkType()) {
							initParams();
							new Thread() {
								public void run() {
									commitAction(
											params,
											"utf-8",
											SettingUtils.get("serverIp")
													+ SettingUtils
															.get("zuoxi_commit_url"));
								};
							}.start();
						}

					}
				});
		// 历史
		this.findViewById(R.id.history).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(CallerDetailActivity.this,
								HistoryActivity.class);
						intent.putExtra("kh_tel", customer.getKh_tel());
						intent.putExtra("userId", userId);
						intent.putExtra("loginname", loginname);
						startActivity(intent);

						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);

					}
				});
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						CallerDetailActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});

		this.findViewById(R.id.toMap).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CallerDetailActivity.this,
						SelectMapActivity.class);
				intent.putExtra("tel", customerTel);
				startActivityForResult(intent, RequestAndResultCode.SELECT_MAP);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});

		// 编辑客户信息
		this.findViewById(R.id.editCustomer).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (!isEdit) {
							editButton.setText("完成");
							name.setEnabled(true);
							sex.setEnabled(true);
							cardId.setEnabled(true);
							address.setEnabled(true);
							bz.setEnabled(true);
							goutong.setEnabled(true);
							birthday.setEnabled(true);
							// callPhone.setEnabled(true);
							one_name.setEnabled(true);
							one_name.setBackgroundResource(R.drawable.mm_edit_focused);
							one_khgx.setEnabled(true);
							one_khgx.setBackgroundResource(R.drawable.mm_edit_focused);
							one_phone.setEnabled(true);
							one_phone
									.setBackgroundResource(R.drawable.mm_edit_focused);
							one_tel.setEnabled(true);
							one_tel.setBackgroundResource(R.drawable.mm_edit_focused);
							two_name.setEnabled(true);
							two_name.setBackgroundResource(R.drawable.mm_edit_focused);
							two_khgx.setEnabled(true);
							two_khgx.setBackgroundResource(R.drawable.mm_edit_focused);
							two_phone.setEnabled(true);
							two_phone
									.setBackgroundResource(R.drawable.mm_edit_focused);
							two_tel.setEnabled(true);
							two_tel.setBackgroundResource(R.drawable.mm_edit_focused);
							wtr_one.setEnabled(true);
							wtr_one.setBackgroundResource(R.drawable.mm_edit_focused);
							wtr_two.setEnabled(true);
							wtr_two.setBackgroundResource(R.drawable.mm_edit_focused);
							// callPhone
							// .setBackgroundResource(R.drawable.mm_edit_focused);
							birthday.setBackgroundResource(R.drawable.mm_edit_focused);
							name.setBackgroundResource(R.drawable.mm_edit_focused);
							sex.setBackgroundResource(R.drawable.mm_edit_focused);
							cardId.setBackgroundResource(R.drawable.mm_edit_focused);
							address.setBackgroundResource(R.drawable.mm_edit_focused);
							bz.setBackgroundResource(R.drawable.mm_edit_focused);

							goutong.setBackgroundResource(R.drawable.mm_edit_focused);
							isEdit = true;

						} else {
							editButton.setText("编辑");
							name.setEnabled(false);
							sex.setEnabled(false);
							cardId.setEnabled(false);
							address.setEnabled(false);
							bz.setEnabled(false);
							goutong.setEnabled(false);
							name.setBackgroundResource(R.drawable.back);
							sex.setBackgroundResource(R.drawable.back);
							cardId.setBackgroundResource(R.drawable.back);
							address.setBackgroundResource(R.drawable.back);
							bz.setBackgroundResource(R.drawable.back);
							goutong.setBackgroundResource(R.drawable.back);
							birthday.setEnabled(true);
							// callPhone.setEnabled(true);
							one_name.setEnabled(true);
							one_name.setBackgroundResource(R.drawable.back);
							one_khgx.setEnabled(true);
							one_khgx.setBackgroundResource(R.drawable.back);
							one_phone.setEnabled(true);
							one_phone.setBackgroundResource(R.drawable.back);
							one_tel.setEnabled(true);
							one_tel.setBackgroundResource(R.drawable.back);
							two_name.setEnabled(true);
							two_name.setBackgroundResource(R.drawable.back);
							two_khgx.setEnabled(true);
							two_khgx.setBackgroundResource(R.drawable.back);
							two_phone.setEnabled(true);
							two_phone.setBackgroundResource(R.drawable.back);
							two_tel.setEnabled(true);
							two_tel.setBackgroundResource(R.drawable.back);
							wtr_one.setEnabled(true);
							wtr_one.setBackgroundResource(R.drawable.back);
							wtr_two.setEnabled(true);
							wtr_two.setBackgroundResource(R.drawable.back);
							// callPhone.setBackgroundResource(R.drawable.back);
							birthday.setBackgroundResource(R.drawable.back);
							isEdit = false;

						}

					}
				});
		// 拨打电话
		this.findViewById(R.id.action).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						PhoneUtils.Tel(CallerDetailActivity.this, callPhone
								.getText().toString().trim());

					}
				});
		// 复制号码
		this.findViewById(R.id.copy).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				copy(callPhone.getText().toString().trim(),
						CallerDetailActivity.this);
				Toast.makeText(CallerDetailActivity.this, "已复制!",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	// 复制内容到剪贴板
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	private boolean checkType() {

		if (typeInt.equals("1")) {
			Toast.makeText(CallerDetailActivity.this, "请选择服务项目!",
					Toast.LENGTH_SHORT).show();
			return false;

		}
		return true;
	}

	// 找到组件
	private void findById() {
		// 办事简介
		bsjj = (EditText) this.findViewById(R.id.bsjj);
		editButton = (Button) this.findViewById(R.id.editCustomer);
		name = (EditText) this.findViewById(R.id.kh_name);
		sex = (EditText) this.findViewById(R.id.sex);
		cardId = (EditText) this.findViewById(R.id.cardId);
		address = (EditText) this.findViewById(R.id.address);
		birthday = (EditText) this.findViewById(R.id.birthday);
		callPhone = (TextView) this.findViewById(R.id.callPhone);
		goutong = (EditText) this.findViewById(R.id.goutong);
		one_name = (EditText) this.findViewById(R.id.one_name);
		one_khgx = (EditText) this.findViewById(R.id.one_khgx);
		one_tel = (EditText) this.findViewById(R.id.one_tel);
		one_phone = (EditText) this.findViewById(R.id.one_phone);
		two_name = (EditText) this.findViewById(R.id.two_name);
		two_khgx = (EditText) this.findViewById(R.id.two_khgx);
		two_tel = (EditText) this.findViewById(R.id.two_tel);
		two_phone = (EditText) this.findViewById(R.id.two_phone);
		wtr_one = (EditText) this.findViewById(R.id.wtr_one);
		wtr_two = (EditText) this.findViewById(R.id.wtr_two);
		bz = (EditText) this.findViewById(R.id.bz);
		sheng = (EditText) this.findViewById(R.id.sheng);
		shi = (EditText) this.findViewById(R.id.shi);
		qu = (EditText) this.findViewById(R.id.qu);
		jiedao = (EditText) this.findViewById(R.id.jiedao);
		sq = (EditText) this.findViewById(R.id.sq);
		jgou = (EditText) this.findViewById(R.id.jgou);
		zdy = (EditText) this.findViewById(R.id.zdy);

	}

	// 赋予值
	private void setValue() {
		if (customer.getQubie().equals("1")) {
			historyBtn.setVisibility(View.VISIBLE);
		}

		sheng.setText(customer.getProvinceindo());
		shi.setText(customer.getCityindo());
		qu.setText(customer.getQuindo());
		jiedao.setText(customer.getJiedaoindo());
		sq.setText(customer.getSqindo());
		zdy.setText(customer.getZdyindo());
		jgou.setText(customer.getJgindo());
		name.setText(customer.getKh_name());
		sex.setText(customer.getKh_sex());
		cardId.setText(customer.getKh_cardid());
		birthday.setText(customer.getKh_sr());
		callPhone.setText(customer.getKh_tel());
		goutong.setText(customer.getKh_goutong());
		one_name.setText(customer.getOne_name());
		one_khgx.setText(customer.getOne_khgx());
		one_tel.setText(customer.getOne_tel());
		one_phone.setText(customer.getOne_phone());
		two_name.setText(customer.getTwo_name());
		two_khgx.setText(customer.getTwo_khgx());
		address.setText(customer.getKh_address());
		two_tel.setText(customer.getTwo_tel());
		two_phone.setText(customer.getTwo_phone());
		wtr_one.setText(customer.getWtr_one());
		wtr_two.setText(customer.getWtr_two());
		bz.setText(customer.getBz());

	}

	private void initParams() {
		try {
			params = new HashMap<String, String>();
			params.put("jibie", typeInt);
			params.put("uid", userId);
			params.put("bsjj",
					UrlEncode.Encode(bsjj.getText().toString().trim()));
			params.put("kh_times", customer.getKh_times());
			params.put("kh_py", customer.getKh_py());
			params.put("kh_pying", customer.getKh_pying());
			params.put("kh_id", customer.getKh_id());
			params.put("kh_name",
					UrlEncode.Encode(name.getText().toString().trim()));
			params.put("jg", customer.getJg());
			params.put("kh_qq", customer.getKh_qq());
			params.put("kh_sex",
					UrlEncode.Encode(sex.getText().toString().trim()));
			params.put("kh_cardid", cardId.getText().toString().trim());
			params.put("kh_sg", customer.getKh_sg());
			params.put("kh_tz", customer.getKh_tz());
			params.put("kh_xl", customer.getKh_xl());
			params.put("kh_yb", customer.getKh_yb());
			params.put("kh_sr", birthday.getText().toString().trim());
			params.put("kh_goutong",
					UrlEncode.Encode(goutong.getText().toString().trim()));
			params.put("kh_tel", callPhone.getText().toString().trim());
			params.put("email", customer.getEmail());
			params.put("kh_address",
					UrlEncode.Encode(address.getText().toString().trim()));
			params.put("sheng", customer.getSheng());
			params.put("shi", customer.getShi());
			params.put("qu", customer.getQu());
			params.put("jiedao", customer.getJiedao());
			params.put("sq", customer.getSq());
			params.put("zdy", customer.getZdy());
			params.put("jg", customer.getJg());
			params.put("kh_fkfs", customer.getKh_fkfs());
			params.put("kh_dwname", UrlEncode.Encode(customer.getKh_dwname()));
			params.put("kh_age", customer.getKh_age());
			params.put("lb_st", customer.getLb_st());
			params.put("lb_tl", customer.getLb_tl());
			params.put("lb_yy", customer.getLb_yy());
			params.put("lb_zt", customer.getLb_zt());
			params.put("lb_zl", customer.getLb_zl());
			params.put("lb_js", customer.getLb_js());
			params.put("lb_dc", customer.getLb_dc());
			params.put("lb_qt", customer.getLb_qt());
			params.put("cj_dj", UrlEncode.Encode(customer.getCj_dj()));
			params.put("cj_num", customer.getCj_num());
			params.put("one_dxtz", customer.getOne_dxtz());
			params.put("one_name", UrlEncode.Encode(customer.getOne_name()));
			params.put("one_khgx", UrlEncode.Encode(customer.getOne_khgx()));
			params.put("one_tel", customer.getOne_tel());
			params.put("one_phone", customer.getOne_phone());
			params.put("one_flag_tz",
					UrlEncode.Encode(customer.getOne_flag_tz()));
			params.put("two_dxtz", UrlEncode.Encode(customer.getTwo_dxtz()));
			params.put("two_name", UrlEncode.Encode(customer.getTwo_name()));
			params.put("two_khgx", UrlEncode.Encode(customer.getTwo_khgx()));
			params.put("two_tel", customer.getTwo_tel());
			params.put("two_phone", customer.getTwo_phone());
			params.put("two_flag_tz", customer.getTwo_flag_tz());
			params.put("three_dxtz", UrlEncode.Encode(customer.getThree_dxtz()));
			params.put("three_name", UrlEncode.Encode(customer.getThree_name()));
			params.put("three_khgx", UrlEncode.Encode(customer.getThree_khgx()));
			params.put("three_tel", customer.getThree_tel());
			params.put("three_phone", customer.getThree_phone());
			params.put("three_flag_tz", customer.getThree_flag_tz());
			params.put("fore_dxtz", UrlEncode.Encode(customer.getFore_dxtz()));
			params.put("fore_name", UrlEncode.Encode(customer.getFore_name()));
			params.put("fore_khgx", UrlEncode.Encode(customer.getFore_khgx()));
			params.put("fore_tel", customer.getFore_tel());
			params.put("fore_phone", customer.getFore_phone());
			params.put("fore_flag_tz", customer.getFore_flag_tz());
			params.put("wtr_one", wtr_one.getText().toString().trim());
			params.put("wtr_two", wtr_two.getText().toString().trim());
			params.put("wtr_three", customer.getWtr_three());
			params.put("wtr_fore", customer.getWtr_fore());
			params.put("kh_sfylbx", UrlEncode.Encode(customer.getSfylbx()));
			params.put("kh_blag", customer.getKh_blag());
			params.put("yb_num", customer.getYb_num());
			params.put("ywgms", customer.getYwgms());
			params.put("jjzk", UrlEncode.Encode(customer.getJjzk()));
			params.put("kh_dw", UrlEncode.Encode(customer.getKh_dw()));
			params.put("kh_zy", UrlEncode.Encode(customer.getKh_zy()));
			params.put("hlxq", UrlEncode.Encode(customer.getHlxq()));
			params.put("zotj", customer.getZotj());
			params.put("zyjn", UrlEncode.Encode(customer.getZyjn()));
			params.put("qzyx", customer.getQzyx());
			params.put("rcfwxq", customer.getRcfwxq());
			params.put("bz", UrlEncode.Encode(bz.getText().toString().trim()));
			params.put("sfdb", UrlEncode.Encode(customer.getSfdb()));
			params.put("qubie", customer.getQubie());
			params.put("black_idd", customer.getBlack_idd());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void commitAction(Map<String, String> params, String encode,
			String connectUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectPostUtil
					.submitPostData(params, encode, connectUrl);
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
				System.out.println("服务器响应码:"
						+ httpURLConnection.getResponseCode());
			} else {
				InputStream inptStream = httpURLConnection.getInputStream();
				// results = HttpConnectUtil.dealResponseResult(inptStream);
				System.out.println("提交到服务器上去了!");
				handler.sendEmptyMessage(HandlerException.CommitSuccess);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 保存url
	private StringBuffer saveUrl() {
		StringBuffer saveUrl = new StringBuffer(SettingUtils.get("serverIp"));
		saveUrl.append(SettingUtils.get("zuoxi_commit_url"));
		try {
			saveUrl.append("kh_times=").append(customer.getKh_times())
					.append("&kh_py=").append(customer.getKh_py())
					.append("&kh_pying=").append(customer.getKh_pying())
					.append("&kh_id=").append(customer.getKh_id())
					.append("&kh_name=")
					.append(UrlEncode.Encode2(customer.getKh_name()))
					.append("&kh_qq=").append(customer.getKh_qq())
					.append("&kh_sex=")
					.append(UrlEncode.Encode2(customer.getKh_sex()))
					.append("&kh_cardid=").append(customer.getKh_cardid())
					.append("&kh_sg=").append(customer.getKh_sg())
					.append("&kh_tz=").append(customer.getKh_tz())
					.append("&kh_xl=").append(customer.getKh_xl())
					.append("&kh_yb=").append(customer.getKh_yb())
					.append("&kh_sr=").append(customer.getKh_sr())
					.append("&kh_goutong=")
					.append(UrlEncode.Encode2(customer.getKh_goutong()))
					.append("&kh_tel=").append(customer.getKh_tel())
					.append("&email=").append(customer.getEmail())
					.append("&kh_address=")
					.append(UrlEncode.Encode2(customer.getKh_address()))
					.append("&sheng=").append(customer.getSheng())
					.append("&shi=").append(customer.getShi()).append("&qu=")
					.append(customer.getQu()).append("&jiedao=")
					.append(customer.getJiedao()).append("&sq=")
					.append(customer.getSq()).append("&zdy=")
					.append(customer.getZdy()).append("&jg=")
					.append(customer.getJg()).append("&kh_fkfs=")
					.append(customer.getKh_fkfs()).append("&kh_dwname=")
					.append(customer.getKh_dwname()).append("&kh_age=")
					.append(customer.getKh_age()).append("&lb_st=")
					.append(customer.getLb_st()).append("&lb_tl=")
					.append(customer.getLb_tl()).append("&lb_yy=")
					.append(customer.getLb_yy()).append("&lb_zt=")
					.append(customer.getLb_zt()).append("&lb_zl=")
					.append(customer.getLb_zl()).append("&lb_js=")
					.append(customer.getLb_js()).append("&lb_dc=")
					.append(customer.getLb_dc()).append("&lb_qt=")
					.append(customer.getLb_qt()).append("&cj_dj=")
					.append(customer.getCj_dj()).append("&cj_num=")
					.append(customer.getCj_num()).append("&one_dxtz=")
					.append(customer.getOne_dxtz()).append("&one_name=")
					.append(customer.getOne_name()).append("&one_khgx=")
					.append(customer.getOne_khgx());

		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("提交的url" + saveUrl);
		return saveUrl;

	}

	// 保存提交的数据
	private void saveAction(StringBuffer saveUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					saveUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				System.out.println("返回的信息 :" + message);
				if (message.equals("success")) {
					handler.sendEmptyMessage(HandlerException.CommitSuccess);
				} else if (message.equals("error")) {
					handler.sendEmptyMessage(HandlerException.Fail);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
		}
	}

	private StringBuffer customDetailUrl() {
		StringBuffer customDetailUrl = new StringBuffer(
				SettingUtils.get("serverIp"));
		// StringBuffer customDetailUrl = new StringBuffer(
		// "http://192.168.1.111:8080");
		customDetailUrl.append(SettingUtils.get("zuoxi_detail_url"))
				.append("id=").append(customId).append("&kyd=1");
		System.out.println("客户详细信息url:" + customDetailUrl);
		return customDetailUrl;

	}

	// 获取类别url
	private StringBuffer typeUrl() {
		// StringBuffer typeUrl = new
		// StringBuffer(SettingUtils.get("serverIp"));
		StringBuffer typeUrl = new StringBuffer(SettingUtils.get("serverIp"));
		typeUrl.append(SettingUtils.get("type_url"));
		System.out.println("获取类别URL：" + typeUrl);
		return typeUrl;
	}

	private List<Type> typeAction(StringBuffer typeUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					typeUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream);
				JSONObject object = new JSONObject(message);
				JSONArray array = new JSONArray(object.getString("indo"));
				if (array.length() != 0) {
					typeList = new ArrayList<Type>();
					Type type1 = new Type();
					type1.setId("1");
					type1.setName("--请选择--");
					typeList.add(type1);
					for (int i = 0; i < array.length(); i++) {

						JSONObject obj = array.getJSONObject(i);
						if (!obj.getString("id").equals("0")) {
							Type type = new Type();
							type.setId(obj.getString("id"));
							type.setName(obj.getString("name"));
							typeList.add(type);
						}

					}
					handler.sendEmptyMessage(HandlerException.Success);
					return typeList;
				} else {
					handler.sendEmptyMessage(HandlerException.Fail);
					return null;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);

		}
		return null;
	}

	private void customDetailAction(StringBuffer customDetailUrl) {
		try {
			HttpURLConnection httpURLConnection = HttpConnectUtil.connect(
					customDetailUrl, "GET");
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				handler.sendEmptyMessage(HandlerException.HttpURLConnection_not_HTTP_OK);
			} else {
				InputStream inputStream = httpURLConnection.getInputStream();
				String message = HttpConnectUtil.getHttpStream(inputStream)
						.replace("null", "");
				System.out.println("message：" + message);
				JSONObject obj = new JSONObject(message);
				customer = new CustomerDetail();
				customer.setKh_times(obj.getString("kh_times"));
				customer.setKh_py(obj.getString("kh_py"));
				customer.setKh_pying(obj.getString("kh_pying"));
				customer.setKh_id(obj.getString("kh_id"));
				customer.setKh_name(obj.getString("kh_name"));
				customer.setJgindo(obj.getString("jgindo"));
				customer.setKh_qq(obj.getString("kh_qq"));
				customer.setKh_sex(obj.getString("kh_sex"));
				customer.setKh_cardid(obj.getString("kh_cardid"));
				customer.setKh_sg(obj.getString("kh_sg"));
				customer.setKh_tz(obj.getString("kh_tz"));
				customer.setKh_xl(obj.getString("kh_xl"));
				customer.setKh_yb(obj.getString("kh_yb"));
				customer.setKh_sr(obj.getString("kh_sr"));
				customer.setKh_goutong(obj.getString("kh_goutong"));
				customer.setKh_tel(obj.getString("kh_tel"));
				customerTel = obj.getString("kh_tel");
				customer.setEmail(obj.getString("email"));
				customer.setKh_address(obj.getString("kh_address"));
				customer.setSheng(obj.getString("sheng"));
				customer.setShi(obj.getString("shi"));
				customer.setQu(obj.getString("qu"));
				customer.setJiedao(obj.getString("jiedao"));
				customer.setSq(obj.getString("sq"));
				customer.setZdy(obj.getString("zdy"));
				customer.setJg(obj.getString("jg"));
				customer.setKh_fkfs(obj.getString("kh_fkfs"));
				customer.setKh_dwname(obj.getString("kh_dwname"));
				customer.setKh_age(obj.getString("kh_age"));
				customer.setLb_st(obj.getString("lb_st"));
				customer.setLb_tl(obj.getString("lb_tl"));
				customer.setLb_yy(obj.getString("lb_yy"));
				customer.setLb_zt(obj.getString("lb_zt"));
				customer.setLb_zl(obj.getString("lb_zl"));
				customer.setLb_js(obj.getString("lb_js"));
				customer.setLb_dc(obj.getString("lb_dc"));
				customer.setLb_qt(obj.getString("lb_qt"));
				customer.setCj_dj(obj.getString("cj_dj"));
				customer.setCj_num(obj.getString("cj_num"));
				customer.setOne_dxtz(obj.getString("one_dxtz"));
				customer.setOne_khgx(obj.getString("one_khgx"));
				customer.setOne_tel(obj.getString("one_tel"));
				customer.setOne_phone(obj.getString("one_phone"));
				customer.setOne_name(obj.getString("one_name"));
				customer.setOne_flag_tz(obj.getString("one_flag_tz"));
				customer.setTwo_dxtz(obj.getString("two_dxtz"));
				customer.setTwo_khgx(obj.getString("two_khgx"));
				customer.setTwo_tel(obj.getString("two_tel"));
				customer.setTwo_phone(obj.getString("two_phone"));
				customer.setTwo_name(obj.getString("two_name"));
				customer.setTwo_flag_tz(obj.getString("two_name"));
				customer.setThree_dxtz(obj.getString("three_dxtz"));
				customer.setThree_khgx(obj.getString("three_khgx"));
				customer.setThree_tel(obj.getString("three_tel"));
				customer.setThree_phone(obj.getString("three_phone"));
				customer.setThree_name(obj.getString("three_name"));
				customer.setThree_flag_tz(obj.getString("three_flag_tz"));
				customer.setFore_dxtz(obj.getString("fore_dxtz"));
				customer.setFore_khgx(obj.getString("fore_khgx"));
				customer.setFore_tel(obj.getString("fore_tel"));
				customer.setFore_phone(obj.getString("fore_phone"));
				customer.setFore_name(obj.getString("fore_name"));
				customer.setFore_flag_tz(obj.getString("fore_flag_tz"));
				customer.setWtr_one(obj.getString("wtr_one"));
				customer.setWtr_two(obj.getString("wtr_two"));
				customer.setWtr_three(obj.getString("wtr_three"));
				customer.setWtr_fore(obj.getString("wtr_fore"));
				customer.setSfylbx(obj.getString("sfylbx"));
				customer.setKh_blag(obj.getString("kh_blag"));
				customer.setYb_num(obj.getString("yb_num"));
				customer.setYwgms(obj.getString("ywgms"));
				customer.setJjzk(obj.getString("jjzk"));
				customer.setKh_dw(obj.getString("kh_dw"));
				customer.setKh_zy(obj.getString("kh_zy"));
				customer.setHlxq(obj.getString("hlxq"));
				customer.setZotj(obj.getString("zotj"));
				customer.setZyjn(obj.getString("zyjn"));
				customer.setProvinceindo(obj.getString("provinceindo"));
				customer.setCityindo(obj.getString("cityindo"));
				customer.setQuindo(obj.getString("quindo"));
				customer.setJiedaoindo(obj.getString("jiedaoindo"));
				customer.setSqindo(obj.getString("sqindo"));
				customer.setZdyindo(obj.getString("zdyindo"));
				customer.setQzyx(obj.getString("qzyx"));
				customer.setRcfwxq(obj.getString("rcfwxq"));
				customer.setBz(obj.getString("bz"));
				customer.setSfdb(obj.getString("sfdb"));
				customer.setQubie(obj.getString("qubie"));
				customer.setBlack_idd(obj.getString("black_idd"));
				handler.sendEmptyMessage(HandlerException.AllSuccess);
			}
		} catch (Exception e) {

			e.printStackTrace();
			handler.sendEmptyMessage(HandlerException.IOException);
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
					customer.setSheng(shengId);
					isSheng = false;
				} else if (isShi) {
					shiId = areaId;
					shi.setText(name);
					isShi = false;
					customer.setShi(shiId);
				} else if (isQu) {
					quId = areaId;
					qu.setText(name);
					isQu = false;
					customer.setQu(quId);
				} else if (isJiedao) {
					jiedaoId = areaId;
					jiedao.setText(name);
					isJiedao = false;
					customer.setJiedao(jiedaoId);
				} else if (isSq) {
					sqId = areaId;
					sq.setText(name);
					isSq = false;
					customer.setSq(sqId);
				} else if (isZdy) {
					zdyId = areaId;
					zdy.setText(name);
					isZdy = false;
					customer.setZdy(zdyId);

				} else if (isJg) {
					jgId = areaId;
					jgou.setText(name);
					isJg = false;
					customer.setJg(jgId);

				}

			} else if (requestCode == RequestAndResultCode.SELECT_MAP) {
				lat = data.getExtras().getDouble("lat");
				lng = data.getExtras().getDouble("lng");
				// address.setText(lat + "," + lng);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// 上下文菜单，本例会通过长按条目激活上下文菜单
	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View view,
	// ContextMenuInfo menuInfo) {
	// menu.setHeaderTitle("相关操作");
	// // 添加菜单项
	// menu.add(0, ITEM1, 0, "拨打电话");
	// menu.add(0, ITEM2, 0, "复制号码");
	// }
	//
	// // 菜单单击响应
	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	//
	// switch (item.getItemId()) {
	// case ITEM1:
	// // 在这里添加处理代码
	// PhoneUtils.Tel(CallerDetailActivity.this, callPhone.getText()
	// .toString().trim());
	// break;
	// case ITEM2:
	// // 在这里添加处理代码
	// copy(callPhone.getText().toString().trim(),
	// CallerDetailActivity.this);
	//
	// break;
	// }
	// return true;
	// }

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CallerDetailActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
