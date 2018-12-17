package com.clf.clftool.so.other;
import java.util.List;

public class JniResult {

	public JniResult() {
		iResult = 0;
		errorCode = 0L;
		byteResult = null;
		listByteResult = null;
		boolResult = false;
		strResult = null;
	}

	public int getiResult() {
		return iResult;
	}

	public void setiResult(int iResult) {
		this.iResult = iResult;
	}

	public byte[] getByteResult() {
		return byteResult;
	}

	public void setByteResult(byte byteResult[]) {
		this.byteResult = byteResult;
	}

	public List getListByte() {
		return listByteResult;
	}

	public void setListByte(List listByteResult) {
		this.listByteResult = listByteResult;
	}

	public boolean getBoolResult() {
		return boolResult;
	}

	public void setBoolResult(boolean boolResult) {
		this.boolResult = boolResult;
	}

	public String getStringResult() {
		return strResult;
	}

	public void setStringResult(String strResult) {
		this.strResult = strResult;
	}

	public long getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}

	public JniResult initJniResult() {
		JniResult jniResult = new JniResult();
		return jniResult;
	}

	private int iResult;
	private long errorCode;
	private byte byteResult[];
	private List listByteResult;
	private boolean boolResult;
	private String strResult;
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\Users\LONGFEI\workspace\POC_yinhang\libs\SCSP.jar
	Total time: 182 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/