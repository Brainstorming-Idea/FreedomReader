package com.wy.fdreader.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dianju.trustedsign.utils.ClfUtil;
import com.wy.fdreader.utils.Constant;
import com.wy.fdreader.activity.MyFileManager;
import com.wy.fdreader.activity.OpenFileActivity;
import com.wy.fdreader.R;
import com.wy.fdreader.adapter.OpenedFilesAdapter;
import com.wy.fdreader.dao.OpenedFilesDao;
import com.wy.fdreader.dao.impl.OpenedFilesDaoImpl;
import com.wy.fdreader.entity.OpenedFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFragment extends Fragment {

	private Context context;
	private FileFragment fragmentContext;
	private ListView lately_file_list;
	private Button btnOpenFile;
	/*private String filePath = "";
	private String filename = "";
	private String filesize = "";
	private String opentime = "";
	private byte[] filethum = null;*/
	
	private List<OpenedFiles> filesInfos = new ArrayList<OpenedFiles>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_file, null);
		context = getActivity();
		fragmentContext = this;
		initView(view);
		init();
		return view;
	}
	
	private void init() {
		OpenedFilesDao openedFilesDao = new OpenedFilesDaoImpl(context);
		// 获取最近打开的文件
		//首先删除超过一周的文件
		openedFilesDao.deleteOpenedFile();
		filesInfos = openedFilesDao.readOpenedFile();
		//适配器
		OpenedFilesAdapter openAdapter = new OpenedFilesAdapter(context, fragmentContext,filesInfos);
		lately_file_list.setAdapter(openAdapter);
	}

	void initView(View view) {
		lately_file_list = (ListView) view.findViewById(R.id.lately_file_list);
		btnOpenFile = (Button) view.findViewById(R.id.imageButton1);
		btnOpenFile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String dir = ClfUtil.getSPString(context, Constant.OPEN_PATH, Constant.OPEN_PATH_DEFAULT);
				Intent intent = new Intent(context, MyFileManager.class);
				intent.putExtra("type", "0");
				intent.putExtra("dir", dir);
				startActivityForResult(intent, Constant.RequestCode.NEW_OPEN);
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==Constant.RequestCode.NEW_OPEN) {
			if(resultCode==1) {
				String savePath = data.getStringExtra("savePath");
				String fileName = data.getStringExtra("fileName");
				ClfUtil.addSP(context, Constant.OPEN_PATH, savePath);
				if(fileName != null && (fileName.endsWith(".aip") || fileName.endsWith(".pdf") || fileName.endsWith(".ofd"))) {
					String filePath1 = savePath+File.separator+fileName;
					Intent intent = new Intent(context, OpenFileActivity.class);
					intent.putExtra("filePath", filePath1);
					startActivityForResult(intent, Constant.RequestCode.FRESH_LIST);
				} else {
					Toast.makeText(context, "文件类型错误！", Toast.LENGTH_SHORT).show();
				}
			}
		} else if(requestCode==Constant.RequestCode.FRESH_LIST) {
			OpenedFilesDao openedFilesDao = new OpenedFilesDaoImpl(context);
			filesInfos = openedFilesDao.readOpenedFile();
			//适配器
			OpenedFilesAdapter openAdapter = new OpenedFilesAdapter(context, fragmentContext, filesInfos);
			lately_file_list.setAdapter(openAdapter);
		}
	}
	
}
