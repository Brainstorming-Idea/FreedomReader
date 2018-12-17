package com.dianju.trustedsign.encript.rsa;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;


import android.util.Base64;
import android.util.Log;

/**
 * RSA加解密,调用RSAUtils
 * @auther chenlf3
 * @date 2015年9月3日-下午6:36:10
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class RSAHelper {
	/** 公钥文件,在assets文件夹下 */
	public static String PUBLIC_KEY_FILE = "rsa_public_key.pem";
	
	/** 私钥文件 */
	public static String PRIVATE_KEY_FILE = "pkcs8_rsa_private_key.pem";
	
	/**
	 * 对原始数据RSA加密，然后再用base64加密(读取文件中的密钥)
	 * @auther chenlf3
	 * @date 2015年9月3日 下午6:36:47
	 * @param context
	 * @param data 明文
	 * @return
	 */
	public static String encryptData(InputStream in,String data) {
		try {
			/** 获取公钥 */
			//InputStream in = context.getAssets().open(PUBLIC_KEY_FILE);
			PublicKey publicKey = RSAUtils.loadPublicKey(in);
			/** 加密 */
			byte[] temp = RSAUtils.encryptData(data.getBytes("UTF-8"), publicKey);
			/** 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换 */
			String res = new String(Base64.encode(temp, Base64.DEFAULT),"UTF-8");
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("dianju", "--------------------加密失败！");
			return null;
		}
	}
	
	/**
	 * 先进行base64解密，再进行RSA解密(读取文件中的密钥)
	 * @auther chenlf3
	 * @date 2015年9月3日 下午6:46:09
	 * @param context
	 * @param data 密文
	 * @return
	 */
	public static String decode(InputStream in, String data) {
		try {
			//InputStream in = context.getAssets().open(PRIVATE_KEY_FILE);
			PrivateKey privateKey = RSAUtils.loadPrivateKey(in);
			/** 首先对数据进行base64解密 */
			byte[] temp = Base64.decode(data, Base64.DEFAULT);
			/** 进行RSA解密 */
			String res = new String(RSAUtils.decryptData(temp, privateKey),"UTF-8");
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("dianju", "--------------------解密失败！");
			return null;
		}
	}
	
	/**
	 * 对原始数据RSA加密，然后再用base64加密
	 * @auther chenlf3
	 * @date 2015年9月3日 下午6:49:08
	 * @param data 明文
	 * @param publicKeyStr 公钥
	 * @return
	 */
	public static String encryptData(String data, String publicKeyStr) {
		try {
			/** 获取公钥 */
			PublicKey publicKey = RSAUtils.loadPublicKey(publicKeyStr);
			/** 加密 */
			byte[] temp = RSAUtils.encryptData(data.getBytes("UTF-8"), publicKey);
			/** 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换 */
			String res = new String(Base64.encode(temp, Base64.DEFAULT),"UTF-8");
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("dianju", "--------------------加密失败！");
			return null;
		}
	}
	
	/**
	 * 先进行base64解密，再进行RSA解密
	 * @auther chenlf3
	 * @date 2015年9月3日 下午6:50:17
	 * @param data 密文
	 * @param privateKeyStr 私钥
	 * @return
	 */
	public static String decode(String data, String privateKeyStr) {
		try {
			PrivateKey privateKey = RSAUtils.loadPrivateKey(privateKeyStr);
			/** 首先对数据进行base64解密 */
			byte[] temp = Base64.decode(data, Base64.DEFAULT);
			/** 进行RSA解密 */
			String res = new String(RSAUtils.decryptData(temp, privateKey),"UTF-8");
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("dianju", "--------------------解密失败！");
			return null;
		}
	}
	
	
}
