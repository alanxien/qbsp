package com.chuannuo.qianbaosuoping.movie;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.duobao.model.Movie;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 电影
 */
public class MovieActivity extends BaseActivity implements OnClickListener{
	
	private Movie movie;
	private ImageView ivMovieLogo;
	private TextView tvMovieTitle;
	private TextView tvMovieAlias;
	private TextView tvMovieType;
	private TextView tvMovieDirector;
	private TextView tvMoviePerformer;
	private TextView tvMovieArea;
	private TextView tvMovieSubtitles;
	private TextView tvMovieDate;
	
	
	/** 屏幕宽度 */
	private int mScreenWidth = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie);
		movie = (Movie) getIntent().getSerializableExtra("movie");
		if(movie==null){
			movie = new Movie();
		}
		
		/*
	     * 横向滑动tab
	     */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		
		initView();
		initData();
	}


	private void initView() {
		// TODO Auto-generated method stub
		ivMovieLogo = (ImageView) findViewById(R.id.am_iv_logo);
		tvMovieAlias = (TextView) findViewById(R.id.movie_alias);
		tvMovieArea = (TextView) findViewById(R.id.movie_area);
		tvMovieDate = (TextView) findViewById(R.id.movie_date);
		tvMovieDirector = (TextView) findViewById(R.id.movie_director);
		tvMoviePerformer = (TextView) findViewById(R.id.movie_performer);
		tvMovieSubtitles = (TextView) findViewById(R.id.movie_subtitles);
		tvMovieTitle = (TextView) findViewById(R.id.movie_title);
		tvMovieType = (TextView) findViewById(R.id.movie_type);
	}

	private void initData() {
		// TODO Auto-generated method stub
		int logoWidth = mScreenWidth*2/5;
		int logoHeight = logoWidth*10/7;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(logoWidth,logoHeight);
		ivMovieLogo.setLayoutParams(lp);
		ImageLoader.getInstance().displayImage(movie.getIcon(), ivMovieLogo);
		tvMovieTitle.setText(movie.getTitle());
		
		getMovieInfo();
	}
	
	private void getMovieInfo() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		default:
			break;
		}
	}

}













