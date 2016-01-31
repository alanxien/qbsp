package com.chuannuo.qianbaosuoping;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 支付宝兑换
 */
public class ExchangeZFBActivity extends BaseActivity implements OnClickListener{
	
	private RelativeLayout rl_alipay_30;
	private RelativeLayout rl_alipay_50;
	private RelativeLayout rl_alipay_100;
	private RelativeLayout rl_alipay_500;
	
	private TextView tv_title2; //支付宝30元
	private TextView tv_title3; //支付宝50元
	private TextView tv_title4; //支付宝100元
	private TextView tv_title5; //支付宝500元
	
	private Intent intent;
	private String title; //兑换类容
	private int xbNum; //积分

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange_zfb);
		
		rl_alipay_30 = (RelativeLayout) findViewById(R.id.rl_alipay_30);
		rl_alipay_50 = (RelativeLayout) findViewById(R.id.rl_alipay_50);
		rl_alipay_100 = (RelativeLayout) findViewById(R.id.rl_alipay_100);
		rl_alipay_500 = (RelativeLayout) findViewById(R.id.rl_alipay_500);
		
		tv_title2 = (TextView) findViewById(R.id.tv_title2);
		tv_title3 = (TextView) findViewById(R.id.tv_title3);
		tv_title4 = (TextView) findViewById(R.id.tv_title4);
		tv_title5 = (TextView) findViewById(R.id.tv_title5);
		
		rl_alipay_30.setOnClickListener(this);
		rl_alipay_50.setOnClickListener(this);
		rl_alipay_100.setOnClickListener(this);
		rl_alipay_500.setOnClickListener(this);
		
		intent = new Intent();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_alipay_30:
			title = tv_title2.getText().toString();
			xbNum = 300*10000;		
			break;
		case R.id.rl_alipay_50:
			title = tv_title3.getText().toString();
			xbNum = 500*10000;
			break;
		case R.id.rl_alipay_100:
			title = tv_title4.getText().toString();
			xbNum = 1000*10000;
			break;
		case R.id.rl_alipay_500:
			title = tv_title5.getText().toString();
			xbNum = 5000*10000;
			break;
		default:
			break;
		}
		
		intent.setClass(this, ExchangeActivity.class);
		intent.putExtra(Constant.TYPE, Constant.ZFB);
		intent.putExtra(Constant.XB_NUM, xbNum);
		intent.putExtra(Constant.TITLE, title);
		startActivity(intent);
		this.finish();
	}

}
