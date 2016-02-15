/* 
 * @Title:  GoodsDetailActivity.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-18 下午8:23:25 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.BindingPhoneActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.ReleaseBitmap;
import com.chuannuo.qianbaosuoping.duobao.adapter.CanyuzheAdapter;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.chuannuo.qianbaosuoping.duobao.model.Goods;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.chuannuo.qianbaosuoping.view.MyImgScroll;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 商品详情
 * 
 * @author xie.xin
 * @data: 2016-1-18 下午8:23:25
 * @version: V1.0
 */
public class GoodsDetailActivity extends BaseActivity implements
		OnClickListener {

	private String TAG = GoodsDetailActivity.class.getSimpleName();

	//private MyImgScroll myPager; // 图片容器
	//private LinearLayout ovalLayout; // 圆点容器
	//private List<View> listViews; // 图片组

	private ListView listView;
	private List<Canyuzhe> list;
	private CanyuzheAdapter adapter;
	private Goods goods;

	private TextView tv_title;
	private TextView tv_total;
	private TextView tv_remain;
	private ProgressBar progressbar;
	//ReleaseBitmap releaseBitmap;
	ProgressBar progressBar;
	private TextView tv_wqjx;
	private TextView tv_duobao;
	private TextView tv_cart;
	private CustomDialog mDialog;
	private CustomDialog cDialog;
	private Boolean isCart = false;
	private TextView tv_twxq;
	private ImageView myvp;
	private TextView tv_cart_num;
	private FrameLayout fl_cart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.db_activity_goods_detail);

		//myPager = (MyImgScroll) findViewById(R.id.myvp);
		//ovalLayout = (LinearLayout) findViewById(R.id.vb);
		listView = (ListView) findViewById(R.id.lv_listView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		tv_title = (TextView) findViewById(R.id.goods_title);
		tv_total = (TextView) findViewById(R.id.tv_total);
		tv_remain = (TextView) findViewById(R.id.tv_remain);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		tv_wqjx = (TextView) findViewById(R.id.tv_wqjx);
		tv_duobao = (TextView) findViewById(R.id.tv_duobao);
		tv_cart = (TextView) findViewById(R.id.tv_cart);
		tv_twxq = (TextView) findViewById(R.id.tv_twxq);
		myvp = (ImageView) findViewById(R.id.myvp);
		tv_cart_num = (TextView) findViewById(R.id.tv_cart_num);
		fl_cart = (FrameLayout) findViewById(R.id.fl_cart);

		goods = (Goods) getIntent().getSerializableExtra("GOODS");
		//releaseBitmap = new ReleaseBitmap();
		// 初始化图片
//		initViewPager();
//		// 开始滚动
//		myPager.start(this, listViews, 4000, ovalLayout,
//				R.layout.ad_bottom_item, R.id.ad_item_v,
//				R.drawable.dot_focused, R.drawable.dot_normal);
		if (goods != null) {
			initData();
		}

		tv_duobao.setOnClickListener(this);
		tv_cart.setOnClickListener(this);
		tv_twxq.setOnClickListener(this);
		fl_cart.setOnClickListener(this);
	}

	public void initDialog() {
		mDialog = new CustomDialog(this, R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						mDialog.cancel();
						Intent intent = new Intent();
						intent.setClass(GoodsDetailActivity.this,
								BindingPhoneActivity.class);
						startActivity(intent);
					}
				}, 1);

		mDialog.setTitle(getResources().getString(R.string.dg_remind_title));
		mDialog.setContent("请先绑定手机号再夺宝");

		mDialog.setBtnStr("立刻绑定");
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
	}

	private void initData() {
		tv_title.setText(goods.getTitle());
		tv_total.setText("总需" + goods.getTotalMoney() + "人次");
		tv_remain.setText(goods.getTotalMoney() - goods.getPayMoney()<=0?"0":goods.getTotalMoney() - goods.getPayMoney() + "");
		
		String cart = pref.getString(Constant.DB_CART_NUM, "");
		if(cart !=null && !cart.equals("")){
			tv_cart_num.setVisibility(View.VISIBLE);
			tv_cart_num.setText(cart.split(",").length+"");
		}else{
			tv_cart_num.setVisibility(View.GONE);
		}
		int result = goods.getPayMoney() * 100
				/goods.getTotalMoney();
		progressbar.setProgress(result);
		ImageLoader.getInstance().displayImage(goods.getPic(),
				myvp);
		getIndianat();

		tv_wqjx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("g_id", goods.getId());
				intent.setClass(GoodsDetailActivity.this, OldTaskActivity.class);
				startActivity(intent);
			}
		});

		cDialog = new CustomDialog(this, R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						switch (view.getId()) {
						case R.id.picker_minus:
							if(cDialog.getPickNum()>0){
								cDialog.setPickNum(cDialog.getPickNum()-1);
							}
							break;
						case R.id.picker_add:
							if(cDialog.getPickNum() < 99){
								cDialog.setPickNum(cDialog.getPickNum()+1);
							}
							break;
						case R.id.tv_picker:
							addCart(goods,cDialog.getPickNum());
							if(isCart){
								cDialog.dismiss();
							}else{
								cDialog.dismiss();
								Intent intent = new Intent();
								intent.setClass(GoodsDetailActivity.this, CartActivity.class);
								startActivity(intent);
							}
							break;
						default:
							break;
						}
					}
				}, 5);
		cDialog.getWindow().setGravity(Gravity.BOTTOM);
	}
	
	private void addCart(final Goods g,int num){
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));// 39887
		params.put("count", num);
		params.put("t_id", g.gettId());
		params.put("c_id", "");
		params.put("sign", "add");
		HttpUtil.get(Constant.DB_OP_CART_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								Toast.makeText(GoodsDetailActivity.this,
										"添加成功",
										Toast.LENGTH_SHORT).show();
								String cart = pref.getString(Constant.DB_CART_NUM, "");
								if(cart ==null || cart.equals("")){
									editor.putString(Constant.DB_CART_NUM, g.gettId()+"");
								}else{
									String args[] = cart.split(",");
									if(!useLoop(args, g.gettId()+"")){
										editor.putString(Constant.DB_CART_NUM, cart+","+g.gettId());
									}
								}
								editor.commit();
								Message msg = mHandler.obtainMessage();
								msg.what = 2;
								mHandler.sendMessage(msg);
							} else {
								Toast.makeText(GoodsDetailActivity.this,
										"加入购物车失败,"+response.getString("info"),
										Toast.LENGTH_SHORT).show();
							}
							super.onSuccess(statusCode, headers, response);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Toast.makeText(GoodsDetailActivity.this,"加入购物车失败，请检查网络",
								Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}
	
	public boolean useLoop(String[] arr, String targetValue) {
	    for(String s: arr){
	        if(s.equals(targetValue))
	            return true;
	    }
	    return false;
	}

	/**
	 * @Title: getIndianat
	 * @Description: TODO
	 * @author xie.xin
	 * @param
	 * @return void
	 * @throws
	 */
	private void getIndianat() {
		progressBar.setVisibility(View.VISIBLE);
		list = new ArrayList<Canyuzhe>();
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));// 39887
		params.put("page", 1);
		params.put("t_id", goods.gettId());
		HttpUtil.get(Constant.DB_INDIANAT_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								JSONArray jArray = response
										.getJSONArray("data");
								if (jArray != null && jArray.length() > 0) {
									Message msg = mHandler.obtainMessage();
									JSONObject obj = jArray.getJSONObject(0);
									if (obj != null) {
										Canyuzhe c = new Canyuzhe();
										c.setCount(obj.getInt("count"));
										c.setTitle(obj.getString("app_id"));
										c.setIp(obj.getString("ip"));
										c.setDate(obj.getString("create_date"));

										list.add(c);
									}
									msg.what = 1;
									mHandler.sendMessage(msg);
								}
							} else {
								Toast.makeText(GoodsDetailActivity.this,
										response.getString("info"),
										Toast.LENGTH_SHORT).show();
							}
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

	Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (null == adapter) {
					adapter = new CanyuzheAdapter(GoodsDetailActivity.this,
							list);
				} else {
					adapter.notifyDataSetChanged();
				}
				listView.setAdapter(adapter);
				break;
			case 2:
				String cart = pref.getString(Constant.DB_CART_NUM, "");
				tv_cart_num.setVisibility(View.VISIBLE);
				tv_cart_num.setText(cart.split(",").length+"");
				break;
			default:
				break;
			}
		};
	};

