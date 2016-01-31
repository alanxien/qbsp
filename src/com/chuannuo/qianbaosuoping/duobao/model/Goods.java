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
public class Goods implements Serializable{

	private int id;
	private String title;
	private String pic;
	private int totalMoney;
	private int payMoney;
	private String createData;
	private int tId;
	private int inventory;
	private List<String> picList;
	
	public int getInventory() {
		return inventory;
	}
	public void setInventory(int inventory) {
		this.inventory = inventory;
	}
	public int gettId() {
		return tId;
	}
	public void settId(int tId) {
		this.tId = tId;
	}
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
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public int getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(int totalMoney) {
		this.totalMoney = totalMoney;
	}
	public int getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(int payMoney) {
		this.payMoney = payMoney;
	}
	public String getCreateData() {
		return createData;
	}
	public void setCreateData(String createData) {
		this.createData = createData;
	}
	public List<String> getPicList() {
		return picList;
	}
	public void setPicList(List<String> picList) {
		this.picList = picList;
	}
	
}
