package org.easydarwin.video.beautify.template;

import org.easydarwin.video.beautify.conf.Conf;
import org.easydarwin.video.render.MediaSource;
import org.easydarwin.video.render.MediaTarget;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 
 * 视频资源（mp4）
 * 
 */
public class VideoAsset extends Asset {

	// 资源长度 （精确到帧）
	private long duration = -1;

	// 资源使用游标，即当前处理的帧序号
	private long cursor = 0;

	private int width;

	private int height;

	private MediaSource mediaSource = null;

	private MediaTarget mediaTarget = null;

	// 添加音乐
	private AudioGroup audioGroup = null;

	// 消除原音
	private boolean useSrcAudio;

	public VideoAsset() {
		super();
		setAssetType(AssetType.VIDEO);
	}

	public long getDuration() {
		if (mediaSource == null) {
			startDecode();
		}
		if (duration == -1) {
			duration = mediaSource.duration() * Conf.VIDEO_FRAMERATE / 1000;
		}
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getCursor() {
		return cursor;
	}

	public void setCursor(long cursor) {
		this.cursor = cursor;
	}

	public void startDecode() {
		if (mediaSource != null) {
			return;
		}
		mediaSource = new MediaSource();
		mediaSource.init(getUri().getPath());
		int open = mediaSource.open();
		if (open != 0) {
			// 异常处理
			Log.e("ExcecuteProject", "open source fail:" + open + "@" + getUri().getPath());
		}
		Log.i("ExcecuteProject", "startDecode:" + getUri().getPath() + "@" + mediaSource);
	}

	public Bitmap getNextFrame() {
		if (mediaSource.getFrame() != 0) {
			// 异常处理
			closeDecode();
			return null;
		}

		while (!mediaSource.isVideo()) {

			if (mediaTarget != null) {

				if (audioGroup != null) {
					// 混音
					mediaTarget.pushAudio(audioGroup.mixAudio(mediaSource.currentAudio(), useSrcAudio),
							mediaSource.audioCurrentNumberOfSamples());
				} else {
					if (useSrcAudio) {
						mediaTarget.pushAudio(mediaSource.currentAudio(), mediaSource.audioCurrentNumberOfSamples());
					}
				}
			}

			if (mediaSource.getFrame() != 0) {
				closeDecode();
				return null;
			}
		}

		// 移动游标
		cursor++;
		Bitmap currentFrame = mediaSource.currentFrame();
		if (cursor >= getDuration() || mediaSource.currentFrame() == null) {
			closeDecode();
		}

		return currentFrame;

	}

	public void closeDecode() {
		if (mediaSource != null) {
			mediaSource.close();
			Log.i("ExcecuteProject", "closeDecode:" + getUri().getPath() + "@" + mediaSource);
			mediaSource = null;
			cursor = 0;
		}
	}

	public void startEncode() {
		if (mediaTarget != null) {
			return;
		}
		mediaTarget = new MediaTarget();
		mediaTarget.init(getUri().getPath());
		mediaTarget.setVideoWidth(480);
		mediaTarget.setVideoHeight(480);
		//
		mediaTarget.open();
	}

	public void appendFrame(Bitmap frame) {
		mediaTarget.pushFrame(frame);
	}

	public void colseEncode() {
		if (mediaTarget != null) {
			mediaTarget.close();
			Log.i("ExcecuteProject", "colseEncode:" + mediaTarget);
			mediaTarget = null;
		}
	}

	public int getWidth() {
		if (mediaSource == null) {
			startDecode();
		}
		if (width == 0) {
			width = mediaSource.videoWidth();
		}
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		if (mediaSource == null) {
			startDecode();
		}
		if (height == 0) {
			height = mediaSource.videoHeight();
		}
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public MediaTarget getMediaTarget() {
		return mediaTarget;
	}

	public void setMediaTarget(MediaTarget mediaTarget) {
		this.mediaTarget = mediaTarget;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public void setMusic(AudioGroup music) {
		audioGroup = music;
	}

	public void setUseSrcAudio(boolean useSrcAudio) {
		this.useSrcAudio = useSrcAudio;
	}

}
