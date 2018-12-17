package com.dianju.trustedsign.utils;

import java.util.Stack;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * activity管理器
 * @auther chenlf3
 * @date 2015年8月28日-下午12:13:55
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class AppManager {
	private static Stack<Activity> activityStack;
	private static AppManager instance;
	
	private AppManager(){}
	/**
	 * 获取实例
	 * @auther chenlf3
	 * @date 2015年8月31日 上午10:26:39
	 * @return
	 */
	public static AppManager getAppManager(){
		if(instance==null){
			instance=new AppManager();
		}
		return instance;
	}

	/**
	 * 添加当前activity入栈
	 * @auther chenlf3
	 * @date 2015年8月31日 上午10:31:53
	 * @param activity
	 */
	public void addActivity(Activity activity){
		if(activityStack==null){
			activityStack=new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	
	/**
	 * 获取当前activity
	 * @auther chenlf3
	 * @date 2015年8月31日 上午10:32:16
	 * @return
	 */
	public Activity currentActivity(){
		Activity activity=activityStack.lastElement();
		return activity;
	}
	
	/**
	 * 关闭当前activity
	 * @auther chenlf3
	 * @date 2015年8月31日 上午10:32:29
	 */
	public void finishActivity(){
		Activity activity=activityStack.lastElement();
		if(activity!=null){
			activity.finish();
			activity=null;
		}
	}

	/**
	 * 关闭指定activity
	 * @auther chenlf3
	 * @date 2015年8月31日 上午10:32:38
	 * @param activity
	 */
	public void finishActivity(Activity activity){
		if(activity!=null){
			activityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}

	public void finishActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls) ){
				finishActivity(activity);
			}
		}
	}

	/**
	 * 关闭所有activity
	 * @auther chenlf3
	 * @date 2015年8月31日 上午10:34:35
	 */
	public void finishAllActivity(){
		for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
            	//System.out.print("--------"+activityStack.size());
            	activityStack.get(i).finish();
            }
	    }
		activityStack.clear();
	}

	/**
	 * 退出app
	 * @auther chenlf3
	 * @date 2015年8月31日 上午10:35:22
	 * @param context
	 */
	public void AppExit(Context context) {
		try {
			//CommonUtil.setToken(context, null);
			finishAllActivity();
			ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {	}
	}
}
