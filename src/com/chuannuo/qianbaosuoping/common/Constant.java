package com.chuannuo.qianbaosuoping.common;

/**
 * @author alan.xie
 * @date 2014-10-13 ����2:41:55
 * @Description: �����ӿ�
 */
public interface Constant {
	
	String PACKAGE_NAME = "com.chuannuo.qianbaosuoping";
	String DOWNLOAD_DIR = "qbsp/download/";
	String IMG_DIR = "qbsp/images/";
	
	/*
	 * sharedPreferenced ����
	 */
	String STUDENTS_EARN = "studentsearn"; //�ļ���
	String IS_FIRST_IN = "isFirstIn";      //�Ƿ��һ�ν���
	String IS_QUICK_START="isQuickStart";  //�Ƿ��ǿ���ע��

	String APPID = "appid"; //�û��˺�
	String CODE = "code";   //У����
	String PHONE = "phone"; //�û��绰
	String PASSWORD = "password"; //�û�����
	String ADDRESS = "address";
	String CFT = "cft";
	String ZFB = "zfb";
    String WAVE_TIMES = "waveTimes";   //ҡһҡʱ��
    String SIGN_TIMES = "signTimes";   //ǩ��ʱ��
    String OFFLINE_TIME = "offLineTime";      //ÿʮ���Ӹ���һ�Σ�
    String APP_RUNNING_TIME = "appRunningTime";//app����ʱ��
    String APP_SIGN_IS_SUCCESS = "appSignIsSuccess"; //appǩ���Ƿ�ɹ���0û��ʼǩ����1δ��ɵ�ǩ����2ǩ���ɹ�
    
    String SHARE_TIME = "shareTime"; //��������ʱ��
    String DOWNLOAD_TIME = "downTime";   //��������ʱ��
    String SLIDER_UNLOCK_TIME = "silderUnlock";//��������ʱ��
    String SLIDED = "isSlided"; //�Ƿ���
    String RECENT_SHARE_TIME = "recentShareTime"; //���һ�η���ʱ��
    String SHARE_SIZE = "shareSize";//�жϷ��������Ƿ���
    
    String R_ACCOUNT = "rememberAccount";//��ס�˺�
    String R_PASSWORD = "rememberPassword";//��ס����
    String IS_LOCK_SCREEN = "isLockScreen";//�Ƿ����ᱦ
    
    String DL_TOTAL_SCORE = "dlTotalScore"; //��¼�����ܻ���
    String DM_TOTAL_SCORE = "dmTotalScore"; //��¼�����ܻ���
    String YM_TOTAL_SCORE = "ymTotalScore"; //��¼�����ܻ���
    
    
    String TASK_SIGN = "taskSign";//ÿ��ǩ��
    String TASK_KNOW_APP = "taskKnowApp";//�˽�APP
    String TASK_WAVE = "taskWave"; //ÿ��ҡһҡ
    String TASK_SHARE = "taskShare";//����
    String TASK_EXPERIENCE = "taskExperience";//����һ��Ӧ������
    String TASK_USER_INFO  = "taskUserInfo";//�����û���Ϣ
  
    String ITEM = "item";
    String SLIDER_NUM = "sliderNum";     //��������
    String SHARE_NUM = "shareNum";       //�������
    String DOWNLOAD_NUM = "downLoadNum"; //���ش���
    String INVIT_CODE = "invitCode";    //������
    String DOWNLOAD_APP_TIME = "downLoadAppTime";//�û�����app�ĵ�ǰʱ��
    String IS_FINISHED = "isFinished";  //Ӧ���Ƿ��Ѿ����� 
    String PAY_CARD = "payCard";//���ʿ�
    String LATEST_SCROE = "latestScore";//�ϴ�ҡһҡʱ��Ļ��֣��ж��Ƿ���Խ����´�ҡһҡ�齱��
    String SCORE = "score";       //��ǰ����
    
    String DOWNLOAD_TIMES = "downLoadTimes"; //ÿ�������ض��ٴ�Ǯ���ᱦapp
    String DOWN_TIME = "downLoadTime";//��¼����ʱ�䣬����һ�����������
    
