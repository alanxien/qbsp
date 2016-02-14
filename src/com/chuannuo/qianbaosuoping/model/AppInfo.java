package com.chuannuo.qianbaosuoping.model;

import java.io.Serializable;

import android.graphics.Bitmap;


@SuppressWarnings("serial")
public class AppInfo implements Serializable{

	public int id;
	public int adId;            //广告ID
	public int resource_id;      //资源ID
	public String title;		 //app名称
	public String price;		 //价格
	public String h5_big_url;    //大图
	public int click_type;		 //类型1:跳到网站 ;2:展示大图片;3:打电话; 4:发短信;5:发邮件;6:定位;7:视广告点击效果类型的图标地址，，默认为8，8是下载
	public int b_type;			 //合作模式，1安装，2注册
	public String name;   		 //资源名称
	public String icon;			 //图标
	public String description;	 //描述
	public String package_name;  //包名
	public String brief;		 //应用简介
	public int score;			 //积分
	public String resource_size; //app大小 KB
	public String html_desc;     //网页描述
	public String file;			 //下载地址
	public int sign_rules;       //签到规则，隔几天可以签到
	public int sign_times;       //签到次数
	public int needSign_times;   //需要签到次数
	public int install_id;       //安装应用ID
	public int totalScore; 		 //做完这个任务总共可赚多少 积分
	public int isAddIntegral;    //判断用户是否添加了下载积分
	public Bitmap bitmap;		//app图标
	public int isShare;          //是否是分享app
	
	public int is_photo;        //是否已经上传，0未上传，1已经 上传
	private int photo_integral;  //上传任务可以获取多少积分
	private int photo_status;   //图片 审核状态，0未上传，1待审核，2任务成功，3任务失败
	private int is_photo_task;  //0不支持，1支持
	private String photo;//客户上传的照片
	
	private boolean isSign = false; //是否是签到任务
	
	public int getIs_photo() {
		return is_photo;
	}
	public void setIs_photo(int is_photo) {
		this.is_photo = is_photo;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setAdId(int adId) {
		this.adId = adId;
	}
	public int getAdId() {
		return adId;
	}
	public int getResource_id() {
		return resource_id;
	}
	public void setResource_id(int resource_id) {
		this.resource_id = resource_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getH5_big_url() {
		return h5_big_url;
	}
	public void setH5_big_url(String h5_big_url) {
		this.h5_big_url = h5_big_url;
	}
	public int getClicktype() {
		return click_type;
	}
	public void setClicktype(int clicktype) {
		this.click_type = clicktype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getB_type() {
		return b_type;
	}
	public void setB_type(int b_type) {
		this.b_type = b_type;
	}
	public String getHtml_desc() {
		return html_desc;
	}
	public void setHtml_desc(String html_desc) {
		this.html_desc = html_desc;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPackage_name() {
		return package_name;
	}
	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}
	public int getPhoto_integral() {
		return photo_integral;
	}
	public void setPhoto_integral(int photo_integral) {
		this.photo_integral = photo_integral;
	}
	public String getBrief() {
		return brief;
	}
	public int getIsShare() {
		return isShare;
	}
	public void setIsShare(int isShare) {
		this.isShare = isShare;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getIsAddIntegral() {
		return isAddIntegral;
	}
	public void setIsAddIntegral(int isAddIntegral) {
		this.isAddIntegral = isAddIntegral;
	}
	public int getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getResource_size() {
		return resource_size;
	}
	public void setResource_size(String resource_size) {
		this.resource_size = resource_size;
	}
	public String getHtmldesc() {
		return html_desc;
	}
	public int getSign_rules() {
		return sign_rules;
	}
	public void setSign_rules(int sign_rules) {
		this.sign_rules = sign_rules;
	}
	public int getSign_times() {
		return sign_times;
	}
	public void setSign_times(int sign_times) {
		this.sign_times = sign_times;
	}
	public int getInstall_id() {
		return install_id;
	}
	public void setInstall_id(int install_id) {
		this.install_id = install_id;
	}
	public int getNeedSign_times() {
		return needSign_times;
	}
	public void setNeedSign_times(int needSign_times) {
		this.needSign_times = needSign_times;
	}
	public void setHtmldesc(String htmldesc) {
		this.html_desc = htmldesc;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public int getPhoto_status() {
		return photo_status;
	}
	public void setPhoto_status(int photo_status) {
		this.photo_status = photo_status;
	}
	public int getIs_photo_task() {
		return is_photo_task;
	}
	public void setIs_photo_task(int is_photo_task) {
		this.is_photo_task = is_photo_task;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public boolean isSign() {
		return isSign;
	}
	public void setSign(boolean isSign) {
		this.isSign = isSign;
	}

}
