package com.chuannuo.qianbaosuoping.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.DownLoadAppActivity;
import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.service.DownloadService;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DepthTaskAdapter extends BaseAdapter {
	
	public static String TAG = "DepthTaskAdapter";

	ArrayList<AppInfo> infoList;
	Context context;
	LayoutInflater la;
	String appName;
	Timer timer;
	SharedPreferences pref;
	Editor editor;
	CustomDialog mDialog;
	AppInfo appInfo;
	
	public DepthTaskAdapter(final Context context,ArrayList<AppInfo> list){
		this.context = context;
		this.infoList = list;
		if(null == pref){
			pref = context.getSharedPreferences(Constant.STUDENTS_EARN, Context.MODE_PRIVATE);
		}
		if(null == editor){
			editor = pref.edit();
		}
		
		mDialog = new CustomDialog(context, R.style.CustomDialog, new CustomDialog.CustomDialogListener() {
			
			@Override
			public void onClick(View view) {
				mDialog.cancel();
				Intent intent = new Intent(context,DownloadService.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(Constant.ITEM, appInfo);
				bundle.putBoolean("isRepeatDown", true);
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startService(intent);
			}
		}, 1);
        mDialog.setTitle(context.getResources().getString(R.string.dg_remind_title));
        mDialog.setBtnStr(context.getResources().getString(R.string.dg_downLoadNow));
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.infoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){
			la = LayoutInflater.from(context);
			convertView = la.inflate(R.layout.depth_task_item, null);
			
			holder = new ViewHolder();
			holder.app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
			holder.app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
			holder.app_sign_rule = (TextView) convertView.findViewById(R.id.tv_sign_rule);
			holder.app_sign = (TextView) convertView.findViewById(R.id.tv_sign);
			holder.app_sign_times = (TextView) convertView.findViewById(R.id.tv_sign_times);
			holder.tv_is_add_ntegral = (TextView) convertView.findViewById(R.id.tv_is_add_ntegral);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.app_name.setText(this.infoList.get(position).getTitle());
		ImageLoader.getInstance().displayImage(this.infoList.get(position).getIcon(),holder.app_icon);
		holder.app_sign_rule.setText(context.getResources().getString(R.string.sign_rules,this.infoList.get(position).getSign_rules(),this.infoList.get(position).getNeedSign_times()));
		holder.app_sign_times.setText(this.infoList.get(position).getSign_times()+"");
		
		if(this.infoList.get(position).getIsAddIntegral() == 0){
			holder.tv_is_add_ntegral.setVisibility(View.VISIBLE);
		}else{
			holder.tv_is_add_ntegral.setVisibility(View.GONE);
		}
		
		holder.app_sign.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(checkPackage(infoList.get(position).getPackage_name())){//如果 应用已经安装
					if(infoList.get(position).getIs_photo_task()== 1 && (infoList.get(position).getPhoto_status()==0||infoList.get(position).getPhoto_status()==3)){
						Intent intent = new Intent(context,DownLoadAppActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(Constant.ITEM, appInfo);
						context.startActivity(intent);
					}else{
						doStartApplicationWithPackageName(infoList.get(position).getPackage_name());
						editor.putLong(Constant.APP_RUNNING_TIME, System.currentTimeMillis());
						editor.commit();
						
						Toast.makeText(context, "试玩两分钟即完成签到！",Toast.LENGTH_SHORT ).show();
						timer = new Timer();
						
						timer.schedule(new TimerTask() {
							
							@Override
							public void run() {
								if(isTopActivity(infoList.get(position).getPackage_name())){
									if(moreThanTimes(System.currentTimeMillis(),2)){
										Log.i(TAG, "---运行超过2分钟，开始签到。。。---");
										timer.cancel();
										Message msg = mHandler.obtainMessage(1,infoList.get(position));
										msg.what = infoList.get(position).getInstall_id();
										msg.arg1 = infoList.get(position).getSign_rules();
										msg.arg2 = infoList.get(position).getIsAddIntegral();
										mHandler.sendMessage(msg);
									}else{
										Log.i(TAG, "---正在签到。。。---");
										editor.putInt(Constant.APP_SIGN_IS_SUCCESS, 1); //签到中
									}
								}else{
									Log.i(TAG, "---退出签到---");
									timer.cancel();
								}
								editor.commit();
							}
						}, 3000,60*1000);
					}
					
				}else{
					//应用被卸载，重新安装
					appInfo = infoList.get(position);
					mDialog.setContent(context.getResources().getString(R.string.dg_to_download,appInfo.getTitle()));
					mDialog.show();
				}
				
			}
		});
		
		return convertView;
	}

	class ViewHolder{ 
		public ImageView app_icon;
		public TextView app_name;
		public TextView app_sign_rule;  //签到规则(隔几天才能签到，签到几次完成任务)
		public TextView app_sign;       //签到按钮
		public TextView app_sign_times; //签到次数
		public TextView tv_is_add_ntegral;//提示是否已经获取下载积分
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(final Message msg) {
			RequestParams params = new RequestParams();
			params.put("app_id", pref.getString(Constant.APPID, "0"));
			params.put("ad_install_id", msg.what);
			
			final AppInfo res = (AppInfo) msg.obj;
			/*
			 * 签到
			 */
			HttpUtil.get(Constant.REPEAT_SIGN_URL, params, new JsonHttpResponseHandler(){
				public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
					try {
						if(response.getInt("code") == 1){
							Toast.makeText(context, "恭喜您，签到成功！",Toast.LENGTH_SHORT ).show();
							editor.putInt(Constant.APP_SIGN_IS_SUCCESS, 2); //签到成功
						}else{
							Toast.makeText(context, response.getString("info"),Toast.LENGTH_SHORT ).show();
							editor.putInt(Constant.APP_SIGN_IS_SUCCESS, -1); //签到失败
						}
						editor.commit();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				};
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
					Toast.makeText(context, "抱歉，签到失败！",Toast.LENGTH_SHORT ).show();
					editor.putInt(Constant.APP_SIGN_IS_SUCCESS, -1); //签到失败
					editor.commit();
				};
			});
			
			/*
			 * 增加积分（防止用户第一次下载应用没有体验足够长时间，而没有加积分）
			 */
			if(res.getIsAddIntegral() == 0){
				HttpUtil.post(Constant.CONFIRM_INSTALL_INTEGRAL, params,
						new JsonHttpResponseHandler() {

							public void onSuccess(int statusCode,
									Header[] headers, JSONObject response) {
								try {
									if (response.getString("code").equals("1")) {
										Log.i(TAG, "增加积分 ---- success");
										/*
										 * 增加积分成功
										 */
										Toast.makeText(
												context,
												context.getResources()
														.getString(
																R.string.add_score_success)
														+ "+"
														+ res.getScore(),
												Toast.LENGTH_LONG).show();
										editor.putInt(Constant.SCORE,
												pref.getInt(Constant.SCORE, 0)
														+ res.getScore());
										editor.commit();
									} else {
										Toast.makeText(context,
												response.getString("info"),
												Toast.LENGTH_SHORT).show();
									}
								} catch (NotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							};

							public void onFailure(int statusCode,
									Header[] headers, Throwable throwable,
									JSONObject errorResponse) {

							};
						});
			}
			
		};
	};
	
	/**
	 * @author alan.xie
	 * @date 2014-12-18 下午3:27:00
	 * @Description: 通过包名启动应用程序
	 * @param @param packagename
	 * @return void
	 */
	private void doStartApplicationWithPackageName(String packagename) {  
		  
	    // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等  
	    PackageInfo packageinfo = null;  
	    try {  
	        packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);  
	    } catch (NameNotFoundException e) {  
	        e.printStackTrace();  
	    }  
	    if (packageinfo == null) {  
	        return;  
	    }  
	  
	    // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent  
	    Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);  
	    resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
	    resolveIntent.setPackage(packageinfo.packageName);  
	  
	    // 通过getPackageManager()的queryIntentActivities方法遍历  
	    List<ResolveInfo> resolveinfoList = context.getPackageManager()  
	            .queryIntentActivities(resolveIntent, 0);  
	  
	    ResolveInfo resolveinfo = resolveinfoList.iterator().next();  
	    if (resolveinfo != null) {  
	        // packagename = 参数packname  
	        String packageName = resolveinfo.activityInfo.packageName;  
	        // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]  
	        String className = resolveinfo.activityInfo.name;  
	        // LAUNCHER Intent  
	        Intent intent = new Intent(Intent.ACTION_MAIN);  
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);  
	  
	        // 设置ComponentName参数1:packagename参数2:MainActivity路径  
	        ComponentName cn = new ComponentName(packageName, className);  
	  
	        intent.setComponent(cn);  
	        context.startActivity(intent);  
	    }  
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-18 下午5:42:49
	 * @Description: 检测应用程序是否在前台运行
	 * @param @param packageName
	 * @param @return
	 * @return boolean
	 */
	private boolean isTopActivity(String packageName){  
  
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningTaskInfo>  tasksInfo = activityManager.getRunningTasks(1);    
        if(tasksInfo.size() > 0){    
            Log.i(TAG, "---------------包名-----------"+packageName);
            //应用程序位于堆栈的顶层    
            if(packageName.equals(tasksInfo.get(0).topActivity.getPackageName())){   
            	Log.i(TAG, packageName);
                return true;    
            }    
        }    
        return false;  
    } 
	
	/**
	 * @author alan.xie
	 * @date 2014-10-29 上午9:40:47
	 * @Description: 计算时间间隔是否大于days
	 * @param @return
	 * @return Boolean
	 */
	public Boolean moreThanTimes(long currTimes,int minute){
		long times = pref.getLong(Constant.APP_RUNNING_TIME, 0);
		if(currTimes-times > minute*60*1000){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-17 下午2:45:48
	 * @Description: 检测该包名所对应的应用是否存在
	 * @param @param packageName
	 * @param @return
	 * @return boolean
	 */
	public boolean checkPackage(String packageName)   
    {    
        if (packageName == null || "".equals(packageName)) {
        	return false;    
        }
        try   
        {    
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;    
        }   
        catch (NameNotFoundException e)   
        {   
            return false;    
        }    
    }
	
}












