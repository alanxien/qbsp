package com.chuannuo.qianbaosuoping;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.common.NetWorkManager;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-10-13 下午2:36:10
 * @Description: 用户启动界面
 */
public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGTH = 3000; //启动界面延时2秒
	
	private boolean isFirstIn; //是否第一次启动
	private static SharedPreferences pref; 
	private Editor editor;
	private Intent intent;
	private NetWorkManager netWorkManager;
	private CustomDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_start);
		
		init();
		start();
	}
	
	private void init(){
		intent = new Intent();
		pref = SplashActivity.this.getSharedPreferences(Constant.STUDENTS_EARN, MODE_PRIVATE);
		isFirstIn = pref.getBoolean(Constant.IS_FIRST_IN, true);
		editor = pref.edit();
		
		netWorkManager = new NetWorkManager(this);
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-13 下午2:36:42
	 * @Description: 通过sharedPreferences判断用户是否是第一次使用app
	 * @param 
	 * @return void
	 */
	private void start(){
		
		if(!netWorkManager.isWiFiActive() && !netWorkManager.gprsIsOpenMethod()){
			mDialog = new CustomDialog(this,R.style.CustomDialog,new CustomDialog.CustomDialogListener() {
				
				@Override
				public void onClick(View view) {
					mDialog.cancel();
					SplashActivity.this.finish();
				}
			},1);
			mDialog.setTitle(getResources().getString(R.string.dg_remind_title));
			mDialog.setContent(getResources().getString(R.string.dg_remind_msg));
			mDialog.setBtnStr(getResources().getString(R.string.dg_confirm));
			mDialog.show();
		}else{

			//如果是第一次使用，则跳转到引导界面
			if(isFirstIn){
				//创建快捷方式
				if(!isAddShortCut()){
					addShortCut();
				}
				
				//延时三秒跳转到 引导界面
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						intent.setClass(SplashActivity.this, GuideActivity.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in,
								android.R.anim.fade_out);
						SplashActivity.this.finish();
					}
				}, SPLASH_DISPLAY_LENGTH);
			}else{
				if(pref.getString(Constant.PHONE, "").equals("") && !pref.getBoolean(Constant.IS_QUICK_START, false)){
					intent.setClass(SplashActivity.this, LoginActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
				}else{
					//否则延时三秒跳转到 主界面
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							intent.setClass(SplashActivity.this, MainActivity.class);
							startActivity(intent);
							overridePendingTransition(android.R.anim.fade_in,
									android.R.anim.fade_out);
							SplashActivity.this.finish();
						}
					}, SPLASH_DISPLAY_LENGTH);
				}
			}
		}
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-13 下午2:37:04
	 * @Description: 判断是否已经创建了快捷方式
	 * @param @return
	 * @return boolean
	 */
	public boolean isAddShortCut(){
		
		boolean isInstallShortcut = false;
        final ContentResolver cr = this.getContentResolver();

        int versionLevel = android.os.Build.VERSION.SDK_INT;
        String AUTHORITY = "com.android.launcher2.settings";
       
        //2.2以上的系统的文件文件名字是不一样的
        if (versionLevel >= 8) {
            AUTHORITY = "com.android.launcher2.settings";
        } else {
            AUTHORITY = "com.android.launcher.settings";
        }

        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI,
                new String[] { "title", "iconResource" }, "title=?",
                new String[] { getString(R.string.app_name) }, null);

        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
        }
        return isInstallShortcut;
	}

	/**
	 * @author alan.xie
	 * @Description: 添加快捷方式
	 * @param 
	 * @return void
	 */
	public void addShortCut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 设置属性
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getResources().getString(R.string.app_name));
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				this.getApplicationContext(), R.drawable.ic_launcher);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconRes);

		// 是否允许重复创建
		shortcut.putExtra("duplicate", false);

		// 设置桌面快捷方式的图标
		Parcelable icon = Intent.ShortcutIconResource.fromContext(this,
				R.drawable.ic_launcher);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

		// 点击快捷方式的操作
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(SplashActivity.this, SplashActivity.class);

		// 设置启动程序
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// 广播通知桌面去创建
		this.sendBroadcast(shortcut);
	}

}









