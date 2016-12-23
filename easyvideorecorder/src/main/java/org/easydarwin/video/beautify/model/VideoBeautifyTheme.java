package org.easydarwin.video.beautify.model;

import android.graphics.Bitmap;

public class VideoBeautifyTheme {
	private String type;// 为了区别主题和滤镜1表示主题,2表示滤镜
	private String id; // ID
	private String name; // 名
	private Bitmap image; // //图片icon
	private String imagepath;
	private String download;// 是否是下载的资源0表示 内置资源1表示下载资源

	public VideoBeautifyTheme() {

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

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImagepath() {
		return imagepath;
	}

	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	public String getDownload() {
		return download;
	}

	public void setDownload(String download) {
		this.download = download;
	}

}
