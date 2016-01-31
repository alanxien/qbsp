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
import com.chuannuo.qianbaosuoping.duobao.adapter.PicTextAdapter;
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
 * 图文详情
 * @author  xie.xin 
 * @data:  2016-1-19 下午9:12:08 
 * @version:  V1.0 
 */
public class PicTextDetailActivity extends BaseActivity{
	
	private List<String> picList;
	private PicTextAdapter adapter;
	private ListView listView;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_activity_pic_list);
		
		listView = (ListView) findViewById(R.id.picListView);
		picList = getIntent().getStringArrayListExtra("picList");
		if(picList !=null && picList.size()>0){
			adapter = new PicTextAdapter(this, picList);
			listView.setAdapter(adapter);
		}
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
		
	}
}
