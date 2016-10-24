package com.zlcdgroup.libs.ocr.adapter;



import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseTemplateAdapter<T> extends BaseAdapter {
	protected Activity activity;

	public BaseTemplateAdapter(Activity contextActivity) {
		this.activity = contextActivity;
	}

	public BaseTemplateAdapter() {
	}

	private final List<T> mList = new LinkedList<T>();

	public List<T> getList() {
		return mList;
	}

	public void appendToList(List<T> list) {
		if (list == null) {
			return;
		}
		mList.addAll(list);
		notifyDataSetChanged();
	}

	public void appendToTopList(List<T> list) {
		if (list == null) {
			return;
		}
		mList.addAll(0, list);
		notifyDataSetChanged();
	}

	public void appendToTop(T t) {
		if (t == null) {
			return;
		}
		mList.add(0, t);
		notifyDataSetChanged();
	}

	public void appendToBottom(T t) {
		if (t == null) {
			return;
		}
		mList.add(t);
		notifyDataSetChanged();
	}

	public void remvoe(T t) {
		if (t == null) {
			return;
		}
		mList.remove(t);
		notifyDataSetChanged();
	}

	public void clear() {
		mList.clear();
		//notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public T getItem(int position) {
		if (position > mList.size() - 1) {
			return null;
		}
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == getCount() - 1) {
			onReachBottom();
		}
		return getExView(position, convertView, parent);
	}

	protected abstract View getExView(int position, View convertView,
			ViewGroup parent);

	protected abstract void onReachBottom();

}
