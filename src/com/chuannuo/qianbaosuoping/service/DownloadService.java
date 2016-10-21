package com.chuannuo.qianbaosuoping.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.androidprocess.AndroidAppProcess;
import com.chuannuo.qianbaosuoping.androidprocess.AndroidAppProcessLoader;
import com.chuannuo.qianbaosuoping.androidprocess.Listener;
import com.chuannuo.qianbaosuoping.common.Configuration;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.MyApplication;
import com.chuannuo.qianbaosuoping.dao.AppDao;
import com.chuannuo.qianbaosuoping.model.AppInfo;
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
 * @date 2014-12-3 下午4:44:25
 * @Description: 下载服务
 */
public class DownloadService extends Service implements Listener{

	private static String TAG = "DownloadService";
	// 标题
	private int titleId = 0;
	// 文件存储
	private File downloadDir = null;
	private File downloadFile = null;
	// 通知栏
	private NotificationManager downloadNotificationManager = null;
	private Notification notify = null;
	// 通知栏跳转Intent
	// private Intent downloadIntent = null;
	private PendingIntent downloadPendingIntent = null;
	private Builder builder;

	private MyApplication mApplication;
	private AppInfo appInfo;
	private Bitmap bitmap;

	private SharedPreferences pref;
	private Editor editor;
	private AppDao appDao;
	private int ad_install_id;

	private Timer timer;
	private boolean isRepeatDown;
	private List<AndroidAppProcess> aliList;
	private int count = 0;

