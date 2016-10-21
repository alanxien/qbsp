package com.chuannuo.qianbaosuoping.service;

import com.chuannuo.qianbaosuoping.LockScreenActivity;
import com.chuannuo.qianbaosuoping.common.Constant;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class LockScreenService extends Service {
	private final static String TAG = "LockScreenService";
	private Intent lockIntent;
	private KeyguardManager keyguardManager = null;
	private KeyguardManager.KeyguardLock keyguardLock = null;
	
	private SharedPreferences pref;
	private Editor editor;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "---------------onCreate-----------------");

		lockIntent = new Intent(LockScreenService.this, LockScreenActivity.class);
		lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		//注册广播
		IntentFilter mScreenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		LockScreenService.this.registerReceiver(mScreenOffReceiver, mScreenOffFilter);
		
		pref = getApplicationContext().getSharedPreferences(Constant.STUDENTS_EARN, FragmentActivity.MODE_PRIVATE);
		editor = pref.edit();
		
		keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
		keyguardLock = keyguardManager.newKeyguardLock("");
		keyguardLock.disableKeyguard(); //这里就是取消系统默认的锁
	}
	

	@SuppressWarnings("deprecation")
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "---------------onDestroy-----------------");
		LockScreenService.this.unregisterReceiver(mScreenOffReceiver);
		keyguardLock.reenableKeyguard();
		if (pref.getBoolean(Constant.IS_LOCK_SCREEN, false)){
			//重新启动activity
			Log.i(TAG, "---------------onDestroy重启-----------------");
			startService(new Intent(LockScreenService.this, LockScreenService.class));
		}
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "---------------onStartCommand-----------------"+intent);
		return Service.START_STICKY;
	}
	
	/**
	 * 屏幕变亮的广播，这里要隐藏系统的夺宝界面
	 */
	private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, intent.getAction());
			if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
					|| intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
				
				Log.i(TAG, lockIntent+"----"+pref.getInt(Constant.SLIDED, 0));
				if(pref.getInt(Constant.SLIDED, 0) == 0){//防止多次启动屏幕
					startActivity(lockIntent);
					editor.putInt(Constant.SLIDED, 1);
					editor.commit();
				}
			}
		}
	};
	
}



