package com.chuannuo.qianbaosuoping;

import net.youmi.android.offers.EarnPointsOrderInfo;
import net.youmi.android.offers.EarnPointsOrderList;
import net.youmi.android.offers.PointsReceiver;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class YMPointsReceiver extends PointsReceiver {

	private SharedPreferences pref;
	
	@Override
	protected void onEarnPoints(Context context, EarnPointsOrderList list) {
		if(!list.isEmpty()){
			EarnPointsOrderInfo obj = list.get(0);
			if(null != obj){
				int score = obj.getPoints();
				addIntegral(score,context.getResources().getString(R.string.ym_app)+"-"+obj.getAppName(),context);
			}
		}
	}

	@Override
	protected void onViewPoints(Context arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void addIntegral(int integral,String reason,final Context context){
		RequestParams params = new RequestParams();
		if(pref == null){
			pref = context.getSharedPreferences(Constant.STUDENTS_EARN, Context.MODE_PRIVATE);
		}
		params.put("appid", pref.getString(Constant.APPID, "0"));
		params.put("integral", integral);
		params.put("code", pref.getString(Constant.CODE, ""));
		params.put("reason", reason);
		
		HttpUtil.get(Constant.ADD_INTEGRAL_URL,params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getInt("code") != 1){
						Toast.makeText(context, response.getString("info"), Toast.LENGTH_SHORT).show();
					}else{
						/*
						 * 增加积分成功
						 */
						Toast.makeText(context, context.getResources().getString(R.string.add_score_success), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
				}
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(context, context.getResources().getString(R.string.sys_remind3), Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
}
