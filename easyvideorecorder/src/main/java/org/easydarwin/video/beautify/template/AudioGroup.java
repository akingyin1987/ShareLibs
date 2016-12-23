package org.easydarwin.video.beautify.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class AudioGroup extends AudioClip {

	private List<AudioClip> audioClips = null;

	private long frameIndex;

	public AudioGroup() {
		audioClips = new ArrayList<AudioClip>();
	}

	public void addAudio(AudioClip audioClip) {
		audioClips.add(audioClip);
	}

	@Override
	public void applyEffect(Context ctx) {
		AudioClip audioClip = null;
		for (Iterator<AudioClip> iterator = audioClips.iterator(); iterator.hasNext();) {
			audioClip = iterator.next();
			if (audioClip.getEffect() == null) {
				audioClip.applyEffect(ctx);
				Log.i("ExcecuteProject", "applyAudio:" + audioClip.getAsset().getUri());
			}
			if (frameIndex >= audioClip.getOutPoint()) {
				audioClip.close();
			}
		}
	}

	@Override
	public void close() {
		for (AudioClip audioClip : audioClips) {
			audioClip.close();
		}
	}

	public void setFrameIndex(long frameIndex) {
		this.frameIndex = frameIndex;
	}

	public byte[] mixAudio(byte[] audioSrc, boolean useSrcAudio) {

		if (!useSrcAudio) {
			for (int i = 0; i < audioSrc.length; i++) {
				audioSrc[i] = 0;
			}
		}

		byte[] mixedSamples = audioSrc;
		AudioClip audioClip = null;
		for (Iterator<AudioClip> iterator = audioClips.iterator(); iterator.hasNext();) {
			audioClip = iterator.next();
			mixedSamples = audioClip.getAsset().mixAudio(mixedSamples);
			Log.i("ExcecuteProject", "mixAudio:" + audioClip.getAsset().getUri());
		}

		return mixedSamples;
	}

}
