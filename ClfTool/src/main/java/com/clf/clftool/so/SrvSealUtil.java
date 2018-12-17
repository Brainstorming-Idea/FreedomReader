package com.clf.clftool.so;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * libSrvSealUtil.so导入
 * @auther chenlf3
 * @date 2015年8月6日-上午9:55:03
 * Copyright (c) 2015点聚信息技术有限公司-版权所有
 */
public class SrvSealUtil {
	/**
	 * 获得节点的数目
	 * @auther chenlf3
	 * @date 2015年8月6日 上午9:55:09
	 * @param nObjID 文档id
	 * @param nPageIndex 文档的页码
	 * @param nNoteType 节点类型
	 * @return
	 */
	public native int getNoteNum(int nObjID, int nPageIndex, int nNoteType);
	/**
	 * 节点类型(nNoteType)介绍：
	 * NOTE_TYPE_PEN = 1：笔迹批注类型(手写板写的东西)
	 * NOTE_TYPE_TEXT = 2:文字批注类型
	 * NOTE_TYPE_PIC = 3:图片和印章及二维条码类型
	 * NOTE_TYPE_LINK = 4:链接类型
	 * NOTE_TYPE_AREA = 10:手写区域及编辑区域类型
	 * NOTE_TYPE_REALPIC = 250:图片类型
	 * NOTE_TYPE_SEAL = 251:印章类型
	 * NOTE_TYPE_BARCODE = 252:条码类型
	 * 
	 * NOTE_TYPE_NULL = 0
	 * NOTE_TYPE_PEN = 1
	 * NOTE_TYPE_TEXT = 2
	 * NOTE_TYPE_PIC = 3
	 * NOTE_TYPE_LINK = 4
	 * NOTE_TYPE_CTRL = 5
	 * NOTE_TYPE_AREA = 10
	 * NOTE_TYPE_EDIT_V1 = 11
	 * NOTE_TYPE_VECTOR = 20
	 * NOTE_TYPE_CONTENT = 126
	 * NOTE_TYPE_UNKNOWN = 127

	 * NOTE_TYPE_AREA_V1 = 13
	 * NOTE_TYPE_PENEXT_V1 = 14

	 * NOTE_TYPE_CHECKBOX = 240
	 * NOTE_TYPE_RADIO = 241
	 * NOTE_TYPE_COMBOX = 242
	 * NOTE_TYPE_LISTBOX = 243
	 * NOTE_TYPE_BUTTON = 244

	 * NOTE_TYPE_PREDEFURLLINK = 248
	 * NOTE_TYPE_REALPIC = 250
	 * NOTE_TYPE_SEAL = 251
	 * NOTE_TYPE_BARCODE = 252
	 * NOTE_TYPE_PREDEFSEAL = 253
	 * NOTE_TYPE_PREDEFBARCODE = 254
	 * 
	 */
	
	
	/**
	 * 根据节点的索引获取节点的名称
	 * @auther chenlf3
	 * @date 2015年8月6日 上午10:00:32
	 * @param nObjID 文档id
	 * @param lIndex 节点索引
	 * @param lNoteType 节点类型
	 * @return
	 */
	public native String getNoteByIndex(int nObjID, int lIndex, int lNoteType);
	
	
	/**
	 * 获取节点的某项数据
	 * @auther chenlf3
	 * @date 2015年8月6日 上午10:01:50
	 * @param nObjID 文档id
	 * @param noteName 节点名称
	 * @param lValueType 获取类型
	 * @param valueName 保留
	 * @param lParam 保留
	 * @param param 保留
	 * @return
	 */
	public native String getValueEx(int nObjID,String noteName,int lValueType,String valueName,int lParam,String param);
	/**
	 * 获取类型(lValueType)介绍：
	 * lValueType=6：获取节点的x坐标
	 * lValueType=7：获取节点的y坐标
	 * lValueType=8：获取节点的宽度
	 * lValueType=9：获取节点的高度
	 * lValueType=20：获取节点所在的页码
	 */
	
	
	/**
	 * 获取PDF文档数据
	 * @auther chenlf3
	 * @date 2015年8月6日 上午10:13:45
	 * @param nObjID 文档id
	 * @return
	 */
	public native byte[] getData(int nObjID);
	
	
	/**
	 * 设置印章模式
	 * @auther chenlf3
	 * @date 2015年8月6日 上午10:04:33
	 * @param nObjID 文档id
	 * @param sealMode 印章模式
	 * @return
	 */
	public native int setSealMode(int nObjID, int sealMode);
	/**
	 * 印章模式(sealMode)介绍：
	 * sealMode=0：对aip文档进行盖章
	 * sealMode=1：对pdf文档进行盖章，并且是通过移动端盖章(bmp图片印章)
	 * sealMode=2：对pdf文档进行盖章，并且是通过陆总接口盖章(sel印章)
	 */
	
	
	/**
	 * 设置预置的印章图片
	 * @auther chenlf3
	 * @date 2015年8月6日 上午11:09:58
	 * @param nObjID 文档id
	 * @param sealBmpValue 为路径或者以STRDATA:打头的base64
	 * @param sealZoom 未知
	 * @return
	 */
	public native int setSealBmpData(int nObjID, String sealBmpValue, int sealZoom);
	
	
	/**
	 * 签章、盖章
	 * @auther chenlf3
	 * @date 2015年8月6日 上午11:14:44
	 * @param nObjID 文档id
	 * @param pagesData 页面信息
	 * @param oriSealName 
	 * @param sealName
	 * @return
	 */
	public native int addSeal(int nObjID, String pagesData, String oriSealName, String sealName);
	/** 盖图片印章 */
	//int as=contentView.getCore().addSeal(contentView.getCore().objID, pagesData, "","AUTO_ADD_SEAL_FROM_PATH");
	//String pagesData=page+","+xz+",0,"+yz+",";//0代表的是图片印章
	
	
	/**
	 * 获取pdf文档上面待签名的数据(签章之后才进行签名)
	 * @auther chenlf3
	 * @date 2015年8月6日 上午11:18:56
	 * @param nObjID 文档id
	 * @return
	 */
	public native byte[] getSignSHAData(int nObjID);
	
	
	/**
	 * 获取签名后的数据p7在PDF文档数据中的预留起始位置(因为需要将p7插入盖章后的pdf中)
	 * @auther chenlf3
	 * @date 2015年8月6日 上午11:20:56
	 * @param nObjID 文档id
	 * @return
	 */
	public native int getSignPos(int nObjID);
	
