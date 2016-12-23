package org.easydarwin.video.beautify.task;

import org.easydarwin.video.beautify.activity.VideoBeautifyActivity;
import org.easydarwin.video.beautify.model.ProcessTask;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

public class ProgressDialogTask extends AsyncTask<ProcessTask, Integer, Uri> {
	private final static String LOG_TAG = "ProgressDialogTask";
	private VideoBeautifyActivity mVideoBeautifyActivity;
	private boolean mIsPreview;
	private boolean mIsSaveFile;
	private Context mContext;
	public ProgressDialogTask(VideoBeautifyActivity VideoBeautifyActivity,Context ctx, boolean isPreview, boolean isSaveFile) {
		super();
		mVideoBeautifyActivity = VideoBeautifyActivity;
		mIsPreview = isPreview;
		mIsSaveFile = isSaveFile;
		mContext=ctx;
	}

	@Override
	protected Uri doInBackground(ProcessTask... params) {
		Log.v(LOG_TAG, LOG_TAG + ": doInBackground begin");
		VideoProcessor videoProcessor = new VideoProcessor(params[0],mContext);
		videoProcessor.setPreview(mIsPreview);
		videoProcessor.setSaveFile(mIsSaveFile);
		videoProcessor.process();
		int progress = 0;
		while (!videoProcessor.isDone()) {
			if (isCancelled()) {// && videoProcessor.isPreview()// 输出文件不中断
				videoProcessor.setSynthesis(true); // block until vieoproject  thread exit
				break;
			}
			if (!mIsPreview) {
				progress = videoProcessor.getProgress();
				publishProgress(progress);
			}
			SystemClock.sleep(10);///////////////////////////////
		}
		Log.v(LOG_TAG, LOG_TAG + ": doInBackground end");
		return videoProcessor.getProcessedUri();// 返回处理后视频路径
	}

	@Override
	protected void onPostExecute(Uri result) {
		Log.v(LOG_TAG, LOG_TAG + ": onPostExecute begin");
		mVideoBeautifyActivity.taskFinished(result);
		if (!mIsPreview && !mIsSaveFile) {
			SystemClock.sleep(500);
			Log.v(LOG_TAG, LOG_TAG + ": onPostExecute end(convert)");
			return;
		} else if (mIsSaveFile) {
			MediaScannerConnection.scanFile(mVideoBeautifyActivity, new String[] { result.getPath() }, null, null);
		}
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		mVideoBeautifyActivity.updateProgress(values[0]);
	}

	@Override
	protected void onCancelled() {
		mVideoBeautifyActivity.preTaskCancelled();
	}
}
