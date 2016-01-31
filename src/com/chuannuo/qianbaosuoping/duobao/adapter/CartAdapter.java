/* 
 * @Title:  CanyuzheAdapter.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-18 下午10:00:21 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.duobao.adapter;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.duobao.model.Canyuzhe;
import com.chuannuo.qianbaosuoping.duobao.model.Cart;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * TODO<请描述这个类是干什么的>
 * 
 * @author xie.xin
 * @data: 2016-1-18 下午10:00:21
 * @version: V1.0
 */
public class CartAdapter extends BaseAdapter {

	private List<Cart> list;
	private Context context;
	private CartClickListener mListener;
	LayoutInflater la;

	public CartAdapter(Context context, List<Cart> list,
			CartClickListener mListener,int num) {
		this.list = list;
		this.context = context;
		this.mListener = mListener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			la = LayoutInflater.from(context);
			convertView = la.inflate(R.layout.db_cart_item, null);

			holder = new ViewHolder();
			holder.pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.et_num = (EditText) convertView.findViewById(R.id.et_num);
			holder.tv_r_money = (TextView) convertView
					.findViewById(R.id.tv_r_money);
			holder.tv_t_money = (TextView) convertView
					.findViewById(R.id.tv_t_money);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.cb_delete = (CheckBox) convertView
					.findViewById(R.id.cb_delete);
			holder.picker_add = (ImageView) convertView
					.findViewById(R.id.picker_add);
			holder.picker_minus = (ImageView) convertView
					.findViewById(R.id.picker_minus);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageLoader.getInstance().displayImage(list.get(position).getPic(), holder.pic);
		holder.et_num.setTag(position);
		holder.et_num.setText(list.get((Integer)holder.et_num.getTag()).getCount() + "");
		holder.tv_t_money.setText("总需" + list.get(position).gettMoney() + "人次");
		holder.tv_r_money
				.setText(Html.fromHtml(context.getResources().getString(
						R.string.r_money, list.get(position).gettMoney() - list.get(position).getpMoney() + "")));
		holder.tv_title.setText(list.get(position).getTitle());

		holder.cb_delete
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							list.get(position).setChecked(true);
						} else {
							list.get(position).setChecked(false);
						}
						mListener.onChecked();
						
					}
				});
		if (list.get(position).isChecked()) {
			holder.cb_delete.setChecked(true);
		} else {
			holder.cb_delete.setChecked(false);
		}

		holder.picker_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isEnable()){
					holder.et_num.setText(Integer.parseInt(holder.et_num.getText().toString().equals("")?"0":holder.et_num.getText().toString())+1+"");
				}
			}
		});

		holder.picker_minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isEnable()){
					holder.et_num.setText(Integer.parseInt(holder.et_num.getText().toString().equals("")?"0":holder.et_num.getText().toString())-1+"");
				}
			}
		});

		holder.et_num.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				int len = s.toString().length();
				String t = s.toString();
				if (len == 1 && t.equals("0")) {
					s.clear();
					s.append("1");
				}
				int o = list.get((Integer)holder.et_num.getTag()).getCount();
				int a = s.toString() == null || s.toString().equals("") ? 0
						: Integer.parseInt(s.toString());
				if(o != a){
					mListener.optCart(list.get((Integer)holder.et_num.getTag()),a);
				}
				
				list.get((Integer)holder.et_num.getTag()).setCount(a);
				mListener.onChangedNum();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});

		return convertView;
	}
	
	private boolean isEnable(){
		Iterator<Cart> c = list.iterator();
		int num =0;
		while(c.hasNext()){
			Cart obj = c.next();
			if(obj.isChecked()){
				num++;
			}
		}
		if(num == 0){
			return true;
		}else{
			return false;
		}
	}
	
	class ViewHolder {
		public ImageView pic;
		public TextView tv_title;
		public TextView tv_t_money;
		public TextView tv_r_money;
		public EditText et_num;
		public CheckBox cb_delete;
		public ImageView picker_add;
		public ImageView picker_minus;
	}

	public interface CartClickListener {
		void onChangedNum();
		void onChecked();
		void optCart(Cart c,int num);
	}
}
