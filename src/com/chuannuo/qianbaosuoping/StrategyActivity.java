package com.chuannuo.qianbaosuoping;

import com.chuannuo.qianbaosuoping.R;

import android.os.Bundle;
import android.webkit.WebView;

/**
 * @author alan.xie
 * @date 2014-10-14 ÏÂÎç12:17:57
 * @Description: ÍÆ¹ã¹¥ÂÔ
 */
public class StrategyActivity extends BaseActivity {

	private WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_strategy);
		
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.loadUrl("file:///android_asset/www/strategy.html");
	}

}
