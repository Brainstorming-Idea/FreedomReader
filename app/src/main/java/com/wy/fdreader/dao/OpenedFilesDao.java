package com.wy.fdreader.dao;

import java.util.List;

import com.wy.fdreader.entity.OpenedFiles;

public interface OpenedFilesDao {

	void addOpenedFile(OpenedFiles openedFile);//增
	void deleteOpenedFile();//删
	void updateOpenedFile(OpenedFiles openedFile);//改
	List<OpenedFiles> readOpenedFile();//查
}
