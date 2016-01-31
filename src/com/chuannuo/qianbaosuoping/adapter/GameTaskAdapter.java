package com.chuannuo.qianbaosuoping.adapter;

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
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GameTaskAdapter extends BaseAdapter {
	
	public static String TAG = "RecommendAdapter";

	ArrayList<AppInfo> infoList;
	Context context;
	LayoutInflater la;
	String appName;
	
	public GameTaskAdapter(Context context,ArrayList<AppInfo> list){
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
			convertView = la.inflate(R.layout.recommend_item, null);
			
			holder = new ViewHolder();
			holder.app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
			holder.app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
			holder.app_desc = (TextView) convertView.findViewById(R.id.tv_app_desc);
			holder.app_xb = (TextView) convertView.findViewById(R.id.tv_xb);
			holder.app_size = (TextView) convertView.findViewById(R.id.tv_app_size);
			holder.is_share = (ImageView) convertView.findViewById(R.id.iv_icon_share);
			holder.tv_friend_share = (TextView) convertView.findViewById(R.id.tv_friend_share);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
			holder.app_name.setText(this.infoList.get(position).getTitle());
			ImageLoader.getInstance().displayImage(this.infoList.get(position).getIcon(),holder.app_icon);
			holder.app_desc.setText(this.infoList.get(position).getBrief());
			holder.app_desc.setTextColor(context.getResources().getColor(R.color.GreenThem));
			holder.app_size.setText(this.infoList.get(position).getResource_size()+"M");

			holder.app_xb.setVisibility(View.GONE);
			holder.is_share.setVisibility(View.GONE);
			holder.tv_friend_share.setVisibility(View.GONE);
		
		return convertView;
	}

	class ViewHolder{ 
		public ImageView app_icon;
		public TextView app_name;
		public TextView app_desc;
		public TextView app_xb;
		public TextView app_size;
		public ImageView is_share;
		public TextView tv_friend_share;
	}
	
}












