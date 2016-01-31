/* 
 * @Title:  ReleaseBitmap.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-1-19 上午1:23:22 
 * @version:  V1.0 
 */
package com.chuannuo.qianbaosuoping.common;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-1-19 上午1:23:22 
 * @version:  V1.0 
 */
public class ReleaseBitmap implements ImageLoadingListener {
	
	private List<Bitmap> mBitmap;
	
	public ReleaseBitmap(){
		mBitmap = new ArrayList<Bitmap>();
	}
	
	public void cleanBitmapList(){
		if(mBitmap.size() >0){
			for(int i=0; i<mBitmap.size(); i++){
				Bitmap b = mBitmap.get(i);
				if(b != null && !b.isRecycled()){
					b.recycle();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.nostra13.universalimageloader.core.listener.ImageLoadingListener#onLoadingCancelled(java.lang.String, android.view.View)
	 */
	@Override
	public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.nostra13.universalimageloader.core.listener.ImageLoadingListener#onLoadingComplete(java.lang.String, android.view.View, android.graphics.Bitmap)
	 */
	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		mBitmap.add(arg2);
	}

	/* (non-Javadoc)
	 * @see com.nostra13.universalimageloader.core.listener.ImageLoadingListener#onLoadingFailed(java.lang.String, android.view.View, com.nostra13.universalimageloader.core.assist.FailReason)
	 */
	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.nostra13.universalimageloader.core.listener.ImageLoadingListener#onLoadingStarted(java.lang.String, android.view.View)
	 */
	@Override
	public void onLoadingStarted(String arg0, View arg1) {
		// TODO Auto-generated method stub

	}

}
