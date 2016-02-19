package com.chuannuo.qianbaosuoping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import net.youmi.android.offers.OffersManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Configuration;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.dao.AppDao;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.service.DownloadService;
import com.chuannuo.qianbaosuoping.view.CustomADImageDialog;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @author alan.xie
 * @date 2015-3-3 ����10:58:06
 * @Description: Ǯ������ Ӧ������
 */
public class DownLoadAppActivity extends BaseActivity implements OnClickListener{
	private final String TAG = "DownLoadAppActivity";
	private ImageView iv_logo;
	private TextView tv_app_name;
	private TextView tv_size;
	private TextView tv_desc;
	private TextView tv_tips1;
	private TextView tv_tips2;
	private TextView tv_tips3;
	private TextView tv_downLoad;
	private TextView tv_share;
	private TextView tv_total_score;
	private LinearLayout ll_how_do;
	private LinearLayout ll_game;
	private TextView tv_breif;
	private ImageView iv_example;
	private RelativeLayout rl_example;
	private ImageView iv_upload;
	private Bitmap imgBitmap;
	private CustomADImageDialog uDialog;
	private CustomDialog dialog;

	String game = "";

	private AppInfo appInfo;

	private boolean isSharing = false;
	private CustomDialog mDialog;
	private CustomDialog sDialog;
	public final int RESULT = 9998;

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
		}else{
			if(appInfo.isSign()){
				tv_downLoad.setVisibility(View.GONE);
				tv_share.setVisibility(View.GONE);
			}else{
				if (moreThanDays(Constant.DOWN_TIME, System.currentTimeMillis(), 1)) {
					tv_downLoad.setVisibility(View.VISIBLE);
					editor.putInt(Constant.DOWNLOAD_TIMES, 0);
					editor.putLong(Constant.DOWN_TIME, System.currentTimeMillis());
					editor.commit();
				} else if (pref.getInt(Constant.DOWNLOAD_TIMES, 0) > 6) {
					tv_downLoad.setVisibility(View.GONE);
				}

				if (myApplication.getResourceId() == appInfo.getResource_id()) {
					tv_downLoad.setVisibility(View.GONE);
				}

				if (myApplication.getDownload() == 1) { // ������
					tv_downLoad.setVisibility(View.VISIBLE);
					tv_downLoad.setClickable(false);
					tv_downLoad
							.setText(getResources().getString(R.string.down_loading));
				} else if (myApplication.getDownload() == 2) { // �������
					tv_downLoad.setClickable(false);
					tv_downLoad.setVisibility(View.GONE);
					tv_downLoad.setText(getResources().getString(
							R.string.down_load_install));
				} else { // û����
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
		if(imgBitmap != null){
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
		ll_game = (LinearLayout) findViewById(R.id.ll_game);
		tv_breif = (TextView) findViewById(R.id.tv_breif);
		iv_example = (ImageView) findViewById(R.id.iv_example);
		iv_upload = (ImageView) findViewById(R.id.iv_upload);
		rl_example = (RelativeLayout) findViewById(R.id.rl_example);

		tv_downLoad.setVisibility(View.GONE);
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

		float integral = (float) ((long) appInfo.getScore() / 10000.0);// ���û���
		DecimalFormat df = new DecimalFormat("0.00");// ��ʽ��С��
		df.setRoundingMode(RoundingMode.DOWN);
		String money = df.format(integral / 10.0).replaceAll("0+?$", "")
				.replaceAll("[.]$", "");

		String url = appInfo.getIcon();
		ImageLoader.getInstance().displayImage(url, iv_logo);
		tv_app_name.setText(appInfo.getTitle());
		tv_size.setText("��С��" + appInfo.getResource_size() + "M");
		tv_desc.setText(appInfo.getDescription());
		float integrals = (float) ((long) appInfo.getTotalScore() / 10000.0);// ���û���
		String money1 = df.format(integrals / 10.0).replaceAll("0+?$", "")
				.replaceAll("[.]$", "");
		String money2 = df.format((float) ((long) appInfo.getPhoto_integral() / 10000.0) / 10.0).replaceAll("0+?$", "")
				.replaceAll("[.]$", "");

		tv_total_score.setText("+" + money1 + "Ԫ");

		if (appInfo.getClicktype() == 1) {
			tv_tips1.setText(appInfo.getFile());
			tv_tips1.setAutoLinkMask(Linkify.ALL);
			tv_tips1.setMovementMethod(LinkMovementMethod.getInstance());
			tv_downLoad.setVisibility(View.GONE);
			tv_tips2.setText("���������ӣ�����ʾ��ɲ�����");
			tv_tips3.setText("������ʾ��ͼ��ͼ�ϴ����ɻ�� "+ money2 + " Ԫ����ע��ֻ��һ���ϴ����ᣬ���ϸ���Ҫ���ϴ���");
			rl_example.setVisibility(View.VISIBLE);
			iv_upload.setVisibility(View.VISIBLE);
			loadImage();
		} else if (appInfo.getClicktype() == 8) {
			tv_downLoad.setVisibility(View.VISIBLE);
			String str="";
			if (appInfo.getB_type() == 2 && Float.parseFloat(money) > 0) {
				str = "���ذ�װ��ע��ɹ���׬ " + money + "Ԫ��";
			} else {
				str = "���ذ�װ��ʹ��3����ϵͳ����׬  " + money + "Ԫ��";
			}
			if (appInfo.getIs_photo() == 1 || appInfo.isSign()) {
				tv_tips1.setText(str+"�뵽δ��������б���,������ʾ��ͼ��ͼ�ϴ����ɻ�� "+ money2 + " Ԫ����ע��ֻ��һ���ϴ����ᣬ���ϸ���Ҫ���ϴ���");
				rl_example.setVisibility(View.VISIBLE);
				if(appInfo.isSign()){
					iv_upload.setVisibility(View.VISIBLE);
				}else{
					iv_upload.setVisibility(View.GONE);	
				}
				loadImage();
			} else {
				tv_tips1.setText(str);
				rl_example.setVisibility(View.GONE);
				iv_upload.setVisibility(View.GONE);
			}
			tv_tips2.setText("��װ��ɺ��뵽δ��������б��У�����ǩ����ÿ��ǩ�����ɻ��0.1Ԫ��");
			tv_tips3.setText("ÿ��" + appInfo.getSign_rules() + ""
					+ "�����ǩ�� һ�Σ�ǩ��" + appInfo.getNeedSign_times() + "�Σ��������");
		}

		rl_example.setOnClickListener(this);
		iv_upload.setOnClickListener(this);
		downLoad();
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
								"�������������û�з����׬�ѣ��ǵ���������Ŷ��", Toast.LENGTH_LONG)
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
					Toast.makeText(DownLoadAppActivity.this, "���ڷ��������,���Ժ�...",
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

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_example:
			CustomADImageDialog.createDialog(this,2).setBigImage(imgBitmap).show();
			break;
		case R.id.iv_upload:
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  
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
        if(requestCode == RESULT && resultCode == RESULT_OK && data != null){  
              
            Uri selectedImage = data.getData();  
            String[] filePathColumn = { MediaStore.Images.Media.DATA };  
   
            Cursor cursor = getContentResolver().query(selectedImage,  
                    filePathColumn, null, null, null);  
            cursor.moveToFirst();  
   
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);  
            final String picturePath = cursor.getString(columnIndex);  
            cursor.close();  
            
            dialog = new CustomDialog(this, R.style.CustomDialog,
					new CustomDialog.CustomDialogListener() {

						@Override
						public void onClick(View view) {
							switch (view.getId()) {
							case R.id.btn_left:
								dialog.dismiss();
								break;
							case R.id.btn_right:
								dialog.dismiss();
								uploadFile(new File(picturePath));
								break;
							default:
								break;
							}
						}
					}, 6);
			dialog.setTitle("��ȷ��ѡ���ͼƬ�ϴ���");
			dialog.setBtnLeftStr("ȡ����ѡ");
			dialog.setBtnRightStr("ȷ���ϴ�");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			if(selectedImage!=null){
				try {
					dialog.setImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage));
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
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	private void uploadFile(File file) {
		uDialog = CustomADImageDialog.createDialog(this, 3);
	    if(appInfo.getClicktype() == 1){
	    	RequestParams params = new RequestParams();  
		    try {
				params.put("photo", file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		    params.put("app_id", pref.getString(Constant.APPID, "0"));
			params.put("code", pref.getString(Constant.CODE, "0"));
			params.put("ad_id",appInfo.getAdId());
			params.put("resource_id", appInfo.getResource_id());
			
	    	upLoading(Constant.UPLOADS_PHOTO_H5,params);
	    }else{
	    	RequestParams params = new RequestParams();  
		    try {
				params.put("photo", file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		    params.put("ad_install_id", appInfo.getInstall_id());
			params.put("code", pref.getString(Constant.CODE, "0"));
			
	    	upLoading(Constant.UPLOADS_PHOTO,params);
	    }
	    
	}

	/** 
	* @Title: upLoading 
	* @Description: TODO
	* @author  xie.xin
	* @param 
	* @return void 
	* @throws 
	*/
	private void upLoading(String url,RequestParams params) {
		// TODO Auto-generated method stub
		if(!uDialog.isShowing()){
        	uDialog.show();
        }
		// �ϴ��ļ�  
	    HttpUtil.post(url, params, new AsyncHttpResponseHandler() {  
	        @Override  
	        public void onSuccess(int statusCode, Header[] headers,  
	                byte[] responseBody) {  
	            // �ϴ��ɹ���Ҫ���Ĺ���  
	            Toast.makeText(DownLoadAppActivity.this, "ͼƬ�ϴ��ɹ�", Toast.LENGTH_LONG).show();
	            iv_upload.setVisibility(View.GONE);
	            if(appInfo.isSign()){
	            	myApplication.setSign(true);
	            }
	            uDialog.dismiss();  
	        }
	        @Override  
	        public void onFailure(int statusCode, Header[] headers,  
	                byte[] responseBody, Throwable error) {  
	            // �ϴ�ʧ�ܺ�Ҫ��������  
	            Toast.makeText(DownLoadAppActivity.this, "ͼƬ�ϴ�ʧ��", Toast.LENGTH_LONG).show();
	            uDialog.dismiss(); 
	        }  
	  
	        @Override  
	        public void onProgress(int bytesWritten, int totalSize) {  
	            // TODO Auto-generated method stub  
	            super.onProgress(bytesWritten, totalSize);  
	            int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
	            // �ϴ�������ʾ  
	            uDialog.setProgress(count);
	            uDialog.setPer(count);
	            //progress.setProgress(count);  
	            Log.e("�ϴ� Progress>>>>>", bytesWritten + " / " + totalSize);  
	        }  
	  
	        @Override  
	        public void onRetry(int retryNo) {  
	            // TODO Auto-generated method stub  
	            super.onRetry(retryNo);  
	            // �������Դ���  
	        }  
	  
	    });  
	}

	private void loadImage(){
		ImageLoader.getInstance().loadImage(appInfo.getH5_big_url(),
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
						iv_example.setImageBitmap(bitmap);
						imgBitmap = bitmap;
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						Log.i(TAG, "cancel");
					}
				});
	}
}
