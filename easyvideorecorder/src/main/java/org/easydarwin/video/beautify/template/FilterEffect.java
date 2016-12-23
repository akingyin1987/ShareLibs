package org.easydarwin.video.beautify.template;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

import org.easydarwin.video.beautify.activity.VideoBeautifyActivity;
import org.easydarwin.video.beautify.util.ContextHolder;

import android.graphics.Bitmap;

/**
 * 
 * 应用GPUImage执行滤镜特效
 * 
 */
public class FilterEffect extends Effect {

	private GPUImage gpuImage = null;

	private GPUImageView gpuImageView = null;

	// 支持多个滤镜效果
	private GPUImageFilterGroup filterGroup = null;

	public FilterEffect() {
		super();
	}

	/**
	 * 生成GPUImage实例
	 * 
	 * @return GPUImage instance
	 */
	private GPUImage getGPUImage() {
		if (gpuImage != null) {
			return gpuImage;
		}

		gpuImage = new GPUImage(ContextHolder.getInstance().getContext());

		if (filterGroup.getFilters().size() == 1) {
			gpuImage.setFilter(filterGroup.getFilters().get(0));
		} else {
			filterGroup.updateMergedFilters();
			gpuImage.setFilter(filterGroup);
		}

		return gpuImage;
	}

	private void setGPUImage() {
		if (gpuImageView != null) {
			return;
		}

		VideoBeautifyActivity activity = (VideoBeautifyActivity) ContextHolder.getInstance().getActivity();
		gpuImageView = activity.mGPUImageView;

		if (filterGroup.getFilters().size() == 1) {
			gpuImageView.setFilter(filterGroup.getFilters().get(0));
		} else {
			filterGroup.updateMergedFilters();
			gpuImageView.setFilter(filterGroup);
		}

	}

	@Override
	public Bitmap applyEffect(Bitmap curFrame, boolean isPreview) {

		if (isPreview) {
			if (gpuImageView == null) {
				setGPUImage();
			}

			gpuImageView.setImage(curFrame);

			return curFrame;

		} else {
			if (gpuImage == null) {
				gpuImage = getGPUImage();
			}

			gpuImage.setImage(curFrame);
			curFrame = gpuImage.getBitmapWithFilterApplied();

			return curFrame;
		}

	}

	protected void addEffect(GPUImageFilter gpuImageFilter, int index) {
		if (filterGroup == null) {
			filterGroup = new GPUImageFilterGroup();
		}
		if (index < 0) {
			index = filterGroup.getFilters().size();
		}
		if (!filterGroup.getFilters().contains(gpuImageFilter)) {
			filterGroup.getFilters().add(index, gpuImageFilter);
			filterGroup.updateMergedFilters();
			if (gpuImageView != null) {
				gpuImageView.setFilter(filterGroup);
			} else if (gpuImage != null) {
				gpuImage.setFilter(filterGroup);
			}
		}
	}

	protected void removeEffect(GPUImageFilter gpuImageFilter) {
		if (filterGroup.getFilters().contains(gpuImageFilter)) {
			filterGroup.getFilters().remove(gpuImageFilter);
			filterGroup.updateMergedFilters();
			if (gpuImageView != null) {
				gpuImageView.setFilter(filterGroup);
			} else if (gpuImage != null) {
				gpuImage.setFilter(filterGroup);
			}
		}
	}

	public void clear() {
		if (gpuImageView != null) {
			gpuImageView.clear();
			gpuImageView = null;
		}

		if (gpuImage != null) {
			gpuImage.clear();
			gpuImage = null;
		}

		if (filterGroup != null) {
			filterGroup.onDestroy();
			filterGroup = null;
		}
		System.gc();
	}

}
