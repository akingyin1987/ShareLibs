/**
 * 
 */
package org.easydarwin.video.beautify.template;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * 
 * 视频处理特效抽象类
 *
 */
public abstract class Effect {

	private String id;

	private String name;

	private Uri icon;

	public Effect() {

	}

	public Effect(String id) {
		if (id == null) {
			throw new IllegalStateException("Effect: id should not be null!");
		}
		this.id = id;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Uri getIcon() {
		return icon;
	}

	public void setIcon(Uri icon) {
		this.icon = icon;
	}

	/**
	 * 执行特效
	 * 
	 * @param curFrame
	 *            当前帧
	 * @return 处理后的帧
	 */
	abstract public Bitmap applyEffect(Bitmap curFrame, boolean isPreview);

}
