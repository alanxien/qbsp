package com.chuannuo.qianbaosuoping;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Configuration;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.dao.AppDao;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.service.DownloadService;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author alan.xie
 * @date 2015-3-3 上午10:58:06
 * @Description: 钱包锁屏 应用下载
 */
public class DownLoadAppActivity extends BaseActivity {
	private final String TAG = "DownLoadAppActivity";
	private ImageView iv_logo;
	private TextView tv_app_name;
	private TextView tv_size;
	private TextView tv_desc;
	private TextView tv_tips;
	private TextView tv_tips2;
	private TextView tv_tips3;
	private TextView tv_downLoad;
	private TextView tv_share;
	private TextView tv_total_score;
	private LinearLayout ll_how_do;
	private LinearLayout ll_game;
	private TextView tv_breif;
	
	String game = "";
	
	private AppInfo appInfo;
	
	private boolean isSharing = false;
	private CustomDialog mDialog;
	private CustomDialog sDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_info);
		
		game = null == getIntent().getStringExtra("game")?"":getIntent().getStringExtra("game");
		initView();
		initData();
	}
	
	@Override
	protected void onResume() {
		if(moreThanDays(Constant.DOWN_TIME, System.currentTimeMillis(), 1)){
			tv_downLoad.setVisibility(View.VISIBLE);
			editor.putInt(Constant.DOWNLOAD_TIMES, 0);
			editor.putLong(Constant.DOWN_TIME, System.currentTimeMillis());
			editor.commit();
		}else if(pref.getInt(Constant.DOWNLOAD_TIMES, 0) > 6){
			tv_downLoad.setVisibility(View.GONE);
		}
		
		if(myApplication.getResourceId() == appInfo.getResource_id()){
			tv_downLoad.setVisibility(View.GONE);
		}
		
		if(myApplication.getDownload() == 1){ //下载中
			tv_downLoad.setVisibility(View.VISIBLE);
			tv_downLoad.setClickable(false);
			tv_downLoad.setText(getResources().getString(R.string.down_loading));
		}else if(myApplication.getDownload() == 2){ //下载完成
			tv_downLoad.setClickable(false);
			tv_downLoad.setVisibility(View.GONE);
			tv_downLoad.setText(getResources().getString(R.string.down_load_install));
		} else { //没下载
			tv_downLoad.setClickable(true);
			tv_downLoad.setVisibility(View.VISIBLE);
			tv_downLoad.setText(getResources().getString(R.string.down_load_install));
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		editor.putInt(Constant.SLIDED, 0);
		editor.commit();
		super.onDestroy();
	}
	
	private void initView(){
		iv_logo = (ImageView) findViewById(R.id.iv_logo);
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		tv_size = (TextView) findViewById(R.id.tv_size);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_downLoad = (TextView) findViewById(R.id.tv_downLoad);
		tv_tips = (TextView) findViewById(R.id.tv_tips);
		tv_tips2 = (TextView) findViewById(R.id.tv_tips2);
		tv_tips3 = (TextView) findViewById(R.id.tv_tips3);
		tv_total_score = (TextView) findViewById(R.id.tv_total_score);
		tv_share = (TextView) findViewById(R.id.tv_share);
		ll_how_do = (LinearLayout) findViewById(R.id.ll_how_do);
		ll_game = (LinearLayout) findViewById(R.id.ll_game);
		tv_breif = (TextView) findViewById(R.id.tv_breif);
		
		tv_downLoad.setVisibility(View.GONE);
		
	}
	
	private void initData(){
		appInfo = (AppInfo) getIntent().getSerializableExtra(Constant.ITEM);
		
		if(game.equals("game")){
			ll_how_do.setVisibility(View.GONE);
			tv_share.setVisibility(View.GONE);
			ll_game.setVisibility(View.VISIBLE);
			tv_breif.setVisibility(View.VISIBLE);
			tv_breif.setText(appInfo.getBrief());
		}else{
			ll_how_do.setVisibility(View.VISIBLE);
			tv_share.setVisibility(View.VISIBLE);
			ll_game.setVisibility(View.GONE);
			tv_breif.setVisibility(View.GONE);
		}
		
		float integral = (float) ((long)appInfo.getScore()/10000.0);//可用积分
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数
		df.setRoundingMode(RoundingMode.DOWN);
		String money = df.format(integral/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
		
		if(appInfo.getB_type() == 2){
			tv_tips.setText("下载安装后，注册成功即赚 "+money+"元");
		}else{
			tv_tips.setText("下载安装后，使用3分钟系统将会赚  "+money+"元");
		}
		
		myApplication.setDownload(0);
		
			String url = appInfo.getIcon();
			ImageLoader.getInstance().displayImage(url, iv_logo);
			tv_app_name.setText(appInfo.getTitle());
			tv_size.setText("大小："+appInfo.getResource_size()+"M");
			tv_desc.setText(appInfo.getDescription());
			float integrals = (float) ((long)appInfo.getTotalScore()/10000.0);//可用积分
			DecimalFormat df1 = new DecimalFormat("0.00");//格式化小数
			df1.setRoundingMode(RoundingMode.DOWN);
			String money1 = df.format(integrals/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
			
			tv_total_score.setText("+"+money1+"元");
			tv_tips2.setText("安装完成后，请到未完成任务列表中，继续签到，每次签到即可获得0.05元。");
			tv_tips3.setText("每隔"+appInfo.getSign_rules()+""+"天可以签到 一次，签到"+appInfo.getNeedSign_times()+"次，任务完成");
			
			tv_downLoad.setVisibility(View.VISIBLE);
			tv_downLoad.setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					if(myApplication.getDownload() == 0){
						Intent intent = new Intent(DownLoadAppActivity.this,DownloadService.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(Constant.ITEM, appInfo);
						intent.putExtras(bundle);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						DownLoadAppActivity.this.startService(intent);
						myApplication.setDownload(1);
						tv_downLoad.setText(getResources().getString(R.string.down_loading));
						if(!game.equals("game")){
							Toast.makeText(DownLoadAppActivity.this, "如果该任务，您还没有分享给赚友，记得立即分享哦！",Toast.LENGTH_LONG).show();
						}
						
					}
					
				}
			});	
			
			mDialog = new CustomDialog(DownLoadAppActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
				
				@Override
				public void onClick(View view) {
					mDialog.cancel();
					Intent intent = new Intent();
					intent.setClass(DownLoadAppActivity.this,
							InvitationActivity.class);
					startActivity(intent);
				}
			}, 1);
	        mDialog.setTitle(getResources().getString(R.string.dg_remind_title));
	        mDialog.setContent(getResources().getString(R.string.dg_invitation_tips,appInfo.getScore()*20/100));
	        mDialog.setBtnStr(getResources().getString(R.string.dg_invitationNow));
	        
	        sDialog = new CustomDialog(DownLoadAppActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
				
				@Override
				public void onClick(View view) {
					sDialog.cancel();
				}
			}, 1);
	        sDialog.setTitle(getResources().getString(R.string.dg_share_success));
			
			final RequestParams params = new RequestParams();
			params.put("app_id", pref.getString(Constant.APPID, "0"));
			params.put("task_id", appInfo.getAdId());
			
			if(!isSharing){
				tv_share.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						isSharing = true;
						Toast.makeText(DownLoadAppActivity.this, "正在分享给好友,请稍后...", Toast.LENGTH_SHORT).show();
						HttpUtil.post(Constant.REPORT_TASK, params, new JsonHttpResponseHandler(){
							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								try {
									if(response.getInt("code") == 1){
										JSONArray jArray = response.getJSONArray("data");
										sDialog.setContent(getResources().getString(R.string.dg_share_success_tips,jArray.length(),appInfo.getScore()*20/100*jArray.length()));
										sDialog.setBtnStr(getResources().getString(R.string.dg_iknow));
										sDialog.show();
									}else if(response.getInt("code") == -1){
										mDialog.show();
									}else{
										Toast.makeText(DownLoadAppActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally{
									isSharing = false;
								}
								super.onSuccess(statusCode, headers, response);
							}
							
							@Override
							public void onFailure(int statusCode, Header[] headers,
									String responseString, Throwable throwable) {
								isSharing = false;
								super.onFailure(statusCode, headers, responseString, throwable);
							}
						});
					}
				});
			}
			
		}

}












