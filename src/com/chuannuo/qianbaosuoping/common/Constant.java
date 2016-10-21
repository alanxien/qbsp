package com.chuannuo.qianbaosuoping.common;

/**
 * @author alan.xie
 * @date 2014-10-13 下午2:41:55
 * @Description: 常量接口
 */
public interface Constant {
	
	String PACKAGE_NAME = "com.chuannuo.qianbaosuoping";
	String DOWNLOAD_DIR = "qbsp/download/";
	String IMG_DIR = "qbsp/images/";
	
	/*
	 * sharedPreferenced 常量
	 */
	String STUDENTS_EARN = "studentsearn"; //文件名
	String IS_FIRST_IN = "isFirstIn";      //是否第一次进入
	String IS_QUICK_START="isQuickStart";  //是否是快速注册

	String APPID = "appid"; //用户账号
	String CODE = "code";   //校验码
	String PHONE = "phone"; //用户电话
	String PASSWORD = "password"; //用户密码
	String ADDRESS = "address";
	String CFT = "cft";
	String ZFB = "zfb";
    String WAVE_TIMES = "waveTimes";   //摇一摇时间
    String SIGN_TIMES = "signTimes";   //签到时间
    String OFFLINE_TIME = "offLineTime";      //每十分钟更新一次，
    String APP_RUNNING_TIME = "appRunningTime";//app运行时间
    String APP_SIGN_IS_SUCCESS = "appSignIsSuccess"; //app签到是否成功，0没开始签到，1未完成的签到，2签到成功
    
    String SHARE_TIME = "shareTime"; //滑屏分享时间
    String DOWNLOAD_TIME = "downTime";   //滑屏下载时间
    String SLIDER_UNLOCK_TIME = "silderUnlock";//滑动解锁时间
    String SLIDED = "isSlided"; //是否滑屏
    String RECENT_SHARE_TIME = "recentShareTime"; //最近一次分享时间
    String SHARE_SIZE = "shareSize";//判断分享任务是否还有
    
    String R_ACCOUNT = "rememberAccount";//记住账号
    String R_PASSWORD = "rememberPassword";//记住密码
    String IS_LOCK_SCREEN = "isLockScreen";//是否开启夺宝
    
    String DL_TOTAL_SCORE = "dlTotalScore"; //记录点乐总积分
    String DM_TOTAL_SCORE = "dmTotalScore"; //记录多盟总积分
    String YM_TOTAL_SCORE = "ymTotalScore"; //记录有米总积分
    
    
    String TASK_SIGN = "taskSign";//每日签到
    String TASK_KNOW_APP = "taskKnowApp";//了解APP
    String TASK_WAVE = "taskWave"; //每日摇一摇
    String TASK_SHARE = "taskShare";//分享
    String TASK_EXPERIENCE = "taskExperience";//体验一个应用任务
    String TASK_USER_INFO  = "taskUserInfo";//完善用户信息
  
    String ITEM = "item";
    String SLIDER_NUM = "sliderNum";     //滑动次数
    String SHARE_NUM = "shareNum";       //分享次数
    String DOWNLOAD_NUM = "downLoadNum"; //下载次数
    String INVIT_CODE = "invitCode";    //邀请码
    String DOWNLOAD_APP_TIME = "downLoadAppTime";//用户下载app的当前时间
    String IS_FINISHED = "isFinished";  //应用是否已经结束 
    String PAY_CARD = "payCard";//工资卡
    String LATEST_SCROE = "latestScore";//上次摇一摇时候的积分，判断是否可以进行下次摇一摇抽奖，
    String SCORE = "score";       //当前积分
    
    String DOWNLOAD_TIMES = "downLoadTimes"; //每天能下载多少次钱包夺宝app
    String DOWN_TIME = "downLoadTime";//记录下载时间，超过一天可重新下载
    
    /*
	 * 是否点击了Home键
	 */
	public static final String IS_KEY_HOME = "isKeyHome";
    
	
	/*
	 * 服务器接口
	 */
    String ROOT_URL = "http://112.74.88.252";	//http://www.jiequbao.com/							//服务器
	String BASE_URL = "http://112.74.88.252/index.php?r=zhuanMi";
	String DOWN_URL = "http://qbsp.jiequbao.com";//下载URL
	//String ROOT_URL = "http://192.168.1.57/zhuanmi2/www";						//本地
	//String BASE_URL = "http://192.168.1.57/zhuanmi2/www/index.php?r=zhuanMi";
	String SCREEN_SHOT_RUL = "http://m.baidu.com/s?word=";
	
	String LOGIN_URL = BASE_URL+"/login";     					//登陆（绑定手机号）
	String USER_INFO_URL = BASE_URL+"/userInfo";				//用户详细信息
	String NEW_EXCHANGE_URL = BASE_URL+"/convertList";			//最新兑换记录
	String SIGN_IN_URL = BASE_URL + "/sign";                    //签到
	String MODIFY_USER_URL = BASE_URL + "/modifyUser";			//用户资料修改
	String ADD_INTEGRAL_URL  = BASE_URL + "/addIntegral";		//增加积分
	String MODIFY_PASSWORD_URL = BASE_URL + "/modifyPassword";  //修改密码
	//String EXCHANGE_QB_URL = BASE_URL + "/exchangeQQ";          //兑换Q币
	String EXCHANGE_PHONE_URL = BASE_URL + "/exchangeMobile";   //手机充值
	String EXCHANGE_ZFB_URL = BASE_URL + "/exchangeAlipay";     //支付宝充值
	String EXCHANGE_CFT_URL = BASE_URL + "/exchangeTenpay";     //财付通充值
	String EXCHANGE_LIST_URL = BASE_URL + "/convertListUser";  //兑换记录
	String INVITATION_LIST_URL = BASE_URL + "/getInvitationUser";//邀请记录
	String TASK_LIST_URL = BASE_URL + "/userTaskList";          //任务完成列表
	String GET_CODE_URL = BASE_URL + "/getCode";                //获取验证码
	String VALIDATE_CODE_URL = BASE_URL + "/validateCode";      //绑定手机号
	String RECOVER_PASSWORD_URL = BASE_URL + "/recoverPassword";//找回密码
	String ADINSTALL_URL = BASE_URL + "/adInstall";             //过滤已经下载安装过的app
	String REPEAT_SIGN_URL = BASE_URL + "/reportSign";          //深度任务 签到
	String DEPTH_TASK_LIST_URL = BASE_URL + "/unfinishedSignList"; //深度任务list
	
