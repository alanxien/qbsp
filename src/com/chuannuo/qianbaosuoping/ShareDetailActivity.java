package com.chuannuo.qianbaosuoping;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.model.ShareApp;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
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

/**
 * @author alan.xie
 * @date 2015-1-27 ����3:01:27
 * @Description: ����app
 */
public class ShareDetailActivity extends BaseActivity implements OnClickListener,IUiListener{
	
	private ImageView iv_logo;		//ͼƬ
	private TextView tv_app_name;	//����
	private TextView tv_score;		//����
	private TextView tv_desc;		//����
	private TextView tv_detail;		//����
	private TextView tv_share_count;//�������
	private LinearLayout ll_qq_share;//qq����
	private LinearLayout ll_weixin_share;//΢�ŷ���
	private Intent intent;
	private ShareApp  appInfo;
	
	public IWXAPI wxApi; 
	
	public static Tencent mTencent;
	public SharedPreferences pref;
	private Editor editor;
	
	WXWebpageObject webpage;   
    WXMediaMessage msg;
    SendMessageToWX.Req req;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_app_info);
		
		
		if(pref == null){
			pref = this.getSharedPreferences(Constant.STUDENTS_EARN, MODE_PRIVATE);
		}
		if(editor == null){
			editor = pref.edit();
		}
		
		//ʵ����  ΢�ŷ���ӿ�
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);
		wxApi.registerApp(Constant.WX_APP_ID);
		initView();
		
	}
	
	@Override
	protected void onResume() {
		if(myApplication.isWxShare()){
			repostShare(2);
		}
		super.onResume();
	}
	private void initView(){
		iv_logo = (ImageView) findViewById(R.id.iv_logo);
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		tv_score = (TextView) findViewById(R.id.tv_score);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_share_count = (TextView) findViewById(R.id.tv_share_count);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		ll_qq_share = (LinearLayout) findViewById(R.id.ll_qq_share);
		ll_weixin_share = (LinearLayout) findViewById(R.id.ll_weixin_share);
		
		appInfo = (ShareApp) getIntent().getSerializableExtra(Constant.ITEM);
		
		if(null != appInfo){
			tv_detail.setOnClickListener(this);
			ll_weixin_share.setOnClickListener(this);
			ll_qq_share.setOnClickListener(this);
			
			ImageLoader.getInstance().displayImage(appInfo.getIcon(), iv_logo);
			tv_app_name.setText(appInfo.getTitle());
			float integral = (float) ((long)appInfo.getShare_integral()/10000.0);//���û���
			DecimalFormat df = new DecimalFormat("0.00");//��ʽ��С��
			df.setRoundingMode(RoundingMode.DOWN);
			String money = df.format(integral/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
			tv_score.setText("+"+money+"Ԫ/��");
			tv_share_count.setText(appInfo.getShare_count()+"");
			tv_desc.setText(appInfo.getDesc());
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_detail:
			intent = new Intent(ShareDetailActivity.this,WebsiteDetailActivity.class);
			intent.putExtra("url", appInfo.getPromoteUrl());
			intent.putExtra("title", appInfo.getTitle());
			startActivity(intent);
			break;
		case R.id.ll_weixin_share:
			wechatShare();
			break;
		case R.id.ll_qq_share:
			doShareTencent();
			break;
		default:
			break;
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (null != mTencent){
    		mTencent.onActivityResult(requestCode, resultCode, data);
    	}
    	
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
        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        bundle.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT,  QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);  
        //����������Ϣ�����ѵ�������תURL��
        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, appInfo.getPromoteUrl());
        //����ı��⡣ע��PARAM_TITLE��PARAM_IMAGE_URL��PARAM_SUMMARY����ȫΪ�գ����ٱ�����һ������ֵ�ġ�
        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, appInfo.getTitle());
        //�����ͼƬURL
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(appInfo.getIcon());
        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        //�������ϢժҪ���50����
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, appInfo.getDesc());
 
        mTencent.shareToQzone(ShareDetailActivity.this, bundle , ShareDetailActivity.this);

    }
	
	/**
	 * @author alan.xie
	 * @date 2015-1-27 ����5:37:59
	 * @Description: ΢�ŷ���
	 * @param @param flag
	 * @return void
	 */
	public void wechatShare(){  
		webpage = new WXWebpageObject();  
	    webpage.webpageUrl = appInfo.getPromoteUrl();  
	    msg = new WXMediaMessage(webpage);  
	    msg.title = appInfo.getTitle();  
	    msg.description = appInfo.getDesc();
	    
	    req = new SendMessageToWX.Req();  
	    req.transaction = String.valueOf(System.currentTimeMillis())+"share";  
	    req.message = msg;  
	    req.scene = SendMessageToWX.Req.WXSceneTimeline;  
	    
		
		ImageLoader.getInstance().loadImage(appInfo.getIcon(), new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				Toast.makeText(ShareDetailActivity.this, "���ڷ���...", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			    wxApi.sendReq(req);
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
				//�����滻һ���Լ��������ͼƬ��Դ  
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
		repostShare(1);
	}

	@Override
	public void onError(UiError arg0) {
		Toast.makeText(this, "QQ�������", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * @author alan.xie
	 * @date 2015-1-28 ����10:23:00
	 * @Description: �ϱ�
	 * @param 
	 * @return void
	 */
	public void repostShare(final int type){
		RequestParams params = new RequestParams();
		params.put("app_id", pref.getString(Constant.APPID, ""));
		params.put("target_id", appInfo.getId());
		params.put("type", "POST");
		params.put("channel", type);
		
		
		HttpUtil.post(Constant.REPORE_SHARE_URL, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if(response.getInt("code")!= 1){
						Toast.makeText(ShareDetailActivity.this, response.getString("info"), Toast.LENGTH_SHORT).show();
					}else{
						String str = "";
						if(type == 1){
							str = "QQ����ɹ�";
							myApplication.setTarget(appInfo.getId()+"_"+1);
						}
						if(type == 2){
							myApplication.setTarget(appInfo.getId()+"_"+2);
							str = "΢�ŷ���ɹ�";
							myApplication.setWxShare(false);
						}
						myApplication.setShare_count(Integer.parseInt(tv_share_count.getText().toString())+1);
						tv_share_count.setText((Integer.parseInt(tv_share_count.getText().toString())+1)+"");
						Toast.makeText(ShareDetailActivity.this, str, Toast.LENGTH_SHORT).show();
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
	}
	
	/**
	 * @author alan.xie
	 * @date 2015-1-27 ����5:36:09
	 * @Description: ����
	 * @param @param v
	 * @return void
	 */
	public void back(View v){
		this.finish();
	}
}












