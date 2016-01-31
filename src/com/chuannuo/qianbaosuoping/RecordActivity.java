package com.chuannuo.qianbaosuoping;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.adapter.ExchangeAdapter;
import com.chuannuo.qianbaosuoping.adapter.MyExchangeAdapter;
import com.chuannuo.qianbaosuoping.adapter.MyInvitationAdapter;
import com.chuannuo.qianbaosuoping.adapter.MyPagerAdapter;
import com.chuannuo.qianbaosuoping.adapter.MyTaskAdapter;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.fragment.MeFragment;
import com.chuannuo.qianbaosuoping.model.Exchange;
import com.chuannuo.qianbaosuoping.model.Invitation;
import com.chuannuo.qianbaosuoping.model.Task;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:17:57
 * @Description: 我的个人记录
 */
public class RecordActivity extends BaseActivity {

	private ViewPager mViewPager;
	private List<View> lists = new ArrayList<View>();
	private MyPagerAdapter myPagerAdapter;
	private TextView tv_profile;      //个人资料
    private TextView tv_task;         //任务记录
    private TextView tv_exchange;     //兑换记录
    private TextView tv_invitation;   //邀请记录
    private int item;
    
    /*
     * pager1
     */
    private TextView tv_xb_title;		
    private TextView tv_xb_total;       //积分累计总数
    private TextView tv_xb_exchanged;	//已兑换积分总数
    private TextView tv_rmb_usable;     //可兑换成人民币
    private TextView tv_task_count;		//任务记录次数
    private TextView tv_exchange_total; //兑换记录次数
    private TextView tv_invitation_total;//邀请人数
    private TextView tv_user_account;	//用户账户
    private TextView tv_address;		//用户地址
    private TextView tv_alipay_account; //支付宝账户
    private TextView tv_tenpay_account; //财付通账户
    
    /*
     * pager2
     */
    private MyTaskAdapter taskAdapter;
	private ArrayList<Task> taskList;
	private ListView taskListView;
    
    /*
     * pager3
     */
    private MyExchangeAdapter exchangeAdapter;
	private ArrayList<Exchange> exchangeList;
	private ListView exchangeListView;
	
	/*
     * pager4
     */
	private MyInvitationAdapter invAdapter;
	private ArrayList<Invitation> invList;
	private ListView invListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		initView();
		
