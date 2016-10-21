/* 
 * @Title:  OldTaskActivity.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-19 下午9:12:08 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.R.color;
import com.chuannuo.qianbaosuoping.duobao.model.Winner;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-19 下午9:12:08 
 * @version:  V1.0 
 */
public class MyNumberActivity extends BaseActivity{
	
	private TextView tv_goods_title;
	private TextView tv_qihao;
	private TextView tv_count;
	private TextView tv_my_num;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_activity_my_number);
		
		tv_goods_title = (TextView) findViewById(R.id.tv_goods_title);
		tv_qihao = (TextView) findViewById(R.id.tv_qihao);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_my_num = (TextView) findViewById(R.id.tv_my_num);
		
		Winner w = (Winner) getIntent().getSerializableExtra("data");
		if(w != null){
			tv_goods_title.setText(w.getTitle());
			tv_qihao.setText("期号："+w.getTaskId());
			tv_count.setText(Html.fromHtml(getResources().getString(R.string.db_my_num_tips,
				w.getCount())));
			List<String> list = w.getNumList();
			if(list!=null && list.size()>0){
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<list.size();i++){
					sb.append(list.get(i));
					sb.append(" ");
				}
				int startIndex = sb.toString().indexOf(w.getwNum());
				if(startIndex>-1){
					SpannableString s = new SpannableString(sb.toString());
					s.setSpan(new ForegroundColorSpan(Color.parseColor("#ef4136")), startIndex, w.getwNum().length()+startIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					tv_my_num.setText(s);
				}else{
					tv_my_num.setText(sb.toString());
				}
			}
		}
		
	}

}
