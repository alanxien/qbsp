package com.chuannuo.qianbaosuoping.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author alan.xie
 * @date 2014-10-13 ����2:39:38
 * @Description: ������������������
 */
public class GuideViewPagerAdapter extends PagerAdapter {

	private ArrayList<View> views;
	
	public GuideViewPagerAdapter (ArrayList<View> views){  
        this.views = views;  
    }  
	
	@Override
	public int getCount() {
		if (views != null) {  
            return views.size();  
        }        
        return 0;
	}
	
	/*
	 * ��ʼ��positionλ�õĽ���
	 */
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position), 0);  
        
        return views.get(position);
	}

	/*
	 * �Ƿ��ɶ������ɽ���
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return (arg0 == arg1);
	}
	
	/*
	 * ����positionλ�õĽ���
	 */
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position)); 
	}

}