//	private void initViewPager() {
//		listViews = new ArrayList<View>();
//		if (goods != null && goods.getPicList() != null) {
//			List<String> images = goods.getPicList();
//			for (int i = 0; i < images.size(); i++) {
//				ImageView imageView = new ImageView(this);
//				imageView.setImageResource(R.drawable.db_default);
//				ImageLoader.getInstance().displayImage(images.get(i),
//						imageView, releaseBitmap);
//				imageView.setScaleType(ScaleType.FIT_XY);
//				imageView.setId(i);
//				listViews.add(imageView);
//			}
//		}
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		//releaseBitmap.cleanBitmapList();
		//System.gc();
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_duobao:
				isCart = false;
	            cDialog.show();
	            cDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				break;
			case R.id.tv_cart:
				isCart = true;
				cDialog.show();
				cDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				break;
			case R.id.tv_twxq:
				Intent intent = new Intent();
				intent.setClass(GoodsDetailActivity.this, PicTextDetailActivity.class);
				Bundle b = new Bundle();
				b.putStringArrayList("picList", (ArrayList<String>) goods.getPicList());
				intent.putExtras(b);
				startActivity(intent);
				break;
			case R.id.fl_cart:
				Intent intent1 = new Intent();
				intent1.setClass(GoodsDetailActivity.this, CartActivity.class);
				startActivity(intent1);
				break;
			default:
				break;
			}
	}
}
