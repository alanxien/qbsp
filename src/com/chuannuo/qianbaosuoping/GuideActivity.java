package com.chuannuo.qianbaosuoping;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.adapter.GuideViewPagerAdapter;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.MyApplication;
import com.chuannuo.qianbaosuoping.common.PhoneInformation;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-10-13 下午2:37:52
 * @Description: 用户引导界面
 */
public class GuideActivity extends BaseActivity implements OnPageChangeListener{
	
	private static final String TAG = "GuideActivity";
	
	private ViewPager viewPager;
	private GuideViewPagerAdapter vpAdapter;
	private ArrayList<View> views;
	
	private View view1,view2,view3,view4;
	private Button btn_start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		initView(); //初始化组件
		initData(); //初始化数据
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-13 下午2:38:05
	 * @Description: 初始化组件
	 * @param 
	 * @return void
	 */
	@SuppressLint("InflateParams")
	private void initView(){
		//实例化各个界面的布局对象
		LayoutInflater mLi = LayoutInflater.from(this);
		view1 = mLi.inflate(R.layout.activity_guide_view1, null);
		view2 = mLi.inflate(R.layout.activity_guide_view2, null);
		view3 = mLi.inflate(R.layout.activity_guide_view3, null);
		view4 = mLi.inflate(R.layout.activity_guide_view4, null);
		
		//实例化viewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		
		views = new ArrayList<View>();
	}
	
	/**
	 * @author xin.xie
	 * 初始化数据
	 */
	private void initData(){
		
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		
		vpAdapter = new GuideViewPagerAdapter(views);
		viewPager.setOnPageChangeListener(this);
		viewPager.setAdapter(vpAdapter);
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		if(position == vpAdapter.getCount()-1){
			btn_start = (Button) findViewById(R.id.btn_start);
			btn_start.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					register();
				}
			});
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-22 上午10:34:47
	 * @Description: 系统注册
	 * @param 
	 * @return void
	 */
	public void register(){
		myApplication  = (MyApplication) getApplication();
		if(!netWork()){
			Toast.makeText(this, getResources().getString(R.string.sys_remind2), Toast.LENGTH_SHORT).show();
		}else{
			editor.putBoolean(Constant.IS_FIRST_IN, false);
			editor.commit();
			//快速开启
			openProgressDialog(getResources().getString(R.string.sys_starting));
			PhoneInformation.initTelephonyManager(this);
			RequestParams params = new RequestParams();
			params.put("imei", PhoneInformation.getImei());
			
			HttpUtil.post(Constant.QUICK_LOGIN_URL, params,new JsonHttpResponseHandler(){
				/* (non-Javadoc)
				 * @see com.loopj.android.http.JsonHttpResponseHandler#onSuccess(int, org.apache.http.Header[], org.json.JSONObject)
				 */
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					try {
						Intent intent = new Intent();
						
						if(response.getInt("code")==1){
							JSONObject obj = response.getJSONObject("data");
							if(!TextUtils.isEmpty(obj.getString("mobile"))){
								intent.setClass(GuideActivity.this, LoginActivity.class);
								startActivity(intent);
							}else{
								
								editor.putString(Constant.APPID, obj.getString("appid"));
								editor.putString(Constant.CODE,obj.getString("code"));
								editor.putString(Constant.INVIT_CODE, obj.getString("invitation_code"));
								editor.putBoolean(Constant.IS_QUICK_START, true);
								editor.commit();
								myApplication.setAppId(new BigInteger(obj.getString("appid")));
								
								intent.setClass(GuideActivity.this, MainActivity.class);
								startActivity(intent);
							}
							
						}else{
							intent.setClass(GuideActivity.this, QuickRegisterActivity.class);
							startActivity(intent);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally{
						GuideActivity.this.finish();
						closeProgressDialog();
					}
					super.onSuccess(statusCode, headers, response);
				}
				
				/* (non-Javadoc)
				 * @see com.loopj.android.http.JsonHttpResponseHandler#onFailure(int, org.apache.http.Header[], java.lang.Throwable, org.json.JSONObject)
				 */
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					closeProgressDialog();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
		}
		
	}

}





