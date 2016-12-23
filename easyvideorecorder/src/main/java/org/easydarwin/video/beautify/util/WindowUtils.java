/**
 * 
 */
package org.easydarwin.video.beautify.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 */
public class WindowUtils {
	private WindowUtils() {
	}

	/**
	 * 获取屏幕尺寸工具
	 * 
	 * @param mContext
	 *           上下文
	 * @return Point size ex：size.x,size.y
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static Point getScreenSize(Context mContext) {
		Point size = new Point();
		if (Build.VERSION.SDK_INT > 13) {
			WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			display.getSize(size);
		} else if (Build.VERSION.SDK_INT < 13 && Build.VERSION.SDK_INT > 8) {
			WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			size.set(width, height);
		} else if (Build.VERSION.SDK_INT <= 8) {
			DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
			size.set(dm.widthPixels, dm.heightPixels);
		}
		return size;
	}

	/**
	 * 实现全屏
	 * 
	 * @param a
	 */
	@SuppressLint("InlinedApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void fullScreen(Activity a) {
		if (Build.VERSION.SDK_INT >= 11) {
			//			a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		} else if (Build.VERSION.SDK_INT < 11) {
			a.requestWindowFeature(Window.FEATURE_NO_TITLE);
			a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
