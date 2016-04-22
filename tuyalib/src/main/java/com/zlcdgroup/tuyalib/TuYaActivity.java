package com.zlcdgroup.tuyalib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.zlcdgroup.util.TuyaDialogCallback;
import com.zlcdgroup.util.TuyaDialogUtil;
import com.zlcdgroup.util.TuyaFileUtils;

/**
 * 涂鸦简单处理（1、将当前获取的图片涂鸦后进行保存，分辨率前后不会发生改变。 2、对于当前图片分辨率比当前手机分辨率稍大的做了适合的缩小处理。
 * 3、只针对比较适合的图片，未对图片进行压缩 4、旋转图片会水清当前涂鸦信息） 参数：picName图片名
 * directoryPath路径（图片文件不存在则直接返回）
 * 返回：当保存成功后返回成功（RESULT_OK），否则相当于未进行涂鸦返回（RESULT_CANCELED） 注意配置：
 * android:configChanges="orientation|screenSize"以免多次调用onCreate
 * 
 * @author king
 * 
 */
public class TuYaActivity extends Activity implements OnClickListener, OnLongClickListener {

	// 图片名
	public static final String KEY_PIC_NAME = "picName";

	public static final String KEY_PIC_ORIGINAL = "Original_name";// 原始图片路径

	// 路径
	public static final String KEY_PIC_DIRECTORYPATH = "directoryPath";

	public static final String KEY_SAVE_RENAME = "saveReName";

	public TuyaView tuyaView;

	DisplayMetrics dm = new DisplayMetrics();

	public LinearLayout tuyaliLayout;

	public Button settingButton;

	public String picName;

	public String directoryPath;

	public String OriginalPath;// 原始图片绝对路径

	// 保存是否重命名
	public String saveReName;

	public static final int quality = 80;

	// 缩放比例（只针对图片稍比当前屏幕大）
	public float scale = 0f;

	public boolean istuya = false;

	public LinearLayout imagefilter_activity_layout2;

	public Button tuyaIconButton;

	
	


	private int postion = 0;
	public List<Shape> mShapes = new ArrayList<Shape>();
	