	@Override
	public void onCreate() {
		Log.i("DownloadService", "service 已经启动，，，，");
		mApplication = (MyApplication) getApplication();
		initImageLoader();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		// .showImageOnLoading(R.drawable.ic_launcher)
		// .showImageForEmptyUri(R.drawable.ic_launcher)
				.cacheInMemory(true).cacheOnDisk(true)
				// .bitmapConfig(Bitmap.Config.RGB_565) //设置图片的质量
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY)
				.denyCacheImageMultipleSizesInMemory()
				// .memoryCacheExtraOptions(300, 200)
				.memoryCache(new WeakMemoryCache())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 获取传值
		appInfo = (AppInfo) intent.getSerializableExtra(Constant.ITEM);
		isRepeatDown = intent.getBooleanExtra("isRepeatDown", false);
		// 创建文件
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			downloadDir = new File(Environment.getExternalStorageDirectory(),
					Constant.DOWNLOAD_DIR);
			downloadFile = new File(downloadDir.getPath(),
					appInfo.getPackage_name() + ".apk");
		}

		// 注册 安装广播
		IntentFilter appInstallFilter = new IntentFilter();
		appInstallFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		appInstallFilter.addDataScheme("package");
		getApplicationContext().registerReceiver(appInstallReceiver,
				appInstallFilter);
		this.downloadNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= 16) {
			builder = new Notification.Builder(this);
			builder.setProgress(100, 2, false)
					.setContentTitle(appInfo.getTitle() + "-正在下载")
					// 设置通知栏标题
					.setContentText("5%")
					.setContentIntent(null)
					// 设置通知栏点击意图
					// .setNumber(number) //设置通知集合的数量
					.setTicker(appInfo.getTitle() + "-开始下载")
					// 通知首次出现在通知栏，
					.setWhen(System.currentTimeMillis())
					// 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
					.setPriority(Notification.PRIORITY_DEFAULT)
					// 设置该通知优先级
					.setAutoCancel(false)
					// 设置这个标志当用户单击面板就可以让通知将自动取消
					.setOngoing(false)
					// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
					.setDefaults(Notification.DEFAULT_VIBRATE)
					// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
					.setSmallIcon(R.drawable.ic_launcher)
					.setOnlyAlertOnce(true)
					.setLargeIcon(
							BitmapFactory.decodeResource(
									getApplicationContext().getResources(),
									R.drawable.ic_launcher));// 设置通知ICON

			// 发出通知
			downloadNotificationManager.notify(0, builder.build());
		} else {
			notify = new Notification.Builder(this)
					.setSmallIcon(R.drawable.ic_launcher) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
															// icon)
					.setTicker(appInfo.getTitle() + "-正在下载")// 设置在status
															// bar上显示的提示文字
					.setContentTitle(appInfo.getTitle() + "")// 设置在下拉status
					.setDefaults(Notification.DEFAULT_VIBRATE) // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
					.setContentText("正在下载 ,请稍后...")// TextView中显示的详细内容
					.setOnlyAlertOnce(true)
					// .setContentIntent(pendingIntent) // 关联PendingIntent
					.setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
					.getNotification(); // 需要注意build()是在API level
			notify.flags |= Notification.FLAG_AUTO_CANCEL;
			downloadNotificationManager.notify(1, notify);
		}

		/*
		 * 异步加载图标
		 */
		ImageLoader.getInstance().loadImage(appInfo.getIcon(),
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

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap) {
						if (Build.VERSION.SDK_INT >= 16) {
							
							builder.setLargeIcon(bitmap);
							// 发出通知
							downloadNotificationManager.notify(0, builder.build());
						}else{
							notify.largeIcon = bitmap;
							downloadNotificationManager.notify(1,notify);
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						Log.i(TAG, "cancel");
					}
				});

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new downloadRunnable()).start();// 这个是下载的重点，是下载的过程

		return super.onStartCommand(intent, Service.START_REDELIVER_INTENT,
				startId);
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-3 下午5:35:42
	 * @Description: 下载app
	 * @param @param downloadUrl
	 * @param @param saveFile
	 * @param @return
	 * @param @throws Exception
	 * @return long
	 */
	public long downloadFile(String downloadUrl, File saveFile)
			throws Exception {
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				mApplication.setDownload(0);
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 1 > downloadCount) {
					downloadCount += 1;
					if(Build.VERSION.SDK_INT >= 16){
						
						builder.setProgress(100, downloadCount, false)
						.setContentText(
								totalSize * 100 / updateTotalSize + "%");
						downloadNotificationManager.notify(0, builder.build());
					}else{
						notify.number = (int) (totalSize*100/updateTotalSize);
						downloadNotificationManager.notify(1,notify);
					}
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

	// 下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;

	private Handler downloadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mApplication.setDownload(2);
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				// 点击安装PendingIntent

				Uri uri = Uri.fromFile(downloadFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				downloadPendingIntent = PendingIntent.getActivity(
						DownloadService.this, 0, installIntent, 0);
				// builder.setProgress(100, 100, false).setTicker("下载完成")
				// .setContentTitle(appInfo.getTitle() + "-正在下载")
				// .setContentText("100%");
				// updateNotification.setLatestEventInfo(UpdateService.this,
				// "xxxx", "下载完成,点击安装。", updatePendingIntent);
				// downloadNotificationManager.notify(0, builder.build());
				installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				DownloadService.this.startActivity(installIntent);

				Log.i(TAG, "---开始安装---");

				// 停止服务
				downloadNotificationManager.cancelAll();
				stopSelf();
				break;
			case DOWNLOAD_FAIL:
				// 下载失败
				if(Build.VERSION.SDK_INT >= 16){
					builder.setProgress(100, 99, false).setTicker("下载失败");
					downloadNotificationManager.notify(0, builder.build());
				}else{
					notify.tickerText="下载失败";
					downloadNotificationManager.notify(1,notify);
				}
				break;
			default:
				stopSelf();
			}
		}
	};

	class downloadRunnable implements Runnable {
		Message message = downloadHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {
				// 增加权限;
				if (!downloadDir.exists()) {
					downloadDir.mkdirs();
				}
				if (!downloadFile.exists()) {
					downloadFile.createNewFile();
				}
				// 下载函数，以QQ为例子
				// 增加权限;
				long downloadSize = downloadFile(appInfo.getFile(),
						downloadFile);
				if (downloadSize > 0) {
					// 下载成功
					downloadHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				mApplication.setDownload(0);
				// 下载失败
				downloadHandler.sendMessage(message);
			}
		}
	}

	private BroadcastReceiver appInstallReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("intent: " + intent.getAction());
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
				// String packageName =
				// intent.getData().getSchemeSpecificPart();
				pref = getSharedPreferences(Constant.STUDENTS_EARN,
						MODE_PRIVATE);
				editor = pref.edit();
				if (isRepeatDown) {
					monitoring(); // 签到
				} else {
					editor.putLong(Constant.DOWNLOAD_APP_TIME,
							System.currentTimeMillis());
					editor.commit();
					adInstall();// 安装完成上报应用信息
				}
			}
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG)
						.show();
			}
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	/**
	 * @author alan.xie
	 * @date 2014-12-8 上午10:58:26
	 * @Description: 过滤已经下载安装过的app,并且上报信息
	 * @param
	 * @return void
	 */
	private void adInstall() {
		if(downloadFile.exists()){
			downloadFile.delete();
		}
		
		Log.i(TAG, "---开始上报---");
		if (pref.getInt(Constant.DOWNLOAD_TIMES, 0) == 0) {
			editor.putInt(Constant.DOWNLOAD_TIMES, 1);
			editor.putLong(Constant.DOWN_TIME, System.currentTimeMillis());
		} else {
			editor.putInt(Constant.DOWNLOAD_TIMES,
					pref.getInt(Constant.DOWNLOAD_TIMES, 0) + 1);
		}
		editor.commit();

		if (appInfo.getB_type() == 1) {
			Toast.makeText(this, "应用安装成功,试玩3分钟即可获的积分！", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(this, "应用安装成功,注册即可获的积分！", Toast.LENGTH_LONG).show();
		}

		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("ad_id", appInfo.getAdId());
		params.put("resource_id", appInfo.getResource_id());
		params.put("package_name", appInfo.getPackage_name());
		params.put("integral", appInfo.getScore());

		mApplication.setResourceId(appInfo.getResource_id());

		// 安装成功删除 app
		String[] args = { String.valueOf(appInfo.getResource_id()) };
		if (null == appDao) {
			appDao = new AppDao(DownloadService.this);
		}
		Log.i(TAG,
				"---记录删除成功---"
						+ appDao.delete(Configuration.TB_APPINFO,
								"resource_id=?", args));

		HttpUtil.get(Constant.ADINSTALL_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getString("code").equals("1")) {
								// Toast.makeText(getApplicationContext(),
								// getResources().getString(R.string.add_score_success),
								// Toast.LENGTH_SHORT).show();
								JSONObject json = response
										.getJSONObject("data");
								ad_install_id = json.getInt("id");
								Log.i(TAG, "---上报成功---");
							} else {
								Log.i(TAG, response.getString("info"));
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
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});

		if (appInfo.getScore() > 0) {
			monitoring();
		}

	}

	/**
	 * @author alan.xie
	 * @date 2015-1-21 下午2:27:12
	 * @Description: 监控程序运行时间
	 * @param
	 * @return void
	 */
	public void monitoring() {
//		editor.putLong(Constant.APP_RUNNING_TIME, System.currentTimeMillis());
//		editor.commit();
		timer = new Timer();
		ad_install_id = appInfo.getInstall_id();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				new AndroidAppProcessLoader(getApplicationContext(), DownloadService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}, 10000, 30000);

