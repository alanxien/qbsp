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
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.duobao.adapter.CanyuzheAdapter;
import com.chuannuo.qianbaosuoping.duobao.adapter.DuoBaoListAdapter;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.chuannuo.qianbaosuoping.duobao.model.Winner;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * TODO<请描述这个类是干什么的>
 * 
 * @author xie.xin
 * @data: 2016-1-19 下午9:12:08
 * @version: V1.0
 */
public class DuoBaoListActivity extends BaseActivity {

	private ListView listView;
	private List<Winner> wList;
	private DuoBaoListAdapter adapter;
	private ProgressBar progressBar;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_activity_duobao_list);

		listView = (ListView) findViewById(R.id.listView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		initData();
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
		wList = new ArrayList<Winner>();
		progressBar.setVisibility(View.VISIBLE);
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));// 39887
		params.put("page", 1);
		HttpUtil.get(Constant.DB_ORDER_LIST_URL, params,
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
									int l = jArray.length();
									for (int i = 0; i < l; i++) {
										JSONObject obj = jArray
												.getJSONObject(i);
										if (obj != null) {
											Winner c = new Winner();
											c.setCount(obj.getInt("count"));
											c.setUserName("赚号："
													+ obj.getInt("app_id"));
											c.setStatus(obj
													.getInt("task_stauts"));
											c.setPic(Constant.ROOT_URL
													+ obj.getString("picture"));
											c.setTitle(obj.getString("title"));
											c.settMoney(obj
													.getInt("total_money"));
											c.setpMoney(obj.getString(
													"pay_money").equals("null") ? 0
													: obj.getInt("pay_money"));
											c.setWinner(obj.getString("winner")
													.equals("null") ? "待定"
													: obj.getString("winner"));
											c.setwCount(obj.getString(
													"winner_count").equals(
													"null") ? 0 : obj
													.getInt("winner_count"));
											c.setLotTime(obj.getString(
													"the_lottery_time").equals(
													"null") ? "待定"
													: obj.getString("the_lottery_time"));
											c.setwNum(obj.getString(
													"winning_number").equals(
													"null") ? "待定"
													: obj.getString("winning_number"));

											JSONArray pjArray = obj
													.getJSONArray("indinana_number_list");
											List<String> list = new ArrayList<String>();
											if (pjArray != null) {
												for (int j = 0; j < pjArray
														.length(); j++) {
													list.add(pjArray.get(j)
															.toString());
												}
												c.setNumList(list);
											}
											wList.add(c);
										}
									}

									msg.what = 1;
									mHandler.sendMessage(msg);
								}
							} else {
								Toast.makeText(DuoBaoListActivity.this,
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
						Toast.makeText(DuoBaoListActivity.this, "数据获取失败，请检查网络",
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
					adapter = new DuoBaoListAdapter(DuoBaoListActivity.this,
							wList);
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
