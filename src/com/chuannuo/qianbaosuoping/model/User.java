package com.chuannuo.qianbaosuoping.model;

import java.math.BigInteger;

public class User {

	public BigInteger id;        //ID号
	public String mobile;  		 //手机号
	public String password;		 //密码
	public int integral;         //剩余积分 
	public int integral_count;   //总积分
	public int exchanged;        //已兑换积分
	public String qq_code;  	 //qq
	public String alipay_code;	 //支付宝
	public int exchange_count;   //兑换次数
	public int task_count;       //任务完成次数
	public int invitation;       //邀请次数
	public int level;            //等级
	public int sign_in;          //签到
	public String verification;  //校验码
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getIntegral() {
		return integral;
	}
	public void setIntegral(int integral) {
		this.integral = integral;
	}
	public int getIntegral_count() {
		return integral_count;
	}
	public void setIntegral_count(int integral_count) {
		this.integral_count = integral_count;
	}
	public int getExchanged() {
		return exchanged;
	}
	public void setExchanged(int exchanged) {
		this.exchanged = exchanged;
	}
	public String getQq_code() {
		return qq_code;
	}
	public void setQq_code(String qq_code) {
		this.qq_code = qq_code;
	}
	public String getAlipay_code() {
		return alipay_code;
	}
	public void setAlipay_code(String alipay_code) {
		this.alipay_code = alipay_code;
	}
	public int getExchange_count() {
		return exchange_count;
	}
	public void setExchange_count(int exchange_count) {
		this.exchange_count = exchange_count;
	}
	public int getTask_count() {
		return task_count;
	}
	public void setTask_count(int task_count) {
		this.task_count = task_count;
	}
	public int getInvitation() {
		return invitation;
	}
	public void setInvitation(int invitation) {
		this.invitation = invitation;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getSign_in() {
		return sign_in;
	}
	public void setSign_in(int sign_in) {
		this.sign_in = sign_in;
	}
	public String getVerification() {
		return verification;
	}
	public void setVerification(String verification) {
		this.verification = verification;
	}
	
	
	
}
