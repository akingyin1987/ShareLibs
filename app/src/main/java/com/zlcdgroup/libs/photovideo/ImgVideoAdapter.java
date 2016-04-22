package com.zlcdgroup.libs.photovideo;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

import com.zlcdgroup.base.model.BaseImgTextItem;
import com.zlcdgroup.util.CameraBitmapUtil;



public class ImgVideoAdapter extends BaseDynamicGridAdapter<BaseImgTextItem> {

	@SuppressLint("UseSparseArrays")
	public ImgVideoAdapter(Context context, List<BaseImgTextItem> items, int columnCount) {
		super(context, items, columnCount);
		
		
	}

	public  static Map<Long, Bitmap> cache   = new LinkedHashMap<Long, Bitmap>();;
	
	//移出某一个
	public void  remove(long  key){
		Bitmap  bitmap  = cache.get(key);
		if(null != bitmap){
			bitmap.recycle();
			bitmap = null;
		}
		cache.remove(key);
	}
	
	

	public void cleanCache() {
		if (null != cache) {

			for (Long key : cache.keySet()) {
				Bitmap bitmap = cache.get(key);
				bitmap.recycle();
				bitmap = null;
			}
			cache.clear();
			System.gc();
		}
	}
	
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BaseImgTextItem   imgTextItem = getItem(position);
		ImgVideoItemView   itemVideoItemView = null;
		if(null == convertView){
			itemVideoItemView = com.zlcdgroup.photovideo.ImgVideoItemView_.build(getContext());
		}else{
			itemVideoItemView = (ImgVideoItemView) convertView;
		}
		if(imgTextItem.style == 1){
			itemVideoItemView.bind(imgTextItem, null);
		}else{
			Bitmap   bitmap  =  cache.get(imgTextItem.getId());
			if(null == bitmap){
				try {
					File  file  =  new   File(imgTextItem.localPath);
					if(file.exists()){
						if(imgTextItem.style == 2){
							bitmap = CameraBitmapUtil.getImageThumbnail(imgTextItem.localPath, 320, 200);
						}else{
							bitmap = CameraBitmapUtil.getVideoThumbnail(imgTextItem.localPath,320, 200, MediaStore.Images.Thumbnails.MICRO_KIND);
						}
						if(null != bitmap){
							cache.put(imgTextItem.getId(), bitmap);						
						}
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			itemVideoItemView.bind(imgTextItem, bitmap);
		}
		return itemVideoItemView;
	}
	
	//设置涂鸦被选中
	public    void     setTuyaChecked(int  postion){
		for(int i=0;i<getCount() ;i++){
			if(postion == i){				
			    getItem(i).setIschecked(true);
			}else{
				getItem(i).setIschecked(false);
			}
		}
		notifyDataSetChanged();
	}
	
	//清除所有选中
	public   void     cleanChecked(){
		for(int i=0 ; i<getCount() ; i++){
			getItem(i).setIschecked(false);
		}
		notifyDataSetChanged();
	}
	
	//选中一个
	public   void     Multiselect(int  postion){
		if(postion >= 0 && postion < getCount() ){
			BaseImgTextItem  item  =  getItem(postion);
			item.setIschecked(!item.isIschecked());
			notifyDataSetChanged();
		}
	}
	
	
}
