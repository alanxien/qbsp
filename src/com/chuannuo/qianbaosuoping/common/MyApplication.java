package com.chuannuo.qianbaosuoping.common;

import java.math.BigInteger;

import org.json.JSONArray;

import android.app.Application;
import android.view.WindowManager;

/**
 * @author alan.xie
 * @date 2014-10-22 上午9:39:33
 * @Description: TODO
 */
public class MyApplication extends Application {

	private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();
	private int download;
	private BigInteger appId;
	private int resourceId;
	private String code;
	private String phone;
	private String password;
	private int flag; //1推荐任务界面,2分享任务界面
	private int type; //0推荐任务，1推荐任务，2分享任务，3更多任务
	private boolean isSign;
	private JSONArray jArry;
	private String target; //id_type,type=1为qq分享，type=2为微信分享，type=3分享完成
	private boolean isWxShare;
	private int share_count;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setAppId(null);
		setCode(null);
		setPhone(null);
		setPassword(null);
		setDownload(0);
		setFlag(0);
		setType(0);
		setResourceId(0);
		setTarget("0_0");
		setWxShare(false);
		setShare_count(0);
		setSign(false);
	}
	
	public WindowManager.LayoutParams getWindowParams(){  
		return wmParams;  
	}
	
	public BigInteger getAppId() {
		return appId;
	}
	
	public void setAppId(BigInteger appId) {
		this.appId = appId;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDownload() {
		return download;
	}

	public void setDownload(int download) {
		this.download = download;
	}

	public JSONArray getjArry() {
		return jArry;
	}

	public void setjArry(JSONArray jArry) {
		this.jArry = jArry;
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public boolean isWxShare() {
		return isWxShare;
	}

	public void setWxShare(boolean isWxShare) {
		this.isWxShare = isWxShare;
	}

	public int getShare_count() {
		return share_count;
	}

	public void setShare_count(int share_count) {
		this.share_count = share_count;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public boolean isSign() {
		return isSign;
	}

	public void setSign(boolean isSign) {
		this.isSign = isSign;
	}
	
}
