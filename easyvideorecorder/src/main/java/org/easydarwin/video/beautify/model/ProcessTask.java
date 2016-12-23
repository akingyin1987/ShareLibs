/**
 * 
 */
package org.easydarwin.video.beautify.model;

import org.easydarwin.video.beautify.template.AudioClip;
import org.easydarwin.video.beautify.template.Filter;
import org.easydarwin.video.beautify.template.Tittle;

import android.net.Uri;

public class ProcessTask {
	/**
	 * 视频文件地址
	 */
	private Uri uri;
	/**
	 * 是否消音
	 */
	private volatile boolean isMute = false;

	public Uri getUri() {
		return uri;
	}

	public ProcessTask setUri(Uri uri) {
		this.uri = uri;
		return this;
	}

	public boolean isMute() {
		return isMute;
	}

	public ProcessTask setMute(boolean isMute) {
		this.isMute = isMute;
		return this;
	}

	// 主题
	private String themeId;

	// 滤镜
	private String filterId;

	// 装饰
	private Filter decorateFilter = null;

	// 字幕
	private Tittle[] titlles = null;

	// 配音
	private AudioClip[] audioClips = null;

	// 配乐
	private AudioClip music = null;

	public String getThemeId() {
		return themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	public String getFilterId() {
		return filterId;
	}

	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}

	public Filter getDecorateFilter() {
		return decorateFilter;
	}

	public void setDecorateFilter(Filter decorateFilter) {
		this.decorateFilter = decorateFilter;
	}

	public Tittle[] getTitlles() {
		return titlles;
	}

	public void setTitlles(Tittle[] titlles) {
		this.titlles = titlles;
	}

	public AudioClip[] getAudioClips() {
		return audioClips;
	}

	public void setAudioClips(AudioClip[] audioClips) {
		this.audioClips = audioClips;
	}

	public AudioClip getMusic() {
		return music;
	}

	public void setMusic(AudioClip music) {
		this.music = music;
	}

}
