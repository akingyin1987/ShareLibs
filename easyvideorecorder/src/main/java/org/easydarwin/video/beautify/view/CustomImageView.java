package org.easydarwin.video.beautify.view;

import akingyin.easyvideorecorder.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomImageView extends ImageView {

	private int shape;
	private static final int SHAPE_NONE = 0;
	private static final int SHAPE_CIRCLE = 1;
	private static final int SHAPE_SQUARE = 2;

	private int mWidth;
	private int mHeight;
	private CornerRadius cornerRadius;
	private float cornerTopLeftRadius;
	private float cornerTopRightRadius;
	private float cornerBottomLeftRadius;
	private float cornerBottomRightRadius;

	private Border border;
	private int borderWidth;
	private int borderPadding;
	private int borderColor;

	private Bitmap mSrc;

	public CustomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomImageView(Context context) {
		this(context, null);
	}

	/**
	 * 初始化一些自定义的参数
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyle, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {

			int attr = a.getIndex(i);
			if (attr == R.styleable.CustomImageView_cornerRadius) {
				cornerRadius = CornerRadius.Parser.parse(a.getString(attr), context);
				if (cornerRadius != null) {
					cornerTopLeftRadius =
							cornerRadius.getTopLeftRadius() == null ? 0 : cornerRadius.getTopLeftRadius();
					cornerTopRightRadius =
							cornerRadius.getTopRightRadius() == null ? 0 : cornerRadius.getTopRightRadius();
					cornerBottomLeftRadius =
							cornerRadius.getBottomLeftRadius() == null ? 0 : cornerRadius.getBottomLeftRadius();
					cornerBottomRightRadius =
							cornerRadius.getBottomRightRadius() == null ? 0 : cornerRadius.getBottomRightRadius();
				}
			} else if (attr == R.styleable.CustomImageView_border) {
				border = Border.Parser.parse(a.getString(attr), context);
				if (border != null) {
					borderWidth = (int) (border.getBorderWidth() == null ? 0 : border.getBorderWidth());
					borderPadding = (int) (border.getBorderPadding() == null ? 0 : border.getBorderPadding());
					borderColor = border.getBorderColor();
				}
			} else if (attr == R.styleable.CustomImageView_shape) {
				shape = a.getInt(attr, SHAPE_NONE);
			} else if (attr == R.styleable.CustomImageView_cornerTopLeftRadius) {
				cornerTopLeftRadius = a.getDimension(attr, 0);
			} else if (attr == R.styleable.CustomImageView_cornerTopRightRadius) {
				cornerTopRightRadius = a.getDimension(attr, 0);
			} else if (attr == R.styleable.CustomImageView_cornerBottomLeftRadius) {
				cornerBottomLeftRadius = a.getDimension(attr, 0);
			} else if (attr == R.styleable.CustomImageView_cornerBottomRightRadius) {
				cornerBottomRightRadius = a.getDimension(attr, 0);
			}  else if (attr == R.styleable.CustomImageView_borderPadding) {
				borderPadding = (int) a.getDimension(attr, 0);
			} else if (attr == R.styleable.CustomImageView_borderColor) {
				borderColor = a.getColor(attr, 0);
			}
		}
		a.recycle();
	}
	//else if (attr == R.styleable.CustomImageView_borderWidth) {
	//	borderWidth = (int) a.getDimension(attr, 0);
	//}
	/**
	 * 计算控件的高度和宽度
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 设置宽度
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);

		if (specMode == MeasureSpec.EXACTLY) {// match_parent , accurate
			mWidth = specSize;
		} else {// 由图片决定的宽
			int desireByImg = getPaddingLeft() + getPaddingRight() + mSrc.getWidth();
			if (specMode == MeasureSpec.AT_MOST) {// wrap_content
				mWidth = Math.min(desireByImg, specSize);
			}
		}
		// 设置高度
		specMode = MeasureSpec.getMode(heightMeasureSpec);
		specSize = MeasureSpec.getSize(heightMeasureSpec);
		if (specMode == MeasureSpec.EXACTLY) {// match_parent , accurate
			mHeight = specSize;
		} else {
			int desire = getPaddingTop() + getPaddingBottom() + mSrc.getHeight();
			if (specMode == MeasureSpec.AT_MOST) {// wrap_content
				mHeight = Math.min(desire, specSize);
			}
		}
		int overFlowDual = (borderWidth + borderPadding) * 2;
		if (shape == SHAPE_CIRCLE || shape == SHAPE_SQUARE) {
			int min = Math.min(mWidth, mHeight);
			setMeasuredDimension(min + overFlowDual, min + overFlowDual);
		} else {
			setMeasuredDimension(mWidth + overFlowDual, mHeight + overFlowDual);
		}
	}

	/**
	 * 绘制
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		mSrc = ((BitmapDrawable) getDrawable()).getBitmap();
		int overFlow = borderWidth + borderPadding;
		int overFlowDual = overFlow * 2;
		int min = Math.min(mWidth, mHeight);
		int srcWidth = mSrc.getWidth();
		int srcHeight = mSrc.getHeight();
		int srcMin = Math.min(srcWidth, srcHeight);
		switch (shape) {
			case SHAPE_CIRCLE:

				Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint.setAntiAlias(true);
				Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
				Canvas can = new Canvas(target);
				float r = min / 2.0f;
				can.drawCircle(r, r, r, paint);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
				if (srcMin == srcWidth) {
					mSrc = Bitmap.createBitmap(mSrc, 0, (srcHeight - srcMin) / 2, srcMin, srcMin);
				} else {
					mSrc = Bitmap.createBitmap(mSrc, (srcWidth - srcMin) / 2, 0, srcMin, srcMin);
				}
				mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
				can.drawBitmap(mSrc, 0, 0, paint);
				paint.setXfermode(null);
				canvas.drawBitmap(target, overFlow, overFlow, null);
				// border
				paint.setColor(borderColor);
				paint.setStrokeWidth(borderWidth);
				paint.setStyle(Style.STROKE);
				canvas.drawCircle((min + overFlowDual) / 2.0f, (min + overFlowDual) / 2.0f, min / 2.0f + overFlow - borderWidth / 2.0f, paint);

				break;
			case SHAPE_SQUARE:
				Paint paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint3.setAntiAlias(true);
				if (srcMin == srcWidth) {
					mSrc = Bitmap.createBitmap(mSrc, 0, (srcHeight - srcMin) / 2, srcMin, srcMin);
				} else {
					mSrc = Bitmap.createBitmap(mSrc, (srcWidth - srcMin) / 2, 0, srcMin, srcMin);
				}
				mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
				float[] cornerRadius = new float[] {
						cornerTopLeftRadius, cornerTopLeftRadius, cornerTopRightRadius, cornerTopRightRadius, cornerBottomRightRadius,
						cornerBottomRightRadius, cornerBottomLeftRadius, cornerBottomLeftRadius };
				RoundRectShape rrs = new RoundRectShape(cornerRadius, null, null);
				Bitmap border1 = Bitmap.createBitmap(min + overFlowDual, min + overFlowDual, Config.ARGB_8888);
				Canvas can1 = new Canvas(border1);
				rrs.resize(min + overFlowDual, min + overFlowDual);
				paint3.setColor(borderColor);
				rrs.draw(can1, paint3);
				Bitmap border2 = Bitmap.createBitmap(min + borderPadding * 2, min + borderPadding * 2, Config.ARGB_8888);
				Canvas can2 = new Canvas(border2);
				rrs.resize(min + borderPadding * 2, min + borderPadding * 2);
				paint3.setColor(Color.GREEN);
				rrs.draw(can2, paint3);

				paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
				can1.drawBitmap(border2, borderWidth, borderWidth, paint3);

				Bitmap border3 = Bitmap.createBitmap(min, min, Config.ARGB_8888);
				Canvas can3 = new Canvas(border3);
				rrs.resize(min, min);
				paint3.setColor(Color.BLUE);
				paint3.setXfermode(null);
				rrs.draw(can3, paint3);

				paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
				can3.drawBitmap(mSrc, 0, 0, paint3);

				paint3.setXfermode(null);
				canvas.drawBitmap(border1, 0, 0, null);

				canvas.drawBitmap(border3, overFlow, overFlow, paint3);

				break;
			case SHAPE_NONE:

				Paint paint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint4.setAntiAlias(true);
				mSrc = Bitmap.createScaledBitmap(mSrc, mWidth, mHeight, false);
				float[] cornerRadius2 = new float[] {
						cornerTopLeftRadius, cornerTopLeftRadius, cornerTopRightRadius, cornerTopRightRadius, cornerBottomRightRadius,
						cornerBottomRightRadius, cornerBottomLeftRadius, cornerBottomLeftRadius };
				RoundRectShape rrs2 = new RoundRectShape(cornerRadius2, null, null);
				Bitmap border5 = Bitmap.createBitmap(mWidth + overFlowDual, mHeight + overFlowDual, Config.ARGB_8888);
				Canvas can5 = new Canvas(border5);
				rrs2.resize(mWidth + overFlowDual, mHeight + overFlowDual);
				paint4.setColor(borderColor);
				rrs2.draw(can5, paint4);
				Bitmap border6 = Bitmap.createBitmap(mWidth + borderPadding * 2, mHeight + borderPadding * 2, Config.ARGB_8888);
				Canvas can6 = new Canvas(border6);
				rrs2.resize(mWidth + borderPadding * 2, mHeight + borderPadding * 2);
				paint4.setColor(Color.GREEN);
				rrs2.draw(can6, paint4);

				paint4.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
				can5.drawBitmap(border6, borderWidth, borderWidth, paint4);

				Bitmap border7 = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
				Canvas can7 = new Canvas(border7);
				rrs2.resize(mWidth, mHeight);
				paint4.setColor(Color.BLUE);
				paint4.setXfermode(null);
				rrs2.draw(can7, paint4);

				paint4.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
				can7.drawBitmap(mSrc, 0, 0, paint4);

				paint4.setXfermode(null);

				canvas.drawBitmap(border5, 0, 0, null);

				canvas.drawBitmap(border7, overFlow, overFlow, paint4);

				break;

		}
	}

	/**
	 * 圆角css解析器
	 * 
	 * @author JiangYiwang parse
	 * 
	 *         <pre>
	 * mengchong:CornerRadius="10dp 9dp 8dp 7dp"
	 * </pre>
	 * 
	 *         get
	 * 
	 *         <pre>
	 * CornerRadius [topLeftRadius=10, topRightRadius=9, bottomLeftRadius=8, bottomRightRadius=7]
	 * </pre>
	 *         <p>
	 *         可支持1-4个参数
	 *         </p>
	 *         <p>
	 *         4个参数时分别指上左，上右，下右，下左圆角度数
	 *         </p>
	 *         <p>
	 *         3个参数时分别指上左，上右，（下右，下左）圆角度数
	 *         </p>
	 *         <p>
	 *         2个参数时分别指（上左，上右），（下右，下左）圆角度数
	 *         </p>
	 *         <p>
	 *         1个参数时指四个角圆角度数
	 *         </p>
	 */
	public static class CornerRadius {

		private Float topLeftRadius;
		private Float topRightRadius;
		private Float bottomLeftRadius;
		private Float bottomRightRadius;

		public static class Parser {
			public static CornerRadius parse(String radius, Context context) {

				if (TextUtils.isEmpty(radius)) {
					return null;
				}

				CornerRadius c = new CornerRadius();
				radius = radius.trim();
				String[] cr = radius.split("\\s+");
				System.out.println(cr.length);
				if (cr.length == 4) {
					if (!cr[0].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[0]);
					}
					c.setTopLeftRadius(dip2px(context, Float.valueOf(cr[0].replaceAll("(dp|dip)", ""))));
					if (!cr[1].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[1]);
					}
					c.setTopRightRadius(dip2px(context, Float.valueOf(cr[1].replaceAll("(dp|dip)", ""))));
					if (!cr[2].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[2]);
					}
					c.setBottomRightRadius(dip2px(context, Float.valueOf(cr[2].replaceAll("(dp|dip)", ""))));
					if (!cr[3].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[3]);
					}
					c.setBottomLeftRadius(dip2px(context, Float.valueOf(cr[3].replaceAll("(dp|dip)", ""))));
					return c;
				} else if (cr.length == 3) {
					if (!cr[0].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[0]);
					}
					c.setTopLeftRadius(dip2px(context, Float.valueOf(cr[0].replaceAll("(dp|dip)", ""))));
					if (!cr[1].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[1]);
					}
					c.setTopRightRadius(dip2px(context, Float.valueOf(cr[1].replaceAll("(dp|dip)", ""))));
					if (!cr[2].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[2]);
					}
					c.setBottomRightRadius(dip2px(context, Float.valueOf(cr[2].replaceAll("(dp|dip)", ""))));
					c.setBottomLeftRadius(dip2px(context, Float.valueOf(cr[2].replaceAll("(dp|dip)", ""))));
					return c;
				} else if (cr.length == 2) {
					if (!cr[0].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[0]);
					}
					c.setTopLeftRadius(dip2px(context, Float.valueOf(cr[0].replaceAll("(dp|dip)", ""))));
					c.setTopRightRadius(dip2px(context, Float.valueOf(cr[0].replaceAll("(dp|dip)", ""))));
					if (!cr[1].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[1]);
					}
					c.setBottomRightRadius(dip2px(context, Float.valueOf(cr[1].replaceAll("(dp|dip)", ""))));
					c.setBottomLeftRadius(dip2px(context, Float.valueOf(cr[1].replaceAll("(dp|dip)", ""))));
					return c;
				} else if (cr.length == 1) {
					if (!cr[0].matches("\\d+(\\.\\d+)?(dp|dip)?")) {
						throw new RuntimeException("parse border error with:" + cr[0]);
					}
					c.setTopLeftRadius(dip2px(context, Float.valueOf(cr[0].replaceAll("(dp|dip)", ""))));
					c.setTopRightRadius(dip2px(context, Float.valueOf(cr[0].replaceAll("(dp|dip)", ""))));
					c.setBottomRightRadius(dip2px(context, Float.valueOf(cr[0].replaceAll("(dp|dip)", ""))));
					c.setBottomLeftRadius(dip2px(context, Float.valueOf(cr[0].replaceAll("(dp|dip)", ""))));
					return c;
				} else if (cr.length == 0) {
					return null;
				}
				return null;

			}

			public static float dip2px(Context context, float dipValue) {
				final float scale = context.getResources().getDisplayMetrics().density;
				return dipValue * scale + 0.5f;
			}
		}

		public Float getTopLeftRadius() {
			return topLeftRadius;
		}

		public void setTopLeftRadius(Float topLeftRadius) {
			this.topLeftRadius = topLeftRadius;
		}

		public Float getTopRightRadius() {
			return topRightRadius;
		}

		public void setTopRightRadius(Float topRightRadius) {
			this.topRightRadius = topRightRadius;
		}

		public Float getBottomLeftRadius() {
			return bottomLeftRadius;
		}

		public void setBottomLeftRadius(Float bottomLeftRadius) {
			this.bottomLeftRadius = bottomLeftRadius;
		}

		public Float getBottomRightRadius() {
			return bottomRightRadius;
		}

		public void setBottomRightRadius(Float bottomRightRadius) {
			this.bottomRightRadius = bottomRightRadius;
		}
	}

	/**
	 * 边框解析器
	 * 
	 * @author JiangYiwang<br>
	 *         parse
	 * 
	 *         <pre>
	 * mengchong:border="10dp #ffffffff 80 10dp"
	 * </pre>
	 * 
	 *         get
	 * 
	 *         <pre>
	 * Border [borderWidth=10, borderColor=#ffffffff, borderAlpha=80, borderPadding=10]
	 * </pre>
	 *         <p>
	 *         可支持1-4个参数，第一个参数需为borderWidth,其他的可不按顺序
	 *         </p>
	 */
	public static class Border {
		private Float borderWidth;
		private int borderColor;
		private Float borderPadding;

		public int getBorderColor() {
			return borderColor;
		}

		public void setBorderColor(int borderColor) {
			this.borderColor = borderColor;
		}

		@Override
		public String toString() {
			return "Border [borderWidth=" + borderWidth + ", borderPadding=" + borderPadding + ", borderColor=" + borderColor + "]";
		}

		public static class Parser {
			public static Border parse(String border, Context context) {
				if (TextUtils.isEmpty(border)) {
					return null;
				}
				Border b = new Border();
				border = border.trim();
				String[] ss = border.split("\\s+");
				System.out.println(ss.length);
				if (ss.length == 3) {
					if (!ss[0].matches("\\d+(\\.\\d+)?(dp|dip){1}")) {
						throw new RuntimeException("parse border error with:" + ss[0]);
					}
					b.setBorderWidth(dip2px(context, Float.valueOf(ss[0].replaceAll("(dp|dip)", ""))));
					if (!ss[1].matches("#[0-9a-fA-F]{1,8}")) {
						throw new RuntimeException("parse border error with:" + ss[1]);
					}
					int color = Color.parseColor(ss[1]);
					b.setBorderColor(color);
					if (!ss[2].matches("\\d+(\\.\\d+)?(dp|dip){1}")) {
						throw new RuntimeException("parse border error with:" + ss[2]);
					}
					b.setBorderPadding(dip2px(context, Float.valueOf(ss[2].replaceAll("(dp|dip)", ""))));
					return b;
				} else if (ss.length == 2) {
					if (!ss[0].matches("\\d+(\\.\\d+)?(dp|dip){1}")) {
						throw new RuntimeException("parse border error with:" + ss[0]);
					}
					b.setBorderWidth(dip2px(context, Float.valueOf(ss[0].replaceAll("(dp|dip)", ""))));
					if (ss[1].matches("#[0-9a-fA-F]{1,8}")) {
						int color = Color.parseColor(ss[1]);
						b.setBorderColor(color);
					} else if (ss[1].matches("\\d+(\\.\\d+)?(dp|dip){1}")) {
						b.setBorderPadding(dip2px(context, Float.valueOf(ss[1].replaceAll("(dp|dip)", ""))));
					} else {
						throw new RuntimeException("parse border error with border" + border);
					}

					return b;
				} else if (ss.length == 1) {
					if (!ss[0].matches("\\d+(\\.\\d+)?(dp|dip){1}")) {
						throw new RuntimeException("parse border error with:" + ss[0]);
					}
					return b;
				} else if (ss.length == 0) {
					return null;
				}
				return null;
			}
		}

		public static float dip2px(Context context, float dipValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return dipValue * scale + 0.5f;
		}

		public Float getBorderWidth() {
			return borderWidth;
		}

		public void setBorderWidth(Float borderWidth) {
			this.borderWidth = borderWidth;
		}

		public Float getBorderPadding() {
			return borderPadding;
		}

		public void setBorderPadding(Float borderPadding) {
			this.borderPadding = borderPadding;
		}
	}

}
