package com.wy.fdreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wy.fdreader.utils.Constant;
import com.wy.fdreader.activity.OpenFileActivity;
import com.wy.fdreader.R;
import com.wy.fdreader.entity.OpenedFiles;
import com.wy.fdreader.fragment.FileFragment;

import java.util.List;

public class OpenedFilesAdapter extends BaseAdapter {

	private Context context;
	private FileFragment fragmentContext;
	private List<OpenedFiles> openedFiles = null;
	
	public OpenedFilesAdapter(Context context,FileFragment fragmentContext,List<OpenedFiles> openedFiles) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.fragmentContext = fragmentContext;
		this.openedFiles = openedFiles;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return openedFiles.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return openedFiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("null")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.file_info, null);
			holder.file_thum = (ImageView) convertView.findViewById(R.id.file_thum);
			holder.file_name = (TextView) convertView.findViewById(R.id.file_name);
			holder.time_size = (TextView) convertView.findViewById(R.id.time_size);
			holder.file_path = (TextView) convertView.findViewById(R.id.file_path);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		//加载获取到的文件信息
		final OpenedFiles openedfile = openedFiles.get(position);
		holder.file_name.setText(openedfile.getFile_name());
		holder.time_size.setText(openedfile.getOpen_time() + "  " + openedfile.getFile_size());
		holder.file_path.setText(openedfile.getFile_path());
		//将数据库中缩略图二进制数据转为bitmap对象
		byte[] thum = openedfile.getFile_thum();
		if (thum != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(thum, 0, thum.length);//byte转bitmap
//			BitmapDrawable bd= new BitmapDrawable(context.getResources(), bitmap);//bitmap转drawable
			holder.file_thum.setImageBitmap(bitmap);
		}
		
		holder.file_name.setTextSize(20);
		holder.time_size.setTextColor(Color.parseColor("#CCCCCC"));
		holder.file_path.setTextColor(Color.parseColor("#CCCCCC"));
		
		//点击事件
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(context, OpenFileActivity.class);
				intent.putExtra("filePath", openedfile.getFile_path());
//				context.startActivity(intent);
				//刷新文件列表
				fragmentContext.startActivityForResult(intent, Constant.RequestCode.FRESH_LIST);
			}
		});
		
		return convertView;
	}
	public class ViewHolder{
		ImageView file_thum;
		TextView file_name;
		TextView time_size;
		TextView file_path;
	}

}