	String SHARE_URL = BASE_URL + "/index&invitation_code=";      //分享
	//String DOWNLOAD_URL = BASE_URL + "/getResourceList";		//下载
	String REPORT_URL = BASE_URL + "/reportPackageNames";        //上报应用安装
	String LOGO_URL = ROOT_URL + "/images/ic_launcher.png";    //logo_url
	String SHARE_TASK_URL = BASE_URL + "/getPostList";			//分享任务
	String REPORE_SHARE_URL = BASE_URL+"/reportShare";			//上报分享
	String CONVERT_SHARE = BASE_URL + "/getConvertShare";		//兑换分享
	String REPORT_CONVERT_SHARE= BASE_URL + "/reportConvertShare";//上报兑换分享
	String CONFIRM_INSTALL_INTEGRAL = BASE_URL + "/confirmInstallIntegral";//确认应用安装 添加积分
	String REPORT_TASK = BASE_URL + "/reportTask";					//上报任务（分享任务）
	String PUSH_MSG = BASE_URL + "/pushMsg";						//轮询消息
	String TASK_RETURN = BASE_URL + "/taskReturn";					//邀请任务详细记录
	String GET_PHONE_NUM_URL = BASE_URL +"/oldLogin";				//根据emei获取手机号
	String QUICK_REGISTRATION_URL = BASE_URL + "/signup";//一键注册
	String QUICK_LOGIN_URL = BASE_URL + "/getUserByImei";//吴手机号登陆
	String BIND_MOBILE_URL = BASE_URL + "/bindMobile";   //绑定手机
	String GET_RESOURCE_LIST_SDK = BASE_URL+"/getResourceListSDK";//游戏SDK列表
	
	/*
	 * 上传截图接口
	 */
	String DOWNLOAD_URL = BASE_URL+"/getResourceListHtml"; //截图任务接口
	String EXCHANGE_INDIANA = BASE_URL +"/exchangeIndiana";//兑换夺宝币
	String UPLOADS_PHOTO = BASE_URL +"/uploadsPhotoHtmls";//上传图片
	String GET_USER_AD_ALERT = BASE_URL+"/getUserAdAlert";//上传图片成功，积分提示
	String MODIFY_USER_ADALERT = BASE_URL + "/modifyUserAdAlert";//截图修改上报，
	String ADDCUSTOM = BASE_URL +"/addCustom";
	
	int PAGER0 = 0;//游戏任务
	int PAGER1 = 1;//推荐任务
	int PAGER2 = 2;//分享任务
	int PAGER3 = 3;//更多任务
	
	/*
	 * 兑换
	 */
	String TYPE = "type";    	//兑换类型
	String XB_NUM = "xbnum"; 	//积分数
	String TITLE = "title"; 	//兑换类容
	String QB = "qb";
	String HF = "hf";
	String QQ_VIP = "vip"; 		//会员
	String QQ_YELLOW = "yellow";//黄钻
	String QQ_RED = "red"; 		//红钻
	String QQ_BLUE = "blue"; 	//蓝钻
	String QQ_GREEN = "green"; 	//绿钻
	String QQ_SUPPER = "supper";//超级QQ
	
	String NEW_TASK = "newTask"; //新手任务
	int NEW_TASK_NUM = 1 ;//新手任务做过三次就不再提示
	String RECOVER = "recover";//找回密码
	
	String QQ_APP_ID = "1103411013";//qqappid
	String QQ_APP_KEY = "hXaugfRTBHcEi3zl";
	
	String WX_APP_ID = "wx99b7c65598e9612d"; //微信APPid
	String WX_APP_SECRET = "4f466dcdc24b9f35fb034c8374230f9d";
	
	/*
	 * 夺宝接口
	 */
	String DB_BASE = "http://www.jiequbao.com/index.php?r=apiIndiana/";
	String DB_GOODSLIST_URL = DB_BASE+"goodListNew"; //获取夺宝资源列表
	String DB_GOODSDETAIL_URL = DB_BASE+"getTastInfo"; //获取当前夺宝任务详细信息
	String DB_INDIANAT_URL = DB_BASE+"indianatList"; //所有参与者
	String DB_CART_LIST_URL = DB_BASE+"cartList";//购物车列表
	String DB_OP_CART_URL = DB_BASE+"cart";//购物车操作
	String DB_CART_PAY_URL = DB_BASE+"cartExchangeIndiana";//购物车结算
	String DB_OLD_TASK_URL = DB_BASE+"oldTaskList";//往期揭晓
	String DB_ORDER_LIST_URL = DB_BASE+"orderIndianatList";//订单接口 
	String DB_USER_INFO_URL = DB_BASE+"userInfo";//用户余额
	
	String DB_CART_NUM = "CARTNUM";//清单列表
}






