package org.easydarwin.video.beautify.template;

import android.graphics.Bitmap;

public class VideoClip extends MediaClip {

	public VideoClip() {
		super();
	}

	public VideoClip(Asset asset) {
		super(asset);
	}

	@Override
	public VideoAsset getAsset() {
		return (VideoAsset) super.getAsset();
	}

	public void startDecode() {
		getAsset().startDecode();
		// 跳转到素材入点
		if (getOffset() > getAsset().getCursor()) {
			int deffer = (int) (getOffset() - getAsset().getCursor());
			for (int i = 0; i < deffer; i++) {
				getNextFrame();
			}
		}
	}

	public Bitmap getNextFrame() {
		Bitmap curFrame = getAsset().getNextFrame();
		return curFrame;
	}

}
