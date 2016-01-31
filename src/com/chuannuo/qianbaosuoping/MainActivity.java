package com.chuannuo.qianbaosuoping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.midi.wall.sdk.AdWall;
import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.Toast;
import cn.dm.android.DMOfferWall;
import cn.yeeguo.Yeeguo;

import com.baidu.mobads.appoffers.PointsChangeListener;
import com.chuannuo.qianbaosuoping.adapter.FragmentAdapter;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.MyApplication;
import com.chuannuo.qianbaosuoping.service.LockScreenService;
import com.chuannuo.qianbaosuoping.service.PollingService;
import com.chuannuo.qianbaosuoping.service.PollingUtils;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.chuannuo.qianbaosuoping.view.CustomViewPager;
import com.datouniao.AdPublisher.AppConfig;
import com.datouniao.AdPublisher.AppConnect;
import com.datouniao.AdPublisher.ReceiveNotifier;
import com.dlnetwork.Dianle;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;
/**
 * @author alan.xie
 * @date 2014-10-13 下午5:56:18
 * @Description: 主界面
 */
public class MainActivity extends BaseFragmentActivity implements
		OnClickListener {

	private static final String TAG = "MainActivity";

	public static final int TAB_HOME = 0; 		// 首页
	public static final int TAB_EXCHANGE = 1; 	// 兑换
	public static final int TAB_EARN = 2; 		// 赚
	public static final int TAB_SNATCH = 3;     //夺宝
	public static final int TAB_ME = 4; 		// 我
	public static final int TAB_MORE = 5; 		// 更多

	private CustomViewPager viewPager;
	private RadioButton main_tab_home, main_tab_exchange, main_tab_earn,
			main_tab_snatch,main_tab_me, main_tab_more;
	private CustomDialog dialogConvert,shareDialog,depthDialog;

	private MyApplication myApplication;
	private SharedPreferences pref;

	public static Tencent mTencent;
	public Intent intent;
	
	private PackageManager pManager;
	//获取手机内所有应用 
	private List<PackageInfo> paklist;
	private FeedbackAgent agent;
	
	private String convertStr;
	private int convertId;
	private int convertScore;
	static public Context context;
	
	/*
	 * 大头鸟积分墙
	 */
	private AppConnect appConnectInstance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context=this;
		myApplication = (MyApplication) getApplication();
		pref = this.getSharedPreferences(Constant.STUDENTS_EARN,MODE_PRIVATE);
		
		pManager = this.getPackageManager();
		 //获取手机内所有应用 
	    paklist = pManager.getInstalledPackages(0);

		if (mTencent == null) {
			mTencent = Tencent.createInstance(Constant.QQ_APP_ID, this);
		}

		
		if (pref.getBoolean(Constant.IS_LOCK_SCREEN, true)) {
			if(null == intent){
				intent = new Intent(MainActivity.this, LockScreenService.class);
			}
			editor.putInt(Constant.SLIDED, 0);
			editor.commit();
			startService(intent); //这里要显示的调用服务
		}
		
		PollingUtils.startPollingService(this, 24*60*60, PollingService.class); //启动轮询服务，任务推送

		if(agent == null){
			agent = new FeedbackAgent(this);
		}
		
		// 初始化
		initOfferWall();

		initView();

		initData();
		depthTask();
		
		main_tab_home.setChecked(true);

	}

	private void initOfferWall() {
		/*
		 * 友盟更新
		 */
		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig(this);
		
		//友盟更新回调接口
		UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {

		    @Override
		    public void onClick(int status) {
		        switch (status) {
		        case UpdateStatus.Update:
		            break;
		        case UpdateStatus.Ignore:
		        case UpdateStatus.NotNow:
		        	PackageManager manager;
					PackageInfo info = null;
					manager = MainActivity.this.getPackageManager();
					try {

						info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);

						} catch (NameNotFoundException e) {

						// TODO Auto-generated catch block

						e.printStackTrace();

						}
					if(info.versionCode < 6){
						MainActivity.this.finish();
					}
		            break;
		        }
		    }
		});
		agent.sync();
		/*
		 * 点乐积分墙
		 */
		Dianle.initGoogleContext(this, "b3400d657800e5914b2d3c65b55660ae");
		Dianle.setCurrentUserID(this, "");

		/*
		 * 多盟积分墙
		 */
		DMOfferWall.init(this, "96ZJ1uSAzeWcHwTDiJ");

		/*
		 * 有米积分墙
		 */
		AdManager.getInstance(this).init("0ffb317788960341",
				"f062b23504663252", false);
		OffersManager.getInstance(this).onAppLaunch();

		/*
		 * 椰果积分墙
		 */
		Yeeguo.initYeeguo(this, "75ed0ae19b1ac31a998fab433f192320","");
		
		//初始化大头鸟
		AppConfig config = new AppConfig();
		config.setCtx(this);
		appConnectInstance = AppConnect.getInstance(this);
		config.setReceiveNotifier(new ReceiveNotifier() {
			
			@Override
			public void GetReceiveResponse(String currencyName, float receiveAmount, float totalAmount,
					String serverOrderID, String appName) {
				Log.i(TAG, "currencyName--"+currencyName);
				Log.i(TAG, "receiveAmount--"+receiveAmount);
				Log.i(TAG, "totalAmount--"+totalAmount);
				Log.i(TAG, "serverOrderID--"+serverOrderID);
				Log.i(TAG, "appName--"+appName);
				Toast.makeText(MainActivity.this, "积分增加成功", Toast.LENGTH_SHORT).show();
			}
		});
		
		/*
		 * 米迪积分墙
		 */
		AdWall.init(this, "21880", "t1jzelu6gqwjphhg");
		/*
		 * 指盟积分墙
		 */
		
		//百度积分监听
				//设置积分监听接口
				com.baidu.mobads.appoffers.OffersManager.setPointsChangeListener(new PointsChangeListener(){

							@Override
							public void onPointsChanged(int arg0) {
								Log.d("onPointsChanged", "total points is: "+arg0);
								final int integral=arg0;
								
								String reason= "百度积分墙";
								
								RequestParams params = new RequestParams();
								params.put("appid", pref.getString(Constant.APPID, "0"));
								params.put("integral", integral);
								params.put("code", pref.getString(Constant.CODE, ""));
								params.put("reason", reason);
								
								HttpUtil.get(Constant.ADD_INTEGRAL_URL,params, new JsonHttpResponseHandler(){
									@Override
									public void onSuccess(int statusCode, Header[] headers,
											JSONObject response) {
										try {
											if(response.getInt("code") != 1){
												Toast.makeText(context, response.getString("info"), Toast.LENGTH_SHORT).show();
											}else{
												/*
												 * 增加积分成功
												 */
												Toast.makeText(context, getResources().getString(R.string.add_score_success)+"+"+integral, Toast.LENGTH_SHORT).show();
												editor.putInt(Constant.SCORE, pref.getInt(Constant.SCORE, 0)+integral);
												editor.commit();
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} finally{
										}
										super.onSuccess(statusCode, headers, response);
									}
									
									@Override
									public void onFailure(int statusCode, Header[] headers,
											Throwable throwable, JSONObject errorResponse) {
										Toast.makeText(context, getResources().getString(R.string.sys_remind3), Toast.LENGTH_SHORT).show();
										super.onFailure(statusCode, headers, throwable, errorResponse);
									}
								});
								
								
								com.baidu.mobads.appoffers.OffersManager.subPoints(context, arg0);

								
							}
							
						});
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-22 上午10:00:41
	 * @Description: TODO
	 * @param
	 * @return void
	 */
	public void initView() {

		viewPager = (CustomViewPager) findViewById(R.id.viewpager);
		main_tab_home = (RadioButton) findViewById(R.id.main_tab_home);
		main_tab_exchange = (RadioButton) findViewById(R.id.main_tab_exchange);
		main_tab_earn = (RadioButton) findViewById(R.id.main_tab_earn);
		main_tab_snatch = (RadioButton) findViewById(R.id.main_tab_snatch);
		main_tab_me = (RadioButton) findViewById(R.id.main_tab_me);
		main_tab_more = (RadioButton) findViewById(R.id.main_tab_more);

		main_tab_home.setOnClickListener(this);
		main_tab_exchange.setOnClickListener(this);
		main_tab_earn.setOnClickListener(this);
		main_tab_me.setOnClickListener(this);
		main_tab_more.setOnClickListener(this);
		main_tab_snatch.setOnClickListener(this);

		FragmentAdapter adapter = new FragmentAdapter(
				getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		
		/*
		 * 兑换分享
		 */
		shareDialog = new CustomDialog(this, R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						switch (view.getId()) {
						case R.id.ll_share_wx:
							wechatShare(convertStr, convertId, convertScore);
							shareDialog.dismiss();
							break;
						case R.id.ll_share_qq:
							doShareTencent(convertStr, convertId, convertScore);
							shareDialog.dismiss();
							break;
						default:
							break;
						}
					}
				}, 4);
		shareDialog.setTitle("分享");
		shareDialog.setCanceledOnTouchOutside(false);
		
		/*
		 * 提示兑换 任务分享
		 */
		dialogConvert = new CustomDialog(this, R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						dialogConvert.dismiss();
						shareDialog.show();
					}
				}, 1);
		dialogConvert.setTitle("兑换成功");
		dialogConvert.setBtnStr("马上分享");
		dialogConvert.setCanceledOnTouchOutside(false);
		
		viewPager.setOffscreenPageLimit(3);
	}

	private void initData() {
		
		RequestParams p = new RequestParams();
		p.put("app_id", pref.getString(Constant.APPID, "0"));
		HttpUtil.post(Constant.CONVERT_SHARE, p, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getInt("code") == 1){
						convertStr = response.getJSONArray("data").getJSONObject(0).getString("info");
						convertId = response.getJSONArray("data").getJSONObject(0).getInt("id");
						convertScore = response.getJSONArray("data").getJSONObject(0).getInt("count");
						dialogConvert.setContent("您兑换"+convertStr+"已到帐，马上分享给好友，即可赚得0.05元！");
						dialogConvert.show();
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
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
		
		/*
		 * 上报手机已经安装的软件
		 * 各个应用包名用，隔开eg: cn.winads.studentsearn,cn.winads.ldbatterySteward
		 */
		String pakStr = getInstalledApp(); 
		Log.i(TAG, pakStr);
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("package_names", pakStr);
		
		HttpUtil.post(Constant.REPORT_URL,params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getInt("code") != 1){
						Log.i(TAG, "上报失败");
					}else{
						Log.i(TAG, "上报成功");
						if(response.getJSONArray("data") != null && response.getJSONArray("data").length() >0){
							myApplication.setjArry(response.getJSONArray("data"));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
				}
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	@Override
	protected void onResume() {
		if(myApplication.getFlag() == 1){
			viewPager.setCurrentItem(TAB_EARN, true);
			main_tab_earn.setChecked(true);
			main_tab_home.setChecked(false);
			myApplication.setFlag(0);
		}
		if(myApplication.getFlag() == 2){
			myApplication.setType(Constant.PAGER2);
			viewPager.setCurrentItem(TAB_EARN, true);
			main_tab_earn.setChecked(true);
			main_tab_home.setChecked(false);
			myApplication.setFlag(0);
		}
		
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_tab_home:
			viewPager.setCurrentItem(TAB_HOME, true);
			break;
		case R.id.main_tab_exchange:
			viewPager.setCurrentItem(TAB_EXCHANGE, true);
			break;
		case R.id.main_tab_earn:
			viewPager.setCurrentItem(TAB_EARN, true);
			break;
		case R.id.main_tab_snatch:
			viewPager.setCurrentItem(TAB_SNATCH,true);
			break;
		case R.id.main_tab_me:
			viewPager.setCurrentItem(TAB_ME, true);
			break;
		case R.id.main_tab_more:
			viewPager.setCurrentItem(TAB_MORE, true);
			break;
		default:
			break;
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-11-25 下午6:16:52
	 * @Description: 判断锁屏是否已经启动
	 * @param @return
	 * @return boolean
	 */
	public boolean isWorked() {
		ActivityManager myManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.equals("cn.winads.studentsearn.LockScreenService")) {
				return true;
			}
		}
		return false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != mTencent) {
			mTencent.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-1 下午5:13:22
	 * @Description: 判断锁屏服务是否运行
	 * @param @param mContext
	 * @param @param serviceName
	 * @param @return
	 * @return boolean
	 */
	public boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = (List<RunningServiceInfo>) myAM
				.getRunningServices(40);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}
	
	/**
	 * @author xin.xie
	 * @date 2015-5-27 上午8:46:12
	 * @Description: 提示未完成任务
	 * @return void
	 * @throws
	 */
	private void depthTask() {
		
		/*
		 * 提示兑换 任务分享
		 */
		depthDialog = new CustomDialog(this, R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						depthDialog.dismiss();
						viewPager.setCurrentItem(TAB_EARN, true);
						main_tab_earn.setChecked(true);
						main_tab_home.setChecked(false);
					}
				}, 1);
		depthDialog.setTitle("未完成任务提示");
		depthDialog.setBtnStr("去签到");
		
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		HttpUtil.get(Constant.DEPTH_TASK_LIST_URL, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") != 1) {
								Toast.makeText(MainActivity.this,
										"数据加载失败", Toast.LENGTH_SHORT).show();
							} else {
								JSONArray jArray = response
										.getJSONArray("data");

								SimpleDateFormat df = new SimpleDateFormat(
										"yyyy-MM-dd hh:mm:ss");
								ArrayList<Long> times = new ArrayList<Long>();
								String date;

								if (null != jArray && jArray.length() > 0) {
									// depthAppList = new ArrayList<AppInfo>();
									for (int i = 0; i < jArray.length(); i++) {

										JSONObject obj = jArray
												.getJSONObject(i);

										if (i < 100) {
											date = (null == obj
													.getString("update_date") || obj
													.getString("update_date")
													.equals("null")) ? obj
													.getString("create_date")
													: obj.getString("update_date");
											try {
												times.add(df.parse(date)
														.getTime()
														+ obj.getLong("reportsigntime")
														* 24 * 60 * 60 * 1000);

											} catch (ParseException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									}

									int l = times.size();
									if (l > 0) {
										for (int i = 0; i < l - 1; i++) {
											for (int j = 0; j < l- i - 1; j++) { 
												
												if (times.get(j) > times.get(j + 1)){ 
													long temp = times.get(j);
													times.set(j, times.get(j+1));
													times.set(j+1, temp);
												}
											}
										}
										
										if(times.get(0) < System.currentTimeMillis()){
											depthDialog.setContent("您有未完成任务，可以签到了，马上去[ 推荐任务--未完成任务 ]签到");
											depthDialog.show();
										}
									}
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
						Toast.makeText(MainActivity.this, "数据加载失败",
								Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}
	
	/**
	 * @author alan.xie
	 * @date 2015-1-5 上午9:45:04
	 * @Description: 获取已经安装的手机App
	 * @param @return
	 * @return String
	 */
	@SuppressWarnings("static-access")
	private String getInstalledApp(){
		StringBuilder pakStr = new StringBuilder();
		for (int i = 0; i < paklist.size(); i++) { 
	        PackageInfo pak = (PackageInfo) paklist.get(i); 
	        //判断是否为非系统预装的应用程序
	        if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) { 
	        	pakStr.append(pak.applicationInfo.packageName);
	        	pakStr.append(",");
	        } 
	    } 
		if(pakStr.length() > 0){
			pakStr.deleteCharAt(pakStr.length()-1);
		}
		return pakStr.toString();
	}
	
	/**
	 * @author alan.xie
	 * @date 2015-1-5 下午3:53:28
	 * @Description: 推荐任务
	 * @param @param v
	 * @return void
	 */
	public void recommenedTask(View v){
		viewPager.setCurrentItem(TAB_EARN, true);
		main_tab_earn.setChecked(true);
		main_tab_home.setChecked(false);
	}
	
	/**
	 * @author alan.xie
	 * @date 2015-3-26 下午5:01:41 
	 * @Description: 大头鸟积分墙
	 * @param v
	 * @return void
	 * @throws
	 */ 
	public void datouniao(View v){
		appConnectInstance.ShowAdsOffers();
	}
}










