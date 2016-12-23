package org.easydarwin.video.beautify.activity;

import akingyin.easyvideorecorder.R;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.co.cyberagent.android.gpuimage.GPUImageView;


import org.easydarwin.video.beautify.adapter.VideoBeautifyThemeAdapter;
import org.easydarwin.video.beautify.model.ProcessTask;
import org.easydarwin.video.beautify.model.Type;
import org.easydarwin.video.beautify.model.VideoBeautifyTheme;
import org.easydarwin.video.beautify.task.ProgressDialogTask;
import org.easydarwin.video.beautify.template.AudioEffect;
import org.easydarwin.video.beautify.util.ContextHolder;
import org.easydarwin.video.beautify.util.ProjectUtils;
import org.easydarwin.video.beautify.util.TouchUtil;
import org.easydarwin.video.beautify.util.VideoBeautifyUtil;
import org.easydarwin.video.beautify.util.VideoCommonPara;
import org.easydarwin.video.beautify.util.WindowUtils;
import org.easydarwin.video.beautify.view.HorizontalListView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

public class VideoBeautifyActivity extends Activity implements OnClickListener {
	private final static String TAG = "VideoBeautifyActivity";

	private HorizontalListView mThemeListView;
	private Button mBackBtn;
	private Button mSubmitBtn;
	private Button vPlayBtn;
	private Button vSilencerBtn;
	private Dialog mPromptDialog;
	public GPUImageView mGPUImageView;
	private volatile boolean isMute = false;
	private volatile boolean mIsPreview = true;
	private volatile boolean mIsSaveFile = true;
	private Uri videoUri;
	private Intent intent;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private final static int SCREEN_FULL = 0;
	private final static int SCREEN_DEFAULT = 1;
	private ProgressDialog mProgressDialog;
	private ProgressDialog mDelayNewTaskDialog;
	private AudioManager mAudioManager;
	private ProgressDialogTask mAsyncTask;
	private int mCountOfClickPlayButton = 0;

	private int mark = Type.TABLE_TYPE_FILTER;

	private Button mThemeBtn;
	private Button mFilterBtn;
	private Button mMusicBtn;

	View btnIndexer;
	private HorizontalListView mFilterListView;
	private Animation animationEnter;
	private Animation animationReEnter;
	private int themeclickpostion = 0;
	private int filterclickpostion = 0;
	private int musicclickpostion = 1;
	private int filterviewid = 0;
	private int themeviewid = 0;
	private int musicviewid = 0;
	private int mID = 0;

