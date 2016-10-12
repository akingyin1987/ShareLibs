package com.zlcdgroup.camera.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;


/**
 * created by lht on 2016/5/10
 */
public class ReferenceLine extends View {

    //private Paint mLinePaint;
    private Paint mLinePaintRec;
    private float top, left, right, bottom;
    private boolean isDraw;
    private float sreenWidth, sreenHeight;

    public ReferenceLine(Context context) {
        this(context, null);

    }

    public ReferenceLine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ReferenceLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        DisplayMetrics screenWH = getContext().getResources().getDisplayMetrics();
        sreenHeight = screenWH.heightPixels;
        sreenWidth = screenWH.widthPixels;
        //读数标识矩形框
        mLinePaintRec = new Paint();
        mLinePaintRec.setAntiAlias(true);
        mLinePaintRec.setColor(Color.GREEN);
        mLinePaintRec.setStyle(Paint.Style.STROKE);
        mLinePaintRec.setStrokeWidth(5);
    }

    // protected void onDraw(Canvas canvas) {
        /*int screenWidth = CarmeraUtils.getScreenWH(getContext()).widthPixels;
        int screenHeight = CarmeraUtils.getScreenWH(getContext()).heightPixels;
        Log.i("LHT", "screenWidth " + screenWidth);
        Log.i("LHT", "screenHeight " + screenHeight);

        float left = (float) (0.3 * screenWidth / 2);
//Log.i("LHT","left "+left);
        MyApplication.x1 = left;
        float top = (float) (screenHeight / 2 - 50 - 0.29 * screenWidth);
//Log.i("LHT","top "+top);
        MyApplication.y1 = top;
//float right = (float) (1.45 * screenWidth / 2);
//float right = (float) (1.7 * screenWidth / 2);
        float right = (float) (screenWidth - left);
//Log.i("LHT","right "+right);
//float bottom = (float) (screenHeight / 2 - 50 - 0.11 * screenWidth);\
        float bottom = (float) (screenHeight / 2 - 50 - 0.1445 * screenWidth);
//Log.i("LHT","width/height ="+(right-left)/(bottom-top));
//Log.i("LHT","width/screenWidth= " +(right-left)/screenWidth);
//Log.i("LHT","Xdestance "+(right-left));
//Log.i("LHT","Ydestance "+(bottom-top));
        MyApplication.xDestance = right - left;
        MyApplication.yDestance = bottom - top;
        canvas.drawRect(left,
                top,
                right,
                bottom, mLinePaintRec);*/
    //  }

    /**
     * 3* @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (isDraw) {
            canvas.drawRect(left,
                    top,
                    right,
                    bottom, mLinePaintRec);
        }
    }

    public synchronized void setRect(float top1, float left1, float right1, float bottom1) {
        isDraw = true;
        top = top1;
        left = left1;
        right = right1;
        bottom = bottom1;
        Log.i("ReferenceLine ","top= "+top+",left= "+left+",right="+right+",bottom="+bottom+"");
        postInvalidate();

    }
}
