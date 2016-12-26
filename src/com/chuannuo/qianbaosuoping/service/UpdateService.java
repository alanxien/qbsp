package com.chuannuo.qianbaosuoping.service;

import java.io.File;

import com.chuannuo.qianbaosuoping.common.Constant;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

public class UpdateService extends Service {
	private DownloadManager dm;
	private long enqueue;
	private BroadcastReceiver receiver;
	
	private String packageUrl="";
	private String updateVersion= "";
	private boolean isNotifaticon = false; //鏄惁閫氱煡鏍忛�氱煡
	private Vibrator mVibrator;// 閲嶅姏鎰熷簲

	@Override
	public IBinder onBind(Intent intent) {
	    return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent!=null){
			isNotifaticon = intent.getBooleanExtra("isNotifaticon", false);
			packageUrl = intent.getStringExtra("packageUrl");
			updateVersion =intent.getStringExtra("updateVersion");
			
			mVibrator = (Vibrator) getApplication().getSystemService(
					VIBRATOR_SERVICE);
			
		    receiver = new BroadcastReceiver() {
		        @Override
		        public void onReceive(Context context, Intent intent) {
		            intent = new Intent(Intent.ACTION_VIEW);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            intent.setDataAndType(Uri.fromFile(new File(Constant.SDCARD +Constant.DOWNLOAD_DIR+"qianbaoduobao_v"+updateVersion+".apk")),
		                    "application/vnd.android.package-archive");
		            startActivity(intent);
		            stopSelf();
		        }
		    };

		    registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		    startDownload();
		    
		    startVibrato();
		}
	    return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
	    unregisterReceiver(receiver);
	    super.onDestroy();
	}

	private void startDownload() {
		File fileDir = new File(Constant.SDCARD +Constant.DOWNLOAD_DIR);
		FileUtils.deleteFile(fileDir);
		
	    dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
	    DownloadManager.Request request = new DownloadManager.Request(
	            Uri.parse(packageUrl));
	    request.setMimeType("application/vnd.android.package-archive");
	    request.setDestinationInExternalPublicDir(Constant.DOWNLOAD_DIR, "qianbaoduobao_v"+updateVersion+".apk");
	    enqueue = dm.enqueue(request);
	}
	
	/**
	 * @Title: startVibrato
	 * @Description: 鎸姩
	 * @param 鏁扮粍涓暟瀛楃殑鍚箟渚濇鏄�
	 *            [闈欐鏃堕暱锛岄渿鍔ㄦ椂闀匡紝闈欐鏃堕暱锛岄渿鍔ㄦ椂闀裤�傘�傘�俔鏃堕暱鐨勫崟浣嶆槸姣,绗簩涓弬鏁版槸閲嶅娆℃暟锛�-1涓轰笉閲嶅
	 * @return void
	 * @throws
	 */
	public void startVibrato() { // 瀹氫箟闇囧姩
		mVibrator.vibrate(new long[] { 400, 200, 400, 200 }, -1);
	}
}