package com.chuannuo.qianbaosuoping.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * @author alan.xie
 * @date 2015-2-9 ����10:57:40
 * @Description: ͨ��AlarmManager������ѯ
 */
public class PollingUtils {
	public static final String ACTION = "n.winads.studentsearn.service.PollingService";
	/**
	 * @author alan.xie
	 * @date 2015-2-9 ����11:32:42 
	 * @Description: ������ѯ����
	 * @param context
	 * @param seconds
	 * @param cls
	 * @param action
	 * @return void
	 * @throws
	 */ 
	public static void startPollingService(Context context,int seconds,Class<?> cls){
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context,cls);
		intent.setAction(ACTION);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtTime = SystemClock.elapsedRealtime();
		/*
		 * ʹ��AlarmManger��setRepeating�������ö���ִ�е�ʱ������seconds�룩����Ҫִ�е�Service
		 */
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, seconds*1000, pendingIntent);
	}
	
	/**
	 * @author alan.xie
	 * @date 2015-2-9 ����11:41:23 
	 * @Description: ֹͣ��ѯ 
	 * @param context
	 * @param cls
	 * @param action
	 * @return void
	 * @throws
	 */ 
	public static void stopPollingService(Context context,Class<?> cls){
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context,cls);
		intent.setAction(ACTION);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		manager.cancel(pendingIntent);
	}
}







