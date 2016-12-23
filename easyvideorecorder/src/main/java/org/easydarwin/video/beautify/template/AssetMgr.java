package org.easydarwin.video.beautify.template;

import android.net.Uri;

/**
 * 
 * 视频处理的资源管理器
 * 
 */
public class AssetMgr {

	public static Asset buildAsset(final AssetType assetType, final Uri uri) {

		switch (assetType) {
		case VIDEO:
			return createAsset(VideoAsset.class, uri);
		case AUDIO:
			return createAsset(AudioAsset.class, uri);
		case IMAGE:
			return createAsset(ImageSeqAsset.class, uri);
		case TITTLE:
			return createAsset(TittleAsset.class, uri);
		default:
			throw new IllegalStateException("No asset of that type!");
		}

	}

	private static Asset createAsset(Class<? extends Asset> assetClass, Uri uri) {
		try {
			Asset asset = assetClass.newInstance();
			asset.setUri(uri);
			asset.setId(asset.getAssetType().toString() + String.valueOf(System.currentTimeMillis()));
			return asset;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
