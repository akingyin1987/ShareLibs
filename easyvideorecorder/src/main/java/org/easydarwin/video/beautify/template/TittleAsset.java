package org.easydarwin.video.beautify.template;

import android.graphics.Bitmap;

/**
 * 
 * 字体Font
 * 
 */
public class TittleAsset extends Asset {

	private Bitmap tittleImage = null;

	public TittleAsset() {
		super();
		setAssetType(AssetType.TITTLE);
	}

	public Bitmap getTittleImage() {
		return tittleImage;
	}

	public void setTittleImage(Bitmap tittleImage) {
		this.tittleImage = tittleImage;
	}

}
