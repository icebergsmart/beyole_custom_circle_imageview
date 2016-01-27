package com.beyole.view;

import java.util.Formatter.BigDecimalLayoutForm;

import com.beyole.customcircleimageview01.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

public class RoundImageView extends ImageView {

	private static final String TAG = RoundImageView.class.getSimpleName();
	// ͼƬ�����ͣ�Բ�λ���Բ��
	private int type;
	private static final int TYPE_ROUND = 1;
	private static final int TYPE_CIRCLE = 0;

	// Բ��Ĭ�ϴ�С
	private static final int DEFAULT_BORDER_RADIUS = 10;
	// Բ�ǵĴ�С
	private int mBorderRadius;
	// ��ͼpaint
	private Paint mBitmapPaint;
	// Բ�ǵİ뾶
	private int mRadius;
	// matrix��Ҫ���ڷŴ����СͼƬʹ��
	private Matrix mMatrix;
	// ��Ⱦͼ��ʹ��ͼ����Ϊ��ɫͼ��
	private BitmapShader mBitmapShader;
	// view�Ŀ��
	private int mWidth;
	private RectF mRoundRect;

	public RoundImageView(Context context) {
		this(context, null);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.e(TAG, "��ʼ����ʼ...");
		mMatrix = new Matrix();
		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyle, 0);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.RoundImageView_borderRadius:
				mBorderRadius = array.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BORDER_RADIUS, getResources().getDisplayMetrics()));
				break;
			case R.styleable.RoundImageView_type:
				type = array.getInt(attr, TYPE_CIRCLE);
				break;
			}
		}
		array.recycle();
		Log.e(TAG, "��ʼ������...");
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// �����Բ�Σ���ǿ�Ƹı��Ⱥ͸߶�һ�£���СֵΪ׼
		if (type == TYPE_CIRCLE) {
			mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
			mRadius = mWidth / 2;
			setMeasuredDimension(mWidth, mWidth);
		}
		Log.e(TAG, "onMeasure");
	}

	/**
	 * drawableתbitmap
	 * 
	 * @param drawable
	 * @return
	 */
	private Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			return bd.getBitmap();
		}
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		// ����
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		// ���Ƶķ�Χ
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * ��ʼ��bitmapshadow
	 */
	private void setUpBitmapShadow() {
		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}
		Bitmap bmp = drawableToBitmap(drawable);
		// ��bmp��Ϊ��ɫ����������ָ�������ڻ���bmp
		mBitmapShader = new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP);
		float scale = 1.0f;
		if (type == TYPE_CIRCLE) {
			// �õ�bitmap�Ŀ�Ⱥ͸߶ȵ���Сֵ
			int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
			// ͼƬ��С�ı���
			scale = mWidth * 1.0f / bSize;
		} else if (type == TYPE_ROUND) {
			// ���ͼƬ�Ŀ�Ⱥ͸߶Ⱥ�view�Ŀ�߲�ƥ�䣬���������Ҫ���ŵı��������ź��ͼƬ�Ŀ��һ��Ҫ�������ǵ�view�Ŀ��
			scale = Math.max(getWidth() * 1.0f / bmp.getWidth(), getHeight() * 1.0f / bmp.getHeight());
		}
		// shadow�ı任������Ҫ���ڷŴ����СͼƬ
		mMatrix.setScale(scale, scale);
		// ���ñ任����
		mBitmapShader.setLocalMatrix(mMatrix);
		// ����shader
		mBitmapPaint.setShader(mBitmapShader);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}
		setUpBitmapShadow();
		if (type == TYPE_ROUND) {
			canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius, mBitmapPaint);
		} else {
			canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
		}
		Log.e(TAG, "onDraw");
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (type == TYPE_ROUND) {
			mRoundRect = new RectF(0, 0, getWidth(), getHeight());
		}
		Log.e(TAG, "onSizeChanged");
	}

	private static final String STATE_INSTANCE = "state_instance";
	private static final String STATE_TYPE = "state_type";
	private static final String STATE_BORDER_RADIUS = "state_border_radius";

	/**
	 * �����û�״̬
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
		bundle.putInt(STATE_TYPE, type);
		bundle.putInt(STATE_BORDER_RADIUS, mBorderRadius);
		return bundle;
	}

	/**
	 * �ָ��û�״̬
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			super.onRestoreInstanceState(bundle.getParcelable(STATE_INSTANCE));
			this.type = bundle.getInt(STATE_TYPE);
			this.mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	public void setBorderRadius(int borderRadius) {
		int pxVal = dp2px(borderRadius);
		if (this.mBorderRadius != pxVal) {
			this.mBorderRadius = pxVal;
			invalidate();
		}
	}

	public void setType(int type) {
		if (this.type != type) {
			this.type = type;
			if (this.type != TYPE_ROUND && this.type != TYPE_CIRCLE) {
				this.type = TYPE_CIRCLE;
			}
			requestLayout();
		}

	}

	public int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
	}
}
