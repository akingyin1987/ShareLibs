package com.zlcdgroup.libs.photovideo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.askerov.dynamicgrid.DynamicGridView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.zlcdgroup.camera.MaterialCamera;
import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.photovideo.vo.ImageTextsVo;
import com.zlcdgroup.libs.photovideo.vo.TempBaseVo;
import com.zlcdgroup.libs.utils.CameraBitmapUtil;
import com.zlcdgroup.libs.utils.DialogCallback;
import com.zlcdgroup.libs.utils.FileUtil;
import com.zlcdgroup.tuyalib.TuYaActivity;


/**
 * 
 * @Description: TODO(图片视频编辑)
 * @author king
 * @date 2015-5-22 下午1:49:27
 * @version V1.0 
 */

public abstract class ImageVideoActivity extends Activity implements OnClickListener {

	private TextView title_name, ivTitleBtnLeft,ivTitleBtnLeftButton;

	private ImageView ivTitleBtnRightImage;
	
	

	private LinearLayout operate_item_layout, operate_result_layout, operate_layout;

	// 基础数据
	private List<BaseImgTextItem> baseImgTextItems = new ArrayList<BaseImgTextItem>();
	// 排序使用数据
	private List<BaseImgTextItem> sortBaseImgTextItems = new ArrayList<BaseImgTextItem>();
	// 文档
	private List<BaseImgTextItem> textsortBaseImgTextItems = new ArrayList<BaseImgTextItem>();

	private DynamicGridView photos_gridview, photos_gridview_sorttext;

	private TextView photo, video, addtext;

	private RadioGroup imgtextsort_type;

	public RadioButton texts_sort, imgs_sort;

	private View addtext_title, photo_video_title, custom_operation_title;

	private TextView tuya, sort,sortall, copy, remark, delect, custom_operation;

	private TextView cancel, finished, operation_info;
	public static ImageVideoModel imageVideoModel = ImageVideoModel.NULL;

	public static ImageVideoTypeModel videoTypeModel = ImageVideoTypeModel.NAV;
	public ImgVideoAdapter adapter, textsortAdapter;

	@SuppressLint("UseSparseArrays")
	private Map<Long, BaseImgTextItem> copyMap = new HashMap<Long, BaseImgTextItem>();

	public static ModelSort modelSort = ModelSort.NULL;

