package org.easydarwin.video.beautify.template;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class AudioEffect extends Effect {

	private MediaPlayer player = null;

	private static MediaPlayer srcAudioPlayer = new MediaPlayer();

	private static boolean isSrcMute = false;

	public AudioEffect() {
		super();
	}

	public AudioEffect(String id) {
		super(id);
	}

	@Override
	public Bitmap applyEffect(Bitmap curFrame, boolean isPreview) {

		return curFrame;
	}

	protected void applyEffect(Uri uri,Context ctx) {
		if (uri != null) {
			player = MediaPlayer.create(ctx, uri);
			player.setVolume(1.0f, 1.0f);
			player.setLooping(true);
			player.start();
		}

	}

	public void close() {
		if (player != null) {
			try {
				player.stop();
				player.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
			player = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	public static void playSrcAudio(Uri srcAudio,Context ctx) {
		
		try {
			srcAudioPlayer = MediaPlayer.create(ctx, srcAudio);
		} catch (Exception e) {
			Log.e("error", e.getMessage());
		}
		
		if (isSrcMute) {
			srcAudioPlayer.setVolume(0.0f, 0.0f);
		} else {
			srcAudioPlayer.setVolume(1.0f, 1.0f);
		}
		srcAudioPlayer.start();

	}

	public static void setSrcPlayerMute(boolean isMute) {
		isSrcMute = isMute;
		if (srcAudioPlayer != null) {
			try {
				if (isMute) {
					srcAudioPlayer.setVolume(0.0f, 0.0f);
				} else {
					srcAudioPlayer.setVolume(1.0f, 1.0f);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void stopSrcPlayer() {
		if (srcAudioPlayer != null) {
			try {
				srcAudioPlayer.stop();
				srcAudioPlayer.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
			srcAudioPlayer = null;
		}
	}

}
