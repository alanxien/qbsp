package com.chuannuo.qianbaosuoping.duobao.model;

import java.io.Serializable;

public class Movie implements Serializable{

	private int id;
	private String alias;
	private String icon;
	private MovieDetail movieDetail;
	
	public int getId() {
		return id;
	}
	public String getAlias() {
		return alias;
	}
	public String getIcon() {
		return icon;
	}
	public MovieDetail getMovieDetail() {
		return movieDetail;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public void setMovieDetail(MovieDetail movieDetail) {
		this.movieDetail = movieDetail;
	}
}
