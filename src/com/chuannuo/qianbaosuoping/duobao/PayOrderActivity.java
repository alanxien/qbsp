/* 
 * @Title:  OldTaskActivity.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-19 下午9:12:08 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.BindingPhoneActivity;
import com.chuannuo.qianbaosuoping.PerfectInfoActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.chuannuo.tangguo.TGData;
import com.chuannuo.tangguo.TangGuoWall;
import com.chuannuo.tangguo.listener.TangGuoWallListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-19 下午9:12:08 
 * @version:  V1.0 
 */
public class PayOrderActivity extends BaseActivity implements TangGuoWallListener{
	
	private int payMoney;
	private TextView tv_total;
	private TextView tv_r_money;
	private CheckBox default_checkbox;
	private TextView tv_pay;
	private CustomDialog pDialog; // 手机对话框
	private CustomDialog mDialog; // 对话框
	
	private RelativeLayout rel_tg;
	private CheckBox tg_checkbox;
	private TextView tvDb;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_activity_pay_order);
		
		tv_total = (TextView) findViewById(R.id.tv_total);
		tv_r_money = (TextView) findViewById(R.id.tv_r_money);
		default_checkbox = (CheckBox) findViewById(R.id.checkbox);
		tv_pay = (TextView) findViewById(R.id.tv_pay);
		tg_checkbox = (CheckBox) findViewById(R.id.tg_checkbox);
		tvDb = (TextView) findViewById(R.id.tv_earn_db);
		payMoney = getIntent().getIntExtra("payMoney", 0);
		
		/*
		 * 在需要显示积分墙的地方 放入如下两段代码
		 */
		TangGuoWall.initWall(this, pref.getString(Constant.APPID, "0"));//参数2为userId，可以为空
		TangGuoWall.setTangGuoWallListener(PayOrderActivity.this);
		tvDb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TangGuoWall.show();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.chuannuo.qianbaosuoping.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}

	/** 
	* @Title: initData 
	* @Description: TODO
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	private void initData() {
		tv_total.setText(payMoney+"元");
		RequestParams param = new RequestParams();
		param.put("app_id", pref.getString(Constant.APPID, "0"));
		
		HttpUtil.get(Constant.DB_USER_INFO_URL, param, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(!response.getString("code").equals("0")){
						JSONObject obj = response.getJSONObject("data");
						if(obj != null){
							double integral = obj.getDouble("indiana_money");//可用积分
							DecimalFormat df = new DecimalFormat("0.00");//格式化小数
							df.setRoundingMode(RoundingMode.DOWN);
							String money = df.format(integral/1.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
							tv_r_money.setText("（夺宝币："+money+"元）");
							if(integral<payMoney){
								default_checkbox.setChecked(false);
								default_checkbox.setText("余额不足");
								default_checkbox.setEnabled(false);
							}else{
								default_checkbox.setChecked(true);
								default_checkbox.setText("");
								default_checkbox.setEnabled(true);
							}
						}
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
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
		
		tv_pay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isBindingPhone()){
					pDialog = new CustomDialog(PayOrderActivity.this, R.style.CustomDialog,
							new CustomDialog.CustomDialogListener() {

								@Override
								public void onClick(View view) {
									pDialog.cancel();
									Intent intent = new Intent();
									intent.setClass(PayOrderActivity.this,
											BindingPhoneActivity.class);
									startActivity(intent);
								}
							}, 1);

					pDialog.setTitle(getResources().getString(R.string.dg_remind_title));
					pDialog.setBtnStr("立刻前往");
					pDialog.setContent("请先绑定好手机号码");
					pDialog.setCancelable(false);
					pDialog.setCanceledOnTouchOutside(false);
					pDialog.show();
				}else if(!isBindingAddress()){
					mDialog = new CustomDialog(PayOrderActivity.this, R.style.CustomDialog,
							new CustomDialog.CustomDialogListener() {

								@Override
								public void onClick(View view) {
									mDialog.cancel();
									Intent intent = new Intent();
									intent.setClass(PayOrderActivity.this,
											PerfectInfoActivity.class);
									startActivity(intent);
								}
							}, 1);

					mDialog.setTitle(getResources().getString(R.string.dg_remind_title));
					mDialog.setBtnStr("立即前往");
					mDialog.setContent("请先填写好收货地址，在（我-账号设置）中可以修改");
					mDialog.setCancelable(false);
					mDialog.setCanceledOnTouchOutside(false);
					mDialog.show();
				}else if(default_checkbox.isChecked()){
					goPay();
				}else if(tg_checkbox.isChecked()){
					TangGuoWall.show();
				}else{
					Toast.makeText(PayOrderActivity.this, "请选择支付方式", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		checkBoxListener();
	}

	/** 
	* @Title: checkBoxListener 
	* @Description: TODO
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	private void checkBoxListener() {
		default_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					tg_checkbox.setChecked(false);
					tv_pay.setText("确认支付");
				}
			}
		});
		
		tg_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					default_checkbox.setChecked(false);
					tv_pay.setText("糖果SDK支付");
				}
			}
		});
	}

	/** 
	* @Title: initData 
	* @Description: TODO
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	private void goPay() {
		openProgressDialog("正在支付...");
		RequestParams param = new RequestParams();
		param.put("appid", pref.getString(Constant.APPID, "0"));//39887
		param.put("code", pref.getString(Constant.CODE, "0"));//3a877d30418ea2452972e7975bc26c1c
		
		HttpUtil.get(Constant.DB_CART_PAY_URL, param, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getString("code").equals("1")){
						Toast.makeText(PayOrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
						
						editor.putString(Constant.DB_CART_NUM,"");
						editor.commit();
						CartActivity.isPay = true;
						PayOrderActivity.this.finish();
					}else{
						Toast.makeText(PayOrderActivity.this, "支付失败,"+response.getString("info"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					closeProgressDialog();
				}
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				closeProgressDialog();
				Toast.makeText(PayOrderActivity.this, "支付失败，请检查网络", Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.listener.TangGuoWallListener#onAddPoint(int, java.lang.String, double)
	 */
	@Override
	public void onAddPoint(int arg0, String arg1, double arg2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.listener.TangGuoWallListener#onSign(int, java.lang.String, double)
	 */
	@Override
	public void onSign(int arg0, String arg1, double arg2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.tangguo.listener.TangGuoWallListener#onUploadImgs(java.util.List)
	 */
	@Override
	public void onUploadImgs(List<TGData> arg0) {
		// TODO Auto-generated method stub
		
	}
		
}
