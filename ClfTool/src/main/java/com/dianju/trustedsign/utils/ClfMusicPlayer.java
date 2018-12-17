package com.dianju.trustedsign.utils;

import java.io.File;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;

/**
 * 音乐播放器
 * @auther chenlf3
 * @date 2015年8月14日-上午11:29:34
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class ClfMusicPlayer {
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
	
	private int state;
	
	private MediaPlayer mediaPlayer;//播放器
	private AudioManager audioManager;//音量
	
	/**
	 * 初始化一些收尾工作
	 * @auther chenlf3
	 * @date 2015年8月14日 下午12:14:41
	 */
	void initTail() {
		if(mediaPlayer == null) return;
		/** 播放完毕后操作 */
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				operationAfterPlay();
			}
		});
		
		/** 播放途中遇到文件数据有损坏而停止的播放，恢复按钮为可用 */
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			public boolean onError(MediaPlayer mediaplayer, int i, int j) {
				operationAfterPlayError();
				return false;
			}
		});
	}
	
	/** 播放报错之后 */
	public void operationAfterPlayError() {
		mediaPlayer.reset();
		state = STOP;
	}
	
	/** 播放完毕之后执行操作 */
	public void operationAfterPlay() {
		mediaPlayer.reset();
		state = STOP;
	}
	
	/**
	 * 构造方法一
	 */
	public ClfMusicPlayer() {
		if(!this.checkSDCard()) return;
		/** 初始化播放器 */
		mediaPlayer = new MediaPlayer();
		this.state = this.STOP;
		initTail();
	}
	
	/**
	 * 播放音乐
	 * @auther chenlf3
	 * @date 2015年8月14日 下午12:17:55
	 * @param filePath 音乐文件路径
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void playMusic(String filePath) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
		if(this.state == this.PLAY) return;
		if(this.mediaPlayer == null) return;
		File file = new File(filePath);
		if(!file.exists()) {
			System.out.println("文件不存在!");
			return;
		}
		mediaPlayer.reset();//重置多媒体
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置播放类型
		mediaPlayer.setDataSource(filePath);//设置播放源文件
		mediaPlayer.prepare();
		mediaPlayer.start();
		this.state = this.PLAY;
	}
	
	/**
	 * 暂停音乐播放
	 * @auther chenlf3
	 * @date 2015年8月14日 下午1:51:34
	 */
	public void pauseMusic() {
		if(this.state == this.PAUSE) return;
		if(this.mediaPlayer != null && this.state == this.PLAY) {
			mediaPlayer.pause();
			this.state = this.PAUSE;
		}
	}
	
	/**
	 * 停止音乐播放
	 * @auther chenlf3
	 * @date 2015年8月14日 下午1:57:37
	 */
	public void stopMusic() {
		if(this.state == this.STOP) return;
		if(this.mediaPlayer != null && (this.state == this.PLAY || this.state == this.PAUSE)) {
			mediaPlayer.stop();
			mediaPlayer.reset();
			this.state = this.STOP;
		}
	}
	
	/**
	 * 音乐重播
	 * @auther chenlf3
	 * @date 2015年8月14日 下午2:07:07
	 */
	public void replay() {
		if(this.mediaPlayer != null && (this.state == this.PLAY || this.state == this.PAUSE)) {
			mediaPlayer.seekTo(0);
			this.state = this.PLAY;
		}
	}
	
	/**
	 * 释放资源
	 * @auther chenlf3
	 * @date 2015年8月14日 下午2:01:13
	 */
	public void releaseRes() {
		if(mediaPlayer != null) {
			stopMusic();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	
	/**
	 * 撤销暂停，继续播放
	 * @auther chenlf3
	 * @date 2015年8月14日 下午1:54:22
	 */
	public void revokePauseMusic() {
		if(this.mediaPlayer != null && this.state == this.PAUSE) {
			mediaPlayer.start();
			this.state = this.PLAY;
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

	public int getState() {
		return state;
	}
	
}
