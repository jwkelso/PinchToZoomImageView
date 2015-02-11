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

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * Encapsulates scale and translation logic for {@link PinchToZoomImageView}.
 * <p/>
 * When {@link #scale(int, int, float, float, float)} is invoked, a
 * {@link PinchToZoomMatrixState} instance is updated with information about the current state of
 * the image matrix. {@link PinchToZoomScaleHelper#scale(PinchToZoomMatrixState)} is then invoked to
 * calculate the changes that will need to be made to the image matrix. When the calculations are
 * complete, a callback will occur via
 * {@link #onScaleChanged(int, int, float, float, float, float)}. When this callback is received,
 * {@link #mMatrix} will be updated, and {@link #mMatrixChangedListener} will be notified that the
 * image matrix has been updated.
 * <p/>
 * When {@link #translate(int, int, float, float)} is invoked, a {@link PinchToZoomMatrixState}
 * instance is updated with information about the current state of the image matrix.
 * {@link PinchToZoomTranslationHelper#translate(float, float, PinchToZoomMatrixState)} is then
 * invoked to calculate the changes that will need to be made to the image matrix.
 * When the calculations are complete, a callback will occur via
 * {@link #onTranslationChanged(float, float)}.  {@link #mMatrix} will be updated, and
 * {@link #mMatrixChangedListener} will be notified of the updates to the image matrix.
 * <p/>
 * {@link #performInitialScaleAndTranslate(int, int)} will call into
 * {@link PinchToZoomScaleHelper#performInitialScale(PinchToZoomMatrixState)}, which will perform
 * the calculations necessary to scale the image such that the largest of the X or Y axis on the
 * coordinate plane will be the same size as the bounds of the view. When the calculations are
 * complete, this class will receive a callback via
 * {@link #onInitialScalePerformed(int, int, float, float)}. This will update the image matrix and
 * call into {@link PinchToZoomTranslationHelper#performInitialTranslation(PinchToZoomMatrixState)}.
 * This will perform the calculations necessary to translate the image such that the smallest of the
 * X or Y axis on the coordinate plane will be translated to center the image within the bounds of
 * the view. When the calculations are complete, this class will receive a callback via
 * {@link #onInitialTranslationPerformed(float, float)}. {@link #mMatrix} will be updated, and
 * {@link #mMatrixChangedListener} will be notified of the updated matrix.
 * <p/>
 */
class PinchToZoomMatrixHelper implements PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener,
        PinchToZoomTranslationHelper.OnPinchToZoomTranslationChangeListener {
    // Recycled objects to avoid new instance creation
    protected final float[] mMatrixValues = new float[9];
    protected final PinchToZoomMatrixState mMatrixState = new PinchToZoomMatrixState();

    // Instance state
    protected boolean mCropToPadding = false;
    protected boolean mPinAxesSmallerThanViewBounds = true;
    protected Rect mPadding = new Rect();
    protected Rect mTranslationExtra = new Rect();
    protected Point mSrcBitmapSize = new Point();
    protected Point mBitmapMinSize = new Point(0, 0);
    protected Point mBitmapMaxSize = new Point(0, 0);
    protected Matrix mMatrix = new Matrix();

    protected PinchToZoomScaleHelper mScaleHelper;
    protected PinchToZoomTranslationHelper mTranslationHelper;

    protected OnPinchToZoomMatrixChangeListener mMatrixChangedListener;

    PinchToZoomMatrixHelper(OnPinchToZoomMatrixChangeListener listener) {
        this.mMatrixChangedListener = listener;
        mScaleHelper = new PinchToZoomScaleHelper(this);
        mTranslationHelper = new PinchToZoomTranslationHelper(this);
    }

    /**
     * Calls into {@link PinchToZoomScaleHelper#performInitialScale(PinchToZoomMatrixState)}, which
     * will perform the calculations necessary to scale the image such that the largest of the X or
     * Y axis on the coordinate plane will be the same size as the bounds of the view. When the
     * calculations are complete, this class will receive a callback via
     * {@link #onInitialScalePerformed(int, int, float, float)}.
     * <p/>
     * Upon receiving this callback, {@link #mMatrix} is updated and
     * {@link PinchToZoomTranslationHelper#performInitialTranslation(PinchToZoomMatrixState)} is
     * invoked. This will perform the calculations necessary to translate the image such that the
     * smallest of the X or Y axis on the coordinate plane will be translated to center the image
     * within the bounds of the view. When the calculations are complete, this class will receive a
     * callback via {@link #onInitialTranslationPerformed(float, float)}.
     * <p/>
     * When this callback is received, {@link #mMatrix} will be updated, and
     * {@link #mMatrixChangedListener} will be notified of the updated matrix.
     *
     * @param viewMeasuredWidth  measured width of the view for which these calculations are
     *                           performed
     * @param viewMeasuredHeight measured height of the view for which these calculations are
     *                           performed
     */
    void performInitialScaleAndTranslate(int viewMeasuredWidth, int viewMeasuredHeight) {
        updateMatrixState(viewMeasuredWidth, viewMeasuredHeight);
        mScaleHelper.performInitialScale(mMatrixState);
    }

    /**
     * A {@link PinchToZoomMatrixState} instance is updated with information about the current state
     * of the image matrix. {@link PinchToZoomScaleHelper#scale(PinchToZoomMatrixState)} is then
     * invoked to calculate the changes that will need to be made to the image matrix.
     * <p/>
     * When the calculations are  complete, a callback will occur via
     * {@link #onScaleChanged(int, int, float, float, float, float)}. When this callback is
     * received, {@link #mMatrix} will be updated, and {@link #mMatrixChangedListener} will be
     * notified that the image matrix has been updated.
     *
     * @param viewMeasuredWidth  measured width of the view for which these calculations are
     *                           performed
     * @param viewMeasuredHeight measured height of the view for which these calculations are
     *                           performed
     * @param desiredScaleFactor the desired factor to which the image should be scaled
     * @param focusX             the X coordinate of the gesture that invoked the scale
     * @param focusY             the Y coordinate of the gesture that invoked the scale
     */
    void scale(int viewMeasuredWidth, int viewMeasuredHeight, float desiredScaleFactor,
               float focusX, float focusY) {

        updateMatrixState(viewMeasuredWidth, viewMeasuredHeight);
        mMatrixState.scaleFactor = desiredScaleFactor;
        mMatrixState.scaleFocusX = focusX;
        mMatrixState.scaleFocusY = focusY;

        mScaleHelper.scale(mMatrixState);
    }

    void checkTranslationBounds(int viewMeasuredWidth, int viewMeasuredHeight) {
        updateMatrixState(viewMeasuredWidth, viewMeasuredHeight);
        mTranslationHelper.checkTranslationBounds(mMatrixState);
    }

    /**
     * A {@link PinchToZoomMatrixState} instance is updated with information about the current state
     * of the image matrix.
     * {@link PinchToZoomTranslationHelper#translate(float, float, PinchToZoomMatrixState)} is then
     * invoked to calculate the changes that will need to be made to the image matrix.
     * <p/>
     * When the calculations are complete, a callback will occur via
     * {@link #onTranslationChanged(float, float)}.  {@link #mMatrix} will be updated, and
     * {@link #mMatrixChangedListener} will be notified of the updates to the image matrix.
     *
     * @param viewMeasuredWidth  measured width of the view for which these calculations are
     *                           performed
     * @param viewMeasuredHeight measured height of the view for which these calculations are
     *                           performed
     * @param dx                 desired translation on the X axis
     * @param dy                 desired translation on the Y axis
     */
    void translate(int viewMeasuredWidth, int viewMeasuredHeight, float dx, float dy) {
        updateMatrixState(viewMeasuredWidth, viewMeasuredHeight);
        mTranslationHelper.translate(dx, dy, mMatrixState);
    }

    /**
     * Ask {@link #mScaleHelper} if any scaling has been performed on the image.
     *
     * @return a boolean indicating whether or not any scaling has been performed
     */
    boolean isActualSizeZoomLevel() {
        return mScaleHelper.isActualSizeZoomLevel();
    }

    /**
     * Reset instance to state it was in immediately following construction. Usually this is invoked
     * because the current source {@link android.graphics.drawable.Drawable} in the
     * {@link PinchToZoomImageView} has been changed.
     */
    void reset() {
        mMatrix.reset();
        mSrcBitmapSize.set(0, 0);
        mScaleHelper.reset();
        mMatrixChangedListener.onMatrixChanged(mMatrix);
    }

    /**
     * Record the padding for view for which calculations are being performed.
     *
     * @param left   desired left padding
     * @param top    desired top padding
     * @param right  desired right padding
     * @param bottom desired bottom padding
     */
    void setPadding(int left, int top, int right, int bottom) {
        mPadding.set(left, top, right, bottom);
    }

    /**
     * Record whether crop-to-padding is enabled on the view for which calculations are being
     * performed.
     *
     * @param cropToPadding flag indicating whether we should crop to padding
     */
    void setCropToPadding(boolean cropToPadding) {
        this.mCropToPadding = cropToPadding;
    }

    /**
     * Record the size of the original bitmap on the view for which calculations are being
     * performed.
     *
     * @param width  the width of the bitmap
     * @param height the height of the bitmap
     */
    void setSrcBitmapSize(int width, int height) {
        mSrcBitmapSize.set(width, height);
    }

    /**
     * Allow the source image to pan outside the bounds of the view. Translation extra works
     * similarly to negative padding in that the bounds of the allowed image translation are
     * adjusted to be larger than the current bounds of the view. Note that negative values are
     * disallowed. Instead of using a negative value to inset the translation, use
     * {@link #setPadding(int, int, int, int)} combined with {@link #setCropToPadding(boolean)}.
     *
     * @param left   extra translation to left of the view
     * @param top    extra translation above the view
     * @param right  extra translation to right of the view
     * @param bottom extra translation below the view
     */
    void setTranslationExtra(int left, int top, int right, int bottom) {
        if (left < 0 || top < 0 || right < 0 || bottom < 0) {
            throw new IllegalArgumentException("setTranslationExtra() values cannot be less than " +
                    "0. Use setPadding() combined with setCropToPadding(true) instead.");
        }
        mTranslationExtra.set(left, top, right, bottom);
    }

    /**
     * The total amount an image may be zoomed out. If no minimum is set, the minimum is 25% of the
     * original size of the bitmap.
     *
     * @param width  minimum width to which a bitmap can be scaled
     * @param height minimum height to which a bitmap can be scaled
     */
    void setBitmapMinimumSize(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException(
                    "Minimum bitmap width and height must be greater than 0.");
        }
        mBitmapMinSize.set(width, height);
    }

    /**
     * The total amount an image may be zoomed in. If no maximum is set, the maximum is 200% of the
     * original size of the bitmap.
     *
     * @param width  maximum width to which a bitmap can be scaled
     * @param height maximum height to which a bitmap can be scaled
     */
    void setBitmapMaximumSize(int width, int height) {
        mBitmapMaxSize.set(width, height);
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
    void setPinAxesSmallerThanViewBounds(boolean pinAxesSmallerThanViewBounds) {
        this.mPinAxesSmallerThanViewBounds = pinAxesSmallerThanViewBounds;
    }

    /**
     * Update {@link #mMatrixState} to reflect the current state of the image matrix and the view
     * for which any calculations will be performed.
     *
     * @param viewWidth  width of the view for which calculations will be performed
     * @param viewHeight height of the view for which calculations will be performed
     */
    protected void updateMatrixState(int viewWidth, int viewHeight) {
        mMatrix.getValues(mMatrixValues);
        float currentScaleX = mMatrixValues[Matrix.MSCALE_X];
        float currentScaleY = mMatrixValues[Matrix.MSCALE_Y];

        mMatrixState.scaledBitmapWidth = mSrcBitmapSize.x * currentScaleX;
        mMatrixState.scaledBitmapHeight = mSrcBitmapSize.y * currentScaleY;
        mMatrixState.translationX = mMatrixValues[Matrix.MTRANS_X];
        mMatrixState.translationY = mMatrixValues[Matrix.MTRANS_Y];
        mMatrixState.paddingLeft = mPadding.left;
        mMatrixState.paddingTop = mPadding.top;
        mMatrixState.paddingRight = mPadding.right;
        mMatrixState.paddingBottom = mPadding.bottom;
        mMatrixState.translationExtraLeft = mTranslationExtra.left;
        mMatrixState.translationExtraTop = mTranslationExtra.top;
        mMatrixState.translationExtraRight = mTranslationExtra.right;
        mMatrixState.translationExtraBottom = mTranslationExtra.bottom;
        mMatrixState.viewWidth = viewWidth;
        mMatrixState.viewHeight = viewHeight;
        mMatrixState.cropToPadding = mCropToPadding;
        mMatrixState.pinAxesSmallerThanViewBounds = mPinAxesSmallerThanViewBounds;
        mMatrixState.srcBitmapWidth = mSrcBitmapSize.x;
        mMatrixState.srcBitmapHeight = mSrcBitmapSize.y;
        mMatrixState.minBitmapWidth = mBitmapMinSize.x;
        mMatrixState.minBitmapHeight = mBitmapMinSize.y;
        mMatrixState.maxBitmapWidth = mBitmapMaxSize.x;
        mMatrixState.maxBitmapHeight = mBitmapMaxSize.y;
    }

    /**
     * Update the {@link PinchToZoomSavedState} object so that it can later be used to create a new
     * instance with the same state. This state should only contains information that is not
     * persistent or can not be reconstructed later.
     *
     * @param state A {@link PinchToZoomSavedState} object that represents the internal state of the
     *              {@link PinchToZoomImageView} and all of its associated helpers.
     */
    void onSaveInstanceState(PinchToZoomSavedState state) {
        state.cropToPadding = mCropToPadding;
        state.pinAxesSmallerThanViewBounds = mPinAxesSmallerThanViewBounds;
        state.padding = mPadding;
        state.translationExtra = mTranslationExtra;
        state.srcBitmapSize = mSrcBitmapSize;
        state.bitmapMinSize = mBitmapMinSize;
        state.bitmapMaxSize = mBitmapMaxSize;
        state.matrix = mMatrix;

        mScaleHelper.onSaveInstanceState(state);
    }

    /**
     * Re-apply a representation of internal state that had previously been generated by
     * {@link #onSaveInstanceState(PinchToZoomSavedState)}. This function will never be called with
     * a null state.
     *
     * @param state The frozen state that had previously been returned by
     *              {@link #onSaveInstanceState(PinchToZoomSavedState)}.
     */
    void onRestoreInstanceState(@NonNull PinchToZoomSavedState state) {
        mCropToPadding = state.cropToPadding;
        mPinAxesSmallerThanViewBounds = state.pinAxesSmallerThanViewBounds;
        mPadding = state.padding;
        mTranslationExtra = state.translationExtra;
        mSrcBitmapSize = state.srcBitmapSize;
        mBitmapMinSize = state.bitmapMinSize;
        mBitmapMaxSize = state.bitmapMaxSize;
        mMatrix = state.matrix;

        mScaleHelper.onRestoreInstanceState(state);
    }

    /**
     * A callback from {@link PinchToZoomScaleHelper#scale(PinchToZoomMatrixState)} indicating that
     * the image matrix should be scaled by scaleX and scaleY at focusX and focusY. Update
     * {@link #mMatrix} and inform {@link #mTranslationHelper} that it needs to check the bounds of
     * the current translation.
     *
     * @param viewWidth  width of the view for which calculations will be performed
     * @param viewHeight height of the view for which calculations will be performed
     * @param scaleX     factor by which the matrix should be scaled in the X coordinate plane
     * @param scaleY     factor by which the matrix should be scaled in the Y coordinate plane
     * @param focusX     the X coordinate of the gesture that invoked the scale
     * @param focusY     the Y coordinate of the gesture that invoked the scale
     */
    @Override
    public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                               float focusX, float focusY) {
        mMatrix.postScale(scaleX, scaleY, focusX, focusY);
        checkTranslationBounds(viewWidth, viewHeight);
    }

    /**
     * A callback from {@link PinchToZoomScaleHelper#performInitialScale(PinchToZoomMatrixState)}
     * indicating that the image matrix should be scaled by scaleX and scaleY. Update
     * {@link #mMatrix} and call
     * {@link PinchToZoomTranslationHelper#performInitialTranslation(PinchToZoomMatrixState)}.
     *
     * @param viewWidth  width of the view for which calculations will be performed
     * @param viewHeight height of the view for which calculations will be performed
     * @param scaleX     factor by which the matrix should be scaled in the X coordinate plane
     * @param scaleY     factor by which the matrix should be scaled in the Y coordinate plane
     */
    @Override
    public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX, float scaleY) {
        mMatrix.setScale(scaleX, scaleY);
        updateMatrixState(viewWidth, viewHeight);
        mTranslationHelper.performInitialTranslation(mMatrixState);
    }

    /**
     * A callback from
     * {@link PinchToZoomTranslationHelper#translate(float, float, PinchToZoomMatrixState)}
     * indicating that the image matrix should be translated by translationX and translationY.
     * Update {@link #mMatrix} and notify {@link #mMatrixChangedListener} that the image matrix has
     * been updated.
     *
     * @param translationX amount by which the matrix should be translated in the X coordinate plane
     * @param translationY amount by which the matrix should be translated in the Y coordinate plane
     */
    @Override
    public void onTranslationChanged(float translationX, float translationY) {
        mMatrix.postTranslate(translationX, translationY);
        mMatrixChangedListener.onMatrixChanged(mMatrix);
    }

    /**
     * A callback from
     * {@link PinchToZoomTranslationHelper#checkTranslationBounds(PinchToZoomMatrixState)}
     * indicating that the image matrix should be translated by translationX and translationY.
     * Update {@link #mMatrix} and notify {@link #mMatrixChangedListener} that the image matrix has
     * been updated.
     *
     * @param translationX amount by which the matrix should be translated in the X coordinate plane
     * @param translationY amount by which the matrix should be translated in the Y coordinate plane
     */
    @Override
    public void onTranslationBoundsChecked(float translationX, float translationY) {
        mMatrix.postTranslate(translationX, translationY);
        mMatrixChangedListener.onMatrixChanged(mMatrix);
    }

    /**
     * A callback from
     * {@link PinchToZoomTranslationHelper#performInitialTranslation(PinchToZoomMatrixState)}
     * indicating that the image matrix should be translated by translationX and translationY.
     * Update {@link #mMatrix} and notify {@link #mMatrixChangedListener} that the image matrix has
     * been updated.
     *
     * @param translationX amount by which the matrix should be translated in the X coordinate plane
     * @param translationY amount by which the matrix should be translated in the Y coordinate plane
     */
    @Override
    public void onInitialTranslationPerformed(float translationX, float translationY) {
        mMatrix.postTranslate(translationX, translationY);
        mMatrixChangedListener.onMatrixChanged(mMatrix);
    }

    /**
     * An interface which allows an interested class to be notified of updates to the image matrix.
     */
    interface OnPinchToZoomMatrixChangeListener {
        void onMatrixChanged(Matrix imageMatrix);
    }
}
