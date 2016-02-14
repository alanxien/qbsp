package com.chuannuo.qianbaosuoping;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
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
 * @Description: 每日签到
 */
public class SignInActivity extends BaseActivity{

	private ImageView iv_sign_in;
	private TextView tv_sign_in_days;
	private TextView tv_sign_in_level;
	private CustomDialog mDialog;
	private CustomDialog dialog;
	
	
	private String sign;
	private AppDao appDao;
	private AppInfo appInfo;
	private int flag = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		
		iv_sign_in = (ImageView) findViewById(R.id.iv_sign_in);
		tv_sign_in_days = (TextView) findViewById(R.id.tv_sign_in_days);
		tv_sign_in_level = (TextView) findViewById(R.id.tv_sign_in_level);
		
		if(!netWork()){
			Toast.makeText(this, getResources().getString(R.string.sys_remind2), Toast.LENGTH_LONG).show();
		}else{
			initData();
		}
	}
	
	@Override
	protected void onResume() {
		iv_sign_in.setClickable(true);
		super.onResume();
	}
	
	private void initData(){
		
		sign = getIntent().getStringExtra(Constant.NEW_TASK);
		
		mDialog = new CustomDialog(SignInActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				mDialog.cancel();
			}
		}, 1);
        mDialog.setTitle(getResources().getString(R.string.dg_remind_title));
        mDialog.setBtnStr(getResources().getString(R.string.dg_iknow));
        
        dialog = new CustomDialog(SignInActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				dialog.cancel();
				if(flag == 1){
					myApplication.setFlag(1);
					SignInActivity.this.finish();
				}else{
					showAdDialog();
				}
				
			}
		}, 1);
        dialog.setTitle(getResources().getString(R.string.dg_remind_title));
        dialog.setContent("您今天还没有做任务，没有签到资格!");
        dialog.setBtnStr(getResources().getString(R.string.dg_rightNow));
		
		RequestParams param = new RequestParams();
		param.put("id", pref.getString(Constant.APPID, "0"));
		
		HttpUtil.get(Constant.USER_INFO_URL, param, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(!response.getString("code").equals("0")){
						tv_sign_in_days.setText(response.getString("sign_cont"));
						tv_sign_in_level.setText(response.getString("grade_cont"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(SignInActivity.this, getResources().getString(R.string.sys_remind2), Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
		
		/*
		 * 签到
		 */
		iv_sign_in.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initAppData(); //初始化 广告
				if(!moreThanDays(Constant.SIGN_TIMES,System.currentTimeMillis(),1)){
					Toast.makeText(SignInActivity.this, "对不起！一天只能签到一次！", Toast.LENGTH_SHORT).show();
				}else if((System.currentTimeMillis()-pref.getLong(Constant.DOWNLOAD_APP_TIME, 0)) > 24*60*60*10000){
					dialog.show();
				}else{
					iv_sign_in.setClickable(false);
					
					RequestParams params = new RequestParams();
					params.put("appid", pref.getString(Constant.APPID, "0"));
					params.put("reason", "签到");
					
					HttpUtil.get(Constant.SIGN_IN_URL,params, new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							try {
								if(response.getString("code").equals("1")){
									if(null != sign && sign.equals(Constant.NEW_TASK)){
										editor.putBoolean(Constant.TASK_SIGN, true);
										editor.commit();
									}
									mDialog.setContent("恭喜您！签到成功！您将赚的0.05元。");
									editor.putLong(Constant.SIGN_TIMES, System.currentTimeMillis());
						    		editor.commit();
									mDialog.show();
								}else{
									mDialog.setContent("对不起！一天只能签到一次！");
									mDialog.show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally{
								iv_sign_in.setClickable(true);
							}
							super.onSuccess(statusCode, headers, response);
						}
						
						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							mDialog.setContent("系统繁忙，请稍后重试！");
							mDialog.show();
							iv_sign_in.setClickable(true);
							super.onFailure(statusCode, headers, throwable, errorResponse);
						}
					});
				}
			}
		});
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
		params.put("channel_id", getMetaData(SignInActivity.this, "LEZHUAN_CHANNEL"));
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
									Toast.makeText(SignInActivity.this, "数据下载失败!", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(SignInActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
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
				Toast.makeText(SignInActivity.this, "数据获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
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
	 * @date 2015-1-4 下午12:08:45
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
			appInfo.setH5_big_url(h5Url);
			appInfo.setIcon(iconUrl);
		}
	}
	
}





























