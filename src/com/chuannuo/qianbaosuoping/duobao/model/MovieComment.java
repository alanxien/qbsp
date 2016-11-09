/* 
 * @Title:  Goods.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-14 下午8:34:35 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao.model;

import java.io.Serializable;
import java.util.List;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-14 下午8:34:35 
 * @version:  V1.0 
 */
public class MovieComment implements Serializable{

	private int mId;
	private int id;
	private int appId;
	private String comments;
	private String createDate;
	
	public int getmId() {
		return mId;
	}
	public int getId() {
		return id;
	}
	public int getAppId() {
		return appId;
	}
	public String getComments() {
		return comments;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setmId(int mId) {
		this.mId = mId;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setAppId(int appId) {
		this.appId = appId;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
}
