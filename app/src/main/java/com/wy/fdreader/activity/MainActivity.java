package com.wy.fdreader.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dianju.trustedsign.utils.ClfUtil;
import com.wy.fdreader.R;
import com.wy.fdreader.fragment.FileFragment;
import com.wy.fdreader.fragment.MeFragment;
import com.wy.fdreader.fragment.SoftFragment;
import com.wy.fdreader.utils.Constant;
import com.wy.fdreader.utils.PermissionUtil;
import com.wy.fdreader.utils.updateUtil.AppUpdateUtil;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {

	private Context context;
	private PermissionUtil permissionUtil;
	private AppUpdateUtil updateUtil;
	private LinearLayout mainConLayout;
	private LinearLayout mainBackLayout;
	private LinearLayout btnFile,btnSoft,btnMe;//三个菜单按钮
	private int currIndex;//当前选中菜单项
	private FragmentManager manager;//片段管理器
	private Fragment fileFragment,softFragment,meFragment;
	private ImageView filePic,softPic,mePic;
	private TextView fileTxt,softTxt,meTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("onCreate","onCreate");
		setContentView(R.layout.activity_main);
		context = this;
		this.mainConLayout = (LinearLayout) this.findViewById(R.id.main_con);
		this.mainBackLayout = (LinearLayout) this.findViewById(R.id.main_back);
		updateUtil = new AppUpdateUtil(context,true);
		permissionUtil = new PermissionUtil(context);
		if(permissionUtil.hasAllPermission()) {
			//初始化工作
			init();
			//checkAppUpdate();
		} else {
			permissionUtil.requestPermissions(permissionUtil.getNoOwnedPermission(), Constant.RequestCode.PERMISSIONS_RESULT);
		}

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				MainActivity.this.finish();
//			}
//		}).start();
	}
	@Override
	protected void onStart() {
		super.onStart();
		Log.e("onStart","onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("onResume","onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("onPause","onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e("onStop","onStop");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e("onRestart","onRestart");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("onDestroy","onDestroy");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.e("onSaveInstanceState","onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.e("onRestoreInstanceState","onRestoreInstanceState");
	}
	/**
	 * 检查app更新
	 * @auther chenlf3
	 * @date 2018年5月9日 下午12:15:14
	 */
	/*void checkAppUpdate() {
		//检查app更新
		if(updateUtil.checkAppUpdate()) {
			//更新操作
		} else {
			init();
		}
	}*/
	
	/**
	 * 初始化界面
	 * @auther chenlf3
	 * @date 2018年5月9日 下午12:15:33
	 */
	void init() {
		//检查版本更新
		try {
			updateUtil.UpdateCheck();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File(Constant.PROJECT_PATH);
		if(!file.exists()) {
			file.mkdirs();
			try {
	            ClfUtil.copyDJFileFromAssets(context, "dianju", Constant.PROJECT_PATH);
	            ClfUtil.copyDJFileFromAssets(context, "fonts", Constant.FONTS_PATH);
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            Log.d("dianju", "获取assets资源错误！");
	            return;
	        }
		}
		File tempDir = new File(Constant.TEMP_DIR);
		if(tempDir.exists() && tempDir.isDirectory()) {
			ClfUtil.delDir(tempDir);
		}
		tempDir.mkdirs();
		mainBackLayout.setVisibility(View.GONE);
		//设置默认加载菜单索引设置
		currIndex = 1;
		//fragment加载
		manager = getFragmentManager();
		fileFragment = new FileFragment();
		softFragment = new SoftFragment();
		meFragment = new MeFragment();
		FragmentTransaction ft = manager.beginTransaction();
		ft.replace(R.id.fragmentLayout, fileFragment);
		ft.commit();
		//初始化选中菜单,默认选中第一项
		this.filePic = (ImageView) this.findViewById(R.id.file_pic);
		this.fileTxt = (TextView) this.findViewById(R.id.file_txt);
		this.softPic = (ImageView) this.findViewById(R.id.soft_pic);
		this.softTxt = (TextView) this.findViewById(R.id.soft_txt);
		this.mePic = (ImageView) this.findViewById(R.id.me_pic);
		this.meTxt = (TextView) this.findViewById(R.id.me_txt);
		this.filePic.setImageResource(R.drawable.file_c);
		this.fileTxt.setTextColor(Color.BLUE);
		//初始化菜单按钮+点击事件
		this.btnFile = (LinearLayout) this.findViewById(R.id.btn_file);
		this.btnSoft = (LinearLayout) this.findViewById(R.id.btn_soft);
		this.btnMe = (LinearLayout) this.findViewById(R.id.btn_me);
		this.btnFile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currIndex != 1) {
					currIndex = 1;
					filePic.setImageResource(R.drawable.file_c);
					fileTxt.setTextColor(Color.BLUE);
					softPic.setImageResource(R.drawable.soft);
					softTxt.setTextColor(Color.BLACK);
					mePic.setImageResource(R.drawable.me);
					meTxt.setTextColor(Color.BLACK);
					
					FragmentTransaction ft = manager.beginTransaction();
					ft.replace(R.id.fragmentLayout, fileFragment);
					ft.commit();
				}
			}
		});
		this.btnSoft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currIndex != 2) {
					currIndex = 2;
					filePic.setImageResource(R.drawable.file);
					fileTxt.setTextColor(Color.BLACK);
					softPic.setImageResource(R.drawable.soft_c);
					softTxt.setTextColor(Color.BLUE);
					mePic.setImageResource(R.drawable.me);
					meTxt.setTextColor(Color.BLACK);
					
					FragmentTransaction ft = manager.beginTransaction();
					ft.replace(R.id.fragmentLayout, softFragment);
					ft.commit();
				}
			}
		});
		this.btnMe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currIndex != 3) {
					currIndex = 3;
					filePic.setImageResource(R.drawable.file);
					fileTxt.setTextColor(Color.BLACK);
					softPic.setImageResource(R.drawable.soft);
					softTxt.setTextColor(Color.BLACK);
					mePic.setImageResource(R.drawable.me_c);
					meTxt.setTextColor(Color.BLUE);
					
					FragmentTransaction ft = manager.beginTransaction();
					ft.replace(R.id.fragmentLayout, meFragment);
					ft.commit();
				}
			}
		});
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		if(requestCode==Constant.RequestCode.PERMISSIONS_RESULT) {
			if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
				//获取下一个权限
				String nextPermission = permissionUtil.getNoOwnedPermission();
				if(TextUtils.isEmpty(nextPermission)) {
					//权限申请完毕，进行初始化工作
					init();
				} else {
					permissionUtil.requestPermissions(nextPermission, Constant.RequestCode.PERMISSIONS_RESULT);
				}
			} else {
				permissionUtil.requestPermissions(permissions[0], Constant.RequestCode.PERMISSIONS_RESULT);
			}
		}
	}
}
