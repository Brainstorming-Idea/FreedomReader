package com.wy.fdreader.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wy.fdreader.R;
import com.wy.fdreader.memoudle.HelpActivity;
import com.wy.fdreader.memoudle.LoginActivity;
import com.wy.fdreader.memoudle.SoftwareInfoActivity;

public class MeFragment extends Fragment {

	private Context context;
	private RelativeLayout me_info;
	private LinearLayout bought,setting,help,about;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_me, null);
		context = getActivity();
		initView(view);
		init();
		return view;
	}
	
	void initView(View view) {
		me_info = (RelativeLayout) view.findViewById(R.id.me_info_layout);
		bought = (LinearLayout) view.findViewById(R.id.my_bought);
		setting = (LinearLayout) view.findViewById(R.id.setting);
		help = (LinearLayout) view.findViewById(R.id.help);
		about = (LinearLayout) view.findViewById(R.id.about);
	}
	
	private void init() {
		// 用户信息项
		me_info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				//跳转到登录注册界面
				Intent loginIntent = new Intent();
				loginIntent.setClass(context, LoginActivity.class);
				context.startActivity(loginIntent);
			}
		});
		
		//用户购买内容
		bought.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//设置
		setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//帮助
		help.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 暂时跳转到点聚主页
				Intent helpIntent = new Intent();
				helpIntent.setClass(context, HelpActivity.class);
				context.startActivity(helpIntent);
			}
		});
		
		//关于
		about.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 软件版本号，更新信息等
				Intent aboutIntent = new Intent();
				aboutIntent.setClass(context, SoftwareInfoActivity.class);
				context.startActivity(aboutIntent);
			}
		});
	}
}
