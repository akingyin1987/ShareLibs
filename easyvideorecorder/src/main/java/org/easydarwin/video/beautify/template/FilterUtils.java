package org.easydarwin.video.beautify.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageTwoInputFilter;
import jp.co.cyberagent.android.gpuimage.util.GPUImageFilterTools;
import jp.co.cyberagent.android.gpuimage.util.GPUImageFilterTools.FilterType;

import org.easydarwin.video.R;
import org.easydarwin.video.beautify.conf.Conf;
import org.easydarwin.video.beautify.util.ContextHolder;
import org.easydarwin.video.beautify.util.ProjectUtils;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

public class FilterUtils {

	public static Filter buildFromXML(String id) {
		return buildFromXML(ProjectUtils.getFilterPath() + id, "meta.xml");
	}

	public static Filter buildFromXML(String filterPath, String metaName) {

		Filter filter = new Filter();

		try {
			File filterFile = new File(filterPath, metaName);
			InputStream inputStream = new FileInputStream(filterFile);

			XmlPullParser parser = Xml.newPullParser();

			parser.setInput(inputStream, "UTF-8");
			int eventType = parser.getEventType();
			Uri attachment = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {

				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					//
					break;
				case XmlPullParser.START_TAG:
					if ("filter".equals(parser.getName())) {
						filter.setId(parser.getAttributeValue(null, "ID"));
						filter.setName(parser.getAttributeValue(null, "name"));
						filter.setFilterType(parser.getAttributeValue(null, "filterType"));
						String temp = parser.getAttributeValue(null, "percentage");
						if (temp != null) {
							filter.setPercentage(Integer.valueOf(temp));
						}
					} else if ("attachment".equals(parser.getName())) {
						String fileName = parser.getAttributeValue(null, "file");
						if (fileName != null) {
							attachment = Uri.fromFile(new File(filterPath, fileName));
						}
					}
					break;

				case XmlPullParser.END_TAG:
					if ("filter".equals(parser.getName())) {

					} else if ("attachment".equals(parser.getName())) {
						filter.setAttachment(attachment);
					}
					break;

				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {
			String errorInfo = "parse filter fail:" + e.getMessage() + "@" + filterPath + metaName;
			Log.e("ExcecuteProject", errorInfo);
			throw new IllegalStateException(errorInfo);
		}

		return filter;
	}

	protected static GPUImageFilter genGPUImageFilter(Filter filter) {

		GPUImageFilter gpuImageFilter = null;
		if ("BLANK".equalsIgnoreCase(filter.getFilterType())) {
			return new GPUImageFilter();
		}
		try {
			Context context = ContextHolder.getInstance().getContext();
			// 暂定滤镜类型为枚举
			FilterType type = FilterType.valueOf(filter.getFilterType());

			gpuImageFilter = GPUImageFilterTools.createFilterForType(context, type);

			// 设置混合型滤镜的附加资源
			if (gpuImageFilter instanceof GPUImageTwoInputFilter) {
				if (filter.getAttachImage() != null) {
					((GPUImageTwoInputFilter) gpuImageFilter).setBitmap(filter.getAttachImage());
				}
				if (filter.getAttachment() != null) {
					Bitmap image = BitmapFactory.decodeFile(filter.getAttachment().getPath());
					((GPUImageTwoInputFilter) gpuImageFilter).setBitmap(image);
				}
				if (filter.getAttachId() > 0) {
					Bitmap image = BitmapFactory.decodeResource(context.getResources(), filter.getAttachId());
					((GPUImageTwoInputFilter) gpuImageFilter).setBitmap(image);
				}
			}
		} catch (Exception e) {
			String errorInfo = "create GPUImageFilter fail:" + e.getMessage() + "@" + filter.getId();
			Log.e("ExcecuteProject", errorInfo);
			throw new IllegalStateException(errorInfo);
		}

		return gpuImageFilter;
	}

	public static Filter buildWatermarkFilter() {

		Filter filter = new Filter();
		filter.setId("WatermarkFilter");
		filter.setName("WatermarkFilter");
		filter.setFilterType("BLEND_ALPHA");
		filter.setAttachId(R.drawable.video_watermark_logo);
		filter.setPercentage(0);
		filter.setFadeIn(1000);
		return filter;
	}

	public static Filter buildTittleFilter() {
		Filter filter = new Filter();
		filter.setId("TittleFilter");
		filter.setName("TittleFilter");
		filter.setFilterType("BLEND_ALPHA");
		filter.setPercentage(0);
		filter.setFadeIn(1000);
		filter.setFadeOut(1000);

		return filter;
	}

	public static Filter buildDecorateFilter() {
		Filter filter = new Filter();
		filter.setId("DecorateFilter");
		filter.setName("DecorateFilter");
		filter.setFilterType("BLEND_NORMAL");

		return filter;
	}

	public static Filter buildBlankFilter() {
		Filter filter = new Filter();
		filter.setId("balnkFilter");
		filter.setName("balnkFilter");
		filter.setFilterType("BLANK");
		filter.setDuration(Conf.V_RE_MAX / 1000 * Conf.VIDEO_FRAMERATE);

		return filter;
	}
}