    /*
	 * �Ƿ�����Home��
	 */
	public static final String IS_KEY_HOME = "isKeyHome";
    
	
	/*
	 * �������ӿ�
	 */
    String ROOT_URL = "http://112.74.88.252";	//http://www.jiequbao.com/							//������
	String BASE_URL = "http://112.74.88.252/index.php?r=zhuanMi";
	String DOWN_URL = "http://qbsp.jiequbao.com";//����URL
	//String ROOT_URL = "http://192.168.1.57/zhuanmi2/www";						//����
	//String BASE_URL = "http://192.168.1.57/zhuanmi2/www/index.php?r=zhuanMi";
	String SCREEN_SHOT_RUL = "http://m.baidu.com/s?word=";
	
	String LOGIN_URL = BASE_URL+"/login";     					//��½�����ֻ��ţ�
	String USER_INFO_URL = BASE_URL+"/userInfo";				//�û���ϸ��Ϣ
	String NEW_EXCHANGE_URL = BASE_URL+"/convertList";			//���¶һ���¼
	String SIGN_IN_URL = BASE_URL + "/sign";                    //ǩ��
	String MODIFY_USER_URL = BASE_URL + "/modifyUser";			//�û������޸�
	String ADD_INTEGRAL_URL  = BASE_URL + "/addIntegral";		//���ӻ���
	String MODIFY_PASSWORD_URL = BASE_URL + "/modifyPassword";  //�޸�����
	//String EXCHANGE_QB_URL = BASE_URL + "/exchangeQQ";          //�һ�Q��
	String EXCHANGE_PHONE_URL = BASE_URL + "/exchangeMobile";   //�ֻ���ֵ
	String EXCHANGE_ZFB_URL = BASE_URL + "/exchangeAlipay";     //֧������ֵ
	String EXCHANGE_CFT_URL = BASE_URL + "/exchangeTenpay";     //�Ƹ�ͨ��ֵ
	String EXCHANGE_LIST_URL = BASE_URL + "/convertListUser";  //�һ���¼
	String INVITATION_LIST_URL = BASE_URL + "/getInvitationUser";//�����¼
	String TASK_LIST_URL = BASE_URL + "/userTaskList";          //��������б�
	String GET_CODE_URL = BASE_URL + "/getCode";                //��ȡ��֤��
	String VALIDATE_CODE_URL = BASE_URL + "/validateCode";      //���ֻ���
	String RECOVER_PASSWORD_URL = BASE_URL + "/recoverPassword";//�һ�����
	String ADINSTALL_URL = BASE_URL + "/adInstall";             //�����Ѿ����ذ�װ����app
	String REPEAT_SIGN_URL = BASE_URL + "/reportSign";          //������� ǩ��
	String DEPTH_TASK_LIST_URL = BASE_URL + "/unfinishedSignList"; //�������list
	
	String SHARE_URL = BASE_URL + "/index&invitation_code=";      //����
	//String DOWNLOAD_URL = BASE_URL + "/getResourceList";		//����
	String REPORT_URL = BASE_URL + "/reportPackageNames";        //�ϱ�Ӧ�ð�װ
	String LOGO_URL = ROOT_URL + "/images/ic_launcher.png";    //logo_url
	String SHARE_TASK_URL = BASE_URL + "/getPostList";			//��������
	String REPORE_SHARE_URL = BASE_URL+"/reportShare";			//�ϱ�����
	String CONVERT_SHARE = BASE_URL + "/getConvertShare";		//�һ�����
	String REPORT_CONVERT_SHARE= BASE_URL + "/reportConvertShare";//�ϱ��һ�����
	String CONFIRM_INSTALL_INTEGRAL = BASE_URL + "/confirmInstallIntegral";//ȷ��Ӧ�ð�װ ��ӻ���
	String REPORT_TASK = BASE_URL + "/reportTask";					//�ϱ����񣨷�������
	String PUSH_MSG = BASE_URL + "/pushMsg";						//��ѯ��Ϣ
	String TASK_RETURN = BASE_URL + "/taskReturn";					//����������ϸ��¼
	String GET_PHONE_NUM_URL = BASE_URL +"/oldLogin";				//����emei��ȡ�ֻ���
	String QUICK_REGISTRATION_URL = BASE_URL + "/signup";//һ��ע��
	String QUICK_LOGIN_URL = BASE_URL + "/getUserByImei";//���ֻ��ŵ�½
	String BIND_MOBILE_URL = BASE_URL + "/bindMobile";   //���ֻ�
	String GET_RESOURCE_LIST_SDK = BASE_URL+"/getResourceListSDK";//��ϷSDK�б�
	
