package org.easydarwin.video;

import org.easydarwin.video.beautify.util.ProjectUtils;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ProjectUtils.init(getApplicationContext());
		initImageLoader(getApplicationContext());
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.diskCacheFileNameGenerator(new Md5FileNameGenerator())
			.diskCacheSize(50 * 1024 * 1024)
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs()
			.build();
		ImageLoader.getInstance().init(config);

	}
}
