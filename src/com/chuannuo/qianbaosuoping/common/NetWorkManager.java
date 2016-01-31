package com.chuannuo.qianbaosuoping.common;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * @author xin.xie
 * �������(wifi,gprs)
 */
public class NetWorkManager {

	private ConnectivityManager connectivityManager;
	
	public NetWorkManager(Context context){
		this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/**
	 * @author xin.xie
	 * @param context
	 * @return
	 * wifi�Ƿ����
	 */
	public boolean isWifiAvailable() {
		NetworkInfo mWiFiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWiFiNetworkInfo != null) {
			return mWiFiNetworkInfo.isAvailable();
		}
		return false;
	}
	
	/**
	 * @author xin.xie
	 * @return
	 * �ж�WiFi�Ƿ��
	 */
	public boolean isWiFiActive() {
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	
	/**
	 * @author xin.xie
	 * @return
	 * ������ر�WIFI
	 */
	public void setWifiEnable(Context context, boolean isEnable) {

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Service.WIFI_SERVICE);
		wifiManager.setWifiEnabled(isEnable);
	}
	
    /**
     * @author xin.xie
     * @param context
     * @return
     * ���GPRS���������Ƿ��
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean gprsIsOpenMethod()
    {
		Class cmClass = connectivityManager.getClass();
        Boolean isOpen = false;
        try
        {
            Method method = cmClass.getMethod("getMobileDataEnabled");

            isOpen = (Boolean) method.invoke(connectivityManager);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return isOpen;
    }
    
	/**
	 * @author xin.xie
	 * @param isEnable
	 * ������ر�gprs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setGprsEnable(boolean isEnable){
		Class cmClass = connectivityManager.getClass();
		Class[] argClasses     = new Class[1];
        argClasses[0]         = boolean.class;
		try {
			Method method = cmClass.getMethod("setMobileDataEnabled",argClasses);
			method.invoke(connectivityManager, isEnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}







