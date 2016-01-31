/* 
 * @Title:  OldTaskActivity.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-19 下午9:12:08 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.duobao.adapter.CanyuzheAdapter;
import com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter;
import com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter.CartClickListener;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.chuannuo.qianbaosuoping.duobao.model.Cart;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 图文详情
 * 
 * @author xie.xin
 * @data: 2016-1-19 下午9:12:08
 * @version: V1.0
 */
public class CartActivity extends BaseActivity implements CartClickListener {

	private List<Cart> cList;
	private CartAdapter adapter;
	private ListView listView;
	private TextView tv_count;
	private TextView tv_pay_money;
	private TextView tv_opt;//结算，删除
	private int totalNum;
	private ProgressBar progressBar;
	private LinearLayout ll_empty;
	public static boolean isPay = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_activity_cart_list);

		listView = (ListView) findViewById(R.id.lv_listView);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_pay_money = (TextView) findViewById(R.id.tv_pay_money);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		tv_opt = (TextView) findViewById(R.id.tv_opt);
		ll_empty = (LinearLayout) findViewById(R.id.ll_empty);

		initData();
	}
	
	/* (non-Javadoc)
	 * @see com.chuannuo.qianbaosuoping.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		if(CartActivity.isPay && cList != null){
			cList.clear();
			if (null == adapter) {
				adapter = new CartAdapter(CartActivity.this, cList,
						CartActivity.this, totalNum);
			} else {
				adapter.notifyDataSetChanged();
			}
			
			tv_opt.setText("结算");
			tv_count.setText("共" + cList.size() + "件商品：");
			tv_pay_money.setText("0元");
			
			if(cList==null || cList.size() ==0){
				ll_empty.setVisibility(View.VISIBLE);
				tv_opt.setClickable(false);
			}else{
				ll_empty.setVisibility(View.GONE);
				tv_opt.setClickable(true);
			}
		}
		super.onResume();
	}

	/**
	 * @Title: initData
	 * @Description: TODO
	 * @author xie.xin
	 * @param
	 * @return void
	 * @throws
	 */
	private void initData() {
		cList = new ArrayList<Cart>();
		getCartData();
		tv_opt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tv_opt.getText().equals("结算")){
					Intent intent = new Intent();
					intent.putExtra("payMoney", totalNum);
					intent.setClass(CartActivity.this, PayOrderActivity.class);
					startActivity(intent);
				}else{
					Iterator<Cart> c = cList.iterator();
					String cIds  = "";
					String tIds  = "";
					while (c.hasNext()) {
						Cart obj = c.next();
						if (obj.isChecked()) {
							cIds = cIds+obj.getcId()+",";
						}else{
							tIds = tIds+obj.gettId()+",";
						}
					}
					if(!cIds.equals("")){
						delGoods(cIds.substring(0,cIds.length()-1),tIds.equals("")?"":tIds.substring(0,tIds.length()-1));
					}
				}
			}
		});
	}

	public void getCartData() {
		progressBar.setVisibility(View.VISIBLE);
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));// 39887
		HttpUtil.get(Constant.DB_CART_LIST_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							Message msg = mHandler.obtainMessage();
							if (response.getInt("code") == 1) {
								JSONArray jArray = response
										.getJSONArray("data");
								if (jArray != null && jArray.length() > 0) {
									cList.clear();
									for (int i = 0; i < jArray.length(); i++) {
										JSONObject obj = jArray
												.getJSONObject(i);
										if (obj != null) {
											Cart c = new Cart();
											c.setChecked(false);
											c.setCount(obj.getInt("count"));
											c.setPic(Constant.ROOT_URL
													+ obj.getString("picture"));
											c.setpMoney(obj.getString(
													"pay_money").equals("null") ? 0
													: obj.getInt("pay_money"));
											c.settMoney(obj.getString(
													"total_money").equals(
													"null") ? 0 : obj
													.getInt("total_money"));
											c.setTitle(obj.getString("title"));
											c.settId(obj.getInt("t_id"));
											c.setcId(obj.getInt("c_id"));
											cList.add(c);
											totalNum = totalNum + c.getCount();
										}
									}
								}
							} else {
								Toast.makeText(CartActivity.this,
										response.getString("info"),
										Toast.LENGTH_SHORT).show();
							}
							msg.what = 1;
							mHandler.sendMessage(msg);
							super.onSuccess(statusCode, headers, response);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							progressBar.setVisibility(View.GONE);
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						progressBar.setVisibility(View.GONE);
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}
	
	private void delGoods(String cIds,final String tIds){
		openProgressDialog("正在删除...");
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));// 39887
		params.put("count", "");
		params.put("t_id", "");
		params.put("c_id", cIds);
		params.put("sign", "delete");
		HttpUtil.get(Constant.DB_OP_CART_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								Toast.makeText(CartActivity.this,
										"删除成功",
										Toast.LENGTH_SHORT).show();
								editor.putString(Constant.DB_CART_NUM, tIds);
								editor.commit();
								Cart obj = new Cart();
								for (int i = cList.size() - 1; i >= 0; i--) {
									obj = cList.get(i);
									if (obj.isChecked()) {
										totalNum = totalNum-obj.getCount();
										cList.remove(i);
									}
								}
								
								Message msg = mHandler.obtainMessage();
								msg.what = 2;
								mHandler.sendMessage(msg);
							} else {
								Toast.makeText(CartActivity.this,
										"删除失败,"+response.getString("info"),
										Toast.LENGTH_SHORT).show();
							}
							super.onSuccess(statusCode, headers, response);
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
						Toast.makeText(CartActivity.this,"删除失败，请检查网络",
								Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}

	Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (null == adapter) {
					adapter = new CartAdapter(CartActivity.this, cList,
							CartActivity.this, totalNum);
				} else {
					adapter.notifyDataSetChanged();
				}
				listView.setAdapter(adapter);
				
				tv_count.setText("共" + cList.size() + "件商品：");
				tv_pay_money.setText(totalNum + "元");
				
				if(cList==null || cList.size() ==0){
					ll_empty.setVisibility(View.VISIBLE);
					tv_opt.setClickable(false);
				}else{
					ll_empty.setVisibility(View.GONE);
					tv_opt.setClickable(true);
				}
				break;
			case 2:
				if (null == adapter) {
					adapter = new CartAdapter(CartActivity.this, cList,
							CartActivity.this, totalNum);
				} else {
					adapter.notifyDataSetChanged();
				}
				listView.setAdapter(adapter);
				tv_opt.setText("结算");
				tv_count.setText("共" + cList.size() + "件商品：");
				tv_pay_money.setText(totalNum + "元");
				
				if(cList==null || cList.size() ==0){
					ll_empty.setVisibility(View.VISIBLE);
					tv_opt.setClickable(false);
				}else{
					ll_empty.setVisibility(View.GONE);
					tv_opt.setClickable(true);
				}
				break;
			default:
				break;
			}
		};
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter.CartClickListener
	 * #onChangedNum(int)
	 */
	@Override
	public void onChangedNum() {

		Iterator<Cart> c = cList.iterator();
		int num = 0;
		while (c.hasNext()) {
			Cart obj = c.next();
			num = num + obj.getCount();
		}
		if (num != totalNum) {
			tv_opt.setText("结算");
			tv_count.setText("共" + cList.size() + "件商品：");
			tv_pay_money.setText(num + "元");
			totalNum = num;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter.CartClickListener
	 * #onChecked(boolean, int)
	 */
	@Override
	public void onChecked() {
		Iterator<Cart> c = cList.iterator();
		int pNum = 0;// 一共结算多少元
		int cNum = 0;// 删除多少个
		int dNum = 0;// 删除多少元
		while (c.hasNext()) {
			Cart obj = c.next();
			pNum = pNum + obj.getCount();
			if (obj.isChecked()) {
				cNum++;
				dNum = dNum + obj.getCount();
			}
		}

		if (cNum > 0) {
			tv_count.setText("共" + cNum + "件商品：");
			tv_pay_money.setText(dNum + "元");
			tv_opt.setText("删除");
			for (int i = 0; i < listView.getChildCount(); i++) {
				View view = listView.getChildAt(i);
				EditText e = (EditText) view.findViewById(R.id.et_num);
				e.setFocusableInTouchMode(false);
				e.clearFocus();
			}
		} else {
			tv_opt.setText("结算");
			tv_count.setText("共" + cList.size() + "件商品：");
			tv_pay_money.setText(pNum + "元");
			for (int i = 0; i < listView.getChildCount(); i++) {
				View view = listView.getChildAt(i);
				EditText e = (EditText) view.findViewById(R.id.et_num);
				e.setFocusableInTouchMode(true);
			}
		}

	}

	/* (non-Javadoc)
	 * @see com.chuannuo.qianbaosuoping.duobao.adapter.CartAdapter.CartClickListener#optCart(com.chuannuo.qianbaosuoping.duobao.model.Cart, int)
	 */
	@Override
	public void optCart(Cart c, int num) {
		int oNum = c.getCount();
		String sign = "add";
		int count = 0;
		if(oNum > num){
			count = oNum-num;
			sign = "reduce";
		}else{
			count = num-oNum;
			sign = "add";
		}
		
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));// 39887
		params.put("count", count);
		params.put("t_id", c.gettId());
		params.put("c_id", "");
		params.put("sign", sign);
		HttpUtil.get(Constant.DB_OP_CART_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}
}
