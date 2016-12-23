package org.easydarwin.video.beautify.template;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageTwoInputFilter;
import jp.co.cyberagent.android.gpuimage.util.GPUImageFilterTools;
import jp.co.cyberagent.android.gpuimage.util.GPUImageFilterTools.FilterAdjuster;

import org.easydarwin.video.beautify.conf.Conf;

import android.graphics.Bitmap;
import android.net.Uri;

public class Filter extends MediaClip {

	/** 滤镜类型（GPUImage FilterType） */
	private String filterType;

	/** 滤镜的可调参数 */
	private int percentage = 50;

	/** 附加素材 */
	private Uri attachment;

	private int attachId;

	private Bitmap attachImage;

	private String name;

	private GPUImageFilter gpuImageFilter = null;

	private FilterAdjuster adjuster = null;

	private int fadeIn = 0;

	private int fadeOut = 0;

	/** 当前模板处理的帧序号 */
	private long frameIndex;

	public Filter() {
		super();
	}

	/**
	 * 设置动态参数
	 */
	public void applyDynamicParam() {
		// 设置背景图片
		if (gpuImageFilter instanceof GPUImageTwoInputFilter) {
			Bitmap bitmap = getTemplateImage();
			if (bitmap != null) {
				// 先释放前次加载的图像
				((GPUImageTwoInputFilter) gpuImageFilter).setBitmap(bitmap);
			}
		}

		if (fadeIn > 0 && frameIndex <= getOffset() + fadeIn) {
			calFadeIn(fadeIn);
			adjuster.adjust(percentage);
		} else if (fadeOut > 0 && frameIndex >= getOutPoint() - fadeOut) {
			calFadeOut(fadeOut);
			adjuster.adjust(percentage);
		}
	}

	/**
	 * 根据模板处理的时序（毫秒）计算混合滤镜的背景图片索引
	 * 
	 * @return
	 */
	private int getImageIndex(ImageSeqAsset imageSeqAsset, long frameIndex) {

		int attachSize = imageSeqAsset.getAttachSize();
		if (attachSize == 1 || this.getDuration() == 0) {
			return 0;
		}

		// 每张图的持续时间（帧）
		long perDuration = this.getDuration() / attachSize;
		frameIndex = frameIndex - getOffset();
		int imageIndex = (int) (frameIndex / perDuration);
		if (imageIndex >= attachSize) {
			// 循环
			if (isRecycle()) {
				imageIndex = imageIndex - attachSize;
			} else {
				imageIndex = attachSize - 1;
			}
		}

		return imageIndex;
	}

	/** 所属视频节点的时间线入点 */
	@Override
	public long getOffset() {
		return super.getOffset();
	}

	@Override
	public FilterEffect getEffect() {
		return (FilterEffect) super.getEffect();
	}

	public long getFrameIndex() {
		return frameIndex;
	}

	public void setFrameIndex(long frameIndex) {
		this.frameIndex = frameIndex;
	}

	public void close() {
		if (getAsset() != null && getAsset().getAssetType() == AssetType.VIDEO) {
			((VideoAsset) getAsset()).closeDecode();
			setAsset(null);
		}
		if (gpuImageFilter != null) {
			gpuImageFilter.destroy();
			gpuImageFilter = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	/**
	 * 获取背景图片，支持静态图片、图片序列、视频
	 * 
	 * @return
	 */
	protected Bitmap getTemplateImage() {
		if (getAssetType() == null) {
			return null;
		}
		switch (getAssetType()) {
		case IMAGE:
			ImageSeqAsset imageSeqAsset = (ImageSeqAsset) getAsset();
			if (imageSeqAsset.getAttachSize() == 1) {
				if (frameIndex == getOffset()) {
					return imageSeqAsset.getImage(0);
				}
			} else {
				int imageIndex = getImageIndex(imageSeqAsset, frameIndex);
				if (imageIndex >= 0) {
					return imageSeqAsset.getImage(imageIndex);
				}
			}
			return null;
		case VIDEO:
			// 视频，需要先解码
			VideoAsset videoAsset = (VideoAsset) getAsset();
			Bitmap frame = null;
			if (videoAsset.getCursor() == 0) {
				videoAsset.startDecode();
			}
			if (videoAsset.getCursor() < getOutPoint()) {
				frame = videoAsset.getNextFrame();
			}
			if (frame == null) {
				if (isRecycle()) {
					videoAsset.startDecode();
					frame = videoAsset.getNextFrame();
				}
			}
			return frame;
		default:
			return null;
		}

	}

	public GPUImageFilter getGpuImageFilter() {
		if (gpuImageFilter == null) {
			gpuImageFilter = FilterUtils.genGPUImageFilter(this);
			adjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
			if (adjuster != null) {
				adjuster.adjust(getPercentage());
			}
		}
		return gpuImageFilter;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public Uri getAttachment() {
		return attachment;
	}

	public void setAttachment(Uri attachment) {
		this.attachment = attachment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAttachId() {
		return attachId;
	}

	public void setAttachId(int attachId) {
		this.attachId = attachId;
	}

	public Bitmap getAttachImage() {
		return attachImage;
	}

	public void setAttachImage(Bitmap attachImage) {
		this.attachImage = attachImage;
	}

	protected void calFadeIn(int fade) {
		float percent = (getFrameIndex() - getOffset()) / (float) fade;
		// 模拟指数曲线
		percentage = (int) (((Math.exp(percent) - 1) / (Math.E - 1)) * 100);
	}

	protected void calFadeOut(int fade) {
		float percent = (getOutPoint() - getFrameIndex()) / (float) fade;
		// 模拟指数曲线
		percentage = (int) (((Math.exp(percent) - 1) / (Math.E - 1)) * 100);
	}

	public void setFadeOut(int fadeOut) {
		this.fadeOut = fadeOut / (1000/Conf.VIDEO_FRAMERATE);//this.getFrameRate()
	}

	public void setFadeIn(int fadeIn) {
		this.fadeIn = fadeIn / (1000/Conf.VIDEO_FRAMERATE);//this.getFrameRate()
	}

	public int getFilterSize() {
		if (getGpuImageFilter() instanceof GPUImageFilterGroup) {
			return ((GPUImageFilterGroup) getGpuImageFilter()).getFilters().size();
		} else {
			return 1;
		}
	}

}
