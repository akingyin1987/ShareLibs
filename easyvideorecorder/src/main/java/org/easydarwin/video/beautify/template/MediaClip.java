/**
 * 
 */
package org.easydarwin.video.beautify.template;

/**
 * 
 * 视频处理的素材抽象类
 * 
 */
public abstract class MediaClip {

	private String id;

	/** 引用资源 */
	private Asset asset;

	/** 资源长度 */
	private long duration;

	/** 素材入点（精确帧） */
	private long offset;

	// 素材出点（精确帧）

	/** 是否对齐循环 */
	private boolean recycle = true;

	/** 帧率 */
	private int frameRate;

	private Effect effect;

	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public MediaClip() {

	}

	public MediaClip(Asset asset) {
		if (asset == null) {
			throw new IllegalStateException("MediaClip:asset should not be null!");
		}
		this.asset = asset;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public AssetType getAssetType() {
		return asset == null ? null : asset.getAssetType();
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public boolean isRecycle() {
		return recycle;
	}

	public void setRecycle(boolean recycle) {
		this.recycle = recycle;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public long getOutPoint() {
		return offset + duration;
	}

}
