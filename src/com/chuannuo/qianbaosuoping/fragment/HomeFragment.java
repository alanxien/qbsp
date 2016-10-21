package com.chuannuo.qianbaosuoping.fragment;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.AccountSettingActivity;
import com.chuannuo.qianbaosuoping.AppDescActivity;
import com.chuannuo.qianbaosuoping.InvitationActivity;
import com.chuannuo.qianbaosuoping.MainActivity;
import com.chuannuo.qianbaosuoping.NewTaskActivity;
import com.chuannuo.qianbaosuoping.PerfectInfoActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.RecordActivity;
import com.chuannuo.qianbaosuoping.SignInActivity;
import com.chuannuo.qianbaosuoping.WaveActivity;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.chuannuo.qianbaosuoping.view.MyImgScroll;



/**
 * @author alan.xie
 * @date 2014-10-14 下午12:18:58
 * @Description: 首页
 */
public class HomeFragment extends Fragment implements  OnClickListener{

	private MyImgScroll myPager;           //图片容器
	private LinearLayout ovalLayout;       //圆点容器
	private List<View> listViews;          //图片组
	
	private TextView tv_sign_in;          //每日签到
	private TextView tv_sign_times;		//签到次数
	private TextView tv_promote_task;     //推广任务
	private TextView tv_balance_account;        //余额
	private TextView tv_all_income;			//累计收益
	private TextView tv_invitation_code;	//邀请码
	
	private Intent intent;
	private SharedPreferences pref;
	private Editor editor;
	private String payCard; //工资卡
	
	
	private CustomDialog dialogNewTask;
	private int recordNum;
	private Boolean flag = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		
		myPager = (MyImgScroll) view.findViewById(R.id.myvp);
		ovalLayout = (LinearLayout) view.findViewById(R.id.vb);
		
		tv_sign_in = (TextView) view.findViewById(R.id.tv_sign_in);
		tv_promote_task = (TextView) view.findViewById(R.id.tv_promote_task);
		tv_balance_account = (TextView) view.findViewById(R.id.tv_balance_account);
		tv_all_income = (TextView) view.findViewById(R.id.tv_all_income);
		tv_invitation_code = (TextView) view.findViewById(R.id.tv_invitation_code);
		tv_sign_times = (TextView) view.findViewById(R.id.tv_sign_times);
		
		tv_sign_in.setOnClickListener(this);
		tv_promote_task.setOnClickListener(this);
		
		intent = new Intent();
		pref = this.getActivity().getSharedPreferences(Constant.STUDENTS_EARN, FragmentActivity.MODE_PRIVATE);
		editor = pref.edit();
		
		//初始化图片
		InitViewPager();
		//开始滚动
		myPager.start(HomeFragment.this.getActivity(), listViews, 4000, ovalLayout,
				R.layout.ad_bottom_item, R.id.ad_item_v,
				R.drawable.dot_focused, R.drawable.dot_normal);
		payCard = pref.getString(Constant.PAY_CARD, "0.00");
		tv_balance_account.setText(payCard);
		tv_invitation_code.setText(pref.getString(Constant.INVIT_CODE, "null"));
		
		/*
		 * 提示新手 任务
		 */
		dialogNewTask = new CustomDialog(HomeFragment.this.getActivity(), R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						switch (view.getId()) {
						case R.id.btn_left:
							dialogNewTask.cancel();
							Intent intent = new Intent();
							intent.setClass(HomeFragment.this.getActivity(),
									NewTaskActivity.class);
							startActivity(intent);
							flag = false;
							break;
						case R.id.btn_right:
							dialogNewTask.cancel();
							flag = false;
							break;
						default:
							break;
						}
					}
				}, 2);
		dialogNewTask.setTitle(getResources().getString(R.string.dg_new_title));
		dialogNewTask.setBtnLeftStr(getResources().getString(R.string.dg_new_confirm));
		dialogNewTask.setBtnRightStr(getResources().getString(R.string.dg_new_cancel));
		dialogNewTask.setContent(getResources().getString(R.string.dg_new_msg));
		dialogNewTask.setCanceledOnTouchOutside(false);
		
		return view;
	}
	
	@Override
	public void onResume() {
		initData();
		super.onResume();
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-13 下午5:59:44
	 * @Description: 初始化图片
	 * @param 
	 * @return void
	 */
	private void InitViewPager() {
		listViews = new ArrayList<View>();
		int[] imageResId = new int[] {R.drawable.one, R.drawable.two,
				R.drawable.three};
		for (int i = 0; i < imageResId.length; i++) {
			ImageView imageView = new ImageView(this.getActivity());
			imageView.setImageResource(imageResId[i]);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setId(imageResId[i]);
			imageView.setOnClickListener(this);
			listViews.add(imageView);
		}
	}
	
	private void initData(){
		
		RequestParams param = new RequestParams();
		 param.put("id", pref.getString(Constant.APPID, "0"));
			HttpUtil.get(Constant.USER_INFO_URL, param, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					try {
						if(!response.getString("code").equals("0")){
							
							double integral = (double) (Long.parseLong(response.getString("integral"))/10000.0);//可用积分
							double income = (double) ((long)(response.getInt("integral")+response.getInt("integraled"))/10000.0);//可用积分
							DecimalFormat df = new DecimalFormat("0.00");//格式化小数
							df.setRoundingMode(RoundingMode.DOWN);
							payCard = integral == 0 ? "0 " : df.format(integral/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
							editor.putString(Constant.PAY_CARD, payCard);
							if(pref.getInt(Constant.LATEST_SCROE, 0) == 0){
								editor.putInt(Constant.LATEST_SCROE, response.getInt("integral"));
							}
							editor.putInt(Constant.SCORE, response.getInt("integral"));
							editor.commit();
							tv_balance_account.setText(payCard);
							String allIncome = income == 0 ? "0 " : df.format(income/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
							tv_all_income.setText("累计收益："+allIncome);
							tv_sign_times.setText(response.getString("sign_cont"));
							recordNum = response.getInt("task_cont");
							
							if (HomeFragment.this != null && HomeFragment.this.getActivity().isTaskRoot() && isShowDialog() && flag && !dialogNewTask.isShowing()) {
								dialogNewTask.show();
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
					}
					super.onSuccess(statusCode, headers, response);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
	}
	

	/**
	 * @author alan.xie
	 * @date 2014-11-14 下午12:15:50
	 * @Description: 判断新手任务是否完成
	 * @param @return
	 * @return Boolean
	 */
	private Boolean isShowDialog() {
		if(recordNum > 2){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_sign_in:
			intent.setClass(HomeFragment.this.getActivity(), SignInActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_promote_task:
			intent.setClass(HomeFragment.this.getActivity(), InvitationActivity.class);
			startActivity(intent);
			break;
		case R.drawable.one:
			intent.setClass(HomeFragment.this.getActivity(), InvitationActivity.class);
			startActivity(intent);
			break;
		case R.drawable.two:
			intent.setClass(HomeFragment.this.getActivity(), PerfectInfoActivity.class);
			startActivity(intent);
			break;
		case R.drawable.three:
			intent.setClass(HomeFragment.this.getActivity(), NewTaskActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}