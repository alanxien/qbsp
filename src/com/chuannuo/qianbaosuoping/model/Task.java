package com.chuannuo.qianbaosuoping.model;

/**
 * @author alan.xie
 * @date 2014-10-14 ����4:58:20
 * @Description: �����¼
 */
public class Task {

	public String TaskConten;//����ʲô����
	public String finishTime;//���ʱ��
	public String integral;  //��������
	
	public String getTaskConten() {
		return TaskConten;
	}
	public void setTaskConten(String taskConten) {
		TaskConten = taskConten;
	}
	public String getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	public String getIntegral() {
		return integral;
	}
	public void setIntegral(String integral) {
		this.integral = integral;
	}
	
	
}
