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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.BindingPhoneActivity;
import com.chuannuo.qianbaosuoping.ExchangeActivity;
import com.chuannuo.qianbaosuoping.PerfectInfoActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.duobao.adapter.CanyuzheAdapter;
import com.chuannuo.qianbaosuoping.duobao.adapter.OldTaskAdapter;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.chuannuo.qianbaosuoping.duobao.model.OldTask;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.chuannuo.qianbaosuoping.view.CustomProgressDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-19 下午9:12:08 
 * @version:  V1.0 
 */
public class PayOrderActivity extends BaseActivity{
	
	private int payMoney;
	private TextView tv_total;
	private TextView tv_r_money;
	private CheckBox checkbox;
	private TextView tv_pay;
	private CustomDialog pDialog; // 手机对话框
	private CustomDialog mDialog; // 对话框

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
		checkbox = (CheckBox) findViewById(R.id.checkbox);
		tv_pay = (TextView) findViewById(R.id.tv_pay);
		
		payMoney = getIntent().getIntExtra("payMoney", 0);
		
		initData();
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
							int integral = obj.getInt("indiana_money");//可用积分
							tv_r_money.setText("（夺宝币："+integral+"元）");
							if(integral<payMoney){
								checkbox.setChecked(false);
								checkbox.setText("余额不足");
								checkbox.setEnabled(false);
							}else{
								checkbox.setChecked(true);
								checkbox.setText("");
								checkbox.setEnabled(true);
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
				}else if(checkbox.isChecked()){
					goPay();
				}else{
					Toast.makeText(PayOrderActivity.this, "请选择支付方式", Toast.LENGTH_SHORT).show();
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
		
}
