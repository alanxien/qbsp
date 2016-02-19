package com.chuannuo.qianbaosuoping.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.chuannuo.qianbaosuoping.DownLoadAppActivity;
import com.chuannuo.qianbaosuoping.LockScreenActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.MyApplication;
import com.chuannuo.qianbaosuoping.model.AppInfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.Notification.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

/**
 * @author alan.xie
 * @date 2015-2-9 ����10:47:17
 * @Description: ��ѯ
 */
public class PollingService extends Service {
	private static final String TAG = "PollingService";

	private NotificationManager mManager;
	private Builder builder;
	private List<AppInfo> appInfoList;

	private SharedPreferences pref;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		pref = getSharedPreferences(Constant.STUDENTS_EARN, MODE_PRIVATE);

		initImageLoader();
		super.onCreate();
	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.ic_launcher)
//				.showImageForEmptyUri(R.drawable.ic_launcher)
				.cacheInMemory(true).cacheOnDisk(true)
				//.bitmapConfig(Bitmap.Config.RGB_565)    //����ͼƬ������
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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("limit", 1);
		HttpUtil.post(Constant.PUSH_MSG, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if (response.getInt("code") == 1) {
						JSONObject jObj = response.getJSONObject("data");
						JSONArray jArray = jObj.getJSONArray("task");
						if (null != jArray && jArray.length() > 0) {
							appInfoList = new ArrayList<AppInfo>();
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject obj = jArray.getJSONObject(i)
										.getJSONObject("resource");
								AppInfo appInfo = new AppInfo();

								appInfo.setResource_id(obj.getInt("id"));
								appInfo.setAdId(obj.getInt("ad_id"));
								appInfo.setTitle(obj.getString("title"));
								appInfo.setName(obj.getString("name"));
								appInfo.setDescription(obj
										.getString("description"));
								appInfo.setPackage_name(obj
										.getString("package_name"));
								appInfo.setBrief(obj.getString("brief"));
								appInfo.setScore(obj.getInt("score"));
								appInfo.setResource_size(obj
										.getString("resource_size"));
								appInfo.setB_type(obj.getInt("btype"));
								appInfo.setTotalScore(obj.getInt("score")
										+ obj.getInt("sign_number") * 5000);
								appInfo.setSign_rules(obj
										.getInt("reportsigntime"));
								appInfo.setNeedSign_times(obj
										.getInt("sign_number"));

								String fileUrl = obj.getString("file");
								String iconUrl = obj.getString("icon");
								String h5Url = obj.getString("h5_big_url");

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

								appInfoList.add(appInfo);
							}

							//showNotification();
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
		return super.onStartCommand(intent, Service.START_REDELIVER_INTENT,
				startId);
	}

	/**
	 * @author alan.xie
	 * @date 2015-2-9 ����11:55:44
	 * @Description: ��ʼ��֪ͨ��
	 * @return void
	 * @throws
	 */
//	private void showNotification() {
//		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		builder = new Notification.Builder(this);
//
//		Iterator<AppInfo> iter = appInfoList.iterator();
//		while (iter.hasNext()) {
//			final AppInfo aInfo = iter.next();
//
//			Bundle bundle = new Bundle();
//			Intent intent = new Intent(this, DownLoadAppActivity.class);
//			bundle.putSerializable(Constant.ITEM, aInfo);
//			intent.putExtras(bundle);
//			PendingIntent pendingIntent = PendingIntent.getActivity(this,
//					aInfo.getResource_id(), intent,
//					PendingIntent.FLAG_UPDATE_CURRENT);
//
//			builder.setContentTitle("Ǯ������-" + aInfo.getTitle())
//					.setContentText(
//							"��ĺ��������Ƽ�����-" + aInfo.getTitle() + "������������׬ȡ���࣡")
//					// ����֪ͨ������
//					.setContentIntent(pendingIntent)
//					// ����֪ͨ�������ͼ
//					.setTicker("Ǯ��������������")
//					// ֪ͨ�״γ�����֪ͨ����
//					.setWhen(System.currentTimeMillis())
//					// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ��һ����ϵͳ��ȡ����ʱ��
//					.setPriority(Notification.PRIORITY_DEFAULT)
//					// ���ø�֪ͨ���ȼ�
//					.setOngoing(false)
//					// ture��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)
//					.setDefaults(Notification.DEFAULT_VIBRATE)
//					// ��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ��������
//					.setSmallIcon(R.drawable.ic_launcher)
//					.setAutoCancel(true)
//					.setLargeIcon(
//							BitmapFactory.decodeResource(
//									getApplicationContext().getResources(),
//									R.drawable.ic_launcher));// ����֪ͨICON
//			mManager.notify(aInfo.getResource_id(), builder.build());
//
//			/*
//			 * �첽����ͼ��
//			 */
//			ImageLoader.getInstance().loadImage(aInfo.getIcon(),
//					new ImageLoadingListener() {
//
//						@Override
//						public void onLoadingStarted(String arg0, View arg1) {
//							Log.i(TAG, "start");
//						}
//
//						@Override
//						public void onLoadingFailed(String arg0, View arg1,
//								FailReason arg2) {
//							Log.i(TAG, "failed");
//						}
//
//						@Override
//						public void onLoadingComplete(String arg0, View arg1,
//								Bitmap bitmap) {
//							builder.setLargeIcon(bitmap);
//							// ����֪ͨ
//							mManager.notify(aInfo.getResource_id(),
//									builder.build());
//						}
//
//						@Override
//						public void onLoadingCancelled(String arg0, View arg1) {
//							Log.i(TAG, "cancel");
//						}
//					});
//		}
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("TAG", "Service:onDestroy");
	}
}
