package com.chuannuo.qianbaosuoping;

import java.text.SimpleDateFormat;
import java.util.Currency;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.NetWorkManager;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-12-1 上午10:46:39
 * @Description: 左滑分享
 */
public class ShareTranslucentActivity extends Activity implements IUiListener{
	
	public SimpleDateFormat sdf;
	public String year;
	public CustomDialog dialog;
	private CustomDialog mDialog;
	public IWXAPI wxApi; 
	
	public static Tencent mTencent;
	public SharedPreferences pref;
	private Editor editor;
	public NetWorkManager netWorkManager;
	
	private int shareNum;//分享次数
	private long recentShareTime;//最近一次分享时间
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_share_translucent);
		
		if(pref == null){
			pref = this.getSharedPreferences(Constant.STUDENTS_EARN, MODE_PRIVATE);
		}
		if(editor == null){
			editor = pref.edit();
		}
		if(netWorkManager == null){
			netWorkManager = new NetWorkManager(this);
		}
		
		shareNum = pref.getInt(Constant.SHARE_NUM, 0);
		recentShareTime = pref.getLong(Constant.RECENT_SHARE_TIME, 0);
		
		//实例化  微信分享接口
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);  
		wxApi.registerApp(Constant.WX_APP_ID);
				
		sdf=new SimpleDateFormat("yyyy");  
		year=sdf.format(new java.util.Date()); 
		
		this.share();
		
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				ShareTranslucentActivity.this.finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		editor.putInt(Constant.SLIDED, 0);
		editor.commit();
		super.onDestroy();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (null != mTencent){
    		mTencent.onActivityResult(requestCode, resultCode, data);
    	}
    	
    } 
	
	/**
	 * @author alan.xie
	 * @date 2014-11-27 下午4:39:11
	 * @Description: 分享
	 * @param @param v
	 * @return void
	 */
	public void share(){
		dialog = new CustomDialog(this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				if(!netWorkManager.isWiFiActive() && !netWorkManager.gprsIsOpenMethod()){
					Toast.makeText(ShareTranslucentActivity.this, "网络连接错误", Toast.LENGTH_SHORT).show();
				}else{
					switch (view.getId()) {
					case R.id.ll_share_wx:
							ShareTranslucentActivity.this.wechatShare(1);
							dialog.dismiss();
							ShareTranslucentActivity.this.finish();
						break;
					case R.id.ll_share_wx_friends:
							ShareTranslucentActivity.this.wechatShare(0);
							dialog.dismiss();
							ShareTranslucentActivity.this.finish();
						break;
					case R.id.ll_share_qq:
							doShareTencent();
							dialog.dismiss();
							ShareTranslucentActivity.this.finish();
						break;
					case R.id.ll_qr_code:
						mDialog = new CustomDialog(ShareTranslucentActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
							
							@Override
							public void onClick(View view) {
								mDialog.dismiss();
								ShareTranslucentActivity.this.finish();
							}
						}, 0);
						mDialog.setTitle(getResources().getString(R.string.app_name));
						mDialog.setImage(Create2DCode(Constant.SHARE_URL+pref.getString(Constant.INVIT_CODE, "0")));
						mDialog.setBtnStr(getResources().getString(R.string.dg_confirm));
						mDialog.setCancelable(false);
						mDialog.setCanceledOnTouchOutside(false);
						mDialog.show();
					default:
						break;
					}
				}
			}
		}, 3);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-11-28 上午11:30:46
	 * @Description: 分享到qq
	 * @param 
	 * @return void
	 */
	public void doShareTencent()
    {
        Bundle bundle = new Bundle();
        
        if(mTencent == null){
			mTencent = Tencent.createInstance(Constant.QQ_APP_ID, this);
		}
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Constant.SHARE_URL+pref.getString(Constant.INVIT_CODE, "0"));
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, getResources().getString(R.string.app_name)+"-注册立即送5元");
        //分享的图片URL
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Constant.LOGO_URL);
        //分享的消息摘要，最长50个字
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, year+"年最给力的手机赚钱应用，注册立即送5元");
 
        mTencent.shareToQQ(this, bundle , ShareTranslucentActivity.this);
        
 
    }
	
	/**
	 * @author alan.xie
	 * @date 2014-11-27 下午5:45:18
	 * @Description: 微信分享
	 * @param @param flag
	 * @return void
	 */
	public void wechatShare(int flag){  
	    WXWebpageObject webpage = new WXWebpageObject();  
	    webpage.webpageUrl = Constant.SHARE_URL+pref.getString(Constant.INVIT_CODE, "0");  
	    WXMediaMessage msg = new WXMediaMessage(webpage);  
	    msg.title = getResources().getString(R.string.app_name)+"-注册立即送5元";  
	    msg.description = year+"年最赚钱的应用！注册立即送5元";  
	    //这里替换一张自己工程里的图片资源  
	    Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);  
	    msg.setThumbImage(thumb);
	    thumb.recycle();
	      
	    SendMessageToWX.Req req = new SendMessageToWX.Req();  
	    req.transaction = String.valueOf(System.currentTimeMillis());  
	    req.message = msg;  
	    req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;  
	    wxApi.sendReq(req); 
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-11-20 下午12:03:38
	 * @Description: 生成二维码图片
	 * @param @param str
	 * @param @return
	 * @param @throws WriterException
	 * @return Bitmap
	 */
	public Bitmap Create2DCode(String str){
		//生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败  
        BitMatrix matrix = null;
		try {
			matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 300, 300);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        //二维矩阵转为一维像素数组,也就是一直横着排了  
        int[] pixels = new int[width * height];  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if(matrix.get(x, y)){  
                    pixels[y * width + x] = 0xff000000;  
                }  
                  
            }  
        }  
          
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
        //通过像素数组生成bitmap,具体参考api  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-31 上午10:02:50
	 * @Description: 增加积分
	 * @param @param integral
	 * @return void
	 */
	public void addIntegral(final int integral,String reason){
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
						Toast.makeText(ShareTranslucentActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
					}else{
						/*
						 * 增加积分成功
						 */
						Toast.makeText(ShareTranslucentActivity.this, getResources().getString(R.string.add_score_success)+integral, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(ShareTranslucentActivity.this, getResources().getString(R.string.sys_remind3), Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	public void onCancel() {
    	Toast.makeText(this, "QQ分享取消", Toast.LENGTH_SHORT).show();
    	dialog.dismiss();
    	ShareTranslucentActivity.this.finish();
	}

	@Override
	public void onComplete(Object arg0) {
		if(shareNum < 3 && (System.currentTimeMillis()-recentShareTime) > 2*60*60*1000){
    		addIntegral(5000,"QQ分享");
    		editor.putLong(Constant.RECENT_SHARE_TIME, System.currentTimeMillis());
    		editor.putInt(Constant.SHARE_NUM, shareNum+1);
    		editor.commit();
    		recentShareTime = System.currentTimeMillis();
    	}
		
    	Toast.makeText(this, "QQ分享成功", Toast.LENGTH_SHORT).show();
    	dialog.dismiss();
    	ShareTranslucentActivity.this.finish();
	}

	@Override
	public void onError(UiError arg0) {
    	Toast.makeText(this, "QQ分享出错", Toast.LENGTH_SHORT).show();
    	dialog.dismiss();
    	ShareTranslucentActivity.this.finish();
	}
}
