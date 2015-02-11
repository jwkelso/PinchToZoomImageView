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

import android.graphics.PointF;

/**
 * Encapsulates translation logic for {@link PinchToZoomImageView}.
 * <p/>
 * {@link #translate(float, float, PinchToZoomMatrixState)} translates the source bitmap. Usually,
 * the translation is limited such that the edges of the image do not cross the bounds of the view.
 * If the view has padding, and {@link PinchToZoomMatrixHelper#setCropToPadding(boolean)}is enabled,
 * the translation is limited to the edges of the view AFTER padding, meaning the translation is
 * inset inside the view. The translated image can be allowed to cross the edges of the view by a
 * finite amount through the use of
 * {@link PinchToZoomMatrixHelper#setTranslationExtra(int, int, int, int)}.
 * <p/>
 * {@link #checkTranslationBounds(PinchToZoomMatrixState)} checks to make sure that the current
 * source bitmap is not translated past the bounds of the view (adjusted for padding and translation
 * extra). Calculate any needed correction and notify {@link #mTranslationChangeListener} that the
 * translation needs to be adjusted by the correction amount.
 * <p/>
 * {@link #performInitialTranslation(PinchToZoomMatrixState)} will translate the source bitmap such
 * that it resides in the middle of the screen.
 */
class PinchToZoomTranslationHelper {
    // Recycled variables to avoid new instance creation
    protected final PointF mNeededCorrection = new PointF();

    protected OnPinchToZoomTranslationChangeListener mTranslationChangeListener;

    public PinchToZoomTranslationHelper(OnPinchToZoomTranslationChangeListener listener) {
        this.mTranslationChangeListener = listener;
    }

    /**
     * Translate the source bitmap. Check to see if the requested translation will cause the image
     * to be translated outside the edges of its containing view (accounting for padding and
     * translation extra). Determine if a correction to the translation is needed to bring it back
     * within the edges of the view. Notify the {@link #mTranslationChangeListener} that a
     * translation has occurred in the amount of the desired translation plus any needed
     * corrections.
     *
     * @param dx    desired translation on the X coordinate plane
     * @param dy    desired translation on the Y coordinate plane
     * @param state an object encapsulating information about the current state of the image matrix
     *              and the view that it supports
     */
    void translate(float dx, float dy, PinchToZoomMatrixState state) {
        updateTranslationStateForTranslation(dx, dy, state);

        PointF neededCorrection = getCorrectionsForTranslation(state);

        mTranslationChangeListener.onTranslationChanged(dx + neededCorrection.x, dy
                + neededCorrection.y);
    }

    /**
     * Translate the source bitmap such that it resides in the middle of the screen. In order for
     * the image to be centered, the translation needs to be adjusted for any paddingStart on both
     * the X and Y coordinate planes (regardless of whether
     * {@link PinchToZoomMatrixHelper#setCropToPadding(boolean)} is enabled or not. Notify
     * {@link #mTranslationChangeListener} that the initial translation has occurred.
     *
     * @param state an object encapsulating information about the current state of the image matrix
     *              and the view that it supports
     */
    void performInitialTranslation(PinchToZoomMatrixState state) {
        // Translate the bitmap to the middle of the screen
        float translateX = state.viewWidth - state.scaledBitmapWidth;
        translateX /= 2;
        // Adjust translation amount for paddingStart
        translateX -= state.paddingLeft;

        float translateY = state.viewHeight - state.scaledBitmapHeight;
        translateY /= 2;
        // Adjust translation amount for paddingStart
        translateY -= state.paddingTop;

        mTranslationChangeListener.onInitialTranslationPerformed(translateX, translateY);
    }

    /**
     * Check to make sure that the current source bitmap is not translated past the bounds of the
     * view (adjusted for padding and translation extra). Calculate any needed correction and notify
     * {@link #mTranslationChangeListener} that the translation needs to be adjusted by the
     * correction amount.
     *
     * @param state an object encapsulating information about the current state of the image matrix
     *              and the view that it supports
     */
    void checkTranslationBounds(PinchToZoomMatrixState state) {
        PointF neededCorrection = getCorrectionsForTranslation(state);
        mTranslationChangeListener.onTranslationBoundsChecked(neededCorrection.x,
                neededCorrection.y);
    }

