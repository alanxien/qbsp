package com.chuannuo.qianbaosuoping.model;

import java.io.Serializable;

/**
 * @author alan.xie
 * @date 2015-1-27 上午11:29:49
 * @Description: 分享任务 app
 */
@SuppressWarnings("serial")
public class ShareApp implements Serializable{

	public int id;
	public String title;
	public String desc;
	public String icon;
	public String promoteUrl;		//app下载url
	public int share_count;			//该app已经分享多少次，
	public int limit_share_count;	//该APP可分享次数
	public int share_integral;		//分享积分
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getPromoteUrl() {
		return promoteUrl;
	}
	public void setPromoteUrl(String promoteUrl) {
		this.promoteUrl = promoteUrl;
	}
	public int getShare_count() {
		return share_count;
	}
	public void setShare_count(int share_count) {
		this.share_count = share_count;
	}
	public int getLimit_share_count() {
		return limit_share_count;
	}
	public void setLimit_share_count(int limit_share_count) {
		this.limit_share_count = limit_share_count;
	}
	public int getShare_integral() {
		return share_integral;
	}
	public void setShare_integral(int share_integral) {
		this.share_integral = share_integral;
	}
	
	
}
