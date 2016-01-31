package com.chuannuo.qianbaosuoping;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2014-10-14 ����12:17:57
 * @Description: ��¼
 */
public class FindPasswordActivity extends BaseActivity implements OnClickListener{
	
	private EditText et_mobile;
	private TextView tv_get_code;
	private EditText et_code;
	private TextView tv_binding;
	private EditText et_password;
	private EditText et_r_password;
	
	private Intent intent;
	private RequestParams params;
	
	private TimeCount time;
	private String password;
	private String rPassword;
	private String code;
	private String mobile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		
		initView();
	}

	private void initView(){
		et_mobile = (EditText) findViewById(R.id.et_mobile);
		et_code = (EditText) findViewById(R.id.et_code);
		tv_get_code = (TextView) findViewById(R.id.tv_get_code);
		tv_binding = (TextView) findViewById(R.id.tv_binding);
		et_password = (EditText) findViewById(R.id.et_password);
		et_r_password = (EditText) findViewById(R.id.et_r_password);
		
		intent = new Intent();
		params = new RequestParams();
		
		tv_get_code.setClickable(true);
		tv_get_code.setOnClickListener(this);
		tv_binding.setOnClickListener(this);
		
		time = new TimeCount(60000, 1000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_get_code:
			mobile = et_mobile.getText().toString();
			if(mobile.equals("")){
				Toast.makeText(this, "�ֻ��Ų���Ϊ��", Toast.LENGTH_SHORT).show();
			}else if(mobile.length() != 11){
				Toast.makeText(this, "�ֻ��Ų���ȷ", Toast.LENGTH_SHORT).show();
			}else{
				params.put("appid", pref.getString(Constant.APPID, "0"));
				params.put("mobile", et_mobile.getText().toString());
				params.put("type", "recoverPassword");
				
				openProgressDialog("���ڻ�ȡ��֤��...");
				HttpUtil.get(Constant.GET_CODE_URL, params, new JsonHttpResponseHandler(){
					
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if(response.getString("code").equals("0")){
								Toast.makeText(FindPasswordActivity.this, "�ֻ��Ų����ڣ�", Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(FindPasswordActivity.this, "��֤���ѷ��͵����ֻ�����Ⱥ�...", Toast.LENGTH_SHORT).show();
								time.start();//��ʼ��ʱ
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
						Toast.makeText(FindPasswordActivity.this, "��ȡ��֤��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}
				});
			}
			break;
		case R.id.tv_binding:
			code = et_code.getText().toString();
			mobile = et_mobile.getText().toString();
			password = et_password.getText().toString();
			rPassword = et_r_password.getText().toString();
			
			if(mobile.equals("")){
				Toast.makeText(this, "�ֻ��Ų���Ϊ��", Toast.LENGTH_SHORT).show();
			}else if(mobile.length() != 11){
				Toast.makeText(this, "�ֻ��Ų���ȷ", Toast.LENGTH_SHORT).show();
			}else if(code.equals("")){
				Toast.makeText(FindPasswordActivity.this, "��֤�벻��Ϊ�գ�", Toast.LENGTH_SHORT).show();
			}else if(code.length() != 6){
				Toast.makeText(FindPasswordActivity.this, "��֤�벻��ȷ��", Toast.LENGTH_SHORT).show();
			}else if(password.equals("") || rPassword.equals("")){
				Toast.makeText(FindPasswordActivity.this, "���벻��Ϊ�գ�", Toast.LENGTH_SHORT).show();
			}else if (password.length() < 6){
				Toast.makeText(FindPasswordActivity.this, "���벻��������λ��", Toast.LENGTH_SHORT).show();
			}else if(!password.equals(rPassword)){
				Toast.makeText(FindPasswordActivity.this, "�����������벻��ȣ�", Toast.LENGTH_SHORT).show();
			}else {
				params.put("mobile", mobile);
				params.put("password", password);
				params.put("code", code);
				params.put("type", "recoverPassword");
				
				openProgressDialog("������֤�ֻ��ź�У����...");
				HttpUtil.get(Constant.RECOVER_PASSWORD_URL, params, new JsonHttpResponseHandler(){
					
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if(response.getString("code").equals("0")){
								Toast.makeText(FindPasswordActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(FindPasswordActivity.this, "�޸�����ɹ���", Toast.LENGTH_SHORT).show();
//								myApplication.setPhone(mobile);
//								editor.putString(Constant.PHONE, mobile);
//								editor.putString(Constant.PASSWORD, password);								
//								editor.commit();
								intent.setClass(FindPasswordActivity.this, LoginActivity.class);
								startActivity(intent);
								FindPasswordActivity.this.finish();
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
						Toast.makeText(FindPasswordActivity.this, "�ֻ��ź���֤�벻ƥ�䣡", Toast.LENGTH_SHORT).show();
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
	 * @date 2014-11-19 ����12:22:22
	 * @Description: ����ʱ 60��
	 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);//��������Ϊ��ʱ��,�ͼ�ʱ��ʱ����
		}
		@Override
		public void onFinish() {//��ʱ���ʱ����
			tv_get_code.setText("������֤");
			tv_get_code.setClickable(true);
		}
		@Override
		public void onTick(long millisUntilFinished){//��ʱ������ʾ
			tv_get_code.setClickable(false);
			tv_get_code.setText(millisUntilFinished /1000+"��");
		}
	}
}













