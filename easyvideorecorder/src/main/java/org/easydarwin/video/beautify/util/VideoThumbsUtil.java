package org.easydarwin.video.beautify.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.easydarwin.video.beautify.model.GalleryModel;
import org.easydarwin.video.beautify.template.VideoAsset;
import org.easydarwin.video.beautify.view.HorizontalListView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

public class VideoThumbsUtil {

	private static class LoadVideoThumbsTask extends AsyncTask<Uri, Void, List<GalleryModel>> {
		private final List<GalleryModel> mmodels;
		private final HorizontalListView mGallery;

		public LoadVideoThumbsTask(final List<GalleryModel> models, HorizontalListView gallery) {
			mmodels = models;
			mGallery = gallery;
		}

		@Override
		protected List<GalleryModel> doInBackground(Uri... params) {
			mmodels.addAll(getVideoThumbs(params[0]));
			return mmodels;
		}

		@Override
		protected void onPostExecute(List<GalleryModel> result) {
			mGallery.requestLayout();
		}
	}

	/**
	 * 异步加载缩略图
	 * 
	 * @param models
	 * @param videoUri
	 * @param gallery
	 */
	public static void loadVideoThumbs(final List<GalleryModel> models, final Uri videoUri,
			final HorizontalListView gallery) {
		new LoadVideoThumbsTask(models, gallery).execute(videoUri);
	}

	/**
	 * 获取视频的缩略图列表（缓存到cache）
	 * 
	 * @param videoUri
	 * @return
	 */
	public static List<GalleryModel> getVideoThumbs(Uri videoUri) {
		return getVideoThumbs(videoUri, 0, new File(ProjectUtils.getCachePath()));
	}

	public static List<GalleryModel> getVideoThumbs(Uri videoUri, int step, File destDir) {

		List<GalleryModel> result = new ArrayList<GalleryModel>();

		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		// 截取视频文件名
		String fileName = videoUri.getPath();
		fileName = fileName.substring(fileName.lastIndexOf(File.separatorChar) + 1);
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));

		fileName = destDir.getAbsolutePath() + File.separator + fileName;

		if (checkFileCached(fileName, step, result)) {
			return result;
		}

		VideoAsset videoAsset = new VideoAsset();
		videoAsset.setUri(videoUri);
		videoAsset.startDecode();

		Bitmap frame = null;
		int count = 0;

		long duration = videoAsset.getDuration();
		if (step == 0) {
			// 固定返回11张图
			step = (int) duration / 10;
		}

		File file = null;
		for (int i = 0; i < duration; i++) {
			frame = videoAsset.getNextFrame();
			if (frame == null) {
				break;
			}

			if (i == 0 || count == step || i == duration - 4) {
				file = new File(fileName + i);
				saveFrame(frame, file);
				result.add(new GalleryModel(Uri.fromFile(file), i));
				count = 0;
			}
			count++;
		}

		videoAsset.closeDecode();
		videoAsset = null;
		return result;
	}

	private static void saveFrame(Bitmap frame, File file) {

		try {
			FileOutputStream out = new FileOutputStream(file);
			/* 压缩 储存 */
			if (frame.compress(Bitmap.CompressFormat.JPEG, 20, out)) {
				//
				out.flush();
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean checkFileCached(String fileName, int step, List<GalleryModel> result) {
		if (step == 0) {
			step = 1;
		}
		boolean isCached = false;
		int index = 0;
		File cachedFile = null;
		while (true) {
			cachedFile = new File(fileName + index);
			if (!cachedFile.exists()) {
				if (index == 0 || index > 250) {
					break;
				} else {
					index += 1;
					continue;
				}
			}
			isCached = true;
			result.add(new GalleryModel(Uri.fromFile(cachedFile), index));
			index += step;
		}

		return isCached;
	}

}
