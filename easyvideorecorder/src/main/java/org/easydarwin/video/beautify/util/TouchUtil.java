package org.easydarwin.video.beautify.util;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

/**
 * 
 * @description 扩大按钮点击范围Util
 */
public class TouchUtil {
	public static void createTouchDelegate(final View view, final int expandTop, final int expandBottom,
			final int expandLeft, final int expandRight) {
		final View parent = (View) view.getParent();
		parent.post(new Runnable() {
			@Override
			public void run() {
				final Rect r = new Rect();
				view.getHitRect(r);
				r.top -= expandTop;
				r.bottom += expandBottom;
				r.left -= expandLeft;
				r.right += expandRight;
				parent.setTouchDelegate(new TouchDelegate(r, view));
			}
		});
	}

	public static void createTouchDelegate(final View view, final int expandTouchWidth) {
		final View parent = (View) view.getParent();
		parent.post(new Runnable() {
			@Override
			public void run() {
				final Rect r = new Rect();
				view.getHitRect(r);
				r.top -= expandTouchWidth;
				r.bottom += expandTouchWidth;
				r.left -= expandTouchWidth;
				r.right += expandTouchWidth;
				parent.setTouchDelegate(new TouchDelegate(r, view));
			}
		});
	}
}
