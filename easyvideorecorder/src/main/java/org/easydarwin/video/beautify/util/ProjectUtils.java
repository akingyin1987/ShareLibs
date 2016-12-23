package org.easydarwin.video.beautify.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.easydarwin.video.beautify.conf.Conf;
import org.easydarwin.video.recoder.utils.AndroidUtils;

import android.content.Context;
import android.os.Environment;

public class ProjectUtils {

	private static String mainPath = null;
	private static File mResourceFile;

	public static void init(Context context) {
		initFoler(context);
		AndroidUtils.init(context);
		VideoBeautifyUtil.getInstance().init(context);
	}

	public static void initFoler(Context context) {
		File environmentPath = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			mResourceFile = new File(new File(Environment.getExternalStorageDirectory(), Conf.ROOT_FILE_PATH), "resource");
			environmentPath = context.getExternalCacheDir();
		} else {
			environmentPath = context.getFilesDir();
			mResourceFile = new File(new File(environmentPath, "draft"), "resource");
		}

		String root = Environment.getExternalStorageDirectory().getAbsolutePath();
		File baseDir = new File(root + Conf.BASE_DIR);
		if (!baseDir.exists()) {
			baseDir.mkdir();
		}
		File tmpDir = new File(root + Conf.TMP_DIR);
		if (!tmpDir.exists()) {
			tmpDir.mkdir();
		}

		File videoTmpDir = new File(root + Conf.VIDEO_TMP_DIR);
		if (!videoTmpDir.exists()) {
			videoTmpDir.mkdir();
		}

		File videoTmpNonmedia = new File(root + Conf.VIDEO_TMP_DIR, ".nonmedia");
		if (!videoTmpNonmedia.exists()) {
			try {
				videoTmpNonmedia.createNewFile();
			} catch (IOException e) {
			}
		}
		File imgTmpDir = new File(root + Conf.IMG_TMP_DIR);
		if (!imgTmpDir.exists()) {
			imgTmpDir.mkdir();
		}

		File imgTmpNonmedia = new File(root + Conf.IMG_TMP_DIR, ".nonmedia");
		if (!imgTmpNonmedia.exists()) {
			try {
				imgTmpNonmedia.createNewFile();
			} catch (IOException e) {
			}
		}

		File audioTmpDir = new File(root + Conf.AUDIO_TMP_DIR);
		if (!audioTmpDir.exists()) {
			audioTmpDir.mkdir();
		}
		File audioTmpNonmedia = new File(root + Conf.AUDIO_TMP_DIR, ".nonmedia");
		if (!audioTmpNonmedia.exists()) {
			try {
				audioTmpNonmedia.createNewFile();
			} catch (IOException e) {
			}
		}

	}

	public static String getAudioTemplateResourcePath() {
		File file = new File(mResourceFile, "audioTemplate");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.toString();
	}

	public static String getVideoResourcePath() {
		File file = new File(mResourceFile, "video");
		if (!file.exists()) {
			file.mkdirs();
		}
		File nomediaFile = new File(file, ".nomedia");
		if (!nomediaFile.exists()) {
			try {
				nomediaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file.toString();
	}

	public static long getAssetsFileLength(Context context, String assetsPath) throws IOException {
		return context.getAssets().openFd(assetsPath).getLength();
	}

	public static long getSdcardFreeSpace() {
		File esd = Environment.getExternalStorageDirectory();
		return esd.getUsableSpace();
	}

	public static void releaseAssetsFile(Context context, String assetsPath, String localPath) throws IOException {
		InputStream is = new BufferedInputStream(context.getAssets().open(assetsPath));
		OutputStream os = new BufferedOutputStream(new FileOutputStream(localPath));
		int c = 0;
		while ((c = is.read()) != -1) {
			os.write(c);
		}
		is.close();
		os.close();
	}

	public static String getResourcePath() {
		if (mainPath == null) {
			mainPath = ContextHolder.getInstance().getContext().getExternalFilesDir(null).getAbsolutePath() + "/.beautify_resource/";
		}
		File file = new File(mainPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return mainPath;
	}

	public static String getCachePath() {
		String path = ContextHolder.getInstance().getContext().getExternalFilesDir(null).getAbsolutePath() + "/.beautify_cache/";
		File destDir = new File(path);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return path;
	}

	public static String getThemePath() {
		return getResourcePath() + "theme/";
	}

	public static String getFilterPath() {
		return getResourcePath() + "filter/";
	}

	public static String getMusicPath() {
		return getResourcePath() + "music/";
	}

	public static String getSaveEffectPath() {
		String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + Conf.SAVE_EFFECT_PATH;
		File file = new File(fileName);
		if (!file.exists()) {
			file.mkdirs();
		}
		return fileName;
	}
}
