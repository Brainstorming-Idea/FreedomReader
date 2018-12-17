package com.dianju.trustedsign.utils;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

/**
 * 需要创建对象的常用工具类
 * 利用科大讯飞对汉字进行发声功能 OnInitListener
 * @auther chenlf3
 * @date 2016年3月24日-上午11:13:50
 * Copyright (c) 2016点聚信息技术有限公司-版权所有
 */
public class ClfCommonUtil implements OnInitListener {

	private Context context;
	private TextToSpeech textToSpeech;//语音功能
	
	public ClfCommonUtil() {
		super();
	}
	
	public ClfCommonUtil(Context context) {
		this();
		this.context = context;
		textToSpeech = new TextToSpeech(context, this);
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * OnInitListener 实现对汉字发音
	 */
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.CHINA);  
            /*if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {  
                Toast.makeText(context, "数据丢失或不支持", Toast.LENGTH_SHORT).show();  
            }*/
            
        }
	}
	
	/**
	 * 对汉字字符串进行发音
	 * @auther chenlf3
	 * @date 2016年3月24日 上午11:21:14
	 * @param voiceStr
	 */
	public void playVoice(String voiceStr) {
		textToSpeech.speak(voiceStr,TextToSpeech.QUEUE_FLUSH, null);
	}
}
