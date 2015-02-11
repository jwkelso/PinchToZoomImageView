/*
 * The MIT License (MIT)
 * Copyright (c) 2015 James W Kelso
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.jameskelso.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An {@link android.widget.ImageView} that allows for scaling and panning an image using pinch,
 * stretch, and pan gestures.
 * <p/>
 * Since this widget controls its own scaling and translation,
 * {@link #setScaleType(android.widget.ImageView.ScaleType)} is disallowed for any scale type other
 * than {@link ImageView.ScaleType#MATRIX}.
 * <p/>
 * The total amount an image may be zoomed in or zoomed out is controlled by
 * {@link #setBitmapMinimumSizePixels(int, int)} and {@link #setBitmapMaximumSizePixels(int, int)}.
 * If no minimum is set, the minimum is 25% of the original size of the bitmap. If no maximum is
 * set, the maximum is 200% of the original size of the bitmap.
 * <p/>
 * Any axis that is smaller than the view bounds (adjusted for padding if
 * {@link #setCropToPadding(boolean)} is enabled) can be automatically pinned to center using
 * {@link #setPinAxesSmallerThanViewBounds(boolean)}. This means that if the current scaled width or
 * height of the image is smaller than the view width or height, that axis will be centered in the
 * view and will not be able to be panned. If this value is not set, it is enabled by default.
 * <p/>
 * The source image can be allowed to pan outside the bounds of the view using
 * {@link #setTranslationExtra(int, int, int, int)}. Translation extra works similarly to negative
 * padding in that the bounds of the allowed image translation are adjusted to be larger than the
 * current bounds of the view. Note that negative values are disallowed. Instead of using a negative
 * value, use {@link #setPadding(int, int, int, int)} combined with
 * {@link #setCropToPadding(boolean)}.
 * <p/>
 * The threshold that determines the difference between a tap on the view and a dragging gesture is
 * controlled by {@link #setPanThreshold(int)}. As the pan threshold increases, the user will need
 * to drag further on the view to initiate panning on the image. If this value is not set, it
 * defaults to an arguably reasonable value.
 *
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_minBitmapWidth
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_maxBitmapWidth
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_minBitmapHeight
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_maxBitmapHeight
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_panThreshold
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_pinAxesSmallerThanBounds
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_transExtraLeft
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_transExtraRight
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_transExtraTop
 * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_transExtraBottom
 */
