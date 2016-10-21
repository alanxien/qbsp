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
 * @date 2014-12-3 ����4:44:25
 * @Description: ���ط���
 */
public class DownloadService extends Service implements Listener{

	private static String TAG = "DownloadService";
	// ����
	private int titleId = 0;
	// �ļ��洢
	private File downloadDir = null;
	private File downloadFile = null;
	// ֪ͨ��
	private NotificationManager downloadNotificationManager = null;
	private Notification notify = null;
	// ֪ͨ����תIntent
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
		Log.i("DownloadService", "service �Ѿ�������������");
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
				// .bitmapConfig(Bitmap.Config.RGB_565) //����ͼƬ������
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
		// ��ȡ��ֵ
		appInfo = (AppInfo) intent.getSerializableExtra(Constant.ITEM);
		isRepeatDown = intent.getBooleanExtra("isRepeatDown", false);
		// �����ļ�
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			downloadDir = new File(Environment.getExternalStorageDirectory(),
					Constant.DOWNLOAD_DIR);
			downloadFile = new File(downloadDir.getPath(),
					appInfo.getPackage_name() + ".apk");
		}

		// ע�� ��װ�㲥
		IntentFilter appInstallFilter = new IntentFilter();
		appInstallFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		appInstallFilter.addDataScheme("package");
		getApplicationContext().registerReceiver(appInstallReceiver,
				appInstallFilter);
		this.downloadNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= 16) {
			builder = new Notification.Builder(this);
			builder.setProgress(100, 2, false)
					.setContentTitle(appInfo.getTitle() + "-��������")
					// ����֪ͨ������
					.setContentText("5%")
					.setContentIntent(null)
					// ����֪ͨ�������ͼ
					// .setNumber(number) //����֪ͨ���ϵ�����
					.setTicker(appInfo.getTitle() + "-��ʼ����")
					// ֪ͨ�״γ�����֪ͨ����
					.setWhen(System.currentTimeMillis())
					// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ��һ����ϵͳ��ȡ����ʱ��
					.setPriority(Notification.PRIORITY_DEFAULT)
					// ���ø�֪ͨ���ȼ�
					.setAutoCancel(false)
					// ���������־���û��������Ϳ�����֪ͨ���Զ�ȡ��
					.setOngoing(false)
					// ture��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)
					.setDefaults(Notification.DEFAULT_VIBRATE)
					// ��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ��������
					.setSmallIcon(R.drawable.ic_launcher)
					.setOnlyAlertOnce(true)
					.setLargeIcon(
							BitmapFactory.decodeResource(
									getApplicationContext().getResources(),
									R.drawable.ic_launcher));// ����֪ͨICON

			// ����֪ͨ
			downloadNotificationManager.notify(0, builder.build());
		} else {
			notify = new Notification.Builder(this)
					.setSmallIcon(R.drawable.ic_launcher) // ����״̬���е�СͼƬ���ߴ�һ�㽨����24��24�����ͼƬͬ��Ҳ��������״̬��������ʾ�������������Ҫ���������ͼƬ������ʹ��setLargeIcon(Bitmap
															// icon)
					.setTicker(appInfo.getTitle() + "-��������")// ������status
															// bar����ʾ����ʾ����
					.setContentTitle(appInfo.getTitle() + "")// ����������status
					.setDefaults(Notification.DEFAULT_VIBRATE) // bar��Activity���������е�NotififyMessage��TextView����ʾ�ı���
					.setContentText("�������� ,���Ժ�...")// TextView����ʾ����ϸ����
					.setOnlyAlertOnce(true)
					// .setContentIntent(pendingIntent) // ����PendingIntent
					.setNumber(1) // ��TextView���ҷ���ʾ�����֣��ɷŴ�ͼƬ���������Ҳࡣ���numberͬʱҲ��һ�����кŵ����ң��������������֪ͨ��ͬһID��������ָ����ʾ��һ����
					.getNotification(); // ��Ҫע��build()����API level
			notify.flags |= Notification.FLAG_AUTO_CANCEL;
			downloadNotificationManager.notify(1, notify);
		}

		/*
		 * �첽����ͼ��
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
							// ����֪ͨ
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

		// ����һ���µ��߳����أ����ʹ��Serviceͬ�����أ��ᵼ��ANR���⣬Service����Ҳ������
		new Thread(new downloadRunnable()).start();// ��������ص��ص㣬�����صĹ���

		return super.onStartCommand(intent, Service.START_REDELIVER_INTENT,
				startId);
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-3 ����5:35:42
	 * @Description: ����app
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
				// Ϊ�˷�ֹƵ����֪ͨ����Ӧ�óԽ����ٷֱ�����10��֪ͨһ��
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

	// ����״̬
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;

	private Handler downloadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mApplication.setDownload(2);
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				// �����װPendingIntent

				Uri uri = Uri.fromFile(downloadFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				downloadPendingIntent = PendingIntent.getActivity(
						DownloadService.this, 0, installIntent, 0);
				// builder.setProgress(100, 100, false).setTicker("�������")
				// .setContentTitle(appInfo.getTitle() + "-��������")
				// .setContentText("100%");
				// updateNotification.setLatestEventInfo(UpdateService.this,
				// "xxxx", "�������,�����װ��", updatePendingIntent);
				// downloadNotificationManager.notify(0, builder.build());
				installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				DownloadService.this.startActivity(installIntent);

				Log.i(TAG, "---��ʼ��װ---");

				// ֹͣ����
				downloadNotificationManager.cancelAll();
				stopSelf();
				break;
			case DOWNLOAD_FAIL:
				// ����ʧ��
				if(Build.VERSION.SDK_INT >= 16){
					builder.setProgress(100, 99, false).setTicker("����ʧ��");
					downloadNotificationManager.notify(0, builder.build());
				}else{
					notify.tickerText="����ʧ��";
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
				// ����Ȩ��;
				if (!downloadDir.exists()) {
					downloadDir.mkdirs();
				}
				if (!downloadFile.exists()) {
					downloadFile.createNewFile();
				}
				// ���غ�������QQΪ����
				// ����Ȩ��;
				long downloadSize = downloadFile(appInfo.getFile(),
						downloadFile);
				if (downloadSize > 0) {
					// ���سɹ�
					downloadHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				mApplication.setDownload(0);
				// ����ʧ��
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
					monitoring(); // ǩ��
				} else {
					editor.putLong(Constant.DOWNLOAD_APP_TIME,
							System.currentTimeMillis());
					editor.commit();
					adInstall();// ��װ����ϱ�Ӧ����Ϣ
				}
			}
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				Toast.makeText(context, "ж�سɹ�" + packageName, Toast.LENGTH_LONG)
						.show();
			}
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				Toast.makeText(context, "�滻�ɹ�" + packageName, Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	/**
	 * @author alan.xie
	 * @date 2014-12-8 ����10:58:26
	 * @Description: �����Ѿ����ذ�װ����app,�����ϱ���Ϣ
	 * @param
	 * @return void
	 */
	private void adInstall() {
		if(downloadFile.exists()){
			downloadFile.delete();
		}
		
		Log.i(TAG, "---��ʼ�ϱ�---");
		if (pref.getInt(Constant.DOWNLOAD_TIMES, 0) == 0) {
			editor.putInt(Constant.DOWNLOAD_TIMES, 1);
			editor.putLong(Constant.DOWN_TIME, System.currentTimeMillis());
		} else {
			editor.putInt(Constant.DOWNLOAD_TIMES,
					pref.getInt(Constant.DOWNLOAD_TIMES, 0) + 1);
		}
		editor.commit();

		if (appInfo.getB_type() == 1) {
			Toast.makeText(this, "Ӧ�ð�װ�ɹ�,����3���Ӽ��ɻ�Ļ��֣�", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(this, "Ӧ�ð�װ�ɹ�,ע�ἴ�ɻ�Ļ��֣�", Toast.LENGTH_LONG).show();
		}

		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("ad_id", appInfo.getAdId());
		params.put("resource_id", appInfo.getResource_id());
		params.put("package_name", appInfo.getPackage_name());
		params.put("integral", appInfo.getScore());

		mApplication.setResourceId(appInfo.getResource_id());

		// ��װ�ɹ�ɾ�� app
		String[] args = { String.valueOf(appInfo.getResource_id()) };
		if (null == appDao) {
			appDao = new AppDao(DownloadService.this);
		}
		Log.i(TAG,
				"---��¼ɾ���ɹ�---"
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
								Log.i(TAG, "---�ϱ��ɹ�---");
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
	 * @date 2015-1-21 ����2:27:12
	 * @Description: ��س�������ʱ��
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
//					Log.i(TAG, "---��ʼ��ʱ��5s---");
//					if (moreThanTimes(Constant.APP_RUNNING_TIME,
//							System.currentTimeMillis(), 3)) {
//						Log.i(TAG, "Ӧ������ɹ�----timer.cancel()");
//						timer.cancel();
//						Message msg = mHandler.obtainMessage();
//						msg.what = 1;
//						mHandler.sendMessage(msg);
//
//					}
//				} else {
//					Log.i(TAG, "Ӧ��ǩ��ʧ��----timer.cancel()");
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
										Log.i(TAG, "���ӻ��� ---- success");
										/*
										 * ���ӻ��ֳɹ�
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
				 * ǩ��
				 */
				HttpUtil.get(Constant.REPEAT_SIGN_URL, p,
						new JsonHttpResponseHandler() {
							public void onSuccess(int statusCode,
									Header[] headers,
									org.json.JSONObject response) {
								try {
									if (response.getInt("code") == 1) {
										Toast.makeText(getApplicationContext(),
												"��ϲ����ǩ���ɹ���", Toast.LENGTH_SHORT)
												.show();
										editor.putInt(
												Constant.APP_SIGN_IS_SUCCESS, 2); // ǩ���ɹ�
									} else {
										Toast.makeText(getApplicationContext(),
												response.getString("info"),
												Toast.LENGTH_SHORT).show();
										editor.putInt(
												Constant.APP_SIGN_IS_SUCCESS,
												-1); // ǩ��ʧ��
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
										"��Ǹ��ǩ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
								editor.putInt(Constant.APP_SIGN_IS_SUCCESS, -1); // ǩ��ʧ��
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
	 * @date 2014-12-18 ����5:42:49
	 * @Description: ���Ӧ�ó����Ƿ���ǰ̨����
	 * @param @param packageName
	 * @param @return
	 * @return boolean
	 */
	private boolean isTopActivity(String packageName) {

		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			Log.i(TAG, "---------------����-----------" + packageName);
			// Ӧ�ó���λ�ڶ�ջ�Ķ���
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
	 * @date 2014-10-29 ����9:40:47
	 * @Description: ����ʱ�����Ƿ����days
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
			//����ʧ��
		}else {
			Boolean isFg = false;
			for(int i=0; i<l;i++){
				if(aliList.get(i).foreground){
					isFg = true;
				}
			}
			
			if(isFg){
				if(count == 5){
					Toast.makeText(getApplicationContext(), "���Ѿ�������2���ӣ���������3���ӣ����ɻ�û��֣�", Toast.LENGTH_SHORT).show();
				}else if(count == 7){
					//����ɹ�
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
				//����ʧ��
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
//		Toast.makeText(this, "���������Ӽ����ǩ����", Toast.LENGTH_SHORT).show();
//		timer = new Timer();
//
//		timer.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				if (isTopActivity(appInfo.getPackage_name())) {
//					if (moreThanTimes(Constant.APP_RUNNING_TIME,
//							System.currentTimeMillis(), 2)) {
//						Log.i(TAG, "---��ʼǩ��������---");
//						timer.cancel();
//						ad_install_id = appInfo.getInstall_id();
//						Message msg = mHandler.obtainMessage(2);
//						mHandler.sendMessage(msg);
//					} else {
//						Log.i(TAG, "---����ǩ��������---");
//						editor.putInt(Constant.APP_SIGN_IS_SUCCESS, 1); // ǩ����
//					}
//				} else {
//					Log.i(TAG, "---�˳�ǩ��---");
//					timer.cancel();
//				}
//				editor.commit();
//			}
//		}, 3000, 5000);
//	}
}