	public   static    final  String   tuyatemp=Environment.getExternalStorageDirectory().toString() + File.separator + "eims"+File.separator+"temp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 全屏设置，隐藏窗口所有装饰 **/
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		/** 标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题 **/
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_imagefilter);

		picName = getIntent().getStringExtra(KEY_PIC_NAME);
		directoryPath = getIntent().getStringExtra(KEY_PIC_DIRECTORYPATH);
		OriginalPath  = getIntent().getStringExtra(KEY_PIC_ORIGINAL);
		if (TextUtils.isEmpty(picName)) {
			picName = "1.jpg";
		}
		if (TextUtils.isEmpty(directoryPath)) {
			directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test";
		}
		saveReName = getIntent().getStringExtra(KEY_SAVE_RENAME);
		if (TextUtils.isEmpty(saveReName)) {
			saveReName = getReName();
		}
		findView();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		try {
			File file = new File(directoryPath, picName);

			Bitmap src = BitmapFactory.decodeFile(file.getAbsolutePath());
			// Bitmap src =
			// BitmapFactory.decodeStream(getAssets().open("1.jpg"));
			scale = CameraBitmapUtil.getBitmapScale(src, dm);

			if (scale > 0) {

				src = CameraBitmapUtil.BitmapScale(src, scale);
			}
			if (src.getWidth() > src.getHeight()) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

			tuyaView = new TuyaView(this, null, dm);
			tuyaView.setCurrentShapeType(ShapeType.Arrow);
			tuyaliLayout.removeAllViews();

			tuyaliLayout.addView(tuyaView);
			tuyaView.setSrc(src);

		} catch (Error e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

	}

	

	

	// 初始花涂鸦图片
	public void initTuyaBitmap() {

		try {
			File file = new File(directoryPath, picName);

			Bitmap src = BitmapFactory.decodeFile(file.getPath());
			scale = CameraBitmapUtil.getBitmapScale(src, dm);
			if (scale > 0) {
				src = CameraBitmapUtil.BitmapScale(src, scale);
			}
			if (src.getWidth() > src.getHeight()) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

			tuyaView = new TuyaView(this, src, dm);
			tuyaView.setCurrentShapeType(ShapeType.Arrow);
			tuyaliLayout.removeAllViews();
			tuyaliLayout.addView(tuyaView);
           
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void findView() {

		// 清除按钮
		findViewById(R.id.imagefilter_clean).setOnLongClickListener(this);
		findViewById(R.id.imagefilter_clean).setOnClickListener(this);

		// 向上移动
		findViewById(R.id.imagefilter_top).setOnClickListener(this);
		// 向下移动
		findViewById(R.id.imagefilter_bottom).setOnClickListener(this);

		// 向右移动
		findViewById(R.id.imagefilter_right).setOnClickListener(this);
		findViewById(R.id.imagefilter_right).setOnLongClickListener(this);
		// 向左移动
		findViewById(R.id.imagefilter_left).setOnClickListener(this);
		findViewById(R.id.imagefilter_left).setOnLongClickListener(this);
		// 保存
		findViewById(R.id.imagefilter_activity_save).setOnClickListener(this);
		settingButton = (Button) findViewById(R.id.imagefilter_operate);
		settingButton.setOnClickListener(this);
		imagefilter_activity_layout2 = (LinearLayout) findViewById(R.id.imagefilter_activity_layout2);
		tuyaliLayout = (LinearLayout) findViewById(R.id.imagefilter_activity_image);

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (null != tuyaView) {
			tuyaView.destroyBitmap();
		}
	}

	@Override
	public void onClick(View v) {
		if (null == tuyaView || null == tuyaView.src) {
			return;
		}
		if (v.getId() == R.id.imagefilter_clean) {
			postion = 0;
			tuyaView.clearLastOne();
			tuyaView.postInvalidate();
		} else if (v.getId() == R.id.imagefilter_top) {
			if(tuyaView.shapList.size() == 0){
				showToast("没有可移动的对象!");
				return;
			}
            if(null != tuyaView){
            	tuyaView.movetop(5,postion);
            }
		} else if (v.getId() == R.id.imagefilter_bottom) {
			if(tuyaView.shapList.size() == 0){
				showToast("没有可移动的对象!");
				return;
			}
            if(null!= tuyaView){
            	tuyaView.movebottom(5,postion);
            }
		} else if (v.getId() == R.id.imagefilter_left) {
			if(tuyaView.shapList.size() == 0){
				showToast("没有可移动的对象!");
				return;
			}
            if(null != tuyaView){
            	tuyaView.moveLeft(5,postion);
            }
		} else if (v.getId() == R.id.imagefilter_right) {
			if(tuyaView.shapList.size() == 0){
				showToast("没有可移动的对象!");
				return;
			}
            if(null != tuyaView){
            	tuyaView.moveright(5,postion);
            }
		} else if (v.getId() == R.id.imagefilter_activity_save) {
			saveBitmap();

		} else if (v.getId() == R.id.imagefilter_operate) {
			PopupMenu popupMenu = new PopupMenu(this, settingButton);
			popupMenu.getMenu().add(0, 1, 1, "箭头");
			popupMenu.getMenu().add(0, 2, 2, "圆");
			popupMenu.getMenu().add(0, 3, 3, "箭头折线");
			popupMenu.getMenu().add(0, 4, 4, "线段");
			popupMenu.getMenu().add(0, 5, 5, "马赛克");			
			popupMenu.getMenu().add(0, 6, 6, "无");
			popupMenu.getMenu().add(0, 8, 8, "矩形");
			popupMenu.getMenu().add(0,9,9,"颜色");
			popupMenu.getMenu().add(0, 10, 10, "回箭头");
			try {
				
				File  file = new  File(OriginalPath);
				
				if(!OriginalPath.endsWith(picName) && file.exists()){
					
					popupMenu.getMenu().add(0, 7, 7, "原始图片");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		
			popupMenu.show();
			popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {

					switch (item.getItemId()) {
					case 1: {
						tuyaView.setCurrentShapeType(ShapeType.Arrow);
						postion = tuyaView.shapList.size();
						
					}
						break;
					case 2: {
						tuyaView.setCurrentShapeType(ShapeType.Circle);
						postion = tuyaView.shapList.size();
						
					}
						break;
					case 3: {
						tuyaView.setCurrentShapeType(ShapeType.MLine);
						postion = tuyaView.shapList.size();
						
					}
						break;
					case 4: {
						tuyaView.setCurrentShapeType(ShapeType.Line);
						postion = tuyaView.shapList.size();
						
					}
						break;
					case 5: {
						tuyaView.setCurrentShapeType(ShapeType.Mosaic);
						postion = tuyaView.shapList.size();
					}
						break;
					case 6: {
						tuyaView.setCurrentShapeType(ShapeType.NULL);
					}
						break;
					case 7: {
						showConfirmDialog();
						return true;
					}
					case 8:
						 tuyaView.setCurrentShapeType(ShapeType.Rectangle);
						break;
						
					case 9:
						TuyaDialogUtil.SelectColor(TuYaActivity.this, new TuyaDialogCallback() {

							@Override
							public void success(Object obj) {

								if (null != tuyaView) {
									try {
										Integer color = Integer.parseInt(obj.toString());
										System.out.println("color=" + color);
										if (color == 0) {
											tuyaView.setTuYaColor(TuYaColor.NULL);
										} else if (color == 1) {
											tuyaView.setTuYaColor(TuYaColor.Red);
										} else if (color == 2) {
											tuyaView.setTuYaColor(TuYaColor.Black);
										} else if (color == 3) {
											tuyaView.setTuYaColor(TuYaColor.Green);
										}
									} catch (Exception e) {
										e.printStackTrace();
										// TODO: handle exception
									}
								}

							}
						});
						break;
					case 10:
						tuyaView.setCurrentShapeType(ShapeType.TurnPainAround);
						postion = tuyaView.shapList.size();
						break;
					}
					
				
					tuyaView.postInvalidate();

					return true;
				}
			});

		}

	}

	
	public  void   showConfirmDialog(){
		new AlertDialog.Builder(this).setTitle("提示").
		setMessage("确定要使用原始图片？使用后已添加的涂鸦将会消失！")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					File   file  = new  File(tuyatemp);
				    if(!file.isDirectory()){
				    	file.mkdirs();
				    }
				    String   name = UUID.randomUUID().toString()+".jpg";
				    TuyaFileUtils.CopySingleFileTo(OriginalPath, tuyatemp, name);

					TuyaFileUtils.CopySingleFileToDel(tuyatemp+File.separator+name, directoryPath, picName);
					File  temp  = new  File(tuyatemp+File.separator+name);
					if(temp.exists()){
						temp.delete();
					}
                    istuya = true;
					initTuyaBitmap();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			    
				

			}
		}).setNegativeButton("取消", null).show();
		
	}
	
	public void spinBitmap(int degree) {
		tuyaView.setSrc(CameraBitmapUtil.spinPicture(tuyaView.src, degree));

		if (tuyaView.src.getWidth() > tuyaView.src.getHeight()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
	}

	public void saveBitmap() {
		if (null == tuyaView.dis) {
			Toast.makeText(TuYaActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
			return;
		}
		FileOutputStream out = null;
		Bitmap saveBitmap = null;
		try {

			if (scale > 0) {
				saveBitmap = CameraBitmapUtil.BitmapScale(tuyaView.dis, 1 / scale);
			}

			File outFile = new File(directoryPath, saveReName);
			out = new FileOutputStream(outFile);
			if (null == saveBitmap) {
				saveBitmapToFile(tuyaView.dis, out, outFile);
			} else {
				saveBitmapToFile(saveBitmap, out, outFile);
			}

		} catch (Exception e) {

			Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (Error e) {

			Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} finally {
			if (null != saveBitmap) {
				saveBitmap.recycle();
				saveBitmap = null;
			}
			if (null != out) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
				}
			}
			System.gc();
		}
	}

	public void saveBitmapToFile(Bitmap mBitmap, FileOutputStream fos, File outFile) {

		if (mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos)) {

			Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
			istuya = true;

		} else {
			Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
		}

	}

	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	// 检查图片文件是否存在
	public boolean CheckFileisExist() {

		if (TextUtils.isEmpty(picName) || TextUtils.isEmpty(directoryPath)) {
			return false;
		}

		// if (!picName.endsWith(".jpg")) {
		// return false;
		// }
		try {
			File file = new File(directoryPath, picName);
			return file.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 获取重命名的图片名
	public String getReName() {

		if (picName.endsWith("tuya.jpg")) {
			return getFileNameNotExtension(picName, 4).trim() + "Atuya.jpg";
		}
		return getFileNameNotExtension(picName, 1).trim() + "tuya.jpg";
	}

	public static String getFileNameNotExtension(String str, int lastlength) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		int index = str.lastIndexOf(File.separator);
		if (index == -1) {
			if (str.indexOf(".") == -1) {
				return "";
			}
			return str.substring(0, str.lastIndexOf(".") - lastlength);
		}
		String strname = str.substring(index);
		if (strname.indexOf(".") == -1) {
			return "";
		}
		return strname.substring(0, str.lastIndexOf(".") - lastlength);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.putExtra(KEY_PIC_DIRECTORYPATH, directoryPath);
			intent.putExtra(KEY_PIC_NAME, picName);
			
			if (istuya) {
				try {
					File   file = new  File(directoryPath, saveReName);
					if(!file.exists()){
						intent.putExtra(KEY_SAVE_RENAME, picName);
					}else{
						intent.putExtra(KEY_SAVE_RENAME, saveReName);
					}
				} catch (Exception e) {
					e.printStackTrace();
					intent.putExtra(KEY_SAVE_RENAME, saveReName);
				}
				
				setResult(RESULT_OK, intent);

			} else {
				setResult(RESULT_CANCELED, intent);
			}
			finish();
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onLongClick(View v) {
		if (v.getId() == R.id.imagefilter_clean) {
			tuyaView.clear();
			tuyaView.postInvalidate();

		} else if (v.getId() == R.id.imagefilter_left) {
			if (tuyaView.shapList.size() <= 1) {
				return true;
			}
			postion--;
			if (postion < 0) {
				postion = tuyaView.shapList.size() - 1;
			}
			showToast("移动之前的涂鸦对象");
		} else if (v.getId() == R.id.imagefilter_right) {
			if (tuyaView.shapList.size() <= 1) {
				return true;
			}
			postion++;
			if (postion >= tuyaView.shapList.size()) {
				postion = 0;
			}
			showToast("移动后面一个涂鸦对象");
		}
		return true;
	}

	private boolean isopenMenu = true;

	private String menuTitle = "隐藏菜单栏";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(1, 1, 1, menuTitle);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {

			if (isopenMenu) {

				menuTitle = "显示菜单栏";
				imagefilter_activity_layout2.setVisibility(View.GONE);
			} else {
				menuTitle = "隐藏菜单栏";
				imagefilter_activity_layout2.setVisibility(View.VISIBLE);
			}
			isopenMenu = !isopenMenu;
			item.setTitle(menuTitle);
		}
		return super.onOptionsItemSelected(item);
	}

	public interface TuyaListion {
		public void moveLeft(int left, int postion);

		public void moveright(int right, int postion);

		public void movetop(int top, int postion);

		public void movebottom(int bottom, int postion);

		public void spin(int angle, int postion);

	}

	public void MoveCurrorPostion(int pos) {
		postion = pos;
	}

}
