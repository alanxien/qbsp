package com.chuannuo.qianbaosuoping.common;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2014-10-21 上午10:29:20
 * @Description: HTTP访问工具类
 */
public class HttpUtil {

	private static AsyncHttpClient	client	= new AsyncHttpClient();	// 实例话对象
	
	static {
		client.setTimeout(11000); // 设置链接超时，如果不设置，默认为10s
	}

	// 用一个完整url获取一个string对象
	public static void get(String urlString, AsyncHttpResponseHandler res) {
		client.get(urlString, res);
	}
	
	public static void post(String urlString, AsyncHttpResponseHandler res) {
		client.post(urlString, res);
	}
	
	//url里面带参数
	public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res){
		client.get(urlString, params, res);
	}
	
	public static void post(String urlString, RequestParams params, AsyncHttpResponseHandler res){
		client.post(urlString, params, res);
	}

	// 不带参数，获取json对象或者数组
	public static void get(String urlString, JsonHttpResponseHandler res) {
		client.get(urlString, res);
	}
	
	public static void post(String urlString, JsonHttpResponseHandler res) {
		client.post(urlString, res);
	}

	// 带参数，获取json对象或者数组
	public static void get(String urlString, RequestParams params,JsonHttpResponseHandler res) {
		client.get(urlString, params, res);
	}
	
	public static void post(String urlString, RequestParams params,JsonHttpResponseHandler res) {
	
		Log.d("onPointsChanged", "POST-url: "+urlString);
		client.post(urlString, params, res);
	}

	// 下载数据使用，会返回byte数据
	public static void get(String uString, BinaryHttpResponseHandler bHandler) {
		client.get(uString, bHandler);
	}
	
	public static void post(String uString, BinaryHttpResponseHandler bHandler) {
		client.post(uString, bHandler);
	}

	public static AsyncHttpClient getClient(){
		return client;
	}
}

