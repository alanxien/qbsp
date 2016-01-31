package com.chuannuo.qianbaosuoping;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.PhoneInformation;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 登录
 */
public class LoginActivity extends BaseActivity {
	
	private EditText et_account; //账号
	private EditText et_password;//密码
	private TextView tv_login;
	
	private CheckBox cb_remember_password;//记住密码
	private TextView tv_find_password; //找回密码
	private TextView tv_register; //注册

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		initView();
		initData();
	}

	private void initView(){
		cb_remember_password = (CheckBox) findViewById(R.id.cb_remember_password);
		tv_find_password = (TextView) findViewById(R.id.tv_find_password);
		et_account = (EditText) findViewById(R.id.et_account);
		et_password = (EditText) findViewById(R.id.et_password);
		tv_login = (TextView) findViewById(R.id.tv_login);
		tv_register = (TextView) findViewById(R.id.tv_register);
		
		PhoneInformation.initTelephonyManager(LoginActivity.this);
		RequestParams params = new RequestParams();
		params.put("imei", PhoneInformation.getImei());
		HttpUtil.get(Constant.GET_PHONE_NUM_URL, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getString("code").equals("1")){
						et_account.setText(response.getJSONObject("data").getString("mobile"));
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
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
		
		
		tv_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(et_account.getText().toString().equals("") || et_password.getText().toString().equals("")){
					Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
				}else{
					openProgressDialog("系统正在登陆...");
					/*
					 * 登陆
					 */
					
					RequestParams params = new RequestParams();
					params.put("mobile", et_account.getText().toString());
					params.put("password", et_password.getText().toString());
					params.put("imei", PhoneInformation.getImei());
					
					HttpUtil.get(Constant.LOGIN_URL, params, new JsonHttpResponseHandler(){
						
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							try {
								
								if(response.getString("code").equals("0")){
									Toast.makeText(LoginActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
									closeProgressDialog();
								}else{
									JSONObject json = response.getJSONObject("data");
									String p = et_account.getText().toString();
									String pa = et_password.getText().toString();
									myApplication.setPhone(p);
									myApplication.setPassword(pa);
									
									editor.putString(Constant.APPID, json.getString("appid"));
									editor.putString(Constant.CODE, json.getString("code"));
									editor.putString(Constant.PHONE, et_account.getText().toString());
									editor.putString(Constant.INVIT_CODE, json.getString("invitation_code"));
									editor.putString(Constant.PASSWORD, et_password.getText().toString());
									editor.putBoolean(Constant.IS_FIRST_IN, false);
									editor.commit();
									
									Message msg = mHandler.obtainMessage();
									msg.what = 1;
									mHandler.sendMessage(msg);
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
							closeProgressDialog();
							Toast.makeText(LoginActivity.this, "登录失败，用户名或密码错误！", Toast.LENGTH_SHORT).show();
							super.onFailure(statusCode, headers, throwable, errorResponse);
						}
					});
				}
			}
		});
		
		tv_find_password.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this,FindPasswordActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			}
		});
		
		tv_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this,QuickRegisterActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			}
		});
	}
	
	private void initData(){
		String account = pref.getString(Constant.PHONE, "");
		String password = pref.getString(Constant.PASSWORD, "");
		if(!account.equals("")){
			et_account.setText(account);
		}
		
		if(!password.equals("")){
			et_password.setText(password);
		}
	}
	

	/**
	 * @author alan.xie
	 * @date 2014-11-4 下午4:49:22
	 * @Description: 获取用户信息
	 * @param 
	 * @return void
	 */
	public void getInfo(){
		RequestParams param = new RequestParams();
		param.put("id", pref.getString(Constant.APPID, "0"));
		
		HttpUtil.get(Constant.USER_INFO_URL, param, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(!response.getString("code").equals("0")){
						editor.putString(Constant.ADDRESS, response.getString("address"));
						editor.putString(Constant.ZFB, response.getString("alipay_code"));
						editor.putString(Constant.CFT, response.getString("tenpay_code"));
						editor.commit();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					closeProgressDialog();
				}
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(LoginActivity.this, getResources().getString(R.string.sys_remind2), Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				getInfo();
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
				break;

			default:
				break;
			}
		};
	};
}













