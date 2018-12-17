package com.wy.fdreader.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 工具类
 * @auther chenlf3
 * @date 2018年5月9日-上午10:25:56
 * Copyright (c) 2018点聚信息技术有限公司-版权所有
 */
public class CommonUtil {
	private Context context;
	public CommonUtil(Context context) {
		this.context = context;
	}
	
	public static boolean isSel(String mFileName) {
		if(TextUtils.isEmpty(mFileName)) return false;
		String fileName = mFileName.toLowerCase();
		if(fileName.endsWith(".sel") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".bmp")) {
			return true;
		}
		return false;
	}
	
	public static void popTip(Context context,String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}
	
}
