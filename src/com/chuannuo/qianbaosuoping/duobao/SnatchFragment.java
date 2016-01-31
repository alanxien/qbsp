/* 
 * @Title:  SnatchFragment.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-4 上午2:58:33 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.ReleaseBitmap;
import com.chuannuo.qianbaosuoping.duobao.adapter.GoodsAdapter;
import com.chuannuo.qianbaosuoping.duobao.adapter.GoodsAdapter.DBOnClickListener;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.chuannuo.qianbaosuoping.duobao.model.Goods;
import com.chuannuo.qianbaosuoping.fragment.EarnFragment;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/** 
 * 夺宝
 * @author  xie.xin 
 * @data:  2016-1-4 上午2:58:33 
 * @version:  V1.0 
 */
public class SnatchFragment extends Fragment{
	
	private List<Goods> list;
	private GoodsAdapter mAdapter;
	private GridView gridView;
	private ProgressBar progressBar;
	private SharedPreferences pref;
	private Editor editor;
	ReleaseBitmap releaseBitmap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_snatch, container, false);
		
		gridView = (GridView) view.findViewById(R.id.gridview);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		releaseBitmap = new ReleaseBitmap();
		
		if(pref == null){
			pref = this.getActivity().getSharedPreferences(Constant.STUDENTS_EARN,
					FragmentActivity.MODE_PRIVATE);
		}
		editor = pref.edit();
		list = new ArrayList<Goods>();
		return view;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
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
		
		progressBar.setVisibility(View.VISIBLE);
		
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));//39887
		params.put("limit", 10);
		HttpUtil.get(Constant.DB_GOODSLIST_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {

								JSONArray jArray = response
										.getJSONArray("data");
								Message msg = mHandler.obtainMessage();
								if (null != jArray && jArray.length() > 0) {
									list.clear();
									for(int i=0;i<jArray.length();i++){
										JSONObject obj = jArray
												.getJSONObject(i);
										if(obj != null){
											Goods g = new Goods();
											g.setId(obj.getInt("id"));
											g.setTitle(obj.getString("title"));
											g.setPic(Constant.ROOT_URL+obj.getString("picture"));
											g.setTotalMoney(obj.getString("total_money").equals("null") ? 0 :obj.getInt("total_money"));
											g.setPayMoney(obj.getString("pay_money").equals("null") ? 0 :obj.getInt("pay_money"));
											g.settId(obj.getInt("t_id"));
											g.setCreateData(obj.getString("create_date"));
											g.setInventory(obj.getInt("inventory"));
											
											list.add(g);
											JSONArray pjArray = obj.getJSONArray("picture_list");
											List<String> l = new ArrayList<String>();
											if(pjArray != null){
												for(int j=0; j<pjArray.length(); j++){
													l.add(Constant.ROOT_URL+pjArray.get(j).toString());
												}
												g.setPicList(l);
											}
										}
									}
									msg.what = 1;
									mHandler.sendMessage(msg);
								}

							} else {
								Toast.makeText(SnatchFragment.this.getActivity(),
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
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(list == null || list.size() == 0){
					Toast.makeText(getActivity(), "数据异常，请检查网络", Toast.LENGTH_SHORT).show();
				}else{
					Intent intent = new Intent();
					Bundle b = new Bundle();
					b.putSerializable("GOODS", list.get(position));
					intent.putExtras(b);
					intent.setClass(getActivity(), GoodsDetailActivity.class);
					startActivity(intent);
				}
				
			}
		});
	}
	
	Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(null == mAdapter){
					mAdapter = new GoodsAdapter(getActivity(), list,releaseBitmap,new DBOnClickListener() {
						
						@Override
						public void onClick(Goods g) {
							addCartData(g);
						}
					});
				}else{
					mAdapter.notifyDataSetChanged();
				}
				gridView.setAdapter(mAdapter);
				break;

			default:
				break;
			}
		};
	};
	
	private void addCartData(final Goods g){
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));// 39887
		params.put("count", 1);
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
								Toast.makeText(getActivity(),
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
							} else {
								Toast.makeText(getActivity(),
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
						Toast.makeText(getActivity(),"加入购物车失败，请检查网络",
								Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiskCache();
		releaseBitmap.cleanBitmapList();
		super.onDestroy();
	}
	
	public boolean useLoop(String[] arr, String targetValue) {
	    for(String s: arr){
	        if(s.equals(targetValue))
	            return true;
	    }
	    return false;
	}

}
