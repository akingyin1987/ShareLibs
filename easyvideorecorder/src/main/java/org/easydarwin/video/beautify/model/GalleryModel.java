package org.easydarwin.video.beautify.model;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.net.Uri;

public class GalleryModel implements Serializable {

	private static final long serialVersionUID = 5751407082315686041L;

	private String id;

	private Bitmap image;

	private Uri imageUri;

	private int frameIndex;

	public GalleryModel() {
		super();
	}

	public GalleryModel(Uri imageUri, int frameIndex) {
		super();
		this.imageUri = imageUri;
		this.frameIndex = frameIndex;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public int getFrameIndex() {
		return frameIndex;
	}

	public void setFrameIndex(int frameIndex) {
		this.frameIndex = frameIndex;
	}

	public Uri getImageUri() {
		return imageUri;
	}

	public void setImageUri(Uri imageUri) {
		this.imageUri = imageUri;
	}

}
