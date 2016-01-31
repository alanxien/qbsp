package com.chuannuo.qianbaosuoping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.common.Constant;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 新手任务
 */
public class NewTaskActivity extends BaseActivity implements OnClickListener{

	private RelativeLayout rl_know_app;//app介绍
	private RelativeLayout rl_sign_in; //每日签到
	private RelativeLayout rl_wave;    //每日摇一摇
	private RelativeLayout rl_share;   //分享任务
	private RelativeLayout rl_application_task;//体验一个应用任务
	private RelativeLayout rl_exchange_msg;    //完善兑换信息
	
	private TextView tv_know_reward;
	private TextView tv_sign_reward;
	private TextView tv_wave_reward;
	private TextView tv_share_reward;
	private TextView tv_experience_reward;
	private TextView tv_user_info_reward;
	
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_task);
		
		initView();
		
	}
	
	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}
	
	private void initView(){
		rl_know_app = (RelativeLayout) findViewById(R.id.rl_know_app);
		rl_sign_in = (RelativeLayout) findViewById(R.id.rl_sign_in);
		rl_wave = (RelativeLayout) findViewById(R.id.rl_wave);
		rl_share = (RelativeLayout) findViewById(R.id.rl_share);
		rl_application_task = (RelativeLayout) findViewById(R.id.rl_application_task);
		rl_exchange_msg = (RelativeLayout) findViewById(R.id.rl_exchange_msg);
		
		tv_know_reward = (TextView) findViewById(R.id.tv_know_reward);
		tv_sign_reward = (TextView) findViewById(R.id.tv_sign_reward);
		tv_wave_reward = (TextView) findViewById(R.id.tv_wave_reward);
		tv_share_reward = (TextView) findViewById(R.id.tv_share_reward);
		tv_experience_reward = (TextView) findViewById(R.id.tv_experience_reward);
		tv_user_info_reward = (TextView) findViewById(R.id.tv_user_info_reward);
		intent = new Intent();
		
		rl_know_app.setOnClickListener(this);
		rl_sign_in.setOnClickListener(this);
		rl_wave.setOnClickListener(this);
		rl_share.setOnClickListener(this);
		rl_application_task.setOnClickListener(this);
		rl_exchange_msg.setOnClickListener(this);
	}
	
	private void initData(){
		if(pref.getBoolean(Constant.TASK_KNOW_APP, false)){
			tv_know_reward.setText(getResources().getString(R.string.finished));
			tv_know_reward.setTextColor(getResources().getColor(R.color.RedTheme));
		}
		if(pref.getBoolean(Constant.TASK_SIGN, false)){
			tv_sign_reward.setText(getResources().getString(R.string.finished));
			tv_sign_reward.setTextColor(getResources().getColor(R.color.RedTheme));
		}
		if(pref.getBoolean(Constant.TASK_WAVE, false)){
			tv_wave_reward.setText(getResources().getString(R.string.finished));
			tv_wave_reward.setTextColor(getResources().getColor(R.color.RedTheme));
		}
		if(pref.getBoolean(Constant.TASK_SHARE, false)){
			tv_share_reward.setText(getResources().getString(R.string.finished));
			tv_share_reward.setTextColor(getResources().getColor(R.color.RedTheme));
		}
		if(pref.getBoolean(Constant.TASK_EXPERIENCE, false)){
			tv_experience_reward.setText(getResources().getString(R.string.finished));
			tv_experience_reward.setTextColor(getResources().getColor(R.color.RedTheme));
		}
		if(pref.getBoolean(Constant.TASK_USER_INFO, false) || (!pref.getString(Constant.QQ, "").equals("") && 
			!pref.getString(Constant.ZFB, "").equals("") && !pref.getString(Constant.CFT, "").equals(""))){
			
			tv_user_info_reward.setText(getResources().getString(R.string.finished));
			tv_user_info_reward.setTextColor(getResources().getColor(R.color.RedTheme));
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_know_app:
			editor.putBoolean(Constant.TASK_KNOW_APP, true);
			editor.commit();
			intent.setClass(NewTaskActivity.this, AppDescActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_sign_in:
			intent.putExtra(Constant.NEW_TASK,Constant.NEW_TASK);
			intent.setClass(NewTaskActivity.this, SignInActivity.class);
			startActivity(intent);		
			break;
		case R.id.rl_wave:
			intent.putExtra(Constant.NEW_TASK,Constant.NEW_TASK);
			intent.setClass(NewTaskActivity.this, WaveActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_share:
			//super.share(new View(this));
			
			myApplication.setType(Constant.PAGER2);
			myApplication.setFlag(2);
			editor.putBoolean(Constant.TASK_SHARE, true);
			editor.commit();
			this.finish();
			break;
		case R.id.rl_application_task:
			myApplication.setFlag(1);
			editor.putBoolean(Constant.TASK_EXPERIENCE, true);
			editor.commit();
			this.finish();
			break;
		case R.id.rl_exchange_msg:
			intent.putExtra(Constant.NEW_TASK,Constant.NEW_TASK);
			if(pref.getString(Constant.PHONE, "").equals("")){
				intent.setClass(NewTaskActivity.this, BindingPhoneActivity.class);
			}else{
				intent.setClass(NewTaskActivity.this, PerfectInfoActivity.class);
			}
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}





























