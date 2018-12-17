package com.wy.fdreader.utils.updateUtil;

import android.os.Bundle;

/**
* @Desc 下载监听接口
* @author wy  
* @date 2018年7月13日
 */
public interface DownloadListener {

	void onStart();
	void onProgressChange(Bundle bundle);
	void onFinish(Bundle bundle);
	void onFailed();
}
