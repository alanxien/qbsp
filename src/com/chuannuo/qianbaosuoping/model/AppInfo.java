package com.chuannuo.qianbaosuoping.model;

import java.io.Serializable;

import android.graphics.Bitmap;


@SuppressWarnings("serial")
public class AppInfo implements Serializable{

	public int id;
	public int adId;            //���ID
	public int resource_id;      //��ԴID
	public String title;		 //app����
	public String price;		 //�۸�
	public String h5_big_url;    //��ͼ
	public int click_type;		 //����1:������վ ;2:չʾ��ͼƬ;3:��绰; 4:������;5:���ʼ�;6:��λ;7:�ӹ����Ч�����͵�ͼ���ַ����Ĭ��Ϊ8��8������
	public int b_type;			 //����ģʽ��1��װ��2ע��
	public String name;   		 //��Դ����
	public String icon;			 //ͼ��
	public String description;	 //����
	public String package_name;  //����
	public String brief;		 //Ӧ�ü��
	public int score;			 //����
	public String resource_size; //app��С KB
	public String html_desc;     //��ҳ����
	public String file;			 //���ص�ַ
	public int sign_rules;       //ǩ�����򣬸��������ǩ��
	public int sign_times;       //ǩ������
	public int needSign_times;   //��Ҫǩ������
	public int install_id;       //��װӦ��ID
	public int totalScore; 		 //������������ܹ���׬���� ����
	public int isAddIntegral;    //�ж��û��Ƿ���������ػ���
	public Bitmap bitmap;		//appͼ��
	public int isShare;          //�Ƿ��Ƿ���app
	
	public int is_photo;        //�Ƿ��Ѿ��ϴ���0δ�ϴ���1�Ѿ� �ϴ�
	private int photo_integral;  //�ϴ�������Ի�ȡ���ٻ���
	private int photo_status;   //ͼƬ ���״̬��0δ�ϴ���1����ˣ�2����ɹ���3����ʧ��
	private int is_photo_task;  //0��֧�֣�1֧��
	private String photo;//�ͻ��ϴ�����Ƭ
	
	private boolean isSign = false; //�Ƿ���ǩ������
	
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
