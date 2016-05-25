package com.callCenter.activity;
import com.callCenter.utils.PhoneUtils;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
/**
 * 关于Activity
 * @author Administrator
 *
 */
public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		Event();
		
	}

	// 按钮点击事件
	private void Event() {
		// 返回按钮
		this.findViewById(R.id.backBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AboutActivity.this.finish();
						overridePendingTransition(R.anim.in_to_right,
								R.anim.out_from_left);
					}
				});
		// 拨打电话
		this.findViewById(R.id.call).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				PhoneUtils.Tel(AboutActivity.this, "0431-00000000");

			}
		});
		// 发送邮件
		this.findViewById(R.id.sendEmail).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						PhoneUtils
								.SendEmail(AboutActivity.this,
										"ztj_5451@163.com", "软件使用反馈",
										"我在使用此软件时发下如下问题：");

					}
				});

	}
	//按下返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AboutActivity.this.finish();
			overridePendingTransition(R.anim.in_to_right, R.anim.out_from_left);
		}
		return super.onKeyDown(keyCode, event);
	}

}
