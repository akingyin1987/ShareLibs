package org.easydarwin.video.beautify.task;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import org.easydarwin.video.beautify.conf.Conf;
import org.easydarwin.video.beautify.model.ProcessTask;
import org.easydarwin.video.beautify.template.VideoProjectUtils;
import org.easydarwin.video.beautify.util.ProjectUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class VideoProcessor implements Runnable {
	private final static String LOG_TAG = "VideoProcessor";
	private volatile int progress = 0;
	private volatile ProcessTask processTask;
	private volatile Uri processedUri;
	private volatile boolean isDone = false;
	private volatile boolean isSynthesis = false; //结束符
	private volatile boolean isPreview = false; //是否预览
	private volatile boolean isSaveFile = false; //保存文件
	private final int FrameRate = 1000/Conf.VIDEO_FRAMERATE; //40ms 播放一帧
	private Thread mVideoProjectThread;
	private static FilterListener filterL;
	private Bitmap bit;
	private Context mContext;

	public void process() {
		mVideoProjectThread = new Thread(this);
		mVideoProjectThread.start();
	}

	public VideoProcessor(ProcessTask processTask,Context ctx) {
		this.processTask = processTask;
		mContext=ctx;
	}

	public int getProgress() {
		return progress;
	}

	@Override
	public void run() {
		new VideoProjectUtils().excute(this,mContext);
	}

	public boolean isDone() {
		return isDone;
	}

	public Uri getProcessedUri() {
		return processedUri;
	}

	public ProcessTask getProcessTask() {
		return processTask;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public boolean isSynthesis() {
		return isSynthesis;
	}

	public void setSynthesis(boolean isSynthesis) {
		this.isSynthesis = isSynthesis;
		if (isSynthesis) {
			if (mVideoProjectThread != null) {
				try {
					mVideoProjectThread.join();

				} catch (InterruptedException in) {
					StringWriter errors = new StringWriter();
					PrintWriter printer = new PrintWriter(errors);
					in.printStackTrace(printer);
					Log.e(LOG_TAG, "mVideoProjectThread.join() " + errors.toString());
					printer = null;
					errors = null;
				}
			}
		}
	}

	public boolean isPreview() {
		return isPreview;
	}

	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}

	public int getFrameRate() {
		return FrameRate;
	}

	public void setProcessTask(ProcessTask processTask) {
		this.processTask = processTask;
	}

	public void setProcessedUri(Uri processedUri) {
		this.processedUri = processedUri;
	}

	public boolean isSaveFile() {
		return isSaveFile;
	}

	public void setSaveFile(boolean isSaveFile) {
		this.isSaveFile = isSaveFile;
	}

	public static FilterListener getFilterL() {
		return filterL;
	}

	public static void setFilterL(FilterListener L) {
		filterL = L;
	}

	public Bitmap getBit() {
		return bit;
	}

	public void setBit(Bitmap bit) {
		this.bit = bit;
	}

	public String getOutPath() {
		String outPath = null;
		if (isSaveFile()) {
			outPath = ProjectUtils.getSaveEffectPath();
		} else {
			outPath = ProjectUtils.getCachePath();
		}
		File outDir = new File(outPath);
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		return outPath;
	}

	public static abstract class FilterListener implements Serializable {
		private static final long serialVersionUID = -6174673391405130886L;

		public abstract void onStart(Bitmap bit);

		public abstract void onFinish(String path);

		public abstract void onCancle();

		public abstract void onProgress(int pro);

		public abstract void onError(int code, String e);
	}

}
