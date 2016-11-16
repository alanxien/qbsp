package com.chuannuo.qianbaosuoping.movie;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author alan.xie
 * @date 2014-10-14 ����12:17:57
 * @Description: ��Ӱ
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

	/** ��Ļ��� */
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
		 * ���򻬶�tab
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
		rlPlot = (RelativeLayout) findViewById(R.id.rl_plot);
		ivPlot = (ImageView) findViewById(R.id.iv_plot);

		llBaidu = (LinearLayout) findViewById(R.id.movie_ll_baidu);
		llAiqiyi = (LinearLayout) findViewById(R.id.movie_ll_aiqiyi);
		llXunlei = (LinearLayout) findViewById(R.id.movie_ll_xunlei);

		tvAqyLink = (TextView) findViewById(R.id.movie_aiqiyi_link);
		tvXlLink = (TextView) findViewById(R.id.movie_xunlei_link);
		tvBdLink = (TextView) findViewById(R.id.movie_baidu_link);
		tvBdPasswrod = (TextView) findViewById(R.id.movie_baidu_password);

		rlPlot.setOnClickListener(this);
		tvBdLink.setOnClickListener(this);

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
									if (jaBaidu != null && jaBaidu.length() > 0) {
										JSONObject baidu = jaBaidu
												.getJSONObject(0);
										BaiduModel b = new BaiduModel();
										b.setLink(baidu.getString("link"));
										b.setTitle(baidu.getString("title"));
										b.setPassword(baidu
												.getString("password"));
										md.setBaiduModel(b);
									}

									if (jaXunlei != null
											&& jaXunlei.length() > 0) {
										JSONObject xunlei = jaXunlei
												.getJSONObject(0);
										XunleiModel x = new XunleiModel();
										x.setLink(xunlei.getString("link"));
										x.setTitle(xunlei.getString("title"));
										md.setXunleiModel(x);
									}

									if (jaAiqiyi != null
											&& jaAiqiyi.length() > 0) {
										JSONObject aiqiyi = jaAiqiyi
												.getJSONObject(0);
										AiqiyiModel a = new AiqiyiModel();
										a.setLink(aiqiyi.getString("link"));
										a.setTitle(aiqiyi.getString("title"));
										md.setAiqiyiModel(a);
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
		case R.id.movie_baidu_link:
			String packageName = "com.xunlei.downloadprovider";
			if (isAppInstalled(MovieActivity.this, packageName)) {
				copy(movie.getMovieDetail().getBaiduModel().getLink(),this);
				doStartApplicationWithPackageName(packageName);
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(Constant.XUNLEI_APK_URL));
				intent.addCategory("android.intent.category.DEFAULT");
				startActivity(intent);
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
				BaiduModel bd = m.getBaiduModel();
				AiqiyiModel aq = m.getAiqiyiModel();
				XunleiModel xl = m.getXunleiModel();

				if (bd != null) {
					llBaidu.setVisibility(View.VISIBLE);
					tvBdLink.setText(bd.getLink());
					tvBdLink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
					tvBdPasswrod.setText(getResources().getString(
							R.string.movie_password, bd.getPassword()));
				} else {
					llBaidu.setVisibility(View.GONE);
				}

				if (aq != null) {
					llAiqiyi.setVisibility(View.VISIBLE);
					tvAqyLink.setText(aq.getLink());
				} else {
					llAiqiyi.setVisibility(View.GONE);
				}

				if (xl != null) {
					llXunlei.setVisibility(View.VISIBLE);
					tvXlLink.setText(xl.getLink());
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
	 * @date 2014-12-18 ����3:27:00
	 * @Description: ͨ����������Ӧ�ó���
	 * @param @param packagename
	 * @return void
	 */
	private void doStartApplicationWithPackageName(String packagename) {

		// ͨ��������ȡ��APP��ϸ��Ϣ������Activities��services��versioncode��name�ȵ�
		PackageInfo packageinfo = null;
		try {
			packageinfo = getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// ����һ�����ΪCATEGORY_LAUNCHER�ĸð�����Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// ͨ��getPackageManager()��queryIntentActivities��������
		List<ResolveInfo> resolveinfoList = getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = ����packname
			String packageName = resolveinfo.activityInfo.packageName;
			// �����������Ҫ�ҵĸ�APP��LAUNCHER��Activity[��֯��ʽ��packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// ����ComponentName����1:packagename����2:MainActivity·��
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			startActivity(intent);
		}
	}

	public static void copy(String content, Context context) 
	{ 
	// �õ������������ 
		ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
	    ClipData clipData = ClipData.newPlainText("label", content); //�ı������� clipData �Ĺ��췽����    
	    clipboardManager.setPrimaryClip(clipData); // �� �ַ��� str ���� ��������
	}}
