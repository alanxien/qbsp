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
import android.content.Intent;
import android.sax.StartElementListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.duobao.MyNumberActivity;
import com.chuannuo.qianbaosuoping.duobao.model.Winner;
import com.nostra13.universalimageloader.core.ImageLoader;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-18 下午10:00:21 
 * @version:  V1.0 
 */
public class DuoBaoListAdapter extends BaseAdapter {

	private List<Winner> list;
	private Context context;
	LayoutInflater la;
	
	public DuoBaoListAdapter(Context context,List<Winner> list){
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
			convertView = la.inflate(R.layout.db_duobao_list_item, null);
			
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tv_account = (TextView) convertView.findViewById(R.id.tv_account);
			holder.tv_my_num = (TextView) convertView.findViewById(R.id.tv_my_num);
			holder.tv_qihao = (TextView) convertView.findViewById(R.id.tv_qihao);
			holder.tv_t_moneye = (TextView) convertView.findViewById(R.id.tv_t_money);
			holder.tv_win_num = (TextView) convertView.findViewById(R.id.tv_win_num);
			holder.tv_winner = (TextView) convertView.findViewById(R.id.tv_winner);
			holder.tv_winner_count = (TextView) convertView.findViewById(R.id.tv_winner_count);
			holder.tv_r_money = (TextView) convertView.findViewById(R.id.tv_r_money);
			convertView.setTag(holder); 
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final Winner c = list.get(position);
		ImageLoader.getInstance().displayImage(c.getPic(), holder.iv_pic);
		holder.title.setText(c.getTitle());
		holder.tv_account.setText(Html.fromHtml(context.getResources().getString(R.string.db_count,
				c.getCount())));
		holder.tv_qihao.setText("参与期号："+c.getTaskId());
		holder.tv_t_moneye.setText("总需"+c.gettMoney()+"人次");
		holder.tv_r_money.setText(Html.fromHtml(context.getResources().getString(
				R.string.r_money, c.gettMoney() - c.getpMoney()<=0?"0":c.gettMoney() - c.getpMoney() + "")));
		if(c.getStatus() == 2){
			holder.tv_winner_count.setVisibility(View.VISIBLE);
			holder.tv_winner_count.setText(Html.fromHtml(context.getResources().getString(R.string.db_count,
					c.getwCount())));
			
			holder.tv_time.setText("揭晓时间 ："+c.getLotTime());
			holder.tv_time.setTextColor(context.getResources().getColor(R.color.grey));
			
			holder.tv_win_num.setVisibility(View.VISIBLE);
			holder.tv_winner.setVisibility(View.VISIBLE);
			holder.tv_win_num.setText(Html.fromHtml(context.getResources().getString(R.string.db_win_num,
					c.getwNum())));
			if(c.getNumList().contains(c.getwNum())){
				holder.tv_winner.setText("恭喜您中奖！");
				holder.tv_winner.setTextColor(context.getResources().getColor(R.color.RedTheme));
			}else{
				holder.tv_winner.setTextColor(context.getResources().getColor(R.color.grey));
				holder.tv_winner.setText(Html.fromHtml(context.getResources().getString(R.string.db_winner,
						c.getWinner())));
			}
			
		}else{
			holder.tv_winner_count.setVisibility(View.GONE);
			holder.tv_win_num.setVisibility(View.GONE);
			holder.tv_winner.setVisibility(View.GONE);
			holder.tv_time.setText("火热进行中...");
			holder.tv_time.setTextColor(context.getResources().getColor(R.color.RedTheme));
		}
		
		
		
		holder.tv_my_num.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MyNumberActivity.class);
				intent.putExtra("data", c);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
	
	class ViewHolder{ 
		public TextView title;
		public TextView tv_qihao;
		public TextView tv_t_moneye;
		public TextView tv_account;
		public TextView tv_my_num;
		public TextView tv_winner;
		public TextView tv_winner_count;
		public TextView tv_win_num;
		public TextView tv_time;
		public ImageView iv_pic;
		public TextView tv_r_money;
	}
	
	public boolean useLoop(String[] arr, String targetValue) {
	    for(String s: arr){
	        if(s.equals(targetValue))
	            return true;
	    }
	    return false;
	}

}

