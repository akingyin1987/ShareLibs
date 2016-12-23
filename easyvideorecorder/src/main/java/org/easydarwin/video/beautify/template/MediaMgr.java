package org.easydarwin.video.beautify.template;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.net.Uri;

public class MediaMgr {

	// 构造没有裁剪的视频素材对象
	protected static List<MediaClip> buildMediaClip(AssetType assetType, Uri... uris) {
		List<MediaClip> mediaClips = new ArrayList<MediaClip>(uris.length);
		for (Uri uri : uris) {
			VideoAsset videoAsset = (VideoAsset) AssetMgr.buildAsset(assetType, uri);
			mediaClips.add(buildMediaClip(videoAsset, 0, videoAsset.getDuration()));
		}
		return mediaClips;
	}

	// 从资源裁剪得到素材
	public static MediaClip buildMediaClip(Asset asset, long offset, long duration) {
		if (asset == null) {
			throw new IllegalStateException("asset can not be null!");
		}

		MediaClip mediaClip = null;

		switch (asset.getAssetType()) {
		case VIDEO:
			mediaClip = createMediaClip(VideoClip.class, asset);
			break;
		case IMAGE:
			mediaClip = createMediaClip(ImageSeqClip.class, asset);
			break;
		case AUDIO:
			mediaClip = createMediaClip(AudioClip.class, asset);
			break;
		case TITTLE:
			mediaClip = createMediaClip(Tittle.class, asset);
			break;
		default:
			break;
		}

		mediaClip.setOffset(offset);
		mediaClip.setDuration(duration);

		return mediaClip;
	}

	private static MediaClip createMediaClip(Class<? extends MediaClip> mediaClipClass, Asset asset) {
		try {
			MediaClip mediaClip = mediaClipClass.newInstance();
			mediaClip.setAsset(asset);
			mediaClip.setId(asset.getAssetType().toString() + System.currentTimeMillis());
			return mediaClip;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获得字幕素材包装对象
	 * 
	 * @param tittleImage
	 *            字幕转成的iamge
	 * @param offset
	 *            入点（帧序号）
	 * @param duration
	 *            时长（持续帧数）
	 * @return
	 */
	public static Tittle createTittle(Bitmap tittleImage, long offset, long duration) {

		TittleAsset tittleAsset = (TittleAsset) AssetMgr.buildAsset(AssetType.TITTLE, null);
		//
		tittleAsset.setTittleImage(tittleImage);

		Tittle tittle = (Tittle) buildMediaClip(tittleAsset, offset, duration);

		return tittle;
	}

	/**
	 * 获得音频素材包装对象
	 * 
	 * @param audioUri
	 *            音频文件uri
	 * @param offset
	 *            入点（帧序号）
	 * @param duration
	 *            时长（持续帧数）
	 * @return
	 */
	public static AudioClip createAudio(Uri audioUri, long offset, long duration) {

		Asset asset = AssetMgr.buildAsset(AssetType.AUDIO, audioUri);

		AudioClip audio = (AudioClip) buildMediaClip(asset, offset, duration);

		return audio;
	}

}
