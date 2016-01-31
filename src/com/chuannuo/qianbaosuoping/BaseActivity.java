package com.chuannuo.qianbaosuoping;

import java.text.SimpleDateFormat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Configuration;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.MyApplication;
import com.chuannuo.qianbaosuoping.common.NetWorkManager;
import com.chuannuo.qianbaosuoping.dao.AppDao;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.view.CustomADImageDialog;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.chuannuo.qianbaosuoping.view.CustomProgressDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * @author alan.xie
 * @date 2014-10-16 ����6:12:01
 * @Description: TODO
 */
@SuppressLint("SimpleDateFormat")
public class BaseActivity extends Activity implements IWXAPIEventHandler,IUiListener{

	public CustomProgressDialog progressDialog;
	public CustomADImageDialog adDialog;
	public SharedPreferences pref;
	public Editor editor;
	public MyApplication myApplication;
	public NetWorkManager netWorkManager;
	public SimpleDateFormat sdf;
	public String year;
	public CustomDialog dialog;
	public IWXAPI wxApi; 
	private Intent intent;
	
	public static Tencent mTencent;
	private int shareNum;//�������
	private long recentShareTime;//���һ�η���ʱ��
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(pref == null){
			pref = this.getSharedPreferences(Constant.STUDENTS_EARN, MODE_PRIVATE);
		}
		if(myApplication == null){
			myApplication  = (MyApplication) getApplication();
		}
		if(netWorkManager == null){
			netWorkManager = new NetWorkManager(this);
		}
		if(null == intent){
			intent = new Intent();
		}
		editor = pref.edit();
		
