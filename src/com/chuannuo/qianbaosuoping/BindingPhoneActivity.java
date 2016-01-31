package com.chuannuo.qianbaosuoping;

import java.io.Closeable;
import java.math.BigInteger;

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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.opengl.ETC1Util;
import android.os.Bundle;
import android.os.CountDownTimer;
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
public class BindingPhoneActivity extends BaseActivity implements OnClickListener{
	
	private EditText et_mobile;
	private TextView tv_get_code;
	private EditText et_veri_code; //验证码
	private EditText et_password;//邀请码
	private TextView tv_binding;
	
	private Intent intent;
	private RequestParams params;
	
	private TimeCount time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binding_phone);
		
		initView();
	}

	private void initView(){
		et_mobile = (EditText) findViewById(R.id.et_mobile);
		et_veri_code = (EditText) findViewById(R.id.et_veri_code);
		et_password = (EditText) findViewById(R.id.et_password);
		tv_get_code = (TextView) findViewById(R.id.tv_get_code);
		tv_binding = (TextView) findViewById(R.id.tv_binding);
		
		intent = new Intent();
		params = new RequestParams();
		
		tv_get_code.setClickable(true);
		tv_get_code.setOnClickListener(this);
		tv_binding.setOnClickListener(this);
		PhoneInformation.initTelephonyManager(this);
		time = new TimeCount(60000, 1000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_get_code:
			String mobile = et_mobile.getText().toString();
			if(mobile.equals("")){
				Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			}else if(mobile.length() != 11){
				Toast.makeText(this, "手机号不正确", Toast.LENGTH_SHORT).show();
			}else{
				params.put("appid", pref.getString(Constant.APPID, "0"));
				params.put("imei", PhoneInformation.getImei());
				params.put("mobile", et_mobile.getText().toString());
				params.put("type", "BINDMOBILE");
				
				openProgressDialog("正在获取验证码...");
				HttpUtil.post(Constant.GET_CODE_URL, params, new JsonHttpResponseHandler(){
					
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if(response.getString("code").equals("0")){
								Toast.makeText(BindingPhoneActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(BindingPhoneActivity.this, "验证码已发送到你手机，请等候...", Toast.LENGTH_SHORT).show();
								time.start();//开始计时
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							closeProgressDialog();
						}
						super.onSuccess(statusCode, headers, response);
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						closeProgressDialog();
						Toast.makeText(BindingPhoneActivity.this, "获取验证码失败！", Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}
				});
			}
			break;
		case R.id.tv_binding:
			String code = et_veri_code.getText().toString();
			String password = et_password.getText().toString();
			
			if(password.equals("")){
				Toast.makeText(BindingPhoneActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
			}else if (password.length() < 6){
				Toast.makeText(BindingPhoneActivity.this, "密码不能少于六位！", Toast.LENGTH_SHORT).show();
			}else if(code.equals("")){
				Toast.makeText(BindingPhoneActivity.this, "验证码不能为空！", Toast.LENGTH_SHORT).show();
			}else if(code.length() != 6){
				Toast.makeText(BindingPhoneActivity.this, "验证码不正确！", Toast.LENGTH_SHORT).show();
			}else{
				params.put("appid", pref.getString(Constant.APPID, "0"));
				params.put("mobile", et_mobile.getText().toString());
				params.put("code", code);
				params.put("type", "BINDMOBILE");
				params.put("imei", PhoneInformation.getImei());
				params.put("password",password);
				
				openProgressDialog("正在验证手机号和校验码...");
				HttpUtil.get(Constant.BIND_MOBILE_URL, params, new JsonHttpResponseHandler(){
					
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if(response.getString("code").equals("0")){
								Toast.makeText(BindingPhoneActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
							}else{
								editor.putString(Constant.APPID, response.getJSONObject("data").getString("appid"));
								editor.putString(Constant.CODE,response.getJSONObject("data").getString("code"));
								editor.putString(Constant.INVIT_CODE, response.getJSONObject("data").getString("invitation_code"));
								editor.putBoolean(Constant.IS_FIRST_IN, false);
								editor.putString(Constant.PHONE, et_mobile.getText().toString());
								editor.putString(Constant.PASSWORD, et_password.getText().toString());
								editor.putBoolean(Constant.IS_QUICK_START, false);
								editor.commit();
								myApplication.setAppId(new BigInteger(response.getJSONObject("data").getString("appid")));
								myApplication.setCode(response.getJSONObject("data").getString("code"));
								
								Toast.makeText(BindingPhoneActivity.this, "手机号绑定成功！", Toast.LENGTH_SHORT).show();
								intent.setClass(BindingPhoneActivity.this, PerfectInfoActivity.class);
								startActivity(intent);
								BindingPhoneActivity.this.finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							closeProgressDialog();
						}
						super.onSuccess(statusCode, headers, response);
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						closeProgressDialog();
						Toast.makeText(BindingPhoneActivity.this, "手机号绑定失败！", Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}
				});
			}
			
			break;
		default:
			break;
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-11-19 下午12:22:22
	 * @Description: 倒计时 60秒
	 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		}
		@Override
		public void onFinish() {//计时完毕时触发
			tv_get_code.setText("重新验证");
			tv_get_code.setClickable(true);
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			tv_get_code.setClickable(false);
			tv_get_code.setText(millisUntilFinished /1000+"秒");
		}
	}
	
}













