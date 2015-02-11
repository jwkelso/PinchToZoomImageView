package com.jameskelso.android.widget;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.test.AndroidTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jkelso on 2/11/15.
 */
public class PinchToZoomMatrixHelperTest extends AndroidTestCase {
    private PinchToZoomMatrixHelper mHelper;
    private PinchToZoomMatrixHelper.OnPinchToZoomMatrixChangeListener mEmptyMatrixListener;
    private PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener mEmptyScaleListener;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mEmptyMatrixListener = new PinchToZoomMatrixHelper.OnPinchToZoomMatrixChangeListener() {
            @Override
            public void onMatrixChanged(Matrix imageMatrix) {

            }
        };
        mHelper = new PinchToZoomMatrixHelper(mEmptyMatrixListener);

        mEmptyScaleListener = new PinchToZoomScaleHelper.OnPinchToZoomScaleChangeListener() {
            @Override
            public void onScaleChanged(int viewWidth, int viewHeight, float scaleX, float scaleY,
                                       float focusX, float focusY) {

            }

            @Override
            public void onInitialScalePerformed(int viewWidth, int viewHeight, float scaleX,
                                                float scaleY) {

            }
        };
    }

    public void tearDown() throws Exception {
        super.tearDown();
        mHelper = null;
        mEmptyMatrixListener = null;
        mEmptyScaleListener = null;
    }

    public void testConstructorMatrixChangedListener() {
        assertNotNull(mHelper.mMatrixChangedListener);
    }

    public void testConstructorScaleHelper() {
        assertNotNull(mHelper.mScaleHelper);
    }

    public void testConstructorTranslationHelper() {
        assertNotNull(mHelper.mTranslationHelper);
    }

    public void testPerformInitialScaleAndTranslateUpdateMatrixState() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomMatrixHelper(mEmptyMatrixListener) {
            @Override
            protected void updateMatrixState(int width, int height) {
                latch.countDown();
            }
        };
        mHelper.performInitialScaleAndTranslate(500, 500);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("performInitialScaleAndTranslate should call updateMatrixState", 0,
                latch.getCount());
    }

    public void testPerformInitialScaleAndTranslatePerformInitialScale()
            throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleHelper = new PinchToZoomScaleHelper(mHelper) {
            @Override
            void performInitialScale(PinchToZoomMatrixState state) {
                latch.countDown();
            }
        };
        mHelper.performInitialScaleAndTranslate(500, 500);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("performInitialScaleAndTranslate should call " +
                "PinchToZoomScaleHelper.performInitialScale", 0, latch.getCount());
    }

    public void testScaleUpdateMatrixState() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomMatrixHelper(mEmptyMatrixListener) {
            @Override
            protected void updateMatrixState(int width, int height) {
                latch.countDown();
            }
        };
        mHelper.scale(0, 0, 0, 0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("scale should call updateMatrixState", 0, latch.getCount());
    }

    public void testScaleUpdateMatrixScaleFactor() {
        final float scaleFactor = 5f;
        mHelper.scale(0, 0, scaleFactor, 0, 0);
        assertEquals(scaleFactor, mHelper.mMatrixState.scaleFactor);
    }

    public void testScaleUpdateMatrixFocusX() {
        final float focusX = 300f;
        mHelper.scale(0, 0, 0, focusX, 0);
        assertEquals(focusX, mHelper.mMatrixState.scaleFocusX);
    }

    public void testScaleUpdateMatrixFocusY() {
        final float focusY = 250f;
        mHelper.scale(0, 0, 0, 0, focusY);
        assertEquals(focusY, mHelper.mMatrixState.scaleFocusY);
    }

    public void testScaleHelperScale() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleHelper = new PinchToZoomScaleHelper(mHelper) {
            @Override
            void scale(PinchToZoomMatrixState state) {
                latch.countDown();
            }
        };
        mHelper.scale(0, 0, 0, 0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("scale should call PinchToZoomScaleHelper.scale", 0, latch.getCount());
    }

    public void testTranslateUpdateMatrixState() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomMatrixHelper(mEmptyMatrixListener) {
            @Override
            protected void updateMatrixState(int width, int height) {
                latch.countDown();
            }
        };
        mHelper.translate(0, 0, 0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("translate should call updateMatrixState", 0, latch.getCount());
    }

    public void testTranslateHelperTranslate() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mTranslationHelper = new PinchToZoomTranslationHelper(mHelper) {
            @Override
            void translate(float dx, float dy, PinchToZoomMatrixState state) {
                latch.countDown();
            }
        };
        mHelper.translate(0, 0, 0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("translate should call PinchToZoomTranslationHelper.translate", 0,
                latch.getCount());
    }

    public void testIsActualSizeZoomLevel() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleHelper = new PinchToZoomScaleHelper(mHelper) {
            @Override
            boolean isActualSizeZoomLevel() {
                latch.countDown();
                return false;
            }
        };
        mHelper.isActualSizeZoomLevel();
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("isActualZoomLevel should call PinchToZoomScaleHelper.isActualZoomLevel", 0,
                latch.getCount());
    }

    public void testResetMatrixIdentity() {
        mHelper.mMatrix.postScale(2, 2);
        mHelper.reset();
        assertTrue(mHelper.mMatrix.isIdentity());
    }

    public void testResetSourceBitmapWidth() {
        mHelper.mSrcBitmapSize.set(1, 2);
        mHelper.reset();
        assertEquals(0, mHelper.mSrcBitmapSize.x);
    }

    public void testResetSourceBitmapHeight() {
        mHelper.mSrcBitmapSize.set(1, 2);
        mHelper.reset();
        assertEquals(0, mHelper.mSrcBitmapSize.y);
    }

    public void testResetScaleHelper() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleHelper = new PinchToZoomScaleHelper(mHelper) {
            @Override
            void reset() {
                latch.countDown();
            }
        };
        mHelper.reset();
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("reset should call PinchToZoomScaleHelper.reset", 0, latch.getCount());
    }

    public void testResetUpdateMatrix() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mMatrixChangedListener =
                new PinchToZoomMatrixHelper.OnPinchToZoomMatrixChangeListener() {
                    @Override
                    public void onMatrixChanged(Matrix imageMatrix) {
                        latch.countDown();
                    }
                };
        mHelper.reset();
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("reset should call mMatrixChangedListener.onMatrixChanged", 0,
                latch.getCount());
    }

    public void testSetPaddingLeft() {
        final int padding = 1;
        mHelper.setPadding(padding, 0, 0, 0);
        assertEquals(padding, mHelper.mPadding.left);
    }

    public void testSetPaddingRight() {
        final int padding = 1;
        mHelper.setPadding(0, 0, padding, 0);
        assertEquals(padding, mHelper.mPadding.right);
    }

    public void testSetPaddingTop() {
        final int padding = 1;
        mHelper.setPadding(0, padding, 0, 0);
        assertEquals(padding, mHelper.mPadding.top);
    }

    public void testSetPaddingBottom() {
        final int padding = 1;
        mHelper.setPadding(0, 0, 0, padding);
        assertEquals(padding, mHelper.mPadding.bottom);
    }

    public void testSetCropToPadding() {
        mHelper.setCropToPadding(true);
        assertTrue(mHelper.mCropToPadding);
    }

    public void testSetSrcBitmapSizeWidth() {
        final int srcBitmapSize = 1;
        mHelper.setSrcBitmapSize(srcBitmapSize, 0);
        assertEquals(srcBitmapSize, mHelper.mSrcBitmapSize.x);
    }

    public void testSetSrcBitmapSizeHeight() {
        final int srcBitmapSize = 1;
        mHelper.setSrcBitmapSize(0, srcBitmapSize);
        assertEquals(srcBitmapSize, mHelper.mSrcBitmapSize.y);
    }

    public void testSetTranslationExtraLeft() {
        final int translationExtra = 1;
        mHelper.setTranslationExtra(translationExtra, 0, 0, 0);
        assertEquals(translationExtra, mHelper.mTranslationExtra.left);
    }

    public void testSetTranslationExtraTop() {
        final int translationExtra = 1;
        mHelper.setTranslationExtra(0, translationExtra, 0, 0);
        assertEquals(translationExtra, mHelper.mTranslationExtra.top);
    }

    public void testSetTranslationExtraRight() {
        final int translationExtra = 1;
        mHelper.setTranslationExtra(0, 0, translationExtra, 0);
        assertEquals(translationExtra, mHelper.mTranslationExtra.right);
    }

    public void testSetTranslationExtraBottom() {
        final int translationExtra = 1;
        mHelper.setTranslationExtra(0, 0, 0, translationExtra);
        assertEquals(translationExtra, mHelper.mTranslationExtra.bottom);
    }

    public void testSetTranslationExtraLeftIllegal() {
        try {
            final int translationExtra = -1;
            mHelper.setTranslationExtra(translationExtra, 0, 0, 0);
            fail("setTranslationExtra should not allow negative values.");
        } catch (IllegalArgumentException e) {
            // This test is supposed to throw this exception
        }
    }

    public void testSetTranslationExtraTopIllegal() {
        try {
            final int translationExtra = -1;
            mHelper.setTranslationExtra(0, translationExtra, 0, 0);
            fail("setTranslationExtra should not allow negative values.");
        } catch (IllegalArgumentException e) {
            // This test is supposed to throw this exception
        }
    }

    public void testSetTranslationExtraRightIllegal() {
        try {
            final int translationExtra = -1;
            mHelper.setTranslationExtra(0, 0, translationExtra, 0);
            fail("setTranslationExtra should not allow negative values.");
        } catch (IllegalArgumentException e) {
            // This test is supposed to throw this exception
        }
    }

    public void testSetTranslationExtraBottomIllegal() {
        try {
            final int translationExtra = -1;
            mHelper.setTranslationExtra(0, 0, 0, translationExtra);
            fail("setTranslationExtra should not allow negative values.");
        } catch (IllegalArgumentException e) {
            // This test is supposed to throw this exception
        }
    }

    public void testSetBitmapMinimumSizeWidth() {
        final int minimumSize = 2;
        mHelper.setBitmapMinimumSize(minimumSize, 1);
        assertEquals(minimumSize, mHelper.mBitmapMinSize.x);
    }

    public void testSetBitmapMinimumSizeHeight() {
        final int minimumSize = 2;
        mHelper.setBitmapMinimumSize(1, minimumSize);
        assertEquals(minimumSize, mHelper.mBitmapMinSize.y);
    }

    public void testSetBitmapMinimumSizeWidthIllegal() {
        try {
            final int minimumSize = 0;
            mHelper.setBitmapMinimumSize(minimumSize, 1);
            fail("setBitmapMinimumSize should not allow zero or less values.");
        } catch (IllegalArgumentException e) {
            // This test is supposed to throw this exception
        }
    }

    public void testSetBitmapMinimumSizeHeightIllegal() {
        try {
            final int minimumSize = 0;
            mHelper.setBitmapMinimumSize(1, minimumSize);
            fail("setBitmapMinimumSize should not allow zero or less values.");
        } catch (IllegalArgumentException e) {
            // This test is supposed to throw this exception
        }
    }

    public void testSetBitmapMaximumSizeWidth() {
        final int maxSize = 500;
        mHelper.setBitmapMaximumSize(maxSize, 1);
        assertEquals(maxSize, mHelper.mBitmapMaxSize.x);
    }

    public void testSetBitmapMaximumSizeHeight() {
        final int maxSize = 500;
        mHelper.setBitmapMaximumSize(1, maxSize);
        assertEquals(maxSize, mHelper.mBitmapMaxSize.y);
    }

    public void testSetPinAxesSmallerThanViewBounds() {
        mHelper.setPinAxesSmallerThanViewBounds(false);
        assertFalse(mHelper.mPinAxesSmallerThanViewBounds);
    }

    public void testUpdateMatrixStateScaledBitmapWidth() {
        final float scale = 2.0f;
        mHelper.mMatrix.postScale(scale, 1);

        final int sourceSize = 500;
        mHelper.mSrcBitmapSize.x = sourceSize;

        mHelper.updateMatrixState(0, 0);
        assertEquals(scale * sourceSize, mHelper.mMatrixState.scaledBitmapWidth);
    }

    public void testUpdateMatrixStateScaledBitmapHeight() {
        final float scale = 2.0f;
        mHelper.mMatrix.postScale(1, scale);

        final int sourceSize = 500;
        mHelper.mSrcBitmapSize.y = sourceSize;

        mHelper.updateMatrixState(0, 0);
        assertEquals(scale * sourceSize, mHelper.mMatrixState.scaledBitmapHeight);
    }

    public void testUpdateMatrixStateTranslationX() {
        final float translate = 55f;
        mHelper.mMatrix.postTranslate(translate, 0);
        mHelper.updateMatrixState(0, 0);
        assertEquals(translate, mHelper.mMatrixState.translationX);
    }

    public void testUpdateMatrixStateTranslationY() {
        final float translate = 55f;
        mHelper.mMatrix.postTranslate(0, translate);
        mHelper.updateMatrixState(0, 0);
        assertEquals(translate, mHelper.mMatrixState.translationY);
    }

    public void testUpdateMatrixStatePaddingLeft() {
        final int padding = 3;
        mHelper.setPadding(padding, 0, 0, 0);
        mHelper.updateMatrixState(0, 0);
        assertEquals(padding, mHelper.mMatrixState.paddingLeft);
    }

    public void testUpdateMatrixStatePaddingTop() {
        final int padding = 3;
        mHelper.setPadding(0, padding, 0, 0);
        mHelper.updateMatrixState(0, 0);
        assertEquals(padding, mHelper.mMatrixState.paddingTop);
    }

    public void testUpdateMatrixStatePaddingRight() {
        final int padding = 3;
        mHelper.setPadding(0, 0, padding, 0);
        mHelper.updateMatrixState(0, 0);
        assertEquals(padding, mHelper.mMatrixState.paddingRight);
    }

    public void testUpdateMatrixStatePaddingBottom() {
        final int padding = 3;
        mHelper.setPadding(0, 0, 0, padding);
        mHelper.updateMatrixState(0, 0);
        assertEquals(padding, mHelper.mMatrixState.paddingBottom);
    }

    public void testUpdateMatrixStateTranslationExtraLeft() {
        final int extra = 3;
        mHelper.setTranslationExtra(extra, 0, 0, 0);
        mHelper.updateMatrixState(0, 0);
        assertEquals(extra, mHelper.mMatrixState.translationExtraLeft);
    }

    public void testUpdateMatrixStateTranslationExtraTop() {
        final int extra = 3;
        mHelper.setTranslationExtra(0, extra, 0, 0);
        mHelper.updateMatrixState(0, 0);
        assertEquals(extra, mHelper.mMatrixState.translationExtraTop);
    }

    public void testUpdateMatrixStateTranslationExtraRight() {
        final int extra = 3;
        mHelper.setTranslationExtra(0, 0, extra, 0);
        mHelper.updateMatrixState(0, 0);
        assertEquals(extra, mHelper.mMatrixState.translationExtraRight);
    }

    public void testUpdateMatrixStateTranslationExtraBottom() {
        final int extra = 3;
        mHelper.setTranslationExtra(0, 0, 0, extra);
        mHelper.updateMatrixState(0, 0);
        assertEquals(extra, mHelper.mMatrixState.translationExtraBottom);
    }

    public void testUpdateMatrixStateViewWidth() {
        final int viewSize = 56;
        mHelper.updateMatrixState(viewSize, 0);
        assertEquals(viewSize, mHelper.mMatrixState.viewWidth);
    }

    public void testUpdateMatrixStateViewHeight() {
        final int viewSize = 56;
        mHelper.updateMatrixState(0, viewSize);
        assertEquals(viewSize, mHelper.mMatrixState.viewHeight);
    }

    public void testUpdateMatrixStateCropToPadding() {
        mHelper.mCropToPadding = true;
        mHelper.updateMatrixState(0, 0);
        assertTrue(mHelper.mMatrixState.cropToPadding);
    }

    public void testUpdateMatrixStatePinAxesSmallerThanViewBounds() {
        mHelper.mPinAxesSmallerThanViewBounds = true;
        mHelper.updateMatrixState(0, 0);
        assertTrue(mHelper.mMatrixState.pinAxesSmallerThanViewBounds);
    }

    public void testUpdateMatrixStateSrcBitmapWidth() {
        final int size = 63;
        mHelper.mSrcBitmapSize.x = size;
        mHelper.updateMatrixState(0, 0);
        assertEquals(size, mHelper.mMatrixState.srcBitmapWidth);
    }

    public void testUpdateMatrixStateSrcBitmapHeight() {
        final int size = 63;
        mHelper.mSrcBitmapSize.y = size;
        mHelper.updateMatrixState(0, 0);
        assertEquals(size, mHelper.mMatrixState.srcBitmapHeight);
    }

    public void testUpdateMatrixStateMinBitmapWidth() {
        final int size = 47;
        mHelper.mBitmapMinSize.x = size;
        mHelper.updateMatrixState(0, 0);
        assertEquals(size, mHelper.mMatrixState.minBitmapWidth);
    }

    public void testUpdateMatrixStateMinBitmapHeight() {
        final int size = 47;
        mHelper.mBitmapMinSize.y = size;
        mHelper.updateMatrixState(0, 0);
        assertEquals(size, mHelper.mMatrixState.minBitmapHeight);
    }

    public void testUpdateMatrixStateMaxBitmapWidth() {
        final int size = 33;
        mHelper.mBitmapMaxSize.x = size;
        mHelper.updateMatrixState(0, 0);
        assertEquals(size, mHelper.mMatrixState.maxBitmapWidth);
    }

    public void testUpdateMatrixStateMaxBitmapHeight() {
        final int size = 33;
        mHelper.mBitmapMaxSize.y = size;
        mHelper.updateMatrixState(0, 0);
        assertEquals(size, mHelper.mMatrixState.maxBitmapHeight);
    }

    public void testOnScaleChangedMatrixScaleX() {
        final float scale = 2.0f;
        mHelper.onScaleChanged(0, 0, scale, 0, 0, 0);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(scale, values[Matrix.MSCALE_X]);
    }

    public void testOnScaleChangedMatrixScaleY() {
        final float scale = 2.0f;
        mHelper.onScaleChanged(0, 0, 0, scale, 0, 0);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(scale, values[Matrix.MSCALE_Y]);
    }

    public void testOnScaleChangedCheckTranslationBounds() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mTranslationHelper = new PinchToZoomTranslationHelper(mHelper) {
            @Override
            void checkTranslationBounds(PinchToZoomMatrixState state) {
                latch.countDown();
            }
        };
        mHelper.onScaleChanged(0, 0, 0, 0, 0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onScaleChanged should call PinchToZoomTranslationHelper" +
                ".checkTranslationBounds", 0, latch.getCount());
    }

    public void testOnInitialScalePerformedScaleX() {
        final float scale = 2.0f;
        mHelper.onInitialScalePerformed(0, 0, scale, 0);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(scale, values[Matrix.MSCALE_X]);
    }

    public void testOnInitialScalePerformedScaleY() {
        final float scale = 2.0f;
        mHelper.onInitialScalePerformed(0, 0, 0, scale);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(scale, values[Matrix.MSCALE_Y]);
    }

    public void testOnInitialScalePerformedUpdateMatrixState() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomMatrixHelper(mEmptyMatrixListener) {
            @Override
            protected void updateMatrixState(int width, int height) {
                latch.countDown();
            }
        };
        mHelper.onInitialScalePerformed(0, 0, 0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onInitialScalePerformed should call updateMatrixState", 0,
                latch.getCount());
    }

    public void testOnInitialScalePerformedTranslatePostScale() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mTranslationHelper = new PinchToZoomTranslationHelper(mHelper) {
            @Override
            void performInitialTranslation(PinchToZoomMatrixState state) {
                latch.countDown();
            }
        };
        mHelper.onInitialScalePerformed(0, 0, 0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onInitialScalePerformed should call " +
                        "PinchToZoomTranslationHelper.performInitialTranslation", 0,
                latch.getCount());
    }

    public void testOnTranslationChangedTranslationX() {
        final float translation = 53.0f;
        mHelper.onTranslationChanged(translation, 0);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(translation, values[Matrix.MTRANS_X]);
    }

    public void testOnTranslationChangedTranslationY() {
        final float translation = 53.0f;
        mHelper.onTranslationChanged(0, translation);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(translation, values[Matrix.MTRANS_Y]);
    }

    public void testOnTranslationChangedUpdateMatrix() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mMatrixChangedListener =
                new PinchToZoomMatrixHelper.OnPinchToZoomMatrixChangeListener() {
                    @Override
                    public void onMatrixChanged(Matrix imageMatrix) {
                        latch.countDown();
                    }
                };
        mHelper.onTranslationChanged(0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onTranslationChanged should call mMatrixChangedListener.onMatrixChanged", 0,
                latch.getCount());
    }

    public void testOnTranslationChangedPostScaleTranslationX() {
        final float translation = 53.0f;
        mHelper.onTranslationBoundsChecked(translation, 0);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(translation, values[Matrix.MTRANS_X]);
    }

    public void testOnTranslationChangedPostScaleTranslationY() {
        final float translation = 53.0f;
        mHelper.onTranslationBoundsChecked(0, translation);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(translation, values[Matrix.MTRANS_Y]);
    }

    public void testOnTranslationChangedPostScaleUpdateMatrix() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mMatrixChangedListener =
                new PinchToZoomMatrixHelper.OnPinchToZoomMatrixChangeListener() {
                    @Override
                    public void onMatrixChanged(Matrix imageMatrix) {
                        latch.countDown();
                    }
                };
        mHelper.onTranslationBoundsChecked(0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onTranslationBoundsChecked should call " +
                "mMatrixChangedListener.onMatrixChanged", 0, latch.getCount());
    }

    public void testOnInitialTranslationPerformedTranslationX() {
        final float translation = 53.0f;
        mHelper.onInitialTranslationPerformed(translation, 0);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(translation, values[Matrix.MTRANS_X]);
    }

    public void testOnInitialTranslationPerformedTranslationY() {
        final float translation = 53.0f;
        mHelper.onInitialTranslationPerformed(0, translation);

        float[] values = new float[9];
        mHelper.mMatrix.getValues(values);
        assertEquals(translation, values[Matrix.MTRANS_Y]);
    }

    public void testOnInitialTranslationPerformedUpdateMatrix() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mMatrixChangedListener =
                new PinchToZoomMatrixHelper.OnPinchToZoomMatrixChangeListener() {
                    @Override
                    public void onMatrixChanged(Matrix imageMatrix) {
                        latch.countDown();
                    }
                };
        mHelper.onInitialTranslationPerformed(0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onTranslationBoundsChecked should call " +
                "mMatrixChangedListener.onMatrixChanged", 0, latch.getCount());
    }

    public void testOnSaveInstanceStateCropToPadding() {
        mHelper.mCropToPadding = true;
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.onSaveInstanceState(state);
        assertTrue(state.cropToPadding);
    }

    public void testOnSaveInstanceStatePinAxesSmallerThanViewBounds() {
        mHelper.mPinAxesSmallerThanViewBounds = true;
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.onSaveInstanceState(state);
        assertTrue(state.pinAxesSmallerThanViewBounds);
    }

    public void testOnSaveInstanceStatePadding() {
        mHelper.mPadding = new Rect(1, 2, 3, 4);
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mPadding, state.padding);
    }

    public void testOnSaveInstanceStateTranslationExtra() {
        mHelper.mTranslationExtra = new Rect(1, 2, 3, 4);
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mTranslationExtra, state.translationExtra);
    }

    public void testOnSaveInstanceStateSrcBitmapSize() {
        mHelper.mSrcBitmapSize = new Point(1, 2);
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mSrcBitmapSize, state.srcBitmapSize);
    }

    public void testOnSaveInstanceStateBitmapMinSize() {
        mHelper.mBitmapMinSize = new Point(1, 2);
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mBitmapMinSize, state.bitmapMinSize);
    }

    public void testOnSaveInstanceStateBitmapMaxSize() {
        mHelper.mBitmapMaxSize = new Point(1, 2);
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mBitmapMaxSize, state.bitmapMaxSize);
    }

    public void testOnSaveInstanceStateMatrix() {
        mHelper.mMatrix.postScale(2, 2);
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        mHelper.onSaveInstanceState(state);
        assertEquals(mHelper.mMatrix, state.matrix);
    }

    public void testOnSaveInstanceStateScaleHelper() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleHelper = new PinchToZoomScaleHelper(mEmptyScaleListener) {
            @Override
            void onSaveInstanceState(PinchToZoomSavedState state) {
                latch.countDown();
            }
        };
        mHelper.onSaveInstanceState(new PinchToZoomSavedState(new Bundle()));
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onSaveInstanceState should call PinchToZoomScaleHelper.onSaveInstanceState",
                0, latch.getCount());
    }

    public void testOnRestoreInstanceStateCropToPadding() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.cropToPadding = true;
        mHelper.onRestoreInstanceState(state);
        assertTrue(state.cropToPadding);
    }

    public void testOnRestoreInstanceStatePinAxesSmallerThanViewBounds() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.pinAxesSmallerThanViewBounds = true;
        mHelper.onRestoreInstanceState(state);
        assertTrue(state.pinAxesSmallerThanViewBounds);
    }

    public void testOnRestoreInstanceStatePadding() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.padding = new Rect(1, 2, 3, 4);
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.padding, mHelper.mPadding);
    }

    public void testOnRestoreInstanceStateTranslationExtra() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.translationExtra = new Rect(1, 2, 3, 4);
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.translationExtra, mHelper.mTranslationExtra);
    }

    public void testOnRestoreInstanceStateSrcBitmapSize() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.srcBitmapSize = new Point(1, 2);
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.srcBitmapSize, mHelper.mSrcBitmapSize);
    }

    public void testOnRestoreInstanceStateBitmapMinSize() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.bitmapMinSize = new Point(1, 2);
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.bitmapMinSize, mHelper.mBitmapMinSize);
    }

    public void testOnRestoreInstanceStateBitmapMaxSize() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.bitmapMaxSize = new Point(1, 2);
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.bitmapMaxSize, mHelper.mBitmapMaxSize);
    }

    public void testOnRestoreInstanceStateMatrix() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(new Bundle());
        state.matrix = new Matrix();
        state.matrix.postScale(2, 2);
        mHelper.onRestoreInstanceState(state);
        assertEquals(state.matrix, mHelper.mMatrix);
    }

    public void testOnRestoreInstanceStateScaleHelper() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mScaleHelper = new PinchToZoomScaleHelper(mEmptyScaleListener) {
            @Override
            void onRestoreInstanceState(PinchToZoomSavedState state) {
                latch.countDown();
            }
        };
        mHelper.onRestoreInstanceState(new PinchToZoomSavedState(new Bundle()));
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onSaveInstanceState should call PinchToZoomScaleHelper" +
                ".onRestoreInstanceState", 0, latch.getCount());
    }

    public void testCheckTranslationBoundsTranslateHelper() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper.mTranslationHelper = new PinchToZoomTranslationHelper(
                new PinchToZoomTranslationHelper.OnPinchToZoomTranslationChangeListener() {
                    @Override
                    public void onTranslationChanged(float translationX, float translationY) {

                    }

                    @Override
                    public void onTranslationBoundsChecked(float translationX, float translationY) {

                    }

                    @Override
                    public void onInitialTranslationPerformed(float translationX,
                                                              float translationY) {

                    }
                }) {
            @Override
            void checkTranslationBounds(PinchToZoomMatrixState state) {
                latch.countDown();
            }
        };
        mHelper.checkTranslationBounds(500, 500);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("checkTranslationBounds should call updateMatrixState", 0,
                latch.getCount());
    }

    public void testCheckTranslationBoundsUpdateMatrixState() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mHelper = new PinchToZoomMatrixHelper(mEmptyMatrixListener) {
            @Override
            protected void updateMatrixState(int width, int height) {
                latch.countDown();
            }
        };
        mHelper.checkTranslationBounds(500, 500);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("checkTranslationBounds should call updateMatrixState", 0,
                latch.getCount());
    }
}
