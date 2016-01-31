package com.chuannuo.qianbaosuoping.model;

/**
 * @author alan.xie
 * @date 2014-10-14 下午4:58:20
 * @Description: TODO
 */
public class Exchange {

	public String account;     //账号
	public String exchangeDesc;//兑换了什么了
	public String exchangeTime;//兑换时间
	public String integral;     //兑换积分
	public String remark;		//审核未通过原因
	public int status;		//审核状态,0审核中，1审核通过，2打款中，3已打款，4完成，5审核不通过
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getExchangeDesc() {
		return exchangeDesc;
	}
	public void setExchangeDesc(String exchangeDesc) {
		this.exchangeDesc = exchangeDesc;
	}
	public String getExchangeTime() {
		return exchangeTime;
	}
	public void setExchangeTime(String exchangeTime) {
		this.exchangeTime = exchangeTime;
	}
	public String getIntegral() {
		return integral;
	}
	public void setIntegral(String integral) {
		this.integral = integral;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