	/**
	 * 保存并关闭文档，如果filePath为空字符串，则只关闭文件，不保存
	 * @auther chenlf3
	 * @date 2015年8月10日 上午10:15:57
	 * @param objID 文档id
	 * @param filePath 保存路径
	 * @return
	 */
	public native int saveFile(int objID, String filePath);
	
	/**
	 * 打开aip文档
	 * @auther chenlf3
	 * @date 2015年8月10日 上午10:17:56
	 * @param openPath 文档路径
	 * @return
	 */
	public native int openTempObj(String openPath);
	
	/**
	 * 打开pdf文档
	 * @auther chenlf3
	 * @date 2015年8月10日 上午11:05:13
	 * @param openPath
	 * @param isPdf 1 固定值
	 * @return
	 */
	public native int openObj(String openPath, int isPdf);
	
	/**
	 * 根据文档id获取文档的页数
	 * @auther chenlf3
	 * @date 2015年8月10日 上午10:19:13
	 * @param objID
	 * @return
	 */
	public native int getPageCount(int objID);
	
	/**
	 * 功能一、将一批数据填充到aip的文本中去，只适用于填充文本
	 * 功能二、拍照后将设定好位置的照片插入pdf/aip文档中
	 * @auther chenlf3
	 * @date 2015年8月10日 上午10:25:02
	 * @param nObjID 文档id
	 * @param name FORM_DATA_TXT_FORMAT 固定值 / 如果pdf文档，需前边设定的插入节点的名字；如果aip文档，直接写节点中名字
	 * @param value 一批数据按照一定规则拼接起来的字符串 / 照片路径(加上前缀"BMPFILE:")
	 * @return
	 */
	public native int setValue(int nObjID, String name, String value);
	
	/**
	 * 获取空白文档的签字数据
	 * @auther chenlf3
	 * @date 2015年8月10日 上午11:37:44
	 * @param nObjID 文档id
	 * @return
	 */
	public native String copyNodes(int nObjID);
	
