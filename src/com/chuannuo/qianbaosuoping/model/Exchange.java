package com.chuannuo.qianbaosuoping.model;

/**
 * @author alan.xie
 * @date 2014-10-14 ����4:58:20
 * @Description: TODO
 */
public class Exchange {

	public String account;     //�˺�
	public String exchangeDesc;//�һ���ʲô��
	public String exchangeTime;//�һ�ʱ��
	public String integral;     //�һ�����
	public String remark;		//���δͨ��ԭ��
	public int status;		//���״̬,0����У�1���ͨ����2����У�3�Ѵ�4��ɣ�5��˲�ͨ��
	
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
