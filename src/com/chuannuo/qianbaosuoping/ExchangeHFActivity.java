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
 * @Description: 话费兑换
 */
public class ExchangeHFActivity extends BaseActivity implements OnClickListener{

	private RelativeLayout rl_hf_20;
	private RelativeLayout rl_hf_30;
	private RelativeLayout rl_hf_50;
	private RelativeLayout rl_hf_100;
	
	private TextView tv_title1; //话费20元
	private TextView tv_title2; //话费30元
	private TextView tv_title3; //话费50元
	private TextView tv_title4; //话费100元
	
	private Intent intent;
	private String title; //兑换类容
	private int xbNum; //积分

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange_hf);
		
		rl_hf_20 = (RelativeLayout) findViewById(R.id.rl_hf_20);
		rl_hf_30 = (RelativeLayout) findViewById(R.id.rl_hf_30);
		rl_hf_50 = (RelativeLayout) findViewById(R.id.rl_hf_50);
		rl_hf_100 = (RelativeLayout) findViewById(R.id.rl_hf_100);
		
		tv_title1 = (TextView) findViewById(R.id.tv_title1);
		tv_title2 = (TextView) findViewById(R.id.tv_title2);
		tv_title3 = (TextView) findViewById(R.id.tv_title3);
		tv_title4 = (TextView) findViewById(R.id.tv_title4);
		
		rl_hf_20.setOnClickListener(this);
		rl_hf_30.setOnClickListener(this);
		rl_hf_50.setOnClickListener(this);
		rl_hf_100.setOnClickListener(this);
		
		intent = new Intent();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_hf_20:
			title = tv_title1.getText().toString();
			xbNum = 200*10000;
			break;
		case R.id.rl_hf_30:
			title = tv_title2.getText().toString();
			xbNum = 295*10000;		
			break;
		case R.id.rl_hf_50:
			title = tv_title3.getText().toString();
			xbNum = 490*10000;
			break;
		case R.id.rl_hf_100:
			title = tv_title4.getText().toString();
			xbNum = 960*10000;
			break;
		default:
			break;
		}
		
		intent.setClass(this, ExchangeActivity.class);
		intent.putExtra(Constant.TYPE, Constant.PHONE);
		intent.putExtra(Constant.XB_NUM, xbNum);
		intent.putExtra(Constant.TITLE, title);
		startActivity(intent);
		this.finish();
	}
}
