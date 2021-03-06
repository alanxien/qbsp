package com.chuannuo.qianbaosuoping.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.dow.android.DOW;
import cn.dow.android.listener.DataListener;

import com.chuannuo.qianbaosuoping.DownLoadAppActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.ShareDetailActivity;
import com.chuannuo.qianbaosuoping.adapter.DepthTaskAdapter;
import com.chuannuo.qianbaosuoping.adapter.GameTaskAdapter;
import com.chuannuo.qianbaosuoping.adapter.MyPagerAdapter;
import com.chuannuo.qianbaosuoping.adapter.RecommendTaskAdapter;
import com.chuannuo.qianbaosuoping.adapter.ShareTaskAdapter;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.MyApplication;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.model.ShareApp;
import com.chuannuo.qianbaosuoping.view.CustomProgressDialog;
import com.chuannuo.qianbaosuoping.view.CustomViewPager;
import com.chuannuo.tangguo.TGData;
import com.chuannuo.tangguo.TangGuoWall;
import com.chuannuo.tangguo.listener.TangGuoWallListener;
import com.chuannuoq.DevInit;
import com.chuannuoq.GetTotalMoneyListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2015-1-26 下午5:14:47
 * @Description: TODO
 */
public class EarnFragment extends Fragment implements OnClickListener{
	public static final String TAG = "EarnFragment";

	private CustomViewPager mViewPager;
	private List<View> lists = new ArrayList<View>();
	private MyPagerAdapter myPagerAdapter;

	//private ListView lv_game; // 游戏任务
	private ListView appListView; // 推荐任务
	private ListView depthListView; // 深度任务
	//private ListView shareListView; // 分享任务

	//private ArrayList<AppInfo> gameList; // 游戏任务
	//private ArrayList<ShareApp> shareAppList; // 分享任务列表
	private ArrayList<AppInfo> infoList; // 推荐任务列表
	private ArrayList<AppInfo> depthAppList; // 深度任务列表

	//private GameTaskAdapter gAdapter; // 游戏任务列表适配器
	private RecommendTaskAdapter rAdapter; // 推荐任务列表适配器
	private DepthTaskAdapter dAdapter; // 深度任务列表适配器
	//private ShareTaskAdapter sAdapter; // 分享任务列表适配器
	// private Wpers oActivity;
	private int lastItem;
	private int count = 0;
	private LinearLayout ll_progressBar; // 推荐任务列表 加载条
	private LinearLayout ll_share_progressBar; // 分享任务 加载条
	private boolean isScroll = false;

	//private TextView tv_game_task; // 游戏任务
	private TextView tv_recommended; // 推荐任务
	private TextView tv_more_task; // 更多任务
	//private TextView tv_share_task; // 分享任务

	private TextView tv_app_list; // 任务列表
	private TextView tv_depth_task; // 未完成任务
	private TextView tv_blew_app_list; // 任务列表下划线颜色
	private TextView tv_blew_depth_task; // 未完成任务下划线颜色
	private TextView tv_sign_tips;

	private RelativeLayout rl_dianjoy; // 点乐积分墙
	private RelativeLayout rl_domob; // 多盟积分墙
	private RelativeLayout rl_youmi; // 有米告积分墙
	private RelativeLayout rl_tg;//糖果推荐

