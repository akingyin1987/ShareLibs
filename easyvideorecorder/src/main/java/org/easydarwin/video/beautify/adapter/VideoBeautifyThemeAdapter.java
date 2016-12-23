package org.easydarwin.video.beautify.adapter;

import akingyin.easyvideorecorder.R;
import java.util.List;

import org.easydarwin.video.beautify.model.VideoBeautifyTheme;
import org.easydarwin.video.beautify.view.CustomImageView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VideoBeautifyThemeAdapter extends BaseAdapter {

	private final Context mContext;
	private final List<VideoBeautifyTheme> models;
	private int selectIndex = -1;

	public void setSelectIndex(int i) {
		selectIndex = i;
	}

	public VideoBeautifyThemeAdapter(Context mContext, List<VideoBeautifyTheme> models) {
		this.mContext = mContext;
		this.models = models;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.video_beautify_secected_item, null);
			holder.imageView = (CustomImageView) convertView.findViewById(R.id.secected_item_image);
			holder.textView = (TextView) convertView.findViewById(R.id.secected_item_text);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 绑定数据
		if (models != null && models.size() > 0) {

			VideoBeautifyTheme model = models.get(position);
			if (model != null) {
				holder.textView.setText(model.getName());
				// 新加图片的显示
				if (model.getImage() == null) {
					holder.imageView.setImageResource(R.drawable.video_beautify_no_theme_filter);
				} else {
					holder.imageView.setImageBitmap(model.getImage());
				}
			}
			// 新加选中的效果
			if (position == selectIndex) {
				convertView.setSelected(true);
			} else {
				convertView.setSelected(false);
			}
		}
		return convertView;
	}

	class ViewHolder {
		private CustomImageView imageView;
		private TextView textView;
	}
}
