package com.dianju.trustedsign.net.urlconnection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;


public class AppConnection {
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	
	private static int CONNECTION_TIMEOIUT = 20000;//默认20秒
	private static int CACHE_SIZE = 51200;
	
	private static String CHARSET = HTTP.UTF_8;
	private static Map<String, String> requestPropertyMap;//请求头
	static {
		requestPropertyMap = new HashMap<String, String>();
		requestPropertyMap.put("Charset", CHARSET);
		//conn.setRequestProperty("Content-Type",  "application/json");
	}
	
	/**
	 * 发送请求
	 * @auther chenlf3
	 * @date 2017年7月25日 下午1:32:14
	 * @param address 地址
	 * @param params 入参
	 * @param type http类型
	 * @return
	 * @throws IOException
	 */
	public static AppResponse sendRequestJSON(String address, JSONObject params, String type) throws IOException {
		if(TextUtils.isEmpty(address)) {
			AppResponse res = new AppResponse(-996, "address空的地址");
			return res;
		}
		if(TextUtils.isEmpty(type)) {
			type = POST;
		}
		if(GET.equalsIgnoreCase(type)) {
			return sendGetJSON(address, params);
		} else if(POST.equalsIgnoreCase(type) || PUT.equalsIgnoreCase(type)) {
			return sendTypeJSON(address, params, type);
		}else {
			AppResponse res = new AppResponse(-998, "未定义的请求类型type="+type);
			return res;
		}
	}
	
	/**
	 * 发送请求
	 * @auther chenlf3
	 * @date 2017年7月25日 下午1:33:05
	 * @param address 地址
	 * @param params 入参
	 * @param type http类型
	 * @return
	 * @throws IOException
	 */
	public static AppResponse sendRequest(String address, Map<String, String> params, String type) throws IOException {
		if(TextUtils.isEmpty(address)) {
			AppResponse res = new AppResponse(-996, "address空的地址");
			return res;
		}
		if(TextUtils.isEmpty(type)) {
			type = POST;
		}
		if(GET.equalsIgnoreCase(type)) {
			return sendGet(address, params);
		} else if(POST.equalsIgnoreCase(type) || PUT.equalsIgnoreCase(type)) {
			return sendType(address, params, type);
		}else {
			AppResponse res = new AppResponse(-998, "未定义的请求类型type="+type);
			return res;
		}
	}
	
	/**
	 * 下载文件
	 * @auther chenlf3
	 * @date 2017年7月25日 下午1:36:26
	 * @param address 下载地址
	 * @param params 入参
	 * @param type http类型
	 * @return
	 * @throws IOException 
	 */
	public static AppResponse download(String address, Map<String, String> params, String type, String saveDir) throws IOException {
		if(TextUtils.isEmpty(address)) {
			AppResponse res = new AppResponse(-996, "address空的地址");
			return res;
		}
		if(TextUtils.isEmpty(type)) {
			type = POST;
		}
		if(GET.equalsIgnoreCase(type)) {
			return downloadGet(address, params, saveDir);
		} else if(POST.equalsIgnoreCase(type) || PUT.equalsIgnoreCase(type)) {
			return downloadType(address, params, type, saveDir);
		}else {
			AppResponse res = new AppResponse(-998, "未定义的请求类型type="+type);
			return res;
		}
	}
	
	/**
	 * 上传文件
	 * @auther chenlf3
	 * @date 2017年7月25日 下午5:57:52
	 * @param address 地址
	 * @param params 参数
	 * @param type 类型
	 * @param file 文件
	 * @return
	 * @throws IOException
	 */
	public static AppResponse upload(String address, Map<String, String> params, String type, File file) throws IOException {
		if(TextUtils.isEmpty(address)) {
			AppResponse res = new AppResponse(-996, "address空的地址");
			return res;
		}
		if(TextUtils.isEmpty(type)) {
			type = POST;
		}
		if(GET.equalsIgnoreCase(type)) {
			return uploadGet(address, params, file);
		} else if(POST.equalsIgnoreCase(type) || PUT.equalsIgnoreCase(type)) {
			return uploadType(address, params, type, file);
		}else {
			AppResponse res = new AppResponse(-998, "未定义的请求类型type="+type);
			return res;
		}
	}
	
