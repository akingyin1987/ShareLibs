package com.zlcdgroup.tuyalib;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.List;

public class TurnPaintAround extends Shape{
	
  
	
	public   Arc     arc =  new Arc();
	
	public   Arrow    arrow = new Arrow();

	@Override
	void draw(Canvas canvas, Paint paint) {
		if(null != getMypaint()){
			arc.draw(canvas, getMypaint());
			arrow.draw(canvas, getMypaint());
		}else{
			arc.draw(canvas, paint);
			arrow.draw(canvas, paint);
		}
		
		
		
	}

	@Override
	void calculate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	boolean drag(Pt pt) {
		// TODO Auto-generated method stub
		 return  arc.drag(pt) || arrow.drag(pt);
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

		arc.move(movex,movey);
		arrow.move(movex,movey);
		
	}
	
	public void setStart(int x, int y) {
		arc.startLine(x, y);
	}

	public void setLineEnd(int x, int y) {
	
		arc.setStart(x, y);
	}
	
	public  void  setArcEnd(int  x,int y){
		arc.setEnd(x, y);
		arrow.setStart(x, y);
	}

	public void setArrowEnd(int x, int y) {
		arrow.setEnd(x, y);
	}

	@Override void onDrawPoints(Canvas canvas, float radius, Paint pointPaint, Paint pointFillPaint) {

	}

	@Override List<Point> getPoints() {
		return null;
	}
}
