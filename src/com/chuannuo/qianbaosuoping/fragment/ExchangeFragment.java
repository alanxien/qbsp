package com.chuannuo.qianbaosuoping.fragment;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.ExchangeCFTActivity;
import com.chuannuo.qianbaosuoping.ExchangeDBActivity;
import com.chuannuo.qianbaosuoping.ExchangeHFActivity;
import com.chuannuo.qianbaosuoping.ExchangeZFBActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.adapter.ExGridViewAdapter;
import com.chuannuo.qianbaosuoping.adapter.ExchangeAdapter;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.model.Exchange;
import com.chuannuo.qianbaosuoping.model.ExchangeMenu;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:18:27
 * @Description: 兑换
 */
public class ExchangeFragment extends Fragment{
	
	private ExchangeAdapter mAdapter;
	private ArrayList<Exchange> mList;
	private ListView mListView;

	private TextView tv_title;
	
	private Intent intent;
	private SharedPreferences pref;
	private Editor editor;
	private GridView mGridView;
	private ExGridViewAdapter mGvAdapter;
	private ArrayList<ExchangeMenu> listItem;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_exchange, container, false);
		
		mListView = (ListView) view.findViewById(R.id.lv_new_exchanged);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		mGridView = (GridView) view.findViewById(R.id.gv_exchange);
		
		intent = new Intent();
		pref = this.getActivity().getSharedPreferences(Constant.STUDENTS_EARN,FragmentActivity.MODE_PRIVATE);
		editor = pref.edit();
		mList = new ArrayList<Exchange>();
		initGridView();
		return view;
	}
	
	@Override
	public void onResume() {
		if(mList.size()<=0){
			initData();
		}
		initTitle();
		super.onResume();
	}
	
	public void initGridView(){

		listItem = new ArrayList<ExchangeMenu>();
		ExchangeMenu em0 = new ExchangeMenu();
		em0.setImageId(R.drawable.withdraw_glod);
		em0.setName("夺宝币");
		ExchangeMenu em1 = new ExchangeMenu();
		em1.setImageId(R.drawable.withdraw_phone);
		em1.setName("话费充值");
		ExchangeMenu em2 = new ExchangeMenu();
		em2.setImageId(R.drawable.withdraw_zhi);
		em2.setName("支付宝");
		ExchangeMenu em3 = new ExchangeMenu();
		em3.setImageId(R.drawable.withdraw_cai);
		em3.setName("财付通");

		listItem.add(em0);
		listItem.add(em1);
		listItem.add(em2);
		listItem.add(em3);
		
		mGvAdapter = new ExGridViewAdapter(getActivity(), listItem);
		mGridView.setAdapter(mGvAdapter);
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int imgId = listItem.get(position).getImageId();
				switch (imgId) {
				case R.drawable.withdraw_glod:
					intent.setClass(ExchangeFragment.this.getActivity(), ExchangeDBActivity.class);
					startActivity(intent);
					break;
				case R.drawable.withdraw_zhi:
					intent.setClass(ExchangeFragment.this.getActivity(), ExchangeZFBActivity.class);
					startActivity(intent);
					break;
				case R.drawable.withdraw_phone:
					intent.setClass(ExchangeFragment.this.getActivity(), ExchangeHFActivity.class);
					startActivity(intent);
					break;
				case R.drawable.withdraw_cai:
					intent.setClass(ExchangeFragment.this.getActivity(), ExchangeCFTActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
				
			}
		});
	}
	
	public void initData(){
		
		HttpUtil.get(Constant.NEW_EXCHANGE_URL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {

				if(response != null && response.length() > 0){
					JSONObject obj = new JSONObject();
					for (int i = 0; i < response.length(); i++) {
						try {
							obj = (JSONObject) response.get(i);
							Exchange ex = new Exchange();
							ex.setAccount(obj.getString("appid"));
							ex.setExchangeDesc(obj.getString("info"));
							ex.setExchangeTime(obj.getString("create_date").substring(5));
							mList.add(ex);
							
							mAdapter = new ExchangeAdapter(getActivity(), mList);
							mListView.setAdapter(mAdapter);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					
				}
				
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
		initTitle();
	}
	
	private void initTitle(){
		RequestParams param = new RequestParams();
		param.put("id", pref.getString(Constant.APPID, "0"));
		
		HttpUtil.get(Constant.USER_INFO_URL, param, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
					try {
						if(!response.getString("code").equals("0")){
							
							editor.putInt(Constant.SCORE, response.getInt("integral"));
							editor.commit();
							double integral = (double) (Long.parseLong(response.getString("integral"))/10000.0);//可用积分
							DecimalFormat df = new DecimalFormat("0.00");//格式化小数
							df.setRoundingMode(RoundingMode.DOWN);
							String money = df.format(integral/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
							tv_title.setText("可兑换："+money+"元");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
}







