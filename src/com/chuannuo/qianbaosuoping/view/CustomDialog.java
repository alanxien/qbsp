package com.chuannuo.qianbaosuoping.view;

import com.chuannuo.qianbaosuoping.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog implements OnClickListener{
	
	Context context;
	private Button btn_left;
	private Button btn_right;
	private Button button;
	private TextView tv_msg;
	private TextView tv_title;
	private ImageView iv_image;
	
	private CustomDialogListener listener;
	private String content;
	private String btnStr;
	private String btnLeftStr;
	private String btnRightStr;
	private String title;
	private Bitmap image;
	
	private ImageView picker_minus;
	private ImageView picker_add;
	private TextView tv_cart;
	private EditText et_num;
	
	private LinearLayout ll_share_wx;
	private LinearLayout ll_share_wx_friends;
	private LinearLayout ll_share_qq;
	private LinearLayout ll_qr_code;
	
	private int tag; //1表示确认框(只有确认按钮)，2表示选择框(确认和取消)，3表示分享弹出框，0表示二维码显示框

	public interface CustomDialogListener {
		public void onClick(View view);
	}

	public CustomDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	public CustomDialog(Context context, int theme,CustomDialogListener listener,int tag) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
		this.tag = tag;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		switch (this.tag) {
		case 0: //二维码
			this.setContentView(R.layout.confirm_dialog);
			tv_msg = (TextView) findViewById(R.id.tv_msg);
			tv_title = (TextView) findViewById(R.id.tv_title);
			iv_image = (ImageView) findViewById(R.id.iv_image);
			button = (Button) findViewById(R.id.btn_confirm);
			button.setOnClickListener(this);
			
			iv_image.setImageBitmap(image);
			button.setText(btnStr);
			tv_title.setText(title);
			tv_msg.setVisibility(View.GONE);
			iv_image.setVisibility(View.VISIBLE);
			break;
		case 1://确认框
			this.setContentView(R.layout.confirm_dialog);
			tv_msg = (TextView) findViewById(R.id.tv_msg);
			tv_title = (TextView) findViewById(R.id.tv_title);
			iv_image = (ImageView) findViewById(R.id.iv_image);
			button = (Button) findViewById(R.id.btn_confirm);
			button.setOnClickListener(this);
			
			tv_msg.setText(content);
			button.setText(btnStr);
			tv_title.setText(title);
			tv_msg.setVisibility(View.VISIBLE);
			iv_image.setVisibility(View.GONE);
			break;
		case 2: //确认,取消框
			this.setContentView(R.layout.dialog);
			tv_msg = (TextView) findViewById(R.id.tv_msg);
			tv_title = (TextView) findViewById(R.id.tv_title);
			
			btn_left = (Button) findViewById(R.id.btn_left);
			btn_right = (Button) findViewById(R.id.btn_right);
			btn_left.setOnClickListener(this);
			btn_right.setOnClickListener(this);
			tv_msg.setText(content);
			btn_left.setText(btnLeftStr);
			btn_right.setText(btnRightStr);
			tv_title.setText(title);
			break;
		case 3: //综合分享
			this.setContentView(R.layout.share_dialog);
			
			ll_share_wx = (LinearLayout) findViewById(R.id.ll_share_wx);
			ll_share_wx_friends = (LinearLayout) findViewById(R.id.ll_share_wx_friends);
			ll_share_qq = (LinearLayout) findViewById(R.id.ll_share_qq);
			ll_qr_code = (LinearLayout) findViewById(R.id.ll_qr_code);
			
			ll_share_wx.setOnClickListener(this);
			ll_share_wx_friends.setOnClickListener(this);
			ll_share_qq.setOnClickListener(this);
			ll_qr_code.setOnClickListener(this);
			break;
		case 4: //空间和朋友圈分享
			this.setContentView(R.layout.convert_share_dialog);
			
			ll_share_wx = (LinearLayout) findViewById(R.id.ll_share_wx);
			ll_share_qq = (LinearLayout) findViewById(R.id.ll_share_qq);
			
			ll_share_wx.setOnClickListener(this);
			ll_share_qq.setOnClickListener(this);
			break;
		case 5://清单
			this.setContentView(R.layout.db_dialog_cart);
			
			picker_minus = (ImageView) findViewById(R.id.picker_minus);
			picker_add = (ImageView) findViewById(R.id.picker_add);
			tv_cart = (TextView) findViewById(R.id.tv_picker);
			et_num = (EditText) findViewById(R.id.et_num);
			
			tv_cart.setOnClickListener(this);
			picker_add.setOnClickListener(this);
			picker_minus.setOnClickListener(this);
			et_num.addTextChangedListener(textWatcher);
		case 6://上传图片
			this.setContentView(R.layout.dialog);
			tv_msg = (TextView) findViewById(R.id.tv_msg);
			tv_title = (TextView) findViewById(R.id.tv_title);
			iv_image = (ImageView) findViewById(R.id.iv_upload_img);
			
			btn_left = (Button) findViewById(R.id.btn_left);
			btn_right = (Button) findViewById(R.id.btn_right);
			btn_left.setOnClickListener(this);
			btn_right.setOnClickListener(this);
			
			tv_msg.setVisibility(View.GONE);
			btn_left.setText(btnLeftStr);
			btn_right.setText(btnRightStr);
			tv_title.setText(title);
			iv_image.setImageBitmap(image);
			iv_image.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onClick(View v) {
		listener.onClick(v);
	}
	
	public void setPickNum(int num){
		et_num.setText(num+"");
	}
	
	public int getPickNum(){
		if(et_num.getText().toString().equals("")){
			return 0;
		}else{
			return Integer.parseInt(et_num.getText().toString());
		}
		
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBtnStr() {
		return btnStr;
	}

	public void setBtnStr(String btnStr) {
		this.btnStr = btnStr;
	}

	public String getBtnLeftStr() {
		return btnLeftStr;
	}

	public void setBtnLeftStr(String btnLeftStr) {
		this.btnLeftStr = btnLeftStr;
	}

	public String getBtnRightStr() {
		return btnRightStr;
	}

	public void setBtnRightStr(String btnRightStr) {
		this.btnRightStr = btnRightStr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	private TextWatcher textWatcher = new TextWatcher() {  
        
        @Override    
        public void afterTextChanged(Editable s) {     
        	 int len = s.toString().length(); 
        	 String t = s.toString();
        	 if (len == 1 && t.equals("0")) { 
        		 s.clear(); 
        		 s.append("1");
        	 }
        }   
          
        @Override 
        public void beforeTextChanged(CharSequence s, int start, int count,  
                int after) {  
            // TODO Auto-generated method stub  
        }  
 
         @Override    
        public void onTextChanged(CharSequence s, int start, int before,     
                int count) {     
                              
        }                    
    };

}
