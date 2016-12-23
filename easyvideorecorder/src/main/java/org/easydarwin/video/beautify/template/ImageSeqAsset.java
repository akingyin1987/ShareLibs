package org.easydarwin.video.beautify.template;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * 
 * 图片序列（png）
 * 
 */
public class ImageSeqAsset extends Asset {

	// 图片URI列表（循环）
	private List<Uri> imageUriList;

	// 缓存Bitmap对象
	private List<SoftReference<Bitmap>> imageList = null;

	private int attachSize = 0;

	public ImageSeqAsset() {
		super();
		setAssetType(AssetType.IMAGE);
	}

	public List<Uri> getImageUriList() {
		return imageUriList;
	}

	public void setImageUriList(List<Uri> imageUriList) {
		this.imageUriList = imageUriList;

		attachSize = imageUriList == null ? 0 : imageUriList.size();
		loadImage();
	}

	/**
	 * 取缓存的Bitmap对象
	 * 
	 * @param index
	 * @return
	 */
	public Bitmap getImage(int index) {

		if (imageList == null) {
			return null;
		}

		SoftReference<Bitmap> softBitmap = imageList.get(index);

		return softBitmap == null ? null : softBitmap.get();
	}

	private void loadImage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (imageUriList != null && imageUriList.size() > 0) {
					imageList = new ArrayList<SoftReference<Bitmap>>(imageUriList.size());
					Bitmap bitmap = null;
					for (Uri uri2 : imageUriList) {
						bitmap = BitmapFactory.decodeFile(uri2.getPath());
						imageList.add(new SoftReference<Bitmap>(bitmap));
					}
				}
			}
		}).start();
	}

	public int getAttachSize() {
		return attachSize;
	}

}
