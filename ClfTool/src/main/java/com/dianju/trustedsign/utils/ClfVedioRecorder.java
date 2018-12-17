package com.dianju.trustedsign.utils;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * 录像机
 * @auther chenlf3
 * @date 2015年8月18日-上午10:21:06
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class ClfVedioRecorder {
	public static final String DEFAULT_STORE_PATH = Environment.getExternalStorageDirectory()+File.separator+"clf"+File.separator+"clfVedio";//视频保存路径
	/**
	 * 前置摄像头
	 */
	public static final int CAMERA_FACING_FRONT = CameraInfo.CAMERA_FACING_FRONT;
	/**
	 * 后置摄像头
	 */
	public static final int CAMERA_FACING_BACK = CameraInfo.CAMERA_FACING_BACK;
	private MediaRecorder mediaRecorder;
	public static final int STOP = 0;//停止状态
	public static final int RECORD = 1;//正在录制状态
	public static final int PAUSED = 2;//暂停状态
	private CameraPreView preview;
	private String fileName = null;//录音后保存的文件名称
	private String storePath = null;
	private Camera camera;//照相机
	private int position;//当前摄像头
	private Context context;
	private int state;
	
	/**
	 * 初始化一些收尾工作
	 * @auther chenlf3
	 * @date 2015年8月14日 下午12:14:17
	 */
	void initTail() {
		/** 录音中途遇到错误，处理方案 */
		mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
			@Override
			public void onError(MediaRecorder mediarecorder, int i, int j) {
				operationAfterRecorderError();
			}
		});
	}
	
	/** 录制中途错误，处理方案，供重写调用 */
	public void operationAfterRecorderError() {
		state = STOP;
	}

	/**
	 * 构造方法一
	 */
	public ClfVedioRecorder(Context context,int position) {
		/** 判断sd卡是否可用 */
		if(!this.checkSDCard()) return;
		/** 创建文件夹 */
		this.storePath = this.DEFAULT_STORE_PATH;
		File file = new File(this.storePath);
		if(!file.exists()) {
			file.mkdirs();
		}
		this.position = position;
		this.context = context;
		this.state = this.STOP;
		this.mediaRecorder = new MediaRecorder();
		initTail();
		if(checkCameraHardware(context)) {
			/** 获取相机 */
			camera = getCameraInstance();
			/** 相机调整旋转度 */
			camera.setDisplayOrientation(this.getCameraDisplayOrientation());
			/** 创建一个预览界面 */
			preview = new CameraPreView(context);
		}
	}
	
	public ClfVedioRecorder(Context context,int position,String storePath) {
		this(context, position);
		this.storePath = storePath;
	}
	
	/**
	 * 开始录制
	 * @auther chenlf3
	 * @date 2015年8月18日 上午11:35:54
	 */
	public void record() {
		if(this.state == this.RECORD) return;
		if(this.mediaRecorder == null) {
			this.mediaRecorder = new MediaRecorder();
		} else {
			this.mediaRecorder.reset();
		}
		/*2.2以下版本设置方法
		 * camera.unlock();
		*//** 设置预览 *//*
		this.mediaRecorder.setPreviewDisplay(this.preview.mHolder.getSurface());
		*//** 设置相机 *//*
		this.mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		*//** 设置声音源为话筒 *//*
		this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		 *//** ②设置錄製視頻输出格式：THREE_GPP/MPEG-4/RAW_AMR/Default
		 * THREE_GPP(3gp格式，H263视频ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB) *//*
		this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		*//**  ②设置視頻/音频文件的编码：AAC/AMR_NB/AMR_MB/Default *//*
		this.mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		 *//** ②设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错  *//*
		this.mediaRecorder.setVideoSize(176, 144);
		 *//** ②设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错  *//*
		this.mediaRecorder.setVideoFrameRate(15); 
		 *//** ②设置输出文件的路径   *//*
		this.fileName = this.storePath + File.separator + "aaa.3gp";
		this.mediaRecorder.setOutputFile(this.fileName);
		this.mediaRecorder.start();*/
		/** 解锁摄像头 */
		camera.unlock();
		/** 给记录器设置摄像头 */
		this.mediaRecorder.setCamera(camera);
		/** 设置声音源为话筒 */
		this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		/** 设置视频源 */
		this.mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		/** 设置输出格式和编码;2.2以上版本 */
		this.mediaRecorder.setProfile(CamcorderProfile.get(this.position,CamcorderProfile.QUALITY_HIGH));
		this.fileName = this.storePath + File.separator + System.currentTimeMillis()+".3gp";
		/** 设置文件输出路径 */
		this.mediaRecorder.setOutputFile(this.fileName);
		/** 设置预览界面 */
		this.mediaRecorder.setPreviewDisplay(preview.mHolder.getSurface());
		try {
			this.mediaRecorder.prepare();
			this.mediaRecorder.start();
			this.state = this.RECORD;
			Toast.makeText(context, "正在录制", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 停止录音
	 * @auther chenlf3
	 * @date 2015年8月19日 上午10:39:03
	 */
	public void stop() {
		if(this.state == this.STOP) return;
		if(this.mediaRecorder != null) {
			this.mediaRecorder.stop();
			this.state = this.STOP;
			Toast.makeText(context, "录制完毕", Toast.LENGTH_LONG).show();
			try {
				/** 重新打开预览界面 */
				camera.reconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void releaseRes() {
		if(this.state == this.RECORD) {
			this.stop();
		}
		if(mediaRecorder != null) {
			this.mediaRecorder.release();
			this.mediaRecorder = null;
		}
		if(this.camera != null) {
			this.camera.release();
			this.camera = null;
		}
	}
	
	/**
	 * 内部类，摄像机预览界面
	 * @auther chenlf3
	 * @date 2015年8月18日-上午11:40:46
	 * Copyright (c) 2015点聚信息技术有限公司-版权所有
	 */
	private class CameraPreView extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		
		public CameraPreView(Context context) {
			super(context);
			this.mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 設置顯示器類型，setType必须设置
		}
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if(mHolder.getSurface() == null) return;
			try {
				/** 在容器大小变化的时候先停止预览 */
				camera.stopPreview();
				/** 然后再开启预览 */
				camera.setPreviewDisplay(mHolder);
				camera.startPreview();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				/** 摄像机将摄到的内容装入容器中，供用户预览 */
				camera.setPreviewDisplay(holder);
				/** 开启预览画面 */
				camera.startPreview();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			
		}
	}

	public CameraPreView getPreview() {
		return preview;
	}
	
	/**
	 * 检查SD卡是否可用
	 * @auther chenlf3
	 * @date 2015年8月6日 上午9:45:37
	 * @return
	 */
	private boolean checkSDCard() {
		boolean res = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		if(!res) {
			System.out.println("SD卡不可用！");
		}
		return res;
	}
	
	/**
	 * 检查手机是否存在摄像头
	 * @auther chenlf3
	 * @date 2015年8月6日 上午9:44:19
	 * @param context
	 * @return
	 */
	public boolean checkCameraHardware(Context context) {
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			//this device has a camera
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取一个摄像机
	 * @auther chenlf3
	 * @date 2015年8月6日 上午9:44:41
	 * @return
	 */
	public Camera getCameraInstance() {
		Camera camera = null;
		try {
			if(position == 1) {//前摄像头
				camera = Camera.open(1);
			} else {
				camera = Camera.open();//默认获取后摄像头
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return camera;
	}
	
	/**
	 * 计算相机应旋转的角度
	 * @auther chenlf3
	 * @date 2015年8月18日 上午11:04:56
	 * @return
	 */
	private int getCameraDisplayOrientation() {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(this.position, info);
	     int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     return result;
	}
	
	/**
	 * 切换摄像头,必须在停止录制状态才可以切换
	 * @auther chenlf3
	 * @date 2015年8月19日 上午10:31:55
	 */
	public void switchCamera() {
		if(this.state != this.STOP) return;
		if(this.position == ClfVedioRecorder.CAMERA_FACING_BACK) {
			this.position = ClfVedioRecorder.CAMERA_FACING_FRONT;
		} else {
			this.position = ClfVedioRecorder.CAMERA_FACING_BACK;
		}
		if(this.camera != null) {
			this.camera.release();
			this.camera = null;
		}
		if(checkCameraHardware(context) && this.camera == null) {
			/** 获取相机 */
			camera = getCameraInstance();
			/** 相机调整旋转度 */
			camera.setDisplayOrientation(this.getCameraDisplayOrientation());
			/** 创建一个预览界面 */
			preview = new CameraPreView(context);
		}
		
	}

	public int getState() {
		return state;
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