	/**
	 * 释放资源
	 * @auther chenlf3
	 * @date 2015年8月10日 上午11:51:40
	 * @param bitmap bitmap
	 * @param nObjID 文档id
	 * @return
	 */
	public native int detachPageBmp(Bitmap bitmap, int nObjID);
	
	/**
	 * 撤销全部操作
	 * @auther chenlf3
	 * @date 2015年8月10日 上午11:53:19
	 * @param nObjID 文档id
	 * @return
	 */
	public native int undoAll(int nObjID);
	
	/**
	 * 撤销上一步操作
	 * @auther chenlf3
	 * @date 2015年8月10日 上午11:54:00
	 * @param nObjID 文档id
	 * @return
	 */
	public native int undo(int nObjID);
	
	/**
	 * 跳转到文档的某一页
	 * @auther chenlf3
	 * @date 2015年8月10日 下午3:52:44
	 * @param objID 文档id
	 * @param page 页码
	 * @return
	 */
	public native int gotoPage(int objID, int page);
	
	/**
	 * 获取该文档当前页的宽度
	 * @auther chenlf3
	 * @date 2015年8月10日 下午3:53:42
	 * @param objID
	 * @return
	 */
	public native float getPageWidth(int objID);
	
	/**
	 * 获取该文档当前页的高度
	 * @auther chenlf3
	 * @date 2015年8月10日 下午3:54:32
	 * @param objID
	 * @return
	 */
	public native float getPageHeight(int objID);
	
	/**
	 * 验证授权信息
	 * @auther chenlf3
	 * @date 2015年8月10日 下午4:02:39
	 * @param im 手机串号：GSM手机的IMEI和CDMA手机的MEID(如果手机串号为null，则获取wifi信息中的MAC地址)
	 * @param str 授权码字符串(需加前缀"STR:")
	 * @return 1：成功；0：失败
	 */
	public native int verifyLic(String im,String str);
	
	/**
	 * 将手写内容粘贴在pdf文档上(手写内容不能是base64)
	 * @auther chenlf3
	 * @date 2015年8月10日 下午5:27:15
	 * @param nObjID 文档id
	 * @param strNodes 手写内容
	 * @param offPage 要粘贴到的页码
	 * @param nw 粘贴区域宽
	 * @param nh 粘贴区域高
	 * @param noffx 粘贴区域左上x坐标
	 * @param noffy 粘贴区域域左上y坐标
	 * @return
	 */
	public native int  pasteNodesEx(int nObjID, String strNodes, int offPage,int nw, int nh, int noffx, int noffy); 
	
	/**
	 * 为pdf文档添加录音文件标示
	 * @auther chenlf3
	 * @date 2015年8月10日 下午5:33:31
	 * @param nObjID 文档id
	 * @param embName 节点名字，随便命名
	 * @param filePath 录音文件路径
	 * @param nPageIndex 要插入标示的页码
	 * @param nLeft 左上角x坐标
	 * @param nTop 左上角y坐标
	 * @return
	 */
	public native int insertEmbFile(int nObjID, String embName, String filePath, int nPageIndex, int nLeft, int nTop);
	
	/**
	 * 设定拍照照片的插入pdf文档的位置
	 * @auther chenlf3
	 * @date 2015年8月10日 下午5:38:06
	 * @param nObjID 文档id
	 * @param noteName 随意起一个节点名字
	 * @param nType 节点类型：1：连接；2：手写区域；3：编辑区域(比如插入拍照照片)；4：自动扩充区域；5：自动扩充区域(但不扩充页面)
	 * @param nPage
	 * @param nPosx
	 * @param nPosy
	 * @param nWidth
	 * @param nHeight
	 * @return
	 */
	public native int insertNote(int nObjID,String noteName, int nType, int nPage, int nPosx, int nPosy, int nWidth, int nHeight);
	/**
	 * 节点类型(nType)介绍：
	 * 7:空格
	 * 8：回车
	 */
	
	/**
	 * 给aip文档中复选框赋值
	 * @auther chenlf3
	 * @date 2015年8月11日 上午11:08:24
	 * @param i 文档id
	 * @param s 几点名称
	 * @param j 类型=3 固定值
	 * @param k 二进制组成的值，例如有三个复选框，1代表选中第一个，100代表选中第三个，100+1代表选中第一个和第三个
	 * @param s1 空字符串"" 固定值
	 * @return
	 */
	public native int setValueEx(int i, String s, int j, int k, String s1);
	
