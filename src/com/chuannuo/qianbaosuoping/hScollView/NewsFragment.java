package com.chuannuo.qianbaosuoping.hScollView;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.duobao.model.Movie;
import com.chuannuo.qianbaosuoping.movie.MovieActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NewsFragment extends Fragment {
	private final static String TAG = "NewsFragment";
	protected final int POST_DELAY = 1000;// 延时1秒加载
	Activity activity;
	ArrayList<Movie> newsList = new ArrayList<Movie>();

	String title;
	int channelId;
	public final static int SET_NEWSLIST = 0;
	NewsAdapter mAdapter;

	private SharedPreferences pref;
	private Editor editor;
	private GridView gridView;
	private SwipeRefreshLayout swipeRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_news, null);

		pref = this.getActivity().getSharedPreferences(Constant.STUDENTS_EARN,
				FragmentActivity.MODE_PRIVATE);
		editor = pref.edit();

		Bundle args = getArguments();
		title = args != null ? args.getString("title") : "";
		channelId = args != null ? args.getInt("id", 0) : 0;

		gridView = (GridView) view.findViewById(R.id.gridview);
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_red_dark, android.R.color.holo_green_dark);
		swipeRefreshLayout
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						getMovieList();
					}
				});
		swipeRefreshLayout.post(new Runnable() {

			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(true);
				getMovieList();
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Movie m = newsList.get(position);
				Intent intent = new Intent(getActivity(),MovieActivity.class);
				intent.putExtra("movie", m);
				startActivity(intent);
			}
		});

		mAdapter = new NewsAdapter(getActivity(), newsList);
		gridView.setAdapter(mAdapter);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		super.onAttach(activity);
	}

	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			// fragment可见时加载数据
			if (newsList != null && newsList.size() != 0) {
				handler.obtainMessage(SET_NEWSLIST).sendToTarget();
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.obtainMessage(SET_NEWSLIST).sendToTarget();
					}
				}).start();
			}
		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void getMovieList() {
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("type", title);
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
								Toast.makeText(NewsFragment.this.getActivity(),
										response.getString("info"),
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

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SET_NEWSLIST:

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mAdapter = null;
	}

	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
