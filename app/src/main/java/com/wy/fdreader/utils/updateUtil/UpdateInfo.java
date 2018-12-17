package com.wy.fdreader.utils.updateUtil;

import java.io.Serializable;

public class UpdateInfo implements Serializable{

	private String versionCode;
	private String versionName;
	private String url;
	private String description;
	
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public void setUrl(String url){
		this.url = url;
	}
	public String getUrl(){
		return url;
	}
	public void setDescription(String description){
		this.description = description;
	}
	public String getDescription(){
		return description;
	}
}
