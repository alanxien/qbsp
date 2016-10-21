package com.chuannuo.qianbaosuoping;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.common.Configuration;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.dao.AppDao;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.view.SliderRelativeLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @author alan.xie
 * @date 2014-12-1 上午11:54:24
 * @Description: 夺宝
 */
public class LockScreenActivity extends BaseActivity {
	private final String TAG = "LockScreenActivity";
	private SliderRelativeLayout sliderRelativeLayout;
	public static int MSG_LOCK_SUCCESS_L = 1; // 向左滑动
	public static int MSG_LOCK_SUCCESS_R = 2; // 向右滑动
	public static int MSG_DOWNLOAD_SUCCESS = 3;// 从服务器下载数据成功

	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; // 自己定义标志
	private Intent intent;
	private ImageView leftRing;
	private TextView tv_score;
	private TextView tv_time;
	private TextView tv_date;
	private TextView tv_weeks;
	private ImageView iv_icon;
	private TextView tv_title;
	private LinearLayout ll_icon;
	private LinearLayout ll_time;
	private RelativeLayout rl_main;
	private int tag = 0; // 1,下载，0,分享
	private int shareNum; // 分享次数
	private int downNum; // 下载次数
	private int total; // 滑动总次数
	private long sliderUnlockTime; // 滑动解锁

	private AppDao appDao;
	private AppInfo appInfo;

