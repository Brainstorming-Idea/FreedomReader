package com.dianju.trustedsign.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * 自定义照相机
 * @auther chenlf3
 * @date 2015年8月6日-上午9:43:31
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class ClfCamera {

	/**
	 * 前置摄像头
	 */
	public static final int CAMERA_FACING_FRONT = CameraInfo.CAMERA_FACING_FRONT;
	/**
	 * 后置摄像头
	 */
	public static final int CAMERA_FACING_BACK = CameraInfo.CAMERA_FACING_BACK;
	private int position;//摄像头位置
	private Camera camera;//相机
	private CameraPreView preview;//预览界面
	private String fileName = null;//拍照后保存的文件名称
	private String storePath = null;//文件的存储路径
	public static final String DEFAULT_STORE_PATH = Environment.getExternalStorageDirectory()+File.separator+"clf"+File.separator+"clfCamera";//默认图片保存路径
	private Context context;
	/**
	 * 构造方法一
	 * @param context 上下文
	 * @param position 摄像头位置
	 */
	public ClfCamera(Context context, int position) {
		this.position = position;
		this.storePath = DEFAULT_STORE_PATH;
		this.context = context;
		if(checkCameraHardware(context)) {
			/** 获取相机 */
			camera = getCameraInstance();
			/** 相机调整旋转度 */
			camera.setDisplayOrientation(this.getCameraDisplayOrientation());
			/** 创建一个预览界面 */
			preview = new CameraPreView(context);
		}
	}
	
	/**
	 * 构造方法二
	 * @param context 上下文
	 * @param position 摄像头位置
	 * @param storePath 存储位置
	 */
	public ClfCamera(Context context, int position, String storePath) {
		this.position = position;
		if(storePath == null || "".equals(storePath)) {
			this.storePath = DEFAULT_STORE_PATH;
		} else {
			this.storePath = storePath;
		}
		this.context = context;
		if(checkCameraHardware(context)) {
			/** 获取相机 */
			camera = getCameraInstance();
			/** 相机调整旋转度 */
			camera.setDisplayOrientation(this.getCameraDisplayOrientation());
			/** 创建一个预览界面 */
			preview = new CameraPreView(context);
		}
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
	
	public CameraPreView getPreview() {
		return preview;
	}

	public String getFileName() {
		return fileName;
	}

	public String getStorePath() {
		return storePath;
	}

	/**
	 * 拍照
	 * @auther chenlf3
	 * @date 2015年8月6日 上午9:45:03
	 */
	public void takePic() {
		if(this.camera == null) {
			System.out.println("camera=null");
			return;
		}
		/** 拍照 */
		camera.takePicture(null, null, new MyPictureCallback());
	}
	
	/**
	 * 聚焦拍照
	 * @auther chenlf3
	 * @date 2015年8月6日 上午9:45:18
	 */
	public void takeFocusPic() {
		if(this.camera == null) {
			System.out.println("camera=null");
			return;
		}
		/** 聚焦 */
		camera.autoFocus(new MyAutoFocusCallback());
	}
	
	public void switchCamera() {
		if(this.position == this.CAMERA_FACING_FRONT) {
			this.position = this.CAMERA_FACING_BACK;
		} else {
			this.position = this.CAMERA_FACING_FRONT;
		}
		this.releaseRes();
		/** 获取相机 */
		camera = getCameraInstance();
		/** 相机旋转度 */
		camera.setDisplayOrientation(this.getCameraDisplayOrientation());
		/** 创建一个预览界面 */
		preview = new CameraPreView(context);
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
	 * 释放资源
	 * @auther chenlf3
	 * @date 2015年8月6日 上午9:45:48
	 */
	public void releaseRes() {
		if(camera != null) {
			camera.release();
			camera = null;
		}
		if(this.preview != null) {
			this.preview = null;
		}
	}
	
	/**
	 * 拍照完毕之后要进行的一些操作
	 * @auther chenlf3
	 * @date 2015年8月6日 上午9:46:00
	 */
	public void operationAfterCamera() {}
	
	/**
	 * @description 
	 * @auther chenlf3
	 * @date 2015年8月6日-上午9:46:14
	 * Copyright (c) 2015点聚信息技术有限公司-版权所有
	 */
	private class MyAutoFocusCallback implements AutoFocusCallback {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			/** 由于直接拍照不清晰，我们可以让自动聚焦之后再拍照 */
			camera.takePicture(null, null, new MyPictureCallback());
			/** 拍完照之后界面就停留在了拍照界面，如果还想继续拍照，执行下面代码 */
			//mCamera.startPreview();
		}
		
	}
	
	/**
	 * 定义接口，此接口供成功拍照之后会进行回调
	 * @auther chenlf3
	 * @date 2015年8月6日-上午9:46:36
	 * Copyright (c) 2015点聚信息技术有限公司-版权所有
	 */
	private class MyPictureCallback implements PictureCallback {
		
		public void onPictureTaken(byte[] data, Camera camera) {
			if(!checkSDCard()) {
				return;
			}
			File file = new File(storePath);
			if(!file.exists()) {
				file.mkdirs();
			}
			fileName = System.currentTimeMillis()+".jpg";
			File pictureFile = new File(storePath,fileName);
			try {
				OutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				/** 存储照片完成后弹出提示吐司 */
				Toast.makeText(context, fileName, Toast.LENGTH_SHORT).show();
				/** 拍完照之后界面就停留在了拍照界面，如果还想继续拍照，执行下面代码 */
				camera.startPreview();
				operationAfterCamera();//拍完照片之后进行的一些操作
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 预览界面
	 * @auther chenlf3
	 * @date 2015年8月6日-上午9:46:48
	 * Copyright (c) 2015点聚信息技术有限公司-版权所有
	 */
	private class CameraPreView extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		
		public CameraPreView(Context context) {
			super(context);
			this.mHolder = getHolder();
			mHolder.addCallback(this);
		}
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			/*if(mHolder.getSurface() == null) return;
			try {
				*//** 在容器大小变化的时候先停止预览 *//*
				camera.stopPreview();
				*//** 然后再开启预览 *//*
				camera.setPreviewDisplay(mHolder);
				camera.startPreview();
			} catch(Exception e) {
				e.printStackTrace();
			}*/
		}

		@Override/** 在home之后再进入程序，会调用该方法，将新的holder重新设置下 */
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
	
}
