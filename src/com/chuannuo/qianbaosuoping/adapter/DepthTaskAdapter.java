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
import android.os.AsyncTask;
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
import com.chuannuo.qianbaosuoping.androidprocess.AndroidAppProcess;
import com.chuannuo.qianbaosuoping.androidprocess.AndroidAppProcessLoader;
import com.chuannuo.qianbaosuoping.androidprocess.Listener;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.model.AppInfo;
import com.chuannuo.qianbaosuoping.service.DownloadService;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DepthTaskAdapter extends BaseAdapter implements Listener{
	
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
	
	private List<AndroidAppProcess> aliList;
	private int count = 0;
	static int position;
	
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
//		if(infoList.get(position).getIs_photo_task()== 1 && infoList.get(position).getPhoto_status()==0){
//			holder.app_sign.setText("�ϴ���ͼ");
//			holder.tv_is_add_ntegral.setText("����δ��ɣ����ϴ���ͼ");
//		}else{
//			holder.app_sign.setText("ǩ��");
//			holder.tv_is_add_ntegral.setText("����δ��ɣ�ǩ���������");
//		}
//		if(this.infoList.get(position).getIsAddIntegral() == 0){
//			holder.tv_is_add_ntegral.setVisibility(View.VISIBLE);
//		}else{
//			holder.tv_is_add_ntegral.setVisibility(View.GONE);
//		}
		
		if ((this.infoList.get(position).getIsAddIntegral() == 0 && this.infoList.get(position).getScore() > 0)
				|| this.infoList.get(position).isSignTime()
				|| (this.infoList.get(position).getIs_photo_task() == 1 && this.infoList.get(position)
						.getPhoto_status() == 0)) {
			holder.app_sign.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.down_load_install_selector));
			if(this.infoList.get(position).getIs_photo_task() == 1 && this.infoList.get(position)
					.getPhoto_status() == 0){
				holder.tv_is_add_ntegral.setText("�ϴ�ͼƬ���������");
				holder.app_sign.setText("�ϴ�");
			}else{
				holder.tv_is_add_ntegral.setText("����ǩ�����������");
				holder.app_sign.setText("ǩ��");
			}
			
		} else {
			holder.app_sign.setBackgroundColor(context.getResources().getColor(R.color.greythem));
			holder.tv_is_add_ntegral.setText("��û��ǩ��ʱ��");
			holder.app_sign.setText("ǩ��");
		}
		
		holder.app_sign.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(checkPackage(infoList.get(position).getPackage_name())){//��� Ӧ���Ѿ���װ
					if(infoList.get(position).getIs_photo_task()== 1 && infoList.get(position).getPhoto_status()==0){
						Intent intent = new Intent(context,DownLoadAppActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(Constant.ITEM, infoList.get(position));
						intent.putExtras(bundle);
						context.startActivity(intent);
					}else{
						DepthTaskAdapter.position = position;
						doStartApplicationWithPackageName(infoList.get(position).getPackage_name());
						
						Toast.makeText(context, "���������Ӽ����ǩ����",Toast.LENGTH_SHORT ).show();
						timer = new Timer();
						
						timer.schedule(new TimerTask() {

							@Override
							public void run() {
								new AndroidAppProcessLoader(context,
										DepthTaskAdapter.this)
										.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
						}, 10000, 30000);
						
//						timer.schedule(new TimerTask() {
//							
//							@Override
//							public void run() {
//								if(isTopActivity(infoList.get(position).getPackage_name())){
//									if(moreThanTimes(System.currentTimeMillis(),2)){
//										Log.i(TAG, "---���г���2���ӣ���ʼǩ��������---");
//										timer.cancel();
//										Message msg = mHandler.obtainMessage(1,infoList.get(position));
//										msg.what = infoList.get(position).getInstall_id();
//										msg.arg1 = infoList.get(position).getSign_rules();
//										msg.arg2 = infoList.get(position).getIsAddIntegral();
//										mHandler.sendMessage(msg);
//									}else{
//										Log.i(TAG, "---����ǩ��������---");
//										editor.putInt(Constant.APP_SIGN_IS_SUCCESS, 1); //ǩ����
//									}
//								}else{
//									Log.i(TAG, "---�˳�ǩ��---");
//									timer.cancel();
//								}
//								editor.commit();
//							}
//						}, 3000,60*1000);
					}
					
				}else{
					//Ӧ�ñ�ж�أ����°�װ
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
		public TextView app_sign_rule;  //ǩ������(���������ǩ����ǩ�������������)
		public TextView app_sign;       //ǩ����ť
		public TextView app_sign_times; //ǩ������
		public TextView tv_is_add_ntegral;//��ʾ�Ƿ��Ѿ���ȡ���ػ���
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(final Message msg) {
			RequestParams params = new RequestParams();
			params.put("app_id", pref.getString(Constant.APPID, "0"));
			params.put("ad_install_id", msg.what);
			
			final AppInfo res = (AppInfo) msg.obj;
			/*
			 * ǩ��
			 */
			HttpUtil.get(Constant.REPEAT_SIGN_URL, params, new JsonHttpResponseHandler(){
				public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
					try {
						if(response.getInt("code") == 1){
							Toast.makeText(context, "��ϲ����ǩ���ɹ���",Toast.LENGTH_SHORT ).show();
							editor.putInt(Constant.APP_SIGN_IS_SUCCESS, 2); //ǩ���ɹ�
						}else{
							Toast.makeText(context, response.getString("info"),Toast.LENGTH_SHORT ).show();
							editor.putInt(Constant.APP_SIGN_IS_SUCCESS, -1); //ǩ��ʧ��
						}
						editor.commit();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				};
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
					Toast.makeText(context, "��Ǹ��ǩ��ʧ�ܣ�",Toast.LENGTH_SHORT ).show();
					editor.putInt(Constant.APP_SIGN_IS_SUCCESS, -1); //ǩ��ʧ��
					editor.commit();
				};
			});
			
			/*
			 * ���ӻ��֣���ֹ�û���һ������Ӧ��û�������㹻��ʱ�䣬��û�мӻ��֣�
			 */
			if(res.getIsAddIntegral() == 0){
				HttpUtil.post(Constant.CONFIRM_INSTALL_INTEGRAL, params,
						new JsonHttpResponseHandler() {

							public void onSuccess(int statusCode,
									Header[] headers, JSONObject response) {
								try {
									if (response.getString("code").equals("1")) {
										Log.i(TAG, "���ӻ��� ---- success");
										/*
										 * ���ӻ��ֳɹ�
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
										editor.putInt(Constant.APP_SIGN_IS_SUCCESS, 2); //ǩ���ɹ�
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
	 * @date 2014-12-18 ����3:27:00
	 * @Description: ͨ����������Ӧ�ó���
	 * @param @param packagename
	 * @return void
	 */
	private void doStartApplicationWithPackageName(String packagename) {  
		  
	    // ͨ��������ȡ��APP��ϸ��Ϣ������Activities��services��versioncode��name�ȵ�  
	    PackageInfo packageinfo = null;  
	    try {  
	        packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);  
	    } catch (NameNotFoundException e) {  
	        e.printStackTrace();  
	    }  
	    if (packageinfo == null) {  
	        return;  
	    }  
	  
	    // ����һ�����ΪCATEGORY_LAUNCHER�ĸð�����Intent  
	    Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);  
	    resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
	    resolveIntent.setPackage(packageinfo.packageName);  
	  
	    // ͨ��getPackageManager()��queryIntentActivities��������  
	    List<ResolveInfo> resolveinfoList = context.getPackageManager()  
	            .queryIntentActivities(resolveIntent, 0);  
	  
	    ResolveInfo resolveinfo = resolveinfoList.iterator().next();  
	    if (resolveinfo != null) {  
	        // packagename = ����packname  
	        String packageName = resolveinfo.activityInfo.packageName;  
	        // �����������Ҫ�ҵĸ�APP��LAUNCHER��Activity[��֯��ʽ��packagename.mainActivityname]  
	        String className = resolveinfo.activityInfo.name;  
	        // LAUNCHER Intent  
	        Intent intent = new Intent(Intent.ACTION_MAIN);  
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);  
	  
	        // ����ComponentName����1:packagename����2:MainActivity·��  
	        ComponentName cn = new ComponentName(packageName, className);  
	  
	        intent.setComponent(cn);  
	        context.startActivity(intent);  
	    }  
	}
	
	/**
	 * @author alan.xie
	 * @date 2014-12-18 ����5:42:49
	 * @Description: ���Ӧ�ó����Ƿ���ǰ̨����
	 * @param @param packageName
	 * @param @return
	 * @return boolean
	 */
	private boolean isTopActivity(String packageName){  
  
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningTaskInfo>  tasksInfo = activityManager.getRunningTasks(1);    
        if(tasksInfo.size() > 0){    
            Log.i(TAG, "---------------����-----------"+packageName);
            //Ӧ�ó���λ�ڶ�ջ�Ķ���    
            if(packageName.equals(tasksInfo.get(0).topActivity.getPackageName())){   
            	Log.i(TAG, packageName);
                return true;    
            }    
        }    
        return false;  
    } 
	
	/**
	 * @author alan.xie
	 * @date 2014-10-29 ����9:40:47
	 * @Description: ����ʱ�����Ƿ����days
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
	 * @date 2014-12-17 ����2:45:48
	 * @Description: ���ð�������Ӧ��Ӧ���Ƿ����
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

	/* (non-Javadoc)
	 * @see com.chuannuo.qianbaosuoping.androidprocess.Listener#onComplete(java.util.List)
	 */
	@Override
	public void onComplete(List<AndroidAppProcess> processes) {
		if (aliList == null) {
			aliList = new ArrayList<AndroidAppProcess>();
		} else {
			aliList.clear();
		}
		int size = processes.size();
		AndroidAppProcess aProcess;
		count++;
		for (int i = 0; i < size; i++) {
			aProcess = processes.get(i);
			if (aProcess.getPackageName() != null
					&& aProcess.getPackageName().equals(
							infoList.get(position).getPackage_name().trim())) {
				aliList.add(aProcess);
			}
		}

		int l = aliList.size();
		if (l == 0) {
			// ����ʧ��
		} else {
			Boolean isFg = false;
			for (int i = 0; i < l; i++) {
				if (aliList.get(i).foreground) {
					isFg = true;
				}
			}

			if (isFg) {
				if (count == 3) {
					Toast.makeText(context, "���Ѿ�������1���ӣ�����2���ӣ����ɻ�û��֣�",
							Toast.LENGTH_SHORT).show();
				} else if (count == 5) {
					// ����ɹ�
					timer.cancel();
					Message msg = mHandler.obtainMessage(1,infoList.get(position));
					msg.what = infoList.get(position).getInstall_id();
					msg.arg1 = infoList.get(position).getSign_rules();
					msg.arg2 = infoList.get(position).getIsAddIntegral();
					mHandler.sendMessage(msg);
				}
			} else {
				// ����ʧ��
			}
		}
		
		if(count >= 5){
			timer.cancel();
			count=0;
		}
	}
	
}












