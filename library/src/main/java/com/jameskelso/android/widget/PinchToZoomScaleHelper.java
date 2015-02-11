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

/**
 * Encapsulates scale logic for {@link PinchToZoomImageView}.
 * <p/>
 * {@link #scale(PinchToZoomMatrixState)} allows for changing the zoom level of the current source
 * bitmap. It verifies that the requested scale factor (inside the {@link PinchToZoomMatrixState}
 * object) does not scale the image larger than the maximum bitmap size or smaller than the minimum
 * bitmap size. The X and Y coordinates of the originating zoom gesture are also determined. If the
 * image is scaled larger than both the width and height of its containing
 * {@link android.widget.ImageView}, the coordinates are calculated as the middle of the ImageView.
 * Otherwise, it just uses the touch coordinates provided inside the {@link PinchToZoomMatrixState}
 * object. Finally, {@link #mScaleListener} is notified that the scale factor and focus has changed.
 * <p/>
 * {@link #performInitialScale(PinchToZoomMatrixState)} will perform the calculations necessary to
 * scale the image such that the largest of the X or Y axis on the coordinate plane will be the same
 * size as the bounds of its containing ImageView. {@link #mScaleListener} will then be notified
 * that the initial scale operation is complete.
 */
class PinchToZoomScaleHelper {
    // Good defaults to calculate min and max bitmap size
    protected static final float DEFAULT_MAX_SCALE_FACTOR = 2f;
    protected static final float DEFAULT_MIN_SCALE_FACTOR = .25f;

    // Current scale factor (zoom level) for the source bitmap
    protected float mCurrentScaleFactor = 1.0f;

    protected OnPinchToZoomScaleChangeListener mScaleListener;

    public PinchToZoomScaleHelper(OnPinchToZoomScaleChangeListener listener) {
        this.mScaleListener = listener;
    }

    /**
     * Allows for changing the zoom level of the current source bitmap. It verifies the requested
     * scale factor (inside the {@link PinchToZoomMatrixState} object) using
     * {@link #verifyScaleFactor(float, PinchToZoomMatrixState)}. This ensures that the scale
     * operation does not scale the image larger than the maximum bitmap size or smaller than the
     * minimum bitmap size. If the scale factor is larger than the maximum or smaller than the
     * minimum, it is capped at the maximum or minimum.
     * <p/>
     * The X and Y coordinates of the originating zoom gesture are also determined. If the image is
     * scaled larger than both the width and height of its containing
     * {@link android.widget.ImageView}, the coordinates are calculated as the middle of the
     * {@link android.widget.ImageView}. Otherwise, it just uses the touch coordinates provided
     * inside the {@link PinchToZoomMatrixState} object. Finally, {@link #mScaleListener} is
     * notified that the scale factor and focus has changed.
     * <p/>
     * Note that the scale factor that is passed in the {@link #mScaleListener} callback is not the
     * value of the new scale factor. It is the multiplier that, when multiplied by the value of the
     * old scale factor, will yield the value of the new scale factor.
     *
     * @param state an object encapsulating information about the current state of the image matrix
     *              and the view that it supports.
     */
    void scale(PinchToZoomMatrixState state) {
        final float oldScaleFactor = mCurrentScaleFactor;
        float newScaleFactor = state.scaleFactor;
        mCurrentScaleFactor = verifyScaleFactor(oldScaleFactor * newScaleFactor, state);

        // Set the attempted scale factor to the multiplier that will allow us to arrive
        // at the new desired scale factor. For example, if we have a desired scale factor
        // of .25f and the current scale factor is .5f, it would take a scale factor of .5f
        // to arrive at .25f.
        newScaleFactor = mCurrentScaleFactor / oldScaleFactor;

        int scaledWidth = Math.round(state.srcBitmapWidth * mCurrentScaleFactor);
        int scaledHeight = Math.round(state.srcBitmapHeight * mCurrentScaleFactor);

        // If our desired source bitmap width is smaller than the bounds of the ImageView
        float scaleX = newScaleFactor;
        float scaleY = newScaleFactor;
        float focusX, focusY;
        if (scaledWidth <= state.viewWidth || scaledHeight <= state.viewHeight) {
            // Scale the source bitmap using the middle of the ImageView bounds as the
            // anchor point
            focusX = (float) state.viewWidth / 2f;
            focusY = (float) state.viewHeight / 2f;
        }
        // Otherwise
        else {
            // Scale the source bitmap using the touch coordinates of the user's gesture as
            // the anchor point
            focusX = state.scaleFocusX;
            focusY = state.scaleFocusY;
        }

        mScaleListener.onScaleChanged(state.viewWidth, state.viewHeight, scaleX, scaleY, focusX,
                focusY);
    }

