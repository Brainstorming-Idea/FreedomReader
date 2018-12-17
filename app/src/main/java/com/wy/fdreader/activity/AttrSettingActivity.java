package com.wy.fdreader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dianju.trustedsign.views.ColorPickerView;
import com.dianju.trustedsign.views.ColorPickerView.OnColorChangedListener;
import com.wy.fdreader.R;

public class AttrSettingActivity extends Activity {
	private Context context;
	private ColorPickerView colorPanel;
	private TextView preColor,preSize;
	private SeekBar sizeBar;
	private int selectColor;
	private int selectSize;
	private Button btnOK,btnCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attrset);
		this.context = this;
		int currColor = getIntent().getIntExtra("currColor", -1);
		int currSize = getIntent().getIntExtra("currSize", -1);
		int maxSize = getIntent().getIntExtra("maxSize", -1);
		this.btnOK = (Button) this.findViewById(R.id.btnOK);
		this.btnCancel = (Button) this.findViewById(R.id.btnCancel);
		colorPanel = (ColorPickerView)this.findViewById(R.id.color_panel);
		preColor = (TextView)this.findViewById(R.id.pre_color);
		sizeBar = (SeekBar)this.findViewById(R.id.penWidthBar);
		//初始化颜色
		preColor.setBackgroundColor(currColor);
		selectColor = currColor;
		selectSize = currSize;
		/** 为颜色板添加监听事件 */
		colorPanel.setOnColorChangedListenner(new OnColorChangedListener() {
			
			//颜色移动的时候
			@Override
			public void onMoveColor(int r, int g, int b) {
				preColor.setBackgroundColor(Color.rgb(r, g, b));
				//penColor = ""+Color.rgb(r, g, b);
			}
			
			//手指抬起，选定颜色时
			@Override
			public void onColorChanged(int r, int g, int b) {
				preColor.setBackgroundColor(Color.rgb(r, g, b));
				selectColor = Color.rgb(r, g, b);
			}
		});
		
		/** 初始化笔宽 */
		sizeBar = (SeekBar) this.findViewById(R.id.penWidthBar);
		preSize = (TextView) this.findViewById(R.id.pre_penWidth);
		sizeBar.setMax(maxSize);
		sizeBar.setProgress(currSize);
		preSize.setText(""+currSize);
		sizeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				preSize.setText(""+progress);
			}
		});
		
		this.btnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectSize = sizeBar.getProgress();
				Intent data = new Intent();
				data.putExtra("selectColor", selectColor);
				data.putExtra("selectSize", selectSize);
				setResult(1, data);
				finish();
			}
		});
		
		this.btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
}
