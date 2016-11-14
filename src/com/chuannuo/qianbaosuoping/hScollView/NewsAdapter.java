package com.chuannuo.qianbaosuoping.hScollView;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.duobao.model.Movie;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class NewsAdapter extends BaseAdapter{
	ArrayList<Movie> newsList;
	Activity activity;
	LayoutInflater inflater = null;
	int mScreenWidth;
	int mScreenHeight;
	
	public NewsAdapter(Activity activity, ArrayList<Movie> newsList) {
		this.activity = activity;
		this.newsList = newsList;
		inflater = LayoutInflater.from(activity);
		
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.widthPixels;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return newsList == null ? 0 : newsList.size();
	}

	@Override
	public Movie getItem(int position) {
		// TODO Auto-generated method stub
		if (newsList != null && newsList.size() != 0) {
			return newsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.movie_item, null);
			mHolder = new ViewHolder();
			mHolder.ivLogo = (ImageView) view.findViewById(R.id.mi_iv_logo);
			mHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
			
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		Movie movie = newsList.get(position);
		ImageLoader.getInstance().displayImage(movie.getIcon(),mHolder.ivLogo);
		mHolder.tvTitle.setText(movie.getAlias());
		
		return view;
	}

	static class ViewHolder {
		public ImageView ivLogo;
		public TextView tvTitle;
	}
}
