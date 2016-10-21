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
 * @date 2014-10-14 ����12:17:57
 * @Description: ҡһҡ
 */
public class WaveActivity extends BaseActivity{
	
	/**
     * ҡһҡ����
     */
    private ShakeListener mShakeListener = null;

    /**
     * ������Ӧ��
     */
    private Vibrator mVibrator;

    /**
     * ҡһҡ������ͼ��
     */
    private RelativeLayout mImgUp;

    /**
     * ҡһҡ������ͼ��
     */
    private RelativeLayout mImgDn;
    
    
    /*
	 * ҡһҡ�н�
	 */
	
	private CustomDialog mDialog;
	private CustomDialog dialog;
	private int integral;
	private String wave; //�ж��Ƿ��Ǵ������������ ��ת������
	
	private AppDao appDao;
	private AppInfo appInfo;
	private int flag = 0; //�ж�Ǯ���ᱦ����Ƿ��Ѿ�û���ˣ�û���˾���ת���Ƽ�����ҳ��
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
				if(flag == 1){//��ת���Ƽ��������
					myApplication.setFlag(1);
					WaveActivity.this.finish();//ҡһҡ�رպ��Ĭ�ϻص�MainActivity���棬ͨ��Application�е�flagѡ����ת���Ǹ�fragment����ʵ����ת
				}else {
					showAdDialog();//�������
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
            		startAnim(); // ��ʼ ҡһҡ���ƶ���
                    mShakeListener.stop();
                    startVibrato(); // ��ʼ ��

                    initAppData(); //��ʼ�� ���
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
                        		dialog.setContent("����������Ŷ��ÿ������2����ֲ��ܻ�ó齱���ᣡ������һ�γ齱���ỹ��"+temp+"����");
                        		dialog.show();
                        	}else{
                            	int winNumber = (int)(Math.random()*100+1); //����һ��1��100�������(�н�����)
                            	
                            	if(winNumber==1){ //һ�Ƚ��н��� 100��֮һ
                            		
                            		mDialog.setContent("��ϲ��������һ�Ƚ���ϵͳ����0.2Ԫ");
                            		integral = 20000;
                            	}else if(winNumber < 4){ //���Ƚ��н��� 50��֮һ
                            		
                            		mDialog.setContent("��ϲ�������˶��Ƚ���ϵͳ����0.1Ԫ");
                            		integral = 10000;
                            	}else if(winNumber < 11){ //���Ƚ��н��� 10��֮һ
                            		
                            		mDialog.setContent("��ϲ�����������Ƚ���ϵͳ����0.05Ԫ");
                            		integral = 5000;
                            	}else if(winNumber < 21){ //�ĵȽ��н���5��֮һ
                            		
                            		mDialog.setContent("��ϲ���������ĵȽ���ϵͳ����0.03Ԫ");
                            		integral = 3000;
                            	}else{
                            		
                            		mDialog.setContent("��ϲ����������Ƚ���ϵͳ����0.02Ԫ");
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
    { // ����ҡһҡ��������
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
    { // ������
        mVibrator.vibrate(new long[]{500, 200, 500, 200}, -1); // ��һ�����������ǽ������飬
                                                               // �ڶ����������ظ�������-1Ϊ���ظ�����-1���մ�pattern��ָ���±꿪ʼ�ظ�
    }

    public void shake_activity_back(View v)
    { // ������ ���ذ�ť
        this.finish();
    }

    public void linshi(View v)
    { // ������
        startAnim();
        mShakeListener.stop();
        startVibrato(); // ��ʼ ��
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
	 * @date 2014-12-8 ����3:33:23
	 * @Description: ��ȡapp����
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
									Toast.makeText(WaveActivity.this, "��������ʧ��!", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(WaveActivity.this, "���ݻ�ȡʧ�ܣ��������磡", Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-16 ����11:43:50
	 * @Description: �����ݿ������ȡ��һ������
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
	 * @date 2014-10-31 ����10:02:50
	 * @Description: ���ӻ���
	 * @param @param integral
	 * @return void
	 */
	public void addIntegral(final int integral,String reason){
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));
		params.put("integral", integral);
		params.put("code", pref.getString(Constant.CODE, ""));
		params.put("reason", "ҡһҡ�齱");
		
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
						 * ���ӻ��ֳɹ�
						 */
						editor.putInt(Constant.LATEST_SCROE, pref.getInt(Constant.SCORE, 0)+integral);//�������ҡ���Ļ���
						editor.putInt(Constant.SCORE, pref.getInt(Constant.SCORE, 0)+integral); //�����ܻ���
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
				mDialog.setContent("ϵͳ��æ���齱ʧ�ܣ��Ժ������ԣ�");
				mDialog.show();
				break;
			}
		};
	};
	
	/**
	 * @author alan.xie
	 * @date 2015-1-4 ����12:09:21
	 * @Description: �������ݿ��ȡ�������
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
