package com.wy.fdreader.memoudle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wy.fdreader.utils.Constant;
import com.wy.fdreader.R;
import com.wy.fdreader.utils.updateUtil.AppUpdateUtil;

public class SoftwareInfoActivity extends Activity {

	private ImageView back,new_msg;
	private RelativeLayout versionUpdate;
	private LinearLayout updateContent;
	private TextView new_version,update_log,versionName;
	
	private AppUpdateUtil updateUtil = null;
	private Context context;
	
	Handler newVerHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.Flag.UPDATE_CLIENT:
				//显示小红点，并文字提示有新版本
				new_msg.setVisibility(View.VISIBLE);
				new_version.setVisibility(View.VISIBLE);
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_software_info);
		context = this;
		updateUtil = new AppUpdateUtil(context,false);
		initView();
		init();
	}

	private void init() {
		
		/*进入关于界面自动检测一下是否有版本更新,不弹出更新提示框*/
		try {
			updateUtil.setNewVerHandler(newVerHandler);;
			updateUtil.UpdateCheck();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 返回点击事件
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 退回到我界面
				onBackPressed();
			}
		});
		
		//检测更新
		versionUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 检测是否有新版本
				try {
					updateUtil = new AppUpdateUtil(context,true);
					updateUtil.UpdateCheck();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//查看最新版本更新日志
		updateContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		back = (ImageView) this.findViewById(R.id.back_about);
		new_msg = (ImageView) this.findViewById(R.id.new_msg);
		versionUpdate = (RelativeLayout) this.findViewById(R.id.versionUpdate);
		updateContent = (LinearLayout) this.findViewById(R.id.updateContent);
		new_version = (TextView) this.findViewById(R.id.new_version);
		update_log = (TextView) this.findViewById(R.id.update_log);
		versionName = (TextView) this.findViewById(R.id.versionName);
		String verName = null;
		try {
			verName = updateUtil.getVersionInfo()[1];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			verName = context.getString(R.string.version_name_error);
			e.printStackTrace();
		}
		versionName.setText(verName);
	}
}