    /**
     * Update the {@link PinchToZoomMatrixState} object to include additional translation.
     *
     * @param dx    desired translation on the X coordinate plane
     * @param dy    desired translation on the Y coordinate plane
     * @param state an object encapsulating information about the current state of the image matrix
     *              and the view that it supports
     */
    protected void updateTranslationStateForTranslation(float dx, float dy,
                                                        PinchToZoomMatrixState state) {
        state.translationX += dx;
        state.translationY += dy;
    }

    /**
     * Calculate any corrections needed to translate the source image back within the bounds of its
     * view (adjusted for padding and translation extra).
     *
     * @param state an object encapsulating information about the current state of the image matrix
     *              and the view that it supports.
     * @return a point whose {@link PointF#x} value represents the needed correction on the X
     * coordinate plane, and whose {@link PointF#y} value represents the needed correction on the Y
     * coordinate plane
     */
    protected PointF getCorrectionsForTranslation(PinchToZoomMatrixState state) {
        float neededCorrectionX = getCorrectionForTranslation(state.getXAxisState());
        float neededCorrectionY = getCorrectionForTranslation(state.getYAxisState());

        mNeededCorrection.set(neededCorrectionX, neededCorrectionY);
        return mNeededCorrection;
    }

    /**
     * Calculate the correction needed to translate a specific X or Y axis of the source image back
     * within the bound of its view (adjusted for padding and translation extra). Additionally, if
     * {@link PinchToZoomMatrixHelper#setPinAxesSmallerThanViewBounds(boolean)} is enabled, and the
     * image is smaller than the bound of the view, return a correction to center the image within
     * the view.
     *
     * @param state an object encapsulating information about the current state of an axis of the
     *              image matrix and the view that it supports
     * @return the needed correction on the specified axis of the coordinate plane, or 0 if no
     * correction is needed
     */
    protected float getCorrectionForTranslation(PinchToZoomMatrixState.AxisState state) {
        // Correction for translations that qualify for axis pinning
        // 0 if the translation doesn't qualify
        float correction = calculateTranslationForPinnedAxis(state);
        if (correction != 0) {
            return correction;
        }

        // Correction for translations that cross the left/top edge of the view
        // 0 if the translation doesn't cross the edge
        correction = calculateTranslationForFirstEdge(state);
        if (correction != 0) {
            return correction;
        }

        // Correction for translations that cross the right/bottom edge of the view
        // 0 if the translation doesn't cross the edge
        correction = calculateTranslationForSecondEdge(state);

        return correction;
    }

    /**
     * Determine if the image is currently translated past the second edge (right or bottom
     * depending on the axis) of its containing view. If it is past the edge, calculate the
     * translation amount necessary to bring it back within the bound of the view. If no translation
     * is needed, return 0.
     *
     * @param state an object encapsulating information about the current state of an axis of the
     *              image matrix and the view that it supports
     * @return the needed correction on the specified axis of the coordinate plane, or 0 if no
     * correction is needed
     */
    protected float calculateTranslationForSecondEdge(PinchToZoomMatrixState.AxisState state) {
        // Right / Bottom edge for collision check
        float secondEdge = findSecondEdgeForTranslationCorrection(state);

        // If our desired translation brings us past our right / bottom edge
        if (state.translation > secondEdge) {
            // Return the amount of translation needed to translate to left / top so that
            // we aren't outside the bounds of the view
            return secondEdge - state.translation;
        }

        // No correction needed
        return 0;
    }

    /**
     * Determine if the image is currently translated past the first edge (left or top depending on
     * the axis) of its containing view. If it is past the edge, calculate the translation amount
     * necessary to bring it back within the bound of the view. If no translation is needed, return
     * 0.
     *
     * @param state an object encapsulating information about the current state of an axis of the
     *              image matrix and the view that it supports
     * @return the needed correction on the specified axis of the coordinate plane, or 0 if no
     * correction is needed
     */
    protected float calculateTranslationForFirstEdge(PinchToZoomMatrixState.AxisState state) {
        // Left / Top edge for collision check
        float firstEdge = findFirstEdgeForTranslationCorrection(state);

        // If our desired translation brings us past our left / top edge
        if (state.translation < firstEdge) {
            // Return the amount of translation needed to translate to right / bottom so that
            // we aren't outside the bounds of the view
            return firstEdge - state.translation;
        }

        // No correction needed
        return 0;
    }

