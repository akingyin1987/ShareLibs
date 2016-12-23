package org.easydarwin.video.recoder.base;

import java.io.File;
import java.io.IOException;

import org.easydarwin.video.recoder.conf.Conf;
import org.easydarwin.video.recoder.utils.AndroidUtils;

import android.os.Environment;

public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		super.onCreate();
		initApplication();
	}

	private void initApplication() {
		initFolder();
		AndroidUtils.init(getApplicationContext());
	}

	private void initFolder() {
		String root = Environment.getExternalStorageDirectory().getAbsolutePath();
		File baseDir = new File(root + Conf.BASE_DIR);
		if (!baseDir.exists()) {
			baseDir.mkdir();
		}
		File vsDir = new File(root + Conf.VS_DIR);
		if (!vsDir.exists()) {
			vsDir.mkdir();

		}
		File vsNonmedia = new File(root + Conf.VS_DIR, ".nonmedia");
		if (!vsNonmedia.exists()) {
			try {
				vsNonmedia.createNewFile();
			} catch (IOException e) {
			}
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
	}
}
