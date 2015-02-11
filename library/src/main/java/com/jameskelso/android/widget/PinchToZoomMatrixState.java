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
 * A class that encapsulates information about the current state of the image matrix and view such
 * that the information can be passed into helper classes for use in calculations. This class holds
 * information for both the X-axis and the Y-axis. However, in order to generify some calculations
 * in the helper classes, it can be necessary to get information about a single axis.
 * <p/>
 * An axis can be retrieved by calling {@link #getXAxisState()} or {@link #getYAxisState()}. In
 * addition to encapsulating data elements related to an axis, the
 * {@link com.jameskelso.android.widget.PinchToZoomMatrixState.AxisState} also contains two methods
 * for calculating values derived from the current state of the axis.
 */
class PinchToZoomMatrixState {
    // Recycled objects to avoid instantiating new ones
    private final AxisState mAxisState = new AxisState();

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomScaleHelper}.mCurrentScaleFactor
     */
    float scaleFactor;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener}
     * .onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY, float focusX,
     * float focusY)
     */
    float scaleFocusX;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener}
     * .onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY, float focusX,
     * float focusY)
     */
    float scaleFocusY;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper#mMatrix}[Matrix.MTRANS_X]
     */
    float translationX;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper#mMatrix}[Matrix.MTRANS_Y]
     */
    float translationY;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.updateMatrixState()
     */
    float scaledBitmapWidth;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.updateMatrixState()
     */
    float scaledBitmapHeight;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomImageView}.getMeasuredWidth()
     */
    int viewWidth;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomImageView}.getMeasuredHeight()
     */
    int viewHeight;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mSrcBitmapSize.x
     */
    int srcBitmapWidth;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mSrcBitmapSize.y
     */
    int srcBitmapHeight;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mMaxBitmapSize.x
     */
    int maxBitmapWidth;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mMaxBitmapSize.y
     */
    int maxBitmapHeight;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mMinBitmapSize.x
     */
    int minBitmapWidth;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mMinBitmapSize.y
     */
    int minBitmapHeight;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mPadding.left
     */
    int paddingLeft;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mPadding.top
     */
    int paddingTop;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mPadding.right
     */
    int paddingRight;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mPadding.bottom
     */
    int paddingBottom;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mTranslationExtra.left
     */
    int translationExtraLeft;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mTranslationExtra.top
     */
    int translationExtraTop;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mTranslationExtra.right
     */
    int translationExtraRight;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mTranslationExtra.bottom
     */
    int translationExtraBottom;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mCropToPadding
     */
    boolean cropToPadding;

    /**
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixHelper}.mPinAxesSmallerThanViewBounds
     */
    boolean pinAxesSmallerThanViewBounds;

    /**
     * Retrieves the current state of the X-axis.
     * <p/>
     * The returned object is a recycled object. This means subsequent calls to
     * this method or {@link #getYAxisState()} will always return the same instance of
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixState.AxisState} with different values.
     *
     * @return The current state of the X-axis
     */
    public AxisState getXAxisState() {
        mAxisState.scaledBitmapDimensionSize = scaledBitmapWidth;
        mAxisState.translation = translationX;
        mAxisState.paddingStart = paddingLeft;
        mAxisState.paddingEnd = paddingRight;
        mAxisState.translationExtraStart = translationExtraLeft;
        mAxisState.translationExtraEnd = translationExtraRight;
        mAxisState.viewDimensionSize = viewWidth;
        mAxisState.cropToPadding = cropToPadding;
        mAxisState.pinAxesSmallerThanViewBounds = pinAxesSmallerThanViewBounds;
        return mAxisState;
    }

    /**
     * Retrieves the current state of the Y-axis.
     * <p/>
     * The returned object is a recycled object. This means subsequent calls to
     * {@link #getXAxisState()} or this method will always return the same instance of
     * {@link com.jameskelso.android.widget.PinchToZoomMatrixState.AxisState} with different values.
     *
     * @return The current state of the Y-axis
     */
    public AxisState getYAxisState() {
        mAxisState.scaledBitmapDimensionSize = scaledBitmapHeight;
        mAxisState.translation = translationY;
        mAxisState.paddingStart = paddingTop;
        mAxisState.paddingEnd = paddingBottom;
        mAxisState.translationExtraStart = translationExtraTop;
        mAxisState.translationExtraEnd = translationExtraBottom;
        mAxisState.viewDimensionSize = viewHeight;
        mAxisState.cropToPadding = cropToPadding;
        mAxisState.pinAxesSmallerThanViewBounds = pinAxesSmallerThanViewBounds;
        return mAxisState;
    }

    /**
     * A class that encapsulates information about the current state of either the X- or Y-axis of
     * an image matrix and parent view. This class also exposes two methods to further describe
     * the current state of the axis it represents.
     */
    static class AxisState {
        /**
         * scaledBitmapWidth or scaledBitmapHeight
         */
        float scaledBitmapDimensionSize;

        /**
         * translationX or translationY
         */
        float translation;

        /**
         * paddingLeft or paddingTop
         */
        int paddingStart;

        /**
         * paddingRight or paddingBottom
         */
        int paddingEnd;

        /**
         * translationExtraLeft or translationExtraTop
         */
        int translationExtraStart;

        /**
         * translationExtraRight or translationExtraBottom
         */
        int translationExtraEnd;

        /**
         * viewWidth or viewHeight
         */
        int viewDimensionSize;

        /**
         * cropToPadding (from containing class)
         */
        boolean cropToPadding;

        /**
         * pinAxesSmallerThanViewBounds (from containing class)
         */
        boolean pinAxesSmallerThanViewBounds;

        /**
         * Returns the size of the current View on the axis represented by this class (i.e. width
         * for the X-axis or height for the Y-axis). If cropToPadding is enabled, the view size
         * will not include any padding.
         *
         * @return The adjusted size of the current view. If cropToPadding is enabled, this value
         * will not include padding start or padding end.
         */
        int getAdjustedViewSize() {
            int adjustedViewSize = viewDimensionSize;
            if (cropToPadding) {
                adjustedViewSize -= paddingStart + paddingEnd;
            }
            return adjustedViewSize;
        }

        /**
         * Calculates the amount of any extra translation that should be allowed on this axis due
         * to the current size of the scaled bitmap (srcBitmapSize * scaleFactor) being larger than
         * the adjusted view size (see {@link #getAdjustedViewSize()}).
         *
         * @return The number of pixels by which the scaled bitmap size is larger than the adjusted
         * view size. Returns 0f if the scaled bitmap size is not larger than the adjusted view
         * size.
         */
        float getAdditionalTranslationForScaledBitmapSize() {
            int adjustedViewSize = getAdjustedViewSize();
            float pixels = 0;
            if (scaledBitmapDimensionSize > adjustedViewSize) {
                pixels = scaledBitmapDimensionSize - adjustedViewSize;
            }

            return pixels;
        }
    }
}