    /**
     * Determine if the image should be centered on the current axis of the containing view. In
     * order for an axis to require centering,
     * {@link PinchToZoomMatrixHelper#setPinAxesSmallerThanViewBounds(boolean)} must be enabled, and
     * the scaled image must be smaller than the size of its containing view. If the axis qualifies
     * for pinning, calculate the amount of translation necessary to center the image on this axis.
     * In order for the image to be centered, the translation needs to be adjusted for any
     * paddingStart on both the X and Y coordinate planes (regardless of whether
     * {@link PinchToZoomMatrixHelper#setCropToPadding(boolean)} is enabled or not.
     *
     * @param state an object encapsulating information about the current state of an axis of the
     *              image matrix and the view that it supports
     * @return the needed correction on the specified axis of the coordinate plane, or 0 if no
     * correction is needed
     */
    protected float calculateTranslationForPinnedAxis(PinchToZoomMatrixState.AxisState state) {
        // No correction if we aren't pinning the axis or if the scaled bitmap is bigger than the
        // bounds of the view.
        if (!state.pinAxesSmallerThanViewBounds
                || state.scaledBitmapDimensionSize > state.getAdjustedViewSize()) {
            return 0;
        }

        // Figure out the translation needed to center on the current axis
        float allowedTranslation = (state.viewDimensionSize - state.scaledBitmapDimensionSize) / 2;
        // Adjust translation amount for paddingStart
        allowedTranslation -= state.paddingStart;
        // Return amount needed to adjust current translation to center on the current axis
        return allowedTranslation - state.translation;
    }

    /**
     * Determine the X or Y coordinate of the first edge of the translation bound (depending on the
     * desired axis). Typically this value is 0, but must be adjusted if the image is scaled larger
     * than the size of the view (the image needs to translate past the edge of the view in order
     * to view all of it). It must also be corrected for padding and for
     * {@link PinchToZoomMatrixHelper#setTranslationExtra(int, int, int, int)}.
     *
     * @param state an object encapsulating information about the current state of an axis of the
     *              image matrix and the view that it supports
     * @return the X or Y coordinate of the first edge of the translation bound
     */
    protected float findFirstEdgeForTranslationCorrection(PinchToZoomMatrixState.AxisState state) {
        float firstEdge = 0;
        firstEdge -= state.getAdditionalTranslationForScaledBitmapSize();
        firstEdge -= state.translationExtraStart;
        // This correction is without regard to state.cropToPadding. This is because of oddities in
        // how the ImageView handles drawing the bitmap with padding.
        firstEdge -= state.paddingStart;

        return firstEdge;
    }

    /**
     * Determine the X or Y coordinate of the second edge of the translation bound (depending on the
     * desired axis). Typically this value is equal to the size of the view minus the scaled bitmap
     * size, but must be adjusted if the image is scaled larger than the size of the view (the image
     * needs to translate past the edge of the view in order to view all of it). Additionally, the
     * edge needs to be corrected for
     * {@link PinchToZoomMatrixHelper#setTranslationExtra(int, int, int, int)} and, if
     * {@link PinchToZoomMatrixHelper#setCropToPadding(boolean)} is enabled, it will need to be
     * corrected for padding.
     *
     * @param state an object encapsulating information about the current state of an axis of the
     *              image matrix and the view that it supports
     * @return the X or Y coordinate of the first edge of the translation bound
     */
    protected float findSecondEdgeForTranslationCorrection(PinchToZoomMatrixState.AxisState state) {
        float secondEdge = state.getAdjustedViewSize();
        secondEdge -= state.scaledBitmapDimensionSize;

        secondEdge += state.getAdditionalTranslationForScaledBitmapSize();
        secondEdge += state.translationExtraEnd;

        if (!state.cropToPadding) {
            secondEdge -= state.paddingEnd;
        }

        return secondEdge;
    }

    /**
     * An interface which allows an interested class to be notified of updates to the translation.
     */
    interface OnPinchToZoomTranslationChangeListener {
        void onTranslationChanged(float translationX, float translationY);

        void onTranslationBoundsChecked(float translationX, float translationY);

        void onInitialTranslationPerformed(float translationX, float translationY);
    }
}
