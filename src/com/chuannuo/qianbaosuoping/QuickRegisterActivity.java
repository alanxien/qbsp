package com.chuannuo.qianbaosuoping;

import java.math.BigInteger;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.PhoneInformation;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 登录
 */
public class QuickRegisterActivity extends BaseActivity{
	
	private EditText et_invite_code;
	private TextView tv_quick_registration;
	private TextView tv_login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quick_registration);
		
		et_invite_code = (EditText) findViewById(R.id.et_invite_code);
		tv_quick_registration = (TextView) findViewById(R.id.tv_quick_registration);
		tv_login = (TextView) findViewById(R.id.tv_login);
		
		tv_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(QuickRegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				QuickRegisterActivity.this.finish();
			}
		});
		
		tv_quick_registration.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String invit_code = et_invite_code.getText().toString();
				RequestParams params = new RequestParams();
				params.put("invitation_code", invit_code);
				PhoneInformation.initTelephonyManager(QuickRegisterActivity.this);
				params.put("imei", PhoneInformation.getImei());
				params.put("imsi", PhoneInformation.getImsi());
				params.put("machineType", PhoneInformation.getMachineType());
				params.put("os_vision", PhoneInformation.getOsVersion());
				params.put("longt_latl", PhoneInformation.getLatitLongit());
				params.put("resolution", QuickRegisterActivity.this.getWindowManager().getDefaultDisplay().getWidth()+"x"+QuickRegisterActivity.this.getWindowManager().getDefaultDisplay().getHeight());
				params.put("net_type", PhoneInformation.getNetType());
				params.put("language", PhoneInformation.getLanguage());
				params.put("macaddress", PhoneInformation.getMacAddress());
				params.put("physical_size", PhoneInformation.getTotalMemory());
				params.put("channel_id", getMetaData(QuickRegisterActivity.this, "LEZHUAN_CHANNEL"));
			
				openProgressDialog(getResources().getString(R.string.sys_starting));
				HttpUtil.post(Constant.QUICK_REGISTRATION_URL, params, new JsonHttpResponseHandler(){
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
								editor.putString(Constant.APPID, obj.getString("appid"));
								editor.putString(Constant.CODE,obj.getString("code"));
								editor.putString(Constant.INVIT_CODE, obj.getString("invitation_code"));
								editor.putBoolean(Constant.IS_FIRST_IN, false);
								editor.putBoolean(Constant.IS_QUICK_START, true);
								editor.commit();
								myApplication.setAppId(new BigInteger(obj.getString("appid")));
								
								intent.setClass(QuickRegisterActivity.this, MainActivity.class);
								startActivity(intent);
								QuickRegisterActivity.this.finish();
							}else{
								Toast.makeText(QuickRegisterActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
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
		});
	}
	
	/**
	 * @author alan.xie
	 * @date 2015-1-14 下午12:37:59
	 * @Description: 获取渠道号
	 * @param @param context
	 * @param @param key
	 * @param @return
	 * @return String
	 */
	private String getMetaData(Context context,
			String key) {
		try {
	           ApplicationInfo  ai = context.getPackageManager().getApplicationInfo(
	                  context.getPackageName(), PackageManager.GET_META_DATA);
	           Object value = ai.metaData.get(key);
	           if (value != null) {
	              return value.toString();
	           }
	       } catch (Exception e) {
	       }
	       return "";

	    }

}













