package com.callCenter.activity;

import com.callCenter.entity.LoginUser;
import com.callCenter.utils.CircularImage;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 个人信息activity
 * 
 * @author Administrator
 * 
 */
public class PersonalInfoActivity extends Activity {
	private TextView name, sex, company, department, job, address, phone, qq;
	private LoginUser loginUser;
	private CircularImage circulalImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.person_info);
		init();
		Event();
	}

	// 初始化
	private void init() {
		circulalImage = (CircularImage) this.findViewById(R.id.user_header);
		circulalImage.setImageResource(R.drawable.user_header);
		name = (TextView) this.findViewById(R.id.name);
		sex = (TextView) this.findViewById(R.id.sex);
		company = (TextView) this.findViewById(R.id.company);
		department = (TextView) this.findViewById(R.id.department);
		job = (TextView) this.findViewById(R.id.job);
		address = (TextView) this.findViewById(R.id.address);
		phone = (TextView) this.findViewById(R.id.phone);
		qq = (TextView) this.findViewById(R.id.qq);
		// 获取登陆用户信息
		loginUser = (LoginUser) getIntent().getSerializableExtra("loginUser");
		name.setText(loginUser.getUserName());
		sex.setText(loginUser.getSex());
		company.setText(loginUser.getGongsi());
		department.setText(loginUser.getBm());
		job.setText(loginUser.getZhiwei());
		address.setText(loginUser.getAddress());
		phone.setText(loginUser.getTel());
		qq.setText(loginUser.getQq());

	}

	// 事件监听
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						PersonalInfoActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);

					}
				});
	}

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			PersonalInfoActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}
}
