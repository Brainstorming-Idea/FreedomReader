package com.dianju.trustedsign.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.view.WindowManager;

/**
 * 对bitmap的操作
 * @auther chenlf3
 * @date 2015年8月11日-上午11:48:10
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
@SuppressLint("NewApi")
public class ClfBitmap {
	
	public static final String DEFAULT_STORE_PATH = Environment.getExternalStorageDirectory()+File.separator+"clf"+File.separator+"clfCamera";//默认图片保存路径
	/**
	 * 创建指定宽高的空白bitmap
	 * @auther chenlf3
	 * @date 2015年8月11日 上午11:57:00
	 * @param width 宽度
	 * @param height 高度
	 * @return
	 * @throws Exception
	 */
	public Bitmap getBlankBitmap(int width,int height)throws Exception {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		return bitmap;
	}
	
	/**
	 * 根据图片资源id获取bitmap
	 * (获得的图片都是只读的图片，相对bitmap进行缩放，必须是一个可以修改的bitmap)
	 * @auther chenlf3
	 * @date 2015年8月11日 下午12:18:19
	 * @param context 上下文
	 * @param id 图片资源id
	 * @return
	 */
	public static Bitmap getBitmap(Context context, int id) {
		return BitmapFactory.decodeResource(context.getResources(),id);
	}
	
	/**
	 * 根据图片资源id获取bitmap
	 * (获得的图片都是只读的图片，相对bitmap进行缩放，必须是一个可以修改的bitmap)
	 * @auther chenlf3
	 * @date 2015年8月11日 下午12:20:24
	 * @param context 上下文
	 * @param id 图片资源id
	 * @param options 一些配置参数
	 * @return
	 */
	public static Bitmap getBitmap(Context context, int id, BitmapFactory.Options options) {
		if(options == null) return BitmapFactory.decodeResource(context.getResources(),id);
		return BitmapFactory.decodeResource(context.getResources(),id,options);
	}
	
	/**
	 * 获取文件对应的bitmap
	 * @auther chenlf3
	 * @date 2017年2月15日 下午4:50:24
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmap(String filePath) {
		return BitmapFactory.decodeFile(filePath);
	}
	
	/**
	 * 根据sd卡中图片路径获取bitmap
	 * (获得的图片都是只读的图片，相对bitmap进行缩放，必须是一个可以修改的bitmap)
	 * @auther chenlf3
	 * @date 2015年8月11日 下午12:25:07
	 * @param filePath
	 * @param options
	 * @return
	 */
	public static Bitmap getBitmap(String filePath, BitmapFactory.Options options) {
		return BitmapFactory.decodeFile(filePath,options);
	}
	
	/**
	 * 根据byte[]获取bitmap
	 * @auther chenlf3
	 * @date 2016年7月7日 上午11:12:54
	 * @param data
	 * @return
	 */
	public static Bitmap getBitmap(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	/**
	 * 
	 * @auther chenlf3
	 * @date 2016年7月7日 上午11:12:57
	 * @param data
	 * @param options
	 * @return
	 */
	public static Bitmap getBitmap(byte[] data, BitmapFactory.Options options) {
		if(options == null) {
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		} else {
			return BitmapFactory.decodeByteArray(data, 0, data.length, options);
		}
		
	}
	
	/**
	 * 将bitmap保存为jpg
	 * @auther chenlf3
	 * @date 2015年8月11日 下午2:18:58
	 * @param bitmap
	 * @param filePath 保存路径
	 * @throws FileNotFoundException
	 */
	public static void saveJpg(Bitmap bitmap, String filePath) {
		File file = new File(filePath);
		FileOutputStream out;
		try {
			out = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 将bitmap保存为png
	 * @auther chenlf3
	 * @date 2015年8月11日 下午2:19:58
	 * @param bitmap
	 * @param filePath 保存路径
	 * @throws FileNotFoundException
	 */
	public static void savePng(Bitmap bitmap, String filePath) {
		File file = new File(filePath);
		if(!file.exists()) {
			try {
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(filePath);
		        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);//100表示不压缩
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 将bitmap保存为bmp文件
	 * @auther chenlf3
	 * @date 2015年8月11日 上午11:48:44
	 * @param bitmap
	 * @param filePath
	 */
	public static void saveBmp(Bitmap bitmap,String filePath) {
		if (bitmap == null)  
            return;  
        /** 位图大小 */  
        int nBmpWidth = bitmap.getWidth();  
        int nBmpHeight = bitmap.getHeight();  
        /** 图像数据大小  */  
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);  
        try {  
            /** 存储文件名   */
            String filename = filePath;  
            File file = new File(filename);  
            if (!file.exists()) {  
                file.createNewFile();  
            }  
            FileOutputStream fileos = new FileOutputStream(filename);  
            /** bmp文件头  */  
            int bfType = 0x4d42;  
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;  
            int bfReserved2 = 0;  
            long bfOffBits = 14 + 40;  
            /** 保存bmp文件头 */   
            writeWord(fileos, bfType);  
            writeDword(fileos, bfSize);  
            writeWord(fileos, bfReserved1);  
            writeWord(fileos, bfReserved2);  
            writeDword(fileos, bfOffBits);
            /** bmp信息头  */  
            long biSize = 40L;  
            long biWidth = nBmpWidth;  
            long biHeight = nBmpHeight;  
            int biPlanes = 1;  
            int biBitCount = 24;  
            long biCompression = 0L;  
            long biSizeImage = 0L;  
            long biXpelsPerMeter = 0L;  
            long biYPelsPerMeter = 0L;  
            long biClrUsed = 0L;  
            long biClrImportant = 0L;  
            /** 保存bmp信息头 */   
            writeDword(fileos, biSize);  
            writeLong(fileos, biWidth);  
            writeLong(fileos, biHeight);  
            writeWord(fileos, biPlanes);  
            writeWord(fileos, biBitCount);  
            writeDword(fileos, biCompression);  
            writeDword(fileos, biSizeImage);  
            writeLong(fileos, biXpelsPerMeter);  
            writeLong(fileos, biYPelsPerMeter);  
            writeDword(fileos, biClrUsed);  
            writeDword(fileos, biClrImportant);  
            /** 像素扫描 */   
            byte bmpData[] = new byte[bufferSize];  
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);  
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)  
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {  
                    int clr = bitmap.getPixel(wRow, nCol);  
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);  
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);  
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);  
                }  
            fileos.write(bmpData);  
            fileos.flush();  
            fileos.close();  
  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
	}
	
	/**
	 * 添加信息头
	 * @param stream
	 * @param value
	 * @throws IOException
	 */
    private static void writeWord(FileOutputStream stream, int value) throws IOException {  
        byte[] b = new byte[2];  
        b[0] = (byte) (value & 0xff);  
        b[1] = (byte) (value >> 8 & 0xff);  
        stream.write(b);  
    }  
  
    /**
     * 添加文件头
     * @param stream
     * @param value
     * @throws IOException
     */
    private static void writeDword(FileOutputStream stream, long value) throws IOException {  
        byte[] b = new byte[4];  
        b[0] = (byte) (value & 0xff);  
        b[1] = (byte) (value >> 8 & 0xff);  
        b[2] = (byte) (value >> 16 & 0xff);  
        b[3] = (byte) (value >> 24 & 0xff);  
        stream.write(b);  
    }  
  
    private static void writeLong(FileOutputStream stream, long value) throws IOException {  
        byte[] b = new byte[4];  
        b[0] = (byte) (value & 0xff);  
        b[1] = (byte) (value >> 8 & 0xff);  
        b[2] = (byte) (value >> 16 & 0xff);  
        b[3] = (byte) (value >> 24 & 0xff);  
        stream.write(b);  
    }
    
    /**
     * color是否近似白色
     * @auther chenlf3
     * @date 2016年3月30日 上午10:50:25
     * @param color
     * @return
     */
    private static boolean colorInRange(int color) {
		int color_range = 220;
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        if (red >= color_range && green >= color_range && blue >= color_range) return true;  
        return false;
    }
    
    /**
     * 压缩图片至全屏(尺寸压缩)
     * @auther chenlf3
     * @date 2015年12月30日 上午10:38:43
     * @param context 上下文
     * @param resId 资源id
     * @return
     */
    public static Bitmap compressImageSize(Context context, int resId) {
    	/** 获取手机屏幕的宽高 */
    	WindowManager manager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
    	int phoneWidth = manager.getDefaultDisplay().getWidth();
		int phoneHeight = manager.getDefaultDisplay().getHeight();
		
    	BitmapFactory.Options opts = new BitmapFactory.Options();
    	//不去真的解析图片，只获取图片的头部信息(包含宽、高)
    	opts.inJustDecodeBounds = true;
    	BitmapFactory.decodeResource(context.getResources(), resId, opts);
    	/** 获取图片的宽高 */
    	int imageWidth = opts.outWidth;
		int imageHeight = opts.outHeight;
		int scale = 1;
		float scaleX = (float)imageWidth/phoneWidth;
		float scaleY = (float)imageHeight/phoneHeight;
		if(scaleX>1 || scaleY>1) {
			float temp = 1;
			if(scaleX>scaleY) {
				temp = scaleX;
			}else {
				temp = scaleY;
			}
			scale = (int)Math.ceil(temp);
		}
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;//缩小比例
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, opts);
    	return bitmap;
    }
	
	/**
     * 压缩图片，尺寸压缩
     * @auther chenlf3
     * @date 2015年12月30日 上午10:38:28
     * @param context 上下文
     * @param resId 资源id
     * @param scale1 缩放比例
     * @return
     */
    public static Bitmap compressImageSize(Context context, int resId, int scale1) {
    	/** 获取手机屏幕的宽高 */
    	WindowManager manager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
    	int phoneWidth = manager.getDefaultDisplay().getWidth();
		int phoneHeight = manager.getDefaultDisplay().getHeight();
    	BitmapFactory.Options opts = new BitmapFactory.Options();
    	//不去真的解析图片，只获取图片的头部信息(包含宽、高)
    	opts.inJustDecodeBounds = true;
    	BitmapFactory.decodeResource(context.getResources(), resId, opts);
    	/** 获取图片的宽高 */
    	int imageWidth = opts.outWidth;
		int imageHeight = opts.outHeight;
		int scale = 1;
		float scaleX = (float)imageWidth/phoneWidth;
		float scaleY = (float)imageHeight/phoneHeight;
		if(scaleX>1 || scaleY>1) {
			float temp = 1;
			if(scaleX>scaleY) {
				temp = scaleX;
			}else {
				temp = scaleY;
			}
			scale = (int)Math.ceil(temp);
		}
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale+scale1;//缩小比例
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, opts);
    	return bitmap;
    }
    
    /**
     * 尺寸压缩(获取一个宽度为widht*height的bitmap)
     * @auther chenlf3
     * @date 2017年2月15日 下午12:25:08
     * @param bitmap
     * @param width bitmap宽度
     * @param height bitmap高度
     * @return
     */
    public static Bitmap compressBitmapSize(Bitmap bitmap, int width, int height) {
    	int imageWidth = bitmap.getWidth();
		int imageHeight = bitmap.getHeight();
		float scaleX = (float)width/imageWidth;
		float scaleY = (float)height/imageHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleX, scaleY);
		Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, imageWidth, imageHeight, matrix, true);
    	return bitmap1;
    }
    
    /**
     * 软尺寸压缩(获取一个宽高比例不变的bitmap，缩放比例为宽度和高度中最小值)
     * @auther chenlf3
     * @date 2017年2月15日 下午12:32:12
     * @param bitmap
     * @param width bitmap宽度
     * @param height bitmap高度
     * @param outOfRangeCompress true-只对超出范围的图片进行压缩;false-对所有图片进行压缩
     * @return
     */
    public static Bitmap compressBitmapSoftsize(Bitmap bitmap, int width, int height) {
    	int imageWidth = bitmap.getWidth();
		int imageHeight = bitmap.getHeight();
		float scaleX = (float)width/imageWidth;
		float scaleY = (float)height/imageHeight;
		float minScale = scaleX;
		if(minScale>scaleY) {
			minScale = scaleY;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(minScale, minScale);
		Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, imageWidth, imageHeight, matrix, true);
    	return bitmap1;
    }
    
    /**
     * 质量压缩
     * @auther chenlf3
     * @date 2017年2月15日 下午12:16:04
     * @param bitmap
     * @param size 压缩比率，100表示不压缩
     * @return
     */
    public static Bitmap compressBitmapQuality(Bitmap bitmap, int size) {
    	ByteArrayOutputStream os = new ByteArrayOutputStream();  
    	bitmap.compress(CompressFormat.PNG, size, os);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到os中
    	byte[] data = os.toByteArray();
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
    
    /**
     * 获取透明的bitmap
     * @auther chenlf3
     * @date 2016年3月30日 上午10:09:07
     * @param bitmap
     * @return
     */
    public static Bitmap getTransparentBitmap(Bitmap bitmap) {
    	/** 循环去掉白色，用透明色0x00FFFFFF代替 */
    	Bitmap sourceBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
    	sourceBitmap.setHasAlpha(true);//设置透明度有效
    	int w=sourceBitmap.getWidth();
		int h=sourceBitmap.getHeight();
		int[] bitpixs = new int[w * h];
		sourceBitmap.getPixels(bitpixs, 0, w, 0, 0, w, h);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int pix = bitpixs[w*j+i];
				if ((pix == Color.WHITE) || colorInRange(pix)) {
					bitpixs[w*j+i] = 0x00FFFFFF;
				}
			}
		}
		sourceBitmap.setPixels(bitpixs, 0, w, 0, 0, w, h);
    	return sourceBitmap;
    }
    
    /**
     * 将图片周围的白色边框截掉(会改变图片的分辨率大小，相当于从原图片中间挖出来一部分)
     * @auther chenlf3
     * @date 2017年2月14日 下午5:54:25
     * @param sourceBitmap 待处理图片
     * @return
     */
	public Bitmap cutOutWhite(Bitmap bitmap) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int minX = w;//非白色像素点的最小x坐标
		int maxX = 0;//非白色像素点的最大x坐标
		int minY = h;//非白色像素点的最小y坐标
		int maxY = 0;//非白色像素点的最大y坐标
		int[] bitpixs = new int[w * h];//定义一个数据用来存储每个像素点的颜色值
		bitmap.getPixels(bitpixs, 0, w, 0, 0, w, h);//获取每个像素点的颜色值
		boolean isFirst = true;
		for(int i=0;i<w;i++) {
			for(int j=0;j<h;j++) {
				if (bitpixs[w * j + i] != 0xffffffff) {
					if(isFirst) {
						isFirst = false;
						minX = maxX = i;
						minY = maxY = j;
					} else {
						if(i<minX) {
							minX = i;
						} else if(i>maxX) {
							maxX = i;
						} else {}
						if(j<minY) {
							minY = j;
						} else if(j>maxY) {
							maxY = j;
						} else {}
					}
				}
			}
		}
		return Bitmap.createBitmap(bitmap, minX, minY, maxX-minX, maxY-minY);
	}
	
	
	
	
	
	
    /**
     * 压缩图片，质量压缩(不改变图片分辨率)
     * @auther chenlf3
     * @date 2015年12月8日 上午10:00:45
     * @param image 待压缩图片
     * @param out 输出对象
     * @param size 压缩质量，值越大，图片大小越大，值越小，图片大小越小
     */
    /*public static void compressImage(Bitmap image, FileOutputStream out, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 >size) {  //340在0.98M左右
        	options -=10;//每次都减少10，注意有可能为负，为负会报错
            if(options <= 0) {
            	break;
            }
        	baos.reset();//重置baos即清空baos 
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        *//** 对bmp进行旋转 *//*
        Matrix m = new Matrix();
        m.postRotate(90,(float)bitmap.getWidth()/2,(float)bitmap.getHeight()/2);
        Bitmap bm = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
        //把压缩后的图片写入SD卡
        bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
    }*/ 
    
    /**
     * 压缩图片，先质量压缩，再尺寸压缩
     * @auther chenlf3
     * @date 2016年1月6日 下午3:56:12
     * @param image
     * @param out
     * @param size
     * @param scale
     */
    /*public static void compressesImage(Bitmap image, String outFile, int size, int scale) {
    	FileOutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 >size) {  //340在0.98M左右
        	options -=20;//每次都减少20，注意有可能为负，为负会报错
            if(options <= 20) {
            	break;
            }
        	baos.reset();//重置baos即清空baos 
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;//尺寸缩放比例，如scale=2则尺寸变为原来一半
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, opts);//把ByteArrayInputStream数据生成图片
        *//** 对bmp进行旋转 *//*
        Matrix m = new Matrix();
        m.postRotate(90,(float)bitmap.getWidth()/2,(float)bitmap.getHeight()/2);
        Bitmap bm = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
        //把压缩后的图片写入SD卡
        bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }*/
    
}