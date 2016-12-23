package org.easydarwin.video.beautify.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

public class LocalMusicLoader {

	private static final String TAG = "LocalMusicLoader";

	private List<LocalMusic> musicList = new ArrayList<LocalMusic>();

	private static LocalMusicLoader musicLoader;

	private static ContentResolver contentResolver;

	private Uri contentUri = Media.EXTERNAL_CONTENT_URI;

	public static LocalMusicLoader instance(ContentResolver pContentResolver) {
		if (musicLoader == null) {
			contentResolver = pContentResolver;
			musicLoader = new LocalMusicLoader();
		}
		return musicLoader;
	}

	private LocalMusicLoader() {
		LocalMusic music = new LocalMusic();
		music.setName("无配乐");
		music.setUrl(null);
		music.setPlayTime(null);
		music.setIntroduction(null);
		musicList.add(music);
		Cursor cursor = contentResolver.query(contentUri, null, null, null, null);
		if (cursor == null) {
			Log.v(TAG, "Line(39	)	Music Loader cursor == null.");
		} else if (!cursor.moveToFirst()) {
			Log.v(TAG, "Line(41	)	Music Loader cursor.moveToFirst() returns false.");
		} else {
			int displayNameCol = cursor.getColumnIndex(MediaColumns.TITLE);
			int albumCol = cursor.getColumnIndex(AudioColumns.ALBUM);
			int urlCol = cursor.getColumnIndex(MediaColumns.DATA);
			int durationCol = cursor.getColumnIndex(AudioColumns.DURATION);
			do {
				String title = cursor.getString(displayNameCol);
				String album = cursor.getString(albumCol);
				int duration = cursor.getInt(durationCol);
				String url = cursor.getString(urlCol);
				if (url.contains(".mp3") && duration > 10000) {
					LocalMusic localMusic = new LocalMusic();
					localMusic.setName(title);
					localMusic.setIntroduction(album);
					localMusic.setUrl(url);
					localMusic.setPlayTime(duration + "");
					musicList.add(localMusic);
				}
			} while (cursor.moveToNext());
		}
	}

	public List<LocalMusic> getMusicList() {
		return musicList;
	}

	public class LocalMusic implements Parcelable {
		private String name; //音乐名称

		private String playTime; //音乐时长

		private int playProTime; //音乐进度

		private String isSelected; //是否选中

		private String isPlay; //是否正在播放

		private String introduction; //音乐简介

		private String url; //音乐路径

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPlayTime() {
			return playTime;
		}

		public void setPlayTime(String playTime) {
			this.playTime = playTime;
		}

		public int getPlayProTime() {
			return playProTime;
		}

		public void setPlayProTime(int playProTime) {
			this.playProTime = playProTime;
		}

		public String getIsSelected() {
			return isSelected;
		}

		public void setIsSelected(String isSelected) {
			this.isSelected = isSelected;
		}

		public String getIsPlay() {
			return isPlay;
		}

		public void setIsPlay(String isPlay) {
			this.isPlay = isPlay;
		}

		public String getIntroduction() {
			return introduction;
		}

		public void setIntroduction(String introduction) {
			this.introduction = introduction;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(name);
			dest.writeString(playTime);
			dest.writeInt(playProTime);
			dest.writeString(isSelected);
			dest.writeString(isPlay);
			dest.writeString(introduction);
			dest.writeString(url);
		}

		public final Creator<LocalMusic> CREATOR = new Creator<LocalMusic>() {

			@Override
			public LocalMusic[] newArray(int size) {
				return new LocalMusic[size];
			}

			@Override
			public LocalMusic createFromParcel(Parcel source) {
				LocalMusic musicInfo = new LocalMusic();
				musicInfo.setName(source.readString());
				musicInfo.setPlayTime(source.readString());
				musicInfo.setPlayProTime(source.readInt());
				musicInfo.setIsSelected(source.readString());
				musicInfo.setIsPlay(source.readString());
				musicInfo.setIntroduction(source.readString());
				musicInfo.setUrl(source.readString());
				return musicInfo;
			}
		};
	}
}
