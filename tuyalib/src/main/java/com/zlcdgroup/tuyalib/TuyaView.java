package com.zlcdgroup.tuyalib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TuyaView extends View  {
	public static int strokeWidth = 5;

	public Bitmap src;

	public Bitmap dis;

	private TuyaMovePostion tuyaMoveListion;

	public TuyaMovePostion getTuyaMoveListion() {
		return tuyaMoveListion;
	}

	public void setTuyaMoveListion(TuyaMovePostion tuyaMoveListion) {
		
		this.tuyaMoveListion = tuyaMoveListion;
	}

  public 	Paint paint = new Paint();

	List<Shape> shapList = new ArrayList<>();

	ShapeType currentShapeType = ShapeType.NULL;
	
    TuYaColor   tuYaColor = TuYaColor.Green;
	
	
	public TuYaColor getTuYaColor() {
		return tuYaColor;
	}

	public void setTuYaColor(TuYaColor tuYaColor) {
		this.tuYaColor = tuYaColor;
		System.out.println("set="+this.tuYaColor.toString());
		
	}

	Shape currenShape;

	public void clear() {
		shapList.clear();
		// currentShapeType = ShapeType.NULL;
	}

	public void clearLastOne() {

		int size = shapList.size();
		if (size >= 1) {
			Shape   shape  =  shapList.get(size-1);			
			shapList.remove(size - 1);
			if(null != currenShape){
				if(shape.equals(currenShape)){
					currenShape = null;
				}
			}			
			if (size == 1) {
				currenShape = null;
			}
		}
	

	}

	// 添加一个涂鸦图例
	public void addShape(Shape shape) {
		if (shape instanceof Arrow) {

			Arrow temparrow = (Arrow) shape;
			Arrow arrow = new Arrow(sw, sh, temparrow.getAngle(), 1.0 / 4, 1.0 / 6);
			arrow.reset(1.0/3, 1.0/3);
			shapList.add(arrow);
		} else if (shape instanceof Line) {
			Line tempLine = (Line) shape;
			Line line = new Line(sw, sh, tempLine.getAngle(), 1.0 / 4, 1.0 / 6);
			line.reset(1.0/3, 1.0/3);
			shapList.add(line);
		} else if (shape instanceof MLine) {
			MLine tempmline = (MLine) shape;
			MLine mline = new MLine(sw, sh, tempmline.getAngle(), tempmline.getScale(), tempmline.isIsright(), 1.0 / 4, 1.0 / 6);
			mline.reset(1.0/5, 1.0/5);
			shapList.add(mline);
		}
		if (null != tuyaMoveListion) {
			tuyaMoveListion.MoveCurrorPostion(shapList.size() - 1);
		}
		invalidate();
	}

	public void setCurrentShapeType(ShapeType currentShapeType) {
		this.currentShapeType = currentShapeType;

		switch (currentShapeType) {
		case Arrow: {
			currenShape = new Arrow();
			
		}
			break;

		case Circle: {
			currenShape = new Circle();
			
		}
			break;
		case Line: {
			currenShape = new Line();
			
		}
			break;
		case MLine: {
			currenShape = new MLine();
			
		}
			break;
		case NULL:
			currenShape = null;
			break;
		case Rectangle:
			currenShape = new Rectangle();
			
			break;
		case Mosaic:
			currenShape = new Mosaic();
			
			break;
		case TurnPainAround:
			currenShape = new TurnPaintAround();
			break;
		default:
			break;
		}
	}

	private int sw, sh;
	private Canvas bufferCanvas;
	private DisplayMetrics dm;

	public void setSrc(Bitmap src) {
		this.src = src;
		if (null != src) {
			init();
			postInvalidate();
		}
	}
	public static final float POINT_RADIUS = 10; // dp，锚点绘制半价
	private Paint mPointPaint;
	private Paint mPointFillPaint;
	private ShapeDrawable mMagnifierDrawable;
	public void init() {
		if (null == src) {
			return;
		}
		sw = src.getWidth();
		sh = src.getHeight();

		dis = Bitmap.createBitmap(sw, sh, Bitmap.Config.RGB_565);
		bufferCanvas = new Canvas(dis);

		if (dis.getWidth() > dis.getHeight()) {

			py = 0;
			if (dm.widthPixels > dm.heightPixels) {
				px = (dm.widthPixels - dis.getWidth()) / 2;
			} else {
				px = (dm.heightPixels - dis.getWidth()) / 2;
			}
			endy = dis.getHeight();
			endx = dis.getWidth();
		} else {
			endx = dis.getWidth();
			endy = dis.getHeight();
			px = (dm.widthPixels - dis.getWidth()) / 2;
			py = (dm.heightPixels - dis.getHeight() - Offset) / 2;
		}
		if (py < 0) {
			py = 0;
		}
		if (px < 0) {
			px = 0;
		}
		mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointPaint.setColor(0xFF00FFFF);
		mPointPaint.setStrokeWidth(1);
		mPointPaint.setStyle(Paint.Style.STROKE);

		mPointFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointFillPaint.setColor(Color.WHITE);
		mPointFillPaint.setStyle(Paint.Style.FILL);
		mPointFillPaint.setAlpha(175);
		BitmapShader magnifierShader = new BitmapShader(dis, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mMagnifierDrawable = new ShapeDrawable(new OvalShape());
		mMagnifierDrawable.getPaint().setShader(magnifierShader);
		getDrawablePosition();
	}

	private int Offset = 0;
	private int endx, endy;

	private   float  mDensity = 0f;
	public TuyaView(Context context, Bitmap src, DisplayMetrics dm) {
		super(context);
		this.src = src;
		this.dm = dm;
		mDensity = getResources().getDisplayMetrics().density;
		Offset = dip2px(context, 48);

		init();

		// old = new Rect(0, 0, dis.getWidth(), dis.getHeight());
		// now = new Rect(0, 0, dm.widthPixels, dm.heightPixels);
	}

	Rect old, now;
	int px, py;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		getDrawablePosition();
		paint.reset();
		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(strokeWidth);
    paint.setStyle(Paint.Style.FILL);
		drawSrc(bufferCanvas);

		drawTuYa(bufferCanvas);
		onDrawPoint(bufferCanvas);
		onDrawMagnifier(bufferCanvas);
		canvas.drawBitmap(dis, px, py, paint);



	}

	private void drawSrc(Canvas canvas) {
		canvas.save();
		// canvas.translate(sh, 0);
		// canvas.rotate(90);

		canvas.drawBitmap(src, 0, 0, paint);
		canvas.restore();
	}

	private void drawTuYa(Canvas canvas) {
		

		for (Shape shape : shapList) {
			
			if(null == shape.getMypaint()){
				
				Paint   selePaint = new  Paint();
				selePaint.reset();
				selePaint.setColor(Color.GREEN);
				if(tuYaColor == TuYaColor.Black){
					selePaint.setColor(Color.BLACK);
				}else if(tuYaColor == TuYaColor.Red){
					selePaint.setColor(Color.RED);
				}
				selePaint.setStrokeWidth(strokeWidth);
				shape.setMypaint(selePaint);
			}
			shape.draw(canvas, paint);

		}

		if (currenShape != null) {
			if (currenShape.getTuyaAction() != TuyaAction.MOVE) {
				currenShape.draw(canvas, paint);
			}

		}
	}

	private    boolean    isSaveBitmap = false;

  public boolean isSaveBitmap() {
    return isSaveBitmap;
  }

  public void setSaveBitmap(boolean saveBitmap) {
    isSaveBitmap = saveBitmap;
		System.out.println("--------setSaveBitmap-------"+isSaveBitmap);
		System.out.println("startTime="+ getNowTime());
  }

  private   String   getNowTime(){
		SimpleDateFormat   simpleDateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
		return  simpleDateFormat.format(new Date());
	}

  /**
   * 画点，只画最后一个，当保存时无需画
   * @param canvas
   */
	protected   void   onDrawPoint(Canvas  canvas){
		System.out.println("onDrawPoint="+isSaveBitmap+"time="+getNowTime());
    if(shapList.size()>0  && !isSaveBitmap){
      float  radios = dp2px(POINT_RADIUS);
      Shape   shape = shapList.get(shapList.size()-1);
      shape.onDrawPoints(canvas,radios,mPointPaint,mPointFillPaint);
    }
  }

	int mLinePoint = 0;
	int mTurnPoint = 0;
	@SuppressLint("ClickableViewAccessibility")

	private    Shape    moveShape;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX() - px;
		int y = (int) event.getY() - py;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}

		if (x > endx) {
			x = endx;
		}
		if (y > endy) {
			y = endy;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			mDraggingPoint = getNearbyPoint(event,x,y);
			if(shapList.size()>0){
				Shape  shape = shapList.get(shapList.size()-1);
				boolean  result = shape.checkPoints(event,x,y,dp2px(POINT_RADIUS));
				if(result){
					moveShape = shape;
					break;
				}
			}
			switch (currentShapeType) {
			case Arrow: {
				
				Arrow arrow = (Arrow) currenShape;
				if(null == arrow){
					currenShape = arrow = new  Arrow();
				}
				if(null == arrow.getMypaint()){
					arrow.setMypaint(getPrint());
				}
				arrow.setTuyaAction(TuyaAction.ADD);
				arrow.setStart(x, y);
			}
				break;

			case Circle: {
				Circle circle = (Circle) currenShape;
				if(null == circle){
					currenShape = circle = new  Circle();
				}
				if(null == circle.getMypaint()){
					circle.setMypaint(getPrint());
				}
				circle.setStart(x, y);
			}
				break;
			case Line: {
				Line line = (Line) currenShape;
				if(null == line){
					currenShape =line = new  Line();
				}
				if(null == line.getMypaint()){
					line.setMypaint(getPrint());
				}
				line.setTuyaAction(TuyaAction.ADD);
				line.setStart(x, y);
			}
				break;
			case MLine: {
				MLine mLine = (MLine) currenShape;
				if(null == mLine){
					currenShape =mLine = new   MLine();
				}
				if(null == mLine.getMypaint()){
					mLine.setMypaint(getPrint());
				}
				switch (mLinePoint) {
				case 0: {
					mLine.setStart(x, y);
					mLinePoint = 1;
				}
					break;
				case 1: {

				}
					break;
				case 2: {
					mLine.setArrowEnd(x, y);
				}
					break;
				}
			}
				break;
			case NULL:
				  Shape   shape = findInAreaShape(x, y);
					if (null != shape) {						
						currenShape = shape;
						currenShape.setArea(Area.CreateArea(currenShape.start, currenShape.end, 10));
						currenShape.setTuyaAction(TuyaAction.MOVE);
						currenShape.setMovePt(x, y);
					}else{
						currenShape = null;						
					}
				break;
			case Rectangle:
				 Rectangle   rectangle = (Rectangle)currenShape;
				 if(null == rectangle){
						currenShape =rectangle = new Rectangle();
					}
				 if(null == rectangle.getMypaint()){
					 rectangle.setMypaint(getPrint());
				 }
				 rectangle.setTuyaAction(TuyaAction.ADD);
				 rectangle.setStart(x, y);
				break;
			case Mosaic:
				Mosaic   mosaic = (Mosaic)currenShape;
				if(null == mosaic){
				     currenShape = mosaic = new Mosaic();
				}
				
				mosaic.setStart(x, y);
				break;
			case TurnPainAround:

				 TurnPaintAround  arc = (TurnPaintAround)currenShape;
					if(null == arc){
						currenShape = arc = new TurnPaintAround();
					}
					arc.setTuyaAction(TuyaAction.ADD);
				
					switch (mTurnPoint) {
					case 0:
						arc.setStart(x, y);
						mTurnPoint = 1;
						break;
					
					case 2:
						Pt   centerPt = TuYaUtil.Circle_Top(arc.arc.line.start, arc.arc.line.end, new Pt(x, y));
						arc.setArcEnd(centerPt.x, centerPt.y);
						arc.setArrowEnd(x, y);
						break;
					
					default:
						break;
					}
	
				break;
			default:

				break;
			}
		}
			break;
		case MotionEvent.ACTION_UP: {
			mDraggingPoint = null;
			if(null != moveShape){
				moveShape.setMovePointPt(null);
				moveShape = null;
				break;
			}
			switch (currentShapeType) {
			case Arrow: {
				Arrow arrow = (Arrow) currenShape;
				arrow.setTuyaAction(TuyaAction.NULL);
				arrow.setEnd(x, y);
				shapList.add(currenShape);
				if (null != tuyaMoveListion) {
					tuyaMoveListion.MoveCurrorPostion(shapList.size() - 1);
				}
				currenShape = new Arrow();
			}
				break;

			case Circle: {
				Circle circle = (Circle) currenShape;
				circle.setEnd(x, y);
				circle.setTuyaAction(TuyaAction.NULL);
				shapList.add(currenShape);
				if (null != tuyaMoveListion) {
					tuyaMoveListion.MoveCurrorPostion(shapList.size() - 1);
				}
				currenShape = new Circle();
			}
				break;
			case Line: {
				Line line = (Line) currenShape;
				line.setEnd(x, y);
				line.setTuyaAction(TuyaAction.NULL);
				shapList.add(currenShape);
				if (null != tuyaMoveListion) {
					tuyaMoveListion.MoveCurrorPostion(shapList.size() - 1);
				}
				currenShape = new Line();
			}
				break;
			case MLine: {
				MLine mLine = (MLine) currenShape;
				switch (mLinePoint) {
				case 0: {

				}
					break;
				case 1: {
					mLine.setEnd(x, y);
					mLinePoint = 2;
				}
					break;
				case 2: {
					mLine.setArrowEnd(x, y);
					mLine.setTuyaAction(TuyaAction.NULL);
					shapList.add(currenShape);
					currenShape = new MLine();
					mLinePoint = 0;
				}
					break;
				}

			}
				break;
			case NULL:
				if (null != currenShape) {
					currenShape.setArea(null);
					currenShape.setTuyaAction(TuyaAction.NULL);
				}
				break;
			case Rectangle:
				Rectangle  rectangle = (Rectangle)currenShape;
				rectangle.setEnd(x, y);
				rectangle.setTuyaAction(TuyaAction.NULL);
				shapList.add(currenShape);
				if (null != tuyaMoveListion) {
					tuyaMoveListion.MoveCurrorPostion(shapList.size() - 1);
				}
				currenShape = new Rectangle();
				break;
			case Mosaic:
				Mosaic   mosaic = (Mosaic)currenShape;
				mosaic.setEnd(x, y);
				mosaic.clip(src);
				mosaic.setTuyaAction(TuyaAction.NULL);
				shapList.add(currenShape);
				if(null != tuyaMoveListion){
					tuyaMoveListion.MoveCurrorPostion(shapList.size() - 1);
				}
			    currenShape = new  Mosaic();
				break;
			case TurnPainAround:				
			{
				TurnPaintAround  arc = (TurnPaintAround)currenShape;

				switch (mTurnPoint) {
				case 1:
					 arc.setLineEnd(x, y);
					 mTurnPoint = 2;
					break;
				
				case 2:
					arc.setArrowEnd(x, y);
					arc.setTuyaAction(TuyaAction.NULL);
					shapList.add(arc);
					if (null != tuyaMoveListion) {
						tuyaMoveListion.MoveCurrorPostion(shapList.size() - 1);
					}
					currenShape = new TurnPaintAround();
					mTurnPoint = 0;
					break;
				default:
					break;
				}
			}

				break;
			
			default:
				break;
			}
		}
			break;
		case MotionEvent.ACTION_MOVE: {
       if(null != mDraggingPoint){
				 toImagePointSize(mDraggingPoint, event);
			 }
			 if(null != moveShape){
       	moveShape.onMovePoint(x,y);
       	break;
			 }
			switch (currentShapeType) {
			case Arrow: {
				Arrow arrow = (Arrow) currenShape;
				
				arrow.setEnd(x, y);
			}
				break;

			case Circle: {
				Circle circle = (Circle) currenShape;
				circle.setEnd(x, y);
			}
				break;
			case Line: {
				Line line = (Line) currenShape;
				line.setEnd(x, y);
			}
				break;

			case MLine: {
				MLine mLine = (MLine) currenShape;
				switch (mLinePoint) {
				case 0: {

				}
					break;
				case 1: {
					mLine.setEnd(x, y);
				}
					break;
				case 2: {
					mLine.setArrowEnd(x, y);
				}
					break;
				}
			}
				break;
			case NULL:
				if (null != currenShape) {
					currenShape.move(x - currenShape.getMoveX(), y - currenShape.getMoveY());
					currenShape.setMovePt(x, y);
				}
				break;
			case Rectangle:
				Rectangle   rectangle = (Rectangle)currenShape;
				rectangle.setEnd(x, y);
			    break;
			case Mosaic:
				Mosaic   mosaic = (Mosaic)currenShape;
				mosaic.setTuyaAction(TuyaAction.MosaicMove);
				mosaic.setEnd(x, y);
				mosaic.clip(src);
				break;
			case TurnPainAround:

				TurnPaintAround  arc = (TurnPaintAround)currenShape;
				switch (mTurnPoint) {
				case 1:
					 arc.setLineEnd(x, y);
					break;
				
				case 2:
					 arc.setArrowEnd(x, y);
					break;
				default:
					break;
				}
				break;

			default:
				break;
			}
		}
			break;
		}
		if (null == currenShape) {
			return true;
		}
		invalidate();
		return true;
	}

	public void destroyBitmap() {
		if (null != src) {
			src.recycle();
			src = null;
		}
		if (null != dis) {
			dis.recycle();
			dis = null;
		}
	}


	private void toImagePointSize(Point dragPoint, MotionEvent event) {
		if (dragPoint == null) {
			return;
		}
		System.out.println("old x="+event.getX()+"  y="+event.getY());
		int x = (int) ((Math.min(Math.max(event.getX(), mActLeft), mActLeft + mActWidth) - mActLeft) / mScaleX);
		int y = (int) ((Math.min(Math.max(event.getY(), mActTop), mActTop + mActHeight)- mActTop) / mScaleY);
		dragPoint.x = x;
		dragPoint.y = y;
		System.out.println("new x="+x+"  y="+y);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public Shape findInAreaShape(int x, int y) {

		if (null == shapList || shapList.size() == 0) {

			return null;
		}
		Pt pt = new Pt(x, y);
		for (Shape shape : shapList) {
			if (shape.drag(pt)) {

				return shape;
			}

		}
		return null;
	}

	
	public void moveLeft(int left, int postion) {
		if (null != shapList && shapList.size() > 0 && postion <= shapList.size()) {
			if (postion == shapList.size()) {
				postion = shapList.size() - 1;
			}
			Shape shape = shapList.get(postion);
			if (null != shape) {
				shape.move(left, 0);
				invalidate();
			}
		}

	}

	
	public void moveright(int right, int postion) {
		if (null != shapList && shapList.size() > 0 && postion <= shapList.size()) {
			if (postion == shapList.size()) {
				postion = shapList.size() - 1;
			}
			Shape shape = shapList.get(postion);
			if (null != shape) {
				shape.move(-right, 0);
				invalidate();
			}
		}

	}

	public void movetop(int top, int postion) {
		if (null != shapList && shapList.size() > 0 && postion <= shapList.size()) {
			if (postion == shapList.size()) {
				postion = shapList.size() - 1;
			}
			Shape shape = shapList.get(postion);
			if (null != shape) {

				shape.move(0, -top);
				invalidate();
			}
		}

	}

	
	public void movebottom(int bottom, int postion) {
		if (null != shapList && shapList.size() > 0 && postion <= shapList.size()) {
			if (postion == shapList.size()) {
				postion = shapList.size() - 1;
			}
			Shape shape = shapList.get(postion);
			if (null != shape) {
				shape.move(0, bottom);
				invalidate();
			}
		}

	}

	
	public void spin(int angle, int postion) {
		if (null != shapList && shapList.size() > 0 && postion <= shapList.size()) {
			if (postion == shapList.size()) {
				postion = shapList.size() - 1;
			}
			Shape shape = shapList.get(postion);
			if (null != shape) {
				shape.spin(angle);
				invalidate();
			}
		}

	}

	
	public   interface  TuyaMovePostion {
		public   void    MoveCurrorPostion(int postion);
	}
	
	public   Paint  getPrint(){
		Paint   selePaint = new  Paint();
		
		selePaint.setColor(Color.GREEN);
		if(tuYaColor == TuYaColor.Black){
			selePaint.setColor(Color.BLACK);
		}else if(tuYaColor == TuYaColor.Red){
			selePaint.setColor(Color.RED);
		}

		selePaint.setStrokeWidth(strokeWidth);
		return selePaint;
	}

	private float[] mMatrixValue = new float[9];
	private float mScaleX, mScaleY; // 显示的图片与实际图片缩放比
	private int mActWidth, mActHeight, mActLeft, mActTop; //实际显示图片的位置


	private void getDrawablePosition() {

		mScaleX = 1F;
		mScaleY = 1F;
		int origW = dis.getWidth();
		int origH = dis.getHeight();

		mActWidth = Math.round(origW * mScaleX);
		mActHeight = Math.round(origH * mScaleY);
		mActLeft = (getWidth() - mActWidth) / 2;
		mActTop = (getHeight() - mActHeight) / 2;
	}

	private static final float MAGNIFIER_CROSS_LINE_WIDTH = 0.8f; //dp，放大镜十字宽度
	private static final float MAGNIFIER_CROSS_LINE_LENGTH = 3; //dp， 放大镜十字长度
	private static final float MAGNIFIER_BORDER_WIDTH = 1; //dp，放大镜边框宽度
	private Point mDraggingPoint = null;
	private   Paint  mMagnifierPaint = null;
	private   Paint  mMagnifierCrossPaint = null;
	private Matrix mMagnifierMatrix = new Matrix();
	protected void onDrawMagnifier(Canvas canvas) {
		if ( mDraggingPoint != null) {
			if (mMagnifierDrawable == null) {
			  return;
			}
			if(null == mMagnifierPaint){
				mMagnifierPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				mMagnifierPaint.setColor(Color.WHITE);
				mMagnifierPaint.setStyle(Paint.Style.FILL);
			}
      if(null == mMagnifierCrossPaint){
				mMagnifierCrossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				mMagnifierCrossPaint.setColor(Color.BLUE);
				mMagnifierCrossPaint.setStyle(Paint.Style.FILL);
				mMagnifierCrossPaint.setStrokeWidth(dp2px(MAGNIFIER_CROSS_LINE_WIDTH));
			}
			float draggingX = getViewPointX(mDraggingPoint);
			float draggingY = getViewPointY(mDraggingPoint);

			float radius = getWidth() / 8;
			float cx = radius;
			int lineOffset = (int) dp2px(MAGNIFIER_BORDER_WIDTH);
			mMagnifierDrawable.setBounds(lineOffset, lineOffset, (int)radius * 2 - lineOffset, (int)radius * 2 - lineOffset);
			double pointsDistance = getPointsDistance(draggingX, draggingY, 0, 0);
			if (pointsDistance < (radius * 2.5)) {
				mMagnifierDrawable.setBounds(getWidth() - (int)radius * 2 + lineOffset, lineOffset, getWidth() - lineOffset, (int)radius * 2 - lineOffset);
				cx = getWidth() - radius;
			}
			canvas.drawCircle(cx, radius, radius, mMagnifierPaint);
			mMagnifierMatrix.setTranslate(radius - draggingX, radius - draggingY);
			mMagnifierDrawable.getPaint().getShader().setLocalMatrix(mMagnifierMatrix);
			mMagnifierDrawable.draw(canvas);

			//画放大镜十字线
			float crossLength = dp2px(MAGNIFIER_CROSS_LINE_LENGTH);
			canvas.drawLine(cx, radius - crossLength, cx, radius + crossLength, mMagnifierCrossPaint);
			canvas.drawLine(cx - crossLength, radius, cx + crossLength, radius, mMagnifierCrossPaint);
		}
	}


	private static final float TOUCH_POINT_CATCH_DISTANCE = 15; //dp，触摸点捕捉到锚点的最小距离
	private Point getNearbyPoint(MotionEvent event,int  x,int y) {

		Point   p  = new Point( x,y);
		float px = getViewPointX(p);
		float py = getViewPointY(p);

		double distance =  Math.sqrt(Math.pow(x - px, 2) + Math.pow(y - py, 2));
		System.out.println("dis=>"+distance);
		if (distance < dp2px(TOUCH_POINT_CATCH_DISTANCE)) {
			return p;
		}
		return p;
	}

	private float getViewPointX(Point point){
		return getViewPointX(point.x);
	}

	private float getViewPointX(float x) {
		//return x * mScaleX + mActLeft;
		return x * mScaleX;
	}

	private float getViewPointY(Point point){
		return getViewPointY(point.y);
	}

	private float getViewPointY(float y) {
	//	return y * mScaleY + mActTop;
		return y * mScaleY ;
	}

	public float dp2px(float dp) {
		return dp * mDensity;
	}


	public static double getPointsDistance(Point p1, Point p2) {
		return getPointsDistance(p1.x, p1.y, p2.x, p2.y);
	}

	public static double getPointsDistance(float x1, float y1, float x2, float y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

}
