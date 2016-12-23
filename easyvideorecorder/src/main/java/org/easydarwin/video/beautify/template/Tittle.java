package org.easydarwin.video.beautify.template;

import android.graphics.Bitmap;

/**
 * 
 * 字幕
 *
 */
public class Tittle extends MediaClip {

	private Filter filter;

	public Tittle() {
		super();
	}

	public Tittle(Asset asset) {
		super(asset);
	}

	@Override
	public TittleAsset getAsset() {
		return (TittleAsset) super.getAsset();
	}

	public Filter getFilter() {
		if (filter == null) {
			filter = FilterUtils.buildTittleFilter();
			Bitmap bitmap = getAsset().getTittleImage();
			filter.setAttachImage(bitmap);
			filter.setOffset(getOffset());
			filter.setDuration(getDuration());
		}
		return filter;
	}

}
