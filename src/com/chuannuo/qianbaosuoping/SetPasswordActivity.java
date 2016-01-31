package com.chuannuo.qianbaosuoping;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.os.Bundle;
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
public class SetPasswordActivity extends BaseActivity{
	
	private EditText et_password;
	private EditText et_r_password;
	private TextView tv_login;
	private TextView tv_tips;
	
	private Intent intent;
	private RequestParams params;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_password);
		
		initView();
	}

	private void initView(){
		et_password = (EditText) findViewById(R.id.et_password);
		et_r_password = (EditText) findViewById(R.id.et_r_password);
		tv_login = (TextView) findViewById(R.id.tv_login);
		tv_tips = (TextView) findViewById(R.id.tv_tips);
		
		tv_tips.setText(String.format(getResources().getString(R.string.binding_password_tips), getIntent().getStringExtra(Constant.PHONE)));
		
		intent = new Intent();
		params = new RequestParams();
		tv_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String password = et_password.getText().toString();
				final String rPassword = et_r_password.getText().toString();
				final String mobile = SetPasswordActivity.this.getIntent().getStringExtra(Constant.PHONE);
				if(password.equals("") || rPassword.equals("")){
					Toast.makeText(SetPasswordActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
				}else if (password.length() < 6){
					Toast.makeText(SetPasswordActivity.this, "密码不能少于六位！", Toast.LENGTH_SHORT).show();
				}else if(!password.equals(rPassword)){
					Toast.makeText(SetPasswordActivity.this, "两次输入密码不相等！", Toast.LENGTH_SHORT).show();
				}else {
					params.put("appid", pref.getString(Constant.APPID, "0"));
					params.put("code", pref.getString(Constant.CODE, "0"));
					params.put("mobile", mobile);
					
					params.put("password", password);
					
					openProgressDialog("正在登陆...");
					HttpUtil.get(Constant.MODIFY_USER_URL, params, new JsonHttpResponseHandler(){
						
						public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
							try {
								if(response.getInt("code") == 1){
									
									myApplication.setPhone(mobile);
									editor.putString(Constant.PHONE, mobile);
									editor.putString(Constant.PASSWORD, password);
									editor.commit();
									intent.setClass(SetPasswordActivity.this, MainActivity.class);
									startActivity(intent);
									SetPasswordActivity.this.finish();
								}else{
									Toast.makeText(SetPasswordActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								closeProgressDialog();
							}
						};
						
						public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
							closeProgressDialog();
							Toast.makeText(SetPasswordActivity.this, "密码设置失败！", Toast.LENGTH_SHORT).show();
						};
					});
				}
			}
		});
		
	}
}













