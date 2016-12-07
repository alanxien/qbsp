package com.chuannuo.qianbaosuoping;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.PhoneInformation;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.service.DownloadService;
import com.chuannuo.qianbaosuoping.view.CustomADImageDialog;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.chuannuo.tangguo.TangGuoActivity;
import com.chuannuo.tangguo.listener.ResponseStateListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @author alan.xie
 * @date 2015-3-3 上午10:58:06
 * @Description: 钱包夺宝 应用下载
 */
public class DownLoadAppActivity extends BaseActivity implements
		OnClickListener {
	private final String TAG = "DownLoadAppActivity";
	private ImageView iv_logo;
	private TextView tv_app_name;
	private TextView tv_size;
	private TextView tv_desc;
	private TextView tv_tips1;
	private TextView tv_tips2;
	private TextView tv_tips3;
	private TextView tv_screen;
	private TextView tv_downLoad;
	private TextView tv_share;
	private TextView tv_total_score;
	private LinearLayout ll_how_do;
	private LinearLayout ll_game;
	private TextView tv_breif;
	private ImageView iv_upload;
	private Bitmap imgBitmap;
	private CustomADImageDialog uDialog;
	private CustomDialog dialog;
	private File imgeDir = null;
	private File imageFile = null;
	private DisplayMetrics dm;
	String game = "";

	private AppInfo appInfo;
	private boolean isFirst = true;
	private String custom1 = "";
	private String custom2 = "";

	private boolean isSharing = false;
	private CustomDialog mDialog;
	private CustomDialog sDialog;
	public final int RESULT = 9998;

	private HorizontalScrollView imgsScrollView;
	private HorizontalScrollView imgsScrollView2;// 用户上传图片\
	private LinearLayout llImageUpload;
	public LinearLayout linearLayout11;
	private AlertDialog aDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_info);

		game = null == getIntent().getStringExtra("game") ? "" : getIntent()
				.getStringExtra("game");
		initView();
		initData();
	}

	@Override
	protected void onResume() {
		if (appInfo.getClicktype() == 1) {
			tv_downLoad.setVisibility(View.GONE);
		} else {
			if (appInfo.isSign()) {
				tv_downLoad.setVisibility(View.GONE);
				tv_share.setVisibility(View.GONE);
			} else {
				if (moreThanDays(Constant.DOWN_TIME,
						System.currentTimeMillis(), 1)) {
					tv_downLoad.setVisibility(View.VISIBLE);
					editor.putInt(Constant.DOWNLOAD_TIMES, 0);
					editor.putLong(Constant.DOWN_TIME,
							System.currentTimeMillis());
					editor.commit();
				} else if (pref.getInt(Constant.DOWNLOAD_TIMES, 0) > 6) {
					tv_downLoad.setVisibility(View.GONE);
				}

				if (myApplication.getResourceId() == appInfo.getResource_id()) {
					tv_downLoad.setVisibility(View.GONE);
				}

				if (myApplication.getDownload() == 1) { // 下载中
					tv_downLoad.setVisibility(View.VISIBLE);
					tv_downLoad.setClickable(false);
					tv_downLoad.setText(getResources().getString(
							R.string.down_loading));
				} else if (myApplication.getDownload() == 2) { // 下载完成
					tv_downLoad.setClickable(false);
					tv_downLoad.setVisibility(View.GONE);
					tv_downLoad.setText(getResources().getString(
							R.string.down_load_install));
				} else { // 没下载
					tv_downLoad.setClickable(true);
					tv_downLoad.setVisibility(View.VISIBLE);
					tv_downLoad.setText(getResources().getString(
							R.string.down_load_install));
				}
			}
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		editor.putInt(Constant.SLIDED, 0);
		editor.commit();
		if (imgBitmap != null) {
			imgBitmap.recycle();
		}
		super.onDestroy();
	}

	private void initView() {
		iv_logo = (ImageView) findViewById(R.id.iv_logo);
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		tv_size = (TextView) findViewById(R.id.tv_size);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_downLoad = (TextView) findViewById(R.id.tv_downLoad);
		tv_tips1 = (TextView) findViewById(R.id.tv_tips1);
		tv_tips2 = (TextView) findViewById(R.id.tv_tips2);
		tv_tips3 = (TextView) findViewById(R.id.tv_tips3);
		tv_total_score = (TextView) findViewById(R.id.tv_total_score);
		tv_share = (TextView) findViewById(R.id.tv_share);
		ll_how_do = (LinearLayout) findViewById(R.id.ll_how_do);
		tv_screen = (TextView) findViewById(R.id.tv_screen);
		ll_game = (LinearLayout) findViewById(R.id.ll_game);
		tv_breif = (TextView) findViewById(R.id.tv_breif);
		iv_upload = (ImageView) findViewById(R.id.iv_upload);
		llImageUpload = (LinearLayout) findViewById(R.id.ll_image_upload);

		tv_downLoad.setVisibility(View.GONE);

		imgsScrollView = new HorizontalScrollView(this);
		LinearLayout.LayoutParams lpHv = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 600);
		lpHv.setMargins(20, 20, 20, 20);
		imgsScrollView.setLayoutParams(lpHv);
		imgsScrollView.setHorizontalScrollBarEnabled(false);

		imgsScrollView2 = new HorizontalScrollView(this);
		imgsScrollView2.setLayoutParams(lpHv);
		imgsScrollView2.setHorizontalScrollBarEnabled(false);
		imgsScrollView2.setVisibility(View.GONE);

		linearLayout11 = new LinearLayout(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		linearLayout11.setLayoutParams(lp);
		linearLayout11.setVisibility(View.GONE);

		llImageUpload.addView(imgsScrollView);
		llImageUpload.addView(linearLayout11);
		llImageUpload.addView(imgsScrollView2);
	}

	private void initData() {
		appInfo = (AppInfo) getIntent().getSerializableExtra(Constant.ITEM);

		if (game.equals("game")) {
			ll_how_do.setVisibility(View.GONE);
			tv_share.setVisibility(View.GONE);
			ll_game.setVisibility(View.VISIBLE);
			tv_breif.setVisibility(View.VISIBLE);
			tv_breif.setText(appInfo.getBrief());
		} else {
			ll_how_do.setVisibility(View.VISIBLE);
			tv_share.setVisibility(View.VISIBLE);
			ll_game.setVisibility(View.GONE);
			tv_breif.setVisibility(View.GONE);
		}

		PhoneInformation.initTelephonyManager(this);
		String html = "<a href='" + Constant.SCREEN_SHOT_RUL
				+ PhoneInformation.getMachineType() + "手机怎么截图"
				+ "'>不知道怎么截图？</a>";
		tv_screen.setText(Html.fromHtml(html));
		tv_screen.setTextSize(15);
		tv_screen.setMovementMethod(LinkMovementMethod.getInstance());

		float integral = (float) ((long) appInfo.getScore() / 10000.0);// 可用积分
		DecimalFormat df = new DecimalFormat("0.00");// 格式化小数
		df.setRoundingMode(RoundingMode.DOWN);
		String money = df.format(integral / 10.0).replaceAll("0+?$", "")
				.replaceAll("[.]$", "");

		String url = appInfo.getIcon();
		ImageLoader.getInstance().displayImage(url, iv_logo);
		tv_app_name.setText(appInfo.getTitle());
		tv_size.setText("大小：" + appInfo.getResource_size() + "M");
		tv_desc.setText(appInfo.getDescription());
		float integrals = (float) ((long) appInfo.getTotalScore() / 10000.0);// 可用积分
		String money1 = df.format(integrals / 10.0).replaceAll("0+?$", "")
				.replaceAll("[.]$", "");
		String money2 = df
				.format((float) ((long) appInfo.getPhoto_integral() / 10000.0) / 10.0)
				.replaceAll("0+?$", "").replaceAll("[.]$", "");

		tv_total_score.setText("+" + money1 + "元");

		if (appInfo.getClicktype() == 1) {
			tv_tips1.setText(appInfo.getFile());
			tv_tips1.setAutoLinkMask(Linkify.ALL);
			tv_tips1.setMovementMethod(LinkMovementMethod.getInstance());
			tv_downLoad.setVisibility(View.GONE);
			tv_tips2.setText("打开上面连接，按提示完成操作。");
			tv_tips3.setText("按下面示例图截图上传即可获得 " + money2
					+ " 元，（注意只有一次上传机会，请严格按照要求上传）");
			
			if (appInfo.getPhoto_status() == 2
					|| appInfo.getPhoto_status() == 3
					|| appInfo.getPhoto_status() == 4) {
				iv_upload.setVisibility(View.GONE);
			}  else if (appInfo.getPhoto_status() == 1) {
				if (appInfo.getCurr_upload_photo() == appInfo
						.getUpload_photo()) {
					iv_upload.setVisibility(View.GONE);
				} else {
					iv_upload.setVisibility(View.VISIBLE);
				}
			} else {
				iv_upload.setVisibility(View.VISIBLE);
			}
			tv_screen.setVisibility(View.VISIBLE);

			mutiImageLoad1();
		} else if (appInfo.getClicktype() == 8) {
			tv_downLoad.setVisibility(View.VISIBLE);
			String str = "";
			if (appInfo.getB_type() == 2 && Float.parseFloat(money) > 0) {
				str = "下载安装后，注册成功即赚 " + money + "元。";
			} else {
				str = "下载安装后，使用3分钟系统将会赚  " + money + "元。";
			}
			if (appInfo.getIs_photo() == 1 || appInfo.isSign()) {
				tv_tips1.setText(str + "请到未完成任务列表中,按下面示例图截图上传即可获得 " + money2
						+ " 元，（注意只有一次上传机会，请严格按照要求上传）");
				if (appInfo.isSign()) {
					if (appInfo.getPhoto_status() == 2
							|| appInfo.getPhoto_status() == 3
							|| appInfo.getPhoto_status() == 4) {
						iv_upload.setVisibility(View.GONE);
					} else if (appInfo.getPhoto_status() == 1) {
						if (appInfo.getCurr_upload_photo() == appInfo
								.getUpload_photo()) {
							iv_upload.setVisibility(View.GONE);
						} else {
							iv_upload.setVisibility(View.VISIBLE);
						}
					} else {
						iv_upload.setVisibility(View.VISIBLE);
					}
				} else {
					iv_upload.setVisibility(View.GONE);
				}
				tv_screen.setVisibility(View.VISIBLE);
				
				mutiImageLoad8();
			} else {
				tv_tips1.setText(str);
				iv_upload.setVisibility(View.GONE);
				tv_screen.setVisibility(View.GONE);
				imgsScrollView.setVisibility(View.GONE);
			}
			tv_tips2.setText("安装完成后，请到未完成任务列表中，继续签到，每次签到即可获得0.1元。");
			tv_tips3.setText("每隔" + appInfo.getSign_rules() + ""
					+ "天可以签到 一次，签到" + appInfo.getNeedSign_times() + "次，任务完成");
		}

		iv_upload.setOnClickListener(this);
		downLoad();
	}
	
	private void mutiImageLoad8(){
		if (appInfo.getImgsList() != null
				&& appInfo.getImgsList().size() > 0) {
			List<String> imgsList = appInfo.getImgsList();
			int s = imgsList.size();
			LinearLayout linearLayout = new LinearLayout(
					DownLoadAppActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 600);
			linearLayout.setLayoutParams(lp);

			TextView textView = new TextView(DownLoadAppActivity.this);
			textView.setText("示例图：");
			textView.setTextSize(20);
			textView.setPadding(40, 20, 0, 20);
			linearLayout.addView(textView);

			for (int i = 0; i < s; i++) {
				final String url = imgsList.get(i);
				ImageView imageView = new ImageView(
						DownLoadAppActivity.this);
				imageView.setId(i);
				lp.setMargins(20, 20, 20, 20);
				imageView.setLayoutParams(lp);
				loadImage(url, imageView);
				linearLayout.addView(imageView);
				// 查看大图
				imageView
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								CustomADImageDialog
								.createDialog(DownLoadAppActivity.this, 2)
								.setBigImage(imgBitmap).show();
							}
						});
			}
			imgsScrollView.addView(linearLayout);
		} else {
			LinearLayout linearLayout = new LinearLayout(
					DownLoadAppActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 600);
			linearLayout.setLayoutParams(lp);

			TextView textView = new TextView(DownLoadAppActivity.this);
			textView.setText("示例图：");
			textView.setTextSize(20);
			textView.setPadding(40, 20, 0, 20);
			linearLayout.addView(textView);

			ImageView imageView = new ImageView(DownLoadAppActivity.this);
			imageView.setLayoutParams(lp);
			loadImage(appInfo.getH5_big_url(), imageView);
			linearLayout.addView(imageView);
			// 查看大图
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CustomADImageDialog
					.createDialog(DownLoadAppActivity.this, 2)
					.setBigImage(imgBitmap).show();
				}
			});
			imgsScrollView.addView(linearLayout);
		}

		if (appInfo.getUpImgList() != null
				&& appInfo.getUpImgList().size() > 0) {
			imgsScrollView2.setVisibility(View.VISIBLE);
			List<String> imgsList = appInfo.getUpImgList();
			int s = imgsList.size();
			LinearLayout linearLayout = new LinearLayout(
					DownLoadAppActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 600);
			linearLayout.setLayoutParams(lp);

			linearLayout11.setVisibility(View.VISIBLE);
			TextView textView = new TextView(DownLoadAppActivity.this);
			textView.setText("已经上传图片：");
			textView.setTextColor(Color
					.parseColor("#ff58616d"));
			textView.setTextSize(20);
			textView.setPadding(40, 20, 0, 20);

			linearLayout11.addView(textView);

			int appeal = appInfo.getAppeal();
			int status = appInfo.getPhoto_status();
			String strAppeal = "";
			String strStatus = "";
			switch (appeal) {
			case 1:
				strAppeal = "申诉";
				break;
			case 2:
				strAppeal = "";
				break;
			case 3:
				strAppeal = "申诉成功，等待审核";
				break;
			default:
				break;
			}

			switch (status) {
			case 0:
				strStatus = "未上传";
				break;
			case 1:
				strStatus = "审核中...";
				break;
			case 2:
				strStatus = "任务完成";
				break;
			case 3:
				strStatus = "任务失败" + appInfo.getCheck_remarks();
				break;
			default:
				break;
			}

			int u = appInfo.getUpload_photo()
					- appInfo.getCurr_upload_photo();
			if (u > 0) {
				TextView textView2 = new TextView(DownLoadAppActivity.this);
				textView2.setText("再上传 " + u + " 张图片完成任务");
				textView2
						.setTextColor(Color
								.parseColor("#ffff5252"));
				textView2.setTextSize(15);
				textView2.setPadding(0, 20, 0, 20);
				linearLayout11.addView(textView2);
			} else {
				TextView textView2 = new TextView(DownLoadAppActivity.this);
				textView2.setText(strStatus);
				textView2.setMaxLines(3);
				textView2.setTextSize(15);
				textView2
						.setTextColor(Color
								.parseColor("#ffff5252"));
				textView2.setPadding(0, 20, 0, 20);
				linearLayout11.addView(textView2);
			}

			TextView textView3 = new TextView(DownLoadAppActivity.this);
			if (appeal == 1) {
				textView3.setText(strAppeal);
				textView3
						.setBackgroundColor(Color
								.parseColor("#ffef4136"));
				textView3
						.setTextColor(Color
								.parseColor("#ffffff"));
				textView3.setTextSize(15);
				textView3.setPadding(40, 20, 40, 40);
				textView3
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								appeal();
							}
						});
			} else if (appeal == 3) {
				textView3.setText(strAppeal);
				textView3
						.setTextColor(Color
								.parseColor("#ffff5252"));
				textView3.setTextSize(15);
				textView3.setPadding(20, 20, 0, 20);
			}

			linearLayout11.addView(textView3);

			for (int i = 0; i < s; i++) {
				final String url = imgsList.get(i);
				ImageView imageView = new ImageView(
						DownLoadAppActivity.this);
				imageView.setId(i);
				lp.setMargins(20, 20, 20, 20);
				imageView.setLayoutParams(lp);
				loadImage(url, imageView);
				linearLayout.addView(imageView);
				// 查看大图
				imageView
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								CustomADImageDialog
								.createDialog(DownLoadAppActivity.this, 2)
								.setBigImage(imgBitmap).show();
							}
						});
			}
			imgsScrollView2.addView(linearLayout);
		} else if (appInfo.getPhoto() != null
				&& !appInfo.getPhoto().isEmpty()) {
			imgsScrollView2.setVisibility(View.VISIBLE);
			LinearLayout linearLayout = new LinearLayout(
					DownLoadAppActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 600);
			linearLayout.setLayoutParams(lp);

			linearLayout11.setVisibility(View.VISIBLE);
			TextView textView = new TextView(DownLoadAppActivity.this);
			textView.setText("已经上传图片：");
			textView.setTextColor(Color
					.parseColor("#ff58616d"));
			textView.setTextSize(20);
			textView.setPadding(40, 20, 0, 20);

			linearLayout11.addView(textView);

			int appeal = appInfo.getAppeal();
			int status = appInfo.getPhoto_status();
			String strAppeal = "";
			String strStatus = "";
			switch (appeal) {
			case 1:
				strAppeal = "申诉";
				break;
			case 2:
				strAppeal = "";
				break;
			case 3:
				strAppeal = "申诉成功，等待审核";
				break;
			default:
				break;
			}

			switch (status) {
			case 0:
				strStatus = "未上传";
				break;
			case 1:
				strStatus = "审核中...";
				break;
			case 2:
				strStatus = "任务完成";
				break;
			case 3:
				strStatus = "任务失败" + appInfo.getCheck_remarks();
				break;
			default:
				break;
			}

			int u = appInfo.getUpload_photo()
					- appInfo.getCurr_upload_photo();
			if (u > 0) {
				TextView textView2 = new TextView(DownLoadAppActivity.this);
				textView2.setText("再上传 " + u + " 张图片完成任务");
				textView2
						.setTextColor(Color
								.parseColor("#ffff5252"));
				textView2.setTextSize(15);
				textView2.setPadding(0, 20, 0, 20);
				linearLayout11.addView(textView2);
			} else {
				TextView textView2 = new TextView(DownLoadAppActivity.this);
				textView2.setText(strStatus);
				textView2.setMaxLines(3);
				textView2.setTextSize(15);
				textView2
						.setTextColor(Color
								.parseColor("#ffff5252"));
				textView2.setPadding(0, 20, 0, 20);
				linearLayout11.addView(textView2);
			}

			TextView textView3 = new TextView(DownLoadAppActivity.this);
			if (appeal == 1) {
				textView3.setText(strAppeal);
				textView3
						.setBackgroundColor(Color
								.parseColor("#ffef4136"));
				textView3
						.setTextColor(Color
								.parseColor("#ffffff"));
				textView3.setTextSize(15);
				textView3.setPadding(40, 20, 40, 40);
				textView3
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								appeal();
							}
						});
			} else if (appeal == 3) {
				textView3.setText(strAppeal);
				textView3
						.setTextColor(Color
								.parseColor("#ffff5252"));
				textView3.setTextSize(15);
				textView3.setPadding(20, 20, 0, 20);
			}

			linearLayout11.addView(textView3);

			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(lp);
			loadImage(appInfo.getPhoto(), imageView);
			linearLayout.addView(imageView);
			// 查看大图
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CustomADImageDialog
					.createDialog(DownLoadAppActivity.this, 2)
					.setBigImage(imgBitmap).show();
				}
			});
			imgsScrollView2.addView(linearLayout);
		}
	}

	private void mutiImageLoad1() {
		imgsScrollView.setVisibility(View.VISIBLE);

		if (appInfo.getImgsList() != null && appInfo.getImgsList().size() > 0) {
			List<String> imgsList = appInfo.getImgsList();
			int s = imgsList.size();
			LinearLayout linearLayout = new LinearLayout(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 600);
			linearLayout.setLayoutParams(lp);

			TextView textView = new TextView(this);
			textView.setText("示例图：");
			textView.setTextSize(20);
			textView.setPadding(40, 20, 0, 20);
			linearLayout.addView(textView);

			for (int i = 0; i < s; i++) {
				final String url = imgsList.get(i);
				ImageView imageView = new ImageView(this);
				imageView.setId(i);
				lp.setMargins(20, 20, 20, 20);
				imageView.setLayoutParams(lp);
				loadImage(url,imageView);
				linearLayout.addView(imageView);
				// 查看大图
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CustomADImageDialog
								.createDialog(DownLoadAppActivity.this, 2)
								.setBigImage(imgBitmap).show();
					}
				});
			}
			imgsScrollView.addView(linearLayout);
		} else {
			LinearLayout linearLayout = new LinearLayout(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 600);
			linearLayout.setLayoutParams(lp);

			TextView textView = new TextView(this);
			textView.setText("示例图：");
			textView.setTextSize(20);
			textView.setPadding(40, 20, 0, 20);
			linearLayout.addView(textView);

			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(lp);
			loadImage(appInfo.getH5_big_url(),imageView);
			linearLayout.addView(imageView);
			// 查看大图
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CustomADImageDialog
							.createDialog(DownLoadAppActivity.this, 2)
							.setBigImage(imgBitmap).show();
				}
			});
			imgsScrollView.addView(linearLayout);
		}

		if (appInfo.getUpImgList() != null && appInfo.getUpImgList().size() > 0) {
			imgsScrollView2.setVisibility(View.VISIBLE);
			List<String> imgsList = appInfo.getUpImgList();
			int s = imgsList.size();
			LinearLayout linearLayout = new LinearLayout(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 600);
			linearLayout.setLayoutParams(lp);

			linearLayout11.setVisibility(View.VISIBLE);
			TextView textView = new TextView(this);
			textView.setText("已经上传图片：");
			textView.setTextColor(Color.parseColor("#ff58616d"));
			textView.setTextSize(20);
			textView.setPadding(40, 20, 0, 20);

			linearLayout11.addView(textView);

			int appeal = appInfo.getAppeal();
			int status = appInfo.getPhoto_status();
			String strAppeal = "";
			String strStatus = "";
			switch (appeal) {
			case 1:
				strAppeal = "申诉";
				break;
			case 2:
				strAppeal = "";
				break;
			case 3:
				strAppeal = "申诉成功，等待审核";
				break;
			default:
				break;
			}

			switch (status) {
			case 0:
				strStatus = "未上传";
				break;
			case 1:
				strStatus = "审核中...";
				break;
			case 2:
				strStatus = "任务完成";
				break;
			case 3:
				strStatus = "任务失败-" + appInfo.getCheck_remarks();
				break;
			default:
				break;
			}

			int u = appInfo.getUpload_photo() - appInfo.getCurr_upload_photo();
			if (u > 0) {
				TextView textView2 = new TextView(this);
				textView2.setText("再上传 " + u + " 张图片完成任务");
				textView2.setTextColor(Color.parseColor("#ffff5252"));
				textView2.setTextSize(15);
				textView2.setPadding(0, 20, 0, 20);
				linearLayout11.addView(textView2);
			} else {
				TextView textView2 = new TextView(this);
				textView2.setText(strStatus);
				textView2.setMaxLines(3);
				textView2.setTextSize(15);
				textView2.setTextColor(Color.parseColor("#ffff5252"));
				textView2.setPadding(0, 20, 0, 20);
				linearLayout11.addView(textView2);
			}

			TextView textView3 = new TextView(this);
			if (appeal == 1) {
				textView3.setText(strAppeal);
				textView3.setBackgroundColor(Color.parseColor("#ffef4136"));
				textView3.setTextColor(Color.parseColor("#ffffffff"));
				textView3.setTextSize(15);
				textView3.setPadding(40, 20, 40, 40);
				textView3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						appeal();
					}
				});
			} else if (appeal == 3) {
				textView3.setText(strAppeal);
				textView3.setTextColor(Color.parseColor("#ffff5252"));
				textView3.setTextSize(15);
				textView3.setPadding(20, 20, 0, 20);
			}

			linearLayout11.addView(textView3);

			for (int i = 0; i < s; i++) {
				final String url = imgsList.get(i);
				ImageView imageView = new ImageView(this);
				imageView.setId(i);
				lp.setMargins(20, 20, 20, 20);
				imageView.setLayoutParams(lp);
				loadImage(url,imageView);
				linearLayout.addView(imageView);
				// 查看大图
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CustomADImageDialog
								.createDialog(DownLoadAppActivity.this, 2)
								.setBigImage(imgBitmap).show();
					}
				});
			}
			imgsScrollView2.addView(linearLayout);
		} else if (appInfo.getPhoto() != null && !appInfo.getPhoto().isEmpty()) {
			imgsScrollView2.setVisibility(View.VISIBLE);
			LinearLayout linearLayout = new LinearLayout(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 600);
			linearLayout.setLayoutParams(lp);

			linearLayout11.setVisibility(View.VISIBLE);
			TextView textView = new TextView(this);
			textView.setText("已经上传图片：");
			textView.setTextColor(Color.parseColor("#ff58616d"));
			textView.setTextSize(20);
			textView.setPadding(40, 20, 0, 20);

			linearLayout11.addView(textView);

			int appeal = appInfo.getAppeal();
			int status = appInfo.getPhoto_status();
			String strAppeal = "";
			String strStatus = "";
			switch (appeal) {
			case 1:
				strAppeal = "申诉";
				break;
			case 2:
				strAppeal = "";
				break;
			case 3:
				strAppeal = "申诉成功，等待审核";
				break;
			default:
				break;
			}

			switch (status) {
			case 0:
				strStatus = "未上传";
				break;
			case 1:
				strStatus = "审核中...";
				break;
			case 2:
				strStatus = "任务完成";
				break;
			case 3:
				strStatus = "任务失败" + appInfo.getCheck_remarks();
				break;
			default:
				break;
			}

			int u = appInfo.getUpload_photo() - appInfo.getCurr_upload_photo();
			if (u > 0) {
				TextView textView2 = new TextView(this);
				textView2.setText("再上传 " + u + " 张图片完成任务");
				textView2.setTextColor(Color.parseColor("#ffff5252"));
				textView2.setTextSize(15);
				textView2.setPadding(0, 20, 0, 20);
				linearLayout11.addView(textView2);
			} else {
				TextView textView2 = new TextView(this);
				textView2.setText(strStatus);
				textView2.setMaxLines(3);
				textView2.setTextSize(15);
				textView2.setTextColor(Color.parseColor("#ffff5252"));
				textView2.setPadding(0, 20, 0, 20);
				linearLayout11.addView(textView2);
			}

			TextView textView3 = new TextView(this);
			if (appeal == 1) {
				textView3.setText(strAppeal);
				textView3.setBackgroundColor(Color.parseColor("#ffef4136"));
				textView3.setTextColor(Color.parseColor("#ffffff"));
				textView3.setTextSize(15);
				textView3.setPadding(40, 20, 40, 40);
				textView3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						appeal();
					}
				});
			} else if (appeal == 3) {
				textView3.setText(strAppeal);
				textView3.setTextColor(Color.parseColor("#ffff5252"));
				textView3.setTextSize(15);
				textView3.setPadding(20, 20, 0, 20);
			}

			linearLayout11.addView(textView3);

			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(lp);
			loadImage(appInfo.getH5_big_url(),imageView);
			linearLayout.addView(imageView);
			// 查看大图
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CustomADImageDialog
							.createDialog(DownLoadAppActivity.this, 2)
							.setBigImage(imgBitmap).show();
				}
			});
			imgsScrollView2.addView(linearLayout);
		}
	}

	/**
	 * @Title: appeal
	 * @Description: 申诉
	 * @author xie.xin
	 * @param
	 * @return void
	 * @throws
	 */
	private void appeal() {
		final EditText editText = new EditText(DownLoadAppActivity.this);
		editText.setMinLines(3);
		editText.setMaxLines(5);
		editText.setGravity(Gravity.TOP | Gravity.LEFT);
		aDialog = new AlertDialog.Builder(DownLoadAppActivity.this)
				.setTitle("申诉")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“确认”后的上传图片
						String str = editText.getText().toString();
						if (!str.isEmpty()) {
							postAppeal(str);
						} else {
							Toast.makeText(DownLoadAppActivity.this,
									"申诉理由不能为空", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.setNegativeButton("返回", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“返回”
						aDialog.dismiss();
					}
				}).setView(editText).show();
	}

	private void postAppeal(String str) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("ad_install_id", appInfo.getInstall_id() + "");
		params.put("appeal_reason", str);
		HttpUtil.get(Constant.DOWNLOAD_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);

						String code = "";
						try {
							code = response.getString("code");
							if (code.equals("1")) {
								Toast.makeText(DownLoadAppActivity.this,
										"申诉成功", Toast.LENGTH_SHORT).show();
							} else {
								if (progressDialog != null) {
									progressDialog.dismiss();
								}
								Toast.makeText(DownLoadAppActivity.this,
										"申诉失败", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (progressDialog != null) {
								progressDialog.dismiss();
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
						Toast.makeText(DownLoadAppActivity.this, "申诉失败",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	/**
	 * @Title: downLoad
	 * @Description: TODO
	 * @author xie.xin
	 * @param
	 * @return void
	 * @throws
	 */
	private void downLoad() {
		tv_downLoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (myApplication.getDownload() == 0) {
					Intent intent = new Intent(DownLoadAppActivity.this,
							DownloadService.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(Constant.ITEM, appInfo);
					intent.putExtras(bundle);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					DownLoadAppActivity.this.startService(intent);
					myApplication.setDownload(1);
					tv_downLoad.setText(getResources().getString(
							R.string.down_loading));
					if (!game.equals("game")) {
						Toast.makeText(DownLoadAppActivity.this,
								"如果该任务，您还没有分享给赚友，记得立即分享哦！", Toast.LENGTH_LONG)
								.show();
					}

				}

			}
		});

		mDialog = new CustomDialog(DownLoadAppActivity.this,
				R.style.CustomDialog, new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						mDialog.cancel();
						Intent intent = new Intent();
						intent.setClass(DownLoadAppActivity.this,
								InvitationActivity.class);
						startActivity(intent);
					}
				}, 1);
		mDialog.setTitle(getResources().getString(R.string.dg_remind_title));
		mDialog.setContent(getResources().getString(
				R.string.dg_invitation_tips, appInfo.getScore() * 20 / 100));
		mDialog.setBtnStr(getResources().getString(R.string.dg_invitationNow));

		sDialog = new CustomDialog(DownLoadAppActivity.this,
				R.style.CustomDialog, new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						sDialog.cancel();
					}
				}, 1);
		sDialog.setTitle(getResources().getString(R.string.dg_share_success));

		final RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("task_id", appInfo.getAdId());

		if (!isSharing) {
			tv_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					isSharing = true;
					Toast.makeText(DownLoadAppActivity.this, "正在分享给好友,请稍后...",
							Toast.LENGTH_SHORT).show();
					HttpUtil.post(Constant.REPORT_TASK, params,
							new JsonHttpResponseHandler() {
								@Override
								public void onSuccess(int statusCode,
										Header[] headers, JSONObject response) {
									try {
										if (response.getInt("code") == 1) {
											JSONArray jArray = response
													.getJSONArray("data");
											sDialog.setContent(getResources()
													.getString(
															R.string.dg_share_success_tips,
															jArray.length(),
															appInfo.getScore()
																	* 20
																	/ 100
																	* jArray.length()));
											sDialog.setBtnStr(getResources()
													.getString(
															R.string.dg_iknow));
											sDialog.show();
										} else if (response.getInt("code") == -1) {
											mDialog.show();
										} else {
											Toast.makeText(
													DownLoadAppActivity.this,
													response.getString("info"),
													Toast.LENGTH_SHORT).show();
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} finally {
										isSharing = false;
									}
									super.onSuccess(statusCode, headers,
											response);
								}

								@Override
								public void onFailure(int statusCode,
										Header[] headers,
										String responseString,
										Throwable throwable) {
									isSharing = false;
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_upload:
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, RESULT);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT && resultCode == RESULT_OK && data != null) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			final String picturePath = cursor.getString(columnIndex);
			cursor.close();

			final File file = compress(picturePath, appInfo.getResource_id());
			dialog = new CustomDialog(this, R.style.CustomDialog,
					new CustomDialog.CustomDialogListener() {

						@Override
						public void onClick(View view) {
							switch (view.getId()) {
							case R.id.btn_left:
								dialog.dismiss();
								break;
							case R.id.btn_right:
								if (custom1 == null || custom1.isEmpty()) {
									custom1 = dialog.etCustom1.getText()
											.toString();
								}
								if (custom2 == null || custom2.isEmpty()) {
									custom2 = dialog.etCustom2.getText()
											.toString();
								}

								if (!dialog.getCustom1().isEmpty()
										&& !dialog.getCustom2().isEmpty()) {
									if (custom1 != null && !custom1.isEmpty()
											&& custom2 != null
											&& !custom2.isEmpty()) {
										dialog.dismiss();
										uploadFile(file);
									} else {
										Toast.makeText(
												DownLoadAppActivity.this,
												"用户信息不能为空", Toast.LENGTH_SHORT)
												.show();
									}
								} else if (dialog.getCustom1().isEmpty()
										&& !dialog.getCustom2().isEmpty()) {
									if (custom2 != null && !custom2.isEmpty()) {
										dialog.dismiss();
										uploadFile(file);
									} else {
										Toast.makeText(
												DownLoadAppActivity.this,
												"用户信息不能为空", Toast.LENGTH_SHORT)
												.show();
									}
								} else if (!dialog.getCustom1().isEmpty()
										&& dialog.getCustom2().isEmpty()) {
									if (custom1 != null && !custom1.isEmpty()) {
										dialog.dismiss();
										uploadFile(file);
									} else {
										Toast.makeText(
												DownLoadAppActivity.this,
												"用户信息不能为空", Toast.LENGTH_SHORT)
												.show();
									}
								} else {
									dialog.dismiss();
									uploadFile(file);
								}
								break;
							default:
								break;
							}
						}
					}, 6);
			if (appInfo.getIsCustom() != 0 && isFirst) {
				dialog.setCustom1(appInfo.getCustomField1() == null ? ""
						: appInfo.getCustomField1());
				dialog.setCustom2(appInfo.getCustomField2() == null ? ""
						: appInfo.getCustomField2());
			} else {
				dialog.setCustom1("");
				dialog.setCustom2("");
			}
			dialog.setTitle("您确定选择该图片上传吗？");
			dialog.setBtnLeftStr("取消重选");
			dialog.setBtnRightStr("确定上传");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			if (selectedImage != null) {
				try {
					dialog.setImage(MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), selectedImage));
					dialog.show();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @Title: uploadFile
	 * @Description: TODO
	 * @author xie.xin
	 * @param
	 * @return void
	 * @throws
	 */
	private void uploadFile(File file) {
		uDialog = CustomADImageDialog.createDialog(this, 3);
		RequestParams params = new RequestParams();
		try {
			params.put("photo", file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("code", pref.getString(Constant.CODE, "0"));
		params.put("ad_id", appInfo.getAdId());
		params.put("resource_id", appInfo.getResource_id());
		params.put("custom1", custom1 == null ? "" : custom1);
		params.put("custom2", custom2 == null ? "" : custom2);

		upLoading(Constant.UPLOADS_PHOTO, params);

	}

	
	private void upLoading(String url, RequestParams params) {
		// TODO Auto-generated method stub
		if (!uDialog.isShowing()) {
			uDialog.show();
		}
		// 上传文件
		HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// 上传成功后要做的工作
				Toast.makeText(DownLoadAppActivity.this, "图片上传成功",
						Toast.LENGTH_LONG).show();
				iv_upload.setVisibility(View.GONE);
				if (appInfo.isSign()) {
					myApplication.setSign(true);
				}
				isFirst = false;
				uDialog.dismiss();
				custom2 = "";
				custom1 = "";
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				// 上传失败后要做到工作
				Toast.makeText(DownLoadAppActivity.this, "图片上传失败",
						Toast.LENGTH_LONG).show();
				uDialog.dismiss();
				custom2 = "";
				custom1 = "";
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				// TODO Auto-generated method stub
				super.onProgress(bytesWritten, totalSize);
				int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
				// 上传进度显示
				uDialog.setProgress(count);
				uDialog.setPer(count);
				// progress.setProgress(count);
				Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);
			}

			@Override
			public void onRetry(int retryNo) {
				// TODO Auto-generated method stub
				super.onRetry(retryNo);
				// 返回重试次数
			}

		});
	}
	
	private void loadImage(String url,final ImageView imageView) {
		ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				Log.i(TAG, "start");
			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				Log.i(TAG, "failed");
			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
				imageView.setImageBitmap(bitmap);
				imgBitmap = bitmap;
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				Log.i(TAG, "cancel");
			}
		});
	}

	/**
	 * @Title: compress
	 * @Description: 图片压缩 50K
	 * @param @param srcPath
	 * @return void
	 * @throws
	 */
	public File compress(String srcPath, int rId) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			imgeDir = new File(Environment.getExternalStorageDirectory(),
					Constant.IMG_DIR);
			imageFile = new File(imgeDir.getPath(), System.currentTimeMillis()
					+ "_" + rId + ".jpg");
		}

		if (!imgeDir.exists()) {
			imgeDir.mkdirs();
		}
		if (!imageFile.exists()) {
			try {
				imageFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float hh = dm.heightPixels;
		float ww = dm.widthPixels;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
		opts.inJustDecodeBounds = false;
		int w = opts.outWidth;
		int h = opts.outHeight;
		int size = 0;
		if (w <= ww && h <= hh) {
			size = 1;
		} else {
			double scale = w >= h ? w / ww : h / hh;
			double log = Math.log(scale) / Math.log(2);
			double logCeil = Math.ceil(log);
			size = (int) Math.pow(2, logCeil);
		}
		opts.inSampleSize = size;
		bitmap = BitmapFactory.decodeFile(srcPath, opts);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		System.out.println(baos.toByteArray().length);
		while (baos.toByteArray().length > 45 * 1024) {
			baos.reset();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			quality -= 20;
			System.out.println(baos.toByteArray().length);
		}
		try {
			baos.writeTo(new FileOutputStream(imageFile));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				baos.flush();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return imageFile;
	}
}