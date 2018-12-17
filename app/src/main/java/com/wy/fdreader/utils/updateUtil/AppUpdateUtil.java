package com.wy.fdreader.utils.updateUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.wy.fdreader.utils.Constant;
import com.wy.fdreader.activity.MainActivity;
import com.wy.fdreader.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 自动更新
 * 
 * @author wy
 * @date 2018年5月9日上午10:08:00
 * @return
 */
public class AppUpdateUtil {
	private static InputStream is;
	private static Context context;
	private Activity activity;
	private boolean autoPopup = false;//是否自动弹出更新提示窗口
	private UpdateInfo info = new UpdateInfo();
	private static ProgressDialog pd;    //进度条对话框  
	private Handler newVerHandler = null;
	
	public AppUpdateUtil(Context context,boolean autoPopup){
		this.context = context;
		this.autoPopup = autoPopup;
		try {
			this.activity = (Activity) context;
		} catch (Exception e) {
			//说明是ApplicationContext
			e.printStackTrace();
		}
	}
	Handler upHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.Flag.UPDATE_CLIENT:
				// 弹出对话框通知用户更新
				if (activity instanceof MainActivity) {//判断传递过来是哪个activity
					showUpdateDialog();
				}else if(autoPopup){
					showUpdateDialog();
				}else {//不弹出更新提示窗口
					Message newMsg = newVerHandler.obtainMessage();
					newMsg.what = Constant.Flag.UPDATE_CLIENT;
					newVerHandler.sendMessage(newMsg);
				}
				break;
			case Constant.Flag.NOT_UPDATE:
				//不更新
				Log.i("CURRENT_VERSION", info.getVersionName());
				break;
			case Constant.Flag.DOWN_ERROR:
				Toast.makeText(context, context.getString(R.string.update_faild), Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};
	public static Handler downHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.Flag.DOWN_OK:
				pd.dismiss();
				int rate = pd.getProgress();
                //判断文件是否下载成功并且正确
				File apkFile = (File) msg.obj;
                if (rate != 100 || apkFile.length()<=0) {
					return;
				}
                //退出APP
                
                installApk(apkFile);  
                pd.dismiss(); //结束掉进度条对话框  
				break;

			default:
				break;
			}
		};
	};
	public void setNewVerHandler(Handler newVerHandler) {
		this.newVerHandler = newVerHandler;
	}
	public void UpdateCheck() throws Exception {
		// TODO Auto-generated method stub
		final String xmlPath = this.context.getResources().getString(R.string.xml_url);
		final String versionCode = getVersionInfo()[0];
		final String versionName = getVersionInfo()[1];
		new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Request request = new Request.Builder().url(xmlPath).build();//创建请求
	    		new OkHttpClient().newCall(request).enqueue(new Callback() {
					
					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						// TODO Auto-generated method stub
						try {
							InputStream is = null;
							is = response.body().byteStream();//获取数据流
							//解析xml文件
							info = getUpdateInfo(is);
							Message msg = upHandler.obtainMessage();
							if (info.getVersionCode().equals(versionCode)) {
								Log.i("TAG", "版本号相同无需升级");
								msg.what = Constant.Flag.NOT_UPDATE;
								upHandler.sendMessage(msg);
							} else {
								Log.i("TAG", "版本号不同 ,提示用户升级 ");
								msg.what = Constant.Flag.UPDATE_CLIENT;
								upHandler.sendMessage(msg);
							}
							is.close();
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

			}
		}.run();
	}
	
	/**
	 * 解析XML文件
	 */
	public UpdateInfo getUpdateInfo(InputStream in) {
		// TODO Auto-generated method stub
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(in, "utf-8");// 设置解析数据源
			int type = parser.getEventType();// 事件类型
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:// 开始
					String nodeName = parser.getName();// 节点名称
					if ("versionCode".equals(nodeName)) {
						info.setVersionCode(parser.nextText());// 获取版本号
					} else if ("versionName".equals(nodeName)) {
						info.setVersionName(parser.nextText());//获取版本名称
					} else if ("url".equals(nodeName)) {
						info.setUrl(parser.nextText());// 获取apk下载地址
					} else if ("description".equals(nodeName)) {
						info.setDescription(parser.nextText());//获取描述信息
					}
					break;

				default:
					break;
				}
				type = parser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}
	
	public String[] getVersionInfo() throws Exception {
		String[] versionInfo = new String[2];
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),
				0);
		versionInfo[0] = packInfo.versionCode+"";
		versionInfo[1] = packInfo.versionName;
		return versionInfo;
	}
	
	/**
	 * 提示更新
	 * @param context
	 */
	public void showUpdateDialog() {
		Builder builer = new Builder(context);
		builer.setTitle(context.getResources().getString(R.string.update_title));
		builer.setMessage(info.getDescription());
		builer.setCancelable(false);
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton(context.getResources().getString(R.string.update_Positive), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//dialog方式下载
//				downLoadApk();
				//通知栏下载
				Intent intentSer = new Intent(context,UpdateService.class);
				Bundle xmlData = new Bundle();
				xmlData.putString("apkUrl", info.getUrl());
				intentSer.putExtras(xmlData);
				context.startService(intentSer);
				dialog.dismiss();
			}
		});
		// 当点取消按钮时
		builer.setNegativeButton(context.getResources().getString(R.string.update_Negative), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}
	/**
	 * 下载apk
	 */
	public void downLoadApk() {  
	    pd = new ProgressDialog(context);  
	    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
	    pd.setMessage(context.getResources().getString(R.string.updating));
	    pd.incrementProgressBy(1);
	    pd.setCancelable(false);
	    pd.show();  
	    new Thread(){  
	        @Override  
	        public void run() {  
	            try {  
	            	DownLoadManager.setDownHandler(downHandler);
	                DownLoadManager.getFileFromServer(info.getUrl(), pd);
	            } catch (Exception e) {  
	                Message msg = upHandler.obtainMessage();  
	                msg.what = Constant.Flag.DOWN_ERROR;  
	                upHandler.sendMessage(msg);  
	                e.printStackTrace();  
	            }  
	        }
	       }.start();  
	}

	// 安装apk并打开
	public static PendingIntent installApk(File file) {
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
		context.startActivity(intent);
//		boolean isIntalled = isAppIntalled(context.getPackageName());
//		if (isIntalled) {
//			PackageManager pm = context.getPackageManager();
//            Intent newApp = pm.getLaunchIntentForPackage(context.getPackageName());
//            context.startActivity(newApp);
//		}else{
//            Toast.makeText(context,context.getString(R.string.intalled_error),Toast.LENGTH_SHORT).show();
//        }
		return pendingIntent;
	}

	/**
	 * 判断APP是否安装成功
	 * @param packageName
	 * @return
	 */
	private boolean isAppIntalled(String packageName) {

        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null){
            return true;
        }else{
            return false;
        }
    }
}
