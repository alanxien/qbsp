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
import com.chuannuo.qianbaosuoping.model.Task;

/**
 * @author alan.xie
 * @date 2014-10-14 下午4:57:48
 * @Description: 任务记录
 */
public class MyTaskAdapter extends BaseAdapter {
	
	public static String TAG = "ExchangeAdapter";

	ArrayList<Task> infoList;
	Context context;
	LayoutInflater la;
	
	public MyTaskAdapter(Context context,ArrayList<Task> list){
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
			convertView = la.inflate(R.layout.activity_record_task_item, null);
			
			holder = new ViewHolder();
			holder.tv_task_info = (TextView) convertView.findViewById(R.id.tv_task_info);
			holder.tv_task_integral = (TextView) convertView.findViewById(R.id.tv_task_integral);
			holder.tv_finish_time = (TextView) convertView.findViewById(R.id.tv_finish_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_task_info.setText(this.infoList.get(position).getTaskConten());
		holder.tv_task_integral.setText(this.infoList.get(position).getIntegral());
		holder.tv_finish_time.setText(this.infoList.get(position).getFinishTime());


		return convertView;
	}

	class ViewHolder{ 
		public TextView tv_task_info;
		public TextView tv_task_integral;
		public TextView tv_finish_time;
	}
	
}












