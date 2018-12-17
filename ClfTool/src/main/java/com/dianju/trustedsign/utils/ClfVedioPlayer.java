package com.dianju.trustedsign.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Environment;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 视频播放器
 * @auther chenlf3
 * @date 2015年8月14日-上午11:29:34
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class ClfVedioPlayer {
	/**
	 * 播放状态
	 */
	public static final int PLAY = 1;
	/**
	 * 暂停状态
	 */
	public static final int PAUSE = 0;
	/**
	 * 停止状态
	 */
	public static final int STOP = 2;
	/**
	 * 播放状态
	 */
	private int state;
	private MediaPlayer mediaPlayer;//播放器
	private VedioView vedioView;//画面
	private int currentPoint;//断点
	private String fileName;//要播放的文件
	
	/**
	 * 初始化一些收尾工作
	 * @auther chenlf3
	 * @date 2015年8月14日 下午12:14:41
	 */
	private void initTail() {
		/** 为mediaPlayer添加监听事件，准备好后执行如下内容 */
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				/** 设置Video影片以SurfaceHolder播放 */
				mediaPlayer.setDisplay(vedioView.mHolder);
				mediaPlayer.start();
				mediaPlayer.seekTo(currentPoint);
			}
		});
		
		/** 播放完后回复播放按钮为可用 */
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {//播放完毕后回调用此方法
				operationAfterPlay();
			}
		});
		
		/** 播放中途遇到文件数据有损坏而停止的播放，恢复按钮为可用 */
		mediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {//播放时播放器挂掉会调用此方法
				operationAfterPlayError();
				return false;
			}
		});
	}
	
	/** 播放报错之后 */
	public void operationAfterPlayError() {
		stopVedio();
	}
	
	/** 播放完毕之后执行操作 */
	public void operationAfterPlay() {
		currentPoint = 0;
		stopVedio();
	}
	
	/**
	 * 构造方法一
	 */
	public ClfVedioPlayer(Context context) {
		if(!this.checkSDCard()) return;
		/** 初始化播放器 */
		this.vedioView = new VedioView(context);
		this.currentPoint = 0;
		this.state = this.STOP;
	}
	
	/**
	 * 播放某个视频
	 * @auther chenlf3
	 * @date 2015年8月14日 下午4:38:50
	 * @param fileName
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void playVedio(String fileName) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
		if(this.state == this.PLAY) return;
		if(TextUtils.isEmpty(fileName)) {
			System.out.println("无效文件！");
			return;
		}
		File file = new File(fileName);
		if(!file.exists()) {
			System.out.println("无效文件！");
			return;
		}
		this.fileName = fileName;
		if(mediaPlayer == null) {
			this.mediaPlayer = new MediaPlayer();
			initTail();
		} else {
			mediaPlayer.reset();
		}
		/** 设置播放类型 */
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		/** 设置要播放的文件 */
		mediaPlayer.setDataSource(fileName);
		mediaPlayer.prepareAsync();//定为异步准备，因为有些大
		this.state = this.PLAY;
	}
	
	/**
	 * 将视频跳转到某块
	 * @auther chenlf3
	 * @date 2015年8月17日 下午4:10:56
	 * @param currentPoint 跳转到的位置
	 */
	public void playVedio(int currentPoint) {
		//int max = mediaPlayer.getDuration();//获取视频最大帧数
		if(mediaPlayer!=null) {
			this.currentPoint = currentPoint;
			if(this.state == this.PLAY) {
				mediaPlayer.seekTo(currentPoint);
			} else if(this.state == this.PAUSE) {
				this.remokePauseVidio();
				mediaPlayer.seekTo(currentPoint);
			} else if(this.state == this.STOP) {
				try {
					this.playVedio(this.fileName);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 重新播放
	 * @auther chenlf3
	 * @date 2015年8月17日 下午4:08:59
	 */
	public void replayVedio() {
		if(mediaPlayer!=null) {
			this.currentPoint = 0;
			if(this.state == this.PLAY) {
				mediaPlayer.seekTo(currentPoint);
			} else if(this.state == this.PAUSE) {
				this.remokePauseVidio();
				mediaPlayer.seekTo(currentPoint);
			} else if(this.state == this.STOP) {
				try {
					this.playVedio(this.fileName);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 暂停视频播放
	 * @auther chenlf3
	 * @date 2015年8月17日 下午12:21:54
	 */
	public void pauseVedio() {
		if(this.state == this.PAUSE) return;
		if(this.mediaPlayer != null || this.state == this.PLAY) {
			mediaPlayer.pause();
			this.state = this.PAUSE;
		}
	}
	
	/**
	 * 将处于暂停状态的视频继续播放
	 * @auther chenlf3
	 * @date 2015年8月17日 下午12:24:16
	 */
	public void remokePauseVidio() {
		if(this.mediaPlayer != null || this.state == this.PAUSE) {
			mediaPlayer.start();
			this.state = this.PLAY;
		}
	}
	
	public void stopVedio() {
		if(this.mediaPlayer != null && this.state != this.STOP) {
			mediaPlayer.stop();
			this.state = this.STOP;
		}
	}
	
	/**
	 * 检查SD卡是否可用
	 * @auther chenlf3
	 * @date 2015年8月14日 下午12:03:06
	 * @return
	 */
	private boolean checkSDCard() {
		boolean res = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		if(!res) {
			System.out.println("SD卡不可用！");
		}
		return res;
	}
	
	private class VedioView extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		
		public VedioView(Context context) {
			super(context);
			this.mHolder = getHolder();
			/** 下面设置surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前，4.0以下系统加上下面一行代码 */
			this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mHolder.addCallback(this);
		}
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				if(currentPoint>0) {
					playVedio(fileName);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if(mediaPlayer != null) {
				currentPoint = mediaPlayer.getCurrentPosition();
			}
			stopVedio();
		}
		
	}
	
	public void releaseRes() {
		if(this.mediaPlayer != null && this.state != this.STOP) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			this.state = this.STOP;
		} else {
			if(mediaPlayer!=null) {
				mediaPlayer.release();
				mediaPlayer=null;
			}
		}
	}

	/**
	 * 返回视频界面
	 * @auther chenlf3
	 * @date 2015年8月17日 下午3:37:44
	 * @return
	 */
	public VedioView getVedioView() {
		return vedioView;
	}
	
}
