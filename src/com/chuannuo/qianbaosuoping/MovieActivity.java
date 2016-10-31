package com.chuannuo.qianbaosuoping;

import java.text.SimpleDateFormat;

import net.youmi.android.offers.OffersManager;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.MyApplication;
import com.chuannuo.qianbaosuoping.common.PhoneInformation;
import com.chuannuo.qianbaosuoping.fragment.MoreFragment;
import com.chuannuo.qianbaosuoping.service.LockScreenService;
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
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-10-14 ����12:17:57
 * @Description: ��¼
 */
public class MovieActivity extends BaseActivity implements OnClickListener,IWXAPIEventHandler,IUiListener{
	
	private RelativeLayout rl_account_setting;    //�˺�����
	private RelativeLayout rl_share_application;  //����Ӧ��
	private RelativeLayout rl_promotion_strategy; //�ƹ㹥��
	private RelativeLayout rl_wave;           	  //ҡһҡ�齱
	private RelativeLayout rl_system_notice;      //ϵͳ֪ͨ
	private RelativeLayout rl_about_us;           //��������
	private RelativeLayout rl_qr_code;            //��ά��ͼƬ
	private RelativeLayout rl_feedback;           //����
	
	private Button btn_exit;
	
	private Intent intent;
	private SharedPreferences pref;
	private MyApplication mApplication;
	private Editor editor;
	private CustomDialog dialog,mDialog,sDialog;
	
	private ImageView iv_lock_screen;
	private TextView tv_lock_screen;
	
	private SimpleDateFormat sdf;
	private String year;
	
