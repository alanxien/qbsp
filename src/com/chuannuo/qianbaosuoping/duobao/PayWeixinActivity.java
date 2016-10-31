/* 
 * @Title:  OldTaskActivity.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-19 下午9:12:08 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.duobao.adapter.CanyuzheAdapter;
import com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter;
import com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter.CartClickListener;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.chuannuo.qianbaosuoping.duobao.model.Cart;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 图文详情
 * 
 * @author xie.xin
 * @data: 2016-1-19 下午9:12:08
 * @version: V1.0
 */
public class PayWeixinActivity extends BaseActivity implements CartClickListener {


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_activity_pay_weixin);

		initData();
	}

	/** 
	* @Title: initData 
	* @Description: TODO
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	private void initData() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter.CartClickListener#onChangedNum()
	 */
	@Override
	public void onChangedNum() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter.CartClickListener#onChecked()
	 */
	@Override
	public void onChecked() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter.CartClickListener#optCart(com.chuannuo.qianbaosuoping.duobao.model.Cart, int)
	 */
	@Override
	public void optCart(Cart c, int num) {
		// TODO Auto-generated method stub
		
	}
}
