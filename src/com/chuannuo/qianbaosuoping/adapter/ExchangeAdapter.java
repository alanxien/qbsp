package com.chuannuo.qianbaosuoping.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.model.Exchange;

/**
 * @author alan.xie
 * @date 2014-10-14 下午4:57:48
 * @Description: 最近兑换记录
 */
public class ExchangeAdapter extends BaseAdapter {
	
	public static String TAG = "ExchangeAdapter";

	ArrayList<Exchange> infoList;
	Context context;
	LayoutInflater la;
	
	public ExchangeAdapter(Context context,ArrayList<Exchange> list){
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
			convertView = la.inflate(R.layout.new_exchanged_item, null);
			
			holder = new ViewHolder();
			holder.tv_account = (TextView) convertView.findViewById(R.id.tv_account);
			holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
			holder.tv_exchange_time = (TextView) convertView.findViewById(R.id.tv_exchange_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_account.setText("赚号:"+this.infoList.get(position).getAccount());
		holder.tv_desc.setText(this.infoList.get(position).getExchangeDesc());
		holder.tv_exchange_time.setText(this.infoList.get(position).getExchangeTime());


		return convertView;
	}

	class ViewHolder{ 
		public TextView tv_account;
		public TextView tv_desc;
		public TextView tv_exchange_time;
	}
	
}












