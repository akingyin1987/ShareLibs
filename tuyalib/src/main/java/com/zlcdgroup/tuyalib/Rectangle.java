package com.zlcdgroup.tuyalib;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Rectangle extends Shape {

	@Override
	void draw(Canvas canvas, Paint paint) {
		if (end.x == 0 && end.y == 0) {
			return;
		}
		if(null != getMypaint()){
			getMypaint().setStyle(Style.STROKE);
			canvas.drawRect(start.x, start.y, end.x, end.y, getMypaint());
		}else{
			paint.setStyle(Style.STROKE);
			canvas.drawRect(start.x, start.y, end.x, end.y, paint);
		}
		
		
	}

	@Override
	void calculate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	boolean drag(Pt pt) {
		
		if(start.x > end.x){
			if(pt.x>start.x || pt.x< end.x){
				return false;
			}
		}else{
			if(pt.x>end.x || pt.x<start.x){
				return false;
			}
		}
		
		if(start.y > end.y){
			if(pt.y>start.y || pt.y < end.y){
				return false;
			}
		}else{
			if(pt.y > end.y||pt.y<start.y){
				return false;
			}
		}
		return true;
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
		start.x = start.x + movex;
		start.y = start.y + movey;
		end.x = end.x + movex;
		end.y = end.y + movey;
		
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
	
	

}
