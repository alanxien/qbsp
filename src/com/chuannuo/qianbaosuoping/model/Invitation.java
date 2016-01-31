package com.chuannuo.qianbaosuoping.model;

/**
 * @author alan.xie
 * @date 2014-10-14 下午4:58:20
 * @Description: 邀请记录
 */
public class Invitation {

	public String invId;     //被邀请人ID
	public String invTime;   //邀请时间
	public String integral;  //获得积分
	
	public String getInvId() {
		return invId;
	}
	public void setInvId(String invId) {
		this.invId = invId;
	}
	public String getInvTime() {
		return invTime;
	}
	public void setInvTime(String invTime) {
		this.invTime = invTime;
	}
	public String getIntegral() {
		return integral;
	}
	public void setIntegral(String integral) {
		this.integral = integral;
	}
	
	
}
