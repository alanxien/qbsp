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
import com.chuannuo.qianbaosuoping.model.Invitation;

/**
 * @author alan.xie
 * @date 2014-10-14 ÏÂÎç4:57:48
 * @Description: ÑûÇë¼ÇÂ¼
 */
public class MyInvitationAdapter extends BaseAdapter {
	
	public static String TAG = "ExchangeAdapter";

	ArrayList<Invitation> infoList;
	Context context;
	LayoutInflater la;
	
	public MyInvitationAdapter(Context context,ArrayList<Invitation> list){
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
			convertView = la.inflate(R.layout.activity_record_invitation_item, null);
			
			holder = new ViewHolder();
			holder.tv_inv_id = (TextView) convertView.findViewById(R.id.tv_inv_id);
			holder.tv_inv_integral = (TextView) convertView.findViewById(R.id.tv_inv_integral);
			holder.tv_inv_time = (TextView) convertView.findViewById(R.id.tv_inv_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_inv_id.setText(this.infoList.get(position).getInvId());
		holder.tv_inv_time.setText(this.infoList.get(position).getInvTime());
		holder.tv_inv_integral.setText(this.infoList.get(position).getIntegral());


		return convertView;
	}

	class ViewHolder{ 
		public TextView tv_inv_id;
		public TextView tv_inv_integral;
		public TextView tv_inv_time;
	}
	
}