	/**
	 * 上传文件到ftp(需引入外部包commons-net-1.4.1.jar)
	 * @auther chenlf3
	 * @date 2017年7月26日 上午10:44:02
	 * @param ftpAddress
	 * @param port
	 * @param username
	 * @param password
	 * @param fileName
	 * @param file 待上传的文件
	 * @return
	 * @throws IOException 
	 */
	public static AppResponse sendFileToFTP(String ip, int port, String path, String username, String password, String fileName, File file) throws IOException {
		AppResponse res = null;
		if(file == null || (!file.exists()) || file.length()<=0) {
			res = new AppResponse(-995, "ftp-待上传文件无效！");
			return res;
		}
		if(TextUtils.isEmpty(fileName)) {
			res = new AppResponse(-995, "ftp-fileName空值!");
			return res;
		}
		FTPClient ftp = new FTPClient();
		ftp.connect(ip,port);
        boolean loginRes = ftp.login(username,password);
        if(!loginRes) {
        	res = new AppResponse(-995, "ftp-登陆失败！");
			return res;
        }
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        int reply = ftp.getReplyCode();      
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            res = new AppResponse(-995, "ftp-响应失败！");
			return res;
        }
        boolean changeRes = ftp.changeWorkingDirectory(path);
        if(!changeRes) {
        	ftp.disconnect();
        	res = new AppResponse(-995, "ftp-切换目录失败！");
			return res;
        }
        InputStream input = new FileInputStream(file);
        //ftp.enterLocalPassiveMode();
        boolean storeRes = ftp.storeFile(fileName, input);
        input.close();
        ftp.disconnect();
        if(storeRes) {
        	res = new AppResponse(200, "ftp-上传成功");
			return res;
        } else {
        	res = new AppResponse(-995, "ftp-上传文件失败");
			return res;
        }
	}
	
	/**
	 * 上传文件到ftp(需引入外部包commons-net-1.4.1.jar)
	 * @auther chenlf3
	 * @date 2017年7月26日 上午10:44:49
	 * @param ip 211.167.101.11
	 * @param port 21
	 * @param path 要上传的具体目录
	 * @param username
	 * @param password
	 * @param fileName 服务器存储文件名字
	 * @param fileData
	 * @return
	 * @throws IOException 
	 */
	public static AppResponse sendFileToFTP(String ip, int port, String path, String username, String password, String fileName, byte[] fileData) throws IOException {
		AppResponse res = null;
		if(fileData == null || fileData.length<=0) {
			res = new AppResponse(-995, "ftp-待上传数据无效！");
			return res;
		}
		if(TextUtils.isEmpty(fileName)) {
			res = new AppResponse(-995, "ftp-fileName空值!");
			return res;
		}
		FTPClient ftp = new FTPClient();
		ftp.connect(ip,port);
        boolean loginRes = ftp.login(username,password);
        if(!loginRes) {
        	res = new AppResponse(-995, "ftp-登陆失败！");
			return res;
        }
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        int reply = ftp.getReplyCode();      
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            res = new AppResponse(-995, "ftp-响应失败！");
			return res;
        }
        boolean changeRes = ftp.changeWorkingDirectory(path);
        if(!changeRes) {
        	ftp.disconnect();
        	res = new AppResponse(-995, "ftp-切换目录失败！");
			return res;
        }
        ByteArrayInputStream input = new ByteArrayInputStream(fileData);
        //ftp.enterLocalPassiveMode();
        boolean storeRes = ftp.storeFile(fileName, input);
        input.close();
        ftp.disconnect();
        if(storeRes) {
        	res = new AppResponse(200, "ftp-上传成功");
			return res;
        } else {
        	res = new AppResponse(-995, "ftp-上传文件失败");
			return res;
        }
	}
	
	/**
	 * 连接ftp服务器
	 * @auther chenlf3
	 * @date 2017年7月26日 上午10:52:31
	 * @param ftp
	 * @param path apache-tomcat/webapps/voucher/wzh/
	 * @param addr
	 * @param port
	 * @param username
	 * @param password
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
	private static boolean connectFtp(FTPClient ftp, String path,String ip,int port,String username,String password) throws IOException {
        ftp.connect(ip,port);
        boolean loginRes = ftp.login(username,password);
        if(!loginRes) return false;
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        int reply = ftp.getReplyCode();      
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }
        boolean res = ftp.changeWorkingDirectory(path);
        if(!res) {
        	ftp.disconnect();
            return false;
        }
        return true;
	}
	
	/** json对象发送 */
	private static AppResponse sendTypeJSON(String address, JSONObject params, String type) throws IOException {
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http方式访问
		conn.setConnectTimeout(CONNECTION_TIMEOIUT);
		conn.setRequestMethod(type);
		if(requestPropertyMap != null && requestPropertyMap.size()>0) {
			for(Map.Entry<String,String> entry:requestPropertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
		}
		conn.setDoOutput(true); //允许传参数
		conn.setDoInput(true); //允许返回值
        //写入参数
		if(params!=null && params.length()>0) {
			String data = params.toString();
			BufferedOutputStream bos = new BufferedOutputStream(conn.getOutputStream());
			bos.write(data.getBytes(CHARSET));
			bos.close();
		}
		int code = conn.getResponseCode();
		//读取返回数据
		BufferedInputStream bis = null;
		if(code==200) {
			bis = new BufferedInputStream(conn.getInputStream());
		} else {
			bis = new BufferedInputStream(conn.getErrorStream());
		}
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		while((len = bis.read(buffer)) != -1) {
			byteos.write(buffer,0,len);
		}
		bis.close();
		String content = null;
		if(byteos!=null && byteos.size()>0) content = new String(byteos.toByteArray(),CHARSET);
		AppResponse res = new AppResponse(code, content);
		return res;
	}
	
	private static AppResponse sendGetJSON(String address, JSONObject params) throws IOException {
		String suffix = "";
		try {
			suffix = getParams(params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppResponse res = new AppResponse(-999, "params参数解析错误！");
			return res;
		}
		URL url = new URL(address+suffix);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http方式访问
		conn.setConnectTimeout(CONNECTION_TIMEOIUT);
		conn.setRequestMethod(GET);
		if(requestPropertyMap != null && requestPropertyMap.size()>0) {
			for(Map.Entry<String,String> entry:requestPropertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
		}
		conn.setDoOutput(false); //允许传参数
		conn.setDoInput(true); //允许返回值
		int code = conn.getResponseCode();
		//读取返回数据
		BufferedInputStream bis = null;
		if(code==200) {
			bis = new BufferedInputStream(conn.getInputStream());
		} else {
			bis = new BufferedInputStream(conn.getErrorStream());
		}
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		while((len = bis.read(buffer)) != -1) {
			byteos.write(buffer,0,len);
		}
		bis.close();
		String content = null;
		if(byteos!=null && byteos.size()>0) content = new String(byteos.toByteArray(),CHARSET);
		AppResponse res = new AppResponse(code, content);
		return res;
	}
	
	/** 参数形式发送 */
	private static AppResponse sendType(String address, Map<String, String> params, String type) throws IOException {
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http方式访问
		conn.setConnectTimeout(CONNECTION_TIMEOIUT);
		conn.setRequestMethod(type);
		if(requestPropertyMap != null && requestPropertyMap.size()>0) {
			for(Map.Entry<String,String> entry:requestPropertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
		}
		conn.setDoOutput(true); //允许传参数
		conn.setDoInput(true); //允许返回值
        //写入参数
		if(params!=null && params.size()>0) {
			//String data = "machineInfo="+URLEncoder.encode(machineInfo,"UTF-8")+"&mng_id="+URLEncoder.encode(user_id,"UTF-8");
			String data = "";
			StringBuilder sb = new StringBuilder();
			Set<String> sets = params.keySet();
			for(String key:sets) {
				String value = params.get(key);
				sb.append(key).append("=").append(TextUtils.isEmpty(value)?"":value).append("&");
			}
			sb.deleteCharAt(sb.length()-1);
			data = sb.toString();
			BufferedOutputStream bos = new BufferedOutputStream(conn.getOutputStream());
			bos.write(data.getBytes(CHARSET));
			bos.close();
		}
		int code = conn.getResponseCode();
		//读取返回数据
		BufferedInputStream bis = null;
		if(code==200) {
			bis = new BufferedInputStream(conn.getInputStream());
		} else {
			bis = new BufferedInputStream(conn.getErrorStream());
		}
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		while((len = bis.read(buffer)) != -1) {
			byteos.write(buffer,0,len);
		}
		bis.close();
		String content = null;
		if(byteos!=null && byteos.size()>0) content = new String(byteos.toByteArray(),CHARSET);
		AppResponse res = new AppResponse(code, content);
		return res;
	}
	
	private static AppResponse sendGet(String address, Map<String, String> params) throws IOException {
		String suffix = getParams(params);
		URL url = new URL(address+suffix);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http方式访问
		conn.setConnectTimeout(CONNECTION_TIMEOIUT);
		conn.setRequestMethod(GET);
		if(requestPropertyMap != null && requestPropertyMap.size()>0) {
			for(Map.Entry<String,String> entry:requestPropertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
		}
		conn.setDoOutput(false); //允许传参数
		conn.setDoInput(true); //允许返回值
		int code = conn.getResponseCode();
		//读取返回数据
		BufferedInputStream bis = null;
		if(code==200) {
			bis = new BufferedInputStream(conn.getInputStream());
		} else {
			bis = new BufferedInputStream(conn.getErrorStream());
		}
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		while((len = bis.read(buffer)) != -1) {
			byteos.write(buffer,0,len);
		}
		bis.close();
		String content = null;
		if(byteos!=null && byteos.size()>0) content = new String(byteos.toByteArray(),CHARSET);
		AppResponse res = new AppResponse(code, content);
		return res;
	}
	
	/** 下载文件 
	 * @throws IOException */
	private static AppResponse downloadType(String address, Map<String, String> params, String type, String saveDir) throws IOException {
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http方式访问
		conn.setConnectTimeout(CONNECTION_TIMEOIUT);
		conn.setRequestMethod(type);
		if(requestPropertyMap != null && requestPropertyMap.size()>0) {
			for(Map.Entry<String,String> entry:requestPropertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
		}
		conn.setDoOutput(true); //允许传参数
		conn.setDoInput(true); //允许返回值
        //写入参数
		if(params!=null && params.size()>0) {
			//String data = "machineInfo="+URLEncoder.encode(machineInfo,"UTF-8")+"&mng_id="+URLEncoder.encode(user_id,"UTF-8");
			String data = "";
			StringBuilder sb = new StringBuilder();
			Set<String> sets = params.keySet();
			for(String key:sets) {
				String value = params.get(key);
				sb.append(key).append("=").append(TextUtils.isEmpty(value)?"":value).append("&");
			}
			sb.deleteCharAt(sb.length()-1);
			data = sb.toString();
			BufferedOutputStream bos = new BufferedOutputStream(conn.getOutputStream());
			bos.write(data.getBytes(CHARSET));
			bos.close();
		}
		int code = conn.getResponseCode();
		//读取返回数据
		BufferedInputStream bis = null;
		if(code==200) {
			/** 获取数据的长度 */
    		int length = conn.getContentLength();
    		// 文件名
    		String nameStr = conn.getHeaderField("Content-Disposition");
    		String fileName = getFileName(nameStr);
    		//判断目录是否存在
            File file = new File(saveDir);
            if ((!file.exists()) || (file.exists() && file.isFile())) {
                file.mkdirs();
            }
            bis = new BufferedInputStream(conn.getInputStream());
            String path = saveDir + File.separator + fileName;
            OutputStream out = new FileOutputStream(path);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            bis.close();
            out.close();
            AppResponse res = new AppResponse(code, fileName);
            return res;
		} else {
			bis = new BufferedInputStream(conn.getErrorStream());
			ByteArrayOutputStream byteos = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while((len = bis.read(buffer)) != -1) {
				byteos.write(buffer,0,len);
			}
			bis.close();
			String content = null;
			if(byteos!=null && byteos.size()>0) content = new String(byteos.toByteArray(),CHARSET);
			AppResponse res = new AppResponse(code, content);
			return res;
		}
	}
	
	private static AppResponse downloadGet(String address, Map<String, String> params, String saveDir) throws IOException {
		String suffix = getParams(params);
		URL url = new URL(address+suffix);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http方式访问
		conn.setConnectTimeout(CONNECTION_TIMEOIUT);
		conn.setRequestMethod(GET);
		if(requestPropertyMap != null && requestPropertyMap.size()>0) {
			for(Map.Entry<String,String> entry:requestPropertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
		}
		conn.setDoOutput(false); //允许传参数
		conn.setDoInput(true); //允许返回值
		int code = conn.getResponseCode();
		//读取返回数据
		BufferedInputStream bis = null;
		if(code==200) {
			/** 获取数据的长度 */
    		int length = conn.getContentLength();
    		// 文件名
    		String nameStr = conn.getHeaderField("Content-Disposition");
    		String fileName = getFileName(nameStr);
    		//判断目录是否存在
            File file = new File(saveDir);
            if ((!file.exists()) || (file.exists() && file.isFile())) {
                file.mkdirs();
            }
            bis = new BufferedInputStream(conn.getInputStream());
            String path = saveDir + File.separator + fileName;
            OutputStream out = new FileOutputStream(path);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            bis.close();
            out.close();
            AppResponse res = new AppResponse(code, fileName);
            return res;
		} else {
			bis = new BufferedInputStream(conn.getErrorStream());
			ByteArrayOutputStream byteos = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while((len = bis.read(buffer)) != -1) {
				byteos.write(buffer,0,len);
			}
			bis.close();
			String content = null;
			if(byteos!=null && byteos.size()>0) content = new String(byteos.toByteArray(),CHARSET);
			AppResponse res = new AppResponse(code, content);
			return res;
		}
	}
	
	/** 上传文件到服务器 
	 * @throws IOException */
	private static AppResponse uploadType(String address, Map<String, String> params, String type, File file) throws IOException {
		if(params==null || params.size()<=0) return uploadType(address, type, file);
		String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\r\n";
		String PREFIX = "--";
	    String LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; //内容类型
		if(file==null || (!file.exists()) || file.length()<=0) {
			AppResponse res = new AppResponse(-997, "无效文件");
			return res;
		}
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http方式访问
		conn.setChunkedStreamingMode(CACHE_SIZE);//用于传输大文件
		conn.setConnectTimeout(CONNECTION_TIMEOIUT);
		conn.setRequestMethod(type);
		conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
		if(requestPropertyMap != null && requestPropertyMap.size()>0) {
			for(Map.Entry<String,String> entry:requestPropertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
		}
		conn.setDoOutput(true); //允许传参数
		conn.setDoInput(true); //允许返回值
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        //发送参数
		if(params!=null && params.size()>0) {
			// 首先组拼文本类型的参数
        	StringBuffer sb = new StringBuffer();
            sb.append(LINE_END);
            for(Map.Entry<String,String> entry:params.entrySet()){                        
                sb.append(PREFIX).append(BOUNDARY).append(LINE_END);//分界符
                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                sb.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                sb.append(LINE_END);
                sb.append(entry.getValue());
                sb.append(LINE_END);//换行！
            }
            dos.write(sb.toString().getBytes(CHARSET));
		}
		// 构建发送字符串数据
        StringBuffer filesb = new StringBuffer();
        filesb.append(PREFIX);//开始拼接文件参数
        filesb.append(BOUNDARY);
        filesb.append(LINE_END);
        /**发送文件
         * 这里重点注意：
         * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
         * filename是文件的名字，包含后缀名的 比如:abc.png
         */
        filesb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+file.getName()+"\""+LINE_END);
        filesb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
        filesb.append(LINE_END);
        dos.write(filesb.toString().getBytes(CHARSET));
        InputStream is = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        int len = 0;
        while((len=is.read(bytes))!=-1){
            dos.write(bytes, 0, len);
        }
        is.close();
        dos.write(LINE_END.getBytes());//一定还有换行
        byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes(CHARSET);
        dos.write(end_data);
        dos.flush();
        dos.close();
		int code = conn.getResponseCode();
		//读取返回数据
		BufferedInputStream bis = null;
		if(code==200) {
			bis = new BufferedInputStream(conn.getInputStream());
		} else {
			bis = new BufferedInputStream(conn.getErrorStream());
		}
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		len = 0;
		byte[] buffer = new byte[1024];
		while((len = bis.read(buffer)) != -1) {
			byteos.write(buffer,0,len);
		}
		bis.close();
		String content = null;
		if(byteos!=null && byteos.size()>0) content = new String(byteos.toByteArray(),CHARSET);
		AppResponse res = new AppResponse(code, content);
		return res;
	}
	
	private static AppResponse uploadType(String address, String type, File file) throws IOException {
		if(file==null || (!file.exists()) || file.length()<=0) {
			AppResponse res = new AppResponse(-997, "无效文件");
			return res;
		}
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http方式访问
		conn.setChunkedStreamingMode(CACHE_SIZE);//用于传输大文件
		conn.setConnectTimeout(CONNECTION_TIMEOIUT);
		conn.setRequestMethod(type);
		if(requestPropertyMap != null && requestPropertyMap.size()>0) {
			for(Map.Entry<String,String> entry:requestPropertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
		}
		conn.setDoOutput(true); //允许传参数
		conn.setDoInput(true); //允许返回值
        //写入文件
		BufferedOutputStream bos = new BufferedOutputStream(conn.getOutputStream());
		InputStream is = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        int len = 0;
        while((len=is.read(bytes))!=-1){
        	bos.write(bytes, 0, len);
        }
        is.close();
        bos.close();
		int code = conn.getResponseCode();
		//读取返回数据
		BufferedInputStream bis = null;
		if(code==200) {
			bis = new BufferedInputStream(conn.getInputStream());
		} else {
			bis = new BufferedInputStream(conn.getErrorStream());
		}
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		len = 0;
		byte[] buffer = new byte[1024];
		while((len = bis.read(buffer)) != -1) {
			byteos.write(buffer,0,len);
		}
		bis.close();
		String content = null;
		if(byteos!=null && byteos.size()>0) content = new String(byteos.toByteArray(),CHARSET);
		AppResponse res = new AppResponse(code, content);
		return res;
	}
	
	private static AppResponse uploadGet(String address, Map<String, String> params, File file) throws IOException {
		if(file==null || (!file.exists()) || file.length()<=0) {
			AppResponse res = new AppResponse(-997, "无效文件");
			return res;
		}
		String suffix = getParams(params);
		URL url = new URL(address+suffix);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http方式访问
		conn.setChunkedStreamingMode(CACHE_SIZE);//用于传输大文件
		conn.setConnectTimeout(CONNECTION_TIMEOIUT);
		conn.setRequestMethod(GET);
		if(requestPropertyMap != null && requestPropertyMap.size()>0) {
			for(Map.Entry<String,String> entry:requestPropertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
		}
		conn.setDoOutput(false); //允许传参数
		conn.setDoInput(true); //允许返回值
        //写入文件
		BufferedOutputStream bos = new BufferedOutputStream(conn.getOutputStream());
		InputStream is = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        int len = 0;
        while((len=is.read(bytes))!=-1){
        	bos.write(bytes, 0, len);
        }
        is.close();
        bos.close();
		int code = conn.getResponseCode();
		//读取返回数据
		BufferedInputStream bis = null;
		if(code==200) {
			bis = new BufferedInputStream(conn.getInputStream());
		} else {
			bis = new BufferedInputStream(conn.getErrorStream());
		}
		ByteArrayOutputStream byteos = new ByteArrayOutputStream();
		len = 0;
		byte[] buffer = new byte[1024];
		while((len = bis.read(buffer)) != -1) {
			byteos.write(buffer,0,len);
		}
		bis.close();
		String content = null;
		if(byteos!=null && byteos.size()>0) content = new String(byteos.toByteArray(),CHARSET);
		AppResponse res = new AppResponse(code, content);
		return res;
	}
	
	/** 涉及方法 */
	private static String getParams(JSONObject params) throws JSONException {
		if(params==null || params.length()<=0) return "";
		Iterator<String> it = params.keys();
		StringBuilder temp = new StringBuilder("?");
		while(it.hasNext()) {
			String key = (String) it.next();  
            String value = params.getString(key);
			try {
				temp.append(key).append("=").append(value==null?"":URLEncoder.encode(value,"UTF-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				 //TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String res = temp.toString();
		if(res.endsWith("&")) {
			return res.substring(0, res.length()-1);
		} else {
			return res;
		}
	}
	
	private static String getParams(Map<String,String> map) {
		if(map == null) return null;
		Set<String> keys = map.keySet();
		if(keys.size() == 0) return null;
		StringBuilder temp = new StringBuilder("?");
		for(String key:keys) {
			try {
				temp.append(key).append("=").append(map.get(key)==null?"":URLEncoder.encode(map.get(key),HTTP.UTF_8)).append("&");
				//temp.append(key).append("=").append(map.get(key)).append("&");
			} catch (UnsupportedEncodingException e) {
				 //TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String res = temp.toString();
		if(res.endsWith("&")) {
			return res.substring(0, res.length()-1);
		} else {
			return res;
		}
	}
	
	private static String getFileName(String nameStr) {
		String res = "default";
		if(TextUtils.isEmpty(nameStr)) return res;
		int index = nameStr.indexOf("filename");
		if(index == -1) return res;
		String fileName = nameStr.substring(index, nameStr.length());
		if(TextUtils.isEmpty(fileName) || (!fileName.contains("="))) {
			return res;
		}
		String[] names = fileName.split("=");
		if(names.length<2 || TextUtils.isEmpty(names[1])) return res;
		fileName = names[1].trim();
		return fileName;
	}
}
