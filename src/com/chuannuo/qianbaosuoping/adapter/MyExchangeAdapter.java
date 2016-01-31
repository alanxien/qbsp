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
 * @date 2014-10-14 ����4:57:48
 * @Description: ����һ���¼
 */
public class MyExchangeAdapter extends BaseAdapter {
	
	public static String TAG = "ExchangeAdapter";

	ArrayList<Exchange> infoList;
	Context context;
	LayoutInflater la;
	
	public MyExchangeAdapter(Context context,ArrayList<Exchange> list){
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
			convertView = la.inflate(R.layout.activity_record_exchange_item, null);
			
			holder = new ViewHolder();
			holder.tv_account = (TextView) convertView.findViewById(R.id.tv_ex_account);
			holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_ex_info);
			holder.tv_exchange_time = (TextView) convertView.findViewById(R.id.tv_ex_time);
			holder.tv_integral = (TextView) convertView.findViewById(R.id.tv_ex_integral);
			holder.tv_remark = (TextView) convertView.findViewById(R.id.tv_ex_remark);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_account.setText(this.infoList.get(position).getAccount());
		holder.tv_desc.setText(this.infoList.get(position).getExchangeDesc());
		holder.tv_exchange_time.setText(this.infoList.get(position).getExchangeTime());
		holder.tv_integral.setText(this.infoList.get(position).getIntegral());
		switch (this.infoList.get(position).getStatus()) {
		case 0:		//0����У�
			holder.tv_remark.setText("�����");
			break;
		case 1:		//1���ͨ����
			holder.tv_remark.setText("���ͨ��");		
			break;
		case 2:		//2����У�
			holder.tv_remark.setText("�����");
			break;
		case 3:		//3�Ѵ�
			holder.tv_remark.setText("�Ѵ��");
			break;
		case 4:		//4��ɣ�
			holder.tv_remark.setText("�����");
			break;
		case 5:		//5��˲�ͨ��
			holder.tv_remark.setText(this.infoList.get(position).getRemark());
			break;
		default:
			break;
		}

		return convertView;
	}

	class ViewHolder{ 
		public TextView tv_account;
		public TextView tv_desc;
		public TextView tv_integral;
		public TextView tv_exchange_time;
		public TextView tv_remark;
	}
	
}












