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

		// ʵ���� ΢�ŷ���ӿ�
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
		waveDialog.setContent("�����ۼ�������2����֣����ڿ���ȥҡһҡ���г齱�ˣ��Ͽ�ȥ�齱�ɣ�ף�����ˣ�");
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
			// ˫���˳���
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
	 * @author xin.xie ˫���˳�
	 */
	private void exitBy2Click() {

		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
			exit = new Timer();
			exit.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					isExit = false;
				}
			}, 2000); // ���������û�а��·��ؼ�����ȡ����һ�ε���¼���
		} else {
			this.finish();
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-11-28 ����11:30:46
	 * @Description: ����qq
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
		// ����������Ϣ�����ѵ�������תURL��
		bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, Constant.SHARE_URL);
		// ����ı��⡣ע��PARAM_TITLE��PARAM_IMAGE_URL��PARAM_SUMMARY����ȫΪ�գ����ٱ�����һ������ֵ�ġ�
		bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, getResources()
				.getString(R.string.app_name) + "-ע��������2Ԫ");
		// �����ͼƬURL
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls.add(Constant.LOGO_URL);
		bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		// �������ϢժҪ���50����
		bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "������Ǯ�������һ���" + desc
				+ "��ע��������5Ԫ��С����ǿ�����������ɣ�");

		mTencent.shareToQzone(BaseFragmentActivity.this, bundle,
				BaseFragmentActivity.this);

	}

	/**
	 * @author alan.xie
	 * @date 2015-1-27 ����5:37:59
	 * @Description: ΢�ŷ���
	 * @param @param flag
	 * @return void
	 */
	public void wechatShare(String desc, int convertId, int convertScore) {
		webpage = new WXWebpageObject();
		webpage.webpageUrl = Constant.SHARE_URL;
		msg = new WXMediaMessage(webpage);
		msg.title = getResources().getString(R.string.app_name)
				+ "-ע��������2Ԫ������׬��" + convertScore + "Ԫ��С����ǿ�����������ɣ�";
		msg.description = "������Ǯ�������һ���" + desc + "��ע��������2ԪŶ��С����ǿ�����������ɣ�";
		req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis())
				+ "convert-" + convertId;
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;

		ImageLoader.getInstance().loadImage(Constant.LOGO_URL,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						Toast.makeText(BaseFragmentActivity.this, "���ڷ���...",
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
						// �����滻һ���Լ��������ͼƬ��Դ
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
		Toast.makeText(this, "QQ����ȡ��", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onComplete(Object obj) {
		Toast.makeText(this, "QQ����ɹ�", Toast.LENGTH_SHORT).show();
		reportConvertShare();
	}

	@Override
	public void onError(UiError arg0) {
		Toast.makeText(this, "QQ�������", Toast.LENGTH_SHORT).show();
	}

	/**
	 * @author alan.xie
	 * @date 2015-2-4 ����12:22:04
	 * @Description: �ϱ��һ�����
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
