package com.zlcdgroup.tuyalib;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import java.util.List;

public class Mosaic extends Shape {
	
	
	Paint  paint2 = new  Paint();
	
	

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (end.x == 0 && end.y == 0) {
			return;
		}
		// paint.setStyle(Style.STROKE);
		// canvas.drawLine(start.x, start.y, end.x, end.y, paint);
		if (targetRect != null && mosaicImg != null && srcMosaic != null) {
			canvas.drawBitmap(mosaicImg, srcMosaic, targetRect, paint);
			// canvas.drawBitmap(mosaicImg, 0, 0,paint);
		} else {
			// if (targetRect != null) {
			// Paint temp = new Paint();
			// temp.setStyle(Style.STROKE);
			// temp.setColor(Color.GREEN);
			// temp.setStrokeWidth(1);
			// canvas.drawRect(targetRect, temp);
			// }

		}
		if(getTuyaAction() == TuyaAction.MosaicMove){
			paint2.setStyle(Style.STROKE);
			paint2.setColor(Color.GREEN);
			canvas.drawRect(start.x, start.y, end.x, end.y, paint2);
		}
		
	}

	public void setStart(int x, int y) {
		start.x = x;
		start.y = y;
	}

	public void setEnd(int x, int y) {
		end.x = x;
		end.y = y;
		calculate();
	}

	private Bitmap mosaicImg = null;
	Rect targetRect;
	Rect srcMosaic;

	public void clip(Bitmap dis) {
		try {
			mosaicImg = makeMosaic(dis, targetRect, 24);
		} catch (Exception e) {
		}
	}

	public static final int min_mosaic_block_size = 4;

	public static Bitmap makeMosaic(Bitmap bitmap, Rect targetRect,
			int blockSize) throws OutOfMemoryError {
		if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0
				|| bitmap.isRecycled()) {
			throw new RuntimeException("bad bitmap to add mosaic");
		}
		if (blockSize < min_mosaic_block_size) {
			blockSize = min_mosaic_block_size;
		}
		// if (targetRect == null) {
		// targetRect = new Rect();
		// }
		// int bw = bitmap.getWidth();
		// int bh = bitmap.getHeight();
		// if (targetRect.isEmpty()) {
		// targetRect.set(0, 0, bw, bh);
		// }
		//
		int rectW = targetRect.width();
		int rectH = targetRect.height()-30;
		int[] bitmapPxs = new int[rectW * rectH];
		// fetch bitmap pxs
		bitmap.getPixels(bitmapPxs, 0, rectW, targetRect.left, targetRect.top,
				rectW, rectH);
		//
		int rowCount = (int) Math.ceil((float) rectH / blockSize);
		int columnCount = (int) Math.ceil((float) rectW / blockSize);
		int maxX = rectW;
		int maxY = rectH;
		for (int r = 0; r < rowCount; r++) { // row loop
			for (int c = 0; c < columnCount; c++) {// column loop
				int startX = c * blockSize + 1;
				int startY = r * blockSize + 1;
				dimBlock(bitmapPxs, startX, startY, blockSize, maxX, maxY);
			}
		}
		// return Bitmap.createBitmap(bitmapPxs, rectW, rectH,
		// Config.ARGB_8888);
		Bitmap mask = Bitmap.createBitmap(bitmapPxs, rectW, rectH,
				Config.ARGB_8888);
		Bitmap buffer = Bitmap.createBitmap(rectW, rectH, Config.ARGB_8888);
		buffer.eraseColor(Color.RED);
		Canvas canvas = new Canvas(buffer);
		canvas.drawBitmap(mask, 0, 0, null);
		return buffer;
	}

	private static void dimBlock(int[] pxs, int startX, int startY,
			int blockSize, int width, int height) {
		int stopX = startX + blockSize;
		int stopY = startY + blockSize;
		int maxX = width - 1;
		int maxY = height - 1;
		if (stopX > maxX) {
			stopX = maxX;
		}
		if (stopY > maxY) {
			stopY = maxY;
		}
		//
		int sampleColorX = startX + blockSize / 2;
		int sampleColorY = startY + blockSize / 2;
		//
		if (sampleColorX > maxX) {
			sampleColorX = maxX;
		}
		if (sampleColorY > maxY) {
			sampleColorY = maxY;
		}
		int colorLinePosition = sampleColorY * width;
		int sampleColor = pxs[colorLinePosition + sampleColorX];
		for (int y = startY; y <= stopY; y++) {
			int p = y * width;
			for (int x = startX; x <= stopX; x++) {
				pxs[p + x] = sampleColor;
			}
		}
	}

	@Override
	public void calculate() {
		int leftUpX = Math.min(start.x, end.x);
		int leftUpY = Math.min(start.y, end.y);
		int rightBottomX = Math.max(start.x, end.x);
		int rightBottomY = Math.max(start.y, end.y);
		int w = Math.abs(rightBottomX - leftUpX);
		int h = Math.abs(rightBottomY - leftUpY);
		w = Math.max(w, 4);
		h = Math.max(h, 4);
		srcMosaic = new Rect(0, 0, w, h);
		targetRect = new Rect(leftUpX, leftUpY, leftUpX + w, leftUpY + h);
	}

	@Override
	boolean drag(Pt pt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	void spin(int angle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void move(int movex, int movey) {
		// TODO Auto-generated method stub
		
	}

	@Override void onDrawPoints(Canvas canvas, float radius, Paint pointPaint, Paint pointFillPaint) {

	}

	@Override List<Point> getPoints() {
		return null;
	}
}
