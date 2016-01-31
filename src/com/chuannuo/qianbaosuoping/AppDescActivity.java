package com.chuannuo.qianbaosuoping;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;


/**
 * @author alan.xie
 * @date 2014-10-14 ����12:17:57
 * @Description: ��������
 */
public class AppDescActivity extends BaseActivity {

	private WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_desc);
		
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.loadUrl("file:///android_asset/www/introduce.html");
		if(getIntent().getBooleanExtra("isNewTask", false)){
			editor.putBoolean(Constant.TASK_KNOW_APP, true);
			editor.commit();
			addIntegral(2000,Constant.TASK_KNOW_APP);
		}
		
	}

}
