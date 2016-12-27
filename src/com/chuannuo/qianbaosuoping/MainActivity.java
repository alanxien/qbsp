package com.chuannuo.qianbaosuoping;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.Toast;
import cn.dow.android.DOW;

import com.chuannuo.qianbaosuoping.adapter.FragmentAdapter;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.MyApplication;
import com.chuannuo.qianbaosuoping.common.PhoneInformation;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.service.DownloadService;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.chuannuo.qianbaosuoping.view.CustomViewPager;
import com.chuannuo.tangguo.TangGuoWall;
import com.chuannuoq.DevInit;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.tauth.Tencent;
import com.umeng.fb.FeedbackAgent;
/**
 * @author alan.xie
 * @date 2014-10-13 下午5:56:18
 * @Description: 主界面
 */
public class MainActivity extends BaseFragmentActivity implements
		OnClickListener{

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
	private CustomDialog dialogConvert,shareDialog,depthDialog,adAlertDialog;

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
	private CustomDialog updateDialog;
	

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

		
//		if (pref.getBoolean(Constant.IS_LOCK_SCREEN, true)) {
//			if(null == intent){
//				intent = new Intent(MainActivity.this, LockScreenService.class);
//			}
//			editor.putInt(Constant.SLIDED, 0);
//			editor.commit();
//			startService(intent); //这里要显示的调用服务
//		}
		
		//PollingUtils.startPollingService(this, 24*60*60, PollingService.class); //启动轮询服务，任务推送

		if(agent == null){
			agent = new FeedbackAgent(this);
		}
		
		// 初始化
		initOfferWall();

		initView();

		initData();
		depthTask();
		
		main_tab_home.setChecked(true);

		checkVersions();
	}
	
	private void checkVersions(){
		String versionName = PhoneInformation.getVersionName(MainActivity.this);
		
		RequestParams params = new RequestParams();
		params.put("updateapk_version_name", versionName);
		HttpUtil.get(Constant.UPDATE, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							int error = response.getInt("error");
							if(error==1){
								//有更新，下载
								String updateVersion = response.getString("update_version");
								String updateUrl = response.getString("update_url");
								
								Message msg = mHandler.obtainMessage();
								msg.what = 2;
								Bundle b = new Bundle();
								b.putString("updateVersion", updateVersion);
								b.putString("updateUrl", updateUrl);
								msg.setData(b);
								mHandler.sendMessage(msg);
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
					
				});
	}

	private void initOfferWall() {
		/*
		 * 点乐积分墙
		 */
		DevInit.initGoogleContext(this, "b3400d657800e5914b2d3c65b55660ae");
		DevInit.setCurrentUserID(this, "");

		/*
		 * 多盟积分墙
		 */
		DOW.getInstance(this).init();

		/*
		 * 有米积分墙
		 */
		AdManager.getInstance(this).init("0ffb317788960341",
				"f062b23504663252", false);
		OffersManager.getInstance(this).onAppLaunch();
		
		/*
		 * 初始化积分墙  必须放在入口activity中
		 */
		TangGuoWall.init(this,pref.getString(Constant.APPID, "0"));
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
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				getUserAdAlert();
			}
		}, 5000);
	}
	
	private void getUserAdAlert(){
		adAlertDialog = new CustomDialog(this, R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						adAlertDialog.dismiss();
					}
				}, 1);
		adAlertDialog.setTitle("图片上传审核");
		adAlertDialog.setBtnStr("我知道了");
		adAlertDialog.setCanceledOnTouchOutside(false);
		
		RequestParams p = new RequestParams();
		p.put("appid", pref.getString(Constant.APPID, "0"));
		HttpUtil.post(Constant.GET_USER_AD_ALERT, p, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getInt("code") == 1){
						JSONObject obj = response.getJSONObject("data");
						if(obj != null){
							JSONArray passArray = obj.getJSONArray("pass");
							JSONArray failArray = obj.getJSONArray("fail");
							JSONObject passObject;
							JSONObject failObject;
							String passInfo = "";
							String failInfo = "";
							String ids ="";
							
							DecimalFormat df = new DecimalFormat("0.00");//格式化小数
							df.setRoundingMode(RoundingMode.DOWN);
							if(passArray !=null && !passArray.equals("[]") && passArray.length()>0){
								for(int i=0; i<passArray.length();i++){
									passObject = passArray.getJSONObject(i);
									if(passObject != null){
										double integral = (double) (Long.parseLong(response.getString("photo_integral"))/10000.0);
										String money = df.format(integral/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
										passInfo = passInfo + passObject.getString("title")+" （审核通过）  +"+money+"元\n";
										ids = ids+passObject.getInt("ad_install_id")+",";
									}
									
								}
								
							}
							
							if(failArray !=null && !failArray.equals("[]") && failArray.length()>0){
								for(int i=0; i<failArray.length();i++){
									failObject = failArray.getJSONObject(i);
									if(failObject != null){
										failInfo = failInfo + failObject.getString("title")+failObject.getString("photo_remarks")+" （审核失败）\n";
										ids = ids+failObject.getInt("ad_install_id")+",";
									}
									
								}
								
							}

							if(passInfo.length() > 0 || failInfo.length()>0){
								adAlertDialog.setContent(passInfo+failInfo);
								adAlertDialog.show();
							}
							if(ids.length() > 0){
								Message msg = mHandler.obtainMessage();
								msg.what = 1;
								msg.obj = ids.substring(0,ids.length()-1);;
								mHandler.sendMessage(msg);
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
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	private void modifyAdAlert(Object obj){
		String adIds = (String) obj;
		RequestParams p = new RequestParams();
		p.put("app_id", pref.getString(Constant.APPID, "0"));
		p.put("ad_install_id_list", adIds);
		HttpUtil.post(Constant.MODIFY_USER_ADALERT, p, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getInt("code") == 1){
						
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
	}
	
	Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(msg.obj  != null){
					modifyAdAlert(msg.obj);
				}
				break;
			case 2:
				final Bundle data = msg.getData();
				
				if(data!=null && !data.getString("updateVersion").isEmpty()
						&& !data.getString("updateUrl").isEmpty()){
					updateDialog = new CustomDialog(MainActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
						
						@Override
						public void onClick(View view) {
							switch (view.getId()) {
							case R.id.btn_left:
								updateDialog.dismiss();
								break;
							case R.id.btn_right:
								// 更新版本
								Intent intent = new Intent(
										MainActivity.this,
										DownloadService.class);
								AppInfo app = new AppInfo();
								app.setTitle("钱包夺宝_v"+data.getString("updateVersion"));
								app.setPackage_name("com.chuannuo.qianbaosuoping");
								app.setFile(data.getString("updateUrl"));
								intent.putExtra(Constant.ITEM, app);
								intent.putExtra("isUpdate", true);
//								intent.putExtra("packageUrl",
//										data.getString("updateUrl"));
//								intent.putExtra("updateVersion",
//										data.getString("updateVersion"));
								startService(intent);
								updateDialog.dismiss();
								break;
							default:
								break;
							}
						}
					}, 2);
					updateDialog.setTitle("版本更新");
					updateDialog.setBtnLeftStr("取消更新");
					updateDialog.setBtnRightStr("立即更新");
					updateDialog.setCancelable(false);
					updateDialog.setCanceledOnTouchOutside(false);
					updateDialog.setContent("检查到有新版本：V"+data.getString("updateVersion")+"\n"
							+"1.更好的用户体验，bug修复。\n"
							+"2.兑换审核加快，新增任务多。");
					updateDialog.show();
				}
				
				break;
			default:
				break;
			}
		};
	};

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
			viewPager.setCurrentItem(TAB_EARN, false);
			main_tab_earn.setChecked(true);
			main_tab_home.setChecked(false);
			myApplication.setFlag(0);
		}
		if(myApplication.getFlag() == 2){
			myApplication.setType(Constant.PAGER2);
			viewPager.setCurrentItem(TAB_EARN, false);
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
			viewPager.setCurrentItem(TAB_HOME, false);
			break;
		case R.id.main_tab_exchange:
			viewPager.setCurrentItem(TAB_EXCHANGE, false);
			break;
		case R.id.main_tab_earn:
			viewPager.setCurrentItem(TAB_EARN, false);
			break;
		case R.id.main_tab_snatch:
			viewPager.setCurrentItem(TAB_SNATCH,false);
			break;
		case R.id.main_tab_me:
			viewPager.setCurrentItem(TAB_ME, false);
			break;
		case R.id.main_tab_more:
			viewPager.setCurrentItem(TAB_MORE, false);
			break;
		default:
			break;
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-11-25 下午6:16:52
	 * @Description: 判断夺宝是否已经启动
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
	 * @Description: 判断夺宝服务是否运行
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
						viewPager.setCurrentItem(TAB_EARN, false);
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

								if (null != jArray && jArray.length() > 0) {
									// depthAppList = new ArrayList<AppInfo>();
									for (int i = 0; i < jArray.length(); i++) {

										JSONObject obj = jArray
												.getJSONObject(i);
										String s = obj.getString("resourceArr");
										if (!s.equals("[]")&&!s.equals("false")) {
											JSONObject childObj = obj
													.getJSONObject("resourceArr");
											if(childObj !=null && (isSignTime(obj)||(obj.getInt("is_photo_task") == 1 && 
													obj.getInt("photo_status")==0))){
												depthDialog.setContent("您有未完成任务，可以签到了，马上去[ 推荐任务--未完成任务 ]签到");
												depthDialog.show();
											}
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
		viewPager.setCurrentItem(TAB_EARN, false);
		main_tab_earn.setChecked(true);
		main_tab_home.setChecked(false);
	}
}










