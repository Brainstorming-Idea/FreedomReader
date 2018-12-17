package com.wy.fdreader.test;

import java.util.ArrayList;
import java.util.List;

import com.wy.fdreader.dao.OpenedFilesDao;
import com.wy.fdreader.dao.impl.OpenedFilesDaoImpl;
import com.wy.fdreader.entity.OpenedFiles;

import android.test.AndroidTestCase;
import android.util.Log;

@SuppressWarnings("deprecation")
public class OpenedFileTest extends AndroidTestCase {

	public void testAddFile() {
		OpenedFiles openedFile = new OpenedFiles();
		openedFile.setFile_name("pdf.pdf");
		openedFile.setFile_path("dianju/pdf.pdf");
		openedFile.setOpen_time("2018-05-20 10:00:00");
		openedFile.setFile_size("100kb");
		OpenedFilesDao openedFilesDao = new OpenedFilesDaoImpl(getContext());
		for (int i = 0; i < 4; i++) {
			openedFilesDao.addOpenedFile(openedFile);
		}
	}
	
	public void testReadOpenedFile(){
		List<OpenedFiles> filesInfos = new ArrayList<OpenedFiles>();
		OpenedFilesDao openedFilesDao = new OpenedFilesDaoImpl(getContext());
		filesInfos = openedFilesDao.readOpenedFile();
		Log.i("fileInfo", filesInfos.get(1).getFile_name()+"--"+filesInfos.get(1).getFile_path());
	}
	
	public void testDeleteOpenedFile(){
		OpenedFilesDao openedFilesDao = new OpenedFilesDaoImpl(getContext());
		openedFilesDao.deleteOpenedFile();
	}

}
