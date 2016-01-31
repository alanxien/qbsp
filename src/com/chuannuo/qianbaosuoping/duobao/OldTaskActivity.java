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
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.duobao.adapter.CanyuzheAdapter;
import com.chuannuo.qianbaosuoping.duobao.adapter.OldTaskAdapter;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.chuannuo.qianbaosuoping.duobao.model.OldTask;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-19 下午9:12:08 
 * @version:  V1.0 
 */
public class OldTaskActivity extends BaseActivity{
	
	private ListView listView;
	private List<OldTask> list;
	private OldTaskAdapter adapter;
	private ProgressBar progressBar;
	private int gId;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_activity_old_task);
		
		listView = (ListView) findViewById(R.id.listView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		gId = getIntent().getIntExtra("g_id", 0);
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
		progressBar.setVisibility(View.VISIBLE);
		list = new ArrayList<OldTask>();
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));// 39887
		params.put("page", 1);
		params.put("g_id", gId);
		HttpUtil.get(Constant.DB_INDIANAT_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								JSONArray jArray = response
										.getJSONArray("data");
								if(jArray != null && jArray.length() >0){
									Message msg = mHandler.obtainMessage();
									JSONObject obj = jArray.getJSONObject(0);
									if (obj != null) {
										OldTask c = new OldTask();
										c.setCount(obj.getInt("count"));
										c.setWinNumber(obj.getString("winning_number"));
										c.setAppId(obj.getInt("app_id"));
										c.setLotTime(obj.getString("the_lottery_time"));

										list.add(c);
									}
								msg.what = 1;
								mHandler.sendMessage(msg);
								}
							} else {
								Toast.makeText(
										OldTaskActivity.this,
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
					adapter = new OldTaskAdapter(OldTaskActivity.this,
							list);
				} else {
					adapter.notifyDataSetChanged();
				}
				listView.setAdapter(adapter);
				break;

			default:
				break;
			}
		};
	};
}
