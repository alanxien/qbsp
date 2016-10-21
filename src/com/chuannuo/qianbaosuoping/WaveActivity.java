package com.chuannuo.qianbaosuoping;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.ShakeListener.OnShakeListener;
import com.chuannuo.qianbaosuoping.common.Configuration;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.dao.AppDao;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 摇一摇
 */
public class WaveActivity extends BaseActivity{
	
	/**
     * 摇一摇监听
     */
    private ShakeListener mShakeListener = null;

    /**
     * 重力感应仪
     */
    private Vibrator mVibrator;

    /**
     * 摇一摇动画上图标
     */
    private RelativeLayout mImgUp;

    /**
     * 摇一摇动画下图标
     */
    private RelativeLayout mImgDn;
    
    
    /*
	 * 摇一摇中奖
	 */
	
	private CustomDialog mDialog;
	private CustomDialog dialog;
	private int integral;
	private String wave; //判断是否是从新手任务界面 跳转过来的
	
	private AppDao appDao;
	private AppInfo appInfo;
	private int flag = 0; //判断钱包夺宝广告是否已经没有了，没有了就跳转到推荐任务页面
	private boolean isShake = true;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wave);
		mVibrator = (Vibrator) getApplication().getSystemService(
                VIBRATOR_SERVICE);
        mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
        mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
        
        wave = getIntent().getStringExtra(Constant.NEW_TASK);
        
        mDialog = new CustomDialog(WaveActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				mDialog.cancel();
				isShake = true;
				if(flag == 1){//跳转到推荐任务界面
					myApplication.setFlag(1);
					WaveActivity.this.finish();//摇一摇关闭后会默认回到MainActivity界面，通过Application中的flag选择跳转到那个fragment，即实现跳转
				}else {
					showAdDialog();//弹出广告
				}
			}
		}, 1);
        mDialog.setTitle(getResources().getString(R.string.dg_remind_title));
        mDialog.setBtnStr(getResources().getString(R.string.dg_iknow));
        
        dialog = new CustomDialog(WaveActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				dialog.cancel();
				isShake = true;
				if(flag == 1){
					myApplication.setFlag(1);
					WaveActivity.this.finish();
				}else {
					showAdDialog();
				}
				
			}
		}, 1);
        dialog.setTitle(getResources().getString(R.string.dg_remind_title));
        dialog.setBtnStr(getResources().getString(R.string.dg_rightNow));

        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new OnShakeListener()
        {
            public void onShake()
            {
            	if(isShake){
            		startAnim(); // 开始 摇一摇手掌动画
                    mShakeListener.stop();
                    startVibrato(); // 开始 震动

                    initAppData(); //初始化 广告
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                        	isShake = false;
                        	if(((pref.getInt(Constant.SCORE, 0) - pref.getInt(Constant.LATEST_SCROE, 0)) < 20000) || pref.getInt(Constant.LATEST_SCROE, 0) == 0){
                        		if((pref.getInt(Constant.SCORE, 0) - pref.getInt(Constant.LATEST_SCROE, 0)) < 0 ){
                        			Editor editor = pref.edit();
                        			editor.putInt(Constant.LATEST_SCROE, pref.getInt(Constant.SCORE, 0));
                        			editor.commit();
                        		}
                        		int temp = (20000-((pref.getInt(Constant.SCORE, 0)-pref.getInt(Constant.LATEST_SCROE, 0))));
                        		dialog.setContent("加油做任务哦，每次做满2万积分才能获得抽奖机会！您离下一次抽奖机会还差"+temp+"积分");
                        		dialog.show();
                        	}else{
                            	int winNumber = (int)(Math.random()*100+1); //产生一个1到100的随机数(中奖号码)
                            	
                            	if(winNumber==1){ //一等奖中奖率 100分之一
                            		
                            		mDialog.setContent("恭喜您！中了一等奖，系统奖励0.2元");
                            		integral = 20000;
                            	}else if(winNumber < 4){ //二等奖中奖率 50分之一
                            		
                            		mDialog.setContent("恭喜您！中了二等奖，系统奖励0.1元");
                            		integral = 10000;
                            	}else if(winNumber < 11){ //三等奖中奖率 10分之一
                            		
                            		mDialog.setContent("恭喜您！中了三等奖，系统奖励0.05元");
                            		integral = 5000;
                            	}else if(winNumber < 21){ //四等奖中奖率5分之一
                            		
                            		mDialog.setContent("恭喜您！中了四等奖，系统奖励0.03元");
                            		integral = 3000;
                            	}else{
                            		
                            		mDialog.setContent("恭喜您！中了五等奖，系统奖励0.02元");
                            		integral = 2000;
                            	}
                            	
                            	WaveActivity.this.addIntegral(integral,Constant.TASK_WAVE);
                            	
                            }
                            
                            mVibrator.cancel();
                            mShakeListener.start();
                        }
                    }, 2500);
            	}
                
            }
        });
    }
	
	@Override
	protected void onResume() {
		isShake = true;
		super.onResume();
	}

    public void startAnim()
    { // 定义摇一摇动画动画
        AnimationSet animup = new AnimationSet(true);
        TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                -0.5f);
        mytranslateanimup0.setDuration(1000);
        TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                +0.5f);
        mytranslateanimup1.setDuration(1000);
        mytranslateanimup1.setStartOffset(1000);
        animup.addAnimation(mytranslateanimup0);
        animup.addAnimation(mytranslateanimup1);
        mImgUp.startAnimation(animup);

        AnimationSet animdn = new AnimationSet(true);
        TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                +0.5f);
        mytranslateanimdn0.setDuration(1000);
        TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                -0.5f);
        mytranslateanimdn1.setDuration(1000);
        mytranslateanimdn1.setStartOffset(1000);
        animdn.addAnimation(mytranslateanimdn0);
        animdn.addAnimation(mytranslateanimdn1);
        mImgDn.startAnimation(animdn);
    }

    public void startVibrato()
    { // 定义震动
        mVibrator.vibrate(new long[]{500, 200, 500, 200}, -1); // 第一个｛｝里面是节奏数组，
                                                               // 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
    }

    public void shake_activity_back(View v)
    { // 标题栏 返回按钮
        this.finish();
    }

    public void linshi(View v)
    { // 标题栏
        startAnim();
        mShakeListener.stop();
        startVibrato(); // 开始 震动
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mShakeListener != null)
        {
            mShakeListener.stop();
        }
    }
    
    /**
	 * @author alan.xie
	 * @date 2014-12-8 下午3:33:23
	 * @Description: 获取app数据
	 * @param 
	 * @return void
	 */
	private void getAppList(){
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("channel_id", getMetaData(WaveActivity.this, "LEZHUAN_CHANNEL"));
		HttpUtil.get(Constant.DOWNLOAD_URL,params, new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getInt("code") == 1){
						
						JSONArray jArray = response.getJSONArray("data");
						
						if(null != jArray && jArray.length() > 0){
							
							JSONArray installed = myApplication.getjArry();
							JSONArray jarrayN = null;
							if(installed != null && installed.length()>0){
								jarrayN = new JSONArray();
								for(int i = 0 ; i<installed.length(); i++){
									for(int j = 0 ; j<jArray.length(); j++){
										if(!jArray.getJSONObject(j).getString("package_name").equals(installed.get(i))){
											jarrayN.put(j, jArray.getJSONObject(j));
										}
									}
								}
							}
							
							if(null != jarrayN){
								jArray = jarrayN;
							}
							for(int i = 0; i < jArray.length(); i++){
								JSONObject obj = jArray.getJSONObject(i);
								ContentValues values = new ContentValues();
								values.put("resource_id", obj.getInt("id"));
								values.put("ad_id", obj.getInt("ad_id"));
								values.put("title", obj.getString("title"));
								values.put("price", obj.getString("price"));
								values.put("h5_big_url", obj.getString("h5_big_url"));
								values.put("click_type", obj.getInt("clicktype"));
								values.put("name", obj.getString("name"));
								values.put("description", obj.getString("description"));
								values.put("package_name", obj.getString("package_name"));
								values.put("brief", obj.getString("brief"));
								values.put("score", obj.getInt("score"));
								values.put("resource_size", obj.getString("resource_size"));
								values.put("file", obj.getString("file"));
								values.put("icon",obj.getString("icon"));
								values.put("b_type", obj.getString("btype"));
								values.put("total_score", obj.getInt("score")+obj.getInt("sign_number")*5000);
								values.put("sign_time", obj.getInt("reportsigntime"));
								values.put("sign_number", obj.getInt("sign_number"));
								
								
								Long id = appDao.insert(Configuration.TB_APPINFO, values);
								
								if(id <= 0){
									Toast.makeText(WaveActivity.this, "数据下载失败!", Toast.LENGTH_SHORT).show();
									appDao.close();
								}
							}
				    		getOne();
						}
						if(null == appInfo || appInfo.equals("")){
							flag = 1;
						}else{
							flag = 0;
							initImageAdDialog(appInfo);
						}
					}else{
						Toast.makeText(WaveActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
					}
					super.onSuccess(statusCode, headers, response);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(WaveActivity.this, "数据获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-16 上午11:43:50
	 * @Description: 从数据库中随机取出一条数据
	 * @param 
	 * @return void
	 */
	private void initAppData(){
		if(null == appDao){
			appDao = new AppDao(this);
		}
		
		if(moreThanMinute(Constant.OFFLINE_TIME, System.currentTimeMillis(), 10)){
			appDao.clearnTable(Configuration.TB_APPINFO);
			editor.putLong(Constant.OFFLINE_TIME, System.currentTimeMillis());
			editor.commit();
		}else{
			getOne();
		}
		
		if(null == appInfo || appInfo.equals("")){
			getAppList();
		}else{
			flag = 0;
			initImageAdDialog(appInfo);
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-31 上午10:02:50
	 * @Description: 增加积分
	 * @param @param integral
	 * @return void
	 */
	public void addIntegral(final int integral,String reason){
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));
		params.put("integral", integral);
		params.put("code", pref.getString(Constant.CODE, ""));
		params.put("reason", "摇一摇抽奖");
		
		HttpUtil.get(Constant.ADD_INTEGRAL_URL,params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Message msg = handler.obtainMessage();
				try {
					if(response.getInt("code") != 1){
						Toast.makeText(WaveActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
						msg.what = -1;
					}else{
						/*
						 * 增加积分成功
						 */
						editor.putInt(Constant.LATEST_SCROE, pref.getInt(Constant.SCORE, 0)+integral);//更新最近摇完后的积分
						editor.putInt(Constant.SCORE, pref.getInt(Constant.SCORE, 0)+integral); //更新总积分
						editor.commit();
						msg.what = 1;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					handler.sendMessage(msg);
					isShake = true;
				}
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(WaveActivity.this, getResources().getString(R.string.sys_remind3), Toast.LENGTH_SHORT).show();
				isShake = true;
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(null != wave && wave.equals(Constant.NEW_TASK)){
					editor.putBoolean(Constant.TASK_WAVE, true);
					editor.commit();
				}
    			mDialog.show();
				break;
			default:
				mDialog.setContent("系统繁忙，抽奖失败，稍后请重试！");
				mDialog.show();
				break;
			}
		};
	};
	
	/**
	 * @author alan.xie
	 * @date 2015-1-4 下午12:09:21
	 * @Description: 本地数据库获取随机数据
	 * @param 
	 * @return void
	 */
	private void getOne(){
		String sql = "SELECT * FROM "+Configuration.TB_APPINFO+" ORDER BY RANDOM() limit 1 ";
		Cursor cur = appDao.randQuery(sql, null);
		if(null != cur && cur.moveToFirst()){
			appInfo = new AppInfo();
			int resIdColumn = cur.getColumnIndex("resource_id");
			int adIdColumn = cur.getColumnIndex("ad_id");
			int titleColumn = cur.getColumnIndex("title");
			int bigUrlColumn = cur.getColumnIndex("h5_big_url");
			int nameColumn = cur.getColumnIndex("name");
			int iconColumn = cur.getColumnIndex("icon");
			int descriptionColumn = cur.getColumnIndex("description");
			int packageNameColumn = cur.getColumnIndex("package_name");
			int briefColumn = cur.getColumnIndex("brief");
			int scoreColumn = cur.getColumnIndex("score");
			int resourceSizeColumn = cur.getColumnIndex("resource_size");
			int fileColumn = cur.getColumnIndex("file");
			int bType = cur.getColumnIndex("b_type");
			int totalScore = cur.getColumnIndex("total_score");
			int reportSignTime = cur.getColumnIndex("sign_time");
			int signNumber = cur.getColumnIndex("sign_number");
			
			appInfo.setTotalScore(cur.getInt(totalScore));
			appInfo.setSign_rules(cur.getInt(reportSignTime));
			appInfo.setNeedSign_times(cur.getInt(signNumber));
			appInfo.setB_type(cur.getInt(bType));
			appInfo.setResource_id(cur.getInt(resIdColumn));
			appInfo.setAdId(cur.getInt(adIdColumn));
			appInfo.setTitle(cur.getString(titleColumn));
			appInfo.setName(cur.getString(nameColumn));
			appInfo.setDescription(cur.getString(descriptionColumn));
			appInfo.setPackage_name(cur.getString(packageNameColumn));
			appInfo.setBrief(cur.getString(briefColumn));
			appInfo.setScore(cur.getInt(scoreColumn));
			appInfo.setResource_size(cur.getString(resourceSizeColumn));
			
			String fileUrl = cur.getString(fileColumn);
			String iconUrl = cur.getString(iconColumn);
			String h5Url = cur.getString(bigUrlColumn);
			
			if(!fileUrl.contains("http")){
				fileUrl = Constant.ROOT_URL+fileUrl;
			}
			if(!iconUrl.contains("http")){
				iconUrl = Constant.ROOT_URL+iconUrl;
			}
			if(!h5Url.contains("http")){
				h5Url = Constant.ROOT_URL+h5Url;
			}
			appInfo.setFile(fileUrl);
			appInfo.setIcon(iconUrl);
			appInfo.setH5_big_url(iconUrl);
		}
	}
}
