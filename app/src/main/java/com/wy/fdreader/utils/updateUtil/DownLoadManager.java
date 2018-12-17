package com.wy.fdreader.utils.updateUtil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wy.fdreader.utils.Constant;
import com.wy.fdreader.utils.MessageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoadManager {

	public static Handler downHandler = null;
	private MessageManager msgManager;
	private static DownLoadManager downloadManager;
	 
	public static void setDownHandler(Handler handler) {
		downHandler = handler;
	}

	public static DownLoadManager getInstence() {
		downloadManager = new DownLoadManager();
		return downloadManager;
	}

	public static void getFileFromServer(final String path, final ProgressDialog pd) throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Request request = new Request.Builder().url(path).build();// 创建请求
			new OkHttpClient().newCall(request).enqueue(new Callback() {

				@Override
				public void onResponse(Call arg0, Response response) throws IOException {
					// TODO Auto-generated method stub
					try {
						String downDir = Constant.ROOT_PATH + "Download/com.dianju.djreader/apk/";
						File apkFolder = new File(downDir);
						if (!apkFolder.exists()) {
							apkFolder.mkdirs();
						}
						File apkFile = new File(downDir + path.substring(path.lastIndexOf("/")));
						if (!apkFile.exists()) {
							apkFile.createNewFile();
						}
						InputStream is = null;
						byte[] buf = new byte[2048];
						int len = 0;
						int now = 0;
						FileOutputStream fos = null;
						long total = response.body().contentLength();
						is = response.body().byteStream();
						fos = new FileOutputStream(apkFile);
						DecimalFormat df = new DecimalFormat("0.00");
						while ((len = is.read(buf)) != -1) {
							fos.write(buf, 0, len);
							now += len;
							String a = df.format((float) now / (int) total);
							int b = (int) (Float.parseFloat(a) * 100);
							pd.setProgress(b);
							pd.setProgressNumberFormat(df.format((float) now / (1024 * 1024)) + "Mb/"
									+ df.format((float) total / (1024 * 1024)) + "Mb");
						}
						fos.flush();
						Message msg = downHandler.obtainMessage();
						msg.what = Constant.Flag.DOWN_OK;
						msg.obj = apkFile;
						downHandler.sendMessage(msg);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(Call arg0, IOException arg1) {
					// 下载失败
					arg1.printStackTrace();
				}
			});
		} else {
		}
	}

	public void downloadFile(final String downUrl, final String downPath, final Handler downloadHandler) {
		Request request = new Request.Builder().url(downUrl).build();// 创建请求
		new OkHttpClient().newCall(request).enqueue(new Callback() {

			@SuppressWarnings("resource")
			@Override
			public void onResponse(Call arg0, Response response) throws IOException {
				// TODO Auto-generated method stub
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					// String downDir = Constant.ROOT_PATH +
					// "Download/com.dianju.djreader/apk/";
					File apkFolder = new File(downPath);
					if (!apkFolder.exists()) {
						apkFolder.mkdirs();
					}
					File apkFile = new File(downPath + downUrl.substring(downUrl.lastIndexOf("/")));
					if (!apkFile.exists()) {
						apkFile.createNewFile();
					}
					byte[] buf = new byte[4*1024];
					int len = 0;
					int now = 0;
					long total = response.body().contentLength();
					is = response.body().byteStream();
					fos = new FileOutputStream(apkFile);
					DecimalFormat df = new DecimalFormat("0.00");
					int progress = 0;
					int tempProgress = 0;
					Bundle pgBundle = new Bundle();
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
						now += len;
						if (progress - tempProgress >= 2) {
							tempProgress = progress;
						}
						progress= (int) (now * 100.0F / total);
//						Log.i("Progress", progress+"");
						if (progress >= tempProgress+2) {
							Message msg = downloadHandler.obtainMessage();
							msg.what = Constant.Flag.DOWN_ING;
							pgBundle.putInt("progress", progress);
							pgBundle.putString("curLen", df.format((float) now / (1024 * 1024)) + "M");
							pgBundle.putString("total", df.format((float) total / (1024 * 1024)) + "M");
							msg.setData(pgBundle);
							downloadHandler.sendMessage(msg);
							Log.i("send Progress", progress+"");
						}
					}
					fos.flush();
					Message msg1 = downloadHandler.obtainMessage();
					msg1.what = Constant.Flag.DOWN_OK;
					msg1.obj = apkFile;
					msg1.setData(pgBundle);
					downloadHandler.sendMessage(msg1);
				} catch (Exception e) {
					// 
					Message msgF = downloadHandler.obtainMessage();
					msgF.what = Constant.Flag.DOWN_ERROR;
					downloadHandler.sendMessage(msgF);
					e.printStackTrace();
				} finally{
					fos.close();
					is.close();
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// 下载失败
				Message msgF = downloadHandler.obtainMessage();
				msgF.what = Constant.Flag.DOWN_ERROR;
				downloadHandler.sendMessage(msgF);
				arg1.printStackTrace();
			}
		});
	}

	public void startDownloads(final String apkUrl, DownloadListener listener) {
		msgManager = new MessageManager(listener);
		listener.onStart();
		// 启动下载线程
		new Thread() {
			@Override
			public void run() {
				try {
					downloadFile(apkUrl, Constant.DOWNLOAD_PATH, msgManager.downloadHandler);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		
	}

	// private static void androidDownLoad(Context context) {
	// // TODO Auto-generated method stub
	// DownloadManager.Request request = new
	// DownloadManager.Request(Uri.parse(path));
	// request.setDestinationInExternalPublicDir("",
	// path.substring(path.lastIndexOf("/")+1));
	// DownloadManager downloadManager = (DownloadManager)
	// context.getSystemService(context.DOWNLOAD_SERVICE);
	// downloadManager.enqueue(request);
	// }
}
