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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.utils.CameraBitmapUtil;


public class ImgVideoAdapter extends BaseDynamicGridAdapter<BaseImgTextItem> {

	@SuppressLint("UseSparseArrays")
	public ImgVideoAdapter(Context context, List<BaseImgTextItem> items, int columnCount) {
		super(context, items, columnCount);
		
		
	}

	public  static Map<Long, Bitmap> cache   = new LinkedHashMap<Long, Bitmap>();
	
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
			itemVideoItemView = new ImgVideoItemView();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_video_item,null);
			itemVideoItemView.copy_img =(ImageView) convertView.findViewById(R.id.copy_img);
			itemVideoItemView.img = (ImageView)convertView.findViewById(R.id.img);
			itemVideoItemView.txt = (TextView)convertView.findViewById(R.id.txt);
			itemVideoItemView.video_img = (ImageView)convertView.findViewById(R.id.video_img);
			itemVideoItemView.img_layout = (LinearLayout)convertView.findViewById(R.id.img_layout);
			convertView.setTag(itemVideoItemView);
		}else{
			itemVideoItemView = (ImgVideoItemView) convertView.getTag();
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
		return convertView;
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
