package com.chuannuo.qianbaosuoping;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 关于我们
 */
public class AboutUsActivity extends BaseActivity {
	
	private TextView tv_versions;
	PackageInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		
		PackageManager manager = AboutUsActivity.this.getPackageManager();
		try {
			info = manager.getPackageInfo(AboutUsActivity.this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tv_versions = (TextView) findViewById(R.id.tv_versions);
		tv_versions.setText("版本："+info.versionName);
	}

}
