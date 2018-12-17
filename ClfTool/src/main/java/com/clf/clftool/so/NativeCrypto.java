package com.clf.clftool.so;

import com.clf.clftool.so.other.JniResult;

/**
 * libNativeCrypto.so导入
 * @auther chenlf3
 * @date 2015年8月6日-上午9:54:37
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class NativeCrypto {
	
	/**
	 * 获取p10
	 * @auther chenlf3
	 * @date 2015年8月6日 上午10:08:47
	 * @param s "CN=CFCA@Mobile@Android@1.0,OU=Customers,O=CFCA,C=CN"
	 * @param s1 证书的类型CFCAPublicConstant.RSA2048
	 * @param i 证书的密码
	 * @param j CFCAPublicConstant.SINGLE_CERT
	 * @return
	 */
	public native JniResult createP10Request(String s, String s1, int i, int j);
	
	
	/**
	 * 获取签名后的数据
	 * @auther chenlf3
	 * @date 2015年8月6日 上午11:31:11
	 * @param s 证书密码
	 * @param abyte0 待签名数据
	 * @param i CFCAPublicConstant.HASH_SHA1
	 * @param j CFCAPublicConstant.SIGN_PKCS7_A
	 * @param abyte1 CFCACertificate.getDercode() 这是证书的某个属性值
	 * @return
	 */
	public native JniResult signMsg(String s, byte abyte0[], int i, int j, byte abyte1[]);
	
	
	
	
	
	
	
	
	
}
