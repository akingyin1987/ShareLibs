package com.zlcdgroup.tuyalib;

public class TuYaUtil {

	 // 计算叉乘 |P0P1| × |P0P2| 
    public  static  double Multiply(Pt p1, Pt p2, Pt p0)
    {
        return ((p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y));
    }
    
    
    /**
     * p1围绕center逆时针旋转angle度
     * @param center 
     * @param p1
     * @param angle
     * @return
     */
    public static   Pt PointRotate(Pt center, Pt p1, double angle) {
        Pt tmp = new Pt();
        double angleHude = angle * Math.PI / 180;/*角度变成弧度*/
        double x1 = (p1.x - center.x) * Math.cos(angleHude) + (p1.y - center.y ) * Math.sin(angleHude) + center.x;
        double y1 = -(p1.x - center.x) * Math.sin(angleHude) + (p1.y - center.y) * Math.cos(angleHude) + center.y;
        tmp.x = (int)x1;
        tmp.y = (int)y1;
        return tmp;
    }
    
    /**
     * 三角形面积
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static double triangleArea(Pt a, Pt b, Pt c) 

    {
     double result = Math.abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y
     - c.x * b.y - a.x * c.y) / 2.0D);
    
     return result;
    }

    
    /**
     * 获取两点间的距离
     * @param p1
     * @param p2
     * @return
     */
    public  static   int  distancePoint(Pt   p1,Pt  p2){
    	
		return (int) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
    	
    }
    
    
    
    /**
     *  AB 与BC垂直
     * @param p1  线段AB A点
     * @param p2  线段AB B点
     * @param dRadius  抛物线顶点到 线段BC中点距离
     * @param center   线段BC C点
     * @return
     */
    public   static   Pt   Circle_Top(Pt p1,Pt p2,double dRadius,Pt center){
    	
    	Pt   top =  new Pt();
    	if(p1.x==p2.x){
    		if(p1.y>p2.y){
    			top.x = (p2.x+center.x)/2;
    			top.y =  (int) (p2.y-dRadius);
    			return top;
    		}
    		top.x = (p2.x+center.x)/2;
			top.y =  (int) (p2.y+dRadius);
    		return top;
    	}
    	if(p1.y == p2.y){
    		if(p1.x<p2.x){
    			top.x = (int) (p2.x+dRadius);
    			top.y = (p2.y+center.y)/2;
    		}else{
    			top.x = (int) (p2.x-dRadius);
    			top.y = (p2.y+center.y)/2;
    		}
    		return top;
    	}
    	

    	double  k_verticle = -1/((p2.y - center.y) / (double)(p2.x - center.x));
    	
    	if(p1.x < p2.x ){
    		top.x = (int) (center.x+dRadius *Math.sqrt(1/(k_verticle*k_verticle+1)));
    	}else{
    		top.x = (int) (center.x-dRadius *Math.sqrt(1/(k_verticle*k_verticle+1)));
    	}
    	
    	top.y = (int) (k_verticle*(top.x - center.x)+center.y);
    	
		return top;
    	
    }
    
    
    public  static   Pt    Circle_Top(Pt  pt1,Pt  pt2,Pt pt3){
   	 Pt  center = new Pt();
   	if(pt1.x == pt2.x){
   		
   		if(pt1.y>pt2.y){
   			center.y = pt1.y+ distancePoint(pt1,pt2);
   		}else{
   			center.y = pt1.y- distancePoint(pt1,pt2);
   		}
   		center.x = (pt1.x+pt2.x)/2;
   		return  center;
   	}
   	double  k = (pt2.y - pt1.y) / (double)(pt2.x - pt1.x);
   	center.x = (int) ((pt2.x+k*k*pt3.x-k*pt3.y+k*pt2.y)/(k*k + 1));
   	center.y = (int) (pt3.y+k*center.x-k*pt3.x);
   	
   	
   	return center;
   	
    }
    
}
