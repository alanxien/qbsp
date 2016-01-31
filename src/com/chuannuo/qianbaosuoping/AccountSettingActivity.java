package com.chuannuo.qianbaosuoping;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.adapter.MyPagerAdapter;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 完善信息
 */
public class AccountSettingActivity extends BaseActivity {
	
	private TextView tv_user_id;
	private TextView tv_phone_number;
	private EditText et_qq;
	private EditText et_zfb;
	private EditText et_cft;
	private EditText et_o_password; //原始秘密
	private EditText et_n_password; //新密码
	private EditText et_r_password; //重复密码
	private Button btn_userInfo_commit;
	private Button btn_password_commit;
	
	private TextView tv_m_user_info;
	private TextView tv_m_passowrd;
	
	private SharedPreferences pref;
	
	private ViewPager mViewPager;
	private List<View> lists = new ArrayList<View>();
	private MyPagerAdapter myPagerAdapter;
	private CustomDialog mDialog;
	
	private String qq;
	private String alipay;
	private String tenpay;
	private String o_password;
	private String n_password;
	private String r_password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_setting);
		
		initView();
		initData();
	}
	
	@SuppressLint("InflateParams")
	public void initView(){
		
		tv_m_user_info = (TextView) findViewById(R.id.tv_m_user_info);
		tv_m_passowrd = (TextView) findViewById(R.id.tv_m_password);
		
		pref = this.getSharedPreferences(Constant.STUDENTS_EARN, MODE_PRIVATE);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		
		
		
		View page1 = getLayoutInflater().inflate(R.layout.activity_account_modify, null);
		View page2 = getLayoutInflater().inflate(R.layout.activity_account_password_modify, null);
		lists.add(page1);
		lists.add(page2);
		
		tv_user_id = (TextView) page1.findViewById(R.id.tv_user_id);
		tv_phone_number = (TextView) page1.findViewById(R.id.tv_phone_number);
		et_qq = (EditText) page1.findViewById(R.id.et_qq);
		et_zfb = (EditText) page1.findViewById(R.id.et_zfb);
		et_cft = (EditText) page1.findViewById(R.id.et_cft);
		btn_userInfo_commit = (Button) page1.findViewById(R.id.btn_userInfo_commit);
		
		et_o_password = (EditText) page2.findViewById(R.id.et_o_password);
		et_n_password = (EditText) page2.findViewById(R.id.et_n_password);
		et_r_password = (EditText) page2.findViewById(R.id.et_r_password);
		btn_password_commit = (Button) page2.findViewById(R.id.btn_password_commit);
		
		
		
		
		myPagerAdapter = new MyPagerAdapter(lists);
		mViewPager.setAdapter(myPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

				switch (arg0) {
				case 0:
					tv_m_user_info.setTextColor(getResources().getColor(R.color.GreenThem));
					tv_m_passowrd.setTextColor(getResources().getColor(R.color.RedTheme));
					break;
				case 1:
					tv_m_passowrd.setTextColor(getResources().getColor(R.color.GreenThem));
					tv_m_user_info.setTextColor(getResources().getColor(R.color.RedTheme));
					break;

				default:
					break;
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

		});
		
		tv_m_user_info.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mViewPager.setCurrentItem(0);
            }
        });
        
		tv_m_passowrd.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
            	mViewPager.setCurrentItem(1);
            }
        });
		
		mDialog = new CustomDialog(AccountSettingActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				mDialog.cancel();
			}
		}, 1);
        mDialog.setTitle(getResources().getString(R.string.dg_remind_title));
        mDialog.setBtnStr(getResources().getString(R.string.dg_iknow));
        mDialog.setContent(getResources().getString(R.string.dg_modify_success));
		
		btn_userInfo_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RequestParams params = new RequestParams();
				qq = et_qq.getText().toString();
				alipay = et_zfb.getText().toString();
				tenpay = et_cft.getText().toString();
				params.put("appid", pref.getString(Constant.APPID, "0"));
				params.put("code", pref.getString(Constant.CODE, "0"));				   
				params.put("qq_code", qq);
				params.put("alipay_code", alipay);
				params.put("tenpay_code", tenpay);
				params.put("password", pref.getString(Constant.PASSWORD, "123456"));
				params.put("mobile", pref.getString(Constant.PHONE, ""));	
				
				HttpUtil.get(Constant.MODIFY_USER_URL, params, new JsonHttpResponseHandler(){
					
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							if(response.getInt("code") == 1){
								editor.putString(Constant.PHONE, pref.getString(Constant.PHONE, ""));
								editor.putString(Constant.PASSWORD, pref.getString(Constant.PASSWORD, ""));
								editor.putString(Constant.QQ, qq);
								editor.putString(Constant.ZFB, alipay);
								editor.putString(Constant.CFT, tenpay);
								editor.commit();
								mDialog.show();
							}else{
								Toast.makeText(AccountSettingActivity.this, "信息修改失败", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
					
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
						Toast.makeText(AccountSettingActivity.this, "信息修改失败", Toast.LENGTH_SHORT).show();
					};
				});
			}
		});
		
		btn_password_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RequestParams params = new RequestParams();
				o_password = et_o_password.getText().toString();
				n_password = et_n_password.getText().toString();
				r_password = et_r_password.getText().toString();
				
				if(o_password.equals("") || o_password.length() < 6){
					Toast.makeText(AccountSettingActivity.this, "原始秘密不能为空，且长度不能少于6", Toast.LENGTH_LONG).show();
				}else if(n_password.length() < 6 || r_password.length() < 6){
					Toast.makeText(AccountSettingActivity.this, "新密码秘密长度不能小于6", Toast.LENGTH_SHORT).show();
				}else if(!n_password.equals(r_password)){
					Toast.makeText(AccountSettingActivity.this, "两次输入秘密不一致", Toast.LENGTH_SHORT).show();
				}else{
					params.put("appid", pref.getString(Constant.APPID, "0"));			   
					params.put("password", o_password);
					params.put("new_password", n_password);
					params.put("new_password2", r_password);
					
					
					HttpUtil.get(Constant.MODIFY_PASSWORD_URL, params, new JsonHttpResponseHandler(){
						
						public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
							try {
								if(response.getInt("code") == 1){
									editor.putString(Constant.PASSWORD, n_password);
									mDialog.setContent(getResources().getString(R.string.dg_modify_password_success));
									mDialog.show();
									et_o_password.setText("");
									et_n_password.setText("");
									et_r_password.setText("");
								}else{
									Toast.makeText(AccountSettingActivity.this, "信息修改失败", Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						};
						
						public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
							Toast.makeText(AccountSettingActivity.this, "信息修改失败", Toast.LENGTH_SHORT).show();
						};
					});
				}
			}
		});
	}
	
	public void initData(){
		
		if(!netWork()){
			Toast.makeText(this, getResources().getString(R.string.sys_remind2), Toast.LENGTH_SHORT).show();
		}else{
			RequestParams params = new RequestParams();
			String id = pref.getString(Constant.APPID, "0");
			params.put("id", id);
			tv_user_id.setText(id);
			String s = pref.getString(Constant.PHONE, "");
			tv_phone_number.setText(pref.getString(Constant.PHONE, ""));
			if(!pref.getString(Constant.QQ, "").equals("null") && !pref.getString(Constant.QQ, "").equals("")){
				et_qq.setText(pref.getString(Constant.QQ, ""));
			}
			if(!pref.getString(Constant.ZFB, "").equals("null") && !pref.getString(Constant.ZFB, "").equals("")){
				et_zfb.setText(pref.getString(Constant.ZFB, ""));
			}
			if(!pref.getString(Constant.CFT, "").equals("null") && !pref.getString(Constant.ZFB, "").equals("")){
				et_cft.setText(pref.getString(Constant.CFT, ""));
			}

		}
		
	}

}










