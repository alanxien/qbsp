/* 
 * @Title:  Canyuzhe.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-18 下午10:09:07 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao.model;

import java.io.Serializable;
import java.util.List;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-18 下午10:09:07 
 * @version:  V1.0 
 */
public class Winner implements Serializable{

	private int appId;
	private String userName;
	private String ip;
	private String createDate;
	private int count;
	private int status;
	private int taskId;
	private String pic;
	private String title;
	private int tMoney;
	private int pMoney;
	private String winner;
	private String wNum;
	private String lotTime;
	private int wCount;
	private List<String> numList;
	public int getAppId() {
		return appId;
	}
	public void setAppId(int appId) {
		this.appId = appId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getWinner() {
		return winner;
	}
	public void setWinner(String winner) {
		this.winner = winner;
	}
	public String getwNum() {
		return wNum;
	}
	public void setwNum(String wNum) {
		this.wNum = wNum;
	}
	public String getLotTime() {
		return lotTime;
	}
	public void setLotTime(String lotTime) {
		this.lotTime = lotTime;
	}
	public int getwCount() {
		return wCount;
	}
	public void setwCount(int wCount) {
		this.wCount = wCount;
	}
	public List<String> getNumList() {
		return numList;
	}
	public void setNumList(List<String> numList) {
		this.numList = numList;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
}
