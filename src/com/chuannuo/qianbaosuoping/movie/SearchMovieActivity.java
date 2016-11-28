package com.chuannuo.qianbaosuoping.movie;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
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
import com.chuannuo.qianbaosuoping.hScollView.NewsAdapter;
import com.chuannuo.qianbaosuoping.hScollView.NewsFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 电影
 */
public class SearchMovieActivity extends BaseActivity implements
		OnClickListener {
	InputMethodManager inputManager;
	private EditText etSearch;
	private ImageButton clearSearch;
	
	protected final int POST_DELAY = 1000;// 延时1秒加载
	ArrayList<Movie> newsList = new ArrayList<Movie>();
	NewsAdapter mAdapter;

	private SharedPreferences pref;
	private Editor editor;
	private GridView gridView;
	private SwipeRefreshLayout swipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_movie);

		initView();
		initData();
	}

	private void initView() {
		etSearch = (EditText) findViewById(R.id.query);
		clearSearch = (ImageButton) findViewById(R.id.search_clear);
		
		//弹出软键盘
		etSearch.setFocusable(true);
		etSearch.setFocusableInTouchMode(true);
		etSearch.requestFocus();
		
		setHint(etSearch, "搜索");
		
		inputManager =
		(InputMethodManager) etSearch.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);

		inputManager.showSoftInput(etSearch, 0);
		
		pref = this.getSharedPreferences(Constant.STUDENTS_EARN,
				FragmentActivity.MODE_PRIVATE);
		editor = pref.edit();


		gridView = (GridView) findViewById(R.id.gridview);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);

		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_red_dark, android.R.color.holo_green_dark);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Movie m = newsList.get(position);
				Intent intent = new Intent(SearchMovieActivity.this,MovieActivity.class);
				intent.putExtra("movie", m);
				startActivity(intent);
			}
		});

		mAdapter = new NewsAdapter(SearchMovieActivity.this, newsList);
		gridView.setAdapter(mAdapter);
	}

	private void initData() {

		etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            	//conversationListView.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                    getMovieList(s.toString());
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.getText().clear();
                hideSoftKeyboard();
            }
        });
	}
	
	protected void setHint(EditText editText, String hint) {
		// 新建一个可以添加属性的文本对象
		SpannableString ss = new SpannableString(hint);
		// 新建一个属性对象,设置文字的大小
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
		// 附加属性到文本
		ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置hint
		editText.setHint(new SpannedString(ss));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
	
	protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
	
	private void getMovieList(String type) {
		swipeRefreshLayout.post(new Runnable() {

			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(true);
			}
		});
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("type", type);
		HttpUtil.get(Constant.GET_MOVIE_LIST, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								newsList.clear();
								JSONArray jsonArry = response
										.getJSONArray("data");
								if (jsonArry != null && !jsonArry.equals("[]")) {
									int size = jsonArry.length();
									if (size > 0) {
										JSONObject obj = null;
										for (int i = 0; i < size; i++) {
											obj = jsonArry.getJSONObject(i);
											Movie m = new Movie();
											String title = obj.getString("title");
											String alias = obj.getString("alias");
											if(title==null || title.isEmpty() || title.equals("null")){
												title = alias;
											}
											m.setTitle(title);
											m.setIcon(obj.getString("picture") != null
													&& !obj.getString("picture")
															.isEmpty() ? Constant.ROOT_URL
													+ obj.getString("picture")
													: "");
											m.setId(obj.getInt("id"));

											newsList.add(m);
										}
										if(mAdapter!=null){
											
											mAdapter.notifyDataSetChanged();
										}
									}
								}

							} else {
								Toast.makeText(SearchMovieActivity.this,
										"搜索失败",
										Toast.LENGTH_SHORT).show();
							}
							super.onSuccess(statusCode, headers, response);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							swipeRefreshLayout.setRefreshing(false);
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						swipeRefreshLayout.setRefreshing(false);
					}
				});
	}

}
