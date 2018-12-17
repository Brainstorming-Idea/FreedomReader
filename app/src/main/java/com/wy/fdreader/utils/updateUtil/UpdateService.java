package com.wy.fdreader.utils.updateUtil;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.wy.fdreader.utils.Constant;
import com.wy.fdreader.R;

import java.io.File;

public class UpdateService extends Service {

	private Context context;
	private String apkpath;
	private String apkUrl;
	private Notification notification;
	private NotificationManager notificationManager;
	private NotificationCompat.Builder nBuilder;
	private RemoteViews notifyView;
	private DownloadListener listener;
	private int progress = 0;
	private String curLen = "0.0M";
	public String total = "0.0M";
	private Bundle xmlData = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		xmlData = intent.getExtras();
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		this.context = this;
		Log.i("TAG", "service is oncreated!");
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		initNotification();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent == null) {
			updateNotify(getString(R.string.download_failed), null);
			stopSelf();
		}
		xmlData = intent.getExtras();
		apkUrl = xmlData.getString("apkUrl");
		apkpath = Constant.DOWNLOAD_PATH + apkUrl.substring(apkUrl.lastIndexOf("/") + 1);
		updateNotify(getString(R.string.notification_ticker), null);
		//开始下载
		startDownload();
		return super.onStartCommand(intent, START_REDELIVER_INTENT, startId);
	}
	
	private void startDownload() {
		DownLoadManager.getInstence().startDownloads(apkUrl, new DownloadListener() {
			
			@Override
			public void onStart() {
				// 开始
				Toast.makeText(context, context.getString(R.string.download_start), Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onProgressChange(Bundle bundle) {
				// 更新进度
				Log.i("onProgressChange", bundle.getInt("progress")+"");
				updateNotify(context.getString(R.string.notification_text), bundle);
				
			}
			
			@Override
			public void onFinish(Bundle bundle) {
				// 下载完成
				Log.i("onFinished", "下载完成");
				updateNotify(context.getString(R.string.download_finish), bundle);
			}
			
			@Override
			public void onFailed() {
				// 下载失败
				Log.i("onFailed", "下载失败");
				updateNotify(context.getString(R.string.download_failed), null);
			}
		});
	}
	
	/**
	 * @Desc 初始化通知 
	 * @author wy  
	 * @date 2018年7月18日
	 */
	public void initNotification(){
		nBuilder = new NotificationCompat.Builder(this);
		nBuilder.setSmallIcon(R.drawable.ic_launcher)
		.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
		.setContentTitle(getResources().getString(R.string.notification_title));
		nBuilder.setAutoCancel(true);//点击通知自动清除
		//自定义更新布局
		notifyView = new RemoteViews(getPackageName(), R.layout.notification_update);
		notifyView.setTextViewText(R.id.notification_update_progress_text, "0%");
		nBuilder.setContent(notifyView);
		notification = nBuilder.build();
	}

	/**
	 * @Desc 更新notification
	 * @author wy  
	 * @date 2018年7月10日
	 */
	private void updateNotify(String result, Bundle pgBundle){
		if (pgBundle != null) {
			progress = pgBundle.getInt("progress");
			curLen = pgBundle.getString("curLen");
			total = pgBundle.getString("total");
		}
		if (notifyView != null) {
			if (context.getString(R.string.download_finish).equals(result)) {
				notifyView.setTextViewText(R.id.notification_update_progress_title, context.getString(R.string.notification_title));
				notifyView.setViewVisibility(R.id.notification_update_progress_bar, View.GONE);
				notifyView.setViewVisibility(R.id.notification_update_progress_text, View.GONE);
				notifyView.setViewVisibility(R.id.linearLayout_result, View.VISIBLE);
				notifyView.setTextViewText(R.id.update_result, context.getString(R.string.download_finish));
				File apkFile = new File(apkpath);
				notifyView.setOnClickPendingIntent(R.id.content_view, installApk(apkFile));
			}else if (context.getString(R.string.download_failed).equals(result)) {
				notifyView.setTextViewText(R.id.notification_update_progress_title, context.getString(R.string.notification_title));
				notifyView.setViewVisibility(R.id.notification_update_progress_bar, View.GONE);
				notifyView.setViewVisibility(R.id.notification_update_progress_text, View.GONE);
				notifyView.setViewVisibility(R.id.linearLayout_result, View.VISIBLE);
				notifyView.setTextViewText(R.id.update_result, context.getString(R.string.download_failed));
			}else {
				notifyView.setViewVisibility(R.id.notification_update_progress_bar, View.VISIBLE);
				notifyView.setViewVisibility(R.id.notification_update_progress_text, View.VISIBLE);
				notifyView.setViewVisibility(R.id.linearLayout_result, View.GONE);
				notifyView.setTextViewText(R.id.notification_update_progress_text, progress+"%");
				notifyView.setTextViewText(R.id.notification_update_progress_title, context.getString(R.string.notification_title)+
						"("+curLen +"/"+total+")");
				notifyView.setProgressBar(R.id.notification_update_progress_bar, 100, progress, false);
			}
			notificationManager.notify(0, notification);
		}
		
//		if (progress>0 && progress <= 100) {
//			nBuilder.setProgress(100, progress, false);//4.0以上可用
//		}else {
//			nBuilder.setProgress(0, 0, false);
//		}
		//设置通知栏内容
//		nBuilder.setContentText(getResources().getString(R.string.notification_text) + "("+curLen +"/"+total+")");
//		nBuilder.setTicker(result);//状态栏通知信息
//		File apkFile = new File(apkpath);
//		Intent mIntent = new Intent(this,MainActivity.class);
//		nBuilder.setContentIntent(progress >= 100 ? AppUpdateUtil.installApk(apkFile) 
//				: PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
//		nBuilder.setContentIntent(progress >= 100 ? AppUpdateUtil.installApk(apkFile) 
//				: null);
//		notificationManager.notify(0, notification);//更新通知
	}
	
	public PendingIntent installApk(File file) {
		/*Android7.0之后文件访问受限，需要区分*/
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			//非activity环境启动应用需要设置以下标签
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
			Uri apkUri = FileProvider.getUriForFile(context, "com.dianju.djreader.intallDjReader", file);
			//添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			// 执行的数据类型
			intent.setDataAndType(apkUri,
					"application/vnd.android.package-archive");
		}else {
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopSelf();
		super.onDestroy();
	}

}
