package org.easydarwin.video.beautify.listener;

import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class ViewOnTouchListener implements View.OnTouchListener {
	Point pushPoint;
	int lastImgLeft;
	int lastImgTop;
	FrameLayout.LayoutParams viewLP;
	FrameLayout.LayoutParams pushBtnLP;
	int lastPushBtnLeft;
	int lastPushBtnTop;
	private View mPushView;

	public ViewOnTouchListener(View mPushView) {
		this.mPushView = mPushView;

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				if (null == viewLP) {
					viewLP = (FrameLayout.LayoutParams) view.getLayoutParams();
				}
				if (null == pushBtnLP) {
					pushBtnLP = (FrameLayout.LayoutParams) mPushView.getLayoutParams();
				}
				pushPoint = getRawPoint(event);
				lastImgLeft = viewLP.leftMargin;
				lastImgTop = viewLP.topMargin;
				lastPushBtnLeft = pushBtnLP.leftMargin;
				lastPushBtnTop = pushBtnLP.topMargin;
				break;
			case MotionEvent.ACTION_MOVE:
				Point newPoint = getRawPoint(event);
				float moveX = newPoint.x - pushPoint.x;
				float moveY = newPoint.y - pushPoint.y;

				viewLP.leftMargin = (int) (lastImgLeft + moveX);
				viewLP.topMargin = (int) (lastImgTop + moveY);
				view.setLayoutParams(viewLP);

				pushBtnLP.leftMargin = (int) (lastPushBtnLeft + moveX);
				pushBtnLP.topMargin = (int) (lastPushBtnTop + moveY);
				mPushView.setLayoutParams(pushBtnLP);

				break;

		}
		return false;
	}

	private Point getRawPoint(MotionEvent event) {
		return new Point((int) event.getRawX(), (int) event.getRawY());
	}

	public class Point {
		public float x;
		public float y;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "x: " + x + ",y: " + y;
		}
	}
}
