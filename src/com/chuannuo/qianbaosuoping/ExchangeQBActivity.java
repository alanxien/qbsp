package com.chuannuo.qianbaosuoping;

import org.w3c.dom.Text;

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
 * @date 2014-10-14 ÏÂÎç12:17:57
 * @Description: Q±Ò¶Ò»»
 */
public class ExchangeQBActivity extends BaseActivity implements OnClickListener{
	
	private RelativeLayout rl_qb_10;
	private RelativeLayout rl_qb_20;
	private RelativeLayout rl_qb_30;
	private RelativeLayout rl_qb_50;
	
	private TextView tv_title2; //¶Ò»»Q±Ò10¸ö
	private TextView tv_title3; //¶Ò»»Q±Ò20¸ö
	private TextView tv_title4; //¶Ò»»Q±Ò30¸ö
	private TextView tv_title5; //¶Ò»»Q±Ò50¸ö
	
	private Intent intent;
	private String title; //¶Ò»»ÀàÈÝ
	private int xbNum; //»ý·Ö

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange_qb);
		
		rl_qb_10 = (RelativeLayout) findViewById(R.id.rl_qb_10);
		rl_qb_20 = (RelativeLayout) findViewById(R.id.rl_qb_20);
		rl_qb_30 = (RelativeLayout) findViewById(R.id.rl_qb_30);
		rl_qb_50 = (RelativeLayout) findViewById(R.id.rl_qb_50);
		
		tv_title2 = (TextView) findViewById(R.id.tv_title2);
		tv_title3 = (TextView) findViewById(R.id.tv_title3);
		tv_title4 = (TextView) findViewById(R.id.tv_title4);
		tv_title5 = (TextView) findViewById(R.id.tv_title5);
		
		rl_qb_10.setOnClickListener(this);
		rl_qb_20.setOnClickListener(this);
		rl_qb_30.setOnClickListener(this);
		rl_qb_50.setOnClickListener(this);
		
		intent = new Intent();
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.rl_qb_10:
			title = tv_title2.getText().toString();
			xbNum = 98*10000;		
			break;
		case R.id.rl_qb_20:
			title = tv_title3.getText().toString();
			xbNum = 195*10000;
			break;
		case R.id.rl_qb_30:
			title = tv_title4.getText().toString();
			xbNum = 290*10000;
			break;
		case R.id.rl_qb_50:
			title = tv_title5.getText().toString();
			xbNum = 480*10000;
			break;
		default:
			break;
		}
		
		intent.setClass(this, ExchangeActivity.class);
		intent.putExtra(Constant.TYPE, Constant.QB);
		intent.putExtra(Constant.XB_NUM, xbNum);
		intent.putExtra(Constant.TITLE, title);
		startActivity(intent);
		this.finish();
	}

}










