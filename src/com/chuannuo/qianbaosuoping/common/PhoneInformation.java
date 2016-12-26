package com.chuannuo.qianbaosuoping.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

/**
 * @author alan.xie
 * @date 2015-3-31 上午11:02:56 
 * @Description: 手机信息
 */
public class PhoneInformation {
	
	public static TelephonyManager tm = null;
	public static LocationManager locationManager = null;
	public static WifiManager wifiMgr = null;

	public static void initTelephonyManager(Context context){
		if(null == tm ){
			tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		}
		if(null == locationManager){
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}
		if(null == wifiMgr){
			wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
                
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午3:24:25
	 * @Description: 国际移动设备识别码
	 * @param @return
	 * @return String
	 */
	public static String getImei(){
		return tm.getDeviceId();
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午3:27:26
	 * @Description: 国际移动用户识别码
	 * @param @return
	 * @return String
	 */
	public static String getImsi(){
		return tm.getSubscriberId();
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午3:34:33
	 * @Description: 设备类型
	 * @param @return
	 * @return String
	 */
	public static String getMachineType(){
		return Build.MODEL;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午3:35:19
	 * @Description: 系统版本
	 * @param @return
	 * @return String
	 */
	public static String getOsVersion(){
		return Build.VERSION.RELEASE;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午3:36:32
	 * @Description: 系统名称
	 * @param @return
	 * @return String
	 */
	public static String getOsName(){
		return "";
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午3:43:02
	 * @Description: 获取手机当前位置
	 * @param @return
	 * @return String
	 */
	public static String getPosition(){
		return "";
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午3:48:06
	 * @Description: 获取经纬度
	 * @param @return
	 * @return String
	 */
	public static String getLatitLongit(){
		double latitude=0.0;  
		double longitude =0.0;
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
            if(location != null){  
                latitude = location.getLatitude();  
                longitude = location.getLongitude();  
                }  
        }else{  
            LocationListener locationListener = new LocationListener() {  
                  
                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数  
            	@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub
					
				} 
                // Provider被enable时触发此函数，比如GPS被打开  
                @Override  
                public void onProviderEnabled(String provider) {  
                      
                }  
                  
                // Provider被disable时触发此函数，比如GPS被关闭   
                @Override  
                public void onProviderDisabled(String provider) {  
                      
                }  
                  
                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发   
                @Override  
                public void onLocationChanged(Location location) {  
                    if (location != null) {     
                        Log.e("Map", "Location changed : Lat: "    
                        + location.getLatitude() + " Lng: "    
                        + location.getLongitude());     
                    }  
                }
            };  
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);     
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);     
            if(location != null){     
                latitude = location.getLatitude(); //经度     
                longitude = location.getLongitude(); //纬度  
            }     
        }
        
        return latitude+"-"+longitude;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午3:58:36
	 * @Description: 获取网络类型
	 * @param @return
	 * @return String
	 */
	public static int getNetType(){
		return tm.getNetworkType();
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午4:04:35
	 * @Description: 获取语言
	 * @param @return
	 * @return String
	 */
	public static String  getLanguage(){
		return Locale.getDefault().getLanguage();
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-9 下午4:09:00
	 * @Description: mac地址
	 * @param @return
	 * @return String
	 */
	public static String getMacAddress() {
        // 获取mac地址：
        String macAddress = "000000000000";
        try {
            WifiInfo info = (null == wifiMgr ? null : wifiMgr
                    .getConnectionInfo());
            if (null != info) {
                if (!TextUtils.isEmpty(info.getMacAddress()))
                    macAddress = info.getMacAddress().replace(":", "");
                else
                    return macAddress;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return macAddress;
        }
        return macAddress;
    }
	
	/**
	 * @author alan.xie
	 * @date 2014-12-12 下午4:01:14
	 * @Description: 获取本地（GPRS）IP地址
	 * @param @return
	 * @return String
	 */
	public static String getLocalIpAddress() {  
        try {  
            for (Enumeration<NetworkInterface> en = NetworkInterface  
                    .getNetworkInterfaces(); en.hasMoreElements();) {  
                NetworkInterface intf = en.nextElement();  
                for (Enumeration<InetAddress> enumIpAddr = intf  
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                    InetAddress inetAddress = enumIpAddr.nextElement();  
                    if (!inetAddress.isLoopbackAddress()) {  
                        return inetAddress.getHostAddress().toString();  
                    }  
                }  
            }  
        } catch (SocketException ex) {  
            Log.e("WifiPreference IpAddress", ex.toString());  
        }  
        return "";  
    } 
	
	/**
	 * @author alan.xie
	 * @date 2014-12-12 下午4:59:10
	 * @Description: 获取IP地址
	 * @param @return
	 * @return String
	 */
	public static String getIpAddress() {  
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();       
        int ipAddress = wifiInfo.getIpAddress();   
        return intToIp(ipAddress);    
    }     
	
    public static String intToIp(int i) {       
         
          return (i & 0xFF ) + "." +       
        ((i >> 8 ) & 0xFF) + "." +       
        ((i >> 16 ) & 0xFF) + "." +       
        ( i >> 24 & 0xFF) ;  
     } 
	
	/**
	 * @author alan.xie
	 * @date 2014-12-12 下午4:46:23
	 * @Description: 系统总内存
	 * @param @return
	 * @return String
	 */
	public static String getTotalMemory() {  
        String str1 = "/proc/meminfo";  
        String str2="";
        Long total = 0l;
        FileReader localFileReader = null;
		BufferedReader localBufferedReader = null;
        try {  
        	localFileReader = new FileReader(str1);  
        	localBufferedReader = new BufferedReader(localFileReader, 8192);  
            str2 = localBufferedReader.readLine();
        } catch (IOException e) {
        	e.printStackTrace();
        }  finally{
        	try {
				localFileReader.close();
				localBufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        String regex = "\\d*";
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(str2);

        while (m.find()) {
        if (!"".equals(m.group()))
        	total += Long.valueOf(m.group());
        }
        return String.valueOf(total/1024);
    }
	
	/** 
	 * @Title: getVersion 
	 * @Description: TODO 
	 * @param @param context
	 * @param @return 
	 * @return String
	 * @throws 
	 */
	public static String getVersionName(Context context)//获取版本号  
    {  
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return pi.versionName;  
        } catch (NameNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return "1.0";  
        }  
    } 
	
}
















