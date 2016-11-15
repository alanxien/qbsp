package com.chuannuo.qianbaosuoping.movie;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.duobao.model.Movie;
import com.chuannuo.qianbaosuoping.duobao.model.MovieDetail;
import com.chuannuo.qianbaosuoping.hScollView.NewsFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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
	private TextView tvMoviePlot;
	
	
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
		tvMoviePlot = (TextView) findViewById(R.id.movie_plot);
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
		RequestParams params = new RequestParams();
		params.put("id", movie.getId());
		HttpUtil.get(Constant.GET_MOVIE_INFO, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								JSONObject data = response.getJSONObject("data");
								if(data!=null && !data.equals("[]")){
									MovieDetail md = new MovieDetail();
									md.setAlias(data.getString("alias"));
									md.setArea(data.getString("area"));
									md.setPlot(data.getString("plot"));
									md.setCreate_date(data.getString("create_date"));
									md.setDirector(data.getString("director"));
									md.setPerformer(data.getString("performer"));
									md.setStatus(data.getString("status"));
									md.setSubtitles(data.getString("subtitles"));
									md.setType(data.getString("type"));
									md.setVersion(data.getString("version"));
									
									JSONArray jaBaidu = data.getJSONArray("film_download_link_baidu");
									JSONArray jaXunlei = data.getJSONArray("film_download_link_xunlei");
									if(jaBaidu!=null && jaBaidu.length()>0){
										JSONObject baidu = jaBaidu.getJSONObject(0);
										md.setBaiduLink(baidu.getString("link"));
										md.setBaiduTitle(baidu.getString("title"));
										md.setBaiduPassword(baidu.getString("password"));
									}
									
									if(jaXunlei!=null && jaXunlei.length()>0){
										JSONObject xunlei = jaXunlei.getJSONObject(0);
										md.setXunleiLink(xunlei.getString("link"));
										md.setXunleiTitle(xunlei.getString("title"));
									}
									movie.setMovieDetail(md);
									handler.sendEmptyMessage(1);
								}

							} else {
								Toast.makeText(MovieActivity.this,
										response.getString("info"),
										Toast.LENGTH_SHORT).show();
							}
							super.onSuccess(statusCode, headers, response);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		movie = null;
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				MovieDetail m = movie.getMovieDetail();
				tvMovieAlias.setText(getResources().getString(R.string.movie_alias, m.getAlias()));
				tvMovieArea.setText(getResources().getString(R.string.movie_area,m.getArea()));
				tvMovieDate.setText(getResources().getString(R.string.movie_date,m.getCreate_date()));
				tvMovieDirector.setText(getResources().getString(R.string.movie_director,m.getDirector()));
				tvMoviePerformer.setText(getResources().getString(R.string.movie_performer,m.getPerformer()));
				tvMovieSubtitles.setText(getResources().getString(R.string.movie_subtitles,m.getSubtitles()));
				tvMovieType.setText(getResources().getString(R.string.movie_type,m.getType()));
				if(m.getPlot()==null || m.getPlot().isEmpty()){
					tvMoviePlot.setVisibility(View.GONE);
				}else{
					tvMoviePlot.setVisibility(View.VISIBLE);
					tvMoviePlot.setText(getResources().getString(R.string.movie_plot,m.getPlot()));
				}
				break;

			default:
				break;
			}
		};
	};

}













