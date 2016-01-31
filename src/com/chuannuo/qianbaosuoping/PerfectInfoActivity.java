package com.chuannuo.qianbaosuoping;

import java.math.BigInteger;

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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 完善信息
 */
public class PerfectInfoActivity extends BaseActivity {
	
	private TextView tv_user_id;
	private TextView tv_phone_number;
	private EditText et_qq;
	private EditText et_zfb;
	private EditText et_cft;
	private Button btn_save;
	
	private String app_id;
	private String phone_number;
	private String qq;
	private String alipay;
	private String tenpay;
	
	private CustomDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perfect_info);
		
		initView();
		initData();
	}
	
	public void initView(){
		tv_user_id = (TextView) findViewById(R.id.tv_user_id);
		tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
		et_qq = (EditText) findViewById(R.id.et_qq);
		et_zfb = (EditText) findViewById(R.id.et_zfb);
		et_cft = (EditText) findViewById(R.id.et_cft);
		btn_save = (Button) findViewById(R.id.btn_save);
		
		phone_number = pref.getString(Constant.PHONE, "");
		app_id= pref.getString(Constant.APPID, "0");
		tv_user_id.setText(app_id);
		tv_phone_number.setText(phone_number);
	}
	
	public void initData(){
		
		mDialog = new CustomDialog(PerfectInfoActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				mDialog.cancel();
				PerfectInfoActivity.this.finish();
			}
		}, 1);
        mDialog.setTitle(getResources().getString(R.string.dg_remind_title));
        mDialog.setBtnStr(getResources().getString(R.string.dg_iknow));
        mDialog.setContent(getResources().getString(R.string.dg_modify_success));
		
		if(!netWork()){
			Toast.makeText(this, getResources().getString(R.string.sys_remind2), Toast.LENGTH_SHORT).show();
		}else{
			openProgressDialog(getResources().getString(R.string.dg_data_loading));
			RequestParams params = new RequestParams();
			params.put("id", app_id);
			openProgressDialog("数据加载中");
			HttpUtil.get(Constant.USER_INFO_URL, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
						JSONObject json = response;
						try {
							if(json.getString("code").equals("0")){
								Toast.makeText(PerfectInfoActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
							}else{
								
								if(!json.getString("qq_code").equals("null")){
									et_qq.setText(json.getString("qq_code"));
								}
								if(!json.getString("alipay_code").equals("null")){
									et_zfb.setText(json.getString("alipay_code"));
								}
								if(!json.getString("tenpay_code").equals("null")){
									et_cft.setText(json.getString("tenpay_code"));
								}
								
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally{
							closeProgressDialog();
						}
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					closeProgressDialog();
					Toast.makeText(PerfectInfoActivity.this, getResources().getString(R.string.sys_remind2), Toast.LENGTH_SHORT).show();
				}
			});
		}
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RequestParams params = new RequestParams();
				
					/*
					 * 完善个人信息
					 */
					qq = et_qq.getText().toString();
					alipay = et_zfb.getText().toString();
					tenpay = et_cft.getText().toString();
					params.put("appid", pref.getString(Constant.APPID, "0"));
					params.put("code", pref.getString(Constant.CODE, "0"));
					params.put("mobile", phone_number);					   
					params.put("qq_code", qq);
					params.put("alipay_code", alipay);
					params.put("tenpay_code", tenpay);
					
					HttpUtil.get(Constant.MODIFY_USER_URL, params, new JsonHttpResponseHandler(){
						
						public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
							try {
								if(response.getInt("code") == 1){
									
									myApplication.setPhone(phone_number);
									editor.putString(Constant.PHONE, phone_number);
									editor.putString(Constant.QQ, qq);
									editor.putString(Constant.ZFB, alipay);
									editor.putString(Constant.CFT, tenpay);
									if(!pref.getBoolean(Constant.TASK_USER_INFO, false)){
										editor.putBoolean(Constant.TASK_USER_INFO, true);
										addIntegral(5000,"完善资料");
									}
									
									editor.commit();
									mDialog.show();
								}else{
									Toast.makeText(PerfectInfoActivity.this, "信息修改失败", Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						};
						
						public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
							Toast.makeText(PerfectInfoActivity.this, "信息保存失败", Toast.LENGTH_SHORT).show();
						};
					});
				}
			
		});
	}

}










