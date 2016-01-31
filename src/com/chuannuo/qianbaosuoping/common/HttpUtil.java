package com.chuannuo.qianbaosuoping.common;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2014-10-21 ����10:29:20
 * @Description: HTTP���ʹ�����
 */
public class HttpUtil {

	private static AsyncHttpClient	client	= new AsyncHttpClient();	// ʵ��������
	
	static {
		client.setTimeout(11000); // �������ӳ�ʱ����������ã�Ĭ��Ϊ10s
	}

	// ��һ������url��ȡһ��string����
	public static void get(String urlString, AsyncHttpResponseHandler res) {
		client.get(urlString, res);
	}
	
	public static void post(String urlString, AsyncHttpResponseHandler res) {
		client.post(urlString, res);
	}
	
	//url���������
	public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res){
		client.get(urlString, params, res);
	}
	
	public static void post(String urlString, RequestParams params, AsyncHttpResponseHandler res){
		client.post(urlString, params, res);
	}

	// ������������ȡjson�����������
	public static void get(String urlString, JsonHttpResponseHandler res) {
		client.get(urlString, res);
	}
	
	public static void post(String urlString, JsonHttpResponseHandler res) {
		client.post(urlString, res);
	}

	// ����������ȡjson�����������
	public static void get(String urlString, RequestParams params,JsonHttpResponseHandler res) {
		client.get(urlString, params, res);
	}
	
	public static void post(String urlString, RequestParams params,JsonHttpResponseHandler res) {
	
		Log.d("onPointsChanged", "POST-url: "+urlString);
		client.post(urlString, params, res);
	}

	// ��������ʹ�ã��᷵��byte����
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