    /**
     * Performs the calculations necessary to scale the image such that the largest of the X or Y
     * axis on the coordinate plane will be the same size as the bounds of its containing ImageView.
     * <p/>
     * Calculates the scale factor necessary to scale the larger of the X and Y axes to the full
     * width or height of the containing view. This accounts for padding if cropToPadding is
     * enabled. If the desired scale factor would scale the image such that its size would be larger
     * than the maximum bitmap size or smaller than the minimum bitmap size, it will be capped at
     * the largest/smallest possible scale factor that will allow the bitmap to remain within its
     * maximum or minimum size.
     * <p/>
     * {@link #mScaleListener} will then be notified that the initial scale operation is complete.
     * Note that the callback returns the total desired scale factor.
     *
     * @param state an object encapsulating information about the current state of the image matrix
     *              and the view that it supports.
     */
    void performInitialScale(PinchToZoomMatrixState state) {
        if (state.srcBitmapWidth == 0 || state.srcBitmapHeight == 0) {
            return;
        }

        // Adjust view width and height for padding (if cropToPadding is enabled)
        int adjustedWidth = state.viewWidth;
        if (state.cropToPadding) {
            adjustedWidth = state.viewWidth - state.paddingLeft - state.paddingRight;
        }

        int adjustedHeight = state.viewHeight;
        if (state.cropToPadding) {
            adjustedHeight = state.viewHeight - state.paddingTop - state.paddingBottom;
        }

        // Calculate the scale factor that would stretch or shrink the image to be equal to the
        // width of the containing view on the X coordinate plane.
        float desiredScaleX = (float) adjustedWidth / (float) state.srcBitmapWidth;

        // Calculate the scale factor that would stretch or shrink the image to be equal to the
        // width of the containing view on the Y coordinate plane.
        float desiredScaleY = (float) adjustedHeight / (float) state.srcBitmapHeight;

        // Use the scale factor from the axis that requires the least amount of stretching (or the
        // most shrinking).
        mCurrentScaleFactor = Math.min(desiredScaleX, desiredScaleY);

        // Cap the scale factor if the resulting scaled bitmap size is greater than the maximum
        // bitmap size or smaller than the minimum bitmap size.
        mCurrentScaleFactor = verifyScaleFactor(mCurrentScaleFactor, state);

        mScaleListener.onInitialScalePerformed(state.viewWidth, state.viewHeight,
                mCurrentScaleFactor, mCurrentScaleFactor);
    }

    /**
     * Determine if any scaling has been performed on the image.
     *
     * @return a boolean indicating whether or not any scaling has been performed
     */
    boolean isActualSizeZoomLevel() {
        return mCurrentScaleFactor == 1f;
    }

    /**
     * Determine if the resulting scaled bitmap size is greater than the maximum bitmap
     * size or smaller than the minimum bitmap size.
     *
     * @param scaleFactor the desired scale factor
     * @param state       an object encapsulating information about the current state of the image
     *                    matrix and the view that it supports.
     * @return a capped scale factor if the resulting scaled bitmap size is outside the maximum
     * bitmap size or the minimum bitmap size. Otherwise, return the desired scale factor.
     */
    protected float verifyScaleFactor(float scaleFactor, PinchToZoomMatrixState state) {
        scaleFactor = verifyScaleFactorMinBitmapWidth(scaleFactor, state.minBitmapWidth,
                state.srcBitmapWidth);
        scaleFactor = verifyScaleFactorMaxBitmapWidth(scaleFactor, state.maxBitmapWidth,
                state.srcBitmapWidth);
        scaleFactor = verifyScaleFactorMinBitmapHeight(scaleFactor, state.minBitmapHeight,
                state.srcBitmapHeight);
        scaleFactor = verifyScaleFactorMaxBitmapHeight(scaleFactor, state.maxBitmapHeight,
                state.srcBitmapHeight);

        return scaleFactor;
    }

    /**
     * Determine if the resulting scaled bitmap height is greater than the maximum bitmap
     * height.
     *
     * @param scaleFactor     the desired scale factor
     * @param maxBitmapHeight the maximum allowable bitmap height
     * @param srcBitmapHeight the original height of the image
     * @return a capped scale factor if the resulting scaled bitmap height is outside the maximum
     * bitmap height. Otherwise, return the desired scale factor.
     */
    protected float verifyScaleFactorMaxBitmapHeight(float scaleFactor, int maxBitmapHeight,
                                                     int srcBitmapHeight) {
        float maxBitmapHeightF = maxBitmapHeight;

        // If no maximum bitmap height is set, calculate one using the default max scale factor
        if (maxBitmapHeightF == 0) {
            maxBitmapHeightF = srcBitmapHeight * DEFAULT_MAX_SCALE_FACTOR;
        }
        // User is trying to scale larger than the allowed maximum bitmap height
        if ((scaleFactor * srcBitmapHeight) > maxBitmapHeightF) {
            scaleFactor = maxBitmapHeightF / (float) srcBitmapHeight;
        }
        return scaleFactor;
    }

