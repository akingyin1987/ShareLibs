package com.zlcdgroup.tuyalib;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract  class Shape {
	
	public  static   float  minRadius  = 10f;
	
	 Pt start = new Pt(), end = new Pt();
	
	private   Pt     movePt = new  Pt();
	
	private   Area      area;
	
	private   Paint    mypaint;
	
	
	
	
	

	public Paint getMypaint() {
		return mypaint;
	}

	public void setMypaint(Paint mypaint) {
		this.mypaint = mypaint;
	}

	public Area getArea() {
		area = Area.CreateArea(start, end, 40);
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public    void   setMovePt(int  x,int y){
		 movePt.x  = x;
		 movePt.y   = y;
	};
	
	public   int    getMoveX(){
        return  movePt.x;
	}
	
	public   int    getMoveY(){
		return  movePt.y;
	}
	
	
	private   TuyaAction    tuyaAction = TuyaAction.NULL;
	
	
	public TuyaAction getTuyaAction() {
		return tuyaAction;
	}

	public void setTuyaAction(TuyaAction tuyaAction) {
		this.tuyaAction = tuyaAction;
	}

	abstract void draw(Canvas canvas, Paint paint);

	abstract void calculate();
	
    abstract boolean  drag(Pt   pt);
    
    abstract boolean   isEmpty();
    
    //旋转角度
    abstract   void  spin(int  angle);
    
    //坐标整体移动
    abstract void   move(int   movex,int  movey);
	
	
}
