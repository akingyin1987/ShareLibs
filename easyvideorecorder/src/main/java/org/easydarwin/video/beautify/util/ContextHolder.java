package org.easydarwin.video.beautify.util;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;

public class ContextHolder {
	private static ContextHolder mInstance;
	private Context mContext;
	private Activity mActivity;

	public static ContextHolder getInstance() {
		if (mInstance == null) {
			mInstance = new ContextHolder();
		}
		return mInstance;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	public Activity getActivity() {
		return mActivity;
	}

	public void setActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}

	public WeakReference<Activity> getEffectActivity() {
		return new WeakReference<Activity>(mActivity);
	}
}
