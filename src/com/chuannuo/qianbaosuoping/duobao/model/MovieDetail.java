package com.chuannuo.qianbaosuoping.duobao.model;

import java.io.Serializable;

public class MovieDetail implements Serializable{

	private String alias;
	private String status;
	private String type;
	private String performer;
	private String director;
	private String area;
	private String subtitles;
	private String version;
	private String create_date;
	private String plot;
	private BaiduModel baiduModel;
	private AiqiyiModel aiqiyiModel;
	private XunleiModel xunleiModel;
	
	public String getStatus() {
		return status;
	}
	public String getType() {
		return type;
	}
	public String getPerformer() {
		return performer;
	}
	public String getDirector() {
		return director;
	}
	public String getArea() {
		return area;
	}
	public String getSubtitles() {
		return subtitles;
	}
	public String getVersion() {
		return version;
	}
	public String getCreate_date() {
		return create_date;
	}
	public String getPlot() {
		return plot;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setPerformer(String performer) {
		this.performer = performer;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public void setSubtitles(String subtitles) {
		this.subtitles = subtitles;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public void setPlot(String plot) {
		this.plot = plot;
	}

	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public BaiduModel getBaiduModel() {
		return baiduModel;
	}
	public void setBaiduModel(BaiduModel baiduModel) {
		this.baiduModel = baiduModel;
	}
	public AiqiyiModel getAiqiyiModel() {
		return aiqiyiModel;
	}
	public void setAiqiyiModel(AiqiyiModel aiqiyiModel) {
		this.aiqiyiModel = aiqiyiModel;
	}
	public XunleiModel getXunleiModel() {
		return xunleiModel;
	}
	public void setXunleiModel(XunleiModel xunleiModel) {
		this.xunleiModel = xunleiModel;
	}

}
