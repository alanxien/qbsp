package com.chuannuo.qianbaosuoping.adapter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.model.ShareApp;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShareTaskAdapter extends BaseAdapter {
	
	public static String TAG = "RecommendAdapter";

	ArrayList<ShareApp> infoList;
	Context context;
	LayoutInflater la;
	String appName;
	
	public ShareTaskAdapter(Context context,ArrayList<ShareApp> list){
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
			convertView = la.inflate(R.layout.share_item, null);
			
			holder = new ViewHolder();
			holder.app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
			holder.app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
			holder.app_desc = (TextView) convertView.findViewById(R.id.tv_app_desc);
			holder.share_count = (TextView) convertView.findViewById(R.id.tv_share_count);
			holder.share_score = (TextView) convertView.findViewById(R.id.tv_score);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
			holder.app_name.setText(this.infoList.get(position).getTitle());
			ImageLoader.getInstance().displayImage(this.infoList.get(position).getIcon(),holder.app_icon);
			holder.app_desc.setText(this.infoList.get(position).getDesc());
			holder.share_count.setText(this.infoList.get(position).getShare_count()+"");
			float integral = (float) ((long)this.infoList.get(position).getShare_integral()*2/10000.0);//可用积分
			DecimalFormat df = new DecimalFormat("0.00");//格式化小数
			df.setRoundingMode(RoundingMode.DOWN);
			String money = df.format(integral/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
			holder.share_score.setText("+"+money+"元");
			return convertView;
	}

	class ViewHolder{ 
		public ImageView app_icon;
		public TextView app_name;
		public TextView app_desc;
		public TextView share_count;
		public TextView share_score;
	}
	
}