    /**
     * Determine if the resulting scaled bitmap height is less than the minimum bitmap
     * height.
     *
     * @param scaleFactor     the desired scale factor
     * @param minBitmapHeight the minimum allowable bitmap height
     * @param srcBitmapHeight the original height of the image
     * @return a capped scale factor if the resulting scaled bitmap height is outside the minimum
     * bitmap height. Otherwise, return the desired scale factor.
     */
    protected float verifyScaleFactorMinBitmapHeight(float scaleFactor, int minBitmapHeight,
                                                     int srcBitmapHeight) {
        float minBitmapHeightF = minBitmapHeight;

        // If no minimum bitmap height is set, calculate one using the default min scale factor
        if (minBitmapHeightF == 0) {
            minBitmapHeightF = srcBitmapHeight * DEFAULT_MIN_SCALE_FACTOR;
        }
        // User is trying to scale smaller than the allowed minimum bitmap height
        if ((scaleFactor * srcBitmapHeight) < minBitmapHeightF) {
            scaleFactor = minBitmapHeightF / (float) srcBitmapHeight;
        }
        return scaleFactor;
    }

    /**
     * Determine if the resulting scaled bitmap width is less than the minimum bitmap
     * width.
     *
     * @param scaleFactor    the desired scale factor
     * @param minBitmapWidth the minimum allowable bitmap width
     * @param srcBitmapWidth the original width of the image
     * @return a capped scale factor if the resulting scaled bitmap width is outside the minimum
     * bitmap width. Otherwise, return the desired scale factor.
     */
    protected float verifyScaleFactorMinBitmapWidth(float scaleFactor, int minBitmapWidth,
                                                    int srcBitmapWidth) {
        float minBitmapWidthF = minBitmapWidth;

        // If no minimum bitmap width is set, calculate one using the default min scale factor
        if (minBitmapWidthF == 0) {
            minBitmapWidthF = srcBitmapWidth * DEFAULT_MIN_SCALE_FACTOR;
        }
        // User is trying to scale smaller than the allowed minimum bitmap width
        if ((scaleFactor * srcBitmapWidth) < minBitmapWidthF) {
            scaleFactor = minBitmapWidthF / (float) srcBitmapWidth;
        }
        return scaleFactor;
    }

    /**
     * Determine if the resulting scaled bitmap width is greater than the maximum bitmap
     * width.
     *
     * @param scaleFactor    the desired scale factor
     * @param maxBitmapWidth the maximum allowable bitmap width
     * @param srcBitmapWidth the original width of the image
     * @return a capped scale factor if the resulting scaled bitmap width is outside the maximum
     * bitmap width. Otherwise, return the desired scale factor.
     */
    protected float verifyScaleFactorMaxBitmapWidth(float scaleFactor, int maxBitmapWidth,
                                                    int srcBitmapWidth) {
        float maxBitmapWidthF = maxBitmapWidth;

        // If no maximum bitmap width is set, calculate one using the default min scale factor
        if (maxBitmapWidthF == 0) {
            maxBitmapWidthF = srcBitmapWidth * DEFAULT_MAX_SCALE_FACTOR;
        }
        // User is trying to scale larger than the allowed maximum bitmap width
        if ((scaleFactor * srcBitmapWidth) > maxBitmapWidthF) {
            scaleFactor = maxBitmapWidthF / (float) srcBitmapWidth;
        }
        return scaleFactor;
    }

    /**
     * Reset instance to state it was in immediately following construction. Usually this is invoked
     * because the current source {@link android.graphics.drawable.Drawable} in the
     * {@link PinchToZoomImageView} has been changed.
     */
    void reset() {
        mCurrentScaleFactor = 1.0f;
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
        state.currentScaleFactor = mCurrentScaleFactor;
    }

    /**
     * Re-apply a representation of internal state that had previously been generated by
     * {@link #onSaveInstanceState(PinchToZoomSavedState)}. This function will never be called with
     * a null state.
     *
     * @param state The frozen state that had previously been returned by
     *              {@link #onSaveInstanceState(PinchToZoomSavedState)}.
     */
    void onRestoreInstanceState(PinchToZoomSavedState state) {
        mCurrentScaleFactor = state.currentScaleFactor;
    }

    /**
     * An interface which allows an interested class to be notified of updates to the scale factor.
     */
    interface OnPinchToZoomScaleChangeListener {
        void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY, float focusX,
                            float focusY);

        void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX, float scaleY);
    }
}
