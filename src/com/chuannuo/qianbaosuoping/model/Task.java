package com.chuannuo.qianbaosuoping.model;

/**
 * @author alan.xie
 * @date 2014-10-14 下午4:58:20
 * @Description: 任务记录
 */
public class Task {

	public String TaskConten;//做了什么任务
	public String finishTime;//完成时间
	public String integral;  //奖励积分
	
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
