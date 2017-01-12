package com.chuannuo.qianbaosuoping.movie;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.chuannuo.qianbaosuoping.AccountSettingActivity;
import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.PhoneInformation;
import com.chuannuo.qianbaosuoping.duobao.model.AiqiyiModel;
import com.chuannuo.qianbaosuoping.duobao.model.BaiduModel;
import com.chuannuo.qianbaosuoping.duobao.model.Movie;
import com.chuannuo.qianbaosuoping.duobao.model.MovieDetail;
import com.chuannuo.qianbaosuoping.duobao.model.XunleiModel;
import com.chuannuo.qianbaosuoping.hScollView.NewsFragment;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 电影
 */
public class MovieActivity extends BaseActivity implements OnClickListener {

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
	private RelativeLayout rlPlot;
	private ImageView ivPlot;

	private LinearLayout llBaidu;
	private LinearLayout llXunlei;
	private LinearLayout llAiqiyi;

	private TextView tvBdLink;
	private TextView tvXlLink;
	private TextView tvAqyLink;
	private TextView tvBdPasswrod;

	private ListView listView;
	private List<Comments> list;
	private CommentsAdapter adapter;
	String comment;
	private CustomDialog mDialog;
	private TextView tvComment;
	
	private ListView bListView;
	private ListView xListView;
	private ListView aListView;
	private BaiduAdapter bLinksAdapter;
	private XunleiAdapter xLinksAdapter;
	private AiqiyiAdapter aLinksAdapter;

