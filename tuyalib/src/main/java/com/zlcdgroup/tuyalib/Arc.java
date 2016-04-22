package com.zlcdgroup.tuyalib;



import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 曲线 Created by Administrator on 2015/11/28.
 */
public class Arc extends Shape {

	public double radius = 20;

	public float sweepAngle = 15;

	public Path path;

	Pt pt = new Pt(), top = new Pt();

	public Line line = new Line();

	@Override
	void draw(Canvas canvas, Paint paint) {
		if (line.end.x == 0 && line.end.y == 0) {
			return;
		}
		line.draw(canvas, paint);
		
		if (end.x == 0 && end.y == 0) {
			return;
		}
		paint.setStyle(Paint.Style.STROKE);
		Pt center = new Pt((start.x + end.x) / 2, (start.y + end.y) / 2);
		radius = Math.sqrt(Math.abs(start.x - end.x) * Math.abs(start.x - end.x)
				+ Math.abs(start.y - end.y) * Math.abs(start.y - end.y));
		top = TuYaUtil.Circle_Top(line.start, line.end, radius, center);

		if (null == path) {
			path = new Path();
		}

		path.reset();
		path.moveTo(start.x, start.y);
		path.quadTo(top.x, top.y, end.x, end.y);

		canvas.drawPath(path, paint);
	}

	@Override
	void calculate() {

	}

	public void startLine(int x, int y) {
		line.setStart(x, y);
	}

	public void setStart(int x, int y) {
		start.x = x;
		start.y = y;
		line.setEnd(x, y);
	}

	public void setEnd(int x, int y) {
		end.x = x;
		end.y = y;
		calculate();
	}

	@Override
	boolean drag(Pt pt) {
		if (line.drag(pt)) {
			return true;
		}
		int minx = Math.min(Math.min(start.x, end.x), top.x);
		int maxx = Math.max(Math.max(start.x, end.x), top.x);

		int miny = Math.min(Math.min(start.y, end.y), top.y);
		int maxy = Math.max(Math.max(start.y, end.y), top.y);

		if (minx < pt.x && maxx > pt.x && miny < pt.y && maxy > pt.y) {
			return true;
		}
		return false;
	}

	@Override
	boolean isEmpty() {
		return false;
	}

	@Override
	void spin(int angle) {

	}

	@Override
	void move(int movex, int movey) {
		start.x = start.x + movex;
		start.y = start.y + movey;

		end.x = end.x + movex;
		end.y = end.y + movey;
		line.move(movex,movey);
	}
}
