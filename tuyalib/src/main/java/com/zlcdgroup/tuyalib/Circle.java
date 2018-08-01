package com.zlcdgroup.tuyalib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import java.util.List;

public class Circle extends Shape {
	private Pt center = new Pt();
	
	private int radius;

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (end.x == 0 && end.y == 0) {
			return;
		}
		if (center.x == 0 && center.y == 0) {
			return;
		}
		if(null != getMypaint()){
			getMypaint().setStyle(Style.STROKE);
			
			canvas.drawCircle(center.x, center.y, radius, getMypaint());
		
		}else{
			paint.setStyle(Style.STROKE);
			int color = paint.getColor();
			paint.setColor(Color.RED);
			canvas.drawCircle(center.x, center.y, radius, paint);
			paint.setColor(color);
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

	@Override
	public void calculate() {
		int xd = end.x - start.x;
		int yd = end.y - start.y;
		center.x = xd / 2 + start.x;
		center.y = yd / 2 + start.y;
		radius = (int) Math.sqrt(xd * xd + yd * yd) / 2;
	}

	@Override
	public boolean drag(Pt   pt) {
		
		return radius - TuYaUtil.distancePoint(center, pt) >=0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void move(int   movex,int  movey) {
		center.x = center.x +movex;
		center.y = center.y +movey;
		
	}

	@Override
	void spin(int angle) {
		
		
	}

	@Override void onDrawPoints(Canvas canvas, float radius, Paint pointPaint, Paint pointFillPaint) {

	}

	@Override List<Point> getPoints() {
		return null;
	}
}
