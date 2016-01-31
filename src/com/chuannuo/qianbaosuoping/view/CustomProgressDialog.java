package com.chuannuo.qianbaosuoping.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import com.chuannuo.qianbaosuoping.R;
  
public class CustomProgressDialog extends Dialog {  
	private Context context = null;  
    private static CustomProgressDialog customProgressDialog = null;  
      
    public CustomProgressDialog(Context context){  
        super(context);  
        this.context = context;  
    }  
      
    public CustomProgressDialog(Context context, int theme) {  
        super(context, theme);  
    }  
      
    public static CustomProgressDialog createDialog(Context context){  
    	if(context != null){
    		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);  
    		customProgressDialog.setContentView(R.layout.custom_progress_dialog);  
    		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;  
    		customProgressDialog.setCanceledOnTouchOutside(false);
    	}
        return customProgressDialog;  
    }  
   
    public void onWindowFocusChanged(boolean hasFocus){  
          
        if (customProgressDialog == null){  
            return;  
        }  
          
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);  
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();  
        animationDrawable.start();  
    }  

    /**
     * @author xin.xie
     * @param strTitle
     * @return
     * 设置dialog标题
     */
    public CustomProgressDialog setTitile(String strTitle){  
        return customProgressDialog;  
    }  
      
      
    /**
     * @author xin.xie
     * @param strMessage
     * @return
     * 设置提示字体
     */
    public CustomProgressDialog setMessage(String strMessage){  
        TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);  
          
        if (tvMsg != null){  
            tvMsg.setText(strMessage);  
        }  
          
        return customProgressDialog;  
    }  
}  