	/**
	 * 合并文档
	 * @auther chenlf3
	 * @date 2015年8月11日 上午11:32:39
	 * @param nObjID 被插入文档id
	 * @param filepath 插入文档路径
	 * @param pageindex 在某页之后插入
	 * @return
	 */
	public native int mergeFile(int nObjID, String filepath, int pageindex);
	
	/**
	 * 获取文档的数据(pdf)，不包含节点信息
	 * @auther chenlf3
	 * @date 2015年12月14日 上午10:44:23
	 * @return
	 */
	public native byte[] getFile();
	
	/**
	 * 获取文档的aip数据
	 * @auther chenlf3
	 * @date 2015年12月14日 上午10:45:26
	 * @return
	 */
	public native byte[] getTempFile();
	
	/**
	 * 获取文档的aip数据
	 * @auther chenlf3
	 * @date 2015年12月14日 上午10:46:09
	 * @return
	 */
	public native String getTempFileBase64();
	
	/**
	 * contentView.setCurrAction(7);
	 * 
	 */
	
	
	/**获取节点坐标
	 * String pageIndex = contentView.getValueEx("DefSignArea",20,"",0,"");
	 * String x = contentView.getValueEx("DefSignArea",6,"",0,"");
	 * String y = contentView.getValueEx("DefSignArea",7,"",0,"");
	 * String width = contentView.getValueEx("DefSignArea",8,"",0,"");
	 * String height = contentView.getValueEx("DefSignArea",9,"",0,"");
	 */
	
	/*授权方式
	授权码授权
	int rr = contentView.verifyLic("A3Lb3oBBAh2dyf/pZ5P1zcVldCr7WrrHT4GcrK1YDF4=");
	int login=contentView.login("longfei", 2, "");
	
	账户授权
	String temp = contentView.getLicOnline("http://www.dianju.cn:9239", "aztest001");
	int login=contentView.login("longfei", 2, "");
	
	未知授权方式(访问地址貌似局方的)
	aa= contentView.httpLoginCanForword("http://10.0.0.8:8080/Seal/general/interface/androidclientlogin.jsp", "usesr");
	int login=contentView.login("longfei", 2, "");
	*/
	
	
	/** 添加手写印章 */
	/*case 2://弹出手写窗体返回
	if(resultCode==1){//确定按钮
		if(extras==null){
			return ;
		}
		//String writeData=extras.getString("writeData");
		String png64Str = extras.getString("getLastpic");
		if(png64Str!=null||!png64Str.equals("")){
			byte[] pngData = Base64.decode(png64Str);
			Bitmap pngBmp = BitmapFactory.decodeByteArray(pngData, 0, pngData.length);
			*//** 将手写数据保存为bmp印章图片 *//*
			contentView.bmpToSeal(pngBmp, "/mnt/sdcard/dianju");
			String[] postionInfo=(String[])userInfos.get(currUser);
			int pageIdex=Integer.valueOf(postionInfo[2]);
			int x=Integer.valueOf(postionInfo[3]);
			int y=Integer.valueOf(postionInfo[4]);
			int w=Integer.valueOf(postionInfo[5]);
			int h=Integer.valueOf(postionInfo[6]);
			*//** 加盖手写印章 *//*
			contentView.addHandSeal(pageIdex, x+w/2, y+h/2, "/mnt/sdcard/dianju", pfxPath, pfxPwd, 1);
			contentView.fresh();
			//contentView.pasteNodesEx(writeData,pageIdex,w,h,x,y);
		}
	}
	break;*/

}
/**
 * 
 * 节点类型：
 * type = core.getValueEx(this.core.objID, this.currNodeName, 12, "", 0, "")
 * 
 * 日期节点
 * core.getValueEx(this.core.objID, this.currNodeName, 2, "", 1, "")//对于日期控件，1-取出来是格式化的日期，会带有'-'
 * core.getValueEx(this.core.objID, this.currNodeName, 2, "", 0, "")//看到什么就取到什么
 * 
 * 下拉控件(type=242)
 * getValueEx(contentView.currNodeName, 2, "", 0, "")//当前选中项内容
 * getValueEx(contentView.currNodeName, 1, "", 1, "")//第一项内容
 * getValueEx(contentView.currNodeName, 1, "", 2, "")//第二项内容
 * 
 */

