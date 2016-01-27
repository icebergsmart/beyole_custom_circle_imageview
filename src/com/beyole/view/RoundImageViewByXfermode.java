package com.beyole.view;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.beyole.customcircleimageview01.R;

public class RoundImageViewByXfermode extends ImageView {
	private Paint mPaint;
	private Xfermode mXfermode = new PorterDuffXfermode(Mode.DST_IN);
	private Bitmap mMaskBitmap;
	private WeakReference<Bitmap> mWeakBitmap;
	// ͼƬ����
	private int type;
	private static final int TYPE_ROUND = 1;
	private static final int TYPE_CIRCLE = 0;
	// Բ��Ĭ��ֵ
	private static final int DEFAULT_BORDER_RADIUS = 10;
	// Բ�Ǵ�С
	private int mBorderRadius;

	public RoundImageViewByXfermode(Context context) {
		this(context, null);
	}

	public RoundImageViewByXfermode(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundImageViewByXfermode(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
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
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// �����Բ�Σ���ǿ�Ƹı��Ⱥ͸߶�һ�£���СֵΪ׼
		if (type == TYPE_CIRCLE) {
			int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
			setMeasuredDimension(width, width);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// �ӻ������ó�ͼƬ
		Bitmap bitmap = mWeakBitmap == null ? null : mWeakBitmap.get();
		// ���ͼƬû�л�������Ѿ�������
		if (null == bitmap || bitmap.isRecycled()) {
			Drawable drawable = getDrawable();
			int dWidth = drawable.getIntrinsicWidth();
			int dHeight = drawable.getIntrinsicHeight();
			if (drawable != null) {
				bitmap = Bitmap.createBitmap(dWidth, dHeight, Bitmap.Config.ARGB_8888);
				float scale = 1.0f;
				// ��������
				Canvas drawCanvas = new Canvas(bitmap);
				if (type == TYPE_ROUND) {
					scale = Math.max(getWidth() * 1.0f / dWidth, getHeight() * 1.0f / dHeight);
				} else {
					scale = getWidth() * 1.0f / Math.min(dWidth, dHeight);
				}
				// �������ű�������bounds���൱������ͼƬ
				drawable.setBounds(0, 0, (int)( scale * dWidth), (int) (scale * dHeight));
				drawable.draw(drawCanvas);
				if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
					mMaskBitmap = getBitmap();
				}
				mPaint.reset();
				mPaint.setFilterBitmap(false);
				mPaint.setXfermode(mXfermode);
				drawCanvas.drawBitmap(mMaskBitmap, 0, 0, mPaint);
				mPaint.setXfermode(null);
				canvas.drawBitmap(bitmap, 0, 0, null);
				mWeakBitmap = new WeakReference<Bitmap>(bitmap);
			}
		}
		// ���bitmap�����ڣ���ֱ�ӻ���
		if (bitmap != null) {
			mPaint.setXfermode(null);
			canvas.drawBitmap(bitmap, 0, 0, mPaint);
			return;
		}

	}

	/**
	 * ������״
	 * 
	 * @return
	 */
	private Bitmap getBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		if (type == TYPE_ROUND) {
			canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), mBorderRadius, mBorderRadius, paint);
		} else {
			canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, paint);
		}
		return bitmap;
	}

	/**
	 * ����invalidate��ʱ����ջ���
	 */
	@Override
	public void invalidate() {
		mWeakBitmap = null;
		if (mMaskBitmap != null) {
			mMaskBitmap.recycle();
			mMaskBitmap = null;
		}
		super.invalidate();
	}
}
