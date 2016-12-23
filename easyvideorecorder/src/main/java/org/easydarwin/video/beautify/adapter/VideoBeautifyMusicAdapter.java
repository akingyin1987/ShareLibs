package org.easydarwin.video.beautify.adapter;

import akingyin.easyvideorecorder.R;
import java.util.List;

import org.easydarwin.video.beautify.model.LocalMusicLoader;
import org.easydarwin.video.beautify.util.DateUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoBeautifyMusicAdapter extends BaseAdapter {

	private final Context mContext;
	private final List<LocalMusicLoader.LocalMusic> models;
	private int selectIndex = 0;

	public VideoBeautifyMusicAdapter(Context mContext, List<LocalMusicLoader.LocalMusic> models) {
		this.mContext = mContext;
		this.models = models;
	}

	public void setSelectIndex(int i) {
		selectIndex = i;
	}

	@Override
	public int getCount() {
		return models != null ? models.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return models != null && models.size() > 0 ? models.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.video_beautify_local_music_list_item, null);
			holder.musicItem = (RelativeLayout) convertView.findViewById(R.id.local_music_list_item);
			holder.name = (TextView) convertView.findViewById(R.id.local_music_name);
			holder.introduction = (TextView) convertView.findViewById(R.id.local_music_introduction);
			holder.playTime = (TextView) convertView.findViewById(R.id.local_music_play_time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (models != null && models.size() > 0) {

			LocalMusicLoader.LocalMusic model = models.get(position);

			if (model != null) {
				holder.name.setText(model.getName());
				if (model.getIntroduction() != null) {
					holder.introduction.setText(model.getIntroduction());
				} else {
					holder.introduction.setText("");
				}
				if (selectIndex == position) {
					holder.musicItem.setBackgroundColor(0xffeaeced);
				} else {
					holder.musicItem.setBackgroundColor(0x00ffffff);

				}
				if (model.getPlayTime() != null) {
					holder.playTime.setText(DateUtils.formateTime(Integer.parseInt(model.getPlayTime()), "00:00"));
				} else {
					holder.playTime.setText("");

				}
			}

		}
		return convertView;
	}

	class ViewHolder {
		TextView name; //歌名
		TextView introduction; //音乐简介
		TextView playTime; //播放时间
		RelativeLayout musicItem; //本地音乐Item
	}
}
