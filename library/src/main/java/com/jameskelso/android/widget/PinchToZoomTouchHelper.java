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

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Encapsulates touch-handling logic for {@link com.jameskelso.android.widget.PinchToZoomImageView}.
 * This class should be set as the {@link android.view.View.OnTouchListener} for the
 * {@link com.jameskelso.android.widget.PinchToZoomImageView}.
 * <p/>
 * When {@link #onTouch(android.view.View, android.view.MotionEvent)} is invoked, the event is
 * passed to {@link #mScaleGestureDetector} which, if a scale gesture is detected, will invoke
 * {@link #onScaleBegin(android.view.ScaleGestureDetector)} which will block the subsequent
 * {@link #processMotionEvent} by setting {@link #mTouchMode} to {@link #STATE_ZOOM}. In
 * addition, {@link #onScale(android.view.ScaleGestureDetector)} will be invoked, which will notify
 * the {@link #mOnPinchToZoomTouchListener}.
 * <p/>
 * If the {@link #mScaleGestureDetector} doesn't detect a scale event, {@link #processMotionEvent}
 * will handle the touch. If the distance on the X and Y coordinate planes between the touch for
 * {@link #processMotionEventActionDown(android.graphics.PointF)} and the touch for
 * {@link #processMotionEventActionUp(android.view.View, android.graphics.PointF)} is less than
 * {@link #mPanThreshold}, the event will register as a tap (or click) and
 * {@link #performUserTap(android.view.View)} will be invoked. This, in turn, will invoke
 * {@link #mOnClickListener} if it is not null.
 * <p/>
 * If the distance on the X and Y coordinate planes between the touch for
 * {@link #processMotionEventActionDown(android.graphics.PointF)} and the touch for
 * {@link #processMotionEventActionUp(android.view.View, android.graphics.PointF)} is greater than
 * or equal to {@link #mPanThreshold}, it will register as a pan event and will notify
 * {@link #mOnPinchToZoomTouchListener}.
 * <p/>
 * Regardless of whether the motion event is a scale gesture, a pan gesture, or a simple tap,
 * {@link #mOnTouchListener} will be notified of the event if it is not null.
 * <p/>
 * The threshold that determines the difference between a tap on the view and a dragging gesture is
 * controlled by {@link #setPanThreshold(int)}. As the pan threshold increases, the user will need
 * to drag further on the view to initiate panning on the image. If this value is not set, it
 * defaults to an arguably reasonable value. Additionally, {@link #setPanThreshold(int)} will throw
 * an {@link java.lang.IllegalArgumentException} if the attempted value is less than 1.
 */
class PinchToZoomTouchHelper implements View.OnTouchListener,
        ScaleGestureDetector.OnScaleGestureListener {
    // Distance a user's finger must travel in order for a touch to not be considered a tap
    protected static final int DEFAULT_PAN_THRESHOLD = 3;

    // Touch states for tracking mode
    static final int STATE_NONE = 0;
    static final int STATE_PAN = 1;
    static final int STATE_ZOOM = 2;

    // Distance a user's touch must pan before we consider the touch to be a pan gesture rather than
    // a tap.
    protected int mPanThreshold = DEFAULT_PAN_THRESHOLD;

    // Track which touch state we are in
    protected int mTouchMode = STATE_NONE;

    // Track the first and last touches in a motion event
    protected PointF mFirstTouch;
    protected PointF mLastTouch;

    // Listeners which can be set on the ImageView
    protected View.OnTouchListener mOnTouchListener;
    protected View.OnClickListener mOnClickListener;
    protected OnPinchToZoomTouchListener mOnPinchToZoomTouchListener;

    // Detect a scale gesture i.e. pinch-to-zoom
    protected ScaleGestureDetector mScaleGestureDetector;

    PinchToZoomTouchHelper(Context context,
                           @NonNull OnPinchToZoomTouchListener onPinchToZoomListener) {
        this.mOnPinchToZoomTouchListener = onPinchToZoomListener;
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    /**
     * An implementation of {@link android.view.View.OnTouchListener}. The event is
     * passed to {@link #mScaleGestureDetector} which, if a scale gesture is detected, will invoke
     * {@link #onScaleBegin(android.view.ScaleGestureDetector)}.
     * <p/>
     * If the {@link #mScaleGestureDetector} doesn't detect a scale event,
     * {@link #processMotionEvent} will handle the touch.
     * <p/>
     * Regardless of whether the motion event is a scale gesture, a pan gesture, or a simple tap,
     * {@link #mOnTouchListener} will be notified of the event if it is not null.\
     *
     * @param v     the View that is the source of the touch event
     * @param event information regarding the motion event
     * @return a boolean indicating whether or not this touch was handled
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Feed the event to the scale gesture detector
        mScaleGestureDetector.onTouchEvent(event);

        processMotionEvent(v, event);

        // If we have a user-specified on touch event, go ahead and process it
        if (mOnTouchListener != null) {
            mOnTouchListener.onTouch(v, event);
        }
        return true;
    }

    /**
     * A callback from {@link #mScaleGestureDetector} that will notify the
     * {@link #mOnPinchToZoomTouchListener} that a scale event has occurred.
     *
     * @param detector the gesture detector that fired the event
     * @return a boolean indicating whether or not this event was handled
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        mOnPinchToZoomTouchListener.onPinchToZoom(detector.getScaleFactor(), detector.getFocusX(),
                detector.getFocusY());
        return true;
    }

    /**
     * A callback from {@link #mScaleGestureDetector} that will block subsequent calls to
     * {@link #processMotionEvent} by setting {@link #mTouchMode} to {@link #STATE_ZOOM}
     *
     * @param detector the gesture detector that fired the event
     * @return a boolean indicating whether or not this event was handled
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        mTouchMode = STATE_ZOOM;
        return true;
    }

    /**
     * A callback from {@link #mScaleGestureDetector} that is currently unused
     *
     * @param detector the gesture detector that fired the event
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // Not currently used
    }

    /**
     * Process touch events captured by the {@link View.OnTouchListener}. It will
     * then route the event to the appropriate method to handle based on the event's action type.
     *
     * @param v     the View that fired the event
     * @param event the MotionEvent captured by the touch listener
     */
    protected void processMotionEvent(View v, @NonNull MotionEvent event) {
        PointF currentTouch = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                processMotionEventActionDown(currentTouch);
                break;
            case MotionEvent.ACTION_MOVE:
                processMotionEventActionMove(currentTouch);
                break;
            case MotionEvent.ACTION_UP:
                processMotionEventActionUp(v, currentTouch);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                processMotionEventPointerUp(currentTouch);
                break;
        }
    }

    /**
     * Process {@link MotionEvent#ACTION_DOWN} events. Since the user has begun interaction with the
     * view, record the coordinates of the current touch in {@link #mFirstTouch} and
     * {@link #mLastTouch} for use in handling subsequent MotionEvents. Also, set
     * {@link #mTouchMode} to {@link #STATE_PAN} in case our next received event is
     * {@link MotionEvent#ACTION_MOVE}.
     *
     * @param touch the coordinates (within View bounds) where the user's finger touched the View
     */
    protected void processMotionEventActionDown(PointF touch) {
        mFirstTouch = touch;
        mLastTouch = touch;
        mTouchMode = STATE_PAN;
    }

    /**
     * Process {@link MotionEvent#ACTION_MOVE} events. If invoked following
     * {@link #processMotionEventActionDown(android.graphics.PointF)} or
     * another call to this method, determine how far the touch coordinates have moved from the last
     * detected coordinates ({@link #mLastTouch}). Notify the {@link #mOnPinchToZoomTouchListener}
     * that a pan event has occurred, and pass it the calculated distance from {@link #mLastTouch}.
     * Record the current touch as the last detected coordinates.
     *
     * @param touch the coordinates (within View bounds) where the user's finger touched the View
     */
    protected void processMotionEventActionMove(PointF touch) {
        // We don't process events for move if we aren't panning
        if (mTouchMode != STATE_PAN) {
            return;
        }

        float dx = touch.x - mLastTouch.x;
        float dy = touch.y - mLastTouch.y;

        mLastTouch = touch;

        mOnPinchToZoomTouchListener.onPan(dx, dy);
    }

    /**
     * Process {@link MotionEvent#ACTION_UP} events. Since the user has stopped interacting with the
     * View, set {@link #mTouchMode} to {@link #STATE_NONE}. Calculate the total distance the user's
     * finger has traveled since the initial
     * {@link #processMotionEventActionDown(android.graphics.PointF)}. If that distance is smaller
     * than the current {@link #mPanThreshold}, alert any {@link #mOnClickListener} that a tap
     * gesture has occurred.
     *
     * @param v     the View that generated the motion event being handled
     * @param touch the coordinates (within View bounds) where the user's finger touched the View
     */
    protected void processMotionEventActionUp(View v, PointF touch) {
        mTouchMode = STATE_NONE;

        // Calculate total x and y distance the user dragged
        float dx = Math.abs(touch.x - mFirstTouch.x);
        float dy = Math.abs(touch.y - mFirstTouch.y);

        // If the user didn't travel more than the pan threshold
        if (dx < mPanThreshold && dy < mPanThreshold) {
            // Process this action as a tap instead of a pan
            performUserTap(v);
        }
    }

    /**
     * Process {@link MotionEvent#ACTION_POINTER_UP} events. Reset the {@link #mTouchMode} to
     * {@link #STATE_NONE} since the user has stopped zooming.
     *
     * @param touch the coordinates (within View bounds) where the user's finger touched the View
     */
    protected void processMotionEventPointerUp(PointF touch) {
        // Reset the touch state. Nothing else to do
        mTouchMode = STATE_NONE;
    }

    /**
     * Alert {@link #mOnClickListener} that a tap gesture has occurred.
     *
     * @param v the view which generated the tap gesture
     */
    protected void performUserTap(View v) {
        if (mOnClickListener == null) {
            return;
        }
        mOnClickListener.onClick(v);
    }

    /**
     * Reset instance to state it was in immediately following construction. Usually this is invoked
     * because the current source {@link android.graphics.drawable.Drawable} in the
     * {@link com.jameskelso.android.widget.PinchToZoomImageView} has been changed.
     */
    void reset() {
        mFirstTouch = null;
        mLastTouch = null;
        mTouchMode = STATE_NONE;
    }

    /**
     * Set an {@link android.view.View.OnTouchListener} to be notified when touch events occur on a
     * view that is currently using this instance as an {@link android.view.View.OnTouchListener}.
     *
     * @param listener OnTouchListener to observe touch events
     */
    void setOnTouchListener(@Nullable View.OnTouchListener listener) {
        this.mOnTouchListener = listener;
    }

    /**
     * Set an {@link android.view.View.OnClickListener} to be notified when tap gestures occur on a
     * view that is currently using this instance as an {@link android.view.View.OnTouchListener}.
     *
     * @param listener OnClickListener to observe click events
     */
    void setOnClickListener(@Nullable View.OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    /**
     * Set the total distance a user's finger must travel from the initial
     * {@link #processMotionEventActionDown(android.graphics.PointF)} to the final
     * {@link #processMotionEventActionUp(android.view.View, android.graphics.PointF)} for a gesture
     * to be considered a tap rather than a pan. This value must be at least 1.
     *
     * @param panThreshold the new threshold for touch events
     */
    void setPanThreshold(int panThreshold) {
        if (panThreshold < 1) {
            throw new IllegalArgumentException("Pan threshold must be greater than 0.");
        }
        this.mPanThreshold = panThreshold;
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
        state.panThreshold = mPanThreshold;
        state.touchMode = mTouchMode;
        state.firstTouch = mFirstTouch;
        state.lastTouch = mLastTouch;
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
        mPanThreshold = state.panThreshold;
        mTouchMode = state.touchMode;
        mFirstTouch = state.firstTouch;
        mLastTouch = state.lastTouch;
    }

    /**
     * An interface which allows an interested class to be notified of pinch-to-zoom or pan
     * gestures.
     */
    interface OnPinchToZoomTouchListener {
        void onPinchToZoom(float desiredScaleFactor, float focusX, float focusY);

        void onPan(float dx, float dy);
    }
}
