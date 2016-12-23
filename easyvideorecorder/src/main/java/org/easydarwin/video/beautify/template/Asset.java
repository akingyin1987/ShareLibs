/**
 * 
 */
package org.easydarwin.video.beautify.template;

import android.net.Uri;

/**
 * 视频处理的资源抽象类
 * 
 */
public abstract class Asset {

	private String id;

	// 资源类型
	private AssetType assetType;

	// 
	private Uri uri;

	public Asset() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}
}
