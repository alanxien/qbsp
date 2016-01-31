/* 
 * @Title:  CanyuzheAdapter.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-18 下午10:00:21 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao.adapter;

import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.nostra13.universalimageloader.core.ImageLoader;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-18 下午10:00:21 
 * @version:  V1.0 
 */
public class PicTextAdapter extends BaseAdapter {

	private List<String> list;
	private Context context;
	LayoutInflater la;
	
	public PicTextAdapter(Context context,List<String> list){
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
			convertView = la.inflate(R.layout.db_pic_text_item, null);
			
			holder = new ViewHolder();
			holder.pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			convertView.setTag(holder); 
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		ImageLoader.getInstance().displayImage(list.get(position), holder.pic);
		return convertView;
	}
	
	class ViewHolder{ 
		public ImageView pic;
	}

}

