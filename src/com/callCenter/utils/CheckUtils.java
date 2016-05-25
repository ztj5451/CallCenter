package com.callCenter.utils;

import java.util.regex.Pattern;

public class CheckUtils {
	//邮件校验
	public static boolean isEmail(String paramString) {
		return Pattern
				.compile(
						"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$")
				.matcher(paramString).matches();
	}
	//数字校验
	public static boolean isNumeric(String paramString) {
		return Pattern.compile("[0-9]{6}").matcher(paramString).matches();
	}
	//电话号码校验
	public static boolean isPhoneNumber(String paramString) {
		return Pattern
				.compile(
						"((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))")
				.matcher(paramString).matches();

	}
	//金额校验
	public static boolean isMoney(String money) {

		return Pattern.compile("^(([0-9]\\d{0,9})|0)(\\.\\d{1,2})?$")
				.matcher(money).matches();
	}
	//身份证号码校验
	public static boolean isCardId(String cardId) {

		return Pattern
				.compile(
						"^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$")
				.matcher(cardId).matches();
	}
}
