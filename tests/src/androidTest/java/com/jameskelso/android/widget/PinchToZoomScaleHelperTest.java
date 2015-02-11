package com.jameskelso.android.widget;

import android.os.Bundle;
import android.test.AndroidTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jkelso on 2/11/15.
 */
public class PinchToZoomScaleHelperTest extends AndroidTestCase {
    private PinchToZoomScaleHelper mHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mHelper = new PinchToZoomScaleHelper(
                new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
                    @Override
                    public void onScaleChanged(int viewWidth, int viewHeight, float scaleX,
                                               float scaleY, float focusX, float focusY) {

                    }

                    @Override
                    public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                        float scaleY) {

                    }
                });
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mHelper = null;
    }

    public void testConstructor() {
        assertNotNull(mHelper.mScaleListener);
    }

    public void testScaleCurrentScaleFactor() {
        mHelper.mCurrentScaleFactor = 2f;

        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.scaleFactor = .5f;
        state.srcBitmapWidth = 500;
        state.srcBitmapHeight = 500;

        mHelper.scale(state);

        assertEquals(1f, mHelper.mCurrentScaleFactor);
    }

    public void testScaleMultiplierX() throws InterruptedException {
        mHelper.mCurrentScaleFactor = 2f;
        final PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.scaleFactor = .5f;
        state.srcBitmapWidth = 500;
        state.srcBitmapHeight = 500;

        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {
                assertEquals(state.scaleFactor, scaleX);
                latch.countDown();
            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {

            }
        };

        mHelper.scale(state);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("scale should call OnPinchToZoomScaleChangeListener.onScaleChanged.", 0,
                latch.getCount());
    }

    public void testScaleMultiplierY() throws InterruptedException {
        mHelper.mCurrentScaleFactor = 2f;
        final PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.scaleFactor = .5f;
        state.srcBitmapWidth = 500;
        state.srcBitmapHeight = 500;

        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {
                assertEquals(state.scaleFactor, scaleY);
                latch.countDown();
            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {

            }
        };

        mHelper.scale(state);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("scale should call OnPinchToZoomScaleChangeListener.onScaleChanged.", 0,
                latch.getCount());
    }

    public void testScaleFocusX() throws InterruptedException {
        final PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.scaleFactor = 1f;
        state.srcBitmapWidth = 500;
        state.srcBitmapHeight = 500;
        state.viewWidth = 200;
        state.viewHeight = 300;
        state.scaleFocusX = 5.5f;

        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {
                assertEquals(state.scaleFocusX, focusX);
                latch.countDown();
            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {

            }
        };

        mHelper.scale(state);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("scale should call OnPinchToZoomScaleChangeListener.onScaleChanged.", 0,
                latch.getCount());
    }

    public void testScaleFocusXEqualToView() throws InterruptedException {
        final PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.scaleFactor = 1f;
        state.srcBitmapWidth = 500;
        state.srcBitmapHeight = 500;
        state.viewWidth = 500;
        state.viewHeight = 600;

        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {
                assertEquals(state.viewWidth / 2f, focusX);
                latch.countDown();
            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {

            }
        };
    }

    public void testScaleFocusY() throws InterruptedException {
        final PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.scaleFactor = 1f;
        state.srcBitmapWidth = 500;
        state.srcBitmapHeight = 500;
        state.viewWidth = 200;
        state.viewHeight = 300;
        state.scaleFocusY = 5.5f;

        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {
                assertEquals(state.scaleFocusY, focusY);
                latch.countDown();
            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {

            }
        };

        mHelper.scale(state);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("scale should call OnPinchToZoomScaleChangeListener.onScaleChanged.", 0,
                latch.getCount());
    }

    public void testScaleFocusYEqualToView() throws InterruptedException {
        final PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.scaleFactor = 1f;
        state.srcBitmapWidth = 500;
        state.srcBitmapHeight = 500;
        state.viewWidth = 500;
        state.viewHeight = 600;

        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {
                assertEquals(state.viewWidth / 2f, focusY);
                latch.countDown();
            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {

            }
        };
    }

    public void testPerformInitialScaleSrcWidthZero() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapHeight = 1;
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {

            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {
                fail("Scaling should not be performed when source bitmap width is unknown.");
                latch.countDown();
            }
        };

        mHelper.performInitialScale(state);
        latch.await(100, TimeUnit.MILLISECONDS);
    }

    public void testPerformInitialScaleSrcHeightZero() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 1;
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {

            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {
                fail("Scaling should not be performed when source bitmap width is unknown.");
                latch.countDown();
            }
        };

        mHelper.performInitialScale(state);
        latch.await(100, TimeUnit.MILLISECONDS);
    }

    public void testPerformInitialScaleTallerThanWide() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 200;
        state.srcBitmapHeight = 400;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        mHelper.performInitialScale(state);
        assertEquals(2f, mHelper.mCurrentScaleFactor);
    }

    public void testPerformInitialScaleTallerThanWideCropToPadding() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 200;
        state.srcBitmapHeight = 400;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        state.cropToPadding = true;
        mHelper.performInitialScale(state);
        assertEquals(1.5f, mHelper.mCurrentScaleFactor);
    }

    public void testPerformInitialScaleTallerThanWideVerifySize() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 200;
        state.srcBitmapHeight = 400;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.maxBitmapHeight = 400;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        mHelper.performInitialScale(state);
        assertEquals(1f, mHelper.mCurrentScaleFactor);
    }

    public void testPerformInitialScaleTallerThanWideVerifySizeCropToPadding() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 200;
        state.srcBitmapHeight = 400;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.maxBitmapHeight = 500;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        state.cropToPadding = true;
        mHelper.performInitialScale(state);
        assertEquals(1.25f, mHelper.mCurrentScaleFactor);
    }

    public void testPerformInitialScaleWiderThanTall() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 400;
        state.srcBitmapHeight = 200;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        mHelper.performInitialScale(state);
        assertEquals(2f, mHelper.mCurrentScaleFactor);
    }

    public void testPerformInitialScaleWiderThanTallCropToPadding() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 400;
        state.srcBitmapHeight = 200;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        state.cropToPadding = true;
        mHelper.performInitialScale(state);
        assertEquals(1.5f, mHelper.mCurrentScaleFactor);
    }

    public void testPerformInitialScaleWiderThanTallVerifySize() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 400;
        state.srcBitmapHeight = 200;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.maxBitmapWidth = 400;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        mHelper.performInitialScale(state);
        assertEquals(1f, mHelper.mCurrentScaleFactor);
    }

    public void testPerformInitialScaleWiderThanTallVerifySizeCropToPadding() {
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 400;
        state.srcBitmapHeight = 200;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.maxBitmapWidth = 500;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        state.cropToPadding = true;
        mHelper.performInitialScale(state);
        assertEquals(1.25f, mHelper.mCurrentScaleFactor);
    }

    public void testPerformInitialScaleListenerFactorX() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {

            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {
                assertEquals(1.5f, scaleX);
                latch.countDown();
            }
        };

        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 400;
        state.srcBitmapHeight = 200;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        state.cropToPadding = true;
        mHelper.performInitialScale(state);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("performInitialScale should call " +
                "PinchToZoomOnScaleListener.onInitialScalePerformed", 0, latch.getCount());
    }

    public void testPerformInitialScaleListenerFactorY() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {

            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {
                assertEquals(1.5f, scaleY);
                latch.countDown();
            }
        };

        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.srcBitmapWidth = 400;
        state.srcBitmapHeight = 200;
        state.viewWidth = 800;
        state.viewHeight = 800;
        state.paddingLeft = 100;
        state.paddingRight = 100;
        state.paddingTop = 100;
        state.paddingBottom = 100;
        state.cropToPadding = true;
        mHelper.performInitialScale(state);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("performInitialScale should call " +
                "PinchToZoomOnScaleListener.onInitialScalePerformed", 0, latch.getCount());
    }

    public void testIsActualSizeZoomLevel() {
        assertTrue(mHelper.isActualSizeZoomLevel());
    }

    public void testIsActualSizeZoomLevelScaled() {
        mHelper.mCurrentScaleFactor = 2f;
        assertFalse(mHelper.isActualSizeZoomLevel());
    }

    public void testVerifyScaleFactorMaxHeight() {
        final float desiredFactor = 4.0f;
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.maxBitmapHeight = 2000;
        state.srcBitmapHeight = 500;
        float scaleFactorResult = mHelper.verifyScaleFactor(desiredFactor, state);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinHeight() {
        final float desiredFactor = .5f;
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.minBitmapHeight = 200;
        state.srcBitmapHeight = 400;
        float scaleFactorResult = mHelper.verifyScaleFactor(desiredFactor, state);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinWidth() {
        final float desiredFactor = .5f;
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.minBitmapWidth = 200;
        state.srcBitmapWidth = 400;
        float scaleFactorResult = mHelper.verifyScaleFactor(desiredFactor, state);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMaxWidth() {
        final float desiredFactor = 4.0f;
        PinchToZoomMatrixState state = new PinchToZoomMatrixState();
        state.maxBitmapWidth = 2000;
        state.srcBitmapWidth = 500;
        float scaleFactorResult = mHelper.verifyScaleFactor(desiredFactor, state);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMaxBitmapHeight() {
        final float desiredFactor = 4.0f;
        float scaleFactorResult = mHelper.verifyScaleFactorMaxBitmapHeight(desiredFactor, 2000,
                500);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMaxBitmapHeightCapped() {
        final float desiredFactor = 2.0f;
        float scaleFactorResult = mHelper.verifyScaleFactorMaxBitmapHeight(desiredFactor, 1, 1);
        assertEquals(1f, scaleFactorResult);
    }

    public void testVerifyScaleFactorMaxBitmapHeightDefault() {
        final float desiredFactor = 2.0f;
        float scaleFactorResult = mHelper.verifyScaleFactorMaxBitmapHeight(desiredFactor, 0, 500);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMaxBitmapHeightDefaultCapped() {
        final float desiredFactor = 4.0f;
        float scaleFactorResult = mHelper.verifyScaleFactorMaxBitmapHeight(desiredFactor, 0, 500);
        assertEquals(2f, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinBitmapHeight() {
        final float desiredFactor = .5f;
        float scaleFactorResult = mHelper.verifyScaleFactorMinBitmapHeight(desiredFactor, 200, 400);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinBitmapHeightCapped() {
        final float desiredFactor = .5f;
        float scaleFactorResult = mHelper.verifyScaleFactorMinBitmapHeight(desiredFactor, 1, 1);
        assertEquals(1f, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinBitmapHeightDefault() {
        final float desiredFactor = .25f;
        float scaleFactorResult = mHelper.verifyScaleFactorMinBitmapHeight(desiredFactor, 0, 500);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinBitmapHeightDefaultCapped() {
        final float desiredFactor = .2f;
        float scaleFactorResult = mHelper.verifyScaleFactorMinBitmapHeight(desiredFactor, 0, 500);
        assertEquals(.25f, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinBitmapWidth() {
        final float desiredFactor = .5f;
        float scaleFactorResult = mHelper.verifyScaleFactorMinBitmapWidth(desiredFactor, 200, 400);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinBitmapWidthCapped() {
        final float desiredFactor = .5f;
        float scaleFactorResult = mHelper.verifyScaleFactorMinBitmapWidth(desiredFactor, 1, 1);
        assertEquals(1f, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinBitmapWidthDefault() {
        final float desiredFactor = .25f;
        float scaleFactorResult = mHelper.verifyScaleFactorMinBitmapWidth(desiredFactor, 0, 500);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMinBitmapWidthDefaultCapped() {
        final float desiredFactor = .2f;
        float scaleFactorResult = mHelper.verifyScaleFactorMinBitmapWidth(desiredFactor, 0, 500);
        assertEquals(.25f, scaleFactorResult);
    }

    public void testVerifyScaleFactorMaxBitmapWidth() {
        final float desiredFactor = 4.0f;
        float scaleFactorResult = mHelper.verifyScaleFactorMaxBitmapWidth(desiredFactor, 2000, 500);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMaxBitmapWidthCapped() {
        final float desiredFactor = 2.0f;
        float scaleFactorResult = mHelper.verifyScaleFactorMaxBitmapWidth(desiredFactor, 1, 1);
        assertEquals(1f, scaleFactorResult);
    }

    public void testVerifyScaleFactorMaxBitmapWidthDefault() {
        final float desiredFactor = 2.0f;
        float scaleFactorResult = mHelper.verifyScaleFactorMaxBitmapWidth(desiredFactor, 0, 500);
        assertEquals(desiredFactor, scaleFactorResult);
    }

    public void testVerifyScaleFactorMaxBitmapWidthDefaultCapped() {
        final float desiredFactor = 4.0f;
        float scaleFactorResult = mHelper.verifyScaleFactorMaxBitmapWidth(desiredFactor, 0, 500);
        assertEquals(2f, scaleFactorResult);
    }

    public void testReset() {
        mHelper.mCurrentScaleFactor = 2f;
        mHelper.reset();
        assertEquals(1f, mHelper.mCurrentScaleFactor);
    }

    public void testOnSaveInstanceState() {
        final float scaleFactor = 37f;
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.mCurrentScaleFactor = scaleFactor;
        mHelper.onSaveInstanceState(state);
        assertEquals(scaleFactor, state.currentScaleFactor);
    }

    public void testOnRestoreInstanceState() {
        final float scaleFactor = 37f;
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.currentScaleFactor = scaleFactor;
        mHelper.onRestoreInstanceState(state);
        assertEquals(scaleFactor, mHelper.mCurrentScaleFactor);
    }
}
