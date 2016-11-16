package com.chuannuo.qianbaosuoping.duobao.model;

import java.io.Serializable;

public class BaiduModel implements Serializable{
	private String title;
	private String link;
	private String password;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
