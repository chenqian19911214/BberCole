package com.bber.company.android.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.bber.company.android.R;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Administrator on 2016/3/3 0003.
 */
public class MyZoomImageView extends SimpleDraweeView {
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;

    private float mCurrentScale = 1.0f;
    private Matrix mCurrentMatrix;
    private float mMidX;
    private float mMidY;
    private OnZoomChangeListener onZoomChangeListener;
    private Activity activity;

    public MyZoomImageView(Context context) {
        this(context, null);
    }

    public MyZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void init() {
        ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                float newScale = mCurrentScale * scaleFactor;
                // Prevent from zooming out more than original


                if (newScale > 1.0f) {
                    // We initialize this lazily so that we don't have to register (and force the user
                    // to unregister) a global layout listener on the view.
                    if (mMidX == 0.0f) {
                        mMidX = getWidth() / 2.0f;
                    }
                    if (mMidY == 0.0f) {
                        mMidY = getHeight() / 2.0f;
                    }
                    mCurrentScale = newScale;
                    // support pinch zoom
                    mCurrentMatrix.postScale(scaleFactor, scaleFactor, mMidX, mMidY);
                    invalidate();
                } else {
                    scaleFactor = 1.0f / mCurrentScale;
                    reset();
                }

                if (onZoomChangeListener != null && scaleFactor != 1.0f) {
                    onZoomChangeListener.onZoomChange(mCurrentScale);
                }

                return true;
            }
        };
        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        mCurrentMatrix = new Matrix();

        GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // support drag
                // disable drag when normal scale

                if (mCurrentScale > 1.0f) {
                    mCurrentMatrix.postTranslate(-distanceX, -distanceY);
                    invalidate();
                }
                return true;
            }

            public boolean onSingleTapUp(MotionEvent e) {
                Log.e("eeeeeeeeeeeeeeeeeee", "----onSingleTapUp");
//                activity.finish();
                return true;
            }
        };
        mGestureDetector = new GestureDetector(getContext(), mGestureListener);

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        ProgressBarDrawable drable = new ProgressBarDrawable();
        drable.setColor(getResources().getColor(R.color.pink));
        GenericDraweeHierarchy hierarchy = builder.
                setProgressBarImage(drable).
                setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).
                build();
        setHierarchy(hierarchy);
    }


    /**
     * manual call
     *
     * @param listener OnZoomChangeListener
     */
    public void setOnZoomChangeListener(OnZoomChangeListener listener) {
        onZoomChangeListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setOnZoomChangeListener(null);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int saveCount = canvas.save();
        canvas.concat(mCurrentMatrix);
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return mScaleDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    /**
     * Resets the zoom of the attached image.
     * This has no effect if the image has been destroyed
     */
    public void reset() {
        mCurrentMatrix.reset();
        mCurrentScale = 1.0f;
        invalidate();
    }

    /**
     * A listener interface for when the zoom scale changes
     */
    public interface OnZoomChangeListener {
        /**
         * Callback method getting triggered when the zoom scale changes.
         * This is not called when the zoom is programmatically reset
         *
         * @param scale the new scale
         */
        void onZoomChange(float scale);
    }
}