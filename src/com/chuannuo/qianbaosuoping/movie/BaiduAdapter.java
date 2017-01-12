/* 
 * @Title:  CanyuzheAdapter.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<���������ļ�����ʲô��> 
 * @author:  xie.xin
 * @data:  2016-1-18 ����10:00:21 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.movie;

import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.R;
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
public class BaiduAdapter extends BaseAdapter {

	private List<BaiduModel> list;
	private Context context;
	LayoutInflater la;
	
	public BaiduAdapter(Context context,List<BaiduModel> list){
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
		
			final BaiduModel b = (BaiduModel) list.get(position);
			holder.title.setText(b.getTitle());
			holder.link.setText(b.getLink());
			holder.password.setVisibility(View.VISIBLE);
			holder.password.setText(b.getPassword());
			holder.link.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					copy(b.getPassword(), context);
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

}

