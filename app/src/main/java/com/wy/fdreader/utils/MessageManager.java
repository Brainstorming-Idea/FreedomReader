package com.wy.fdreader.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wy.fdreader.utils.updateUtil.DownloadListener;

public class MessageManager {
	
	private DownloadListener listener;
	private Bundle pgBundle = new Bundle();
	
	public MessageManager(DownloadListener listener){
		this.listener = listener;
	}

	public Handler downloadHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.Flag.DOWN_ING:
				pgBundle = msg.getData();
				listener.onProgressChange(pgBundle);
				break;
			case Constant.Flag.DOWN_OK:
				pgBundle = msg.getData();
				listener.onFinish(pgBundle);
				break;
			case Constant.Flag.DOWN_ERROR:
				listener.onFailed();
				break;
			default:
				break;
			}
		};
	};
}
