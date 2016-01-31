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
public class Cart implements Serializable{

	private int tId;
	private int cId;
	private String title;
	private String pic;
	private int tMoney;
	private int pMoney;
	private int count;
	private boolean isChecked;
	
	public int gettId() {
		return tId;
	}
	public void settId(int tId) {
		this.tId = tId;
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
	public int gettMoney() {
		return tMoney;
	}
	public void settMoney(int tMoney) {
		this.tMoney = tMoney;
	}
	public int getpMoney() {
		return pMoney;
	}
	public void setpMoney(int pMoney) {
		this.pMoney = pMoney;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public int getcId() {
		return cId;
	}
	public void setcId(int cId) {
		this.cId = cId;
	}
	
	
}
