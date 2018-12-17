package com.dianju.trustedsign.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 录音机
 * @auther chenlf3
 * @date 2015年8月11日-下午2:35:36
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class ClfMusicRecorder {
	private MediaRecorder mediaRecorder;
	public static final int STOP = 0;//停止状态
	public static final int RECORD = 1;//正在录制状态
	public static final int PAUSED = 2;//暂停状态
	private int state = 0;
	private List<String> filePaths;//多次暂停的文件名称
	private String filePath;//录音文件的名字
	public static final String DEFAULT_STORE_PATH = Environment.getExternalStorageDirectory()+File.separator+"dianju"+File.separator+"voice";//音频保存路径
	private boolean isUsePause = false;//默认不适用暂停功能
	//private String fileName = null;//录音后保存的文件名称
	//private String storePath = null;
	//private Context context;
	
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
	
	public ClfMusicRecorder() {
		if(!this.checkSDCard()) return;
		this.isUsePause = false;
		filePaths = new ArrayList<String>();
		state = STOP;
		/** 初始化一个录音机 */
		mediaRecorder = new MediaRecorder();
		this.initTail();
	}
	
	public ClfMusicRecorder(boolean isUsePause) {
		if(!this.checkSDCard()) return;
		this.isUsePause = isUsePause;
		filePaths = new ArrayList<String>();
		state = STOP;
		/** 初始化一个录音机 */
		mediaRecorder = new MediaRecorder();
		this.initTail();
	}
	
	public void start(String filePath) throws IllegalStateException, IOException {
		if(this.state != STOP) return;
		if(mediaRecorder == null) {
			mediaRecorder = new MediaRecorder();
		} else {
			mediaRecorder.reset();
		}
		//设置音频源为MIC
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		//设置输入格式
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		//设置编码格式
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		if(isUsePause) {
			this.filePath = filePath;
			//生成一个文件名字
			String tempFilePath = DEFAULT_STORE_PATH+File.separator+System.currentTimeMillis()+".amr";
			filePaths.add(tempFilePath);
			mediaRecorder.setOutputFile(tempFilePath);//设置保存路径
		} else {
			mediaRecorder.setOutputFile(filePath);//设置保存路径
		}
		mediaRecorder.prepare();
		mediaRecorder.start();
		this.state = this.RECORD;
		Log.i("clfutil", "开始录音");
	}
	
	/**
	 * 继续录音
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @auther chenlf3
	 * @date 2015年8月11日 下午3:10:39
	 */
	public void unPause() throws IllegalStateException, IOException {
		if((!this.isUsePause) || (this.state != this.PAUSED)) return;
		if(mediaRecorder == null) {
			mediaRecorder = new MediaRecorder();
		} else {
			mediaRecorder.reset();
		}
		//生成一个文件名字
		String tempFilePath = DEFAULT_STORE_PATH+File.separator+System.currentTimeMillis()+".amr";
		this.filePaths.add(tempFilePath);
		//设置音频源为MIC
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		//设置输入格式
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		//设置编码格式
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mediaRecorder.setOutputFile(tempFilePath);//设置保存路径
		mediaRecorder.prepare();
		mediaRecorder.start();
		this.state = this.RECORD;
		Log.i("clfutil", "继续录音");
	}
	
	/*public void record(String fileName) throws IllegalStateException, IOException {
		if(this.state != this.STOP) return;
		if(mediaRecorder == null) {
			mediaRecorder = new MediaRecorder();
		} else {
			mediaRecorder.reset();
		}
		//生成一个文件名字
		this.fileName = fileName;
		String tempFileName = System.currentTimeMillis()+".amr";
		fileNames.add(tempFileName);
		//设置音频源为MIC
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		//设置输入格式
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		//设置编码格式
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mediaRecorder.setOutputFile(this.storePath+File.separator+tempFileName);//设置保存路径
		mediaRecorder.prepare();
		mediaRecorder.start();
		this.state = this.RECORD;
		Log.i("clfutil", "开始录音");
	}*/
	
	/**
	 * 暂停录音
	 * @auther chenlf3
	 * @date 2015年8月11日 下午4:01:08
	 */
	public void pause() {
		if(this.state == this.PAUSED) return;
		if(mediaRecorder == null) {
			Log.e("clfutil", "录音机未初始化！");
			return;
		}
		/** 只有录音状态才可以进行暂停操作 */
		if(this.state == this.RECORD) {
			mediaRecorder.stop();
			mediaRecorder.reset();
			this.state = this.PAUSED;
			Log.i("clfutil", "暂停录音");
		}
	}
	
	/**
	 * 停止录音
	 * @throws IOException 
	 * @auther chenlf3
	 * @date 2015年8月11日 下午3:38:21
	 */
	public void stop() throws IOException {
		if(this.state == this.STOP) return;
		if(mediaRecorder == null) {
			Log.e("clfutil", "录音机未初始化！");
			return;
		}
		if(this.state == this.RECORD) {
			mediaRecorder.stop();
			mediaRecorder.reset();
		}
		this.state = this.STOP;
		if(isUsePause) {
			if(filePaths.size() <= 0) {
				Log.i("clfutil", "没有发现录音内容！");
				return;
			}
			/** 调用拼接接口 */
			this.mergeMusicFile(filePaths, this.filePath);
			/** 拼接完成之后进行删除 */
			for(String path:filePaths) {
				this.delFile(path);
			}
		}
		Log.i("clfutil", "录音完毕-停止");
	}
	
	/**
	 * 音频文件拼接(list里面为暂停录音所产生的几段录音文件的名字，中间几段文件的减去前面的6个字节头文件)
	 * @auther chenlf3
	 * @date 2015年8月11日 下午5:29:45
	 * @param filePaths
	 * @param finalFilePath
	 * @return
	 * @throws IOException 
	 */
	private boolean mergeMusicFile(List<String> filePaths, String finalFilePath) {
		if(filePaths == null || filePaths.size() <= 0) {
			System.out.println("filePaths为空！");
			return false;
		}
		/** 检验文件是否全都存在 */
		for(int i=0;i<filePaths.size();i++) {
			File file = new File(filePaths.get(i));
			if(!file.exists()) {
				System.out.println(filePaths.get(i)+"文件不存在！");
				return false;
			}
		}
		OutputStream os = null;
		RandomAccessFile raf = null;
		try {
			os = new FileOutputStream(finalFilePath);
			for(int i=0;i<filePaths.size();i++) {
				raf = new RandomAccessFile(filePaths.get(i), "r");
				if(i!=0) {
					raf.seek(6);
				}
				byte[] buffer = new byte[1024*8];
				int len = 0;
				while((len=raf.read(buffer))!=-1) {
					os.write(buffer,0,len);
				}
			}
			raf.close();
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if(os != null) {
					os.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除文件
	 * @auther chenlf3
	 * @date 2015年8月11日 下午6:13:57
	 * @param path
	 */
	public void delFile(String path) {
		File file = new File(path);
		if(file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * 释放资源
	 * @auther chenlf3
	 * @date 2015年8月11日 下午3:06:48
	 */
	public void releaseRes() {
		if(mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}
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

	public int getState() {
		return state;
	}
	
}
