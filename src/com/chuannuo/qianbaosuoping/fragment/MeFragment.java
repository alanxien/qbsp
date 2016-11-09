package com.chuannuo.qianbaosuoping.fragment;


import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.http.Header;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.MoreActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.RecordActivity;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.duobao.CartActivity;
import com.chuannuo.qianbaosuoping.duobao.DuoBaoListActivity;
import com.chuannuo.qianbaosuoping.duobao.GoodsDetailActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


/**
 * @author alan.xie
 * @date 2014-10-14 下午12:19:13
 * @Description: 我
 */
public class MeFragment extends Fragment implements OnClickListener{

	private RelativeLayout rl_cart;		//购物车
	private RelativeLayout rl_db_list; //夺宝记录
	private TextView tv_duobao_yuer;//夺宝余额
	private RelativeLayout rl_more;
	
	private LinearLayout ll_my_profile;         //用户资料
	private View view;
	private TextView tv_title;
	private TextView tv_exchanged;              //已兑换
	private TextView tv_level;					//等级
	private TextView tv_sign_times;				//签到天数
	private TextView tv_integral_total;         //累计已赚积分
	private TextView tv_integral_usable;        //可用积分
	private TextView  tv_cart_num;
	
	private Intent intent;
	
	private SharedPreferences pref;
	private Editor editor;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.fragment_me, container, false);
	    
	    initView();
	    initData();
		return view;
	}
	
	@Override
	public void onResume() {
		initData();
		super.onResume();
	}
	
	public void initView(){
		rl_cart = (RelativeLayout) view.findViewById(R.id.rl_me_cart);
		rl_db_list = (RelativeLayout) view.findViewById(R.id.rl_win_list);
		ll_my_profile = (LinearLayout) view.findViewById(R.id.ll_my_profile);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_exchanged = (TextView) view.findViewById(R.id.tv_exchanged);
		tv_level = (TextView) view.findViewById(R.id.tv_level);
		tv_sign_times = (TextView) view.findViewById(R.id.tv_sign_day);
		tv_integral_total = (TextView) view.findViewById(R.id.tv_integral_total);
		tv_integral_usable = (TextView) view.findViewById(R.id.tv_integral_usable);
		tv_cart_num = (TextView) view.findViewById(R.id.tv_cart_num);
		tv_duobao_yuer = (TextView) view.findViewById(R.id.tv_duobao_yuer);
		rl_more = (RelativeLayout) view.findViewById(R.id.rl_more);
		
		intent = new Intent();
		
		pref = this.getActivity().getSharedPreferences(Constant.STUDENTS_EARN,FragmentActivity.MODE_PRIVATE);
		editor = pref.edit();
		
		rl_cart.setOnClickListener(this);
		rl_db_list.setOnClickListener(this);
		ll_my_profile.setOnClickListener(this);
		rl_more.setOnClickListener(this);
		
	}
	
	public void initData(){	
		tv_title.setText("赚号："+pref.getString(Constant.APPID, null));
		String cart = pref.getString(Constant.DB_CART_NUM, "");
		if(cart !=null && !cart.equals("")){
			tv_cart_num.setVisibility(View.VISIBLE);
			tv_cart_num.setText(cart.split(",").length+"");
		}else{
			tv_cart_num.setVisibility(View.GONE);
		}
		
		RequestParams param = new RequestParams();
		param.put("id", pref.getString(Constant.APPID, "0"));
		
		HttpUtil.get(Constant.USER_INFO_URL, param, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(!response.getString("code").equals("0")){
						tv_sign_times.setText(response.getString("sign_cont"));
						tv_level.setText(response.getString("grade_cont"));
						
						double integraled = (double) (Long.parseLong(response.getString("integraled"))/10000.0);//已兑换积分
						double integral = (double) (Long.parseLong(response.getString("integral"))/10000.0);//可用积分
						double integralTotal = (double) ((Long.parseLong(response.getString("integraled"))+Long.parseLong(response.getString("integral")))/10000.0);//累计已赚
						
						editor.putInt(Constant.SCORE, response.getInt("integral"));
						editor.commit();
						
						DecimalFormat df = new DecimalFormat("0.00");//格式化小数
						df.setRoundingMode(RoundingMode.DOWN);
						String money1 = df.format(integraled/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
						String money2 = df.format(integral/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
						String money3 = df.format(integralTotal/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
						
						tv_exchanged.setText(money1+"元");
						tv_integral_usable.setText(money2+"元");
						tv_integral_total.setText(money3+"元");
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
		
		RequestParams param1 = new RequestParams();
		param1.put("app_id", pref.getString(Constant.APPID, "0"));
		
		HttpUtil.get(Constant.DB_USER_INFO_URL, param1, new JsonHttpResponseHandler() {
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
							tv_duobao_yuer.setText("（余额："+money+"元）");
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_my_profile:
			intent.setClass(this.getActivity(), RecordActivity.class);
			intent.putExtra("ITEM", 1);
			startActivity(intent);
			break;
		case R.id.rl_me_cart:
			Intent intent = new Intent();
			intent.setClass(this.getActivity(), CartActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_win_list:
			Intent intent1 = new Intent();
			intent1.setClass(this.getActivity(), DuoBaoListActivity.class);
			startActivity(intent1);
			break;
		case R.id.rl_more:
			Intent intent2 = new Intent();
			intent2.setClass(this.getActivity(), MoreActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}
}