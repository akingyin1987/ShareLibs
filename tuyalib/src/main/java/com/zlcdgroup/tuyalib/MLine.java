package com.zlcdgroup.tuyalib;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.List;

public class MLine extends Shape {
	private Line line = new Line();
	private Arrow arrow = new Arrow();

	private int width, height;
	private double angle;
	private boolean isright;
	private double scale;
	


	public double getScale() {
		return scale;
	}

	public boolean isIsright() {
		return isright;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getAngle() {
		return angle;
	}

	// 箭头占整图片涂鸦的比例
	public double widthscale = 1.0 / 3;
	public double heightscale = 1.0 / 3;

	public MLine() {
	}

	/**
	 * 
	 * @param
	 * @param
	 * @param angle
	 *            箭头与直线角度
	 * @param scale
	 *            直线与箭头比例
	 * @param
	 *
	 */
	public MLine(int width, int height, double angle, double scale,
			boolean isrigth) {
		this.width = width;
		this.height = height;
		this.angle = angle;
		this.scale  =  scale;
		this.isright = isrigth;
		
	}
	
	public  MLine(int width, int height, double angle, double scale,
			boolean isrigth,double widthscale,double heightscale){
		this.width = width;
		this.height = height;
		this.angle = angle;
		this.scale  =  scale;
		this.isright = isrigth;
	//	this.widthscale  =  widthscale;
		//this.heightscale =  heightscale;
		
	}
	
	public  void   reset(double widthscale,double heightscale){
		this.widthscale = widthscale;
		this.heightscale = heightscale;
		reset();
	}

	public void reset() {
		int centerx = width / 2;
		int centery = height / 2;
		int off;
		if (width <= height) {
			// 箭头长度
			off = (int) (height * widthscale);
//			if (0 <= angle && angle <= 360) {
//				double angledrgee = Math.PI / 180 * angle;
//				setStart(centerx - (int) ((off *scale* Math.cos(angledrgee))), centery
//						- (int) (off *scale* Math.sin(angledrgee)));
//				setEnd(centerx, centery);
//			}
//			if (isright) {
//				setArrowEnd(centerx + off, centery);
//			} else {
//				setArrowEnd(centerx - off, centery);
//			}
		} else {
			off = (int) (width * heightscale);
//			if (0 <= angle && angle <= 360) {
//				double angledrgee = Math.PI / 180 * angle;
//				setStart(centerx - (int) ((off *scale* Math.cos(angledrgee))), centery
//						- (int) (off *scale* Math.sin(angledrgee)));
//				setEnd(centery, centerx);
//			}
//			if (isright) {
//				setArrowEnd(centery + off, centerx);
//			} else {
//				setArrowEnd(centery - off, centerx);
//			}
		}
		if (0 <= angle && angle <= 360) {
			double angledrgee = Math.PI / 180 * angle;
			
				setStart(centerx - (int) ((off *scale* Math.cos(angledrgee))), centery
						- (int) (off *scale* Math.sin(angledrgee)));
			
			
			setEnd(centerx, centery);
			
			
		}
		if (isright) {
			setArrowEnd(centerx - off, centery);
		} else {
			setArrowEnd(centerx + off, centery);
		}
		int currentx = (line.start.x + line.end.x + arrow.end.x) / 3;
		int currenty = (line.start.y + line.end.y + arrow.end.y) / 3;
		// 计算偏移使整个图例居当前范围内的中间
		move(centerx - currentx, centery - currenty);

	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if(null != getMypaint()){
			line.draw(canvas, getMypaint());
			arrow.draw(canvas, getMypaint());
		}else{
			line.draw(canvas, paint);
			arrow.draw(canvas, paint);
		}
		
	}

	public void setStart(int x, int y) {
		line.setStart(x, y);
	}

	public void setEnd(int x, int y) {
		line.setEnd(x, y);
		arrow.setStart(x, y);
	}

	public void setArrowEnd(int x, int y) {
		arrow.setEnd(x, y);
	}

	@Override
	public void calculate() {

	}

	@Override
	public boolean drag(Pt pt) {
         
		return line.drag(pt) || arrow.drag(pt);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void move(int movex, int movey) {

		line.move(movex, movey);
		arrow.move(movex, movey);
	}

	@Override
	void spin(int angle) {
		Pt center = new Pt((line.start.x + line.end.x + arrow.end.x) / 3,
				(line.start.y + line.end.y + arrow.end.y) / 3);
		line.spin(angle, center);
		arrow.spin(angle, center);

	}

	@Override void onDrawPoints(Canvas canvas, float radius, Paint pointPaint, Paint pointFillPaint) {

	}

	@Override List<Point> getPoints() {
		return null;
	}
}
