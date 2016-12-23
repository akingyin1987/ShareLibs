package org.easydarwin.video.beautify.adapter;

import akingyin.easyvideorecorder.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easydarwin.video.beautify.util.DateUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoBeautifyMusicHistoryAdapter extends BaseAdapter {
	private final Context mContext;
	private List<String> models = new ArrayList<>();

	private int selectIndex = -1; 

	public VideoBeautifyMusicHistoryAdapter(Context mContext, Map<String, ?> model) {
		this.mContext = mContext;
		for (Map.Entry<String, ?> entry : model.entrySet()) {
			models.add((String) entry.getValue());
		}
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

			String model = models.get(position);

			if (model != null) {
				String[] musicHistory = model.split(",");
				holder.name.setText(musicHistory[0]);
				holder.introduction.setText(musicHistory[1]);
				if (selectIndex == position) {
					holder.musicItem.setBackgroundColor(0xffeaeced);
				} else {
					holder.musicItem.setBackgroundColor(0x00ffffff);
				}
				holder.playTime.setText(DateUtils.formateTime(Integer.parseInt(musicHistory[2]), "00:00"));
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
