package com.chuannuo.qianbaosuoping.view;

import com.chuannuo.qianbaosuoping.LockScreenActivity;
import com.chuannuo.qianbaosuoping.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


/**
 * @author alan.xie
 * @date 2014-12-1 下午12:01:54
 * @Description: 夺宝
 */
public class SliderRelativeLayout extends RelativeLayout{
	private final static String TAG = "SliderRelativeLayout";
	
	private Context context;
	private Bitmap dragBitmap = null; 			//拖拽图片
	private ImageView heartView = null; 		//主要是获取相对布�?���?
	private ImageView leftRingView = null;
	private ImageView rightRingView = null;
	private LinearLayout linearLayoutL = null;
	private LinearLayout linearLayoutR = null;
	private Handler handler = null; 			//信息传�?
	private int locationX = 0; 					//bitmap初始绘图位置，足够大，可以认为看不见
	private static int BACK_DURATION = 10 ;   	//回滚动画时间间隔40ms
	private static float VE_HORIZONTAL = 0.9f ; //水平方向前进速率 0.1dip/ms
	
	private boolean flag = false; //记录是否已经按下�?
	
	public SliderRelativeLayout(Context context) {
		super(context); 
		SliderRelativeLayout.this.context = context;
		intiDragBitmap();
	}
	
	public SliderRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		SliderRelativeLayout.this.context = context;
		intiDragBitmap();
	}

	public SliderRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		SliderRelativeLayout.this.context = context;
		intiDragBitmap();
	}

	
	private void intiDragBitmap() {
		setWillNotDraw(false);
		if(dragBitmap == null){
			dragBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_slide_circle_n);
		}
	}
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		heartView = (ImageView) findViewById(R.id.loveView);
		leftRingView = (ImageView) findViewById(R.id.leftRing);
		rightRingView = (ImageView) findViewById(R.id.rightRing);
		linearLayoutL = (LinearLayout) findViewById(R.id.linearLayoutL);
		linearLayoutR = (LinearLayout) findViewById(R.id.linearLayoutR);
	}

	/**
	 * 对拖拽图片不同的点击事件处理
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int X = (int) event.getX();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: 
			dragBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.lock_touch);
			heartView.setImageBitmap(dragBitmap);
			linearLayoutL.setBackgroundResource(R.drawable.left_bg_default);
			linearLayoutR.setBackgroundResource(R.drawable.left_bg_default);
			locationX = (int) event.getX();
			Log.i(TAG, "是否点击到位=" + isActionDown(event));
			return isActionDown(event);//判断是否点击了滑动区�?
			
		case MotionEvent.ACTION_MOVE: //保存x轴方向，绘制图画
			locationX = X;
			invalidate(); //重新绘图
			return true;
			
		case MotionEvent.ACTION_UP: //判断是否解锁成功
			dragBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_slide_circle_n);
			heartView.setImageBitmap(dragBitmap);
			linearLayoutL.setBackgroundColor(getResources().getColor(R.color.transparent));
			linearLayoutR.setBackgroundResource(getResources().getColor(R.color.transparent));
			if(!unLockLeft() && !unLockRight()){ //没有解锁成功,动画应该回滚
				flag = false;
				handleActionUpEvent(event);      //动画回滚
			}
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-1 上午11:59:59
	 * @Description: 回滚动画
	 * @param @param event
	 * @return void
	 */
	private void handleActionUpEvent(MotionEvent event) {
		int x = (int) event.getX();
		if( x < getScreenWidth()/2){
			locationX = getScreenWidth()/2 - x; 
		}
		if(x > getScreenWidth()/2){
			locationX = x - getScreenWidth()/2;
		}
		
		if(locationX >= 0){
			handler.postDelayed(ImageBack, BACK_DURATION); //回滚
		}
	}

	/**
	 * 未解锁时，图片回�?
	 */
	private Runnable ImageBack = new Runnable() {
		@Override
		public void run() {
			locationX = locationX - (int) (VE_HORIZONTAL*BACK_DURATION);
			if(locationX > 0){
				handler.postDelayed(ImageBack, BACK_DURATION); //回滚
				invalidate();
			}
		}
	};

	/**
	 * @author alan.xie
	 * @date 2015-3-13 下午4:39:03
	 * @Description: 判断是否点击到了滑动区域
	 * @param @param event
	 * @param @return
	 * @return boolean
	 */
	private boolean isActionDown(MotionEvent event) {
		Rect rect = new Rect();
		heartView.getHitRect(rect);
		boolean isIn = rect.contains((int)event.getX(), (int)event.getY());
		if(isIn){
			//heartView.setVisibility(View.GONE);
			flag = true;
			return true;
		}
		return false;
	}
	
	/**
	 * 绘制拖动时的图片
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		invalidateDragImg(canvas);
	}
	
	
	/**
	 * @author alan.xie
	 * @date 2014-12-1 上午11:59:38
	 * @Description: 图片随手势移�?
	 * @param @param canvas
	 * @return void
	 */
	private void invalidateDragImg(Canvas canvas) { 
		int drawX = locationX - heartView.getWidth()/2;
		int drawY = heartView.getTop();
		Log.i(TAG, "重绘=" +drawX);
		if(!flag){ 
			heartView.setVisibility(View.VISIBLE);
			return;
		}else {
			if(locationX < linearLayoutL.getWidth()-10){ //滑动到最左边，heartView消失
				linearLayoutL.setBackgroundResource(R.drawable.left_bg_select);
				return;
			}
			if(locationX > (getScreenWidth() - linearLayoutR.getWidth()-20)){ //滑到�?���?heartView 消失
				linearLayoutR.setBackgroundResource(R.drawable.left_bg_select);
				return;
			}
			heartView.setVisibility(View.GONE);
			if(drawX > linearLayoutL.getWidth()/2){
				canvas.drawBitmap(dragBitmap,drawX,drawY,null);
			}
			
			
		}	
	}
	
	/**
	 * @author alan.xie
	 * @date 2015-3-13 下午4:38:35
	 * @Description: 向左解锁
	 * @param @return
	 * @return boolean
	 */
	private boolean unLockLeft(){
		if(locationX < linearLayoutL.getWidth()){
			heartView.setVisibility(View.GONE);
			//leftRingView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_lock_screen_share_s));
			handler.obtainMessage(LockScreenActivity.MSG_LOCK_SUCCESS_L).sendToTarget();
			Log.i(TAG, "-------------------left----------");
			return true;
		}
		return false;
	}

	/**
	 * @author alan.xie
	 * @date 2015-3-13 下午4:38:17
	 * @Description: 向右解锁
	 * @param @return
	 * @return boolean
	 */
	private boolean unLockRight(){
		if(locationX > (getScreenWidth() - linearLayoutR.getWidth()-20)){
			heartView.setVisibility(View.GONE);
			//rightRingView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_action_unlock_s));
			handler.obtainMessage(LockScreenActivity.MSG_LOCK_SUCCESS_R).sendToTarget();
			Log.i(TAG, "-------------------right----------");
			return true;
		}
		return false;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-1 上午11:56:14
	 * @Description: 获取屏幕宽度
	 * @param @return
	 * @return int
	 */
	private int getScreenWidth(){
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = manager.getDefaultDisplay().getWidth();
		return width;
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-1 上午11:56:35
	 * @Description: 与主activity通信
	 * @param @param handler
	 * @return void
	 */
	public void setMainHandler(Handler handler){
		this.handler = handler;
	}

}