	/** 屏幕宽度 */
	private int mScreenWidth = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie);
		movie = (Movie) getIntent().getSerializableExtra("movie");
		if (movie == null) {
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
		
		mDialog = new CustomDialog(MovieActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.btn_left:
					mDialog.getEtComment().setText("");
					mDialog.dismiss();
					break;
				case R.id.btn_right:
					String text = mDialog.getEtComment().getText().toString();
					if(text.isEmpty()){
						Toast.makeText(MovieActivity.this, "评论不能为空", Toast.LENGTH_SHORT).show();
					}else{
						comments(text);
					}
					break;
				default:
					break;
				}
				
			}
		}, 7);
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
		rlPlot = (RelativeLayout) findViewById(R.id.rl_plot);
		ivPlot = (ImageView) findViewById(R.id.iv_plot);
		
		
		listView = (ListView) findViewById(R.id.lv_comments);
		tvComment = (TextView) findViewById(R.id.tv_comment);

		llBaidu = (LinearLayout) findViewById(R.id.movie_ll_baidu);
		llAiqiyi = (LinearLayout) findViewById(R.id.movie_ll_aiqiyi);
		llXunlei = (LinearLayout) findViewById(R.id.movie_ll_xunlei);

		bListView = (ListView) findViewById(R.id.lv_baidu);
		xListView = (ListView) findViewById(R.id.lv_xunlei);
		aListView = (ListView) findViewById(R.id.lv_aiqiyi);

		rlPlot.setOnClickListener(this);
		tvComment.setOnClickListener(this);

		list = new ArrayList<Comments>();
		adapter = new CommentsAdapter(this, list);
		listView.setAdapter(adapter);
		fixListViewHeight(listView);
	}

	private void initData() {
		// TODO Auto-generated method stub
		int logoWidth = mScreenWidth * 2 / 5;
		int logoHeight = logoWidth * 10 / 7;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				logoWidth, logoHeight);
		ivMovieLogo.setLayoutParams(lp);
		ImageLoader.getInstance().displayImage(movie.getIcon(), ivMovieLogo);
		tvMovieTitle.setText(movie.getTitle());

		getMovieInfo();
		getCommentsList();
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
								JSONObject data = response
										.getJSONObject("data");
								if (data != null && !data.equals("[]")) {
									MovieDetail md = new MovieDetail();
									md.setAlias(data.getString("alias"));
									md.setArea(data.getString("area"));
									md.setPlot(data.getString("plot"));
									md.setCreate_date(data
											.getString("create_date"));
									md.setDirector(data.getString("director"));
									md.setPerformer(data.getString("performer"));
									md.setStatus(data.getString("status"));
									md.setSubtitles(data.getString("subtitles"));
									md.setType(data.getString("type"));
									md.setVersion(data.getString("version"));

									JSONArray jaBaidu = data
											.getJSONArray("film_download_link_baidu");
									JSONArray jaXunlei = data
											.getJSONArray("film_download_link_xunlei");
									JSONArray jaAiqiyi = data
											.getJSONArray("film_download_link_shiping");
									
									int bSize = jaBaidu.length();
									int xSize = jaXunlei.length();
									int aSize = jaAiqiyi.length();
									
									if (jaBaidu != null &&  bSize> 0) {
										List<BaiduModel> list = new ArrayList<BaiduModel>();
										BaiduModel b ;
										JSONObject baidu;
										for(int i=0;i<bSize; i++){
											baidu = jaBaidu
													.getJSONObject(i);
											if(baidu!=null){
												b = new BaiduModel();
												b.setType(1);
												b.setLink(baidu.getString("link"));
												b.setTitle(baidu.getString("title"));
												b.setPassword(baidu.getString("password"));
												list.add(b);
											}
										}
										md.setBaiduModel(list);
									}

									if (jaXunlei != null
											&& xSize > 0) {
										
										List<XunleiModel> list = new ArrayList<XunleiModel>();
										XunleiModel x ;
										JSONObject xunlei;
										for(int i=0;i<xSize; i++){
											xunlei = jaXunlei
													.getJSONObject(i);
											if(xunlei!=null){
												x = new XunleiModel();
												x.setType(2);
												x.setLink(xunlei.getString("link"));
												x.setTitle(xunlei.getString("title"));
												x.setPassword("");
												list.add(x);
											}
										}
										md.setXunleiModel(list);
									}

									if (jaAiqiyi != null
											&& aSize > 0) {
										
										List<AiqiyiModel> list = new ArrayList<AiqiyiModel>();
										AiqiyiModel a;
										JSONObject aiqiyi;
										for(int i=0;i<aSize; i++){
											aiqiyi = jaAiqiyi
													.getJSONObject(i);
											if(aiqiyi!=null){
												a = new AiqiyiModel();
												a.setType(3);
												a.setLink(aiqiyi.getString("link"));
												a.setTitle(aiqiyi.getString("title"));
												a.setPassword("");
												list.add(a);
											}
										}
										md.setAiqiyiModel(list);
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

	private void getCommentsList() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("m_id", movie.getId());
		HttpUtil.get(Constant.GET_MOVIE_COMMENT_LIST, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								JSONArray jsonArry = response
										.getJSONArray("data");
								if (jsonArry != null && !jsonArry.equals("[]")) {
									int size = jsonArry.length();
									if (size > 0) {
										JSONObject obj = null;
										for (int i = 0; i < size; i++) {
											obj = jsonArry.getJSONObject(i);
											Comments c = new Comments();
											c.setAppId(obj.getString("app_id"));
											c.setComment(obj
													.getString("comment"));
											c.setCreateDate(obj
													.getString("create_date"));
											c.setId(obj.getInt("id"));

											list.add(c);
										}
										adapter.notifyDataSetChanged();
									}
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

	private void comments(final String comment) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("m_id", movie.getId());
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("comment", comment);
		HttpUtil.get(Constant.POST_MOVIE_COMMENT, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								if(mDialog!=null) mDialog.dismiss();
								Toast.makeText(MovieActivity.this, "评论成功",
										Toast.LENGTH_SHORT).show();

								Comments c = new Comments();
								c.setAppId(pref.getString(Constant.APPID, "0"));
								c.setComment(comment);
								c.setCreateDate(getNowDate());
								c.setId(movie.getId());

								list.add(c);
								adapter.notifyDataSetChanged();
							} else {
								Toast.makeText(MovieActivity.this, "评论失败",
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

		case R.id.rl_plot:
			if (rlPlot.getTag().equals(1)) {
				rlPlot.setTag(2);
				ivPlot.setImageResource(R.drawable.quickaction_arrow_up);
				tvMoviePlot.setMaxLines(100);
			} else {
				rlPlot.setTag(1);
				ivPlot.setImageResource(R.drawable.quickaction_arrow_down);
				tvMoviePlot.setMaxLines(3);
			}
			break;
		case R.id.tv_comment:
			if(mDialog!=null){
				mDialog.show();
				WindowManager windowManager = getWindowManager();
				Display display = windowManager.getDefaultDisplay();
				WindowManager.LayoutParams lp = mDialog.getWindow()
						.getAttributes();
				lp.width = (int) (display.getWidth() * 0.9); // 设置宽度
				mDialog.getWindow().setAttributes(lp);
			}
			
			break;
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

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				MovieDetail m = movie.getMovieDetail();

				tvMovieAlias.setText(getResources().getString(
						R.string.movie_alias, m.getAlias()));
				tvMovieArea.setText(getResources().getString(
						R.string.movie_area, m.getArea()));
				tvMovieDate.setText(getResources().getString(
						R.string.movie_date, m.getCreate_date()));
				tvMovieDirector.setText(getResources().getString(
						R.string.movie_director, m.getDirector()));
				tvMoviePerformer.setText(getResources().getString(
						R.string.movie_performer, m.getPerformer()));
				tvMovieSubtitles.setText(getResources().getString(
						R.string.movie_subtitles, m.getSubtitles()));
				tvMovieType.setText(getResources().getString(
						R.string.movie_type, m.getType()));
				if (m.getPlot() == null || m.getPlot().isEmpty()) {
					tvMoviePlot.setVisibility(View.GONE);
					rlPlot.setVisibility(View.GONE);
				} else {
					rlPlot.setVisibility(View.VISIBLE);
					tvMoviePlot.setVisibility(View.VISIBLE);
					tvMoviePlot.setText(getResources().getString(
							R.string.movie_plot, m.getPlot()));
				}
				List<BaiduModel> bdList = m.getBaiduModel();
				List<AiqiyiModel> aqList = m.getAiqiyiModel();
				List<XunleiModel> xlList = m.getXunleiModel();

				if (bdList != null && !bdList.isEmpty()) {
					llBaidu.setVisibility(View.VISIBLE);
					bLinksAdapter = new BaiduAdapter(MovieActivity.this, bdList);
					bListView.setAdapter(bLinksAdapter);
					fixListViewHeight(bListView);
				} else {
					llBaidu.setVisibility(View.GONE);
				}

				if (aqList != null && !aqList.isEmpty()) {
					llAiqiyi.setVisibility(View.VISIBLE);
					aLinksAdapter = new AiqiyiAdapter(MovieActivity.this, aqList);
					aListView.setAdapter(aLinksAdapter);
					fixListViewHeight(aListView);
				} else {
					llAiqiyi.setVisibility(View.GONE);
				}

				if (xlList != null && !xlList.isEmpty()) {
					llXunlei.setVisibility(View.VISIBLE);
					xLinksAdapter = new XunleiAdapter(MovieActivity.this, xlList);
					xListView.setAdapter(xLinksAdapter);
					fixListViewHeight(xListView);
				} else {
					llXunlei.setVisibility(View.GONE);
				}

				break;

			default:
				break;
			}
		};
	};

	public boolean isAppInstalled(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-18 下午3:27:00
	 * @Description: 通过包名启动应用程序
	 * @param @param packagename
	 * @return void
	 */
	private void doStartApplicationWithPackageName(String packagename) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			startActivity(intent);
		}
	}

	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager clipboardManager = (ClipboardManager) context
				.getSystemService(CLIPBOARD_SERVICE);
		ClipData clipData = ClipData.newPlainText("label", content); // 文本型数据
																		// clipData
																		// 的构造方法。
		clipboardManager.setPrimaryClip(clipData); // 将 字符串 str 保存 到剪贴板
	}

	public String getNowDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);;
		return dateString;
	}
	
	public void fixListViewHeight(ListView listView) {   
        // 如果没有设置数据适配器，则ListView没有子项，返回。  
        ListAdapter listAdapter = listView.getAdapter();  
        int totalHeight = 0;   
        if (listAdapter == null) {   
            return;   
        }   

        for (int i = 0, len = listAdapter.getCount(); i <= len; i++) {     
            View listViewItem = listAdapter.getView(i , null, listView);  
            // 计算子项View 的宽高   
            listViewItem.measure(0, 0);    
            // 计算所有子项的高度和
            totalHeight += listViewItem.getMeasuredHeight();    
        }   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        // listView.getDividerHeight()获取子项间分隔符的高度   
        // params.height设置ListView完全显示需要的高度    
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
        listView.setLayoutParams(params);   

    }   
}