	private Calendar calendar = null;
	private List<Bitmap> mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 不要title
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏显示

		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED,
				FLAG_HOMEKEY_DISPATCHED);// 关键代码

		setContentView(R.layout.activity_lock_screen);
		initImageLoader();
		leftRing = (ImageView) findViewById(R.id.leftRing);
		tv_score = (TextView) findViewById(R.id.tv_score);
		rl_main = (RelativeLayout) findViewById(R.id.rl_main);
		ll_time = (LinearLayout) findViewById(R.id.ll_time);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_title = (TextView) findViewById(R.id.tv_title);
		ll_icon = (LinearLayout) findViewById(R.id.ll_icon);

		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_weeks = (TextView) findViewById(R.id.tv_weeks);

		ll_icon.setVisibility(View.GONE);
		initData();

		sliderRelativeLayout = (SliderRelativeLayout) findViewById(R.id.sliderLayout);
		sliderRelativeLayout.setMainHandler(handler);
		mBitmap = new ArrayList<Bitmap>();
		// sliderRelativeLayout.getBackground().setAlpha(360); //设置背景的�?明度
		// ll_time.getBackground().setAlpha(360);
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "success---onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "success---onResume");
		String dayOfWeeks = "";
		String minute = "";
		String hour = "";

		calendar = Calendar.getInstance();
		int tm = calendar.get(Calendar.MINUTE);
		int th = calendar.get(Calendar.HOUR_OF_DAY);

		if (th == 0) {
			hour = "00";
		} else if (th < 10) {
			hour = "0" + th;
		} else {
			hour = th + "";
		}

		if (tm == 0) {
			minute = "00";
		} else if (tm < 10) {
			minute = "0" + tm;
		} else {
			minute = tm + "";
		}
		tv_time.setText(hour + ":" + minute);
		tv_date.setText((calendar.get(Calendar.MONTH) + 1) + "月"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "日 ");
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			dayOfWeeks = "天";
			break;
		case 2:
			dayOfWeeks = "一";
			break;
		case 3:
			dayOfWeeks = "二";
			break;
		case 4:
			dayOfWeeks = "三";
			break;
		case 5:
			dayOfWeeks = "四";
			break;
		case 6:
			dayOfWeeks = "五";
			break;
		case 7:
			dayOfWeeks = "六";
			break;

		default:
			break;
		}
		tv_weeks.setText(" 星期" + dayOfWeeks);
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (pref.getBoolean(Constant.IS_KEY_HOME, false)) {
			editor.putBoolean(Constant.IS_KEY_HOME, false);
			editor.commit();
			this.finish();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		editor.putInt(Constant.SLIDED, 0);
		editor.commit();
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiskCache();
		cleanBitmapList();
		System.gc();
		Log.i(TAG, "onDestroy----" + pref.getInt(Constant.SLIDED, 0));
		super.onDestroy();
	}

	@Override
	protected void onUserLeaveHint() {
		if (!pref.getBoolean(Constant.IS_KEY_HOME, false)) {
			editor.putBoolean(Constant.IS_KEY_HOME, true);
			editor.commit();
		}
		super.onUserLeaveHint();
	}

	@SuppressWarnings("deprecation")
	private void initData() {

		// 如果间隔超过�?��，分享次数清零�?
		if (moreThanDays(Constant.SHARE_TIME, System.currentTimeMillis(), 1)) {
			editor.putInt(Constant.SHARE_NUM, 0);
			editor.putLong(Constant.SHARE_TIME, System.currentTimeMillis());
			editor.commit();
		}
		// 如果间隔超过�?��，下载次数清零�?
		if (moreThanDays(Constant.DOWNLOAD_TIME, System.currentTimeMillis(), 1)) {
			editor.putInt(Constant.DOWNLOAD_NUM, 0);
			editor.putLong(Constant.DOWNLOAD_TIME, System.currentTimeMillis());
			editor.commit();
		}

		if (moreThanMinute(Constant.OFFLINE_TIME, System.currentTimeMillis(),
				10)) {

		}

		total = pref.getInt(Constant.SLIDER_NUM, 0);
		downNum = pref.getInt(Constant.DOWNLOAD_NUM, 0);
		shareNum = pref.getInt(Constant.SHARE_NUM, 0);

		if (total % 2 == 1) {
			// 下载
			leftRing.setImageResource(R.drawable.icon_action_download_n);
			rl_main.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.default_download));
			initAppData();
			tag = 1;
		} else {
			// 分享
			leftRing.setImageResource(R.drawable.icon_lock_screen_share_n);
			rl_main.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.default_download));
			tag = 0;
		}

		sliderUnlockTime = pref.getLong(Constant.SLIDER_UNLOCK_TIME, 0);
		if ((System.currentTimeMillis() - sliderUnlockTime) > 3 * 60 * 60 * 1000) {
			tv_score.setVisibility(View.VISIBLE);
		} else {
			tv_score.setVisibility(View.GONE);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (MSG_LOCK_SUCCESS_L == msg.what) {
				// virbate();
				if (tag == 0) {
					myApplication.setType(Constant.PAGER2);
					if (pref.getInt(Constant.SHARE_SIZE, 0) > 0) {
						if (isTopActivity("com.chuannuo.qianbaosuoping")
								&& !pref.getBoolean(Constant.IS_FINISHED, true)) {
							myApplication.setFlag(Constant.PAGER2);
						} else {
							myApplication.setFlag(Constant.PAGER2);
							doStartApplicationWithPackageName("com.chuannuo.qianbaosuoping");
						}
					} else {
						// if(isTopActivity("com.chuannuo.qianbaosuoping") &&
						// !pref.getBoolean(Constant.IS_FINISHED, true)){
						// startActivity(new Intent(LockScreenActivity.this,
						// InvitationActivity.class));
						// }else{
						// doStartApplicationWithPackageName("com.chuannuo.qianbaosuoping");
						// startActivity(new Intent(LockScreenActivity.this,
						// InvitationActivity.class));
						// }
						startActivity(new Intent(LockScreenActivity.this,
								InvitationActivity.class));
					}

					editor.putInt(Constant.SLIDER_NUM, total + 1);
					editor.commit();
				} else {
					intent = new Intent();
					if (null == appInfo || appInfo.equals("")) {
						if (isTopActivity("com.chuannuo.qianbaosuoping")
								&& !pref.getBoolean(Constant.IS_FINISHED, true)) {
							myApplication.setFlag(1);
						} else {
							myApplication.setFlag(1);
							doStartApplicationWithPackageName("com.chuannuo.qianbaosuoping");
						}
					} else {
						Bundle bundle = new Bundle();
						bundle.putSerializable(Constant.ITEM, appInfo);
						intent.putExtras(bundle);
						intent.setClass(LockScreenActivity.this,
								DownLoadAppActivity.class);
						LockScreenActivity.this.startActivity(intent);
					}
					editor.putInt(Constant.SLIDER_NUM, total + 1);
					editor.putInt(Constant.DOWNLOAD_NUM, downNum + 1);
					editor.commit();
				}
				LockScreenActivity.this.finish();
				editor.putInt(Constant.SLIDED, 0);
				editor.commit();
			} else if (MSG_LOCK_SUCCESS_R == msg.what) {
				finish();
				sliderUnlockTime = pref.getLong(Constant.SLIDER_UNLOCK_TIME, 0);
				if ((System.currentTimeMillis() - sliderUnlockTime) > 3 * 60 * 60 * 1000) {
					addIntegral(2000, "滑屏解锁");
					editor.putLong(Constant.SLIDER_UNLOCK_TIME,
							System.currentTimeMillis());
				}
				editor.putInt(Constant.SLIDED, 0);
				editor.commit();
			} else if (MSG_DOWNLOAD_SUCCESS == msg.what) {
				getOne();
			}
		}
	};

	/**
	 * @author alan.xie
	 * @date 2014-12-1 上午11:54:41
	 * @Description: 震动
	 * @param
	 * @return void
	 */
	private void virbate() {
		Vibrator vibrator = (Vibrator) this
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

	/**
	 * 屏蔽掉返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // 返回�?
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) { // HOME�?
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) { // MENU�?
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-8 下午3:33:23
	 * @Description: 获取app数据
	 * @param
	 * @return void
	 */
	private void getAppList() {
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("channel_id", getMetaData(LockScreenActivity.this, "LEZHUAN_CHANNEL"));
		HttpUtil.get(Constant.DOWNLOAD_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {

								JSONArray jArray = response
										.getJSONArray("data");

								if (null != jArray && jArray.length() > 0) {

									JSONArray installed = myApplication
											.getjArry();
									JSONArray jarrayN = null;
									if (installed != null
											&& installed.length() > 0) {
										jarrayN = new JSONArray();
										for (int i = 0; i < installed.length(); i++) {
											for (int j = 0; j < jArray.length(); j++) {
												if (!jArray
														.getJSONObject(j)
														.getString(
																"package_name")
														.equals(installed
																.get(i))) {
													jarrayN.put(j, jArray
															.getJSONObject(j));
												}
											}
										}
									}

									if (null != jarrayN) {
										jArray = jarrayN;
									}
									for (int i = 0; i < jArray.length(); i++) {
										JSONObject obj = jArray
												.getJSONObject(i);
										ContentValues values = new ContentValues();
										values.put("resource_id",
												obj.getInt("id"));
										values.put("ad_id", obj.getInt("ad_id"));
										values.put("title",
												obj.getString("title"));
										values.put("price",
												obj.getString("price"));
										values.put("h5_big_url",
												obj.getString("h5_big_url"));
										values.put("click_type",
												obj.getInt("clicktype"));
										values.put("name",
												obj.getString("name"));
										values.put("description",
												obj.getString("description"));
										values.put("package_name",
												obj.getString("package_name"));
										values.put("brief",
												obj.getString("brief"));
										values.put("score", obj.getInt("score"));
										values.put("resource_size",
												obj.getString("resource_size"));
										values.put("file",
												obj.getString("file"));
										values.put("icon",
												obj.getString("icon"));
										values.put("b_type",
												obj.getString("btype"));
										values.put(
												"total_score",
												obj.getInt("score")
														+ obj.getInt("sign_number")
														* 5000);
										values.put("sign_time",
												obj.getInt("reportsigntime"));
										values.put("sign_number",
												obj.getInt("sign_number"));

										Long id = appDao.insert(
												Configuration.TB_APPINFO,
												values);

										if (id <= 0) {
											Toast.makeText(
													LockScreenActivity.this,
													"数据下载失败!",
													Toast.LENGTH_SHORT).show();
											appDao.close();
										}
									}

									Message msg = handler.obtainMessage();
									msg.what = MSG_DOWNLOAD_SUCCESS;
									handler.sendMessage(msg);
								}

							} else {
								Toast.makeText(LockScreenActivity.this,
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
						Toast.makeText(LockScreenActivity.this,
								"数据获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}

	private void initAppData() {
		if (null == appDao) {
			appDao = new AppDao(this);
		}

		if (moreThanMinute(Constant.OFFLINE_TIME, System.currentTimeMillis(),
				10)) {
			appDao.clearnTable(Configuration.TB_APPINFO);
			editor.putLong(Constant.OFFLINE_TIME, System.currentTimeMillis());
			editor.commit();
		} else {
			getOne();
		}

		if (null == appInfo || appInfo.equals("")) {
			getAppList();
		}

	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.ic_launcher)
//				.showImageForEmptyUri(R.drawable.ic_launcher)
				.cacheInMemory(true).cacheOnDisk(true)
				//.bitmapConfig(Bitmap.Config.RGB_565)    //设置图片的质量
	            .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY)
				.denyCacheImageMultipleSizesInMemory()
                //.memoryCacheExtraOptions(300, 200)
                .memoryCache(new WeakMemoryCache())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}

	private BroadcastReceiver timeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
				// mClockWidget.refresh(); //更新时间的方�?
			}
		}
	};

	/**
	 * @author alan.xie
	 * @date 2015-1-4 上午11:13:47
	 * @Description: 从本地数据库随机获取�?��数据
	 * @param
	 * @return void
	 */
	private void getOne() {
		String sql = "SELECT * FROM " + Configuration.TB_APPINFO
				+ " ORDER BY RANDOM() limit 1 ";
		Cursor cur = appDao.randQuery(sql, null);
		if (null != cur && cur.moveToFirst()) {
			if (null == appInfo) {
				appInfo = new AppInfo();
			}
			int resIdColumn = cur.getColumnIndex("resource_id");
			int adIdColumn = cur.getColumnIndex("ad_id");
			int titleColumn = cur.getColumnIndex("title");
			int bigUrlColumn = cur.getColumnIndex("h5_big_url");
			int nameColumn = cur.getColumnIndex("name");
			int iconColumn = cur.getColumnIndex("icon");
			int descriptionColumn = cur.getColumnIndex("description");
			int packageNameColumn = cur.getColumnIndex("package_name");
			int briefColumn = cur.getColumnIndex("brief");
			int scoreColumn = cur.getColumnIndex("score");
			int resourceSizeColumn = cur.getColumnIndex("resource_size");
			int fileColumn = cur.getColumnIndex("file");
			int bType = cur.getColumnIndex("b_type");
			int totalScore = cur.getColumnIndex("total_score");
			int reportSignTime = cur.getColumnIndex("sign_time");
			int signNumber = cur.getColumnIndex("sign_number");

			appInfo.setB_type(cur.getInt(bType));
			appInfo.setResource_id(cur.getInt(resIdColumn));
			appInfo.setAdId(cur.getInt(adIdColumn));
			appInfo.setTitle(cur.getString(titleColumn));
			appInfo.setName(cur.getString(nameColumn));
			appInfo.setDescription(cur.getString(descriptionColumn));
			appInfo.setPackage_name(cur.getString(packageNameColumn));
			appInfo.setBrief(cur.getString(briefColumn));
			appInfo.setScore(cur.getInt(scoreColumn));
			appInfo.setResource_size(cur.getString(resourceSizeColumn));
			appInfo.setTotalScore(cur.getInt(totalScore));
			appInfo.setSign_rules(cur.getInt(reportSignTime));
			appInfo.setNeedSign_times(cur.getInt(signNumber));

			String fileUrl = cur.getString(fileColumn);
			String iconUrl = cur.getString(iconColumn);
			String h5Url = cur.getString(bigUrlColumn);

			if (!fileUrl.contains("http")) {
				fileUrl = Constant.ROOT_URL + fileUrl;
			}
			if (!iconUrl.contains("http")) {
				iconUrl = Constant.ROOT_URL + iconUrl;
			}
			if (!h5Url.contains("http")) {
				h5Url = Constant.ROOT_URL + h5Url;
			}

			appInfo.setFile(fileUrl);
			appInfo.setH5_big_url(h5Url);
			appInfo.setIcon(iconUrl);

			ll_icon.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(iconUrl, iv_icon);
			tv_title.setText(appInfo.getTitle());

			ImageLoader.getInstance().loadImage(h5Url,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							Log.i(TAG, "start");
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							Log.i(TAG, "failed");
						}

						@SuppressWarnings("deprecation")
						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap bitmap) {
							rl_main.setBackgroundDrawable(new BitmapDrawable(
									getResources(), bitmap));
							mBitmap.add(bitmap);
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							Log.i(TAG, "cancel");
						}
					});

		}
	}
	
	public void cleanBitmapList(){
		if(mBitmap.size() >0){
			for(int i=0; i<mBitmap.size(); i++){
				Bitmap b = mBitmap.get(i);
				if(b != null && !b.isRecycled()){
					b.recycle();
				}
			}
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-18 下午5:42:49
	 * @Description: �?��应用程序是否在前台运�?
	 * @param @param packageName
	 * @param @return
	 * @return boolean
	 */
	private boolean isTopActivity(String packageName) {

		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			Log.i(TAG, "---------------包名-----------" + packageName);
			// 应用程序位于堆栈的顶�?
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				Log.i(TAG, packageName);
				return true;
			}
		}
		return false;
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
			packageinfo = this.getPackageManager().getPackageInfo(packagename,
					0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建�?��类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = this.getPackageManager()
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
			this.startActivity(intent);
		}
	}
}
