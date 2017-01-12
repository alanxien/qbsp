/* 
 * @Title:  CanyuzheAdapter.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<���������ļ�����ʲô��> 
 * @author:  xie.xin
 * @data:  2016-1-18 ����10:00:21 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.movie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.duobao.model.AiqiyiModel;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.chuannuo.qianbaosuoping.duobao.model.BaiduModel;
import com.chuannuo.qianbaosuoping.duobao.model.XunleiModel;

/** 
 * TODO<������������Ǹ�ʲô��> 
 * @author  xie.xin 
 * @data:  2016-1-18 ����10:00:21 
 * @version:  V1.0 
 */
public class XunleiAdapter extends BaseAdapter {

	private List<XunleiModel> list;
	private Context context;
	LayoutInflater la;
	
	public XunleiAdapter(Context context,List<XunleiModel> list){
		this.list = list;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			la = LayoutInflater.from(context);
			convertView = la.inflate(R.layout.item_link, null);
			
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.password = (TextView) convertView.findViewById(R.id.password);
			holder.link = (TextView) convertView.findViewById(R.id.link);
			convertView.setTag(holder); 
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
			final XunleiModel x = (XunleiModel) list.get(position);
			holder.title.setText(x.getTitle());
			holder.link.setText(x.getLink());
			holder.password.setVisibility(View.GONE);
			
			holder.link.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String packageName = "com.xunlei.downloadprovider";
					if (isAppInstalled(context, packageName)) {
						copy(x.getLink(), context);
						doStartApplicationWithPackageName(packageName);
					} else {
						Intent intent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(Constant.XUNLEI_APK_URL));
						intent.addCategory("android.intent.category.DEFAULT");
						context.startActivity(intent);
					}
				}
			});
		
		return convertView;
	}
	
	class ViewHolder{ 
		public TextView title;
		public TextView password;
		public TextView link;
	}
	
	public static void copy(String content, Context context) {
		// �õ������������
		ClipboardManager clipboardManager = (ClipboardManager) context
				.getSystemService(context.CLIPBOARD_SERVICE);
		ClipData clipData = ClipData.newPlainText("label", content); // �ı�������
																		// clipData
																		// �Ĺ��췽����
		clipboardManager.setPrimaryClip(clipData); // �� �ַ��� str ���� ��������
	}
	
	public boolean isAppInstalled(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);
	}

	/**
	 * @author alan.xie
	 * @date 2014-12-18 ����3:27:00
	 * @Description: ͨ����������Ӧ�ó���
	 * @param @param packagename
	 * @return void
	 */
	private void doStartApplicationWithPackageName(String packagename) {

		// ͨ��������ȡ��APP��ϸ��Ϣ������Activities��services��versioncode��name�ȵ�
		PackageInfo packageinfo = null;
		try {
			packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// ����һ�����ΪCATEGORY_LAUNCHER�ĸð�����Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// ͨ��getPackageManager()��queryIntentActivities��������
		List<ResolveInfo> resolveinfoList = context.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = ����packname
			String packageName = resolveinfo.activityInfo.packageName;
			// �����������Ҫ�ҵĸ�APP��LAUNCHER��Activity[��֯��ʽ��packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// ����ComponentName����1:packagename����2:MainActivity·��
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}

}