	/*
	 * �ϴ���ͼ�ӿ�
	 */
	String DOWNLOAD_URL = BASE_URL+"/getResourceListHtml"; //��ͼ����ӿ�
	String EXCHANGE_INDIANA = BASE_URL +"/exchangeIndiana";//�һ��ᱦ��
	String UPLOADS_PHOTO = BASE_URL +"/uploadsPhotoHtmls";//�ϴ�ͼƬ
	String GET_USER_AD_ALERT = BASE_URL+"/getUserAdAlert";//�ϴ�ͼƬ�ɹ���������ʾ
	String MODIFY_USER_ADALERT = BASE_URL + "/modifyUserAdAlert";//��ͼ�޸��ϱ���
	String ADDCUSTOM = BASE_URL +"/addCustom";
	
	int PAGER0 = 0;//��Ϸ����
	int PAGER1 = 1;//�Ƽ�����
	int PAGER2 = 2;//��������
	int PAGER3 = 3;//��������
	
	/*
	 * �һ�
	 */
	String TYPE = "type";    	//�һ�����
	String XB_NUM = "xbnum"; 	//������
	String TITLE = "title"; 	//�һ�����
	String QB = "qb";
	String HF = "hf";
	String QQ_VIP = "vip"; 		//��Ա
	String QQ_YELLOW = "yellow";//����
	String QQ_RED = "red"; 		//����
	String QQ_BLUE = "blue"; 	//����
	String QQ_GREEN = "green"; 	//����
	String QQ_SUPPER = "supper";//����QQ
	
	String NEW_TASK = "newTask"; //��������
	int NEW_TASK_NUM = 1 ;//���������������ξͲ�����ʾ
	String RECOVER = "recover";//�һ�����
	
	String QQ_APP_ID = "1103411013";//qqappid
	String QQ_APP_KEY = "hXaugfRTBHcEi3zl";
	
	String WX_APP_ID = "wx99b7c65598e9612d"; //΢��APPid
	String WX_APP_SECRET = "4f466dcdc24b9f35fb034c8374230f9d";
	
	/*
	 * �ᱦ�ӿ�
	 */
	String DB_BASE = "http://www.jiequbao.com/index.php?r=apiIndiana/";
	String DB_GOODSLIST_URL = DB_BASE+"goodListNew"; //��ȡ�ᱦ��Դ�б�
	String DB_GOODSDETAIL_URL = DB_BASE+"getTastInfo"; //��ȡ��ǰ�ᱦ������ϸ��Ϣ
	String DB_INDIANAT_URL = DB_BASE+"indianatList"; //���в�����
	String DB_CART_LIST_URL = DB_BASE+"cartList";//���ﳵ�б�
	String DB_OP_CART_URL = DB_BASE+"cart";//���ﳵ����
	String DB_CART_PAY_URL = DB_BASE+"cartExchangeIndiana";//���ﳵ����
	String DB_OLD_TASK_URL = DB_BASE+"oldTaskList";//���ڽ���
	String DB_ORDER_LIST_URL = DB_BASE+"orderIndianatList";//�����ӿ� 
	String DB_USER_INFO_URL = DB_BASE+"userInfo";//�û����
	
	String DB_CART_NUM = "CARTNUM";//�嵥�б�
}






