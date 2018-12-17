package com.dianju.trustedsign.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 网络访问接口
 * @auther chenlf3
 * @date 2017年11月24日-下午2:09:36
 * Copyright (c) 2017点聚信息技术有限公司-版权所有
 * conn.setDoOutput(true);,get方式不可以加该设置
 */
public class ClfNet {
	public static final String CODE_FORMAT = "UTF-8";
	public static final int CONN_TIME = 30000;
	
	public static String[] sendPostJson(String url, JSONObject params) {
		String[] res = new String[2];
		try {
			URL address = new URL(url);
	        HttpURLConnection conn = (HttpURLConnection) address.openConnection();
	        conn.setConnectTimeout(CONN_TIME);
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        conn.setRequestMethod("POST");
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.connect();
	        //写数据进去
	        String data = params.toString();
	        BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
	        out.write(data.getBytes(CODE_FORMAT));
	        out.flush();
	        out.close();
	        int code = conn.getResponseCode();
	        res[0] = ""+code;
	        if(code==200) {
	        	BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
	        	ByteArrayOutputStream os = new ByteArrayOutputStream();
	            byte[] buffer = new byte[1024];
	            int len = -1;
	            while((len=is.read(buffer))!=-1){
	            	os.write(buffer, 0, len);
	            }
	        	is.close();
	        	os.close();
	        	conn.disconnect();
	        	byte[] respData = os.toByteArray();
				String msg = new String(respData, CODE_FORMAT);
	        	res[1] = msg;
	        } else {
	        	conn.disconnect();
	        }
	        return res;
		} catch(IOException e) {
			res[0] = ""+-999;
			res[0] = e.getMessage();
			return res;
		}
	}
	
	public static String[] sendPost(String url, Map<String, String> params) {
		String[] res = new String[2];
		try {
			URL address = new URL(url);
	        HttpURLConnection conn = (HttpURLConnection) address.openConnection();
	        conn.setConnectTimeout(CONN_TIME);
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        conn.setRequestMethod("POST");
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.connect();
	        //写数据进去
	        String data = params.toString();
	        String temp = URLEncoder.encode(data,CODE_FORMAT);
	        BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
	        out.write(temp.getBytes(CODE_FORMAT));
	        out.flush();
	        out.close();
	        int code = conn.getResponseCode();
	        res[0] = ""+code;
	        if(code==200) {
	        	BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
	        	ByteArrayOutputStream os = new ByteArrayOutputStream();
	            byte[] buffer = new byte[1024];
	            int len = -1;
	            while((len=is.read(buffer))!=-1){
	            	os.write(buffer, 0, len);
	            }
	        	is.close();
	        	os.close();
	        	conn.disconnect();
	        	byte[] respData = os.toByteArray();
				String resStr = new String(respData, CODE_FORMAT);
	        	String msg = URLDecoder.decode(resStr,CODE_FORMAT);
	        	res[1] = msg;
	        } else {
	        	conn.disconnect();
	        }
	        return res;
		} catch(IOException e) {
			res[0] = ""+-999;
			res[0] = e.getMessage();
			return res;
		}
	}
	
	
	public static String getParams(Map<String,String> map) throws UnsupportedEncodingException {
		if(map == null) return null;
		Set<String> keys = map.keySet();
		if(keys.size() == 0) return null;
		StringBuilder temp = new StringBuilder("?");
		for(String key:keys) {
			//temp.append(key).append("=").append(map.get(key)==null?"":map.get(key)).append("&");
			temp.append(key).append("=").append(map.get(key)==null?"":URLEncoder.encode(map.get(key),CODE_FORMAT)).append("&");
		}
		String res = temp.toString();
		if(res.endsWith("&")) {
			return res.substring(0, res.length()-1);
		} else {
			return res;
		}
	}
	
	public static String getParams(JSONObject jsonObj) throws JSONException, UnsupportedEncodingException {
		if(jsonObj == null) return null;
		StringBuilder temp = new StringBuilder("?");
		Iterator iterator = jsonObj.keys();
		while(iterator.hasNext()){
			String key = (String) iterator.next();
		    String value = jsonObj.getString(key);
		    //temp.append(key).append("=").append(value==null?"":value).append("&");
		    temp.append(key).append("=").append(value==null?"":URLEncoder.encode(value,CODE_FORMAT)).append("&");
		}
		String res = temp.toString();
		if(res.endsWith("&")) {
			return res.substring(0, res.length()-1);
		} else {
			return res;
		}
	}
}
