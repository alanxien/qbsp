package com.chuannuo.qianbaosuoping;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import net.youmi.android.offers.OffersManager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
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
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

public class BaseFragmentActivity extends FragmentActivity implements
		IUiListener {

	private static Boolean isExit = false;
	private Timer exit = null;

	public SharedPreferences pref;
	public Editor editor;
	private Intent intent;
	private CustomDialog dialog, waveDialog;
	public int convert_id;

	public IWXAPI wxApi;
	public static Tencent mTencent;

	WXWebpageObject webpage;
	WXMediaMessage msg;
	SendMessageToWX.Req req;

	@Override
	protected void onCreate(Bundle arg0) {

		initImageLoader();

		pref = this.getSharedPreferences(Constant.STUDENTS_EARN, MODE_PRIVATE);
		editor = pref.edit();

		editor.putBoolean(Constant.IS_FINISHED, false);
		editor.commit();

		// 实例化 微信分享接口
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);
		wxApi.registerApp(Constant.WX_APP_ID);

		waveDialog = new CustomDialog(BaseFragmentActivity.this,
				R.style.CustomDialog, new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						waveDialog.dismiss();
						intent = new Intent();
						intent.setClass(BaseFragmentActivity.this,
								WaveActivity.class);
						startActivity(intent);
					}
				}, 1);
		waveDialog.setTitle(getResources().getString(R.string.dg_wave_notice));
		waveDialog.setContent("您已累计做满了2万积分，现在可以去摇一摇进行抽奖了，赶快去抽奖吧，祝君好运！");
		waveDialog.setBtnStr(getResources().getString(R.string.dg_wave));
		waveDialog.setCanceledOnTouchOutside(false);
		
		super.onCreate(arg0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != mTencent) {
			mTencent.onActivityResult(requestCode, resultCode, data);
		}

	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);

		if ((pref.getInt(Constant.SCORE, 0) - pref.getInt(
				Constant.LATEST_SCROE, 0)) > 20000
				&& pref.getInt(Constant.LATEST_SCROE, 0) > 0) {
			waveDialog.show();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		editor.putBoolean(Constant.IS_FINISHED, true);
		editor.commit();
		super.onDestroy();
	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.ic_launcher)
//				.showImageForEmptyUri(R.drawable.ic_launcher)
				.cacheInMemory(true).cacheOnDisk(true)
	            .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY)
				.denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 双击退出；
			// exitBy2Click();

			dialog = new CustomDialog(this, R.style.CustomDialog,
					new CustomDialog.CustomDialogListener() {

						@Override
						public void onClick(View view) {
							switch (view.getId()) {
							case R.id.btn_left:
								dialog.dismiss();
								OffersManager.getInstance(
										BaseFragmentActivity.this).onAppExit();
								BaseFragmentActivity.this.finish();
								break;
							case R.id.btn_right:
								dialog.dismiss();
								break;
							default:
								break;
							}
						}
					}, 2);
			dialog.setTitle(getResources().getString(R.string.exit_zhuanmi));
			dialog.setContent(getResources().getString(R.string.exit_content));
			dialog.setBtnLeftStr(getResources().getString(
					R.string.exit_btn_left));
			dialog.setBtnRightStr(getResources().getString(
					R.string.exit_btn_right));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
		return false;
	}

	/**
	 * @author xin.xie 双击退出
	 */
	private void exitBy2Click() {

		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			exit = new Timer();
			exit.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					isExit = false;
				}
			}, 2000); // 如果两秒内没有按下返回键，就取消第一次点击事件；
		} else {
			this.finish();
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-11-28 上午11:30:46
	 * @Description: 分享到qq
	 * @param
	 * @return void
	 */
	public void doShareTencent(String desc, int convertId, int convertScore) {
		convert_id = convertId;
		Bundle bundle = new Bundle();

		if (mTencent == null) {
			mTencent = Tencent.createInstance(Constant.QQ_APP_ID, this);
		}
		bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
				QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		bundle.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT,
				QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		// 这条分享消息被好友点击后的跳转URL。
		bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, Constant.SHARE_URL);
		// 分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_SUMMARY不能全为空，最少必须有一个是有值的。
		bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, getResources()
				.getString(R.string.app_name) + "-注册立刻送2元");
		// 分享的图片URL
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls.add(Constant.LOGO_URL);
		bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		// 分享的消息摘要，最长50个字
		bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "我又在钱包锁屏兑换了" + desc
				+ "，注册立即送5元，小伙伴们快来下载试玩吧！");

		mTencent.shareToQzone(BaseFragmentActivity.this, bundle,
				BaseFragmentActivity.this);

	}

	/**
	 * @author alan.xie
	 * @date 2015-1-27 下午5:37:59
	 * @Description: 微信分享
	 * @param @param flag
	 * @return void
	 */
	public void wechatShare(String desc, int convertId, int convertScore) {
		webpage = new WXWebpageObject();
		webpage.webpageUrl = Constant.SHARE_URL;
		msg = new WXMediaMessage(webpage);
		msg.title = getResources().getString(R.string.app_name)
				+ "-注册立刻送2元，我又赚了" + convertScore + "元，小伙伴们快来下载试玩吧！";
		msg.description = "我又在钱包锁屏兑换了" + desc + "，注册立刻送2元哦，小伙伴们快来下载试玩吧！";
		req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis())
				+ "convert-" + convertId;
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;

		ImageLoader.getInstance().loadImage(Constant.LOGO_URL,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						Toast.makeText(BaseFragmentActivity.this, "正在分享...",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						wxApi.sendReq(req);
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap) {
						// 这里替换一张自己工程里的图片资源
						msg.setThumbImage(bitmap);
						wxApi.sendReq(req);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});
	}

	@Override
	public void onCancel() {
		Toast.makeText(this, "QQ分享取消", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onComplete(Object obj) {
		Toast.makeText(this, "QQ分享成功", Toast.LENGTH_SHORT).show();
		reportConvertShare();
	}

	@Override
	public void onError(UiError arg0) {
		Toast.makeText(this, "QQ分享错误", Toast.LENGTH_SHORT).show();
	}

	/**
	 * @author alan.xie
	 * @date 2015-2-4 下午12:22:04
	 * @Description: 上报兑换分享
	 * @param
	 * @return void
	 */
	public void reportConvertShare() {
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("convert_id", convert_id);
		params.put("code", pref.getString(Constant.CODE, ""));
		HttpUtil.post(Constant.REPORT_CONVERT_SHARE, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								Toast.makeText(
										BaseFragmentActivity.this,
										getResources().getString(
												R.string.add_score_success)
												+ "+5000", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(BaseFragmentActivity.this,
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

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}

}