		//ʵ����  ΢�ŷ���ӿ�
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);  
		wxApi.registerApp(Constant.WX_APP_ID);
		wxApi.handleIntent(getIntent(), this);
		
	    sdf=new SimpleDateFormat("yyyy");  
	    year=sdf.format(new java.util.Date());  
	    shareNum = pref.getInt(Constant.SHARE_NUM, 0);
	    recentShareTime = pref.getLong(Constant.RECENT_SHARE_TIME, 0);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (null != mTencent){
    		mTencent.onActivityResult(requestCode, resultCode, data);
    	}
    	
    } 
	
	@Override
	protected void onResume() {
		if(myApplication.getFlag()==1 || myApplication.getFlag()==2){
			this.finish();
		}
		
		super.onResume();
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-11-27 ����4:39:11
	 * @Description: ����
	 * @param @param v
	 * @return void
	 */
	public void share(View v){
		//����������һ�죬����������㡣
		if(moreThanDays(Constant.SHARE_TIME,System.currentTimeMillis(),1)){
			editor.putInt(Constant.SHARE_NUM, 0);
			editor.putLong(Constant.SHARE_TIME, System.currentTimeMillis());
			editor.commit();
		}
		intent.setClass(this, InvitationActivity.class);
		startActivity(intent);
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-11-28 ����11:30:46
	 * @Description: ����qq
	 * @param 
	 * @return void
	 */
	public void doShareTencent()
    {
        Bundle bundle = new Bundle();
        
        if(mTencent == null){
			mTencent = Tencent.createInstance(Constant.QQ_APP_ID, this);
		}
        //����������Ϣ�����ѵ�������תURL��
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Constant.SHARE_URL+pref.getString(Constant.INVIT_CODE, "0"));
        //����ı��⡣ע��PARAM_TITLE��PARAM_IMAGE_URL��PARAM_SUMMARY����ȫΪ�գ����ٱ�����һ������ֵ�ġ�
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, getResources().getString(R.string.app_name)+"-ע��������5Ԫ");
        //�����ͼƬURL
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Constant.LOGO_URL);
        //�������ϢժҪ���50����
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, year+"����������ֻ�׬ǮӦ�ã�ע��������5Ԫ");
 
        mTencent.shareToQQ(BaseActivity.this, bundle , BaseActivity.this);
        
    }
	
	/**
	 * @author alan.xie
	 * @date 2014-11-27 ����5:45:18
	 * @Description: ΢�ŷ���
	 * @param @param flag 0���ѷ���1����Ȧ����
	 * @return void
	 */
	public void wechatShare(int flag){  
	    WXWebpageObject webpage = new WXWebpageObject();  
	    webpage.webpageUrl = Constant.SHARE_URL+pref.getString(Constant.INVIT_CODE, "0");  
	    WXMediaMessage msg = new WXMediaMessage(webpage);
	    msg.title = getResources().getString(R.string.app_name)+"-ע��������5Ԫ\n"+year+"����������ֻ�׬ǮӦ�ã�";  
	    msg.description = year+"����������ֻ�׬ǮӦ�ã�ע��������5Ԫ";  
	    //�����滻һ���Լ��������ͼƬ��Դ  
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
	 * @date 2014-10-22 ����10:51:38
	 * @Description: ���ؽ�����
	 * @param @param str
	 * @return void
	 */
	public void openProgressDialog(String str){
		if (this.progressDialog == null){  
            this.progressDialog = CustomProgressDialog.createDialog(this);
        }
		this.progressDialog.setMessage(str); 
		this.progressDialog.show();
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-22 ����10:51:41
	 * @Description: TODO
	 * @param 
	 * @return void
	 */
	public void closeProgressDialog(){
		this.progressDialog.dismiss();
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-15 ����6:01:29
	 * @Description: ͼƬ���
	 * @param @param url
	 * @return void
	 */
	public void initImageAdDialog(final AppInfo appInfo){
		if(this.adDialog == null){
			this.adDialog = CustomADImageDialog.createDialog(this);
		}
		ImageLoader.getInstance().loadImage(appInfo.getH5_big_url(), new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
				BaseActivity.this.adDialog.setImage(bitmap);
				BaseActivity.this.adDialog.setAppInfo(appInfo);
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
			}
		});
	}
	
	public void showAdDialog(){
		BaseActivity.this.adDialog.show();
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-22 ����9:44:32
	 * @Description: ����
	 * @param @param v
	 * @return void
	 */
	public void back(View v){
		this.finish();
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-27 ����3:14:06
	 * @Description: �ж��Ƿ�������
	 * @param @return
	 * @return boolean
	 */
	public boolean netWork(){
		if(!netWorkManager.isWiFiActive() && !netWorkManager.gprsIsOpenMethod()){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-22 ����9:55:37
	 * @Description: �Ƿ��½
	 * @param @return
	 * @return boolean
	 */
	public boolean isLogin(){
		if(myApplication.getPhone() != null && myApplication.getPassword() != null){
			return true;
		}
		return false;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-23 ����2:57:16
	 * @Description: �Ƿ���ֻ�
	 * @param @return
	 * @return boolean
	 */
	public boolean isBindingPhone(){
		String phone = pref.getString(Constant.PHONE, "");
		if(!phone.equals("") && phone != null){
			return true;
		}
		return false;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-23 ����5:05:59
	 * @Description: �Ƿ��QQ
	 * @param @return
	 * @return boolean
	 */
	public boolean isBindingQQ(){
		String qq = pref.getString(Constant.QQ, "");
		if(!qq.equals("") && qq != null){
			return true;
		}
		return false;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-23 ����5:05:47
	 * @Description: �Ƿ�󶨲Ƹ�ͨ
	 * @param @return
	 * @return boolean
	 */
	public boolean isBindingCFT(){
		String cft = pref.getString(Constant.CFT, "");
		if(!cft.equals("") && cft != null){
			return true;
		}
		return false;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-23 ����5:05:36
	 * @Description: �Ƿ��֧����
	 * @param @return
	 * @return boolean
	 */
	public boolean isBindingZFB(){
		String zfb = pref.getString(Constant.ZFB, "");
		if(!zfb.equals("") && zfb != null){
			return true;
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
	public Boolean moreThanDays(String type ,long currTimes,int days){
		long times = pref.getLong(type, 0);
		if(currTimes-times > days*24*60*60*1000){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @author alan.xie
	 * @date 2015-1-19 ����10:03:53
	 * @Description: ����ʱ�������ڶ��ٷ���
	 * @param @param type
	 * @param @param currTimes
	 * @param @param minute
	 * @param @return
	 * @return Boolean
	 */
	public Boolean moreThanMinute(String type ,long currTimes,int minute){
		long times = pref.getLong(type, 0);
		if(currTimes-times > minute*60*1000){
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * @author alan.xie
	 * @date 2014-10-31 ����10:02:50
	 * @Description: ���ӻ���
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
						Toast.makeText(BaseActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
					}else{
						/*
						 * ���ӻ��ֳɹ�
						 */
						Toast.makeText(BaseActivity.this, getResources().getString(R.string.add_score_success)+"+"+integral, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(BaseActivity.this, getResources().getString(R.string.sys_remind3), Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-11-20 ����12:03:38
	 * @Description: ���ɶ�ά��ͼƬ
	 * @param @param str
	 * @param @return
	 * @param @throws WriterException
	 * @return Bitmap
	 */
	public Bitmap Create2DCode(String str){
		//���ɶ�ά����,����ʱָ����С,��Ҫ������ͼƬ�Ժ��ٽ�������,������ģ������ʶ��ʧ��  
        BitMatrix matrix = null;
		try {
			matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 300, 300);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        //��ά����תΪһά��������,Ҳ����һֱ��������  
        int[] pixels = new int[width * height];  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if(matrix.get(x, y)){  
                    pixels[y * width + x] = 0xff000000;  
                }  
                  
            }  
        }  
          
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
        //ͨ��������������bitmap,����ο�api  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
	}
	
	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onResp(BaseResp resp) {
		 switch (resp.errCode) {  
	        case BaseResp.ErrCode.ERR_OK:
	        	if(resp.transaction.contains("share")){
	        		myApplication.setWxShare(true);
	        		this.finish();
	        	}else if(resp.transaction.contains("convert")){
	        		String id = resp.transaction.split("-")[1];
	        		reportConvertShare(Integer.parseInt(id));
	        		this.finish();
	        	}else{
	        		shareNum = pref.getInt(Constant.SHARE_NUM, 0);
		        	recentShareTime = pref.getLong(Constant.RECENT_SHARE_TIME, 0);
		        	if(shareNum < 3 && (System.currentTimeMillis()-recentShareTime) > 2*60*60*1000){
		        		addIntegral(5000,"΢�ŷ���");
		        		editor.putLong(Constant.RECENT_SHARE_TIME, System.currentTimeMillis());
		        		editor.putInt(Constant.SHARE_NUM, shareNum+1);
		        		editor.commit();
		        	}
		        	Toast.makeText(BaseActivity.this, "΢�ŷ���ɹ�", Toast.LENGTH_SHORT).show();
		            this.finish();
	        	}
	        	
	        	//����ɹ�
	            break;  
	        case BaseResp.ErrCode.ERR_USER_CANCEL: 
	        	Toast.makeText(this, "΢�ŷ�����ȡ��", Toast.LENGTH_SHORT).show();
	        	this.finish();
	            //����ȡ��  
	            break;  
	        case BaseResp.ErrCode.ERR_AUTH_DENIED:  
	        	Toast.makeText(this, "΢�ŷ���ʧ��", Toast.LENGTH_SHORT).show();
	        	this.finish();
	            //����ܾ�  
	            break;  
	        }
		 
	}


	@Override
	public void onCancel() {
		Toast.makeText(this, "QQ����ȡ��", Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onComplete(Object arg0) {
		shareNum = pref.getInt(Constant.SHARE_NUM, 0);
    	recentShareTime = pref.getLong(Constant.RECENT_SHARE_TIME, 0);
		if(shareNum < 3 && (System.currentTimeMillis()-recentShareTime) > 2*60*60*1000){
    		addIntegral(5000,"QQ����");
    		editor.putLong(Constant.RECENT_SHARE_TIME, System.currentTimeMillis());
    		editor.putInt(Constant.SHARE_NUM, shareNum+1);
    		editor.commit();
    	}
		Toast.makeText(this, "QQ����ɹ�", Toast.LENGTH_SHORT).show();
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
	public void reportConvertShare(int convert_id){
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, "0"));
		params.put("convert_id",convert_id);
		params.put("code", pref.getString(Constant.CODE, ""));
		Toast.makeText(BaseActivity.this,pref.getString(Constant.APPID, "0")+convert_id+pref.getString(Constant.CODE, ""),Toast.LENGTH_SHORT).show();
		HttpUtil.post(Constant.REPORT_CONVERT_SHARE,params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getInt("code") == 1){
						Toast.makeText(BaseActivity.this, getResources().getString(R.string.add_score_success)+"+5000", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(BaseActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
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
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
}







