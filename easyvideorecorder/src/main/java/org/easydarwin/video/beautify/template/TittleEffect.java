package org.easydarwin.video.beautify.template;

import android.graphics.Bitmap;

/**
 * 
 * 字幕特效
 *
 */
public class TittleEffect extends Effect {

	// 字幕素材
	private Tittle tittle;

	public Tittle getTittle() {
		return tittle;
	}

	public void setTittle(Tittle tittle) {
		this.tittle = tittle;
	}

	@Override
	public Bitmap applyEffect(Bitmap curFrame, boolean isPreview) {
		return null;
	}

}
