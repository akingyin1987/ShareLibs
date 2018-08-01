package com.zlcdgroup.tuyalib;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.List;

public class Arrow extends Shape {
	public static final int arrowEdgeLength = 40;
	public static final double arrowAngle = Math.PI / 3;
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

	public Arrow() {
	}

	public Arrow(int width, int height, double angle, double widthscale,
			double heightscale) {
		this.width = width;
		this.height = height;
		this.angle = angle;
	//	this.widthscale = widthscale;
	//	this.heightscale = heightscale;
	//	reset();
	}

	public Arrow(int width, int height, double angle) {
		this.width = width;
		this.height = height;
		this.angle = angle;
	//	reset();
	}

	public  void  reset(double widthscale,double heightscale){
		this.widthscale = widthscale;
		this.heightscale = heightscale;
		reset();
	}
	public void reset() {
		int centerx = width / 2;
		int centery = height / 2;
		int off	 ;
		
		if (width <= height) {
			off = (int) (height * widthscale)/2;
			
		} else {
			off = (int) (width * heightscale)/2;
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

	

	@SuppressWarnings("unused")
	private Pt A1 = new Pt(), A2 = new Pt();

	private Path path = null;

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (end.x == 0 && end.y == 0) {
			return;
		}
		
		if(null != getMypaint()){
			
			getMypaint().setStyle(Style.STROKE);
			canvas.drawLine(start.x, start.y, end.x, end.y, getMypaint());
			
			if (path != null) {
				getMypaint().setStyle(Style.FILL);
				canvas.drawPath(path, getMypaint());
			}
		}else{
			
			paint.setStyle(Style.STROKE);
			canvas.drawLine(start.x, start.y, end.x, end.y, paint);
			if (path != null) {
				paint.setStyle(Style.FILL);
				canvas.drawPath(path, paint);
			}
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

	// public void calculate() {
	// double ylen = end.y - start.y;
	// double xlen = end.x - start.x;
	// double slope = Math.atan2(ylen, xlen);
	//
	// A1.y = (int) (end.y + arrowEdgeLength * Math.cos(slope + arrowAngle));
	// A1.x = (int) (end.x - arrowEdgeLength * Math.sin(slope + arrowAngle));
	//
	// A2.y = (int) (end.y - arrowEdgeLength * Math.cos(arrowAngle - slope));
	// A2.x = (int) (end.x - arrowEdgeLength * Math.sin(arrowAngle - slope));
	//
	// path = new Path();
	// path.moveTo(A1.x, A1.y);
	// path.lineTo(A2.x, A2.y);
	// path.lineTo(end.x, end.y);
	// }

	float ex, ey, sx, sy;
	double H = 40;
	double L = 14;

	@SuppressLint("UseValueOf")
	public void calculate() {
		ex = end.x;
		ey = end.y;
		sx = start.x;
		sy = start.y;

		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H);
		double arraow_len = Math.sqrt(L * L + H * H);
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0];
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0];
		double y_4 = ey - arrXY_2[1];
		Double X3 = new Double(x_3);
		x3 = X3.intValue();
		Double Y3 = new Double(y_3);
		y3 = Y3.intValue();
		Double X4 = new Double(x_4);
		x4 = X4.intValue();
		Double Y4 = new Double(y_4);
		y4 = Y4.intValue();

		if (ey == sy) {
			if(ex >sx){
				ex = ex + offx;
			}else if(ex < sx){
				ex = ex - offx;
			}

		} else if (ex == sx) {
			if(ey > sy){
				ey = ey + offx;
			}else if(ey < sy){
				ey = ey - offx;
			}

		} else {
			double slope = Math.atan2((ey - sy) ,(ex - sx));
				ex = (float) (ex + offx * Math.cos(slope));
				ey = (float) (ey + offx * Math.sin(slope));

		}
		path = new Path();
		path.moveTo(ex, ey);
		path.lineTo(x3, y3);
		path.lineTo(x4, y4);
		path.close();

	}

	private   void    drawArrow(){
		float x = ex - sx;
		float y = ey - sy;
		double d = x * x + y * y;
		double r = Math.sqrt(d);
		float zx = (float) (ex - (L * x / r));
		float zy = (float) (ey - (L * y / r));
		float xz = zx - sx;
		float yz = zy - sy;
		double zd = xz * xz + yz * yz;
		double zr = Math.sqrt(zd);
		path = new Path();
		path.moveTo(sx, sy);
		path.lineTo((float) (zx + H * yz / zr), (float) (zy - H * xz / zr));
		path.lineTo((float) (zx + H * 2 * yz / zr), (float) (zy - H * 2 * xz / zr));
		path.lineTo(ex, ey);
		path.lineTo((float) (zx - H * 2 * yz / zr), (float) (zy + H * 2 * xz / zr));
		path.lineTo((float) (zx - H * yz / zr), (float) (zy + H * xz / zr));
		path.close();
	}

	// 计算
	public double[] rotateVec(Float px, Float py, double ang, boolean isChLen,
			double newLen) {
		double mathstr[] = new double[2];
		// 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}

	public static final float offx = 6f;

	@Override
	public boolean drag(Pt pt) {

		if (null == getArea()) {
			return false;
			
		}
		
		return getArea().isInArea(pt);
	}

	@Override
	public boolean isEmpty() {

		return false;
	}

	@Override
	public void move(int movex, int movey) {
		
		start.x = start.x + movex;
		start.y = start.y + movey;
		
		end.x = end.x + movex;
		end.y = end.y + movey;
		calculate();
	}

	@Override
	void spin(int angle) {
		Pt centre = new Pt((start.x + end.x) / 2, (start.y + end.y) / 2);

		start = TuYaUtil.PointRotate(centre, start, angle);

		end = TuYaUtil.PointRotate(centre, end, angle);
		calculate();

	}

	void spin(int angle, Pt centre) {
		start = TuYaUtil.PointRotate(centre, start, angle);

		end = TuYaUtil.PointRotate(centre, end, angle);
		calculate();
	}

	@Override void onDrawPoints(Canvas canvas, float radius, Paint pointPaint, Paint pointFillPaint) {
		canvas.drawCircle(start.x, start.y, radius, pointFillPaint);
		canvas.drawCircle(start.x, start.y, radius, pointPaint);
		canvas.drawCircle(end.x, end.y, radius, pointFillPaint);
		canvas.drawCircle(end.x, end.y, radius, pointPaint);
	}

	private   List<Point>   mPoints;
	@Override List<Point> getPoints() {
		if(null == mPoints){
			mPoints = new ArrayList<>();

		}
		if(end.x>0 && end.y>0){
			mPoints.clear();
			mPoints.add(new Point(start.x,start.y));
			mPoints.add(new Point(end.x,end.y));
		}
		return mPoints;
	}


	@Override protected boolean checkPoints(MotionEvent event, int x, int y, float radius) {
		super.checkPoints(event, x, y, radius);
		setMovePointPt(null);
		double  d1 =TuYaUtil.getPointsDistance(x,y,start.x,start.y);
		if(d1<radius){
			setMovePointPt(start);
			return  true;
		}else{
			d1 = TuYaUtil.getPointsDistance(x,y,end.x,end.y);
			if(d1<radius){
				setMovePointPt(end);
				return  true;
			}
		}
		return  false;
	}

	@Override protected void onMovePoint(int moveX, int moveY) {
		super.onMovePoint(moveX, moveY);
		if(null!= getMovePointPt()){
			getMovePointPt().x = moveX;
			getMovePointPt().y = moveY;
			calculate();
		}
	}
}