		item = getIntent().getIntExtra("ITEM", 0);
		switch(item){
		case 1:
			tv_profile.callOnClick();
			break;
		case 2:
			tv_task.callOnClick();
			break;
		case 3:
			tv_exchange.callOnClick();
			break;
		case 4:
			tv_invitation.callOnClick();
			break;
		default:
			tv_profile.callOnClick();
			break;
		}
		
		
	}

	@SuppressLint("InflateParams")
	public void initView(){
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		tv_profile = (TextView) findViewById (R.id.tv_profile);
		tv_task = (TextView) findViewById (R.id.tv_task);
		tv_exchange = (TextView) findViewById (R.id.tv_exchange);
		tv_invitation = (TextView) findViewById (R.id.tv_invitation);
		
		View pager1 = getLayoutInflater().inflate(R.layout.activity_record_profile, null);
		View pager2 = getLayoutInflater().inflate(R.layout.activity_record_task, null);
		View pager3 = getLayoutInflater().inflate(R.layout.activity_record_exchange, null);
		View pager4 = getLayoutInflater().inflate(R.layout.activity_record_invitation, null);
		
		lists.add(pager1);
		lists.add(pager2);
		lists.add(pager3);
		lists.add(pager4);
		
		
		myPagerAdapter = new MyPagerAdapter(lists);
		mViewPager.setAdapter(myPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					tv_profile.setTextColor(getResources().getColor(R.color.GreenThem));
					tv_task.setTextColor(getResources().getColor(R.color.RedTheme));
					tv_exchange.setTextColor(getResources().getColor(R.color.RedTheme));
					tv_invitation.setTextColor(getResources().getColor(R.color.RedTheme));
					break;
				case 1:
					tv_profile.setTextColor(getResources().getColor(R.color.RedTheme));
					tv_task.setTextColor(getResources().getColor(R.color.GreenThem));
					tv_exchange.setTextColor(getResources().getColor(R.color.RedTheme));
					tv_invitation.setTextColor(getResources().getColor(R.color.RedTheme));
					break;
				case 2:
					tv_profile.setTextColor(getResources().getColor(R.color.RedTheme));
					tv_task.setTextColor(getResources().getColor(R.color.RedTheme));
					tv_exchange.setTextColor(getResources().getColor(R.color.GreenThem));
					tv_invitation.setTextColor(getResources().getColor(R.color.RedTheme));
					break;
				case 3:
					tv_profile.setTextColor(getResources().getColor(R.color.RedTheme));
					tv_task.setTextColor(getResources().getColor(R.color.RedTheme));
					tv_exchange.setTextColor(getResources().getColor(R.color.RedTheme));
					tv_invitation.setTextColor(getResources().getColor(R.color.GreenThem));
					break;

				default:
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		tv_profile.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mViewPager.setCurrentItem(0);
            }
        });
		
		tv_task.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mViewPager.setCurrentItem(1);
            }
        });
        
        tv_exchange.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
            	mViewPager.setCurrentItem(2);
            }
        });
        
        tv_invitation.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
            	mViewPager.setCurrentItem(3);
            }
        });
        

		initPager1(pager1);
		initPager2(pager2);
		initPager3(pager3);
		initPager4(pager4);
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-29 下午3:37:00
	 * @Description: 初始化第一个页面（用户资料页面）
	 * @param @param pager
	 * @return void
	 */
	public void initPager1(View pager){
		 tv_xb_title = (TextView) pager.findViewById(R.id.tv_xb_title);
		 tv_xb_total = (TextView) pager.findViewById(R.id.tv_xb_total);
		 tv_xb_exchanged = (TextView) pager.findViewById(R.id.tv_xb_exchanged);
		 tv_task_count = (TextView) pager.findViewById(R.id.tv_task_count);
		 tv_exchange_total = (TextView) pager.findViewById(R.id.tv_exchange_total);
		 tv_user_account = (TextView) pager.findViewById(R.id.tv_user_account);
		 tv_address = (TextView) pager.findViewById(R.id.tv_address);
		 tv_alipay_account = (TextView) pager.findViewById(R.id.tv_alipay_account);
		 tv_tenpay_account = (TextView) pager.findViewById(R.id.tv_tenpay_account);
		 tv_invitation_total = (TextView) pager.findViewById(R.id.tv_inv_total);
		 tv_rmb_usable = (TextView) pager.findViewById(R.id.tv_rmb_usable);
		 
		 RequestParams param = new RequestParams();
		 param.put("id", pref.getString(Constant.APPID, "0"));
			openProgressDialog("数据加载中...");
			HttpUtil.get(Constant.USER_INFO_URL, param, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					try {
						if(!response.getString("code").equals("0")){
							
							double integraled = (double) (Long.parseLong(response.getString("integraled"))/10000.0);//已兑换积分
							double integral = (double) (Long.parseLong(response.getString("integral"))/10000.0);//可用积分
							double integralTotal = (double) ((Long.parseLong(response.getString("integraled"))+Long.parseLong(response.getString("integral")))/10000.0);//累计已赚
							
							
							DecimalFormat df = new DecimalFormat("0.00");//格式化小数
							df.setRoundingMode(RoundingMode.DOWN);
							String money1 = df.format(integraled/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
							String money2 = df.format(integral/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
							String money3 = df.format(integralTotal/10.0).replaceAll("0+?$", "").replaceAll("[.]$", "");
							
							tv_xb_exchanged.setText(money1+"元");
							tv_xb_total.setText(money3+"元");
							tv_xb_title.setText("总收入："+ money3+"元");
							df.setRoundingMode(RoundingMode.DOWN);
							tv_rmb_usable.setText(money2+"元");
							
							tv_task_count.setText(response.getString("task_cont")+"次");
							tv_exchange_total.setText(response.getString("exchange_cont")+"次");
							tv_invitation_total.setText(response.getString("invitation_cont")+"人");
							tv_user_account.setText(pref.getString(Constant.APPID, null));
							tv_address.setText(response.getString("address"));
							tv_alipay_account.setText(response.getString("alipay_code"));
							tv_tenpay_account.setText(response.getString("tenpay_code"));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						closeProgressDialog();
					}
					super.onSuccess(statusCode, headers, response);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					closeProgressDialog();
					Toast.makeText(RecordActivity.this, getResources().getString(R.string.sys_remind2), Toast.LENGTH_SHORT).show();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-29 下午3:37:23
	 * @Description: 初始化第二个页面（任务记录页面）
	 * @param @param pager
	 * @return void
	 */
	public void initPager2(View pager){
		taskList = new ArrayList<Task>();
		taskListView = (ListView) pager.findViewById(R.id.lv_task_record);
		
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));
		params.put("page", 1);
		openProgressDialog("数据加载中...");
		HttpUtil.get(Constant.TASK_LIST_URL,params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {

				if(response != null && response.length() > 0){
					JSONObject obj = new JSONObject();
					try {
						for (int i = 0; i < response.length(); i++) {
							obj = (JSONObject) response.get(i);
							Task task = new Task();
							task.setIntegral(obj.getString("integral"));
							task.setTaskConten(obj.getString("info"));
							task.setFinishTime(obj.getString("create_date"));
							
							taskList.add(task);
						}
							
							taskAdapter = new MyTaskAdapter(RecordActivity.this, taskList);
							taskListView.setAdapter(taskAdapter);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							closeProgressDialog();
						}
					
				}
				
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				closeProgressDialog();
				Toast.makeText(RecordActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-10-29 下午3:37:45
	 * @Description: 初始化第三个页面（兑换记录页面）
	 * @param @param pager
	 * @return void
	 */
	public void initPager3(View pager){
		exchangeList = new ArrayList<Exchange>();
		exchangeListView = (ListView) pager.findViewById(R.id.lv_exchange_record);
		
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));
		params.put("page", 1);
		openProgressDialog("数据加载中...");
		HttpUtil.get(Constant.EXCHANGE_LIST_URL,params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				
				if(response != null && response.length() > 0){
					JSONObject obj = new JSONObject();
					DecimalFormat df = new DecimalFormat("0.00");//格式化小数
					float integral; 
					try {
						for (int i = 0; i < response.length(); i++) {
							obj = (JSONObject) response.get(i);
							Exchange ex = new Exchange();
							ex.setAccount(obj.getString("account"));
							ex.setExchangeDesc(obj.getString("info"));
							ex.setExchangeTime(obj.getString("create_date"));
							ex.setRemark(obj.getString("remark"));
							ex.setStatus(obj.getInt("status"));
							
							integral = (float) (Long.parseLong(obj.getString("integral"))/10000.0);//积分
							ex.setIntegral(df.format(integral).replaceAll("0+?$", "").replaceAll("[.]$", "")+"万");
							
							exchangeList.add(ex);
						}
							
							exchangeAdapter = new MyExchangeAdapter(RecordActivity.this, exchangeList);
							exchangeListView.setAdapter(exchangeAdapter);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							closeProgressDialog();
						}
					
				}
				
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				closeProgressDialog();
				Toast.makeText(RecordActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	private void initPager4(View pager){
		invList = new ArrayList<Invitation>();
		invListView = (ListView) pager.findViewById(R.id.lv_invitation_record);
		
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));
		params.put("page", 1);
		openProgressDialog("数据加载中...");
		HttpUtil.get(Constant.INVITATION_LIST_URL,params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				int a = response.length();
				if(response != null && a > 0){
					JSONObject obj = new JSONObject();
					try {
						for (int i = 0; i < response.length(); i++) {
							obj = (JSONObject) response.get(i);
							Invitation inv = new Invitation();
							inv.setInvId(obj.getString("app_id"));
							inv.setIntegral(obj.getString("integral"));
							inv.setInvTime(obj.getString("create_date"));
							
							invList.add(inv);
						}
							
							invAdapter = new MyInvitationAdapter(RecordActivity.this, invList);
							invListView.setAdapter(invAdapter);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							closeProgressDialog();
						}
					
				}
				
				super.onSuccess(statusCode, headers, response);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				closeProgressDialog();
				Toast.makeText(RecordActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
}








