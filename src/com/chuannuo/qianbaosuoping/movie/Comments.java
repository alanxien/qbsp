/* 
 * @Title:  Comments.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<���������ļ�����ʲô��> 
 * @author:  xie.xin
 * @data:  2016-11-28 ����9:33:27 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.movie;

/** 
 * TODO<������������Ǹ�ʲô��> 
 * @author  xie.xin 
 * @data:  2016-11-28 ����9:33:27 
 * @version:  V1.0 
 */
public class Comments {

	private int id;
	private String appId;
	private String comment;
	private String createDate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
}
