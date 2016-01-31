/* 
 * @Title:  CanyuzheAdapter.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-18 下午10:00:21 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.duobao.model.OldTask;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-18 下午10:00:21 
 * @version:  V1.0 
 */
public class OldTaskAdapter extends BaseAdapter {

	private List<OldTask> list;
	private Context context;
	LayoutInflater la;
	
	public OldTaskAdapter(Context context,List<OldTask> list){
		this.list = list;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			la = LayoutInflater.from(context);
			convertView = la.inflate(R.layout.db_oldtask_item, null);
			
			holder = new ViewHolder();
			holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_winner = (TextView) convertView.findViewById(R.id.tv_winner);
			holder.tv_winNum= (TextView) convertView.findViewById(R.id.tv_winNum);
			convertView.setTag(holder); 
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		OldTask o = list.get(position);
		holder.tv_count.setText(o.getCount()+"");
		holder.tv_time.setText("揭晓时间 :"+o.getLotTime());
		holder.tv_winner.setText(o.getAppId()+"");
		holder.tv_winNum.setText(o.getWinNumber());
		return convertView;
	}
	
	class ViewHolder{ 
		public TextView tv_time;
		public TextView tv_winner;
		public TextView tv_winNum;
		public TextView tv_count;
	}

}

