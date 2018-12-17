package com.dianju.trustedsign.net.urlconnection;

public class AppResponse {
	private int code;//返回码
	private String content;//返回内容
	
	public AppResponse() {
		super();
	}
	
	public AppResponse(int code, String content) {
		super();
		this.code = code;
		this.content = content;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