	private LinearLayout photo_video_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_photo_video);
		title_name = (TextView) findViewById(R.id.title_name);
		ivTitleBtnLeft = (TextView) findViewById(R.id.ivTitleBtnLeft);
		ivTitleBtnLeft.setText("返回");
		ivTitleBtnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
		
		imgtextsort_type = (RadioGroup) findViewById(R.id.imgtextsort_type);
		imgs_sort = (RadioButton) findViewById(R.id.imgs_sort);
		texts_sort = (RadioButton) findViewById(R.id.texts_sort);
		//findViewById(R.id.ivTitleBtnRightText).setVisibility(View.GONE);
		
		ivTitleBtnRightImage = (ImageView) findViewById(R.id.ivTitleBtnRightImage);
		ivTitleBtnRightImage.setVisibility(View.VISIBLE);
		ivTitleBtnRightImage.setImageResource(R.drawable.zhantie);
		ivTitleBtnRightImage.setOnClickListener(this);
		operate_item_layout = (LinearLayout) findViewById(R.id.operate_item_layout);
		operate_result_layout = (LinearLayout) findViewById(R.id.operate_result_layout);
		operate_layout = (LinearLayout) findViewById(R.id.operate_layout);
		photos_gridview = (DynamicGridView) findViewById(R.id.photos_gridview);
		photos_gridview_sorttext = (DynamicGridView) findViewById(R.id.photos_gridview_sorttext);
		photo_video_title = findViewById(R.id.photo_video_title);
		addtext_title = findViewById(R.id.addtext_title);
		addtext = (TextView) findViewById(R.id.addtext);
		addtext.setOnClickListener(this);
		photo = (TextView) findViewById(R.id.photo);
		photo.setOnClickListener(this);
		sortall = (TextView)findViewById(R.id.sortall);
		sortall.setOnClickListener(this);
		video = (TextView) findViewById(R.id.video);
		video.setOnClickListener(this);
		tuya = (TextView) findViewById(R.id.tuya);
		tuya.setOnClickListener(this);
		sort = (TextView) findViewById(R.id.sort);
		sort.setOnClickListener(this);
		copy = (TextView) findViewById(R.id.copy);
		copy.setOnClickListener(this);
		remark = (TextView) findViewById(R.id.remark);
		remark.setOnClickListener(this);
		delect = (TextView) findViewById(R.id.delect);
		delect.setOnClickListener(this);
		cancel = (TextView) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		custom_operation = (TextView) findViewById(R.id.custom_operation);
		custom_operation_title = findViewById(R.id.custom_operation_title);
		custom_operation.setOnClickListener(this);
		finished = (TextView) findViewById(R.id.finished);
		photo_video_content = (LinearLayout) findViewById(R.id.photo_video_content);
		finished.setOnClickListener(this);
		photos_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				photos_gridview(position);

			}
		});
		
		photos_gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("imglort");
				if(imageVideoModel == ImageVideoModel.SORT2){
					ImageTextsVo imag2 = new ImageTextsVo();
					List<BaseImgTextItem> res2 = new ArrayList<BaseImgTextItem>();
					
					res2.add( (BaseImgTextItem) adapter.getItem(position));
					imag2.setImgs(res2);
					Intent mIntent2 = new Intent(ImageVideoActivity.this, PhotoViewpagerActivity.class);
					mIntent2.putExtra("imgeList", imag2);
					mIntent2.putExtra("pos", position);
					startActivity(mIntent2);
					return false;
				}
				return true;
			}
		});
		photos_gridview_sorttext.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			     if(imageVideoModel == ImageVideoModel.SORT2){
			    	 //移回去
			    	 
			    	 List<BaseImgTextItem>  listsortdata = new ArrayList<BaseImgTextItem>();
			    	 listsortdata.addAll(adapter.getItems());
			    	 adapter.clear();
			    	 BaseImgTextItem  item = textsortAdapter.getItem(position);
			    	 if(textsortAdapter.getCount() == 2){
			    		 listsortdata.addAll(textsortAdapter.getItems());
			    		 textsortAdapter.clear();
			    	 }else{
			    		 listsortdata.add(item);
			    	 }
			    	
			    	 Collections.sort(listsortdata, new Comparator<BaseImgTextItem>() {

							@Override
							public int compare(BaseImgTextItem lhs, BaseImgTextItem rhs) {
								if (lhs.sort > rhs.sort) {
									return 1;
								} else if (lhs.sort < rhs.sort) {
									return -1;
								}
								return 0;
							}
					});
			    	if(textsortAdapter.getCount() >0){
			    		textsortAdapter.remove(item);
			    	}
			 
			    	System.out.println("list.size="+listsortdata.size()+":"+adapter.getCount());
			    	adapter.add(listsortdata);
			    	System.out.println("adapter.size="+adapter.getCount());
			     }
				
			}
		});
		photos_gridview_sorttext.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("textlort");
				if(imageVideoModel == ImageVideoModel.SORT2){
					ImageTextsVo imag = new ImageTextsVo();
					List<BaseImgTextItem> res = new ArrayList<BaseImgTextItem>();
					for (int i = 0; i < textsortAdapter.getCount(); i++) {
						BaseImgTextItem imagetext = (BaseImgTextItem) textsortAdapter.getItem(i);
						res.add(imagetext);
					}
					imag.setImgs(res);
					Intent mIntent = new Intent(ImageVideoActivity.this, PhotoViewpagerActivity.class);
					mIntent.putExtra("imgeList", imag);
					mIntent.putExtra("pos", position);
					startActivity(mIntent);
					return true;
				}
				return false;
			}
		});
		operation_info = (TextView) findViewById(R.id.operation_info);
		imgtextsort_type.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(imageVideoModel == ImageVideoModel.SORT2){
					if (checkedId == R.id.texts_sort) {
						System.out.println(adapter.getCount()+":--------");
						photos_gridview.setVisibility(View.VISIBLE);
						photos_gridview_sorttext.setVisibility(View.GONE);
					} else if (checkedId == R.id.imgs_sort) {
						photos_gridview.setVisibility(View.GONE);
						photos_gridview_sorttext.setVisibility(View.VISIBLE);
					}
				}else{
					if (checkedId == R.id.texts_sort) {
						photos_gridview.setVisibility(View.GONE);
						photos_gridview_sorttext.setVisibility(View.VISIBLE);
					} else if (checkedId == R.id.imgs_sort) {
						photos_gridview.setVisibility(View.VISIBLE);
						photos_gridview_sorttext.setVisibility(View.GONE);
					}
				}
				

			}
		});
		initView();

	}

	public void initView() {
		videoTypeModel = initImageVideoType();
		if (null == videoTypeModel) {
			videoTypeModel = ImageVideoTypeModel.NULL;
		}
		switch (videoTypeModel) {
		case NAV:
			modelSort = ModelSort.DOWN;
			// 图文制作
			ivTitleBtnLeft.setVisibility(View.GONE);
			ivTitleBtnLeftButton = (TextView) findViewById(R.id.ivTitleBtnLeftButton);
			ivTitleBtnLeftButton.setVisibility(View.VISIBLE);
			
			ivTitleBtnLeftButton.setText("向下增加");
			ivTitleBtnLeftButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (modelSort) {
					case DOWN:
						ivTitleBtnLeftButton.setText("向上增加");
						modelSort = ModelSort.UP;
						break;
					case UP:
						ivTitleBtnLeftButton.setText("向下增加");
						modelSort = ModelSort.DOWN;
						break;
					default:
						break;
					}

				}
			});
			break;
		case PHOTO:
			remark.setText("重拍");
			addtext.setVisibility(View.GONE);
			addtext_title.setVisibility(View.GONE);
			photo_video_title.setVisibility(View.GONE);
			video.setVisibility(View.GONE);
			break;
		case VIDEO:
			tuya.setVisibility(View.GONE);
			remark.setText("重拍");
			addtext.setVisibility(View.GONE);
			addtext_title.setVisibility(View.GONE);
			photo_video_title.setVisibility(View.GONE);
			photo.setVisibility(View.GONE);
			break;
		case PHOTO_VIDEO:
			remark.setText("重拍");
			addtext.setVisibility(View.GONE);
			addtext_title.setVisibility(View.GONE);
			break;
		case NULL:
			operate_item_layout.setVisibility(View.GONE);
			operate_layout.setVisibility(View.GONE);
			ivTitleBtnRightImage.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		addDATA(baseImgTextItems);
		adapter = new ImgVideoAdapter(this, baseImgTextItems, 2);
		photos_gridview.setAdapter(adapter);
		title_name.setText(getTitleName());
		CustomView(photo_video_content);
		customOperationView(custom_operation, custom_operation_title);
	}

	public  void   showToast(String message){
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
	}

	public void photo() {
		// 拍照
		if(adapter.getCount() >=15){
			showToast("图片最多可添加15张");
			return;
		}
		operate_result_layout.setVisibility(View.GONE);
		currentPostion = -1;
		imageVideoModel = ImageVideoModel.NULL;
		PaiZhao();
	}

	public void video() {
		// 拍照
		operate_result_layout.setVisibility(View.GONE);
		currentPostion = -1;
		imageVideoModel = ImageVideoModel.NULL;
		VideoRecorder();
	}

	public void remark() {
		operate_layout.setVisibility(View.GONE);
		operate_item_layout.setVisibility(View.GONE);
		operate_result_layout.setVisibility(View.VISIBLE);
		operation_info.setText(remark.getText());
		cancel.setVisibility(View.GONE);

		imageVideoModel = ImageVideoModel.REMARK;
	}

	public void delect() {
		operate_layout.setVisibility(View.GONE);
		operate_item_layout.setVisibility(View.GONE);
		operate_result_layout.setVisibility(View.VISIBLE);
		operation_info.setText("删除");
		cancel.setVisibility(View.VISIBLE);
		baseImgTextItems.clear();
		sortBaseImgTextItems.clear();
		for (int i = 0; i < adapter.getCount(); i++) {
			baseImgTextItems.add(adapter.getItem(i));
			sortBaseImgTextItems.add(adapter.getItem(i));
		}
		imageVideoModel = ImageVideoModel.DELECT;
	}

	public void tuya() {
		operate_layout.setVisibility(View.GONE);
		operate_item_layout.setVisibility(View.GONE);
		operate_result_layout.setVisibility(View.VISIBLE);
		operation_info.setText("图片涂鸦");
		cancel.setVisibility(View.GONE);

		imageVideoModel = ImageVideoModel.TUYA;

	}

	public void copy() {
		operate_layout.setVisibility(View.GONE);
		operate_item_layout.setVisibility(View.GONE);
		operate_result_layout.setVisibility(View.VISIBLE);
		operation_info.setText("复制");
		cancel.setVisibility(View.VISIBLE);

		imageVideoModel = ImageVideoModel.COPY;
	}

	public void sort() {
		if (adapter.getCount() <= 1) {
			showToast("文件至少2个才可排序");
			return;
		}
		operate_layout.setVisibility(View.GONE);
		operate_item_layout.setVisibility(View.GONE);
		operate_result_layout.setVisibility(View.VISIBLE);
		operation_info.setText("文件排序");
		cancel.setVisibility(View.VISIBLE);
		sortBaseImgTextItems.clear();
		baseImgTextItems.clear();
		textsortBaseImgTextItems.clear();
		for (int i = 0; i < adapter.getCount(); i++) {
			baseImgTextItems.add(adapter.getItem(i));
			if (adapter.getItem(i).style == 1) {
				textsortBaseImgTextItems.add(adapter.getItem(i));
			} else {
				sortBaseImgTextItems.add(adapter.getItem(i));
			}

		}

		imageVideoModel = ImageVideoModel.SORT;
		if (videoTypeModel == ImageVideoTypeModel.NAV) {
			photos_gridview.clearModificationHistory();
			adapter = new ImgVideoAdapter(this, sortBaseImgTextItems, 2);
			photos_gridview.setAdapter(adapter);
			imgtextsort_type.setVisibility(View.VISIBLE);
			imgs_sort.setChecked(true);

			textsortAdapter = new ImgVideoAdapter(this, textsortBaseImgTextItems, 2);
			photos_gridview_sorttext.setAdapter(textsortAdapter);

			photos_gridview_sorttext.startEditMode();

		} else {

		}

		photos_gridview.startEditMode();
	}
	
	//排序2
	public  void   sort2(){
		if (adapter.getCount() <= 1) {
			showToast("文件至少2个才可排序");
			return;
		}
		if(null == textsortAdapter){
			textsortAdapter = new ImgVideoAdapter(this, textsortBaseImgTextItems, 2);
			photos_gridview_sorttext.setAdapter(textsortAdapter);
		}else{
			textsortAdapter.clear();
			textsortAdapter.cleanCache();
		}
		
		operate_layout.setVisibility(View.GONE);
		operate_item_layout.setVisibility(View.GONE);
		operate_result_layout.setVisibility(View.VISIBLE);
		operation_info.setText("文件排序2");
		cancel.setVisibility(View.VISIBLE);
		sortBaseImgTextItems.clear();
		baseImgTextItems.clear();
		baseImgTextItems.addAll(adapter.getItems());
		imgtextsort_type.setVisibility(View.VISIBLE);
		texts_sort.setText("待排序");
		imgs_sort.setText("已排序");
		imageVideoModel = ImageVideoModel.SORT2;
		texts_sort.setChecked(true);
		
		
	}

	public void finished() {
        System.out.println("imageviewmodel="+imageVideoModel.toString());
		switch (imageVideoModel) {
		case TUYA:
			adapter.cleanChecked();

			break;
		case SORT:
			if (videoTypeModel == ImageVideoTypeModel.NAV) {
				imgtextsort_type.setVisibility(View.GONE);
				photos_gridview_sorttext.setVisibility(View.GONE);
				photos_gridview.setVisibility(View.VISIBLE);
				baseImgTextItems.clear();
				baseImgTextItems.addAll(textsortAdapter.getItems());
				baseImgTextItems.addAll(adapter.getItems());
				adapter.clear();
				adapter.add(baseImgTextItems);
			}
			try {
				ActiveAndroid.beginTransaction();
				for (int i = 0; i < adapter.getCount(); i++) {
					BaseImgTextItem item = adapter.getItem(i);
					item.sort = i + 1;
					item.save();

				}
				ActiveAndroid.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
				showToast("出错了");
			} finally {
				ActiveAndroid.endTransaction();
			}
			if (photos_gridview.isEditMode()) {
				photos_gridview.stopEditMode();
			}
			if (videoTypeModel == ImageVideoTypeModel.NAV) {
				if (photos_gridview_sorttext.isEditMode()) {
					photos_gridview_sorttext.stopEditMode();
				}
			}
			imageVideoModel = ImageVideoModel.NULL;
			break;
		case DELECT:
			try {
				ActiveAndroid.beginTransaction();
				Iterator<BaseImgTextItem> iterator = baseImgTextItems.iterator();
				while (iterator.hasNext()) {
					BaseImgTextItem imgTextItem = iterator.next();
					if (imgTextItem.isIschecked()) {
						imgTextItem.delete();
						iterator.remove();
					}
				}
				ActiveAndroid.setTransactionSuccessful();
				adapter.clear();
				adapter.add(baseImgTextItems);
			} catch (Exception e) {
				e.printStackTrace();
				showToast("删除失败");
			} finally {
				ActiveAndroid.endTransaction();
			}

			break;
		case COPY:
			
			if (copyMap.size() == 0) {
				adapter.cleanChecked();
				break;
			}
			showToast("开始复制");
			CopyFile();
			break;
		case REMARK:
			adapter.cleanChecked();
			break;
		case SORT2:
			if(baseImgTextItems.size() != textsortAdapter.getCount()){
				showToast("排序未完成无法完成");
				return;
			}
			try {
				for(int i=0;i<textsortAdapter.getCount();i++){
					BaseImgTextItem  item = textsortAdapter.getItem(i);
					item.sort = i+1;
					item.save();
				}
				adapter.clear();
				adapter.add(textsortAdapter.getItems());
				imgtextsort_type.setVisibility(View.GONE);
				photos_gridview.setVisibility(View.VISIBLE);
				photos_gridview_sorttext.setVisibility(View.GONE);
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		default:
			break;
		}
		imageVideoModel = ImageVideoModel.NULL;
		operate_layout.setVisibility(View.VISIBLE);
		operate_item_layout.setVisibility(View.VISIBLE);
		operate_result_layout.setVisibility(View.GONE);
		DataChange();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photo:
			photo();
			break;
		case R.id.video:
			video();
			break;
		case R.id.tuya:
			tuya();
			break;
		case R.id.sort:
			sort();
			break;
		case R.id.copy:
			copy();
			break;
		case R.id.remark:
			remark();

			break;
		case R.id.delect:
			delect();
			break;
		case R.id.cancel:
			cancel();
			break;
		case R.id.finished:
			finished();
			break;
		case R.id.sortall:
			sort2();
			break;
		case R.id.ivTitleBtnRightImage:
			if (Temp.tempData.size() == 0) {
				showToast("没有可粘贴的数据");
				return;
			}
			if(adapter.getCount() + Temp.tempData.size()>=15){
				showToast("图片最多可添加15，不可再粘贴!");
				return;
			}
			boolean pastRes = false;
			if (videoTypeModel == ImageVideoTypeModel.PHOTO) {
				for (BaseImgTextItem imageTextVo : Temp.tempData) {
					if (!pastRes && imageTextVo.style == 2) {
						pastRes = true;
					}
				}
			} else if (videoTypeModel == ImageVideoTypeModel.VIDEO) {
				for (BaseImgTextItem imageTextVo : Temp.tempData) {
					if (!pastRes && imageTextVo.style == 3) {
						pastRes = true;
					}
				}
			} else {
				pastRes = true;
			}
			if (!pastRes) {
				showToast("没有可粘贴的数据");
				return;
			}

			PasteNavs();
			break;
		case R.id.addtext:
			showEditDialog(null, new DialogCallback() {

				@Override
				public void yes() {
					String message = dialogEditText.getText().toString().trim();
					if (TextUtils.isEmpty(message)) {
						showToast("图文项文字不能为空！");
					}
					int sort = getAddImgTextPostion(false);
					BaseImgTextItem item = addText(sort, message);
					adapter.add(sort, item);
					try {
						ActiveAndroid.beginTransaction();
						for (int i = sort; i < adapter.getCount(); i++) {
							adapter.getItem(i).sort = i + 1;
							adapter.getItem(i).save();
						}
						DataChange();
						ActiveAndroid.setTransactionSuccessful();
					} catch (Exception e) {
						e.printStackTrace();
						showToast("保存出错了");
					} finally {
						ActiveAndroid.endTransaction();
					}

				}

				@Override
				public void no() {
					// TODO Auto-generated method stub

				}
			});
			break;
		case R.id.custom_operation:
			customOperation();
			break;
		default:
			break;
		}

	}

	public TextView dialogtitle;
	public EditText dialogEditText;
	public Dialog textDialog;

	public void showEditDialog(String message, final DialogCallback callback) {
		if (null != textDialog && textDialog.isShowing()) {
			textDialog.dismiss();
		}
		if (null == textDialog) {
			textDialog = new Dialog(this, R.style.loading_dialog);
			textDialog.setContentView(R.layout.custom_2btn_edit_dialog);
			textDialog.setCancelable(false);
			textDialog.setCanceledOnTouchOutside(false);
			dialogtitle = (TextView) textDialog.findViewById(R.id.dialogTitle);
			dialogEditText = (EditText) textDialog.findViewById(R.id.dialogEdit);
			dialogEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(150) });
		}
		textDialog.findViewById(R.id.dialogLeftBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				textDialog.dismiss();
				callback.no();

			}
		});
		textDialog.findViewById(R.id.dialogRightBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				textDialog.dismiss();
				callback.yes();

			}
		});

		dialogtitle.setText("图文文字项编辑");
		if (TextUtils.isEmpty(message)) {
			dialogEditText.setText("");
		} else {
			dialogEditText.setText(message);
			dialogEditText.setSelection(message.length());
		}
		textDialog.show();
	}

	public void CopyFile() {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					Temp.tempData.clear();
					for (BaseImgTextItem item : copyMap.values()) {
						System.out.println("null=="+(null == item));
						TempBaseVo  imageText = item.Copy();
						if (item.style == 2) {
							String fileName = Temp.getUUID() + ".jpg";
							imageText.localPath = Temp.TEMP_ROOT + File.separator + fileName;
							FileUtil.CopySingleFileTo(item.localPath, Temp.TEMP_ROOT, fileName);

						} else if (item.style == 3) {
							String fileName = Temp.getUUID() + ".mp4";
							imageText.localPath = Temp.TEMP_ROOT + File.separator + fileName;
							FileUtil.CopySingleFileTo(item.localPath, Temp.TEMP_ROOT, fileName);
						}

						Temp.tempData.add(imageText);
					}
					pasteHandler.sendEmptyMessage(2);
				} catch (Exception e) {
					e.printStackTrace();
					pasteHandler.sendEmptyMessage(-2);
				}
			}

		}.start();

	}

	public void cancel() {

		switch (imageVideoModel) {
		case SORT:
			if (videoTypeModel == ImageVideoTypeModel.NAV) {
				imgtextsort_type.setVisibility(View.GONE);
				photos_gridview_sorttext.setVisibility(View.GONE);
				photos_gridview.setVisibility(View.VISIBLE);
				if (photos_gridview_sorttext.isEditMode()) {
					photos_gridview_sorttext.stopEditMode();
				}
		
				textsortAdapter.clear();
				textsortAdapter.notifyDataSetChanged();
			}
			adapter.clear();
			adapter.add(baseImgTextItems);
			adapter.cleanChecked();
			if (photos_gridview.isEditMode()) {
				photos_gridview.stopEditMode();
			}
         
			break;
		case SORT2:
			imgtextsort_type.setVisibility(View.GONE);
			photos_gridview_sorttext.setVisibility(View.GONE);
			photos_gridview.setVisibility(View.VISIBLE);
			adapter.clear();
			adapter.add(baseImgTextItems);
			break;
		case DELECT:
			adapter.cleanChecked();
			break;
		case COPY:
			adapter.cleanChecked();
			copyMap.clear();
			break;
		default:
			break;
		}
		imageVideoModel = ImageVideoModel.NULL;
		operate_layout.setVisibility(View.VISIBLE);
		operate_item_layout.setVisibility(View.VISIBLE);
		operate_result_layout.setVisibility(View.GONE);
	}

	public void photos_gridview(int postion) {
		final BaseImgTextItem item = adapter.getItem(postion);
		switch (imageVideoModel) {
		case TUYA:
			if (item.style != 2) {
				showToast("只有图片支持涂鸦!");
				return;
			}
			if (!item.isImageExist()) {
				showToast("文件不存在!");
				return;
			}
			adapter.setTuyaChecked(postion);
			currentPostion = postion;
			Tuya(item.localPath,item.originalLocalPath);
			break;
		case REMARK:
			adapter.setTuyaChecked(postion);
			currentPostion = postion;
			if (item.style == 2) {
				PaiZhao();
			} else if (item.style == 3) {
				VideoRecorder();
			} else if (item.style == 1) {
				showEditDialog(item.textdesc, new DialogCallback() {

					@Override
					public void yes() {
						String message = dialogEditText.getText().toString().trim();
						if (TextUtils.isEmpty(message)) {
							showToast("图文项文字不能为空！");
							return;
						}
						item.textdesc = message;
						item.save();
						adapter.notifyDataSetChanged();
					}

					@Override
					public void no() {
						// TODO Auto-generated method stub

					}
				});
			}

			break;
		case DELECT:
			adapter.Multiselect(postion);
			break;
		case COPY:

			adapter.Multiselect(postion);
			if (null == copyMap.get(item.getId())) {
				copyMap.put(item.getId(), item);
			} else {
				copyMap.remove(item.getId());
			}
			break;
		case SORT2:
			BaseImgTextItem  moveitem = adapter.getItem(postion);
			textsortAdapter.add(moveitem);
			adapter.remove(moveitem);
			
			if(adapter.getCount() == 1){
				BaseImgTextItem   moveItem2 = adapter.getItem(0);
				adapter.clear();
				textsortAdapter.add(moveItem2);
			}
			break;
		case NULL:
			ImageTextsVo imag = new ImageTextsVo();
			List<BaseImgTextItem> res = new ArrayList<BaseImgTextItem>();
			for (int i = 0; i < adapter.getCount(); i++) {
				BaseImgTextItem imagetext = adapter.getItem(i);
				res.add(imagetext);
			}
			imag.setImgs(res);
			Intent mIntent = new Intent(ImageVideoActivity.this, PhotoViewpagerActivity.class);
			mIntent.putExtra("imgeList", imag);
			mIntent.putExtra("pos", postion);
			startActivity(mIntent);
			break;
		default:
			break;
		}
	}

	public static final int REQUEST_CODE_SYSCAMERA = 2;// 系统相机
	public static final int REQUEST_CODE_CUESTOMCAMERA = 3;// 自定义相机
	public static final int REQUEST_CODE_TUYA = 4;
	public static final int REQUEST_CODE_SYSVIDEO = 6;// 系统拍视频
	public static final int REQUEST_CODE_CUESTOMVIDEO = 7;// 自定义拍视频

	public int getMaxSort() {
		int sort = 0;
		for (BaseImgTextItem imageTextVo : baseImgTextItems) {
			if (imageTextVo.sort > sort) {
				sort = imageTextVo.sort;
			}
		}
		return sort + 1;
	}

	public static String localPath = "";

	@SuppressLint("SimpleDateFormat")
	public void PaiZhao() {

		int cameratype = new Random().nextInt(2);
		String directory = getImageDirectory();

		File file = new File(directory);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}

		String pictureName = Temp.getUUID()+ ".jpg";
		localPath = directory + File.separator + pictureName;

		Intent mIntent = null;
		if (cameratype == 1) {
			mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(localPath)));
			startActivityForResult(mIntent, REQUEST_CODE_SYSCAMERA);
		} else if (cameratype == 0) {
			new MaterialCamera(this).saveDir(directory).saveName(pictureName).startAuto(REQUEST_CODE_CUESTOMCAMERA);
		}
	}

	@SuppressLint("SimpleDateFormat")
	public void VideoRecorder() {
		try {

			String directory = getVideoDirectory();
            int   cameratype = new Random().nextInt(2);
			File file = new File(directory);
			if (!file.exists() || !file.isDirectory()) {
				file.mkdirs();
			}

			String pictureName = Temp.getUUID() + ".mp4";
			localPath = directory + File.separator + pictureName;
			Intent mIntent = null;
			if (cameratype == 0) {
				
				mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				// mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
				// AppConfig.VIDEO_QUALITY);
				mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, Temp.DURATION_LIMIT);
				mIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, Temp.SIZE_LIMIT);

				mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(localPath)));
				startActivityForResult(mIntent, REQUEST_CODE_SYSVIDEO);
			} else if (cameratype == 1) {
				// mIntent = new Intent(this, FFmpegRecorderActivity.class);
				// FFmpegRecorderActivity.directory = directory;
				// FFmpegRecorderActivity.videoName = pictureName;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public String getFileName(int model) {

		String pictureName = Temp.getUUID() + "copy.mp4";
		if (model == 1) {
			pictureName = Temp.getUUID() + "copy.jpg";
		}
		return pictureName;
	}



	@SuppressLint("SimpleDateFormat")
	public void Tuya(String imgpath,String  original) {

		String directory = getImageDirectory();


		String pictureName = Temp.getUUID() + "tuya" + ".jpg";

		
		localPath = directory + File.separator + pictureName;
		Intent intent = new Intent(this, TuYaActivity.class);
		intent.putExtra(TuYaActivity.KEY_PIC_DIRECTORYPATH, FileUtil.getFolderName(imgpath));
		intent.putExtra(TuYaActivity.KEY_PIC_NAME, FileUtil.getFileName(imgpath));
		intent.putExtra(TuYaActivity.KEY_SAVE_RENAME, pictureName);
		intent.putExtra(TuYaActivity.KEY_PIC_ORIGINAL, original);
		startActivityForResult(intent, REQUEST_CODE_TUYA);
	}

	public static int currentPostion = -1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("onActivityResult"+requestCode+":"+resultCode);
		// 系统相机
		if (requestCode == REQUEST_CODE_SYSCAMERA) {
			if (resultCode == RESULT_OK) {
				if (CameraBitmapUtil.SysCameraZipImage(localPath)) {
					DataChange();
					if (currentPostion == -1 && adapter.getCount() == 0) {
						fristAdd(System.currentTimeMillis());
					}
					if (currentPostion == -1) {
						int sort = 0;
						if (videoTypeModel == ImageVideoTypeModel.NAV) {
							sort = getAddImgTextPostion(true);
						} else {
							sort = getMaxSort();
						}
						BaseImgTextItem imageBaseImgTextItem = addImage(sort, localPath);
						if (videoTypeModel == ImageVideoTypeModel.NAV) {
							adapter.add(sort, imageBaseImgTextItem);
							currentPostion = sort;

						} else {
							currentPostion = adapter.getCount() - 1;
							adapter.add(imageBaseImgTextItem);
						}
						baseImgTextItems.add(imageBaseImgTextItem);

					} else {
						BaseImgTextItem imageBaseImgTextItem = adapter.getItem(currentPostion);
						imageBaseImgTextItem.localPath = localPath;
						imageBaseImgTextItem.copyName = null;
						imageBaseImgTextItem.save();
						adapter.remove(imageBaseImgTextItem.getId());
						adapter.notifyDataSetChanged();

					}
					if (videoTypeModel == ImageVideoTypeModel.NAV) {
						Tuya(localPath,localPath);
					}
				} else {
					showToast("内存不足，拍照出错了");
				}
			}

		} else if (requestCode == REQUEST_CODE_CUESTOMCAMERA ) {
			if(resultCode == RESULT_OK || resultCode == RESULT_CANCELED){

				// 自定义相机

				try {
					File file = new File(localPath);
					if (file.exists()) {
						DataChange();
						if (currentPostion == -1 && adapter.getCount() == 0) {
							fristAdd(System.currentTimeMillis());
						}
						if (currentPostion == -1) {
							int sort = 0;
							if (videoTypeModel == ImageVideoTypeModel.NAV) {
								sort = getAddImgTextPostion(true);
							} else {
								sort = getMaxSort();
							}
							BaseImgTextItem imageBaseImgTextItem = addImage(sort, localPath);
							baseImgTextItems.add(imageBaseImgTextItem);
							if (videoTypeModel == ImageVideoTypeModel.NAV) {
								adapter.add(sort, imageBaseImgTextItem);
								currentPostion = sort;

							} else {
								adapter.add(imageBaseImgTextItem);
								currentPostion = adapter.getCount() - 1;
							}

						} else {
							BaseImgTextItem imageBaseImgTextItem = adapter.getItem(currentPostion);
							imageBaseImgTextItem.localPath = localPath;
							imageBaseImgTextItem.copyName = null;
							imageBaseImgTextItem.save();
							adapter.remove(imageBaseImgTextItem.getId());
							adapter.notifyDataSetChanged();

						}
						if (videoTypeModel == ImageVideoTypeModel.NAV) {
							Tuya(localPath,localPath);
						}

					} else {
						//showToast("拍照出错了");
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToast("拍照出错了");
				}
			
			
			}
		} else if (requestCode == REQUEST_CODE_CUESTOMVIDEO) {
			try {
				File file = new File(localPath);
				if (file.exists()) {
					DataChange();
					if (currentPostion == -1 && adapter.getCount() == 0) {
						fristAdd(System.currentTimeMillis());
					}
					if (currentPostion == -1) {
						int sort = 0;
						if (videoTypeModel == ImageVideoTypeModel.NAV) {
							sort = getAddImgTextPostion(true);
							currentPostion = sort;
						} else {
							sort = getMaxSort();
							currentPostion = adapter.getCount() - 1;
						}
						BaseImgTextItem imageBaseImgTextItem = addVideo(sort, localPath);
						baseImgTextItems.add(imageBaseImgTextItem);
						if (videoTypeModel == ImageVideoTypeModel.NAV) {
							adapter.add(sort, imageBaseImgTextItem);
						} else {
							adapter.add(imageBaseImgTextItem);
						}

					} else {
						BaseImgTextItem imageBaseImgTextItem = adapter.getItem(currentPostion);
						imageBaseImgTextItem.localPath = localPath;
						imageBaseImgTextItem.copyName = null;
						imageBaseImgTextItem.save();
						adapter.remove(imageBaseImgTextItem.getId());
						adapter.notifyDataSetChanged();
					}

				}
			} catch (Exception e) {

			}
		} else if (requestCode == REQUEST_CODE_SYSVIDEO) {
			try {
				if (resultCode == RESULT_OK) {
					File file = new File(localPath);
					if (file.exists()) {
						DataChange();
						if (currentPostion == -1 && adapter.getCount() == 0) {
							fristAdd(System.currentTimeMillis());
						}
						if (currentPostion == -1) {
							int sort = 0;
							if (videoTypeModel == ImageVideoTypeModel.NAV) {
								sort = getAddImgTextPostion(true);
							} else {
								sort = getMaxSort();
							}
							BaseImgTextItem imageBaseImgTextItem = addVideo(sort, localPath);
							baseImgTextItems.add(imageBaseImgTextItem);

							if (videoTypeModel == ImageVideoTypeModel.NAV) {
								currentPostion = sort;
								adapter.add(sort, imageBaseImgTextItem);
							} else {
								adapter.add(imageBaseImgTextItem);
								currentPostion = adapter.getCount() - 1;
							}

						} else {

							BaseImgTextItem imageBaseImgTextItem = adapter.getItem(currentPostion);
							imageBaseImgTextItem.localPath = localPath;
							imageBaseImgTextItem.copyName = null;
							imageBaseImgTextItem.save();
							adapter.remove(imageBaseImgTextItem.getId());
							adapter.notifyDataSetChanged();
						}

					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		} else if (requestCode == REQUEST_CODE_TUYA) {
			// 涂鸦
			if (resultCode == RESULT_OK) {

				String tuyapath = data.getStringExtra(TuYaActivity.KEY_SAVE_RENAME);
				String directoryPath = data.getStringExtra(TuYaActivity.KEY_PIC_DIRECTORYPATH);
				try {
					File file = new File(directoryPath, tuyapath);
					if (file.exists()) {
						DataChange();
						BaseImgTextItem item = adapter.getItem(currentPostion);
						item.localPath = file.getPath();
						item.webPath = null;
						item.copyName = null;
						item.save();
						adapter.remove(item.getId());
						adapter.notifyDataSetChanged();
					} else {
						showToast("涂鸦出错了");
					}
				} catch (Exception e) {
					showToast("涂鸦出错了");
				}

			}
		}
	}

	int error = 0;
	public List<BaseImgTextItem> pasteBaseImgTextItems = new ArrayList<BaseImgTextItem>();

	public void PasteNavs() {
		new Thread() {

			@Override
			public void run() {

				super.run();
				try {
					error = 0;
					pasteBaseImgTextItems.clear();
					ActiveAndroid.beginTransaction();

					List<BaseImgTextItem> pasteList = new ArrayList<BaseImgTextItem>(Temp.tempData);
					Iterator<BaseImgTextItem> iterator = pasteList.iterator();
					while (iterator.hasNext()) {
						BaseImgTextItem imageTextVo = iterator.next();
						if (videoTypeModel == ImageVideoTypeModel.PHOTO) {
							if (imageTextVo.style == 1 || imageTextVo.style == 3) {
								iterator.remove();
								continue;
							}
						} else if (videoTypeModel == ImageVideoTypeModel.VIDEO) {
							if (imageTextVo.style == 1 || imageTextVo.style == 2) {
								iterator.remove();
								continue;
							}
						}
						if (EQ(imageTextVo)) {
							iterator.remove();
						}
					}
					Collections.sort(pasteList, new Comparator<BaseImgTextItem>() {

						@Override
						public int compare(BaseImgTextItem lhs, BaseImgTextItem rhs) {
							if (lhs.sort > rhs.sort) {
								return 1;
							} else if (lhs.sort < rhs.sort) {
								return -1;
							}
							return 0;
						}
					});

					int startSort = getMaxSort() + 1;
					// 粘贴数据
					String Directory = getImageDirectory();
					File file = new File(Directory);
					if (!file.exists() || !file.isDirectory()) {
						file.mkdirs();
					}
					for (BaseImgTextItem imageTextVo : pasteList) {
						String newFileName = getFileName(imageTextVo.style - 1);
						if (!TextUtils.isEmpty(imageTextVo.localPath)) {

							String oldFileName = imageTextVo.copyName;

							boolean auth = FileUtil.CopySingleFileTo(imageTextVo.localPath, Directory, newFileName);
							if (!auth) {
								error++;
							}
							if (2 == imageTextVo.style) {
								BaseImgTextItem item = PasteImageVideo(startSort, 2, Directory + File.separator + newFileName, oldFileName);

								pasteBaseImgTextItems.add(item);

							} else if (imageTextVo.style == 3) {
								BaseImgTextItem item = PasteImageVideo(startSort, 3, Directory + File.separator + newFileName, oldFileName);

								pasteBaseImgTextItems.add(item);
							}
						} else {
							if (imageTextVo.style == 1) {
								BaseImgTextItem item = addText(getAddImgTextPostion(false), imageTextVo.textdesc);
								pasteBaseImgTextItems.add(item);
							}

						}
						startSort++;

					}

					ActiveAndroid.setTransactionSuccessful();
					pasteHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					e.printStackTrace();
					pasteHandler.sendEmptyMessage(-1);
				} finally {
					ActiveAndroid.endTransaction();
				}

			}

		}.start();
	}

	@SuppressLint("HandlerLeak")
	public Handler pasteHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			if (msg.what == 1) {

				if (error > 0) {
					showToast("有" + error + "个文件粘贴失败!");
				} else {
					showToast("粘贴成功");
				}
				if (adapter.getCount() == 0 && pasteBaseImgTextItems.size() > 0) {
					fristAdd(System.currentTimeMillis());
				}
				adapter.add(pasteBaseImgTextItems);
				DataChange();
			} else if (msg.what == -1) {

				showToast("粘贴失败");
			} else if (msg.what == -2) {

				showToast("复制出错了！");
				adapter.cleanChecked();
			} else if (msg.what == 2) {

				showToast("复制成功");
				adapter.cleanChecked();
			}
		}

	};

	// 获取添加图文的顺序 true=图文的位置　false = 文字的位置
	public int getAddImgTextPostion(boolean addpostion) {

		if (adapter.getCount() == 0) {
			return 0;
		}
		if (modelSort == ModelSort.DOWN) {

			if (addpostion) {
				return adapter.getCount();
			}

			for (int i = adapter.getCount() - 1; i >= 0; i--) {

				if (adapter.getItem(i).isImage() == addpostion) {

					return i + 1;
				}
			}
		} else if (modelSort == ModelSort.UP) {

			if (!addpostion) {
				return 0;
			}
			for (int i = 0; i < adapter.getCount(); i++) {
				if (adapter.getItem(i).isImage() == addpostion) {
					return i;
				}
			}
			return adapter.getCount();
		}
		return 0;

	}

	public boolean EQ(BaseImgTextItem imageTextVo) {
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItem(i).eqImageText(imageTextVo)) {

				return true;
			}
		}
		return false;
	}

	public boolean isExist(long id, String path) {
		for (int i = 0; i < adapter.getCount(); i++) {
			BaseImgTextItem item = adapter.getItem(i);
			if (id != item.getId()) {
				if (item.localPath.equals(path)) {
					return true;
				}
			}
		}
		return false;
	}

	public abstract String getTitleName();

	// 添加 基础数据
	public abstract void addDATA(List<BaseImgTextItem> baseImgTextItems);

	// 获取图片目录
	public abstract String getImageDirectory();

	// 获取视频目录
	public abstract String getVideoDirectory();

	// 第一次添加时返回时间
	public abstract void fristAdd(long time);

	// 添加文字
	public abstract BaseImgTextItem addText(int sort, String message);

	// 添加图片
	public abstract BaseImgTextItem addImage(int sort, String localpath);

	// 添加视频
	public abstract BaseImgTextItem addVideo(int sort, String localpath);

	// 粘贴图文 需要把最原始的图片路径改变
	public abstract BaseImgTextItem PasteImageVideo(int sort, int style, String localPath, String oldFileName);

	// 初始化类型
	public abstract ImageVideoTypeModel initImageVideoType();

	// 自定义扩展显示
	public abstract void CustomView(LinearLayout content);

	// 当前项数据改变调用
	public abstract void DataChange();

	// 自定义操作
	public abstract void customOperation();

	// 自定义操作VIEW
	public abstract void customOperationView(TextView customOperation, View v);

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			if(null != adapter){
				adapter.cleanCache();
			}
			if(null != textsortAdapter){
				textsortAdapter.cleanCache();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK &&
				event.getRepeatCount() == 0){
			if(imageVideoModel != ImageVideoModel.SORT2){
				for(int i=0;i<adapter.getCount();i++){
					BaseImgTextItem   item = adapter.getItem(i);
					System.out.println("sort="+item.sort);
					item.sort = i+1;
					item.save();
				}
			}
			
			imageVideoModel = ImageVideoModel.NULL;
			
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
	
}
