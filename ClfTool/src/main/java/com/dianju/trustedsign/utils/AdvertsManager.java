package com.dianju.trustedsign.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * 图片轮播
 * @auther chenlf3
 * @date 2015年9月4日-下午5:45:36
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class AdvertsManager {
	
	private Context context;
	private ImageView imageView;//用来显示图片
	private ImageView tempImageView;//用来显示动画效果
	private List<ImageView> icons;//用来保存小圆点
	private String filePath;
	private String[] fileNames;//名字
	private int currentPoint = 0;//当前的显示位置
	private GestureDetector gestureDetector;//滑屏对象
	private boolean isCycle = false;
	private Handler handler;//用来显示下一张图片
	private Integer iconUnChecked = null;//未选定图标
	private Integer iconChecked = null;//选定图标
	public static final int LEFT_OUT = 0;//动画左出
	public static final int RIGHT_OUT = 1;//动画右出
	public static final int LEFT_IN = 2;//动画左进
	public static final int RIGHT_IN = 3;//动画右进
	public static final int ANIM_TIME = 500;//动画播放时长
	private CycleThread cycleThread;//控制图片的轮播
	private int cycleTime = 5000;//广告轮播时间
	
	
	/**
	 * 不带图标，路径为assets下路径
	 * @param context
	 * @param filePath 图片存放路径
	 * @param isCycle 是否自动轮播
	 * @throws MyException
	 */
	public AdvertsManager(Context context,String filePath,boolean isCycle) {
		this.context = context;
		this.filePath = filePath;
		this.isCycle = isCycle;
		initView();
		if(isCycle) {
			initThread();
		}
	}
	
	/**
	 * 带有图标，路径为assets下路径
	 * @param context
	 * @param filePath
	 * @param iconUnChecked 未选定的图标样式
	 * @param iconChecked 选定的图标样式
	 * @param isCycle 是否自动轮播
	 * @throws MyException
	 */
	public AdvertsManager(Context context,String filePath,int iconUnChecked, int iconChecked, boolean isCycle) {
		this.context = context;
		this.filePath = filePath;
		this.isCycle = isCycle;
		this.iconUnChecked = iconUnChecked;
		this.iconChecked = iconChecked;
		initView();
		initIcon();
		if(isCycle) {
			initThread();
		}
	}
	
	/** 基本初始化操作 */
	private void initView() {
		/** 声明检测手势事件 */
		gestureDetector = new GestureDetector(new MyOnGestureListener());
		/** 初始化ImageView */
		imageView = new ImageView(context) {
			/** 触屏事件，调用滑屏事件进行处理 */
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		/** 为view绑定触屏，停止自动播放的效果 */
		imageView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(isCycle && cycleThread != null && event.getAction() == MotionEvent.ACTION_DOWN) {
					cycleThread.stopThread();
					cycleThread = null;
				}
				if(isCycle && cycleThread == null && event.getAction() == MotionEvent.ACTION_UP) {
					cycleThread = new CycleThread();
					cycleThread.start();
				}
				return false;//返回false，滑动效果才会接受再处理
			}
		});
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		tempImageView = new ImageView(context);
		tempImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		tempImageView.setVisibility(View.GONE);
		try {
			/** 获取资源 */
			fileNames = context.getAssets().list(filePath);
			/** 设定小圆点 */
			this.setDoll(currentPoint);
			/** 设定背景图片 */
			this.setResource(currentPoint);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("AdvertsManager", "获取广告资源错误!");
		}
	}
	
	/** 初始化图标(用来标记当前显示到第几张的小圆点) */
	private void initIcon() {
		if(iconChecked != null && iconUnChecked!=null && fileNames != null && fileNames.length > 0) {
			icons = new ArrayList<ImageView>();
			for(int i=0;i<fileNames.length;i++) {
				ImageView dot = new ImageView(context);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 10, 0);
				dot.setLayoutParams(params);
				if(i == 0) {
					dot.setImageResource(iconChecked);
				} else {
					dot.setImageResource(iconUnChecked);
				}
				icons.add(dot);
			}
		}
	}
	
	/** 带自动循环的线程初始化操作 */
	private void initThread() {
		this.isCycle = true;
		/** 初始化handler */
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				showNextResourceWithCycle();
			};
		};
		/**创建线程 */
		cycleThread = new CycleThread();
		cycleThread.start();
	}
	
	/**
	 * 为imageview设定背景图片
	 * @auther chenlf3
	 * @date 2015年9月4日 下午2:13:31
	 * @param filePath
	 * @throws IOException
	 */
	private void setResource(int point) {
		if(fileNames != null && fileNames.length>0) {
			try {
				/** 设置要展示的图片 */
				this.imageView.setBackgroundDrawable(Drawable.createFromStream(context.getAssets().open(filePath+"/"+fileNames[point]), null));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("AdvertsManager", "资源不存在！");
			}
		}
	}
	
	/** 设定小圆点 */
	private void setDoll(int point) {
		/** 如果界面有初始化小圆点，设置选中的小圆点 */
		if(icons != null && icons.size()>0 && iconUnChecked != null && iconChecked != null) {
			for(int i=0;i<icons.size();i++) {
				if(i == point) {
					icons.get(i).setImageResource(iconChecked);
				} else {
					icons.get(i).setImageResource(iconUnChecked);
				}
			}
		}
	}
	
	/**
	 * 为演示动画的imageView设定资源，与setResource区别在于，设定资源后不改变小圆点显示位置
	 * @auther chenlf3
	 * @date 2015年12月31日 下午12:02:39
	 * @param point
	 */
	private void setTempResource(int point) {
		if(fileNames != null && fileNames.length>0) {
			try {
				/** 设置要展示的图片 */
				this.tempImageView.setBackgroundDrawable(Drawable.createFromStream(context.getAssets().open(filePath+"/"+fileNames[point]), null));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("AdvertsManager", "资源不存在！");
			}
		}
	}
	
	
	/**
	 * 显示下一张图片时动画效果
	 * @auther chenlf3
	 * @date 2015年12月31日 下午12:32:50
	 */
	private void nextResourceAnim() {
		/**
		 * 1.让imageView开启左出动画
		 * 2.让tempImageView显示，并开启左进动画
		 */
		Animation leftOutAnim = this.getAnim(this.LEFT_OUT);
		Animation leftInAnim = this.getAnim(this.LEFT_IN);
		/** 动画完毕之后的一些操作 */
		leftInAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				/** 动画完结之后，设定显示资源，并清理imageview的动画 */
				imageView.clearAnimation();
				tempImageView.clearAnimation();
				tempImageView.setVisibility(View.GONE);
				setResource(currentPoint);
				loadGoMain();
			}
		});
		this.setTempResource(currentPoint);
		tempImageView.setVisibility(View.VISIBLE);
		imageView.startAnimation(leftOutAnim);
		tempImageView.startAnimation(leftInAnim);
	}
	
	/**
	 * 显示上一张图片时动画效果
	 * @auther chenlf3
	 * @date 2015年12月31日 下午12:32:50
	 */
	private void preResourceAnim() {
		/**
		 * 1.让imageView开启右出动画
		 * 2.让tempImageView显示，并开启右进动画
		 */
		Animation rightOutAnim = this.getAnim(this.RIGHT_OUT);
		Animation rightInAnim = this.getAnim(this.RIGHT_IN);
		/** 动画完毕之后的一些操作 */
		rightInAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				/** 动画完结之后，设定显示资源，并清理imageview的动画 */
				imageView.clearAnimation();
				tempImageView.clearAnimation();
				tempImageView.setVisibility(View.GONE);
				setResource(currentPoint);
			}
		});
		this.setTempResource(currentPoint);
		tempImageView.setVisibility(View.VISIBLE);
		imageView.startAnimation(rightOutAnim);
		tempImageView.startAnimation(rightInAnim);
	}
	
	/**
	 * 显示上一张图片,如果是第一张，则提示已经是第一张
	 * @throws IOException 
	 * @auther chenlf3
	 * @date 2015年9月4日 下午2:18:45
	 */
	private void showPreResource() {
		if(fileNames != null && fileNames.length>0) {
			currentPoint --;
			if(currentPoint<0) {
				Toast.makeText(context, "当前已经是第一张！", Toast.LENGTH_SHORT).show();
				currentPoint ++;
			} else {
				this.setDoll(currentPoint);
				/** 不带动画的设定资源 */
				//this.setResource(currentPoint);
				/** 带动画的设定资源 */
				this.preResourceAnim();
			}
		}
	}
	
	/**
	 * 显示下一张图片,如果是最后一张，则提示
	 * @throws IOException 
	 * @auther chenlf3
	 * @date 2015年9月4日 下午2:23:47
	 */
	private void showNextResource() {
		if(fileNames != null && fileNames.length>0) {
			//int oldPoint = currentPoint;
			currentPoint ++;
			if(currentPoint>fileNames.length-1) {
				Toast.makeText(context, "当前已经是最后一张！", Toast.LENGTH_SHORT).show();
				currentPoint --;
			} else {
				this.setDoll(currentPoint);
				/** 不带动画效果 */
				//this.setResource(currentPoint);
				/** 带动画效果的 */
				this.nextResourceAnim();
			}
		}
	}
	
	/**
	 * 显示上一张图片,如果是第一张，则循环
	 * @auther chenlf3 
	 * @date 2015年9月4日 下午2:26:06
	 * @throws IOException
	 */
	private void showPreResourceWithCycle() {
		if(fileNames != null && fileNames.length>0) {
			currentPoint --;
			if(currentPoint<0) {
				currentPoint = fileNames.length-1;
			}
			/** 设定小圆点 */
			this.setDoll(currentPoint);
			/** 不带动画效果的 */
			//this.setResource(currentPoint);
			/** 带动画效果的 */
			this.preResourceAnim();
		}
	}
	
	/**
	 * 显示下一张图片,如果是最后一张，则循环
	 * @auther chenlf3 
	 * @date 2015年9月4日 下午2:26:06
	 * @throws IOException
	 */
	private void showNextResourceWithCycle() {
		if(fileNames != null && fileNames.length>0) {
			currentPoint ++;
			if(currentPoint>fileNames.length-1) {
				currentPoint = 0;
			}
			this.setDoll(currentPoint);
			/** 不带动画的设定资源 */
			//this.setResource(currentPoint);
			/** 带动画的设定资源 */
			this.nextResourceAnim();
		}
	}
	
	/**
	 * 返回图片画面
	 * @auther chenlf3
	 * @date 2015年9月4日 下午4:25:50
	 * @return
	 */
	public ImageView getView() {
		return imageView;
	}
	
	public ImageView getTempView() {
		return tempImageView;
	}
	
	/**
	 * 返回小圆点画面
	 * @auther chenlf3
	 * @date 2015年9月4日 下午4:25:58
	 * @return
	 */
	public List<ImageView> getIcons() {
		if(iconChecked != null && iconUnChecked!=null && icons != null) {
			return icons;
		}
		return null;
	}
	
	/**
	 * 检测手势响应事件
	 * @auther chenlf3
	 * @date 2015年9月4日-下午2:35:17
	 * Copyright (c) 2015点聚信息技术有限公司-版权所有
	 */
	private class MyOnGestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		/** 滑动响应事件 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(e2.getX()-e1.getX()>60) {//从左向右滑动，上一张
				/** 滑动翻页 */
				if(isCycle) {
					showPreResourceWithCycle();//显示上一张(带循环)
				} else {
					showPreResource();//显示上一张(不带循环)
				}
				/** 如果是最后一张，显示隐藏的进入主界面按钮 */
				loadGoMain();
			}else if(e2.getX()-e1.getX()<-60) {//从右向左滑动，下一张
				if(isCycle) {
					showNextResourceWithCycle();
				} else {
					showNextResource();
				}
				/** 如果是最后一张，显示隐藏的进入主界面按钮 */
				//loadGoMain();此操作放入动画监听里面
			} else {
				
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
	}
	
	/**
	 * 在显示到最后一页的时候，显示进入主界面按钮，如果是循环模式，永远不显示进入按钮
	 * @auther chenlf3
	 * @date 2015年9月4日 下午5:12:44
	 */
	private void loadGoMain() {
		if(!isCycle && fileNames != null && fileNames.length>0) {
			if(currentPoint == fileNames.length-1) {
				showGoMain();
			} else {
				hideGoMain();
			}
		}
	}
	
	/**
	 * 显示进入主界面按钮，供外部调用
	 * @auther chenlf3
	 * @date 2015年9月4日 下午5:14:32
	 */
	public void showGoMain(){};
	
	/**
	 * 隐藏进入主界面按钮，供外部调用
	 * @auther chenlf3
	 * @date 2015年9月4日 下午5:15:02
	 */
	public void hideGoMain(){};
	
	/**
	 * 控制循环的线程,让图片循环显示
	 * @auther chenlf3
	 * @date 2015年9月4日-下午3:31:03
	 * Copyright (c) 2015点聚信息技术有限公司-版权所有
	 */
	private class CycleThread extends Thread {
		private boolean flag = true;
		@Override
		public void run() {
			while(flag) {
				try {
					Thread.sleep(cycleTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(flag) {
					handler.sendEmptyMessage(0);
				}
			}
		}
		
		/** 停止线程 */
		public void stopThread() {
			if(flag) {
				flag = false;
			}
		}
	}
	
	/**
	 * 获取动画效果
	 * @auther chenlf3
	 * @date 2015年12月31日 上午11:18:05
	 * @return
	 */
	private Animation getAnim(int orientation) {
		TranslateAnimation animation = null;
		if(orientation>=0 && orientation <= 3) {
			if(orientation == this.LEFT_OUT) {
				animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
				//animation.setInterpolator(new AnticipateInterpolator());
			} else if(orientation == this.LEFT_IN) {
				animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
				//animation.setInterpolator(new AnticipateOvershootInterpolator());
			} else if(orientation == this.RIGHT_OUT) {
				animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
				//animation.setInterpolator(new AnticipateInterpolator());
			} else if(orientation == this.RIGHT_IN) {
				animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
				//animation.setInterpolator(new AnticipateOvershootInterpolator());
			} else {}
			if(animation != null) {
				animation.setInterpolator(new LinearInterpolator());//设定均匀速度
				animation.setDuration(ANIM_TIME);
			}
		}
		return animation;
	}
	
	/**
	 * 释放资源
	 * @auther chenlf3
	 * @date 2015年9月4日 下午3:46:55
	 */
	public void releaseRes() {
		if(cycleThread != null) {
			cycleThread.stopThread();
		}
	}
	
	/**
	 * 修改轮播时长
	 * @auther chenlf3
	 * @date 2016年3月4日 下午5:42:45
	 * @param millisecond
	 */
	public void updateCycleTime(int millisecond) {
		this.cycleTime = millisecond;
	}
	
	/**
	 * 恢复轮播(如果创建对象时就是非轮播的，该方法不起作用)
	 * @auther chenlf3
	 * @date 2016年3月11日 下午4:51:38
	 */
	public void startCycle() {
		if(isCycle && cycleThread == null) {
			cycleThread = new CycleThread();
			cycleThread.start();
		}
	}
	
	/**
	 * 暂停轮播(如果创建对象时就是非轮播的，该方法不起作用)
	 * @auther chenlf3
	 * @date 2016年3月11日 下午4:52:43
	 */
	public void pauseCycle() {
		if(isCycle && cycleThread != null) {
			cycleThread.stopThread();
			cycleThread = null;
		}
	}
	
}
