package com.zlcdgroup.camera;

import android.graphics.Point;

public class MathUtils {


    private MathUtils() {
    }

    /**
     * Ends up being a bit faster than {@link Math#round(float)}. This merely rounds its
     * argument to the nearest int, where x.5 rounds up to x+1. Semantics of this shortcut
     * differ slightly from {@link Math#round(float)} in that half rounds down for negative
     * values. -2.5 rounds to -3, not -2. For purposes here it makes no difference.
     *
     * @param d real value to round
     * @return nearest {@code int}
     */
    public static int round(float d) {
      return (int) (d + (d < 0.0f ? -0.5f : 0.5f));
    }

    public static float distance(float aX, float aY, float bX, float bY) {
      float xDiff = aX - bX;
      float yDiff = aY - bY;
      return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public static float distance(int aX, int aY, int bX, int bY) {
      int xDiff = aX - bX;
      int yDiff = aY - bY;
      return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
    
    public   static   final   float   scale16_9 = 16f/9;
    public   static   final   float   scale4_3 = 4f/3;
    
    public  static   Point   findBestViewSize(Point  screenPoint,Point  cameraPoint){
      if(null == cameraPoint || null == screenPoint){
        return null;
      }
      float  screenScale = Math.max(screenPoint.x,screenPoint.y)/(float)Math.min(screenPoint.x,screenPoint.y);
      float  scale = Math.max(cameraPoint.x, cameraPoint.y)/(float)Math.min(cameraPoint.x, cameraPoint.y);
      int  width = Math.min(screenPoint.x, screenPoint.y);
      int  height = Math.max(screenPoint.x, screenPoint.y);

      if(Math.abs(scale-screenScale)>0.01){
        int   beseheight = (int) (width * scale);
        if(beseheight>height){
          //如果高度超出范围
          width = (int) (height/scale);
          return new Point(width, 0);
        }
        return new Point(0, beseheight);
      }

      return null;
    }

}
