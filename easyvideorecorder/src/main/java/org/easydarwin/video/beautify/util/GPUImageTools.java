package org.easydarwin.video.beautify.util;

import jp.co.cyberagent.android.gpuimage.GPUImage;

import org.easydarwin.video.beautify.template.Filter;
import org.easydarwin.video.beautify.template.FilterUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class GPUImageTools {

	private GPUImage gpuImage = null;

	private static GPUImageTools instance = null;

	public GPUImageTools(Context context) {
		gpuImage = new GPUImage(context);
	}

	public static GPUImageTools getInstance() {
		if (instance == null) {
			Context context = ContextHolder.getInstance().getContext();
			instance = new GPUImageTools(context);
		}
		return instance;
	}

	public Bitmap blend(Bitmap bitmap1, Bitmap bitmap2) {

		Filter filter = FilterUtils.buildDecorateFilter();
		filter.setAttachImage(bitmap1);

		gpuImage.setFilter(filter.getGpuImageFilter());
		gpuImage.setImage(bitmap2);

		Bitmap result = gpuImage.getBitmapWithFilterApplied();

		Matrix matrix = new Matrix();
		matrix.postScale(1, -1); // 镜像垂直翻转
		result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);

		return result;
	}

}
