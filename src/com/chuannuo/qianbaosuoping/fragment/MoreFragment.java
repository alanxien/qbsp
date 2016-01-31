package com.chuannuo.qianbaosuoping.fragment;

import java.text.SimpleDateFormat;

import net.youmi.android.offers.OffersManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.AboutUsActivity;
import com.chuannuo.qianbaosuoping.AccountSettingActivity;
import com.chuannuo.qianbaosuoping.BaseActivity;
import com.chuannuo.qianbaosuoping.BindingPhoneActivity;
import com.chuannuo.qianbaosuoping.MainActivity;
import com.chuannuo.qianbaosuoping.NewsHelpActivity;
import com.chuannuo.qianbaosuoping.PerfectInfoActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.StrategyActivity;
import com.chuannuo.qianbaosuoping.SysNoticeActivity;
import com.chuannuo.qianbaosuoping.WaveActivity;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.MyApplication;
import com.chuannuo.qianbaosuoping.service.LockScreenService;
import com.chuannuo.qianbaosuoping.view.CustomDialog;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
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

/**
 * @author alan.xie
 * @date 2014-10-14 ����12:19:21
 * @Description: ����
 */
public class MoreFragment extends Fragment implements OnClickListener,IWXAPIEventHandler,IUiListener{
	
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
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more, container, false);
		
		rl_account_setting = (RelativeLayout) view.findViewById(R.id.rl_account_setting);
		rl_share_application = (RelativeLayout) view.findViewById(R.id.rl_share_application);
		rl_promotion_strategy = (RelativeLayout) view.findViewById(R.id.rl_promotion_strategy);
		rl_wave = (RelativeLayout) view.findViewById(R.id.rl_wave);
		rl_system_notice = (RelativeLayout) view.findViewById(R.id.rl_system_notice);
		rl_about_us = (RelativeLayout) view.findViewById(R.id.rl_about_us);
		rl_qr_code = (RelativeLayout) view.findViewById(R.id.rl_qr_code);
		btn_exit = (Button) view.findViewById(R.id.btn_exit);
		iv_lock_screen = (ImageView) view.findViewById(R.id.iv_lock_screen);
		tv_lock_screen = (TextView) view.findViewById(R.id.tv_lock_screen);
		rl_feedback = (RelativeLayout) view.findViewById(R.id.rl_feedback);
		
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
		pref = this.getActivity().getSharedPreferences(Constant.STUDENTS_EARN, FragmentActivity.MODE_PRIVATE);
		editor = pref.edit();
		mApplication = (MyApplication) this.getActivity().getApplication();
		
		if(pref.getBoolean(Constant.IS_LOCK_SCREEN, true)){
			iv_lock_screen.setImageDrawable(MoreFragment.this.getActivity().getResources().getDrawable(R.drawable.radiobtn_on));
			tv_lock_screen.setText(MoreFragment.this.getActivity().getResources().getString(R.string.m_lock_screen_on));
		}else{
			iv_lock_screen.setImageDrawable(MoreFragment.this.getActivity().getResources().getDrawable(R.drawable.radiobtn_off));
			tv_lock_screen.setText(MoreFragment.this.getActivity().getResources().getString(R.string.m_lock_screen_off));
		}
		
		sdf=new SimpleDateFormat("yyyy");  
	    year=sdf.format(new java.util.Date()); 
		
		//ʵ����  ΢�ŷ���ӿ�
		wxApi = WXAPIFactory.createWXAPI(MoreFragment.this.getActivity(), Constant.WX_APP_ID, true);  
		wxApi.registerApp(Constant.WX_APP_ID);
		
		//���˷���
		if(agent == null){
			agent = new FeedbackAgent(MoreFragment.this.getActivity());
		}
		
		if(mTencent == null){
			mTencent = Tencent.createInstance(Constant.QQ_APP_ID, MoreFragment.this.getActivity());
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_account_setting:
			if(!pref.getString(Constant.PHONE, "").equals("")){
				intent.setClass(MoreFragment.this.getActivity(), AccountSettingActivity.class);
			}else{
				intent.setClass(MoreFragment.this.getActivity(), BindingPhoneActivity.class);
			}
			
			startActivity(intent);
			break;
		case R.id.rl_share_application:
			this.share();           
			break;
		case R.id.rl_promotion_strategy:
			intent.setClass(MoreFragment.this.getActivity(), StrategyActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_wave:
			intent.setClass(MoreFragment.this.getActivity(), WaveActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_system_notice:
			intent.setClass(MoreFragment.this.getActivity(), SysNoticeActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_about_us:
			intent.setClass(MoreFragment.this.getActivity(), AboutUsActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_exit:
			
			dialog = new CustomDialog(MoreFragment.this.getActivity(), R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
				
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
						OffersManager.getInstance(MoreFragment.this.getActivity()).onAppExit();
						MoreFragment.this.getActivity().finish();
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
			mDialog = new CustomDialog(MoreFragment.this.getActivity(), R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
				
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
			intent = new Intent(MoreFragment.this.getActivity(), LockScreenService.class);
			if(!pref.getBoolean(Constant.IS_LOCK_SCREEN, false)){
				MobclickAgent.onEvent(MoreFragment.this.getActivity(), "startLockScreen");
				iv_lock_screen.setImageDrawable(MoreFragment.this.getActivity().getResources().getDrawable(R.drawable.radiobtn_on));
				tv_lock_screen.setText(MoreFragment.this.getActivity().getResources().getString(R.string.m_lock_screen_on));
				editor.putBoolean(Constant.IS_LOCK_SCREEN, true);
				editor.putInt(Constant.SLIDED, 0);
				editor.commit();
				MoreFragment.this.getActivity().startService(intent); //����Ҫ��ʾ�ĵ��÷���
			}else{
				MobclickAgent.onEvent(MoreFragment.this.getActivity(), "closeLockScreen");
				iv_lock_screen.setImageDrawable(MoreFragment.this.getActivity().getResources().getDrawable(R.drawable.radiobtn_off));
				tv_lock_screen.setText(MoreFragment.this.getActivity().getResources().getString(R.string.m_lock_screen_off));
				editor.putBoolean(Constant.IS_LOCK_SCREEN, false);
				editor.commit();
				MoreFragment.this.getActivity().stopService(intent); //�رյ��÷���
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
		sDialog = new CustomDialog(MoreFragment.this.getActivity(), R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.ll_share_wx:
					MoreFragment.this.wechatShare(1);
					break;
				case R.id.ll_share_wx_friends:
					MoreFragment.this.wechatShare(0);
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
 
        MainActivity.mTencent.shareToQQ(MoreFragment.this.getActivity(), bundle , this);
 
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