	private SharedPreferences pref;
	private Editor editor;
	private MyApplication myApplication;
	public CustomProgressDialog progressDialog;
	private View pager0;
	//private View pager1;
	//private View pager2;
	private View pager3;
	static public Context context;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_earn, container, false);
		context = EarnFragment.this.getActivity();
		pager0 = inflater.inflate(R.layout.recom_task, null);// 推荐任务
		//pager1 = inflater.inflate(R.layout.game_task, null);// 游戏任务
		//pager2 = inflater.inflate(R.layout.share_task, null);// 分享任务
		pager3 = inflater.inflate(R.layout.more_task, null);// 更多任务

		tv_recommended = (TextView) view.findViewById(R.id.tv_recommended);
		tv_more_task = (TextView) view.findViewById(R.id.tv_more_task);
		//tv_share_task = (TextView) view.findViewById(R.id.tv_share_task);
		//tv_game_task = (TextView) view.findViewById(R.id.tv_game_task);

		mViewPager = (CustomViewPager) view.findViewById(R.id.viewpager);

		rl_dianjoy = (RelativeLayout) pager3.findViewById(R.id.rl_dianjoy);
		rl_domob = (RelativeLayout) pager3.findViewById(R.id.rl_domob);
		rl_youmi = (RelativeLayout) pager3.findViewById(R.id.rl_youmi);
		//rl_tg = (RelativeLayout) pager3.findViewById(R.id.rl_tg);

		myApplication = (MyApplication) getActivity().getApplication();

		tv_recommended.setOnClickListener(this);
		tv_more_task.setOnClickListener(this);
		//tv_share_task.setOnClickListener(this);
		//tv_game_task.setOnClickListener(this);

		rl_dianjoy.setOnClickListener(this);
		rl_domob.setOnClickListener(this);
		rl_youmi.setOnClickListener(this);
		//rl_tg.setOnClickListener(this);

		lists.add(pager0);
		//lists.add(pager1);
		//lists.add(pager2);
		lists.add(pager3);

		/*
		 * 在需要显示积分墙的地方 放入如下两段代码
		 */
		TangGuoWall.initWall(getActivity(), "");//参数2为userId，可以为空
		TangGuoWall.setTangGuoWallListener(new TangGuoWallListener() {
			
			/* (non-Javadoc)
			 * 图片上传审核状态回调。
			 */
			@Override
			public void onUploadImgs(List<TGData> dataList) {
				/*
				 * 图片审核状态 TGData
				 * title，标题
				 * score，积分
				 * isPass，审核是否通过，true,false
				 * remarks，审核true通过remarks为空，失败false，remarks为审核失败理由
				 */
				if(dataList.size()>0){
					int score = 0;
					for(int i=0; i<dataList.size();i++){
						if(dataList.get(i).isPass()){
							score += (int) dataList.get(i).getScore();
						}
					}
					if(score >0){
						addIntegral(
								score,
								EarnFragment.this.getActivity()
										.getResources()
										.getString(R.string.tg_app));
					}
				}
			}
			
			/* 
			 * 用户下载应用增加积分
			 * status 1-体验应用成功，0-体验应用失败
			 * appName 应用名称
			 * point 增加多少积分
			 */
			@Override
			public void onAddPoint(int status, String appName, double point) {
				if(status == 1){
					Toast.makeText(getActivity(), "体验应用成功", Toast.LENGTH_SHORT).show();
//					addIntegral(
//							point,
//							EarnFragment.this.getActivity()
//									.getResources()
//									.getString(R.string.tg_app));
				}else{
					Toast.makeText(getActivity(), "体验应用失败", Toast.LENGTH_SHORT).show();
				}
			}

			/* 
			 * 用户签到增加积分
			 * status 1-应用签到成功，0-应用签到失败
			 * appName 应用名称
			 * point 增加多少积分
			 */
			@Override
			public void onSign(int status, String appName, double point) {
				if(status == 1){
					Toast.makeText(getActivity(), "应用签到成功", Toast.LENGTH_SHORT).show();
					/*addIntegral(
							point,
							EarnFragment.this.getActivity()
									.getResources()
									.getString(R.string.tg_app));*/
				}else{
					Toast.makeText(getActivity(), "应用签到失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
		initData();
		return view;
	}

	private void initData() {
		pref = this.getActivity().getSharedPreferences(Constant.STUDENTS_EARN,
				FragmentActivity.MODE_PRIVATE);
		editor = pref.edit();
		initPager0(pager0);
		myPagerAdapter = new MyPagerAdapter(lists);
		mViewPager.setAdapter(myPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					if (null == infoList || infoList.size() == 0) {
						initPager0(pager0);
					}

					tv_recommended.setTextColor(getResources().getColor(
							R.color.white1));
					tv_recommended.setBackgroundResource(R.color.RedTheme);
					tv_more_task.setTextColor(getResources().getColor(
							R.color.RedTheme));
					tv_more_task
							.setBackgroundResource(R.drawable.corner_right_white1);
					myApplication.setType(0);
					break;
				case 1:
					tv_recommended.setTextColor(getResources().getColor(
							R.color.RedTheme));
					tv_recommended
							.setBackgroundResource(R.drawable.corner_left_white1);
					tv_more_task.setTextColor(getResources().getColor(
							R.color.white1));
					tv_more_task
							.setBackgroundResource(R.drawable.corner_right_yellow);
					myApplication.setType(3);
				default:
					break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

		});

	}

	@Override
	public void onResume() {
		if(myApplication.isSign()){
			tv_depth_task.performClick();
		}

		if (pref.getInt(Constant.APP_SIGN_IS_SUCCESS, 0) == 1) {
			Toast.makeText(EarnFragment.this.getActivity(), "签到失败，试玩没超过2分钟",
					Toast.LENGTH_SHORT).show();
			editor.putInt(Constant.APP_SIGN_IS_SUCCESS, 0);
			editor.commit();
		} else if (pref.getInt(Constant.APP_SIGN_IS_SUCCESS, 0) == 2) { // 签到成功
			initDepthTask();
			editor.putInt(Constant.APP_SIGN_IS_SUCCESS, 0);
			editor.commit();
		}

//		addDianJoyPoint();
//		addDMPoint();
//		addYMPoint();

		if (infoList != null && infoList.size() > 0) {
			AppInfo app = new AppInfo();
			for (int i = infoList.size() - 1; i >= 0; i--) {
				app = infoList.get(i);
				if (app.getResource_id() == myApplication.getResourceId()) {
					infoList.remove(i);
					break;
				}
			}
			if (rAdapter != null) {
				rAdapter.notifyDataSetChanged();
			}

		}

		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_dianjoy:
			/*
			 * 点乐积分墙
			 */
			DevInit.showOffers(EarnFragment.this.getActivity());
			break;
		case R.id.rl_domob:
			/*
			 * 多盟 积分墙
			 */
			DOW.getInstance(EarnFragment.this.getActivity())
					.show(EarnFragment.this.getActivity());
			break;
		case R.id.rl_youmi:
			/*
			 * 有米积分墙
			 */
			OffersManager.getInstance(EarnFragment.this.getActivity())
					.showOffersWall();
			break;
//		case R.id.rl_tg:
//			/*
//			 * 显示积分墙广告
//			 */
//			TangGuoWall.show();
//			break;
		case R.id.tv_recommended:
			mViewPager.setCurrentItem(0);
			myApplication.setType(Constant.PAGER0);
			break;
		case R.id.tv_game_task:
			mViewPager.setCurrentItem(1);
			myApplication.setType(Constant.PAGER1);
			break;
		case R.id.tv_share_task:
			mViewPager.setCurrentItem(2);
			myApplication.setType(Constant.PAGER2);
			break;
		case R.id.tv_more_task:
			mViewPager.setCurrentItem(3);
			myApplication.setType(Constant.PAGER3);
			break;
		case R.id.tv_app_list:
			if (depthListView == null) {
				depthListView = (ListView) pager0
						.findViewById(R.id.lv_depth_task);
			}
			tv_sign_tips.setVisibility(View.GONE);
			appListView.setVisibility(View.VISIBLE);
			if(depthListView != null){
				depthListView.setVisibility(View.GONE);
			}
			
			tv_blew_app_list.setBackgroundColor(getResources().getColor(
					R.color.RedTheme));
			tv_blew_depth_task.setBackgroundColor(getResources().getColor(
					R.color.blueNew));
			break;
		case R.id.tv_depth_task:
			if (depthListView == null) {
				depthListView = (ListView) pager0
						.findViewById(R.id.lv_depth_task);
			}
			tv_sign_tips.setVisibility(View.VISIBLE);
			appListView.setVisibility(View.GONE);
			depthListView.setVisibility(View.VISIBLE);
			tv_blew_app_list.setBackgroundColor(getResources().getColor(
					R.color.blueNew));
			tv_blew_depth_task.setBackgroundColor(getResources().getColor(
					R.color.RedTheme));
			initDepthTask();
			break;
		default:
			break;
		}
	}


	/**
	 * @author alan.xie
	 * @date 2014-11-10 下午6:28:59
	 * @Description: 初始化推荐任务 界面
	 * @param @param pager
	 * @return void
	 */
	public void initPager0(View pager) {
		appListView = (ListView) pager.findViewById(R.id.lv_recommend);
		tv_sign_tips = (TextView) pager.findViewById(R.id.tv_sign_tips);
		ll_progressBar = (LinearLayout) pager.findViewById(R.id.ll_progressBar);

		tv_depth_task = (TextView) pager.findViewById(R.id.tv_depth_task);
		tv_app_list = (TextView) pager.findViewById(R.id.tv_app_list);
		tv_blew_app_list = (TextView) pager.findViewById(R.id.tv_blew_app_list);
		tv_blew_depth_task = (TextView) pager
				.findViewById(R.id.tv_blew_depth_task);

		ll_progressBar.setVisibility(View.VISIBLE);

		infoList = new ArrayList<AppInfo>();

		appListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (infoList == null || infoList.size() == 0) {
					Toast.makeText(getActivity(), "数据异常，请检查网络",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable(Constant.ITEM,
							infoList.get(position));
					intent.putExtras(bundle);
					intent.setClass(EarnFragment.this.getActivity(),
							DownLoadAppActivity.class);
					startActivity(intent);
				}
			}
		});

		tv_app_list.setOnClickListener(this);
		tv_depth_task.setOnClickListener(this);

		getResourceList();
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-17 上午11:55:40
	 * @Description: 初始化 未完成任务
	 * @param
	 * @return void
	 */
	private void initDepthTask() {
		myApplication.setSign(false);
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		HttpUtil.get(Constant.DEPTH_TASK_LIST_URL, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") != 1) {
								Toast.makeText(EarnFragment.this.getActivity(),
										"数据加载失败", Toast.LENGTH_SHORT).show();
							} else {
								JSONArray jArray = response
										.getJSONArray("data");
								Log.i("EarnFragment", jArray.toString());
								if (null != jArray && jArray.length() > 0) {
									depthAppList = new ArrayList<AppInfo>();
									for (int i = 0; i < jArray.length(); i++) {

										JSONObject obj = jArray
												.getJSONObject(i);
										String s = obj.getString("resourceArr");
										if (!s.equals("[]")&&!s.equals("false")) {
											JSONObject childObj = obj
													.getJSONObject("resourceArr");
											// if(checkPackage(obj.getString("package_name"))){
											// //判断用户是否已经安装该软件
											AppInfo appInfo = new AppInfo();
											if (null != childObj) {
												appInfo.setTitle(childObj
														.getString("title"));
												if(appInfo.getTitle().equals("")){
													appInfo.setTitle(childObj
															.getString("name"));
												}
												appInfo.setResource_id(childObj
														.getInt("id"));
												appInfo.setAdId(childObj
														.getInt("ad_id"));
												appInfo.setResource_size(childObj
														.getString("resource_size"));
												appInfo.setB_type(childObj
														.getInt("btype"));
												appInfo.setScore(childObj
														.getInt("score"));
												appInfo.setClicktype(childObj.getInt("clicktype"));
												
												appInfo.setCustomStatus(obj.getInt("is_custom_status"));
												appInfo.setIsCustom(obj.getInt("is_custom"));
												appInfo.setCustomField1(childObj.getString("custom1"));
												appInfo.setCustomField2(childObj.getString("custom2"));
												
												
												appInfo.setInstall_id(obj
														.getInt("ad_install_id"));
												appInfo.setIs_photo(obj.getInt("is_photo"));
												appInfo.setPhoto_integral(obj.getInt("photo_integral"));
												appInfo.setPhoto_status(obj.getInt("photo_status"));
												appInfo.setIs_photo_task(obj.getInt("is_photo_task"));

												appInfo.setUpload_photo(obj
														.getInt("photo_upload_number"));
												appInfo.setCurr_upload_photo(obj
														.getInt("upload_photo_number"));
												appInfo.setPhoto_remarks(childObj
														.getString("photo_remarks"));
												appInfo.setCheck_remarks(obj
														.getString("photo_remarks"));
												appInfo.setAppeal(obj
														.getInt("appeal"));
												
												JSONArray plJson = childObj
														.getJSONArray("picture_list");
												if (plJson != null
														&& plJson.length() > 0) {
													List<String> l = new ArrayList<String>();
													for (int j = 0; j < plJson
															.length(); j++) {
														String url = plJson
																.getString(j);
														if (!url.contains("http")) {
															url = Constant.ROOT_URL
																	+ url;
														}
														l.add(url);
													}
													appInfo.setImgsList(l);
												}

												if (appInfo
														.getCurr_upload_photo() > 0) {
													JSONArray ulJson = obj
															.getJSONArray("upload_picture_list");
													if (plJson != null
															&& ulJson.length() > 0) {
														List<String> l = new ArrayList<String>();
														for (int j = 0; j < ulJson
																.length(); j++) {
															String url = ulJson
																	.getString(j);
															if (!url.contains("http")) {
																url = Constant.ROOT_URL
																		+ url;
															}
															l.add(url);
														}
														appInfo.setUpImgList(l);
													}
												}
												
												String photo = obj
														.getString("photo");
												if (!photo.isEmpty()
														&& !photo
																.contains("http")) {
													photo = Constant.ROOT_URL
															+ photo;
												}

												String fileUrl = childObj
														.getString("file");
												String iconUrl = childObj
														.getString("icon");
												String h5Url = childObj
														.getString("h5_big_url");

												if (!h5Url.contains("http")) {
													h5Url = Constant.ROOT_URL
															+ h5Url;
												}

												if (!fileUrl.contains("http")) {
													fileUrl = Constant.DOWN_URL
															+ fileUrl;
												}
												if (!iconUrl.contains("http")) {
													iconUrl = Constant.ROOT_URL
															+ iconUrl;
												}
												appInfo.setH5_big_url(h5Url);
												appInfo.setFile(fileUrl);
												appInfo.setIcon(iconUrl);
											}
											appInfo.setSign_times(obj
													.getInt("sign_count"));
											appInfo.setNeedSign_times(obj
													.getInt("sign_number"));
											appInfo.setSign_rules(obj
													.getInt("reportsigntime"));
											appInfo.setPackage_name(obj
													.getString("package_name"));
											appInfo.setIsAddIntegral(obj
													.getInt("is_add_integral"));
											appInfo.setScore(obj
													.getInt("integral"));
											appInfo.setSign(true);
											if(isSignTime(obj)){
												appInfo.setSignTime(true);
											}
											
											if (appInfo.getPhoto_status() != 3) {
												depthAppList.add(appInfo);
											} else {
												String date = (null == obj
														.getString("update_date") || obj
														.getString(
																"update_date")
														.equals("null")) ? ""
														: obj.getString("update_date");
												if (canAppeal(date)) {
													depthAppList.add(appInfo);
												}
											}
											
//											if (appInfo.getIs_photo_task() != 1) {
//												depthAppList.add(appInfo);
//											}else{
//												if(appInfo.getIs_photo() == 0 || appInfo.getPhoto_status() == 2){
//													depthAppList.add(appInfo);
//												}
//											}
										}
									}

									Message msg = mHandler.obtainMessage();
									msg.what = 2;
									mHandler.sendMessage(msg);
								}

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
					
					@SuppressLint("SimpleDateFormat")
					private boolean canAppeal(String date) {
						SimpleDateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss");
						long time;
						try {

							time = df.parse(date).getTime();
							if (time + 24 * 60 * 60 * 1000 > System
									.currentTimeMillis()) {
								return true;
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
						}

						return false;
					}
				});
	}

	private boolean isSignTime(JSONObject obj) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date;
		long time;
		try {
			date = (null == obj.getString("update_date") || obj.getString(
					"update_date").equals("null")) ? obj
					.getString("create_date") : obj.getString("update_date");

			time = df.parse(date).getTime() + obj.getLong("reportsigntime")
					* 24 * 60 * 60 * 1000;
			if (time < System.currentTimeMillis()) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch
			// block
			e.printStackTrace();
		}

		return false;
	}

	/** 
	* @Title: getResourceList 
	* @Description: 资源列表
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	public void getResourceList() {
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("channel_id", getMetaData(getActivity(), "LEZHUAN_CHANNEL"));
		HttpUtil.get(Constant.DOWNLOAD_URL, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {

								JSONArray jArray = response
										.getJSONArray("data");
								Log.i("EarnFragment", jArray.toString());
								int l1 = jArray.length();
								if (null != jArray && l1 > 0) {
									Message msg = mHandler.obtainMessage();
									JSONArray installed = myApplication
											.getjArry();
									JSONArray jarrayN = null;

									if (installed != null
											&& installed.length() > 0) {
										int l2 = installed.length();
										jarrayN = new JSONArray();
										for (int i = 0; i < l1; i++) {
											for (int j = 0; j < l2; j++) {
												if (jArray
														.getJSONObject(i)
														.getString(
																"package_name")
														.equals(installed
																.get(j))) {
													break;
												}
												if (j >= l2 - 1) {
													jarrayN.put(jArray
															.getJSONObject(i));
												}
											}
										}
									}

									if (null != jarrayN && jarrayN.length() > 0) {
										jArray = jarrayN;
									}
									for (int i = 0; i < jArray.length(); i++) {
										JSONObject obj = jArray
												.getJSONObject(i);
										if (null != obj) {
											AppInfo appInfo = new AppInfo();

											appInfo.setResource_id(obj
													.getInt("id"));
											appInfo.setAdId(obj.getInt("ad_id"));
											appInfo.setTitle(obj
													.getString("title"));
											appInfo.setName(obj
													.getString("name"));
											if(appInfo.getTitle().equals("")){
												appInfo.setTitle(appInfo.getName());
											}
											appInfo.setDescription(obj
													.getString("description"));
											appInfo.setPackage_name(obj
													.getString("package_name"));
											appInfo.setBrief(obj
													.getString("brief"));
											appInfo.setScore(obj
													.getInt("score"));
											appInfo.setResource_size(obj
													.getString("resource_size"));
											appInfo.setB_type(obj
													.getInt("btype"));
											appInfo.setTotalScore(obj
													.getInt("score")
													+ obj.getInt("sign_number")
													* 10000+obj.getInt("photo_integral"));
											appInfo.setSign_rules(obj
													.getInt("reportsigntime"));
											appInfo.setNeedSign_times(obj
													.getInt("sign_number"));
											appInfo.setIsShare(obj
													.getInt("isShare"));
											
											appInfo.setIsCustom(obj.getInt("is_custom"));
											appInfo.setCustomField1(obj.getString("custom1"));
											appInfo.setCustomField2(obj.getString("custom2"));
											
											appInfo.setClicktype(obj.getInt("clicktype"));
											appInfo.setIs_photo(obj.getInt("is_phopo"));
											appInfo.setPhoto_integral(obj.getInt("photo_integral"));

											String fileUrl = obj
													.getString("file");
											String iconUrl = obj
													.getString("icon");
											String h5Url = obj
													.getString("h5_big_url");

											if (!fileUrl.contains("http")) {
												fileUrl = Constant.DOWN_URL
														+ fileUrl;
											}
											if (!iconUrl.contains("http")) {
												iconUrl = Constant.ROOT_URL
														+ iconUrl;
											}
											if (!h5Url.contains("http")) {
												h5Url = Constant.ROOT_URL
														+ h5Url;
											}

											appInfo.setFile(fileUrl);
											appInfo.setH5_big_url(h5Url);
											appInfo.setIcon(iconUrl);
											
											JSONArray imgArray = obj.getJSONArray("picture_list");
											if(imgArray!=null && imgArray.length()>0){
												int len = imgArray.length();
												List<String> imgs = new ArrayList<String>();
												for(int k=0; k<len; k++){
													String url = imgArray.getString(k);
													if (!url.contains("http")) {
														url = Constant.ROOT_URL
																+ url;
													}
													imgs.add(url);
												}
												appInfo.setImgsList(imgs);
											}

											infoList.add(appInfo);
										}

									}

									if (null != infoList && infoList.size() > 0) {
										ll_progressBar.setVisibility(View.GONE);
									}

									msg.what = 1;
									mHandler.sendMessage(msg);
								}

							} else {
								Toast.makeText(EarnFragment.this.getActivity(),
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
						if(ll_progressBar != null){
							ll_progressBar.setVisibility(View.GONE);
						}
						
						if(EarnFragment.this.getActivity() != null){
							Toast.makeText(EarnFragment.this.getActivity(),
									"数据获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
						}
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}
	
	private String getMetaData(Context context,
			String key) {
		try {
	           ApplicationInfo  ai = context.getPackageManager().getApplicationInfo(
	                  context.getPackageName(), PackageManager.GET_META_DATA);
	           Object value = ai.metaData.get(key);
	           if (value != null) {
	              return value.toString();
	           }
	       } catch (Exception e) {
	       }
	       return "";

	    }

	Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				appListView.setVisibility(View.VISIBLE);
				if (null == rAdapter) {
					rAdapter = new RecommendTaskAdapter(getActivity(), infoList);
				} else {
					rAdapter.notifyDataSetChanged();
				}

				appListView.setAdapter(rAdapter);
				break;
			case 2:
				dAdapter = new DepthTaskAdapter(getActivity(), depthAppList);
				depthListView.setAdapter(dAdapter);
				break;
			case 6:
				if (null == rAdapter) {
					appListView.setVisibility(View.VISIBLE);
					ll_progressBar.setVisibility(View.GONE);
					rAdapter = new RecommendTaskAdapter(getActivity(), infoList);
					appListView.setAdapter(rAdapter);
				} else {
					rAdapter.notifyDataSetChanged();
				}
				if (count > 10 && isScroll) {
					appListView.setSelection(count / 9 * 5);
					rAdapter.notifyDataSetChanged();
				}
				break;
			default:
				progressDialog.dismiss();
				break;
			}
		};
	};

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-31 上午10:02:50
	 * @Description: 增加积分
	 * @param @param integral
	 * @return void
	 */
	public void addIntegral(final int integral, String reason) {
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));
		params.put("integral", integral);
		params.put("code", pref.getString(Constant.CODE, ""));
		params.put("reason", reason);

		HttpUtil.get(Constant.ADD_INTEGRAL_URL, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") != 1) {
								Toast.makeText(EarnFragment.this.getActivity(),
										response.getString("info"),
										Toast.LENGTH_SHORT).show();
							} else {
								/*
								 * 增加积分成功
								 */
								Toast.makeText(
										EarnFragment.this.getActivity(),
										getResources().getString(
												R.string.add_score_success)
												+ "+" + integral,
										Toast.LENGTH_SHORT).show();
								editor.putInt(Constant.SCORE,
										pref.getInt(Constant.SCORE, 0)
												+ integral);
								editor.commit();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
						}
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Toast.makeText(EarnFragment.this.getActivity(),
								"网络连接错误，请检查网络", Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-17 下午2:45:48
	 * @Description: 检测该包名所对应的应用是否存在
	 * @param @param packageName
	 * @param @return
	 * @return boolean
	 */
	public boolean checkPackage(String packageName) {
		if (packageName == null || "".equals(packageName)) {
			return false;
		}
		try {
			EarnFragment.this
					.getActivity()
					.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-26 下午4:32:21
	 * @Description: 监听点乐积分变化
	 * @param
	 * @return void
	 */
	private void addDianJoyPoint() {
		/*
		 * 点乐 积分
		 */
		DevInit.getTotalMoney(EarnFragment.this.getActivity(),
				new GetTotalMoneyListener() {

					/*
					 * amount，返回当前总积分
					 */
					@Override
					public void getTotalMoneySuccessed(String name, long amount) {
						Log.i(TAG, "点乐积分回调---"+amount);
						
						if (amount == 0) {
							editor.putInt(Constant.DL_TOTAL_SCORE, 0);
							editor.commit();
						}
						int score = pref.getInt(Constant.DL_TOTAL_SCORE, -1);

						if (score == -1) {
							editor.putInt(Constant.DL_TOTAL_SCORE, Long
									.valueOf(amount).intValue());
							editor.commit();
						}

						// 判断积分是否变化，若改变，则增加积分
						if (score != -1
								&& score != Long.valueOf(amount).intValue()) {
							editor.putInt(Constant.DL_TOTAL_SCORE, Long
									.valueOf(amount).intValue());
							editor.commit();
							addIntegral(
									Long.valueOf(amount).intValue() - score,
									EarnFragment.this.getActivity()
											.getResources()
											.getString(R.string.dl_app));
						}
					}

					@Override
					public void getTotalMoneyFailed(String error) {
					}
				});
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-26 下午4:32:48
	 * @Description: 监听多盟积分变化
	 * @param
	 * @return void
	 */
	private void addDMPoint() {
		/*
		 * 多盟积分墙
		 */
		DOW.getInstance(EarnFragment.this.getActivity()).checkPoints(
				new DataListener() {

					@Override
					public void onError(String arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onResponse(Object... point) {
						
						double totalPoint = (Double) point[1];
						Log.i(TAG, "多盟积分回调---"+totalPoint);
						if (totalPoint == 0) {
							editor.putInt(Constant.DM_TOTAL_SCORE, 0);
							editor.commit();
						}
						int score = pref.getInt(Constant.DM_TOTAL_SCORE, -1);

						if (score == -1) {
							editor.putInt(Constant.DM_TOTAL_SCORE, Double.valueOf(totalPoint).intValue());
							editor.commit();
						}

						if (score != totalPoint
								&& (score != -1 || totalPoint == 0)) {
							editor.putInt(Constant.DM_TOTAL_SCORE, Double.valueOf(totalPoint).intValue());
							editor.commit();
							addIntegral(
									Double.valueOf(totalPoint).intValue() - score,
									EarnFragment.this.getActivity()
											.getResources()
											.getString(R.string.dm_app));
						}
					}
				});
	}
	
	/** 
	* @Title: addYMPoint 
	* @Description: 有米积分墙监听
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	private void addYMPoint(){
		PointsManager.getInstance(getActivity()).registerNotify(new PointsChangeNotify() {
			
			@Override
			public void onPointBalanceChange(float pointsBalance) {
				Log.i(TAG, "有米积分回调---"+pointsBalance);
				if (pointsBalance == 0) {
					editor.putInt(Constant.YM_TOTAL_SCORE, 0);
					editor.commit();
				}
				int score = pref.getInt(Constant.YM_TOTAL_SCORE, -1);

				if (score == -1) {
					editor.putInt(Constant.YM_TOTAL_SCORE, Float.valueOf(pointsBalance).intValue());
					editor.commit();
				}

				if (score != pointsBalance
						&& (score != -1 || pointsBalance == 0)) {
					editor.putInt(Constant.YM_TOTAL_SCORE, Float.valueOf(pointsBalance).intValue());
					editor.commit();
					addIntegral(
							Float.valueOf(pointsBalance).intValue() - score,
							EarnFragment.this.getActivity()
									.getResources()
									.getString(R.string.ym_app));
				}
			}
		});
	}

}