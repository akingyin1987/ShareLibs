package com.zlcdgroup.tuyalib;

public class Area {

	public    Pt    topleftPt;
	
	public    Pt    toprightPt;
	
	public    Pt    bottomleftPt;
	
	public    Pt    bottomrightPt;
	
	
	public    boolean   isInArea(Pt   pt){
		if(null == topleftPt || null == toprightPt || 
				null == bottomleftPt || null == bottomrightPt){
			return  false;
		}
	
		if(TuYaUtil.triangleArea(bottomleftPt, topleftPt, toprightPt) + TuYaUtil.triangleArea(bottomleftPt, bottomrightPt, toprightPt)== 
			TuYaUtil.triangleArea(pt, topleftPt, toprightPt) + TuYaUtil.triangleArea(pt, toprightPt, bottomrightPt)+
			TuYaUtil.triangleArea(pt, bottomrightPt, bottomleftPt)+TuYaUtil.triangleArea(pt, bottomleftPt, topleftPt)){
			return true;
		}
		return false;
	};
	
	
	public static   Area   CreateArea(Pt  statPt  ,Pt  endPt,int  length){
		
		Area   area  =  new   Area();
		if(statPt.x == endPt.x){
			area.topleftPt = new  Pt();
			area.topleftPt.x = statPt.x-length;
			area.topleftPt.y = endPt.y;
			area.bottomleftPt = new Pt(statPt.x-length,statPt.y);
			area.toprightPt = new Pt(statPt.x-length,endPt.y);
			area.bottomrightPt = new  Pt(statPt.x+length,statPt.y);
			return  area;
		}else if(statPt.y == endPt.y){
			area.topleftPt = new  Pt(statPt.x, statPt.y+length);
			area.toprightPt = new  Pt(endPt.x, endPt.y+length);
			area.bottomleftPt = new  Pt(statPt.x, statPt.y-length);
			area.bottomrightPt = new  Pt(endPt.x, endPt.y-length);
			return area;
		}
		double slope = Math.atan2((double)(endPt.y - statPt.y) , (double) (endPt.x - statPt.x));
		
		int  movex = (int) (length* Math.sin(slope/Math.PI * 180)) ;
		
		int  movey = (int) (length *Math.cos(slope/Math.PI * 180));
		
		
		area.topleftPt = new  Pt(statPt.x-movex, statPt.y+movey);
		area.bottomleftPt = new  Pt(statPt.x + movex, statPt.y-movey);
		area.toprightPt = new  Pt(endPt.x-movex, endPt.y+movey);
		area.bottomrightPt = new  Pt(endPt.x + movex, endPt.y-movey);
		
		
		return area;
		
	}   
	
	
}
