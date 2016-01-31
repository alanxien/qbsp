/* 
 * @Title:  GoodsAdapter.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-14 下午8:33:45 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao.adapter;

import java.text.NumberFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.ReleaseBitmap;
import com.chuannuo.qianbaosuoping.duobao.model.Goods;
import com.nostra13.universalimageloader.core.ImageLoader;

/** 
 * 商品列表 
 * @author  xie.xin 
 * @data:  2016-1-14 下午8:33:45 
 * @version:  V1.0 
 */
public class GoodsAdapter extends BaseAdapter {

	private List<Goods> list;
	private Context context;
	LayoutInflater la;
	NumberFormat numberFormat;
	ReleaseBitmap releaseBitmap;
	DBOnClickListener listener;
	
	public GoodsAdapter(Context context,List<Goods> list,ReleaseBitmap releaseBitmap,DBOnClickListener listener){
		this.list = list;
		this.context = context;
		this.releaseBitmap = releaseBitmap;
		this.listener = listener;
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
			convertView = la.inflate(R.layout.db_goods_item, null);
			
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.schedule = (TextView) convertView.findViewById(R.id.tv_schedule);
			holder.duobao = (TextView) convertView.findViewById(R.id.tv_cart);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final Goods g = list.get(position);
		holder.title.setText(g.getTitle());
		ImageLoader.getInstance().displayImage(g.getPic(),holder.pic);
		int result = g.getPayMoney()/g.getTotalMoney()*100;
		holder.progressBar.setProgress(result);
		holder.schedule.setText(Html.fromHtml(context.getResources().getString(R.string.schedule,
				result+"%")));
		
		holder.duobao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onClick(g);
			}
		});
		return convertView;
	}
	
	class ViewHolder{ 
		public ImageView pic;
		public TextView title;
		public TextView schedule;//开奖进度
		public TextView duobao;//夺宝按钮
		public ProgressBar progressBar;
	}
	
	public interface DBOnClickListener{
		void onClick(Goods g);
	}

}
