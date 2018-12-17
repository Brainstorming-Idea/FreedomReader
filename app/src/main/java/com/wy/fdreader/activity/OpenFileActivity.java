package com.wy.fdreader.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dianju.showpdf.DJContentView;
import com.dianju.showpdf.DJContentView.DJCode;
import com.dianju.showpdf.DJContentView.NodeType;
import com.dianju.showpdf.DJContentView.OperType;
import com.dianju.showpdf.DJSOInterface;
import com.dianju.showpdf.PageMode;
import com.dianju.trustedsign.utils.ClfUtil;
import com.wy.fdreader.R;
import com.wy.fdreader.dao.OpenedFilesDao;
import com.wy.fdreader.dao.impl.OpenedFilesDaoImpl;
import com.wy.fdreader.entity.OpenedFiles;
import com.wy.fdreader.utils.CommonUtil;
import com.wy.fdreader.utils.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OpenFileActivity extends Activity {

	private Context context;
	private LinearLayout readP,noteP,handP,sealP;
	//private LinearLayout btnRead,btnNote,btnHand,btnSeal;
	//阅读按钮，批注按钮，手写按钮，盖章按钮
	private LinearLayout readExArea,noteExArea,handExArea,sealExArea;
	private LinearLayout btnReadEx1,btnReadEx2,btnReadEx3;
	private LinearLayout btnNoteEx1,btnNoteEx2;
	private LinearLayout btnHandEx1,btnHandEx2,btnHandEx3;
	private LinearLayout btnSealEx1,btnSealEx2;
	private View selectBtn;
	private String currHandleType;
	private int selectMenu;
	private LinearLayout contentLayout;
	private DJContentView contentView;
	private boolean isListener;
	private String filePath;

	private OpenedFiles openedFile = null;
	private OpenedFilesDao openedFilesDao = null;
	
	private ImageView btnReturn,btnSaveAs,btnShare;
	
	/** 编辑框界面 */
	private View editLayout;
	private Button btnEditCancel,btnEditOK;
	private EditText editArea;
	
	/** 其他 */
	private TextView handSetT,readType,pageE;
	private Button btnPageTo;
	
	public Handler myhandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DJCode.OPEN_FILE://打开文件
				if (msg.arg1 == 1) {//加载成功
					//打开文件后，记录传递过来的文件信息，当做最近打开的文件
			        recordOpenedFiles(filePath);
				}
				break;
			case DJCode.SEAL://盖章结束
				cleanSelectExBtn();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_openfile);
		this.context = this;
		Intent intent = getIntent(); 
		Bundle extras = intent.getExtras();
        String action = intent.getAction();
        if(intent.ACTION_VIEW.equals(action)){
           Uri uri = (Uri)intent.getData();  
           filePath = getUriAbsolutePath(context,uri);
        } else {
        	filePath = extras.getString("filePath");
//    		index=extras.getInt("index");
//    		type = extras.getString("type");
        }
		isListener = true;
		init();
		initEditView();
	}
	
	private void recordOpenedFiles(String filePath) {
		// 将数据存储到数据库
		openedFilesDao = new OpenedFilesDaoImpl(context);
		openedFile = new OpenedFiles();
		String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
		File opendFile = new File(filePath);
		String filesize = opendFile.length()/1000 + "Kb";
		if (opendFile.length()/1000 >= 1000) {
			filesize = opendFile.length()/1000/1000 + "Mb";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//大写代表24小时制
		Date date = new Date(System.currentTimeMillis());
		String currTime = dateFormat.format(date);
		//API24以上使用此方法
//		Calendar ca = Calendar.getInstance();
//		currTime = ca.get(Calendar.HOUR_OF_DAY) + "";
		String opentime = currTime;
		openedFile.setFile_name(filename);
		openedFile.setFile_path(filePath);
		openedFile.setOpen_time(opentime);
		openedFile.setFile_size(filesize);
		//获取文件缩略图
		byte[] filethum = getFileThum(filePath);
		openedFile.setFile_thum(filethum);
		//检测数据库中是否有相同的文件，有就覆盖
		boolean isRepeat = checkRepeatFile(filePath);
		if (!isRepeat) {
			openedFilesDao.addOpenedFile(openedFile);
		}
	}

	private byte[] getFileThum(String filePath) {
		// TODO Auto-generated method stub
		byte[] thum = null;
		DJSOInterface util = new DJSOInterface();
		if(filePath.endsWith(".aip")) {
			util.openTempFile(filePath);
		} else if(filePath.endsWith(".pdf")) {
			util.openFile(filePath);
		} else if(filePath.endsWith(".ofd")) {
			util.openTempFile(filePath);
		}
		Bitmap bitmap = util.getBitmap(0, 150, 150, 0);
		util.saveFile("");
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 10, bout);
		thum = bout.toByteArray();
		return thum;
	}

	private boolean checkRepeatFile(String filePath) {
		// 
		List<OpenedFiles> filesInfos = new ArrayList<OpenedFiles>();
		filesInfos = openedFilesDao.readOpenedFile();
		for (int i = 0; i < filesInfos.size(); i++) {
			String fpath = filesInfos.get(i).getFile_path();
			String id = filesInfos.get(i).getId();
			openedFile.setId(id);
			if (filePath.equals(fpath)) {
				//修改重复文件的文件信息
				openedFilesDao.updateOpenedFile(openedFile);
				return true;
			}
		}
		return false;
	}

	private String getUriAbsolutePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String absoluPath = null;
        if (scheme == null)
            absoluPath = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            absoluPath = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        absoluPath = cursor.getString(index);
                    } else {
                    	String tempPath = uri.getPath();
                        if (tempPath.startsWith("/root")) {
                            tempPath = tempPath.substring(5, tempPath.length());
                        }
                        absoluPath = tempPath;
                    }
                }
                cursor.close();
            }
        }
        return absoluPath;
    }

	void init() {
		this.readP = (LinearLayout) this.findViewById(R.id.readP);
		this.noteP = (LinearLayout) this.findViewById(R.id.noteP);
		this.handP = (LinearLayout) this.findViewById(R.id.handP);
		this.sealP = (LinearLayout) this.findViewById(R.id.sealP);
		//主按钮
		//this.btnRead = (LinearLayout) this.findViewById(R.id.btn_read);
		//this.btnNote = (LinearLayout) this.findViewById(R.id.btn_note);
		//this.btnHand = (LinearLayout) this.findViewById(R.id.btn_hand);
		//this.btnSeal = (LinearLayout) this.findViewById(R.id.btn_seal);
		
		this.readExArea = (LinearLayout) this.findViewById(R.id.readExArea);
		this.noteExArea = (LinearLayout) this.findViewById(R.id.noteExArea);
		this.handExArea = (LinearLayout) this.findViewById(R.id.handExArea);
		this.sealExArea = (LinearLayout) this.findViewById(R.id.sealExArea);
		
		//每个主按钮的扩展按钮
		this.btnReadEx1 = (LinearLayout) this.findViewById(R.id.btnReadEx1);
		this.btnReadEx2 = (LinearLayout) this.findViewById(R.id.btnReadEx2);
		this.btnReadEx3 = (LinearLayout) this.findViewById(R.id.btnReadEx3);
		this.btnNoteEx1 = (LinearLayout) this.findViewById(R.id.btnNoteEx1);
		this.btnNoteEx2 = (LinearLayout) this.findViewById(R.id.btnNoteEx2);
		this.btnHandEx1 = (LinearLayout) this.findViewById(R.id.btnHandEx1);
		this.btnHandEx2 = (LinearLayout) this.findViewById(R.id.btnHandEx2);
		this.btnHandEx3 = (LinearLayout) this.findViewById(R.id.btnHandEx3);
		this.btnSealEx1 = (LinearLayout) this.findViewById(R.id.btnSealEx1);
		this.btnSealEx2 = (LinearLayout) this.findViewById(R.id.btnSealEx2);
		
		//顶部按钮
		this.btnReturn = (ImageView) this.findViewById(R.id.btnReturn);
		this.btnSaveAs = (ImageView) this.findViewById(R.id.btnSaveAs);
		this.btnShare = (ImageView) this.findViewById(R.id.btnShare);
		
		//其他
		this.handSetT = (TextView) this.findViewById(R.id.handSetT);
		this.readType = (TextView) this.findViewById(R.id.readType);
		this.pageE = (TextView) this.findViewById(R.id.pageE);
		this.btnPageTo = (Button) this.findViewById(R.id.btnPageTo);
		
		this.contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);
		initBtnClick();
		//初始化文档
		if(TextUtils.isEmpty(filePath)) {
			Toast.makeText(context, "打开文件失败，空的文件路径!", Toast.LENGTH_LONG).show();
		} else if((!(filePath.toLowerCase()).endsWith(".pdf")) && (!(filePath.toLowerCase()).endsWith(".ofd")) && (!(filePath.toLowerCase()).endsWith(".aip"))) {
			Toast.makeText(context, "打开文件失败，错误的文件格式!", Toast.LENGTH_LONG).show();
		} else {
			this.contentLayout.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
				
				@Override
				public boolean onPreDraw() {
					// TODO Auto-generated method stub
					if(isListener) {
						isListener = false;
						int openRes = loadFile(filePath);
						if(openRes<=0) {
							Toast.makeText(context, "打开文件失败，空的文件路径!openRes="+openRes, Toast.LENGTH_LONG).show();
							return true;
						}
						contentView.login("HWSEALDEMO", 4, "");
					}
					return true;
				}
			});
		}
		selectMenu = Constant.TopMenu.READ;
		//初始化翻页模式
		int readTypeV = ClfUtil.getSPInt(context, Constant.READ_TYPE, Constant.READ_TYPE_DEF);
		if(readTypeV==1) {
			readType.setText("左右页");
		} else {
			readType.setText("上下页");
		}
	}
	
	void initEditView() {
		this.editLayout = this.findViewById(R.id.editLayout);
		this.btnEditCancel = (Button) this.findViewById(R.id.btn_cancel);
		this.btnEditOK = (Button) this.findViewById(R.id.btn_ok);
		this.editArea = (EditText) this.findViewById(R.id.editArea);
		this.btnEditCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editLayout.setVisibility(View.GONE);
				if(contentView != null) {
					contentView.unLockScreen();
				}
			}
		});
		
		this.btnEditOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO Auto-generated method stub
				if(contentView.getCurrAction()==OperType.AUTO_HANDLE && Constant.AutoHandleType.TEXT_ANNOTATION.equals(currHandleType)) {//插入节点状态
					String con = editArea.getText().toString();
					if(!TextUtils.isEmpty(con)) {
						String nodeName = getRandName(NodeType.Head.EDIT_TEXT_EX);
						int x = contentView.getClickNodeX();
						int y = contentView.getClickNodeY();
						int w = contentView.getClickNodeWidth();
						int h = contentView.getClickNodeHeight();
						Point lt = new Point(x,y);//左上角点
						if(w<0) {
							lt.x = lt.x + w;
							w = -w;
						}
						if(h<0) {
							lt.y = lt.y + h;
							h = -h;
						}
						if(w<10000) {
							w = 10000;
						}
						if(h<2000) {
							h = 2000;
						}
						contentView.insertNoteByWH(nodeName, NodeType.EDIT_TEXT_EX, contentView.getClickPage(), lt.x, lt.y, w, h);
						contentView.setValue(nodeName, con);
					}
				} else if(contentView.getCurrAction()==OperType.EDITTEXT) {
					String con = editArea.getText().toString();
					contentView.setValueEx(contentView.currNodeName, 2, 0, con);
				}
				editLayout.setVisibility(View.GONE);
				contentView.unLockScreen();
				contentView.freshPDF();
			}
		});
	}
	
	void initBtnClick() {
		this.readP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(selectMenu == Constant.TopMenu.READ) return;
				selectMenu = Constant.TopMenu.READ;
				readP.setBackgroundColor(getResources().getColor(R.color.fileMenuCheckedColor));
				noteP.setBackgroundResource(R.drawable.file_topback);
				handP.setBackgroundResource(R.drawable.file_topback);
				sealP.setBackgroundResource(R.drawable.file_topback);
				readExArea.setVisibility(View.VISIBLE);
				noteExArea.setVisibility(View.INVISIBLE);
				handExArea.setVisibility(View.INVISIBLE);
				sealExArea.setVisibility(View.INVISIBLE);
				//翻页方式
				int readTypeV = ClfUtil.getSPInt(context, Constant.READ_TYPE, Constant.READ_TYPE_DEF);
				if(readTypeV==1) {
					readType.setText("左右页");
				} else {
					readType.setText("上下页");
				}
				if(contentView != null) {
					contentView.unLockScreen();
					contentView.setCurrAction(OperType.NONE);
					if(!TextUtils.isEmpty(contentView.xukuangnode)) {
						contentView.uncheck();
						contentView.freshPDF();
					}
					editLayout.setVisibility(View.GONE);
				}
			}
		});
		this.noteP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(selectMenu == Constant.TopMenu.TEXT) return;
				selectMenu = Constant.TopMenu.TEXT;
				readP.setBackgroundResource(R.drawable.file_topback);
				noteP.setBackgroundColor(getResources().getColor(R.color.fileMenuCheckedColor));
				handP.setBackgroundResource(R.drawable.file_topback);
				sealP.setBackgroundResource(R.drawable.file_topback);
				readExArea.setVisibility(View.INVISIBLE);
				noteExArea.setVisibility(View.VISIBLE);
				handExArea.setVisibility(View.INVISIBLE);
				sealExArea.setVisibility(View.INVISIBLE);
				if(contentView != null) {
					selectExBtn(btnNoteEx1);
					currHandleType = Constant.AutoHandleType.TEXT_ANNOTATION;
					contentView.setCurrAction(OperType.AUTO_HANDLE);
					contentView.unLockScreen();
					if(!TextUtils.isEmpty(contentView.xukuangnode)) {
						contentView.uncheck();
						contentView.freshPDF();
					}
				}
			}
		});
		this.handP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(selectMenu == Constant.TopMenu.HADN) return;
				selectMenu = Constant.TopMenu.HADN;
				readP.setBackgroundResource(R.drawable.file_topback);
				noteP.setBackgroundResource(R.drawable.file_topback);
				handP.setBackgroundColor(getResources().getColor(R.color.fileMenuCheckedColor));
				sealP.setBackgroundResource(R.drawable.file_topback);
				readExArea.setVisibility(View.INVISIBLE);
				noteExArea.setVisibility(View.INVISIBLE);
				handExArea.setVisibility(View.VISIBLE);
				sealExArea.setVisibility(View.INVISIBLE);
				contentView.setCurrAction(OperType.WRITE);
				//加载手触设置内容
				int setHand = ClfUtil.getSPInt(context, Constant.SET_HAND, Constant.SET_HAND_DEF);
				if(setHand==1) {
					handSetT.setText("禁用手写");
				} else {
					handSetT.setText("启用手写");
				}
				contentView.unLockScreen();
				editLayout.setVisibility(View.GONE);
				if(!TextUtils.isEmpty(contentView.xukuangnode)) {
					contentView.uncheck();
					contentView.freshPDF();
				}
			}
		});
		this.sealP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(selectMenu == Constant.TopMenu.SEAL) return;
				selectMenu = Constant.TopMenu.SEAL;
				readP.setBackgroundResource(R.drawable.file_topback);
				noteP.setBackgroundResource(R.drawable.file_topback);
				handP.setBackgroundResource(R.drawable.file_topback);
				sealP.setBackgroundColor(getResources().getColor(R.color.fileMenuCheckedColor));
				readExArea.setVisibility(View.INVISIBLE);
				noteExArea.setVisibility(View.INVISIBLE);
				handExArea.setVisibility(View.INVISIBLE);
				sealExArea.setVisibility(View.VISIBLE);
				if(contentView != null) {
					contentView.setCurrAction(OperType.NONE);
					contentView.unLockScreen();
					if(!TextUtils.isEmpty(contentView.xukuangnode)) {
						contentView.uncheck();
						contentView.freshPDF();
					}
				}
				editLayout.setVisibility(View.GONE);
			}
		});
		this.btnReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		this.btnSaveAs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(contentView != null) {
					int res = contentView.saveFileEx(filePath, 0);
					String msg = "";
					if(res==1) {
						msg = "保存成功!";
					} else {
						msg = "保存失败,res="+res;
					}
					AlertDialog dialog = ClfUtil.popDialog(context, "提醒", msg, "确定", "", null, null);
				}
			}
		});
		//分享(邮件、微信、qq、微博)
		this.btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//showShareDialog();
				//获取fileName
				if(TextUtils.isEmpty(filePath)) {
					CommonUtil.popTip(context, "error-filePath空值!");
					return;
				}
				String[] cons = filePath.split(File.separator);
				String fileName = cons[cons.length-1];
				String shareFilePath = Constant.TEMP_DIR+File.separator+fileName;
				int res = contentView.saveFileEx(shareFilePath, 0);
				if(res!=1) {
					CommonUtil.popTip(context, "error-saveFile获取文件失败");
					return;
				}
				File file = new File(shareFilePath);
				shareFile(context, file);
			}
		});
		
		/** 初始化宽展按钮组 */
		
		//阅读方式切换
		this.btnReadEx1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int readTypeV = ClfUtil.getSPInt(context, Constant.READ_TYPE, Constant.READ_TYPE_DEF);
				if(readTypeV==1) {
					ClfUtil.addSP(context, Constant.READ_TYPE, 0);
					readType.setText("上下页");
					contentView.setPageMode(PageMode.SinglePage);
				} else {
					contentView.setPageMode(PageMode.MultiPage);
					readType.setText("左右页");
					ClfUtil.addSP(context, Constant.READ_TYPE, 1);
				}
				contentView.freshPDF();
			}
		});
		
		//撤销
		this.btnReadEx2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contentView.undoAll(false);
			}
		});
		
		//删除
		this.btnReadEx3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(contentView.getCurrAction()==OperType.DELETE) {
					cleanSelectExBtn();
					contentView.setCurrAction(OperType.NONE);
					if(!TextUtils.isEmpty(contentView.xukuangnode)) {
						contentView.uncheck();
						contentView.freshPDF();
					}
				} else {
					selectExBtn(btnReadEx3);
					contentView.setCurrAction(OperType.DELETE);
				}
			}
		});
		
		//新建批注
		this.btnNoteEx1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(contentView != null) {
					selectExBtn(btnNoteEx1);
					currHandleType = Constant.AutoHandleType.TEXT_ANNOTATION;
					contentView.setCurrAction(OperType.AUTO_HANDLE);
				}
			}
		});
		
		//编辑批注
		this.btnNoteEx2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(contentView != null) {
					selectExBtn(btnNoteEx2);
					contentView.setCurrAction(OperType.EDITTEXT);
				}
			}
		});
		
		//手写属性设置
		this.btnHandEx1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//获取默认属性值
				int currColor = ClfUtil.getSPInt(context, Constant.SET_HANDCOLOR, Constant.SET_HANDCOLOR_DEF);
				int currSize = ClfUtil.getSPInt(context, Constant.SET_HANDSIZE, Constant.SET_HANDSIZE_DEF);
				Intent intent = new Intent(context, AttrSettingActivity.class);
				intent.putExtra("currColor", currColor);
				intent.putExtra("currSize", currSize);
				intent.putExtra("maxSize", Constant.MAXHANDSIZE);
				startActivityForResult(intent, Constant.RequestCode.SET_HAND_ATTR);
			}
		});
		//手触设置
		this.btnHandEx2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int setHand = ClfUtil.getSPInt(context, Constant.SET_HAND, Constant.SET_HANDCOLOR_DEF);
				if(setHand==1) {
					ClfUtil.addSP(context, Constant.SET_HAND, 0);
					contentView.setUseFingerWrite(false);
					handSetT.setText("启用手写");
				} else {
					ClfUtil.addSP(context, Constant.SET_HAND, 1);
					contentView.setUseFingerWrite(true);
					handSetT.setText("禁用手写");
				}
			}
		});
		//擦除设置
		this.btnHandEx3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(contentView.getCurrAction()==OperType.WRITE) {
					selectExBtn(btnHandEx3);
					contentView.setCurrAction(OperType.ERASER);
				} else {
					cleanSelectExBtn();
					contentView.setCurrAction(OperType.WRITE);
				}
			}
		});
		
		//选择印章文件进行盖章
		this.btnSealEx1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectExBtn(btnSealEx1);
				String dir = ClfUtil.getSPString(context, Constant.SELECT_SEL_PATH, Constant.SELECT_SEL_PATH_DEF);
				Intent intent = new Intent(context, MyFileManager.class);
				intent.putExtra("type", "0");
				intent.putExtra("dir", dir);
				startActivityForResult(intent, Constant.RequestCode.SELECT_SEL);
			}
		});
		
		//验章
		this.btnSealEx2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					List<Map<String,Object>> list = contentView.getSealListInfos();
					if(list == null || list.size()<=0) {
						CommonUtil.popTip(context, "无印章");
						return;
					}
					for(Map<String,Object> map:list) {
						String verifyRes = (String) map.get("verifyRes");
						if(!"1".equals(verifyRes)) {
							CommonUtil.popTip(context, "印章已失效");
							return;
						}
					}
					CommonUtil.popTip(context, "印章有效");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					CommonUtil.popTip(context, "验证异常:"+e.getMessage());
				}
			}
		});
		
		//其他按钮
		//页面跳转
		this.btnPageTo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//获取页码
				String con = pageE.getText().toString();
				if(TextUtils.isEmpty(con)) {
					return;
				}
				int page = Integer.parseInt(con)-1;
				if(page<0) {
					page = 0;
					pageE.setText(""+(page+1));
				}
				if(page>contentView.getPageCount()-1) {
					page = contentView.getPageCount()-1;
					pageE.setText(""+(page+1));
				}
				contentView.gotoPage(page);
				contentView.freshClearPDF();
			}
		});
	}
	
	private int loadFile(String filePath) {
		if(contentView == null) {
			contentView = new DJContentView(context);
			contentLayout.addView(contentView);
		} else {
			contentView.saveFile("");
			contentView.disposeResource();
		}
		contentView.setZOrderMediaOverlay(true);
		contentView.setMyhandler(myhandler);
		/** 配置信息设置 */
		contentView.setValue("ADD_FORCETYPE_VALUE6",""+((2+4)*16));
		contentView.setValue("ADD_FORCETYPE_VALUE", "8192");
		/** 配置信息设置 */
		int openRes = 0;
		if(TextUtils.isEmpty(filePath)) {
			return -997;//空路径
		} else if((filePath.toLowerCase()).endsWith(".pdf")) {
			openRes = contentView.openFile(filePath);
		} else if((filePath.toLowerCase()).endsWith(".ofd")) {
			openRes = contentView.openTempFile(filePath);
		} else if((filePath.toLowerCase()).endsWith(".aip")) {
			openRes = contentView.openTempFile(filePath);
		} else {
			return -996;//文件类型错误
		}
		contentView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				contentView.onTouchEvent(event);//保持contentview原有的放大缩小功能还在
				//外接编辑控件(文字录入、日历)
				externalEvent(event);
				return true;
			}
		});
		//手触设置
		int setHand = ClfUtil.getSPInt(context, Constant.SET_HAND, Constant.SET_HAND_DEF);
		if(setHand!=1) {
			contentView.setUseFingerWrite(false);
		}
		//翻页模式设置
		int readTypeV = ClfUtil.getSPInt(context, Constant.READ_TYPE, Constant.READ_TYPE_DEF);
		if(readTypeV!=1) {
			contentView.setPageMode(PageMode.SinglePage);
		}
		int currColor = ClfUtil.getSPInt(context, Constant.SET_HANDCOLOR, Constant.SET_HANDCOLOR_DEF);
		int currSize = ClfUtil.getSPInt(context, Constant.SET_HANDSIZE, Constant.SET_HANDSIZE_DEF);
		contentView.setPenAttr(currSize, currColor);
		return openRes;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==Constant.RequestCode.SELECT_SEL) {
			if(resultCode==1) {
				String savePath = data.getStringExtra("savePath");
				String fileName = data.getStringExtra("fileName");
				ClfUtil.addSP(context, Constant.SELECT_SEL_PATH, savePath);
				if(CommonUtil.isSel(fileName)) {
					String filePath1 = savePath+File.separator+fileName;
					if((fileName.toLowerCase()).endsWith(".sel")) {
						contentView.addSeal(filePath1, Constant.PFX_PATH, Constant.PFX_PWD, 2);
					} else {
						contentView.addHandSeal(filePath1, Constant.PFX_PATH, Constant.PFX_PWD, 2, 50);
					}
					CommonUtil.popTip(context, "点击文档任意位置盖章！");
				} else {
					cleanSelectExBtn();
					Toast.makeText(context, "文件类型错误！", Toast.LENGTH_SHORT).show();
				}
			} else {
				cleanSelectExBtn();
			}
		} else if(requestCode==Constant.RequestCode.SET_HAND_ATTR) {
			if(resultCode==1) {
				int color = data.getIntExtra("selectColor", -1);
				int size = data.getIntExtra("selectSize", -1);
				ClfUtil.addSP(context, Constant.SET_HANDCOLOR, color);
				ClfUtil.addSP(context, Constant.SET_HANDSIZE, size);
				contentView.setPenAttr(size, color);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(contentView != null) {
			contentView.saveFile("");
			contentView.disposeResource();
			contentView = null;
		}
		super.onDestroy();
	}
	
	void cleanSelectExBtn() {
		if(selectBtn != null) {
			selectBtn.setBackgroundColor(getResources().getColor(R.color.fileMenuCheckedColor));
			selectBtn = null;
		}
	}
	void selectExBtn(View view) {
		cleanSelectExBtn();
		selectBtn = view;
		selectBtn.setBackgroundColor(getResources().getColor(R.color.fileExMenuCheckedColor));
	}
	
	void externalEvent(MotionEvent event) {
		if((!contentView.isLockScreen()) && ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP)) {
			if(contentView.getCurrAction()==OperType.AUTO_HANDLE) {
				contentView.lockScreen();
				if(currHandleType == Constant.AutoHandleType.TEXT_ANNOTATION) {//文字批注：准备弹出文字录入界面
					editArea.setText("");
					editLayout.setVisibility(View.VISIBLE);
				}
			} else if(contentView.getCurrAction()==OperType.EDITTEXT && (!TextUtils.isEmpty(contentView.currNodeName))) {
				if(contentView.getCurrNodeType()==10) {//多中文本框/日历
					if(contentView.isDateNode(contentView.currNodeName)) {//日历
						
					} else if(contentView.isEditBoxNode(contentView.currNodeName)) {//文字编辑框
						contentView.lockScreen();
						String con = contentView.getValueEx(contentView.currNodeName, 2, "", 0, "");
						editArea.setText(con);
						editLayout.setVisibility(View.VISIBLE);
					} else {}
				} else if(contentView.getCurrNodeType()==5) {//复选框/单选框/下拉框(该类型被jar处理，此处不会受到该类型节点)
					
				} else if(contentView.getCurrNodeType()==2) {//附件类型的注释
					/*String content = contentView.getValueEx(contentView.currNodeName, 2, userName, 0, "");
					editArea.setText(content);
					editLayout.setVisibility(View.VISIBLE);
					imm.showSoftInput(editArea, InputMethodManager.HIDE_NOT_ALWAYS);
					//弹编辑框需锁屏
					contentView.lockScreen();*/
				}
			}
		}
	}
	
	//获取一个随机字符串
	String getRandName(String head) {
		Date d = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String nodename = head + format.format(d);
		return nodename;
	}
	
	//分享界面
	/*public void showShareDialog() {
		View view = View.inflate(context, R.layout.dialog_share, null);
		Button btnShareMail = (Button) view.findViewById(R.id.btnShareMail);
		Button btnShareQQ = (Button) view.findViewById(R.id.btnShareQQ);
		Button btnShareWeChat = (Button) view.findViewById(R.id.btnShareWeChat);
		//邮件分享
		btnShareMail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File(Environment.getExternalStorageDirectory()+"/dianju/bxqz.pdf");
				shareFileToMail(context, file);
			}
		});
		btnShareQQ.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File(Environment.getExternalStorageDirectory()+"/dianju/bxqz.pdf");
				shareFileToQQ(context, file);
			}
		});
		btnShareWeChat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File(Environment.getExternalStorageDirectory()+"/dianju/bxqz.pdf");
				shareFileToWeChat(context, file);
			}
		});
		ClfUtil.popDialog(context, null, "确定", "取消", null, null, view);
		//dialog.getWindow().setGravity(Gravity.TOP|Gravity.RIGHT);
	}*/
	
	public static void shareFile(Context context, File file) {
    	Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));  
        context.startActivity(Intent.createChooser(intent, "分享"));
    }
}
