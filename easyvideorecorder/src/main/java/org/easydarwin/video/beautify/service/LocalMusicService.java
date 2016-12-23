package org.easydarwin.video.beautify.service;

import org.easydarwin.video.beautify.model.LocalMusicLoader;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class LocalMusicService extends Service {

	private static final String TAG = "org.easydarwin.service.LocalMusicService";
	public static final String ACTION_UPDATE_PROGRESS = "org.easydarwin.service.UPDATE_PROGRESS";
	public static final String ACTION_UPDATE_DURATION = "org.easydarwin.service.UPDATE_DURATION";
	public static final String ACTION_UPDATE_CURRENT_MUSIC = "org.easydarwin.service.UPDATE_CURRENT_MUSIC";
	private MediaPlayer mediaPlayer;
	private boolean isPlaying = false;
	private Binder localMusicBinder = new LocalMusicBinder();

	private LocalMusicLoader.LocalMusic mLocalMusic;
	private int currentPosition;

	private static final int updateProgress = 1;
	private static final int updateCurrentMusic = 2;
	private static final int updateDuration = 3;
	private static final int updatePlayState = 4;

	private int currentMode = 1; //默认播放顺序

	public static final int MODE_ONE_LOOP = 0;
	public static final int MODE_ALL_LOOP = 1;
	public static final int MODE_RANDOM = 2;
	public static final int MODE_SEQUENCE = 3;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case updateProgress:
				toUpdateProgress();
				break;
			case updateDuration:
				toUpdateDuration();
				break;
			case updateCurrentMusic:
				toUpdateCurrentMusic();
				break;
			case updatePlayState:
				toUpdatePlayState();
				break;
			}
		}
	};

	private void toUpdateProgress() {
		if (mediaPlayer != null && isPlaying) {
			int progress = mediaPlayer.getCurrentPosition();
			Intent intent = new Intent();
			intent.setAction(ACTION_UPDATE_PROGRESS);
			intent.putExtra(ACTION_UPDATE_PROGRESS, progress);
			sendBroadcast(intent);
			handler.sendEmptyMessageDelayed(updateProgress, 1000);
		}
	}

	private void toUpdateDuration() {
		if (mediaPlayer != null) {
			int duration = mediaPlayer.getDuration();
			Intent intent = new Intent();
			intent.setAction(ACTION_UPDATE_DURATION);
			intent.putExtra(ACTION_UPDATE_DURATION, duration);
			sendBroadcast(intent);
		}
	}

	private void toUpdateCurrentMusic() {
		Intent intent = new Intent();
		intent.setAction(ACTION_UPDATE_CURRENT_MUSIC);
		intent.putExtra(ACTION_UPDATE_CURRENT_MUSIC, mLocalMusic);
		sendBroadcast(intent);
	}

	private void toUpdatePlayState() {
		Intent intent = new Intent();
		intent.setAction(ACTION_UPDATE_PROGRESS);
		intent.putExtra(ACTION_UPDATE_PROGRESS, "");
		sendBroadcast(intent);
	}

	@Override
	public void onCreate() {
		initMediaPlayer();
		Log.v(TAG, "OnCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * initialize the MediaPlayer
	 */
	private void initMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mediaPlayer.start();
				mediaPlayer.seekTo(currentPosition);
				handler.sendEmptyMessage(updateDuration);
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (isPlaying) {
					switch (currentMode) {
					case MODE_ONE_LOOP:
						mediaPlayer.start();
						break;
					case MODE_ALL_LOOP:
						play(mLocalMusic, 0);

						break;
					case MODE_RANDOM:

						break;
					case MODE_SEQUENCE:

						break;
					default:

						break;
					}
				}
			}
		});
	}

	private void setCurrentMusic(LocalMusicLoader.LocalMusic localMusic) {
		mLocalMusic = localMusic;
		handler.sendEmptyMessage(updateCurrentMusic);
	}

	private void play(LocalMusicLoader.LocalMusic localMusic, int currentPosition) {
		this.currentPosition = currentPosition;
		try {
		setCurrentMusic(localMusic);
		mediaPlayer.reset();
			mediaPlayer.setDataSource(localMusic.getUrl());
			mediaPlayer.prepareAsync();
			handler.sendEmptyMessage(updateProgress);
			isPlaying = true;
		} catch (Exception e) {
			e.printStackTrace();
			isPlaying = false;
		}
	}

	private void stop() {
		mediaPlayer.stop();
		handler.sendEmptyMessage(updatePlayState);
		isPlaying = false;
	}

	//播放规则
	private void playNext() {
		switch (currentMode) {
		case MODE_ONE_LOOP:
			play(mLocalMusic, currentPosition);
			break;
		case MODE_ALL_LOOP:

			break;
		case MODE_SEQUENCE:

			break;
		case MODE_RANDOM:
			break;
		}
	}

	private void playPrevious() {
		switch (currentMode) {
		case MODE_ONE_LOOP:
			play(mLocalMusic, 0);
			break;
		case MODE_ALL_LOOP:

			break;
		case MODE_SEQUENCE:

			break;
		case MODE_RANDOM:

			break;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return localMusicBinder;
	}

	public class LocalMusicBinder extends Binder {

		public void startPlay(LocalMusicLoader.LocalMusic mLocalMusic, int currentPosition) {
			play(mLocalMusic, currentPosition);
		}

		public void stopPlay() {
			stop();
		}

		public void toNext() {
			playNext();
		}

		public void toPrevious() {
			playPrevious();
		}

		/**
		 * MODE_ONE_LOOP = 1;
		 * MODE_ALL_LOOP = 2;
		 * MODE_RANDOM = 3;
		 * MODE_SEQUENCE = 4;
		 */
		public void changeMode() {
			currentMode = (currentMode + 1) % 4;
		}

		/**
		 * return the current mode
		 * MODE_ONE_LOOP = 1;
		 * MODE_ALL_LOOP = 2;
		 * MODE_RANDOM = 3;
		 * MODE_SEQUENCE = 4;
		 * 
		 * @return
		 */
		public int getCurrentMode() {
			return currentMode;
		}

		public boolean isPlaying() {
			return isPlaying;
		}

		public void notifyActivity() {
			toUpdateCurrentMusic();
			toUpdateDuration();
		}

		public void changeProgress(int progress) {
			if (mediaPlayer != null) {
				currentPosition = progress;
				if (isPlaying) {
					mediaPlayer.seekTo(currentPosition);
				} else {
					play(mLocalMusic, currentPosition);
				}
			}
		}
	}

}
