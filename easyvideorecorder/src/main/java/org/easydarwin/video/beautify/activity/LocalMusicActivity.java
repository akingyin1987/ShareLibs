package org.easydarwin.video.beautify.activity;

import akingyin.easyvideorecorder.R;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easydarwin.video.beautify.adapter.VideoBeautifyMusicAdapter;
import org.easydarwin.video.beautify.adapter.VideoBeautifyMusicHistoryAdapter;
import org.easydarwin.video.beautify.listener.OnMusicViewChangeListener;
import org.easydarwin.video.beautify.model.LocalMusicLoader;
import org.easydarwin.video.beautify.service.LocalMusicService;
import org.easydarwin.video.beautify.service.LocalMusicService.LocalMusicBinder;
import org.easydarwin.video.beautify.store.VideoBeautifySharedPrefs;
import org.easydarwin.video.beautify.util.DateUtils;
import org.easydarwin.video.beautify.util.ProjectUtils;
import org.easydarwin.video.beautify.util.TouchUtil;
import org.easydarwin.video.beautify.util.VideoCommonPara;
import org.easydarwin.video.beautify.view.LocalMusicScrollLayout;
import org.easydarwin.video.render.AudioConverter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class LocalMusicActivity extends Activity implements OnMusicViewChangeListener, OnClickListener {
	private final String TAG = "LocalMusicShowActivity";
	private LocalMusicScrollLayout mScrollLayout;
	private LinearLayout[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	private int currentPosition;
	private List<LocalMusicLoader.LocalMusic> localMusics;
	private Map<String, ?> musicHistoryList = new HashMap<String, String>();
	private boolean isPlay = false; // 是否播放音乐状态
	private boolean isFirst = true; // 是否第一次进入状态
	private static final int updateDialogDismiss = 3;
	private static final int updateDialogShow = 4;
	private TextView mLocalLibrary;
	private TextView mMusicHistory;
	private View mLocalLibraryUnderlined;
	private View mMusicHistoryUnderlined;
	private ListView mLocalLibraryList;
	private ListView mMusicHistoryList;
	private SeekBar seekBar;
	private TextView playProTime;
	private TextView playTimeBottom;
	private ImageButton btnPlay;
	private ImageView addMusic;
	private VideoBeautifyMusicAdapter musicAdapter;
	private VideoBeautifyMusicHistoryAdapter musicHistoryAdapter;
	private RelativeLayout musicPlay;

	private ProgressReceiver progressReceiver;
	private LocalMusicBinder localMusicBinder;
	private ProgressDialog mDelayNewTaskDialog;
	private Button mBackBtn;

	private TextView noMusic, noMusicHistory;

	private int postion = 1;

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			localMusicBinder = (LocalMusicBinder) service;
			if (isFirst && localMusics.size() > 0) {
				musicAdapter.setSelectIndex(postion);
				musicAdapter.notifyDataSetChanged();
				if (localMusics.size() > 1) {
					localMusicBinder.startPlay(localMusics.get(postion), 0);
					seekBarBase = Integer.parseInt(localMusics.get(postion).getPlayTime());//////////////////////////////
					setSeekBarSecondProgress(videoDuration);
					playTimeBottom.setText(DateUtils.formateTime(Integer.parseInt(localMusics.get(postion).getPlayTime()), "00:00"));
					isFirst = false;
					isPlay = true;
				}
			}
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case updateDialogDismiss:
					if (mDelayNewTaskDialog != null) {
						mDelayNewTaskDialog.dismiss();
					}
					finish();
					break;
				case updateDialogShow:
					if (null != mDelayNewTaskDialog) {
						mDelayNewTaskDialog.show();
					}
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_beautify_local_music_activity);
		initActivity();
		getVideoDur();
		addListener();
		initData();
		initReceiver();
	}

	int videoDuration = 0;

	private void getVideoDur() {
		String videoUri = getIntent().getStringExtra("videoUri");
		if (!TextUtils.isEmpty(videoUri) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			try {
				retriever.setDataSource(videoUri);
				videoDuration = (Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					retriever.release();
				} catch (Exception ex) {
				}
			}
		}
	}

	private int seekBarMax = 1000;
	private int seekBarBase = 1000;

	private void setSeekBarProgress(int pro) {
		seekBar.setProgress((1000 * pro / seekBarBase));
	}

	private void setSeekBarSecondProgress(int pro) {
		seekBar.setSecondaryProgress((1000 * pro / seekBarBase));
	}

	private int getSeekBarProgress() {
		return seekBar.getProgress() * seekBarBase / 1000;
	}

	private int getSeekBarProgress(int pro) {
		return pro * seekBarBase / 1000;
	}

	private void initActivity() {
		mLocalLibrary = (TextView) findViewById(R.id.local_library);
		mMusicHistory = (TextView) findViewById(R.id.music_history);
		mLocalLibraryUnderlined = findViewById(R.id.local_library_underlined);
		mMusicHistoryUnderlined = findViewById(R.id.music_history_underlined);
		seekBar = (SeekBar) findViewById(R.id.local_music_playpro);
		seekBar.setMax(seekBarMax);
		/////////////////////////////////////////////////////////////////////
		playProTime = (TextView) findViewById(R.id.local_music_playpro_time);
		playTimeBottom = (TextView) findViewById(R.id.local_music_play_time_bottom);
		btnPlay = (ImageButton) findViewById(R.id.local_music_play);
		addMusic = (ImageButton) findViewById(R.id.local_music_add_music);
		mLocalLibraryList = (ListView) findViewById(R.id.local_music_list);
		mMusicHistoryList = (ListView) findViewById(R.id.local_music_hostory_list);
		musicPlay = (RelativeLayout) findViewById(R.id.local_music_list_item_play);

		noMusic = (TextView) findViewById(R.id.video_beautify_no_music);
		noMusicHistory = (TextView) findViewById(R.id.video_beautify_no_history_music);

		mScrollLayout = (LocalMusicScrollLayout) findViewById(R.id.ScrollLayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.tablelayout);
		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new LinearLayout[mViewCount];
		mBackBtn = (Button) findViewById(R.id.video_beautify_back);
		int m = 0;
		for (int i = 0; i <= mViewCount; i++) {
			if (linearLayout.getChildAt(i) instanceof LinearLayout) {
				mImageViews[m] = (LinearLayout) linearLayout.getChildAt(i);
				mImageViews[m].setEnabled(true);
				mImageViews[m].setOnClickListener(this);
				mImageViews[m].setTag(m);
				m++;
			}
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}

	boolean isNoMusic = false;

	private void addListener() {
		btnPlay.setOnClickListener(this);
		addMusic.setOnClickListener(this);
		TouchUtil.createTouchDelegate(mBackBtn, 300);
		mBackBtn.setOnClickListener(this);
		mLocalLibraryList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				musicAdapter.setSelectIndex(pos);
				if (pos == 0) {
					btnPlay.setImageResource(R.drawable.video_beautify_local_music_play_music_selector);
					localMusicBinder.stopPlay();
					isPlay = false;
					playProTime.setText("00:00");
					playTimeBottom.setText("00:00");
					setSeekBarProgress(0);
					setSeekBarSecondProgress(0);
					isNoMusic = true;
				} else {
					isNoMusic = false;
					localMusicBinder.startPlay(localMusics.get(pos), 0);
					playTimeBottom.setText(DateUtils.formateTime(Integer.parseInt(localMusics.get(pos).getPlayTime()), "00:00"));
					btnPlay.setImageResource(R.drawable.video_beautify_local_music_stop_music_selector);
					final int max = Integer.parseInt(localMusics.get(pos).getPlayTime());
					seekBarBase = max;
					setSeekBarProgress(0);
					setSeekBarSecondProgress(videoDuration);

					playProTime.setText(DateUtils.formateTime(0, "00:00"));
					postion = pos;
					isPlay = true;
				}
				musicAdapter.notifyDataSetChanged();
			}
		});

		mMusicHistoryList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int positions, long id) {
			}
		});
		TouchUtil.createTouchDelegate(seekBar, 200);

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					localMusicBinder.changeProgress(getSeekBarProgress(progress));
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void initData() {
		connectToNatureService();
		mLocalLibrary.setTextColor(this.getResources().getColor(R.color.video_beautify_local_music_table_select_text));
		localMusics = LocalMusicLoader.instance(getContentResolver()).getMusicList();

		if (localMusics.size() == 0) {
			noMusic.setVisibility(View.VISIBLE);
			musicPlay.setVisibility(View.GONE);
		}

		musicAdapter = new VideoBeautifyMusicAdapter(this, localMusics);
		mLocalLibraryList.setAdapter(musicAdapter);
		musicAdapter.notifyDataSetChanged();

		musicHistoryList = VideoBeautifySharedPrefs.getAll(this);
		if (musicHistoryList.size() == 0) {
			noMusicHistory.setVisibility(View.VISIBLE);
		}

		musicHistoryAdapter = new VideoBeautifyMusicHistoryAdapter(this, musicHistoryList);
		mMusicHistoryList.setAdapter(musicHistoryAdapter);
		if (mDelayNewTaskDialog == null) {
			mDelayNewTaskDialog = new ProgressDialog(LocalMusicActivity.this);
		}
		mDelayNewTaskDialog.setCanceledOnTouchOutside(false);
	}

	private void connectToNatureService() {
		Intent intent = new Intent(this, LocalMusicService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.local_music_play) {
			if (localMusics.size() <= 1) {
				return;
			}
			if (!isPlay) {
				btnPlay.setImageResource(R.drawable.video_beautify_local_music_stop_music_selector);
				localMusicBinder.startPlay(localMusics.get(postion), currentPosition);
				isPlay = true;
			} else {
				btnPlay.setImageResource(R.drawable.video_beautify_local_music_play_music_selector);
				localMusicBinder.stopPlay();
				isPlay = false;
			}
		} else if (v.getId() == R.id.video_beautify_back) {
			finish();
		} else if (v.getId() == R.id.local_music_add_music) {
			if (localMusics.size() <= 1) {
				return;
			}
			if (VideoBeautifySharedPrefs.getValue(this, localMusics.get(postion).getUrl(), null) != null) {

			} else {
				String HistoryMusic = localMusics.get(postion).getName() + "," + localMusics.get(postion).getIntroduction() + "," + localMusics
					.get(postion)
					.getPlayTime();
				VideoBeautifySharedPrefs.putValue(this, localMusics.get(postion).getUrl(), HistoryMusic);
			}
			if (mDelayNewTaskDialog == null) {
				mDelayNewTaskDialog = new ProgressDialog(LocalMusicActivity.this);
			}

			mDelayNewTaskDialog.setCanceledOnTouchOutside(false);
			mDelayNewTaskDialog.show();
			applyMusic();
		} else {
			int pos = (Integer) (v.getTag());
			setCurPoint(pos);
			mScrollLayout.snapToScreen(pos);
		}

	}

	@Override
	public void OnViewChange(int view) {
		setCurPoint(view);
	}

	@SuppressWarnings("deprecation")
	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;
		noMusicHistory.setVisibility(View.GONE);
		noMusic.setVisibility(View.GONE);
		if (index == 0) {
			mLocalLibrary.setTextColor(this.getResources().getColor(R.color.video_beautify_local_music_table_select_text));
			mMusicHistory.setTextColor(this.getResources().getColor(R.color.video_beautify_local_music_table_normal_text));
			mLocalLibraryUnderlined.setBackgroundColor(this.getResources().getColor(R.color.video_beautify_local_music_table_underlined_select));
			mMusicHistoryUnderlined.setBackgroundColor(this.getResources().getColor(R.color.video_beautify_local_music_table_underlined_normal));
		} else if (index == 1) {
			mLocalLibrary.setTextColor(this.getResources().getColor(R.color.video_beautify_local_music_table_normal_text));
			mMusicHistory.setTextColor(this.getResources().getColor(R.color.video_beautify_local_music_table_select_text));
			mLocalLibraryUnderlined.setBackgroundColor(this.getResources().getColor(R.color.video_beautify_local_music_table_underlined_normal));
			mMusicHistoryUnderlined.setBackgroundColor(this.getResources().getColor(R.color.video_beautify_local_music_table_underlined_select));
		}
		initData();
	}

	@Override
	public void onResume() {
		Log.v(TAG, "OnResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.v(TAG, "OnPause unregister progress receiver");
		super.onPause();

	}

	@Override
	public void onStop() {
		Log.v(TAG, "OnStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "Destroy");
		super.onDestroy();
		unbindService(serviceConnection);
		unregisterReceiver(progressReceiver);
	}

	private void initReceiver() {
		progressReceiver = new ProgressReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LocalMusicService.ACTION_UPDATE_PROGRESS);
		intentFilter.addAction(LocalMusicService.ACTION_UPDATE_DURATION);
		intentFilter.addAction(LocalMusicService.ACTION_UPDATE_CURRENT_MUSIC);
		registerReceiver(progressReceiver, intentFilter);
	}

	class ProgressReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e("ProgressReceiver ", "action=" + action);
			Log.e("getSeekBarProgress ", "progress=" + getSeekBarProgress());
			if (LocalMusicService.ACTION_UPDATE_PROGRESS.equals(action)) {
				int progress = intent.getIntExtra(LocalMusicService.ACTION_UPDATE_PROGRESS, 0);
				if (progress > 0) {
					currentPosition = getSeekBarProgress();
					playProTime.setText(DateUtils.formateTime(progress, "00:00"));
					if (progress > getSeekBarProgress() + videoDuration) {
						localMusicBinder.startPlay(localMusics.get(postion), getSeekBarProgress());
					}
					//					seekBar.setSecondaryProgress(getSeekBarProgress()+ videoDuration);
					setSeekBarSecondProgress(getSeekBarProgress() + videoDuration);
				}
			} else if (LocalMusicService.ACTION_UPDATE_CURRENT_MUSIC.equals(action)) {
				// currentMusic =
				// intent.getIntExtra(LocalMusicService.ACTION_UPDATE_CURRENT_MUSIC,
				// 0);
			} else if (LocalMusicService.ACTION_UPDATE_DURATION.equals(action)) {
				//				currentMax = intent.getIntExtra(LocalMusicService.ACTION_UPDATE_DURATION, 0);
				//				seekBar.setMax(currentMax);
				//				seekBar.setProgress(0);
				//				seekBar.setSecondaryProgress(videoDuration);
			}
		}
	}

	private void applyMusic() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				handler.sendEmptyMessage(updateDialogShow);
				if (isNoMusic) {
					VideoCommonPara.getInstance().setLocalMusicUri(null);
				} else {
					String sourceFileName = localMusics.get(postion).getUrl();
					File destDir = new File(ProjectUtils.getCachePath());
					if (!destDir.exists()) {
						destDir.mkdirs();
					}
					String outputFileName = ProjectUtils.getCachePath() + System.currentTimeMillis();
					int startTime = currentPosition;
					int endTime = startTime + videoDuration;
					int playtime = Integer.parseInt(localMusics.get(postion).getPlayTime());
					if (endTime > playtime) {
						endTime = playtime;
					}
					AudioConverter.convert(sourceFileName, outputFileName, startTime, endTime);
					VideoCommonPara.getInstance().setLocalMusicUri(outputFileName);
				}
				handler.sendEmptyMessage(updateDialogDismiss);
			}
		}).start();
	}

}
