package com.chuannuo.qianbaosuoping.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.service.DownloadService;

  
public class CustomADImageDialog extends Dialog implements android.view.View.OnClickListener{  
	private Context context = null;  
    private static CustomADImageDialog customAdImageDialog = null; 
    private static ImageView iv_ad_image;
    private static ImageView iv_ad_close;
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
      
    public static CustomADImageDialog createDialog(Context context){  
    	if(context != null){
    		customAdImageDialog = new CustomADImageDialog(context,R.style.CustomProgressDialog);  
    		customAdImageDialog.setContentView(R.layout.custom_ad_image_dialog);  
    		customAdImageDialog.getWindow().getAttributes().gravity = Gravity.CENTER;  
    		customAdImageDialog.setCanceledOnTouchOutside(false);
    	}
    	if(intent == null){
    		intent = new Intent(context,DownloadService.class);
    	}
    	
    	iv_ad_image = (ImageView) customAdImageDialog.findViewById(R.id.iv_ad_image);
    	iv_ad_close = (ImageView) customAdImageDialog.findViewById(R.id.iv_ad_close);
    	iv_ad_image.setOnClickListener(customAdImageDialog);
    	iv_ad_close.setOnClickListener(customAdImageDialog);
    	
    	
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
