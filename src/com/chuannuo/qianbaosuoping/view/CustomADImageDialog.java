package com.chuannuo.qianbaosuoping.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.service.DownloadService;

  
public class CustomADImageDialog extends Dialog implements android.view.View.OnClickListener{  
	private Context context = null;  
    private static CustomADImageDialog customAdImageDialog = null; 
    private static ImageView iv_ad_image;
    private static ImageView iv_ad_close;
    private static ImageView iv_big_image;
    private static ProgressBar progressBar;
    private static TextView tv_per;
    private AppInfo appInfo;
    
    private static Intent intent ;
      
    public CustomADImageDialog(Context context){  
        super(context);  
        this.context = context;  
    }  
      
    public CustomADImageDialog(Context context, int theme) {  
        super(context, theme);  
        this.context = context;
    }  
      
    public static CustomADImageDialog createDialog(Context context,int t){  
    	if(context != null){
    		customAdImageDialog = new CustomADImageDialog(context,R.style.CustomProgressDialog);  
    		customAdImageDialog.getWindow().getAttributes().gravity = Gravity.CENTER;  
    	}
    	if(t == 1){
    		customAdImageDialog.setContentView(R.layout.custom_ad_image_dialog); 
    		customAdImageDialog.setCanceledOnTouchOutside(false);
    		iv_ad_image = (ImageView) customAdImageDialog.findViewById(R.id.iv_ad_image);
        	iv_ad_close = (ImageView) customAdImageDialog.findViewById(R.id.iv_ad_close);
        	iv_ad_image.setOnClickListener(customAdImageDialog);
        	iv_ad_close.setOnClickListener(customAdImageDialog);
    	}else if(t == 2){
    		customAdImageDialog.setContentView(R.layout.custom_big_image_dialog); 
    		customAdImageDialog.setCanceledOnTouchOutside(false);
    		iv_big_image = (ImageView) customAdImageDialog.findViewById(R.id.iv_big_image);
        	iv_big_image.setOnClickListener(customAdImageDialog);
    	}else if(t == 3){
    		customAdImageDialog.setContentView(R.layout.upload_progress_dialog); 
    		customAdImageDialog.setCanceledOnTouchOutside(false);
    		progressBar = (ProgressBar) customAdImageDialog.findViewById(R.id.progressbar);
    		tv_per = (TextView) customAdImageDialog.findViewById(R.id.tv_per);
    	}
    	if(intent == null){
    		intent = new Intent(context,DownloadService.class);
    	}
        return customAdImageDialog;  
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_ad_image:
			downLoadApp();
			customAdImageDialog.dismiss();
			break;
		case R.id.iv_ad_close:
			customAdImageDialog.dismiss();
		case R.id.iv_big_image:
			customAdImageDialog.dismiss();
			break;
		default:
			break;
		}
	}
	
	public CustomADImageDialog setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
		return customAdImageDialog;
	}
	
	public CustomADImageDialog setImage(Bitmap bm){
		iv_ad_image.setImageBitmap(bm);
    	return customAdImageDialog;
    }
	
	public CustomADImageDialog setProgress(int num){
		progressBar.setProgress(num);
    	return customAdImageDialog;
    }
	
	public CustomADImageDialog setBigImage(Bitmap bm){
		iv_big_image.setImageBitmap(bm);
    	return customAdImageDialog;
	}
	
	public CustomADImageDialog setPer(int num){
		tv_per.setText(num+"%");
		return customAdImageDialog;
	}
	
	public void downLoadApp(){
		if(null != appInfo && appInfo.getFile() != null && !appInfo.getFile().equals("")){
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.ITEM, appInfo);
			intent.putExtras(bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(intent);
		}
	}
    
}  
