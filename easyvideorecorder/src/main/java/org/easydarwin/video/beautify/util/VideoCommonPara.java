package org.easydarwin.video.beautify.util;

import org.easydarwin.video.beautify.conf.Conf;
import org.easydarwin.video.beautify.model.ProcessTask;
import org.easydarwin.video.beautify.template.AudioClip;
import org.easydarwin.video.beautify.template.Filter;
import org.easydarwin.video.beautify.template.FilterUtils;
import org.easydarwin.video.beautify.template.MediaMgr;
import org.easydarwin.video.beautify.template.Tittle;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

public class VideoCommonPara {

	private Uri videoUri; // 视频地址
	private int CannelActivity = 1; // 渠道Id
	private boolean isdubAudio; // 是否开启了录音效果
	private String dubAudioUri; // 录音地址
	private int dubAudioTime; // 录音开始时间 ,以毫秒为单位
	private int dubAudioTimeLength; // 录音时长
	private int isLocalMusic; // 是否开启了本地音乐
	private String localMusicUri; // 本地音乐地址
	private int localMusicBinTime; // 本地音乐开始时间
	private int localMusicTimeLength;// 本地音乐时长
	private Bitmap mBitmap; // 装饰bitmap
	private int subtitlePosition = 0;// 字幕游标位置
	private int subtitleBegin = 0; // 字幕开始阶段
	private int subitileEnd = 0; // 字幕结束阶段
	private int subtitleShadow = 0;// 字幕时长
	private Bitmap subitileBitmap; // 字幕图片
	public static VideoCommonPara mInstance;

	public static VideoCommonPara getInstance() {
		if (mInstance == null) {
			mInstance = new VideoCommonPara();
		}
		return mInstance;
	}

	public Uri getVideoUri() {
		return videoUri;
	}

	public void setVideoUri(Uri videoUri) {
		this.videoUri = videoUri;
	}

	public int getCannelActivity() {
		return CannelActivity;
	}

	public void setCannelActivity(int cannelActivity) {
		CannelActivity = cannelActivity;
	}

	public String getDubAudioUri() {
		return dubAudioUri;
	}

	public void setDubAudioUri(String dubAudioUri) {
		this.dubAudioUri = dubAudioUri;
		audioClips = null;
	}

	public int getDubAudioTime() {
		return dubAudioTime;
	}

	public void setDubAudioTime(int dubAudioTime) {
		this.dubAudioTime = dubAudioTime;
	}

	public int getDubAudioTimeLength() {
		return dubAudioTimeLength;
	}

	public void setDubAudioTimeLength(int dubAudioTimeLength) {
		this.dubAudioTimeLength = dubAudioTimeLength;
	}

	public boolean isIsdubAudio() {
		return isdubAudio;
	}

	public int getSubtitleBegin() {
		return subtitleBegin;
	}

	public void setSubtitleBegin(int subtitleBegin) {
		this.subtitleBegin = subtitleBegin;
	}

	public int getSubitileEnd() {
		return subitileEnd;
	}

	public void setSubitileEnd(int subitileEnd) {
		this.subitileEnd = subitileEnd;
	}

	public Bitmap getSubitileBitmap() {
		return subitileBitmap;
	}

	public void setSubitileBitmap(Bitmap subitileBitmap) {
		this.subitileBitmap = subitileBitmap;
		titlles = null;
	}

	public void setIsdubAudio(boolean isdubAudio) {
		this.isdubAudio = isdubAudio;
	}

	public int getIsLocalMusic() {
		return isLocalMusic;
	}

	public void setIsLocalMusic(int isLocalMusic) {
		this.isLocalMusic = isLocalMusic;
	}

	public String getLocalMusicUri() {
		return localMusicUri;
	}

	public void setLocalMusicUri(String localMusicUri) {
		this.localMusicUri = localMusicUri;
		music = null;
	}

	public int getLocalMusicBinTime() {
		return localMusicBinTime;
	}

	public void setLocalMusicBinTime(int localMusicBinTime) {
		this.localMusicBinTime = localMusicBinTime;
	}

	public int getLocalMusicTimeLength() {
		return localMusicTimeLength;
	}

	public void setLocalMusicTimeLength(int localMusicTimeLength) {
		this.localMusicTimeLength = localMusicTimeLength;
	}

	public Bitmap getmBitmap() {
		return mBitmap;
	}

	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
		decorateFilter = null;
	}

	public String getDeviceId() {
		return ((TelephonyManager) ContextHolder.getInstance().getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	private ProcessTask processTask = new ProcessTask();
	// 装饰
	private Filter decorateFilter = null;
	// 字幕
	private Tittle[] titlles = null;
	// 配音
	private AudioClip[] audioClips = null;
	// 配乐
	private AudioClip music = null;

	public ProcessTask getProcessTask() {

		processTask.setUri(videoUri);

		if (mBitmap != null) {
			decorateFilter = FilterUtils.buildDecorateFilter();
			decorateFilter.setAttachImage(mBitmap);
			decorateFilter.setOffset(0);
			decorateFilter.setDuration(Conf.V_RE_MAX / 1000 * Conf.VIDEO_FRAMERATE);
		}
		processTask.setDecorateFilter(decorateFilter);

		if (subitileBitmap != null && subitileEnd > 40) {
			titlles = new Tittle[1];
			titlles[0] = MediaMgr.createTittle(subitileBitmap, subtitleBegin / 40, subitileEnd / 40);
		}
		processTask.setTitlles(titlles);

		if (audioClips == null && dubAudioUri != null) {
			audioClips = new AudioClip[1];
			audioClips[0] = MediaMgr.createAudio(Uri.parse(dubAudioUri), dubAudioTime / 40, dubAudioTimeLength / 40);
		}
		Log.i("ExcecuteProject", "add audio:" + dubAudioUri);
		processTask.setAudioClips(audioClips);

		if (music == null && localMusicUri != null) {
			music = MediaMgr.createAudio(Uri.parse(localMusicUri), 0, Conf.V_RE_MAX / 1000 * Conf.VIDEO_FRAMERATE);
		}
		processTask.setMusic(music);

		return processTask;
	}

	public int getSubtitlePosition() {
		return subtitlePosition;
	}

	public void setSubtitlePosition(int subtitlePosition) {
		this.subtitlePosition = subtitlePosition;
	}

	public int getSubtitleShadow() {
		return subtitleShadow;
	}

	public void setSubtitleShadow(int subtitleShadow) {
		this.subtitleShadow = subtitleShadow;
	}

}