	public final static int SHOOTING = 1;
	public final static int ALBUM = 2;
	public final static int LOCALVIDEO = 3;
	public final static String Cannel = "CANNELACTIVIT";
	public int cannel = 0;
	private Handler handler = new Handler();
	private PowerManager.WakeLock mWakeLock;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowUtils.fullScreen(this);
		setContentView(R.layout.video_beautify_activity);
		ContextHolder.getInstance().setContext(this);
		ContextHolder.getInstance().setActivity(this);
		initActivity();
		addListeners();
		initAction();
	}

	private void initActivity() {
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mThemeListView = (HorizontalListView) findViewById(R.id.themelistview);
		mBackBtn = (Button) findViewById(R.id.video_beautify_back);
		mSubmitBtn = (Button) findViewById(R.id.video_beautify_submit);
		vPlayBtn = (Button) findViewById(R.id.video_beautify_play);
		vSilencerBtn = (Button) findViewById(R.id.video_beautify_silencer);
		intent = getIntent();
		btnIndexer = findViewById(R.id.btnIndexer);
		mGPUImageView = (GPUImageView) findViewById(R.id.video_real_time_view);
		mThemeBtn = (Button) findViewById(R.id.video_beautify_menu_theme);
		mFilterBtn = (Button) findViewById(R.id.video_beautify_menu_filter);
		mMusicBtn = (Button) findViewById(R.id.video_beautify_menu_music);
		mFilterListView = (HorizontalListView) findViewById(R.id.filterlistview);
	}

	private void addListeners() {
		TouchUtil.createTouchDelegate(mBackBtn, 100);
		mBackBtn.setOnClickListener(this);
		mSubmitBtn.setOnClickListener(this);
		vPlayBtn.setOnClickListener(this);
		vSilencerBtn.setOnClickListener(this);
		mThemeBtn.setOnClickListener(this);
		mFilterBtn.setOnClickListener(this);
		mMusicBtn.setOnClickListener(this);
	}

	public void initAction() {

		if (intent.getStringExtra("path") == null) {
			videoUri = VideoCommonPara.getInstance().getVideoUri();
		} else {
			videoUri = Uri.parse(intent.getStringExtra("path"));
			VideoCommonPara.getInstance().setVideoUri(videoUri);
		}

		if (intent.getIntExtra("CANNELACTIVIT", 0) == 0) {
			cannel = VideoCommonPara.getInstance().getCannelActivity();
		} else {
			cannel = intent.getIntExtra("CANNELACTIVIT", 1);
			VideoCommonPara.getInstance().setCannelActivity(cannel);
		}
		getScreenSize();
		vPlayBtn.setVisibility(View.INVISIBLE);

		animationEnter = AnimationUtils.loadAnimation(this, R.anim.voide_beautify_button_enter);
		animationReEnter = AnimationUtils.loadAnimation(this, R.anim.voide_button_reenter);
		initList();
		showView(Type.TABLE_TYPE_FILTER);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.video_beautify_back) {

			promptContent();

		} else if (v.getId() == R.id.video_beautify_submit) {
			promptProgressDialogAndProcessVideo(false, true);

		} else if (v.getId() == R.id.video_beautify_play) {
			if (mCountOfClickPlayButton == 0) {
				startVideo();
			}
			mCountOfClickPlayButton++;
		} else if (v.getId() == R.id.video_beautify_menu_theme) {
			showView(Type.TABLE_TYPE_THEME);
		} else if (v.getId() == R.id.video_beautify_menu_filter) {
			showView(Type.TABLE_TYPE_FILTER);
		} else if (v.getId() == R.id.video_beautify_menu_music) {
			goNextFrame(LocalMusicActivity.class);
		} else if (v.getId() == R.id.video_beautify_silencer) {
			switchMute();
		}
	}

	public void promptContent() {
		switch (cannel) {
			case SHOOTING:
				promptDialog(R.string.video_beautify_prompt_msg, R.string.video_beautify_msg_when_quite);
				break;
			case ALBUM:
				setResult(RESULT_OK);
				VideoCommonPara.mInstance = null;
				VideoBeautifyActivity.this.finish();
				break;
			case LOCALVIDEO:
				setResult(RESULT_OK);
				VideoCommonPara.mInstance = null;
				VideoBeautifyActivity.this.finish();
				break;
		}
	}

	public void promptDialog(int StringID, int content) {
		final Dialog dialog = new Builder(this).setTitle("是否确认放弃该视频?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mAsyncTask != null) {
					mID = -1;
					mAsyncTask.cancel(false);
				}
				setResult(RESULT_OK);
				VideoCommonPara.mInstance = null;
				dialog.dismiss();
				VideoBeautifyActivity.this.finish();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();

		mPromptDialog = dialog;
		mPromptDialog.setCanceledOnTouchOutside(true);
		mPromptDialog.show();
	}

	private void dismissDialog() {
		if (mPromptDialog != null && mPromptDialog.isShowing()) {
			mPromptDialog.dismiss();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (themeclickpostion > 0 || filterclickpostion > 0) {

			}
			promptContent();

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
		}
		return true;
	}

	private long mTimeOfPreCancel = 0;
	private Handler mDelayNewTaskHandler = new Handler();
	private Runnable mDelayNewTaskRunnable = new Runnable() {
		@Override
		public void run() {
			if (mID < 0) {
				return;
			}
			startNewProcessTask();
			if (null != mDelayNewTaskDialog) {
				mDelayNewTaskDialog.dismiss();
			}
		}
	};

	public void preTaskCancelled() {
		if (mID < 0) {
			return;
		}
		long delayMillis = 0;

		if (mIsPreview) {
			long d = System.currentTimeMillis() - mTimeOfPreCancel;
			if (d > 0) {
				delayMillis = 400 - d;
			}
			if (delayMillis < 0) {
				delayMillis = 0;
			}
		}
		mDelayNewTaskHandler.postDelayed(mDelayNewTaskRunnable, delayMillis);
	}

	private void startNewProcessTask() {
		if (mID < 0) {
			return;
		}

		mGPUImageView.setVisibility(View.VISIBLE);
		if (!mIsPreview) {
			if (mProgressDialog == null) {
				mProgressDialog = new ProgressDialog(VideoBeautifyActivity.this);
			}
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}
		mAsyncTask = new ProgressDialogTask(this, VideoBeautifyActivity.this, mIsPreview, mIsSaveFile);
		ProcessTask processTask = VideoCommonPara.getInstance().getProcessTask();
		processTask.setUri(videoUri);
		processTask.setMute(isMute);
		processTask.setThemeId("" + themeviewid);
		processTask.setFilterId("" + filterviewid);
		if (cannel == ALBUM) {
			if (themeviewid > 0 || musicviewid > 0) {
				processTask.setMute(true);
			}
		}
		mAsyncTask.executeOnExecutor(processExecutorService, processTask);
	}

	ExecutorService processExecutorService = Executors.newCachedThreadPool();

	private void promptProgressDialogAndProcessVideo(boolean isPreview) {
		promptProgressDialogAndProcessVideo(isPreview, false);
	}

	private void promptProgressDialogAndProcessVideo(boolean isPreview, boolean isSaveFile) {
		vPlayBtn.setVisibility(View.INVISIBLE);

		mIsPreview = isPreview;
		mIsSaveFile = isSaveFile;
		mID = mark;
		// mType = type;

		boolean taskCancel = false;
		if (mAsyncTask != null) {
			mTimeOfPreCancel = System.currentTimeMillis();
			taskCancel = mAsyncTask.cancel(false);
		}

		if (taskCancel && mIsPreview) {
			if (mDelayNewTaskDialog == null) {
				mDelayNewTaskDialog = new ProgressDialog(VideoBeautifyActivity.this);
			}
			mDelayNewTaskDialog.setCanceledOnTouchOutside(false);
			mDelayNewTaskDialog.show();
		}

		if (!taskCancel) {
			startNewProcessTask();
		}
	}

	private void hidePlayBtn() {
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
		aa.setDuration(500);
		aa.setFillAfter(false);
		vPlayBtn.startAnimation(aa);
		vPlayBtn.setVisibility(View.GONE);
	}

	private void showPlayBtn() {
		vPlayBtn.setVisibility(View.VISIBLE);
		mCountOfClickPlayButton = 0;
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(500);
		aa.setFillAfter(false);
		vPlayBtn.startAnimation(aa);
	}

	private void switchMute() {
		if (isMute) {
			vSilencerBtn.setBackgroundResource(R.drawable.video_beautify_silencer_normal);
			Toast.makeText(this, R.string.video_beautify_silencer_mute, Toast.LENGTH_SHORT).show();
		} else {
			vSilencerBtn.setBackgroundResource(R.drawable.video_beautify_silencer_selected);
			Toast.makeText(this, R.string.video_beautify_silencer_unmute, Toast.LENGTH_SHORT).show();
		}
		isMute = !isMute;
		AudioEffect.setSrcPlayerMute(isMute);
	}

	private void setVideoScale(int flag) {
		switch (flag) {
			case SCREEN_FULL:
				Log.d(TAG, "screenWidth: " + screenWidth + " screenHeight: " + screenHeight);
				mGPUImageView.setGPUImageScale(screenWidth, screenHeight);
				break;
			case SCREEN_DEFAULT:
				int videoWidth = 480;
				int videoHeight = 480;
				int mWidth = screenWidth;
				int mHeight = screenHeight - 25;
				if (videoWidth > 0 && videoHeight > 0) {
					if (videoWidth * mHeight > mWidth * videoHeight) {
						mHeight = mWidth * videoHeight / videoWidth;
					} else if (videoWidth * mHeight < mWidth * videoHeight) {
						mWidth = mHeight * videoWidth / videoHeight;
					}
				}
				mGPUImageView.setGPUImageScale(mWidth, mHeight);
				break;
		}
	}

	private void startVideo() {
		promptProgressDialogAndProcessVideo(true);
		hidePlayBtn();
	}

	@Override
	public void onRestart() {
		Log.d(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		showView(mark);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		if (mWakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
			mWakeLock.acquire();
		}
		mGPUImageView.onResume();
		setVideoScale(SCREEN_DEFAULT);
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.show();
		} else {
			dismissDialog();
			startVideo();
		}
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		mGPUImageView.onPause();
		if (null != mDelayNewTaskDialog) {
			mDelayNewTaskDialog.dismiss();
		}
		if (mAsyncTask != null && mIsPreview) {
			mID = -1;
			mAsyncTask.cancel(false);
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		if (null != mDelayNewTaskDialog) {
			mDelayNewTaskDialog.dismiss();
		}

		if (mAsyncTask != null && mIsPreview) {
			mID = -1;
			mAsyncTask.cancel(false);
		}
		saveState();
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");

		if (mAsyncTask != null) {
			mID = -1;
			mAsyncTask.cancel(false);
		}
		dismissDialog();
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getScreenSize();// 重新获取屏幕尺寸
	}

	private void getScreenSize() {
		Point point = WindowUtils.getScreenSize(getApplicationContext());
		screenWidth = point.x;
		screenHeight = point.y;
	}

	/**
	 * 显示view mark为标示1为主图2为滤镜
	 * 
	 */
	private void showView(int mark) {
		this.mark = mark;

		View curView = null;

		if (mThemeListView.getVisibility() == View.VISIBLE) {
			curView = mThemeListView;
		} else if (mFilterListView.getVisibility() == View.VISIBLE) {
			curView = mFilterListView;
		}
		View clickView = null;
		SetBottomMenuBg(mark);
		switch (mark) {
			case Type.TABLE_TYPE_THEME:
				clickView = mThemeListView;
				break;
			case Type.TABLE_TYPE_FILTER:
				clickView = mFilterListView;
				break;
			case Type.TABLE_TYPE_DECORATE:
				break;
			case Type.TABLE_TYPE_SUBTITLE:
				break;
			case Type.TABLE_TYPE_DUB:
				break;
		}

		if (clickView == curView) {
			clickView.startAnimation(animationReEnter);
		} else {
			if (curView != null) {
				curView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.video_beautify_button_exit));
				curView.setVisibility(View.GONE);
			}
			clickView.startAnimation(animationEnter);
			clickView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @param
	 */
	private void SetBottomMenuBg(int mark) {
		switch (mark) {
			case Type.TABLE_TYPE_THEME:
				btnIndexer.animate().translationX(mThemeBtn.getX()).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator());
				mThemeBtn.setTextColor(Color.parseColor("#51ff8a"));
				mFilterBtn.setTextColor(Color.parseColor("#ffffff"));
				break;
			case Type.TABLE_TYPE_FILTER:
				btnIndexer.animate().translationX(mFilterBtn.getX()).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator());
				mFilterBtn.setTextColor(Color.parseColor("#51ff8a"));
				mThemeBtn.setTextColor(Color.parseColor("#ffffff"));
				break;
		}
	}

	private void goNextFrame(final Class<?> cls) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {
					Intent intent = new Intent(VideoBeautifyActivity.this, cls);
					intent.putExtra("videoUri", videoUri.getPath());
					startActivity(intent);
				} catch (Throwable e) {
					e.printStackTrace();
					Log.e(TAG, e.getMessage(), e);
				}
			}
		});

	}

	private void initList() {
		{
			List<VideoBeautifyTheme> models = VideoBeautifyUtil.getInstance().parse("theme", ProjectUtils.getThemePath());
			VideoBeautifyThemeAdapter adapter = new VideoBeautifyThemeAdapter(this, models);
			mThemeListView.setAdapter(adapter);
			mThemeListView.setOnItemClickListener(itemClickListener);
		}
		{
			List<VideoBeautifyTheme> models = VideoBeautifyUtil.getInstance().parse("filter", ProjectUtils.getFilterPath());
			VideoBeautifyThemeAdapter adapter = new VideoBeautifyThemeAdapter(this, models);
			mFilterListView.setAdapter(adapter);
			mFilterListView.setOnItemClickListener(itemClickListener);
		}
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		private boolean mClicked = false;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mClicked) {
				return;
			}
			mClicked = true;

			VideoBeautifyThemeAdapter adapter = (VideoBeautifyThemeAdapter) parent.getAdapter();
			adapter.setSelectIndex(position);
			adapter.notifyDataSetChanged();

			VideoBeautifyTheme model = (VideoBeautifyTheme) parent.getItemAtPosition(position);

			int type = Integer.parseInt(model.getType());
			switch (type) {
				case Type.TABLE_TYPE_THEME:
					themeclickpostion = position;
					themeviewid = Integer.parseInt(model.getId());
					break;
				case Type.TABLE_TYPE_FILTER:
					filterclickpostion = position;
					filterviewid = Integer.parseInt(model.getId());
					break;
				case Type.TABLE_TYPE_MUSIC:
					musicclickpostion = position;
					musicviewid = Integer.parseInt(model.getId());
					if (position == 0) {
						goNextFrame(LocalMusicActivity.class);
						mClicked = false;
						return;
					}
					String localMusicUri = null;
					if (musicviewid == 0 || musicviewid == 1) {
						localMusicUri = null;
					} else {
						localMusicUri = ProjectUtils.getMusicPath() + musicviewid + "/music.mp3";
					}
					VideoCommonPara.getInstance().setLocalMusicUri(localMusicUri);
					break;
				default:
					break;
			}

			promptProgressDialogAndProcessVideo(true);

			mClicked = false;
		}
	};

	private void saveState() {
		Properties state = new Properties();
		state.put("mark", String.valueOf(mark));
		state.put("themeindex", String.valueOf(themeclickpostion));
		state.put("filterindex", String.valueOf(filterclickpostion));
		state.put("musicindex", String.valueOf(musicclickpostion));

		state.put("isMute", String.valueOf(isMute));
		if (VideoCommonPara.getInstance().getLocalMusicUri() != null) {
			state.put("localMusicUri", VideoCommonPara.getInstance().getLocalMusicUri());
		}
	}

	public void taskFinished(final Uri result) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		if (mIsSaveFile) {

			if (mAsyncTask != null) {
				mID = -1;
				mAsyncTask.cancel(false);
			}
			setResult(RESULT_OK);
			VideoCommonPara.mInstance = null;
			Intent intent = new Intent("VideoPlayActivity");
			intent.putExtra("path", result.getPath());
			startActivity(intent);

		}
		showPlayBtn();
	}

	public void updateProgress(int progress) {
		if (mIsPreview) {
		} else {
			if (mProgressDialog != null) {
				mProgressDialog.setProgress(progress > 100 ? 100 : progress);
			}
		}
	}
}
