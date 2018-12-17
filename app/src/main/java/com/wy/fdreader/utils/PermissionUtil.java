package com.wy.fdreader.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

/**
 * 权限检测相关类
 * @auther chenlf3
 * @date 2018年5月9日-上午11:12:49
 * Copyright (c) 2018点聚信息技术有限公司-版权所有
 */
public class PermissionUtil {
	/**
	 * 定义权限组
	 */
	public static final String[] PERMISSION_PARAMS = new String[]{"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.INTERNET"};
	private Activity context;
	
	public PermissionUtil(Context context) {
		this.context = (Activity) context;
	}
	
	/**
	 * 是否拥有所有的权限
	 * @auther chenlf3
	 * @date 2018年5月9日 上午11:32:42
	 * @return
	 */
	public boolean hasAllPermission() {
		if(Build.VERSION.SDK_INT>=23) {
			for(int i=0;i<PERMISSION_PARAMS.length;i++) {
				int res = context.checkSelfPermission(PERMISSION_PARAMS[i]);
				if(res != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 获取下一个未拥有的权限
	 * @auther chenlf3
	 * @date 2018年5月9日 上午11:36:41
	 * @return
	 */
	public String getNoOwnedPermission() {
		if(Build.VERSION.SDK_INT>=23) {
			for(int i=0;i<PERMISSION_PARAMS.length;i++) {
				int res = context.checkSelfPermission(PERMISSION_PARAMS[i]);
				if(res != PackageManager.PERMISSION_GRANTED) {
					return PERMISSION_PARAMS[i];
				}
			}
		}
		return "";
	}
	
	/**
	 * 申请单个权限
	 * @auther chenlf3
	 * @date 2018年5月9日 上午11:42:34
	 * @param permission
	 * @param requestCode
	 */
	public void requestPermissions(String permission, int requestCode) {
		if(Build.VERSION.SDK_INT>=23 && (!TextUtils.isEmpty(permission))) {
			String[] permissions = new String[]{permission};
			context.requestPermissions(permissions, requestCode);
		}
	}
}
