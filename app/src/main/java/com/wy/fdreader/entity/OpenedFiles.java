package com.wy.fdreader.entity;

import java.io.Serializable;
import java.sql.Blob;

public class OpenedFiles implements Serializable {

	//定义SerializableUID
	private static final long SerializableUID = 12345678L;
	private String id;
	private String file_name;
	private String file_path;
	private String open_time;
	private String file_size;
	private byte[] file_thum;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getFile_path() {
		return file_path;
	}
	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}
	public String getOpen_time() {
		return open_time;
	}
	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}
	public String getFile_size() {
		return file_size;
	}
	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}
	public byte[] getFile_thum() {
		return file_thum;
	}
	public void setFile_thum(byte[] file_thum) {
		this.file_thum = file_thum;
	}
	
	public OpenedFiles() {
		super();
	}
	public OpenedFiles(String id, String file_name, String file_path, String open_time, String file_size, byte[] file_thum) {
		super();
		this.id = id;
		this.file_name = file_name;
		this.file_path = file_path;
		this.open_time = open_time;
		this.file_size = file_size;
		this.file_thum = file_thum;
	}
}
