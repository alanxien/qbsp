package com.chuannuo.qianbaosuoping;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.fragment.MovieFragment;
import com.chuannuo.qianbaosuoping.view.CustomDialog;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author alan.xie
 * @date 2015-1-5 下午3:15:37
 * @Description: 邀请任务
 */
public class InvitationActivity extends BaseActivity implements OnClickListener{
	
	private LinearLayout ll_weixin_friends; //微信好友
	private LinearLayout ll_weixin_cirele; 	//微信朋友圈
	private LinearLayout ll_share_qq; 		//qq分享
	private LinearLayout ll_qr_code;        //二维码
	private TextView tv_step_4;
	
	private CustomDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_promote_task);
		
		ll_weixin_friends = (LinearLayout) findViewById(R.id.ll_weixin_friends);
		ll_weixin_cirele = (LinearLayout) findViewById(R.id.ll_weixin_cirele);
		ll_share_qq = (LinearLayout) findViewById(R.id.ll_share_qq);
		tv_step_4 = (TextView) findViewById(R.id.tv_step_4);
		ll_qr_code = (LinearLayout) findViewById(R.id.ll_qr_code);
		
		ll_weixin_friends.setOnClickListener(this);
		ll_weixin_cirele.setOnClickListener(this);
		ll_share_qq.setOnClickListener(this);
		ll_qr_code.setOnClickListener(this);
		
		tv_step_4.setText(getResources().getString(R.string.step_4,pref.getString(Constant.INVIT_CODE, "")));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_weixin_friends:
			wechatShare(0);
			this.finish();
			break;
		case R.id.ll_weixin_cirele:
			wechatShare(1);
			this.finish();
			break;
		case R.id.ll_share_qq:
			doShareTencent();
			break;
		case R.id.ll_qr_code:
			mDialog = new CustomDialog(this, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
				
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
		default:
			break;
		}
	}
	
	
}
