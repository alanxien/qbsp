package com.chuannuo.qianbaosuoping;

import com.chuannuo.qianbaosuoping.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author alan.xie
 * @date 2014-10-14 ÏÂÎç12:17:57
 * @Description: ÐÂÊÖ°ïÖú
 */
public class NewsHelpActivity extends BaseActivity implements OnClickListener{
	
	private RelativeLayout rl_help_1;
	private RelativeLayout rl_help_2;
	private RelativeLayout rl_help_3;
	private RelativeLayout rl_help_4;
	private RelativeLayout rl_help_5;
	private RelativeLayout rl_help_6;
	private RelativeLayout rl_help_7;
	private RelativeLayout rl_help_8;
	private RelativeLayout rl_help_9;
	
	private TextView tv_exp1;
	private TextView tv_exp2;
	private TextView tv_exp3;
	private TextView tv_exp4;
	private TextView tv_exp5;
	private TextView tv_exp6;
	private TextView tv_exp7;
	private TextView tv_exp8;
	private TextView tv_exp9;
	
	private ImageView iv_image_1;
	private ImageView iv_image_2;
	private ImageView iv_image_3;
	private ImageView iv_image_4;
	private ImageView iv_image_5;
	private ImageView iv_image_6;
	private ImageView iv_image_7;
	private ImageView iv_image_8;
	private ImageView iv_image_9;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_help);
		
		initView();
	}

	public void initView(){
		rl_help_1 = (RelativeLayout) findViewById(R.id.rl_help_1);
		rl_help_2 = (RelativeLayout) findViewById(R.id.rl_help_2);
		rl_help_3 = (RelativeLayout) findViewById(R.id.rl_help_3);
		rl_help_4 = (RelativeLayout) findViewById(R.id.rl_help_4);
		rl_help_5 = (RelativeLayout) findViewById(R.id.rl_help_5);
		rl_help_6 = (RelativeLayout) findViewById(R.id.rl_help_6);
		rl_help_7 = (RelativeLayout) findViewById(R.id.rl_help_7);
		rl_help_8 = (RelativeLayout) findViewById(R.id.rl_help_8);
		rl_help_9 = (RelativeLayout) findViewById(R.id.rl_help_9);
		
		tv_exp1 = (TextView) findViewById(R.id.tv_exp1);
		tv_exp2 = (TextView) findViewById(R.id.tv_exp2);
		tv_exp3 = (TextView) findViewById(R.id.tv_exp3);
		tv_exp4 = (TextView) findViewById(R.id.tv_exp4);
		tv_exp5 = (TextView) findViewById(R.id.tv_exp5);
		tv_exp6 = (TextView) findViewById(R.id.tv_exp6);
		tv_exp7 = (TextView) findViewById(R.id.tv_exp7);
		tv_exp8 = (TextView) findViewById(R.id.tv_exp8);
		tv_exp9 = (TextView) findViewById(R.id.tv_exp9);
		
		iv_image_1 = (ImageView) findViewById(R.id.iv_image_1);
		iv_image_2 = (ImageView) findViewById(R.id.iv_image_2);
		iv_image_3 = (ImageView) findViewById(R.id.iv_image_3);
		iv_image_4 = (ImageView) findViewById(R.id.iv_image_4);
		iv_image_5 = (ImageView) findViewById(R.id.iv_image_5);
		iv_image_6 = (ImageView) findViewById(R.id.iv_image_6);
		iv_image_7 = (ImageView) findViewById(R.id.iv_image_7);
		iv_image_8 = (ImageView) findViewById(R.id.iv_image_8);
		iv_image_9 = (ImageView) findViewById(R.id.iv_image_9);
		
		rl_help_1.setOnClickListener(this);
		rl_help_2.setOnClickListener(this);
		rl_help_3.setOnClickListener(this);
		rl_help_4.setOnClickListener(this);
		rl_help_5.setOnClickListener(this);
		rl_help_6.setOnClickListener(this);
		rl_help_7.setOnClickListener(this);
		rl_help_8.setOnClickListener(this);
		rl_help_9.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_help_1:
			if(tv_exp1.getVisibility() == View.GONE){
				tv_exp1.setVisibility(View.VISIBLE);
				iv_image_1.setImageResource(R.drawable.newuser2);
			}else{
				tv_exp1.setVisibility(View.GONE);
				iv_image_1.setImageResource(R.drawable.newuser1);
			}
			break;
		case R.id.rl_help_2:
			if(tv_exp2.getVisibility() == View.GONE){
				tv_exp2.setVisibility(View.VISIBLE);
				iv_image_2.setImageResource(R.drawable.newuser2);
			}else{
				tv_exp2.setVisibility(View.GONE);
				iv_image_2.setImageResource(R.drawable.newuser1);
			}
			break;
		case R.id.rl_help_3:
			if(tv_exp3.getVisibility() == View.GONE){
				tv_exp3.setVisibility(View.VISIBLE);
				iv_image_3.setImageResource(R.drawable.newuser2);
			}else{
				tv_exp3.setVisibility(View.GONE);
				iv_image_3.setImageResource(R.drawable.newuser1);
			}
			break;
		case R.id.rl_help_4:
			if(tv_exp4.getVisibility() == View.GONE){
				tv_exp4.setVisibility(View.VISIBLE);
				iv_image_4.setImageResource(R.drawable.newuser2);
			}else{
				tv_exp4.setVisibility(View.GONE);
				iv_image_4.setImageResource(R.drawable.newuser1);
			}
			break;
		case R.id.rl_help_5:
			if(tv_exp5.getVisibility() == View.GONE){
				tv_exp5.setVisibility(View.VISIBLE);
				iv_image_5.setImageResource(R.drawable.newuser2);
			}else{
				tv_exp5.setVisibility(View.GONE);
				iv_image_5.setImageResource(R.drawable.newuser1);
			}
			break;
		case R.id.rl_help_6:
			if(tv_exp6.getVisibility() == View.GONE){
				tv_exp6.setVisibility(View.VISIBLE);
				iv_image_6.setImageResource(R.drawable.newuser2);
			}else{
				tv_exp6.setVisibility(View.GONE);
				iv_image_6.setImageResource(R.drawable.newuser1);
			}
			break;
		case R.id.rl_help_7:
			if(tv_exp7.getVisibility() == View.GONE){
				tv_exp7.setVisibility(View.VISIBLE);
				iv_image_7.setImageResource(R.drawable.newuser2);
			}else{
				tv_exp7.setVisibility(View.GONE);
				iv_image_7.setImageResource(R.drawable.newuser1);
			}
			break;
		case R.id.rl_help_8:
			if(tv_exp8.getVisibility() == View.GONE){
				tv_exp8.setVisibility(View.VISIBLE);
				iv_image_8.setImageResource(R.drawable.newuser2);
			}else{
				tv_exp8.setVisibility(View.GONE);
				iv_image_8.setImageResource(R.drawable.newuser1);
			}
			break;
		case R.id.rl_help_9:
			if(tv_exp9.getVisibility() == View.GONE){
				tv_exp9.setVisibility(View.VISIBLE);
				iv_image_9.setImageResource(R.drawable.newuser2);
			}else{
				tv_exp9.setVisibility(View.GONE);
				iv_image_9.setImageResource(R.drawable.newuser1);
			}
			break;

		default:
			break;
		}
	}
}






