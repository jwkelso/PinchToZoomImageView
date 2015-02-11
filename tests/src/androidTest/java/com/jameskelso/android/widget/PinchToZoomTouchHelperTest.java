package com.jameskelso.android.widget;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.test.AndroidTestCase;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jkelso on 2/11/15.
 */
public class PinchToZoomTouchHelperTest extends AndroidTestCase {
    private PinchToZoomTouchHelper.OnPinchToZoomTouchListener mEmptyTouchHelperListener;
    private View.OnClickListener mEmptyClickListener;
    private View.OnTouchListener mEmptyTouchListener;
    private PinchToZoomTouchHelper mHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mEmptyTouchHelperListener = new PinchToZoomTouchHelper.OnPinchToZoomTouchListener() {

            @Override
            public void onPinchToZoom(float desiredScaleFactor, float focusX,
                                      float focusY) {

            }

            @Override
            public void onPan(float dx, float dy) {

            }
        };
        mHelper = new PinchToZoomTouchHelper(getContext(), mEmptyTouchHelperListener);
        mEmptyClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        mEmptyTouchListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        };
    }

    @Override
    public void tearDown() throws Exception {
        mHelper = null;
        mEmptyTouchHelperListener = null;
        super.tearDown();
    }

    public void testConstructorTouchListener() {
        assertNotNull(mHelper.mOnPinchToZoomTouchListener);
    }

    public void testConstructorGestureDetector() {
        assertNotNull(mHelper.mScaleGestureDetector);
    }

    public void testOnTouchGestureDetector() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleGestureDetector = new ScaleGestureDetector(getContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener()) {
            @Override
            public boolean onTouchEvent(@NonNull MotionEvent e) {
                latch.countDown();
                return false;
            }
        };
        mHelper.onTouch(null, MotionEvent.obtain(0, 0, 0, 0, 0, 0));

        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("PinchToZoomTouchHelper should call ScaleGestureDetector.onTouchEvent() " +
                "during onTouch().", 0, latch.getCount());
    }

    public void testOnScale() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleGestureDetector = new ScaleGestureDetector(getContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener()) {
            @Override
            public float getScaleFactor() {
                return 0f;
            }

            @Override
            public float getFocusX() {
                return 0f;
            }

            @Override
            public float getFocusY() {
                return 0f;
            }
        };

        mHelper.mOnPinchToZoomTouchListener = new PinchToZoomTouchHelper
                .OnPinchToZoomTouchListener() {
            @Override
            public void onPinchToZoom(float desiredScaleFactor, float focusX, float focusY) {
                latch.countDown();
            }

            @Override
            public void onPan(float dx, float dy) {
                // Not used here
            }
        };
        mHelper.onScale(mHelper.mScaleGestureDetector);

        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals("PinchToZoomTouchHelper should call " +
                        "OnPinchToZoomTouchListener.onPinchToZoom() during onScale().", 0,
                latch.getCount());
    }

    public void testOnScaleBegin() {
        mHelper.onScaleBegin(null);
        assertEquals(PinchToZoomTouchHelper.STATE_ZOOM, mHelper.mTouchMode);
    }

    public void testOnScaleEnd() {
        mHelper.onScaleEnd(null);
        // Nothing to test here. Method doesn't do anything.
    }

    public void testProcessMotionEventSwitchActionDown() {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomTouchHelper(getContext(), mEmptyTouchHelperListener) {
            @Override
            protected void processMotionEventActionDown(PointF currentTouch) {
                latch.countDown();
            }

            @Override
            protected void processMotionEventActionUp(View v, PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }

            @Override
            protected void processMotionEventActionMove(PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }

            @Override
            protected void processMotionEventPointerUp(PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }
        };
        mHelper.processMotionEvent(null, MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0,
                0));

        assertEquals("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                "based on MotionEvent.getAction().", 0, latch.getCount());
    }

    public void testProcessMotionEventSwitchActionUp() {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomTouchHelper(getContext(), mEmptyTouchHelperListener) {
            @Override
            protected void processMotionEventActionDown(PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }

            @Override
            protected void processMotionEventActionUp(View v, PointF currentTouch) {
                latch.countDown();
            }

            @Override
            protected void processMotionEventActionMove(PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }

            @Override
            protected void processMotionEventPointerUp(PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }
        };
        mHelper.processMotionEvent(null, MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0, 0,
                0));

        assertEquals("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                "based on MotionEvent.getAction().", 0, latch.getCount());
    }

    public void testProcessMotionEventSwitchActionMove() {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomTouchHelper(getContext(), mEmptyTouchHelperListener) {
            @Override
            protected void processMotionEventActionDown(PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }

            @Override
            protected void processMotionEventActionUp(View v, PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }

            @Override
            protected void processMotionEventActionMove(PointF currentTouch) {
                latch.countDown();
            }

            @Override
            protected void processMotionEventPointerUp(PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }
        };
        mHelper.processMotionEvent(null, MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 0, 0,
                0));

        assertEquals("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                "based on MotionEvent.getAction().", 0, latch.getCount());
    }

    public void testProcessMotionEventSwitchPointerUp() {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomTouchHelper(getContext(), mEmptyTouchHelperListener) {
            @Override
            protected void processMotionEventActionDown(PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }

            @Override
            protected void processMotionEventActionUp(View v, PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }

            @Override
            protected void processMotionEventActionMove(PointF currentTouch) {
                fail("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                        "based on MotionEvent.getAction().");
            }

            @Override
            protected void processMotionEventPointerUp(PointF currentTouch) {
                latch.countDown();
            }
        };
        mHelper.processMotionEvent(null, MotionEvent.obtain(0, 0, MotionEvent.ACTION_POINTER_UP, 0,
                0, 0));

        assertEquals("PinchToZoomTouchHelper.processMotionEvent should call the correct method" +
                "based on MotionEvent.getAction().", 0, latch.getCount());
    }

    public void testProcessMotionEventActionDownFirstTouch() {
        final PointF motionDownPoint = new PointF(1, 2);
        mHelper.processMotionEventActionDown(motionDownPoint);
        assertSame(motionDownPoint, mHelper.mFirstTouch);
    }

    public void testProcessMotionEventActionDownLastTouch() {
        final PointF motionDownPoint = new PointF(2, 4);
        mHelper.processMotionEventActionDown(motionDownPoint);
        assertSame(motionDownPoint, mHelper.mLastTouch);
    }

    public void testProcessMotionEventActionDownTouchMode() {
        final PointF motionDownPoint = new PointF(1, 2);
        mHelper.processMotionEventActionDown(motionDownPoint);
        assertEquals(PinchToZoomTouchHelper.STATE_PAN, mHelper.mTouchMode);
    }

    public void testProcessMotionEventActionMoveNotPan() {
        final PointF motionMovePoint = new PointF(1, 2);
        mHelper.processMotionEventActionMove(motionMovePoint);
        assertNull("processMotionEventActionMove shouldn't process the event if mTouchMode is " +
                "not STATE_PAN", mHelper.mLastTouch);
    }

    public void testProcessMotionEventActionMoveLastTouch() {
        final PointF motionDownPoint = new PointF(1, 2);
        mHelper.processMotionEventActionDown(motionDownPoint);

        final PointF motionMovePoint = new PointF(2, 4);
        mHelper.processMotionEventActionMove(motionMovePoint);
        assertSame(motionMovePoint, mHelper.mLastTouch);
    }

    public void testProcessMotionEventActionMoveNotifyListener() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomTouchHelper.OnPinchToZoomTouchListener listener =
                new PinchToZoomTouchHelper.OnPinchToZoomTouchListener() {
                    @Override
                    public void onPinchToZoom(float desiredScaleFactor, float focusX,
                                              float focusY) {

                    }

                    @Override
                    public void onPan(float dx, float dy) {
                        latch.countDown();
                    }
                };
        mHelper.mOnPinchToZoomTouchListener = listener;

        final PointF motionDownPoint = new PointF(1, 2);
        mHelper.processMotionEventActionDown(motionDownPoint);

        final PointF motionMovePoint = new PointF(2, 4);
        mHelper.processMotionEventActionMove(motionMovePoint);

        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("PinchToZoomTouchHelper should notify OnPinchToZoomTouchListener on " +
                "processMotionEventDown()", 0, latch.getCount());
    }

    public void testProcessMotionEventActionMoveNotifyListenerVerifyDx() throws
            InterruptedException {
        final PointF motionDownPoint = new PointF(1, 2);
        final PointF motionMovePoint = new PointF(2, 4);

        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomTouchHelper.OnPinchToZoomTouchListener listener =
                new PinchToZoomTouchHelper.OnPinchToZoomTouchListener() {
                    @Override
                    public void onPinchToZoom(float desiredScaleFactor, float focusX,
                                              float focusY) {

                    }

                    @Override
                    public void onPan(float dx, float dy) {
                        if (dx == (motionMovePoint.x - motionDownPoint.x)) {
                            latch.countDown();
                        }
                    }
                };
        mHelper.mOnPinchToZoomTouchListener = listener;

        mHelper.processMotionEventActionDown(motionDownPoint);
        mHelper.processMotionEventActionMove(motionMovePoint);

        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("PinchToZoomTouchHelper calculated incorrect dx when notifiying " +
                "OnPinchToZoomTouchListener.onPan()", 0, latch.getCount());
    }

    public void testProcessMotionEventActionMoveNotifyListenerVerifyDy() throws
            InterruptedException {
        final PointF motionDownPoint = new PointF(1, 2);
        final PointF motionMovePoint = new PointF(2, 4);

        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomTouchHelper.OnPinchToZoomTouchListener listener =
                new PinchToZoomTouchHelper.OnPinchToZoomTouchListener() {
                    @Override
                    public void onPinchToZoom(float desiredScaleFactor, float focusX,
                                              float focusY) {

                    }

                    @Override
                    public void onPan(float dx, float dy) {
                        if (dy == (motionMovePoint.y - motionDownPoint.y)) {
                            latch.countDown();
                        }
                    }
                };
        mHelper.mOnPinchToZoomTouchListener = listener;

        mHelper.processMotionEventActionDown(motionDownPoint);
        mHelper.processMotionEventActionMove(motionMovePoint);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("PinchToZoomTouchHelper calculated incorrect dy when notifiying " +
                "OnPinchToZoomTouchListener.onPan()", 0, latch.getCount());
    }

    public void testProcessMotionEventActionUpTouchMode() {
        mHelper.mTouchMode = PinchToZoomTouchHelper.STATE_ZOOM;
        mHelper.mFirstTouch = new PointF(0, 0);
        mHelper.processMotionEventActionUp(null, new PointF(0, 0));
        assertEquals(PinchToZoomTouchHelper.STATE_NONE, mHelper.mTouchMode);
    }

    public void testProcessMotionEventActionUpCrossPanThresholdX() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latch.countDown();
            }
        };
        mHelper.setOnClickListener(listener);
        mHelper.mFirstTouch = new PointF(0, 0);
        mHelper.processMotionEventActionUp(null, new PointF(100, 0));
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals("PinchToZoomTouchHelper.processMotionEventActionUp() should not call " +
                "performUserTap() for touches that cross the pan threshold.", 1, latch.getCount());
    }

    public void testProcessMotionEventActionUpCrossPanThresholdY() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latch.countDown();
            }
        };
        mHelper.setOnClickListener(listener);
        mHelper.mFirstTouch = new PointF(0, 0);
        mHelper.processMotionEventActionUp(null, new PointF(0, 100));
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals("PinchToZoomTouchHelper.processMotionEventActionUp() should not call " +
                "performUserTap() for touches that cross the pan threshold.", 1, latch.getCount());
    }

    public void testProcessMotionEventActionUpUnderPanThreshold() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latch.countDown();
            }
        };
        mHelper.setOnClickListener(listener);
        mHelper.mFirstTouch = new PointF(0, 0);
        mHelper.processMotionEventActionUp(null, new PointF(0, 0));
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals("PinchToZoomTouchHelper.processMotionEventActionUp() should call " +
                "performUserTap() for touches under the pan threshold.", 0, latch.getCount());
    }

    public void testProcessMotionEventPointerUp() {
        mHelper.mTouchMode = PinchToZoomTouchHelper.STATE_ZOOM;
        mHelper.processMotionEventPointerUp(null);
        assertEquals(PinchToZoomTouchHelper.STATE_NONE, mHelper.mTouchMode);
    }

    public void testPerformUserTap() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                latch.countDown();
            }
        };

        mHelper.setOnClickListener(listener);
        mHelper.performUserTap(null);
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals("PinchToZoomTouchHelper.performUserTap should call OnClickListener when " +
                "not null", 0, latch.getCount());
    }

    public void testPerformUserTapNullListener() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                latch.countDown();
            }
        };

        mHelper.setOnClickListener(listener);
        mHelper.setOnClickListener(null);
        mHelper.performUserTap(null);
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals("PinchToZoomTouchHelper.performUserTap should call OnClickListener when " +
                "listener is null", 1, latch.getCount());
    }

    public void testResetFirstTouch() {
        mHelper.mFirstTouch = new PointF(5, 5);
        mHelper.reset();
        assertNull(mHelper.mFirstTouch);
    }

    public void testResetLastTouch() {
        mHelper.mFirstTouch = new PointF(4, 4);
        mHelper.reset();
        assertNull(mHelper.mLastTouch);
    }

    public void testResetTouchMode() {
        mHelper.mTouchMode = PinchToZoomTouchHelper.STATE_ZOOM;
        mHelper.reset();
        assertEquals(PinchToZoomTouchHelper.STATE_NONE, mHelper.mTouchMode);
    }

    public void testSetOnTouchListener() {
        mHelper.setOnTouchListener(mEmptyTouchListener);
        assertSame(mEmptyTouchListener, mHelper.mOnTouchListener);
    }

    public void testSetOnTouchListenerNull() {
        mHelper.setOnTouchListener(mEmptyTouchListener);
        mHelper.setOnTouchListener(null);
        assertNull("PinchToZoomTouchHelper should allow setting a null OnTouchListener",
                mHelper.mOnTouchListener);
    }

    public void testSetOnClickListener() {
        mHelper.setOnClickListener(mEmptyClickListener);
        assertSame(mEmptyClickListener, mHelper.mOnClickListener);
    }

    public void testSetOnClickListenerNull() {
        mHelper.setOnClickListener(mEmptyClickListener);
        mHelper.setOnClickListener(null);
        assertNull("PinchToZoomTouchHelper should allow setting a null OnClickListener",
                mHelper.mOnClickListener);
    }

    public void testSetPanThreshold() {
        mHelper.setPanThreshold(2);
        assertEquals(2, mHelper.mPanThreshold);
    }

    public void testSetPanThresholdIllegal() {
        try {
            mHelper.setPanThreshold(0);
            fail("PinchToZoomTouchHelper should not allow pan thresholds less than 1");
        } catch (IllegalArgumentException e) {
            // This is expected here
        }
    }

    public void testOnSaveInstanceStatePanThreshold() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.mPanThreshold = 5;
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mPanThreshold, state.panThreshold);
    }

    public void testOnSaveInstanceStateTouchMode() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.mTouchMode = PinchToZoomTouchHelper.STATE_ZOOM;
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mTouchMode, state.touchMode);
    }

    public void testOnSaveInstanceStateFirstTouch() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.mFirstTouch = new PointF(1f, 2f);
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mFirstTouch, state.firstTouch);
    }

    public void testOnSaveInstanceStateLastTouch() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.mLastTouch = new PointF(1f, 2f);
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mLastTouch, state.lastTouch);
    }

    public void testOnRestoreInstanceStatePanThreshold() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.panThreshold = 23;
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.panThreshold, mHelper.mPanThreshold);
    }

    public void testOnRestoreInstanceStateTouchMode() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.touchMode = PinchToZoomTouchHelper.STATE_PAN;
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.touchMode, mHelper.mTouchMode);
    }

    public void testOnRestoreInstanceStateFirstTouch() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.firstTouch = new PointF(1, 2);
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.firstTouch, mHelper.mFirstTouch);
    }

    public void testOnRestoreInstanceStateLastTouch() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.lastTouch = new PointF(1, 2);
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.lastTouch, mHelper.mLastTouch);
    }
}