//		timer.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				if (isTopActivity(appInfo.getPackage_name())) {
//					Log.i(TAG, "---开始计时中5s---");
//					if (moreThanTimes(Constant.APP_RUNNING_TIME,
//							System.currentTimeMillis(), 3)) {
//						Log.i(TAG, "应用试玩成功----timer.cancel()");
//						timer.cancel();
//						Message msg = mHandler.obtainMessage();
//						msg.what = 1;
//						mHandler.sendMessage(msg);
//
//					}
//				} else {
//					Log.i(TAG, "应用签到失败----timer.cancel()");
//					timer.cancel();
//				}
//				editor.commit();
//			}
//		}, 3000, 60 * 1000);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				RequestParams params = new RequestParams();
				params.put("app_id", pref.getString(Constant.APPID, "0"));
				params.put("ad_install_id", ad_install_id);

				HttpUtil.post(Constant.CONFIRM_INSTALL_INTEGRAL, params,
						new JsonHttpResponseHandler() {

							public void onSuccess(int statusCode,
									Header[] headers, JSONObject response) {
								try {
									if (response.getString("code").equals("1")) {
										Log.i(TAG, "增加积分 ---- success");
										/*
										 * 增加积分成功
										 */
										Toast.makeText(
												getApplicationContext(),
												getResources()
														.getString(
																R.string.add_score_success)
														+ "+"
														+ appInfo.getScore(),
												Toast.LENGTH_LONG).show();
										editor.putInt(Constant.SCORE,
												pref.getInt(Constant.SCORE, 0)
														+ appInfo.getScore());
										editor.commit();
									} else {
										Toast.makeText(getApplicationContext(),
												response.getString("info"),
												Toast.LENGTH_SHORT).show();
									}
								} catch (NotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							};

							public void onFailure(int statusCode,
									Header[] headers, Throwable throwable,
									JSONObject errorResponse) {

							};
						});
				break;
			case 2:
				RequestParams p = new RequestParams();
				p.put("app_id", pref.getString(Constant.APPID, "0"));
				p.put("ad_install_id", ad_install_id);

				/*
				 * 签到
				 */
				HttpUtil.get(Constant.REPEAT_SIGN_URL, p,
						new JsonHttpResponseHandler() {
							public void onSuccess(int statusCode,
									Header[] headers,
									org.json.JSONObject response) {
								try {
									if (response.getInt("code") == 1) {
										Toast.makeText(getApplicationContext(),
												"恭喜您，签到成功！", Toast.LENGTH_SHORT)
												.show();
										editor.putInt(
												Constant.APP_SIGN_IS_SUCCESS, 2); // 签到成功
									} else {
										Toast.makeText(getApplicationContext(),
												response.getString("info"),
												Toast.LENGTH_SHORT).show();
										editor.putInt(
												Constant.APP_SIGN_IS_SUCCESS,
												-1); // 签到失败
									}
									editor.commit();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							};

							public void onFailure(int statusCode,
									Header[] headers, Throwable throwable,
									org.json.JSONObject errorResponse) {
								Toast.makeText(getApplicationContext(),
										"抱歉，签到失败！", Toast.LENGTH_SHORT).show();
								editor.putInt(Constant.APP_SIGN_IS_SUCCESS, -1); // 签到失败
								editor.commit();
							};
						});
				break;
			default:
				break;
			}
		};
	};

	/**
	 * @author alan.xie
	 * @date 2014-12-18 下午5:42:49
	 * @Description: 检测应用程序是否在前台运行
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
			// 应用程序位于堆栈的顶层
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
	 * @date 2014-10-29 上午9:40:47
	 * @Description: 计算时间间隔是否大于days
	 * @param @return
	 * @return Boolean
	 */
	public Boolean moreThanTimes(String type, long currTimes, int minute) {
		long times = pref.getLong(type, 0);
		if (currTimes - times > minute * 60 * 1000) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.chuannuo.qianbaosuoping.androidprocess.Listener#onComplete(java.util.List)
	 */
	@Override
	public void onComplete(List<AndroidAppProcess> processes) {
		if(aliList == null){
			aliList = new ArrayList<AndroidAppProcess>();
		}else{
			aliList.clear();
		}
		int size = processes.size();
		AndroidAppProcess aProcess;
		count++;
		for(int i=0;i<size;i++){
			aProcess = processes.get(i);
			if(aProcess.getPackageName() != null && aProcess.getPackageName().equals(appInfo.getPackage_name().trim())){
				aliList.add(aProcess);
			}
		}
		
		int l =aliList.size();
		if(l==0){
			//体验失败
		}else {
			Boolean isFg = false;
			for(int i=0; i<l;i++){
				if(aliList.get(i).foreground){
					isFg = true;
				}
			}
			
			if(isFg){
				if(count == 5){
					Toast.makeText(getApplicationContext(), "您已经体验了2分钟，继续体验3分钟！即可获得积分！", Toast.LENGTH_SHORT).show();
				}else if(count == 7){
					//体验成功
					if(isRepeatDown){
						Message msg = mHandler.obtainMessage();
						msg.what = 2;
						mHandler.sendMessage(msg);
					}else{
						Message msg = mHandler.obtainMessage();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}
					
				}
			}else{
				//体验失败
			}
		}
		
		if(count >=7){
			timer.cancel();
			count=0;
		}
	}

//	public void signIn() {
//		editor.putLong(Constant.APP_RUNNING_TIME, System.currentTimeMillis());
//		editor.commit();
//
//		Toast.makeText(this, "试玩两分钟即完成签到！", Toast.LENGTH_SHORT).show();
//		timer = new Timer();
//
//		timer.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				if (isTopActivity(appInfo.getPackage_name())) {
//					if (moreThanTimes(Constant.APP_RUNNING_TIME,
//							System.currentTimeMillis(), 2)) {
//						Log.i(TAG, "---开始签到。。。---");
//						timer.cancel();
//						ad_install_id = appInfo.getInstall_id();
//						Message msg = mHandler.obtainMessage(2);
//						mHandler.sendMessage(msg);
//					} else {
//						Log.i(TAG, "---正在签到。。。---");
//						editor.putInt(Constant.APP_SIGN_IS_SUCCESS, 1); // 签到中
//					}
//				} else {
//					Log.i(TAG, "---退出签到---");
//					timer.cancel();
//				}
//				editor.commit();
//			}
//		}, 3000, 5000);
//	}
}