	private IWXAPI wxApi;
	public static Tencent mTencent;
	private FeedbackAgent agent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_more);
		
		rl_account_setting = (RelativeLayout) findViewById(R.id.rl_account_setting);
		rl_share_application = (RelativeLayout) findViewById(R.id.rl_share_application);
		rl_promotion_strategy = (RelativeLayout) findViewById(R.id.rl_promotion_strategy);
		rl_wave = (RelativeLayout) findViewById(R.id.rl_wave);
		rl_system_notice = (RelativeLayout) findViewById(R.id.rl_system_notice);
		rl_about_us = (RelativeLayout) findViewById(R.id.rl_about_us);
		rl_qr_code = (RelativeLayout) findViewById(R.id.rl_qr_code);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		iv_lock_screen = (ImageView) findViewById(R.id.iv_lock_screen);
		tv_lock_screen = (TextView) findViewById(R.id.tv_lock_screen);
		rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);
		
		rl_account_setting.setOnClickListener(this);
		rl_share_application.setOnClickListener(this);
		rl_promotion_strategy.setOnClickListener(this);
		rl_wave.setOnClickListener(this);
		rl_system_notice.setOnClickListener(this);
		rl_about_us.setOnClickListener(this);
		rl_qr_code.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		iv_lock_screen.setOnClickListener(this);
		rl_feedback.setOnClickListener(this);
		
		
		intent = new Intent();
		pref = getSharedPreferences(Constant.STUDENTS_EARN, FragmentActivity.MODE_PRIVATE);
		editor = pref.edit();
		mApplication = (MyApplication) getApplication();
		
		if(pref.getBoolean(Constant.IS_LOCK_SCREEN, true)){
			iv_lock_screen.setImageDrawable(getResources().getDrawable(R.drawable.radiobtn_on));
			tv_lock_screen.setText(getResources().getString(R.string.m_lock_screen_on));
		}else{
			iv_lock_screen.setImageDrawable(getResources().getDrawable(R.drawable.radiobtn_off));
			tv_lock_screen.setText(getResources().getString(R.string.m_lock_screen_off));
		}
		
		sdf=new SimpleDateFormat("yyyy");  
	    year=sdf.format(new java.util.Date()); 
		
		//ʵ����  ΢�ŷ���ӿ�
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, true);  
		wxApi.registerApp(Constant.WX_APP_ID);
		
		//���˷���
		if(agent == null){
			agent = new FeedbackAgent(this);
		}
		
		if(mTencent == null){
			mTencent = Tencent.createInstance(Constant.QQ_APP_ID, this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_account_setting:
			if(!pref.getString(Constant.PHONE, "").equals("")){
				intent.setClass(MovieActivity.this, AccountSettingActivity.class);
			}else{
				intent.setClass(MovieActivity.this, BindingPhoneActivity.class);
			}
			
			startActivity(intent);
			break;
		case R.id.rl_share_application:
			this.share();           
			break;
		case R.id.rl_promotion_strategy:
			intent.setClass(MovieActivity.this, StrategyActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_wave:
			intent.setClass(MovieActivity.this, WaveActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_system_notice:
			intent.setClass(MovieActivity.this, SysNoticeActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_about_us:
			intent.setClass(MovieActivity.this, AboutUsActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_exit:
			
			dialog = new CustomDialog(MovieActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
				
				@Override
				public void onClick(View view) {
					switch (view.getId()) {
					case R.id.btn_left:
						if(!pref.getString(Constant.PHONE, "").equals("")){
							mApplication.setPhone("");
							mApplication.setPassword("");
							editor.putString(Constant.PHONE, "");
							editor.putString(Constant.PASSWORD, "");
							editor.commit();
						}
						OffersManager.getInstance(MovieActivity.this).onAppExit();
						MovieActivity.this.finish();
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
			dialog.setBtnLeftStr(getResources().getString(R.string.exit_btn_left));
			dialog.setBtnRightStr(getResources().getString(R.string.exit_btn_right));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			break;
		case R.id.rl_qr_code:
			mDialog = new CustomDialog(MovieActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
				
				@Override
				public void onClick(View view) {
					mDialog.dismiss();
				}
			}, 0);
			mDialog.setTitle(getResources().getString(R.string.app_name));
			mDialog.setImage(Create2DCode(Constant.SHARE_URL+pref.getString(Constant.INVIT_CODE, "0")));
			mDialog.setBtnStr(getResources().getString(R.string.dg_confirm));
			mDialog.setCancelable(false);
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
			break;
		case R.id.iv_lock_screen:
			intent = new Intent(MovieActivity.this, LockScreenService.class);
			if(!pref.getBoolean(Constant.IS_LOCK_SCREEN, false)){
				MobclickAgent.onEvent(MovieActivity.this, "startLockScreen");
				iv_lock_screen.setImageDrawable(getResources().getDrawable(R.drawable.radiobtn_on));
				tv_lock_screen.setText(getResources().getString(R.string.m_lock_screen_on));
				editor.putBoolean(Constant.IS_LOCK_SCREEN, true);
				editor.putInt(Constant.SLIDED, 0);
				editor.commit();
				MovieActivity.this.startService(intent); //����Ҫ��ʾ�ĵ��÷���
			}else{
				MobclickAgent.onEvent(MovieActivity.this, "closeLockScreen");
				iv_lock_screen.setImageDrawable(getResources().getDrawable(R.drawable.radiobtn_off));
				tv_lock_screen.setText(getResources().getString(R.string.m_lock_screen_off));
				editor.putBoolean(Constant.IS_LOCK_SCREEN, false);
				editor.commit();
				MovieActivity.this.stopService(intent); //�رյ��÷���
			}
			break;
		case R.id.rl_feedback:
			agent.startFeedbackActivity();
			break;
		default:
			break;
		}
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
			matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 250, 250);
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
	
	
	/**
	 * @author alan.xie
	 * @date 2014-11-27 ����5:39:12
	 * @Description: ����
	 * @param 
	 * @return void
	 */
	private void share(){
		sDialog = new CustomDialog(MovieActivity.this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.ll_share_wx:
					MovieActivity.this.wechatShare(1);
					break;
				case R.id.ll_share_wx_friends:
					MovieActivity.this.wechatShare(0);
					break;
				case R.id.ll_share_qq:
					doShareTencent();
					break;
				default:
					break;
				}
			}
		}, 3);
		sDialog.show();
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
        //����������Ϣ�����ѵ�������תURL��
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Constant.SHARE_URL+pref.getString(Constant.INVIT_CODE, "0"));
        //����ı��⡣ע��PARAM_TITLE��PARAM_IMAGE_URL��PARAM_SUMMARY����ȫΪ�գ����ٱ�����һ������ֵ�ġ�
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, getResources().getString(R.string.app_name));
        //�����ͼƬURL
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Constant.LOGO_URL);
        //�������ϢժҪ���50����
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, year+"����������ֻ�׬ǮӦ��");
 
        MainActivity.mTencent.shareToQQ(MovieActivity.this, bundle , this);
 
        sDialog.dismiss();
    }
	
	/**
	 * @author alan.xie
	 * @date 2014-11-27 ����5:45:18
	 * @Description: ΢�ŷ���
	 * @param @param flag
	 * @return void
	 */
	public void wechatShare(int flag){  
	    WXWebpageObject webpage = new WXWebpageObject();  
	    webpage.webpageUrl = Constant.SHARE_URL+pref.getString(Constant.INVIT_CODE, "0");  
	    WXMediaMessage msg = new WXMediaMessage(webpage);  
	    msg.title = getResources().getString(R.string.app_name);  
	    msg.description = year+"����׬Ǯ��Ӧ�ã�";  
	    Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);  
	    msg.setThumbImage(thumb);  
	    thumb.recycle();
	      
	    SendMessageToWX.Req req = new SendMessageToWX.Req();  
	    req.transaction = String.valueOf(System.currentTimeMillis());  
	    req.message = msg;  
	    req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;  
	    wxApi.sendReq(req);
	    sDialog.dismiss();
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onComplete(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(UiError arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp arg0) {
		// TODO Auto-generated method stub
		
	} 

}













