package com.jameskelso.android.widget;

import android.graphics.PointF;
import android.test.AndroidTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jkelso on 2/11/15.
 */
public class PinchToZoomTranslationHelperTest extends AndroidTestCase {
    private PinchToZoomTranslationHelper mHelper;
    private PinchToZoomTranslationHelper.OnPinchToZoomTranslationChangeListener mEmptyListener;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mEmptyListener = new PinchToZoomTranslationHelper.OnPinchToZoomTranslationChangeListener() {
            @Override
            public void onTranslationChanged(float translationX, float translationY) {

            }

            @Override
            public void onTranslationBoundsChecked(float translationX, float translationY) {

            }

            @Override
            public void onInitialTranslationPerformed(float translationX, float translationY) {

            }
        };
        mHelper = new PinchToZoomTranslationHelper(mEmptyListener);
    }

    @Override
    public void tearDown() throws Exception {
        mHelper = null;
        mEmptyListener = null;
        super.tearDown();
    }

    public void testConstructor() {
        assertNotNull(mHelper.mTranslationChangeListener);
    }

    public void testTranslateUpdateTranslationState() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomTranslationHelper(mEmptyListener) {
            @Override
            protected void updateTranslationStateForTranslation(float dx, float dy,
                                                                PinchToZoomMatrixState state) {
                latch.countDown();
            }
        };
        mHelper.translate(5, 5, new PinchToZoomMatrixState());
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("translate should call updateTranslationState", 0, latch.getCount());
    }

    public void testTranslateCallbackDx() throws InterruptedException {
        final float desiredDx = -7f;

        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomTranslationHelper(
                new PinchToZoomTranslationHelper.OnPinchToZoomTranslationChangeListener() {
                    @Override
                    public void onTranslationChanged(float translationX, float translationY) {
                        assertEquals(-14f, translationX);
                        latch.countDown();
                    }

                    @Override
                    public void onTranslationBoundsChecked(float translationX, float translationY) {

                    }

                    @Override
                    public void onInitialTranslationPerformed(float translationX, float translationY) {

                    }
                }) {
            @Override
            protected PointF getCorrectionsForTranslation(PinchToZoomMatrixState state) {
                return new PointF(desiredDx, 0);
            }
        };
        mHelper.translate(desiredDx, 0, new PinchToZoomMatrixState());
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("translate should call OnPinchToZoomTranslationChangeListener" +
                ".onTranslationChanged", 0, latch.getCount());
    }

    public void testTranslateCallbackDy() throws InterruptedException {
        final float desiredDy = -15f;

        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomTranslationHelper(
                new PinchToZoomTranslationHelper.OnPinchToZoomTranslationChangeListener() {
                    @Override
                    public void onTranslationChanged(float translationX, float translationY) {
                        assertEquals(-30f, translationY);
                        latch.countDown();
                    }

                    @Override
                    public void onTranslationBoundsChecked(float translationX, float translationY) {

                    }

                    @Override
                    public void onInitialTranslationPerformed(float translationX, float translationY) {

                    }
                }) {
            @Override
            protected PointF getCorrectionsForTranslation(PinchToZoomMatrixState state) {
                return new PointF(0, desiredDy);
            }
        };
        mHelper.translate(0, desiredDy, new PinchToZoomMatrixState());
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("translate should call OnPinchToZoomTranslationChangeListener" +
                ".onTranslationChanged", 0, latch.getCount());
    }

    public void testPerformInitialTranslationPaddingX() throws InterruptedException {
        final int viewWidth = 200;
        final int imageWidth = 100;
        final int paddingLeftRight = 30;

        final CountDownLatch latch = new CountDownLatch(1);

        mHelper.mTranslationChangeListener = new PinchToZoomTranslationHelper
                .OnPinchToZoomTranslationChangeListener() {
            @Override
            public void onTranslationChanged(float translationX, float translationY) {

            }

            @Override
            public void onTranslationBoundsChecked(float translationX,
                                                   float translationY) {

            }

            @Override
            public void onInitialTranslationPerformed(float translationX,
                                                      float translationY) {
                assertEquals(20f, translationX);
                latch.countDown();
            }
        };
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.viewWidth = viewWidth;
        state.paddingLeft = paddingLeftRight;
        state.scaledBitmapWidth = imageWidth;

        mHelper.performInitialTranslation(state);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("performInitialTranslate must call OnPinchToZoomTranslationChangeListener" +
                ".onInitialTranslationPerformed", 0, latch.getCount());
    }

    public void testPerformInitialTranslationPaddingY() throws InterruptedException {
        final int viewHeight = 200;
        final int imageHeight = 100;
        final int paddingTopBottom = 30;

        final CountDownLatch latch = new CountDownLatch(1);

        mHelper.mTranslationChangeListener = new PinchToZoomTranslationHelper
                .OnPinchToZoomTranslationChangeListener() {
            @Override
            public void onTranslationChanged(float translationX, float translationY) {

            }

            @Override
            public void onTranslationBoundsChecked(float translationX,
                                                   float translationY) {

            }

            @Override
            public void onInitialTranslationPerformed(float translationX,
                                                      float translationY) {
                assertEquals(20f, translationY);
                latch.countDown();
            }
        };
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.viewHeight = viewHeight;
        state.paddingTop = paddingTopBottom;
        state.scaledBitmapHeight = imageHeight;

        mHelper.performInitialTranslation(state);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("performInitialTranslate must call OnPinchToZoomTranslationChangeListener" +
                ".onInitialTranslationPerformed", 0, latch.getCount());
    }

    public void testCheckTranslationBoundsScaleX() throws InterruptedException {
        final float desiredDx = 27.5f;
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomTranslationHelper(new PinchToZoomTranslationHelper
                .OnPinchToZoomTranslationChangeListener() {
            @Override
            public void onTranslationChanged(float translationX, float translationY) {

            }

            @Override
            public void onTranslationBoundsChecked(float translationX, float translationY) {
                assertEquals(desiredDx, translationX);
                latch.countDown();
            }

            @Override
            public void onInitialTranslationPerformed(float translationX, float translationY) {

            }
        }) {
            @Override
            protected PointF getCorrectionsForTranslation(PinchToZoomMatrixState state) {
                return new PointF(desiredDx, 0);
            }
        };
        latch.await(100, TimeUnit.MILLISECONDS);
        mHelper.checkTranslationBounds(new PinchToZoomMatrixState());
        assertEquals("checkTranslationBounds must call OnPinchToZoomTranslationChangeListener" +
                ".onTranslationBoundsChecked.", 0, latch.getCount());
    }

    public void testCheckTranslationBoundsScaleY() throws InterruptedException {
        final float desiredDy = 32f;
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomTranslationHelper(new PinchToZoomTranslationHelper
                .OnPinchToZoomTranslationChangeListener() {
            @Override
            public void onTranslationChanged(float translationX, float translationY) {

            }

            @Override
            public void onTranslationBoundsChecked(float translationX, float translationY) {
                assertEquals(desiredDy, translationY);
                latch.countDown();
            }

            @Override
            public void onInitialTranslationPerformed(float translationX, float translationY) {

            }
        }) {
            @Override
            protected PointF getCorrectionsForTranslation(PinchToZoomMatrixState state) {
                return new PointF(0, desiredDy);
            }
        };
        latch.await(100, TimeUnit.MILLISECONDS);
        mHelper.checkTranslationBounds(new PinchToZoomMatrixState());
        assertEquals("checkTranslationBounds must call OnPinchToZoomTranslationChangeListener" +
                ".onTranslationBoundsChecked.", 0, latch.getCount());
    }

    public void testUpdateStateForTranslationX() {
        final float x = 5f;
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        mHelper.updateTranslationStateForTranslation(x, 0, state);
        assertEquals(x, state.translationX);
    }

    public void testUpdateStateForTranslationY() {
        final float y = 5f;
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        mHelper.updateTranslationStateForTranslation(0, y, state);
        assertEquals(y, state.translationY);
    }

    public void testGetCorrectionsForTranslationX() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.viewWidth = 500;
        state.scaledBitmapWidth = 500;
        state.translationX = 1;

        PointF result = mHelper.getCorrectionsForTranslation(state);
        assertEquals(-1f, result.x);
    }

    public void testGetCorrectionsForTranslationY() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.viewHeight = 500;
        state.scaledBitmapHeight = 500;
        state.translationY = 1;

        PointF result = mHelper.getCorrectionsForTranslation(state);
        assertEquals(-1f, result.y);
    }

    public void testGetCorrectionForTranslationPinnedAxis() {
        final float desiredCorrection = 5f;
        mHelper = new PinchToZoomTranslationHelper(mEmptyListener) {
            @Override
            protected float calculateTranslationForPinnedAxis(PinchToZoomMatrixState
                                                                      .AxisState state) {
                return desiredCorrection;
            }

            @Override
            protected float calculateTranslationForFirstEdge(PinchToZoomMatrixState
                                                                     .AxisState state) {
                return 6f;
            }

            @Override
            protected float calculateTranslationForSecondEdge(PinchToZoomMatrixState
                                                                      .AxisState state) {
                return 7f;
            }
        };

        float correction = mHelper.getCorrectionForTranslation(new PinchToZoomMatrixState()
                .getXAxisState());
        assertEquals(desiredCorrection, correction);
    }

    public void testGetCorrectionForTranslationFirstEdge() {
        final float desiredCorrection = 6f;
        mHelper = new PinchToZoomTranslationHelper(mEmptyListener) {
            @Override
            protected float calculateTranslationForPinnedAxis(PinchToZoomMatrixState
                                                                      .AxisState state) {
                return 0f;
            }

            @Override
            protected float calculateTranslationForFirstEdge(PinchToZoomMatrixState
                                                                     .AxisState state) {
                return desiredCorrection;
            }

            @Override
            protected float calculateTranslationForSecondEdge(PinchToZoomMatrixState
                                                                      .AxisState state) {
                return 7f;
            }
        };

        float correction = mHelper.getCorrectionForTranslation(new PinchToZoomMatrixState()
                .getXAxisState());
        assertEquals(desiredCorrection, correction);
    }

    public void testGetCorrectionForTranslationSecondEdge() {
        final float desiredCorrection = 7f;
        mHelper = new PinchToZoomTranslationHelper(mEmptyListener) {
            @Override
            protected float calculateTranslationForPinnedAxis(PinchToZoomMatrixState
                                                                      .AxisState state) {
                return 0f;
            }

            @Override
            protected float calculateTranslationForFirstEdge(PinchToZoomMatrixState
                                                                     .AxisState state) {
                return 0f;
            }

            @Override
            protected float calculateTranslationForSecondEdge(PinchToZoomMatrixState
                                                                      .AxisState state) {
                return desiredCorrection;
            }
        };

        float correction = mHelper.getCorrectionForTranslation(new PinchToZoomMatrixState()
                .getXAxisState());
        assertEquals(desiredCorrection, correction);
    }

    public void testCalculateTranslationForSecondEdge() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.viewDimensionSize = 500;
        state.scaledBitmapDimensionSize = 499;
        state.translation = 2f;
        assertEquals(-1f, mHelper.calculateTranslationForSecondEdge(state));
    }

    public void testCalculateTranslationForSecondEdgeNoCorrection() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.viewDimensionSize = 500;
        state.scaledBitmapDimensionSize = 499;
        state.translation = 1f;
        assertEquals(0f, mHelper.calculateTranslationForSecondEdge(state));
    }

    public void testCalculateTranslationForFirstEdge() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.translation = -1f;
        assertEquals(1f, mHelper.calculateTranslationForFirstEdge(state));
    }

    public void testCalculateTranslationForFirstEdgeNoCorrection() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.translation = 1f;
        assertEquals(0f, mHelper.calculateTranslationForFirstEdge(state));
    }

    public void testCalculateTranslationForPinnedAxis() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.pinAxesSmallerThanViewBounds = true;
        state.scaledBitmapDimensionSize = 250f;
        state.viewDimensionSize = 500;
        state.paddingStart = 50;
        state.translation = 5;
        float correction = mHelper.calculateTranslationForPinnedAxis(state);
        assertEquals(70f, correction);
    }

    public void testCalculateTranslationForPinnedAxisNoPinAxes() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.pinAxesSmallerThanViewBounds = false;
        state.scaledBitmapDimensionSize = 250f;
        state.viewDimensionSize = 500;
        float correction = mHelper.calculateTranslationForPinnedAxis(state);
        assertEquals(0f, correction);
    }

    public void testCalculateTranslationForPinnedAxisScaledEqualToView() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.pinAxesSmallerThanViewBounds = true;
        state.scaledBitmapDimensionSize = 250f;
        state.viewDimensionSize = 250;
        float correction = mHelper.calculateTranslationForPinnedAxis(state);
        assertEquals(0f, correction);
    }

    public void testFindFirstEdgeForTranslationCorrection() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.paddingStart = 50;
        state.translationExtraStart = 50;
        float edge = mHelper.findFirstEdgeForTranslationCorrection(state);
        assertEquals(-100f, edge);
    }

    public void testFindFirstEdgeForTranslationCorrectionCropToPadding() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.paddingStart = 50;
        state.translationExtraStart = 50;
        state.cropToPadding = true;
        float edge = mHelper.findFirstEdgeForTranslationCorrection(state);
        assertEquals(-150f, edge);
    }

    public void testFindSecondEdgeForTranslationCorrection() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.paddingEnd = 50;
        state.translationExtraEnd = 50;
        state.scaledBitmapDimensionSize = 500;
        state.viewDimensionSize = 500;
        float edge = mHelper.findSecondEdgeForTranslationCorrection(state);
        assertEquals(0f, edge);
    }

    public void testFindSecondEdgeForTranslationCorrectionCropToPadding() {
        PinchToZoomMatrixState.AxisState state = new PinchToZoomMatrixState.AxisState();
        state.paddingEnd = 50;
        state.translationExtraEnd = 50;
        state.scaledBitmapDimensionSize = 500;
        state.viewDimensionSize = 500;
        state.cropToPadding = true;
        float edge = mHelper.findSecondEdgeForTranslationCorrection(state);
        assertEquals(50f, edge);
    }
}
