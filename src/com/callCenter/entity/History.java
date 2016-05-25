package com.callCenter.entity;

/**
 * 历史记录
 * 
 * @author Administrator
 * 
 */
public class History {
	private String id;
	private String kh_name;
	private String kh_tel;
	private String contents;
	private String times;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKh_name() {
		return kh_name;
	}

	public void setKh_name(String kh_name) {
		this.kh_name = kh_name;
	}

	public String getKh_tel() {
		return kh_tel;
	}

	public void setKh_tel(String kh_tel) {
		this.kh_tel = kh_tel;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

}
