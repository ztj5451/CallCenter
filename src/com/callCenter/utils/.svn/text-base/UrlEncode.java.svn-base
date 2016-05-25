package com.callCenter.utils;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

public class UrlEncode {

	public static String Encode(String url) throws UnsupportedEncodingException {

		String encodeUrl = URLEncoder.encode(url, "UTF-8").toString();
		return encodeUrl;
	}

	public static String Encode2(String url)
			throws UnsupportedEncodingException {

		String encodeUrl = URLEncoder.encode(url, "UTF-8").toString();
		return URLEncoder.encode(encodeUrl, "UTF-8").toString();
	}

}
