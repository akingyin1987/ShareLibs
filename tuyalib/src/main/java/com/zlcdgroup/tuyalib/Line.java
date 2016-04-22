package com.zlcdgroup.tuyalib;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Line extends Shape {
	// 箭头占整图片涂鸦的比例
	public double widthscale = 1.0 / 3;
	public double heightscale = 1.0 / 3;
	
	private int width, height;
	private double angle;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getAngle() {
		return angle;
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (end.x == 0 && end.y == 0) {
			return;
		}
		if(null != getMypaint()){
			paint.setStyle(Style.STROKE);
			canvas.drawLine(start.x, start.y, end.x, end.y, getMypaint());
		}else{
			paint.setStyle(Style.STROKE);
			canvas.drawLine(start.x, start.y, end.x, end.y, paint);
		}
		
	}

	public Line() {
	};

	public Line(int width, int height, double angle) {
        this.width  =  width;
        this.height =  height;
        this.angle  =  angle;
       // reset();
	}
	
	public  Line(int width, int height, double angle, double widthscale,
			double heightscale){
		this.width = width;
		this.height = height;
		this.angle = angle;
	//	this.widthscale = widthscale;
	//	this.heightscale = heightscale;
	//	reset();
	}
	
	public   void   reset(double widthscale,double heightscale){
		this.widthscale = widthscale;
		this.heightscale  =  heightscale;
		reset();
	}

	public void reset() {
		int centerx = width / 2;
		int centery = height / 2;
		int off;
		if (width <= height) {
			off = (int) (height * widthscale)/2;
//			if (0 <= angle && angle <= 360) {
//				double angledrgee = Math.PI / 180 * angle;
//				setStart(centerx - (int) (off * Math.cos(angledrgee)), centery
//						- (int) (off * Math.sin(angledrgee)));
//				setEnd(centerx + (int) (off * Math.cos(angledrgee)), centery
//						+ (int) (off * Math.sin(angledrgee)));
//			}
		} else {
			off = (int) (height * heightscale)/2;
//			if (0 <= angle && angle <= 360) {
//				double angledrgee = Math.PI / 180 * angle;
//				setStart(centery - (int) (off * Math.cos(angledrgee)), centerx
//						- (int) (off * Math.sin(angledrgee)));
//				setEnd(centery + (int) (off * Math.cos(angledrgee)), centerx
//						+ (int) (off * Math.sin(angledrgee)));
//			}
		}
		
		
		if (0 <= angle && angle <= 360) {
			double angledrgee = Math.PI / 180 * angle;
			setStart(centerx - (int) (off * Math.cos(angledrgee)), centery
					- (int) (off * Math.sin(angledrgee)));
			setEnd(centerx + (int) (off * Math.cos(angledrgee)), centery
					+ (int) (off * Math.sin(angledrgee)));
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

	}

	@Override
	public boolean drag(Pt pt) {
		if (null == getArea()) {
			return false;
		}
		return getArea().isInArea(pt);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void move(int movex, int movey) {
		start.x = start.x + movex;
		start.y = start.y + movey;
		end.x = end.x + movex;
		end.y = end.y + movey;

	}

	@Override
	void spin(int angle) {
		Pt centre = new Pt((start.x + end.x) / 2, (start.y + end.y) / 2);

		start = TuYaUtil.PointRotate(centre, start, angle);

		end = TuYaUtil.PointRotate(centre, end, angle);

	}

	void spin(int angle, Pt centre) {
		start = TuYaUtil.PointRotate(centre, start, angle);

		end = TuYaUtil.PointRotate(centre, end, angle);
	}

}
