package org.easydarwin.video.beautify.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;

public class FilterGroup extends Filter {

	private List<Filter> filters = null;
	private List<Filter> tempFilters = null;

	public FilterGroup() {
		super();
		filters = new ArrayList<Filter>();
		tempFilters = new ArrayList<Filter>();
	}

	public FilterGroup(List<Filter> filters) {
		this.filters = filters;
		tempFilters = new ArrayList<Filter>();
	}

	public void addFilter(Filter filter) {
		addFilter(filter, -1);
	}

	public void addFilter(Filter filter, int index) {
		if (index < 0) {
			filters.add(filter);
		} else {
			filters.add(index, filter);
		}
		if (getEffect() == null) {
			setEffect(new FilterEffect());
		}
		getEffect().addEffect(filter.getGpuImageFilter(), index);
	}

	public Bitmap applyEffect(Bitmap curFrame, boolean isPreview) {
		if (getEffect() == null) {
			throw new IllegalStateException("filter:filterEffect should not be null!");
		}

		tempFilters.clear();
		Filter filter = null;
		for (Iterator<Filter> iterator = filters.iterator(); iterator.hasNext();) {
			filter = iterator.next();
			// 根据入点、出点过滤当前需要执行的滤镜
			if (getFrameIndex() >= filter.getOffset() && getFrameIndex() < filter.getOutPoint()) {
				//getEffect().addEffect(filter.getGpuImageFilter(), tempFilters.size());
				tempFilters.add(filter);
			} else {
				//etEffect().removeEffect(filter.getGpuImageFilter());
			}
		}

		for (Filter filter2 : tempFilters) {
			filter2.applyDynamicParam();
		}

		curFrame = getEffect().applyEffect(curFrame, isPreview);

		return curFrame;
	}

	@Override
	public void setFrameRate(int frameRate) {
		super.setFrameRate(frameRate);
		for (Filter filter : filters) {
			filter.setFrameRate(frameRate);
		}
	}

	@Override
	public void setFrameIndex(long frameIndex) {
		super.setFrameIndex(frameIndex);
		for (Filter filter : filters) {
			filter.setFrameIndex(frameIndex);
		}
	}

	@Override
	public void close() {
		for (Filter filter : filters) {
			filter.close();
		}
		getEffect().clear();
	}

	@Override
	public int getFilterSize() {
		return filters == null ? 0 : filters.size();
	}

	public List<Filter> getFilters() {
		return filters;
	}

}