public class PinchToZoomImageView extends ImageView implements
        PinchToZoomTouchHelper.OnPinchToZoomTouchListener,
        PinchToZoomMatrixHelper.OnPinchToZoomMatrixChangeListener {

    /**
     * Helper class to abstract the math behind touches.
     */
    protected PinchToZoomTouchHelper mTouchHelper;

    /**
     * Helper class to abstract the math behind scaling and translation.
     */
    protected PinchToZoomMatrixHelper mMatrixHelper;

    /**
     * Flag indicating a checkTranslationBounds call is necessary on the next measure pass
     */
    protected boolean mRequiresTranslationBoundsCheck = false;

    public PinchToZoomImageView(Context context) {
        super(context);
        setUp(null, 0, 0);
    }

    public PinchToZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(attrs, 0, 0);
    }

    public PinchToZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PinchToZoomImageView(Context context, AttributeSet attrs, int defStyleAttr,
                                int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp(attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Load values from attributes when this view is inflated from layout XML.
     *
     * @param attrs       attribute set provided by the constructor
     * @param defStyle    attribute ID that points to default style resource provided by constructor
     * @param defStyleRes default style resource provided by constructor
     */
    private void resolveAttrs(@Nullable AttributeSet attrs, int defStyle,
                              @StyleRes int defStyleRes) {
        if (attrs == null) {
            return;
        }

        //Retrieve styles attributes
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView, defStyle,
                defStyleRes);

        int minBitmapWidth = a.getDimensionPixelSize(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_minBitmapWidth, 0);
        int minBitmapHeight = a.getDimensionPixelSize(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_minBitmapHeight, 0);
        if (minBitmapWidth != 0 || minBitmapHeight != 0) {
            mMatrixHelper.setBitmapMinimumSize(minBitmapWidth, minBitmapHeight);
        }

        int maxBitmapWidth = a.getDimensionPixelSize(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_maxBitmapWidth,
                0);
        int maxBitmapHeight = a.getDimensionPixelSize(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_maxBitmapHeight,
                0);
        if (maxBitmapWidth != 0 || maxBitmapHeight != 0) {
            mMatrixHelper.setBitmapMaximumSize(maxBitmapWidth, maxBitmapHeight);
        }

        int panThreshold = a.getInt(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_panThreshold, -1);
        if (panThreshold != -1) {
            mTouchHelper.setPanThreshold(panThreshold);
        }

        boolean pinAxes = a.getBoolean(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_pinAxesSmallerThanBounds,
                true);
        mMatrixHelper.setPinAxesSmallerThanViewBounds(pinAxes);

        int translationExtraLeft = a.getDimensionPixelSize(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_transExtraLeft, 0);
        int translationExtraTop = a.getDimensionPixelSize(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_transExtraTop, 0);
        int translationExtraRight = a.getDimensionPixelSize(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_transExtraRight, 0);
        int translationExtraBottom = a.getDimensionPixelSize(
                R.styleable.com_jameskelso_android_widget_PinchToZoomImageView_transExtraBottom, 0);
        if (translationExtraLeft != 0 || translationExtraTop != 0 || translationExtraRight != 0
                || translationExtraBottom != 0) {
            mMatrixHelper.setTranslationExtra(translationExtraLeft, translationExtraTop,
                    translationExtraRight, translationExtraBottom);
        }

        a.recycle();

        if (Build.VERSION.SDK_INT >= 16) {
            mMatrixHelper.setCropToPadding(getCropToPadding());
        }

        mMatrixHelper.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                getPaddingBottom());
    }

    /**
     * Perform tasks common to all constructors.
     *
     * @param attrs       attribute set provided by the constructor
     * @param defStyle    attribute ID that points to default style resource provided by constructor
     * @param defStyleRes default style resource provided by constructor
     */
    private void setUp(@Nullable AttributeSet attrs, int defStyle, @StyleRes int defStyleRes) {
        setClickable(true);
        super.setScaleType(ScaleType.MATRIX);

        // Instantiate touch helper object and make it the default OnTouchListener
        mTouchHelper = new PinchToZoomTouchHelper(getContext(), this);
        super.setOnTouchListener(mTouchHelper);

        // Instantiate the matrix helper object
        mMatrixHelper = new PinchToZoomMatrixHelper(this);

        // Load values from layout XML
        resolveAttrs(attrs, defStyle, defStyleRes);
    }

    /**
     * Set a scale type for this ImageView. This method will throw an
     * @{link IllegalArgumentException} for any scale type other than MATRIX.
     *
     * @param scaleType Scale type to which the ImageView should be set.
     * @attr ref android.R.styleable#ImageView_scaleType
     */
    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            return;
        }
        throw new IllegalArgumentException("Scale type must be ScaleType.MATRIX for " +
                "PinchToZoomImageView");
    }

    /**
     * Set an {@link android.view.View.OnTouchListener} for this ImageView. The touch helper is set
     * as the default {@link android.view.View.OnTouchListener} for the ImageView. The touch helper
     * will keep a reference to this {@link android.view.View.OnTouchListener} and will notify it
     * of touches as needed.
     *
     * @param listener the listener to be set
     */
    @Override
    public void setOnTouchListener(@Nullable OnTouchListener listener) {
        mTouchHelper.setOnTouchListener(listener);
    }

    /**
     * Set an {@link android.view.View.OnClickListener} for this ImageView. Because the touch helper
     * controls what is considered a tap and what is considered a pan gesture, the touch helper must
     * keep a reference to the {@link android.view.View.OnClickListener} to notify it when a tap
     * occurs.
     *
     * @param listener the listener to be set
     */
    @Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
        mTouchHelper.setOnClickListener(listener);
    }

    /**
     * Perform the measure pass on this widget. Additionally, if the zoom level is identity (1.0),
     * capture information about the view so that the matrix helper can perform the necessary
     * calculations for panning and zooming.
     *
     * @param widthMeasureSpec  {@link android.view.View.MeasureSpec} for the desired view width
     * @param heightMeasureSpec {@link android.view.View.MeasureSpec} for the desired view height
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mRequiresTranslationBoundsCheck) {
            mRequiresTranslationBoundsCheck = false;
            mMatrixHelper.checkTranslationBounds(getMeasuredWidth(), getMeasuredHeight());
        }

        // We capture some information about the drawable when we haven't yet scaled it
        if (mMatrixHelper.isActualSizeZoomLevel()) {
            captureDrawableState();

            // Scale our drawable to fit the screen
            mMatrixHelper.performInitialScaleAndTranslate(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    /**
     * Set the padding for this widget. Also, notify the matrix helper of the change in padding.
     *
     * @param left   desired left padding
     * @param top    desired top padding
     * @param right  desired right padding
     * @param bottom desired bottom padding
     * @attr ref android.R.styleable#View_padding
     * @attr ref android.R.styleable#View_paddingBottom
     * @attr ref android.R.styleable#View_paddingLeft
     * @attr ref android.R.styleable#View_paddingRight
     * @attr ref android.R.styleable#View_paddingTop
     */
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mMatrixHelper.setPadding(left, top, right, bottom);
        super.setPadding(left, top, right, bottom);
    }

    /**
     * Set the relative padding for this widget. Also, notify the matrix helper of the change in
     * padding.
     *
     * @param start  start padding
     * @param top    top padding
     * @param end    end padding
     * @param bottom bottom padding
     * @attr ref android.R.styleable#View_padding
     * @attr ref android.R.styleable#View_paddingBottom
     * @attr ref android.R.styleable#View_paddingStart
     * @attr ref android.R.styleable#View_paddingEnd
     * @attr ref android.R.styleable#View_paddingTop
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        mMatrixHelper.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                getPaddingBottom());

    }

    /**
     * Notify the matrix helper to enable/disable crop to padding. Also, inform the super class.
     *
     * @param cropToPadding flag indicating whether we should crop to padding
     * @attr ref android.R.styleable#ImageView_cropToPadding
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setCropToPadding(boolean cropToPadding) {
        mMatrixHelper.setCropToPadding(cropToPadding);
        super.setCropToPadding(cropToPadding);
    }

    /**
     * Change the source image for this ImageView. Also, reset touch and matrix helpers to identity.
     *
     * @param resId resource ID for the source image
     * @attr ref android.R.styleable#ImageView_src
     */
    @Override
    public void setImageResource(@DrawableRes int resId) {
        reset();
        super.setImageResource(resId);
    }

    /**
     * Change the source image for this ImageView. Also, reset touch and matrix helpers to identity.
     *
     * @param uri uri for the source image
     */
    @Override
    public void setImageURI(Uri uri) {
        reset();
        super.setImageURI(uri);
    }

    /**
     * Change the source image for this ImageView. Also, reset touch and matrix helpers to identity.
     *
     * @param drawable Drawable for the source image
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        reset();
        super.setImageDrawable(drawable);
    }

    /**
     * Reset the touch and matrix helpers to identity
     */
    protected void reset() {
        // Items can be null because drawables are set in the super constructor. Setting a drawable
        // triggers a reset().
        if (mTouchHelper != null) {
            mTouchHelper.reset();
        }
        if (mMatrixHelper != null) {
            mMatrixHelper.reset();
        }
    }

    /**
     * Capture information about the current source image for the matrix helper
     */
    protected void captureDrawableState() {
        Drawable d = getDrawable();

        // If our drawable doesn't have height or width, or is null, return
        if (d == null || d.getIntrinsicWidth() == 0 || d.getIntrinsicHeight() == 0) {
            return;
        }

        mMatrixHelper.setSrcBitmapSize(d.getIntrinsicWidth(), d.getIntrinsicHeight());
    }

    /**
     * Allow the source image to pan outside the bounds of the view. Translation extra works
     * similarly to negative padding in that the bounds of the allowed image translation are
     * adjusted to be larger than the current bounds of the view. Note that negative values are
     * disallowed. Instead of using a negative value to inset the translation, use
     * {@link #setPadding(int, int, int, int)} combined with {@link #setCropToPadding(boolean)}.
     *
     * @param left   amount the image should pan outside the left bound of the view
     * @param top    amount the image should pan outside the top bound of the view
     * @param right  amount the image should pan outside the right bound of the view
     * @param bottom amount the image should pan outside the bottom bound of the view
     * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_transExtraLeft
     * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_transExtraRight
     * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_transExtraTop
     * @attr ref R.styleable#com_jameskelso_android_widget_PinchToZoomImageView_transExtraBottom
     */
    public void setTranslationExtra(int left, int top, int right, int bottom) {
        mMatrixHelper.setTranslationExtra(left, top, right, bottom);
    }

    /**
     * The total amount an image may be zoomed out. If no minimum is set, the minimum is 25% of the
     * original size of the bitmap.
     *
     * @param minBitmapWidth  minimum width in pixels to which the bitmap can be scaled
     * @param minBitmapHeight minimum height in pixels to which the bitmap can be scaled
     */
    public void setBitmapMinimumSizePixels(int minBitmapWidth, int minBitmapHeight) {
        this.mMatrixHelper.setBitmapMinimumSize(minBitmapWidth, minBitmapHeight);
    }

    /**
     * The total amount an image may be zoomed in. If no maximum is set, the maximum is 200% of the
     * original size of the bitmap.
     *
     * @param maxBitmapWidth  maximum width in pixels to which the bitmap can be scaled
     * @param maxBitmapHeight maximum height in pixels to which the bitmap can be scaled
     */
    public void setBitmapMaximumSizePixels(int maxBitmapWidth, int maxBitmapHeight) {
        this.mMatrixHelper.setBitmapMaximumSize(maxBitmapWidth, maxBitmapHeight);
    }

    /**
     * Set the threshold that determines the difference between a tap on the view and a dragging
     * gesture. As the pan threshold increases, the user will need to drag further on the view to
     * initiate panning on the image. If this value is not set, it defaults to an arguably
     * reasonable value.
     *
     * @param panThreshold the distance in pixels over which a touch must travel to be considered
     *                     a panning gesture
     */
    public void setPanThreshold(int panThreshold) {
        this.mTouchHelper.setPanThreshold(panThreshold);
    }

    /**
     * Set whether any axis that is smaller than the view bounds (adjusted for padding if
     * {@link #setCropToPadding(boolean)} is enabled) should be automatically pinned to center.
     * This means that if the current scaled width or height of the image is smaller than the view
     * width or height, that axis will be centered in the view and will not be able to be panned. If
     * this value is not set, it is enabled by default.
     *
     * @param pinAxesSmallerThanViewBounds whether or not translations should be pinned
     */
    public void setPinAxesSmallerThanViewBounds(boolean pinAxesSmallerThanViewBounds) {
        this.mMatrixHelper.setPinAxesSmallerThanViewBounds(pinAxesSmallerThanViewBounds);
    }

    /**
     * Generate a representation of internal state that can later be used to create a new instance
     * with that same state. This state should only contains information that is not persistent or
     * can not be reconstructed later.
     *
     * @return a PinchToZoomSavedState object containing the view's current dynamic state.
     */
    @Override
    public PinchToZoomSavedState onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        PinchToZoomSavedState state = new PinchToZoomSavedState(superState);
        mMatrixHelper.onSaveInstanceState(state);
        mTouchHelper.onSaveInstanceState(state);

        return state;
    }

    /**
     * Re-apply a representation of internal state that had previously been generated by
     * {@link #onSaveInstanceState}. This function will never be called with a null state.
     *
     * @param state The frozen state that had previously been returned by
     *              {@link #onSaveInstanceState}.
     */
    @Override
    public void onRestoreInstanceState(@NonNull Parcelable state) {
        if (state instanceof PinchToZoomSavedState) {
            PinchToZoomSavedState ptzState = (PinchToZoomSavedState) state;
            super.onRestoreInstanceState(ptzState.getSuperState());

            mMatrixHelper.onRestoreInstanceState(ptzState);
            mTouchHelper.onRestoreInstanceState(ptzState);

            mRequiresTranslationBoundsCheck = true;
        } else {
            throw new IllegalArgumentException("Wrong state class, expecting " +
                    "PinchToZoomSavedState but received " + state.getClass().toString() +
                    " instead. This usually happens when two views of different type have the " +
                    "same id in the same hierarchy. Make sure other views do not use the same id.");
        }
    }

    /**
     * A callback from the touch helper indicating that the user has initiated a pinch or
     * stretch gesture.
     *
     * @param desiredScaleFactor amount by which the image should be scaled
     * @param focusX             the focus coordinate of the gesture on the X-axis
     * @param focusY             the focus coordinate of the gesture on the Y-axis
     */
    @Override
    public void onPinchToZoom(float desiredScaleFactor, float focusX, float focusY) {
        mMatrixHelper.scale(getMeasuredWidth(), getMeasuredHeight(), desiredScaleFactor,
                focusX, focusY);
    }

    /**
     * A callback from the touch helper indicating that the user has initiated a pan
     * gesture.
     *
     * @param dx amount by which the image should be translated on the X-axis
     * @param dy amount by which the image should be translated on the Y-axis
     */
    @Override
    public void onPan(float dx, float dy) {
        mMatrixHelper.translate(getMeasuredWidth(), getMeasuredHeight(), dx, dy);
    }

    /**
     * A callback from the matrix helper indicating that the image matrix has been updated
     * due to a scale or translation.
     *
     * @param imageMatrix the matrix containing the new values set by the matrix helper
     */
    @Override
    public void onMatrixChanged(Matrix imageMatrix) {
        setImageMatrix(imageMatrix);
    }
}
