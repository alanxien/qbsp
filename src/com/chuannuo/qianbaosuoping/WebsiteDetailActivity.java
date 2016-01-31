package com.chuannuo.qianbaosuoping;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 推广攻略
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebsiteDetailActivity extends BaseActivity {

	private WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_website_detail);
		
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		String title = intent.getStringExtra("title");
		setTitle(title);
		
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.getSettings().setUseWideViewPort(true); 
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		
		mWebView.setWebChromeClient(new WebChromeClient() {  
            public void onProgressChanged(WebView view, int progress) {  
              //Activity和Webview根据加载程度决定进度条的进度大小  
             //当加载到100%的时候 进度条自动消失  
            	WebsiteDetailActivity.this.setProgress(progress * 100);  
     }  
  });
		mWebView.loadUrl(url);
	}

}
