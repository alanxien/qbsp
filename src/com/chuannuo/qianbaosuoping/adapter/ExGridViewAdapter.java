package com.chuannuo.qianbaosuoping.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.model.Exchange;
import com.chuannuo.qianbaosuoping.model.ExchangeMenu;

/**
 * @author alan.xie
 * @date 2014-10-14 下午4:57:48
 * @Description: 最近兑换记录
 */
public class ExGridViewAdapter extends BaseAdapter {
	
	public static String TAG = "ExchangeAdapter";

	ArrayList<ExchangeMenu> infoList;
	Context context;
	LayoutInflater la;
	
	public ExGridViewAdapter(Context context,ArrayList<ExchangeMenu> list){
		this.context = context;
		this.infoList = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.infoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			la = LayoutInflater.from(context);
			convertView = la.inflate(R.layout.exchange_item, null);
			
			holder = new ViewHolder();
			holder.mIvlogo = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.mTvName = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mIvlogo.setImageResource(this.infoList.get(position).getImageId());
		holder.mTvName.setText(this.infoList.get(position).getName());


		return convertView;
	}

	class ViewHolder{ 
		public ImageView mIvlogo;
		public TextView mTvName;
	}
	
}












