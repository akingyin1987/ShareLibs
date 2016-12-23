package org.easydarwin.video.beautify.template;

import android.content.Context;

public class AudioClip extends MediaClip {

	public AudioClip() {
		super();
	}

	public AudioClip(Asset asset) {
		super(asset);
	}

	@Override
	public AudioEffect getEffect() {
		return (AudioEffect) super.getEffect();
	}

	private void buildEffect() {
		// //
		AudioEffect effect = new AudioEffect();
		setEffect(effect);
	}

	public void applyEffect(Context ctx) {
		if (super.getEffect() == null) {
			buildEffect();
		}
		getEffect().applyEffect(getAsset().getUri(),ctx);
	}

	public void close() {
		if (getEffect() != null) {
			getEffect().close();
		}
		if (getAsset() != null) {
			getAsset().closeDecode();
		}
	}

	@Override
	public AudioAsset getAsset() {
		return (AudioAsset) super.getAsset();
	}

}
