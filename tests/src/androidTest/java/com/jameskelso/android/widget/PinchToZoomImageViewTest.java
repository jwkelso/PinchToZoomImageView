package com.jameskelso.android.widget;

import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.test.AndroidTestCase;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.jameskelso.android.tests.R;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jkelso on 2/11/15.
 */
public class PinchToZoomImageViewTest extends AndroidTestCase {
    private View.OnTouchListener mEmptyTouchListener;
    private View.OnClickListener mEmptyClickListener;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mEmptyTouchListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        };
        mEmptyClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    @Override
    public void tearDown() throws Exception {
        mEmptyTouchListener = null;
        mEmptyClickListener = null;
        super.tearDown();
    }

    public void testResolveAttrsMinBitmapWidth() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.min_bmp_size);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(20, helper.mBitmapMinSize.x);
    }

    public void testResolveAttrsMinBitmapWidthDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mBitmapMinSize.x);
    }

    public void testResolveAttrsMinBitmapHeight() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.min_bmp_size);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(40, helper.mBitmapMinSize.y);
    }

    public void testResolveAttrsMinBitmapHeightDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mBitmapMinSize.y);
    }

    public void testResolveAttrsMaxBitmapWidth() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.max_bmp_size);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(200, helper.mBitmapMaxSize.x);
    }

    public void testResolveAttrsMaxBitmapWidthDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mBitmapMaxSize.x);
    }

    public void testResolveAttrsMaxBitmapHeight() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.max_bmp_size);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(400, helper.mBitmapMaxSize.y);
    }

    public void testResolveAttrsMaxBitmapHeightDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mBitmapMaxSize.y);
    }

    public void testResolveAttrsPanThreshold() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.pan_threshold);
        PinchToZoomTouchHelper helper = imgView.mTouchHelper;
        assertEquals(1000, helper.mPanThreshold);
    }

    public void testResolveAttrsPanThresholdDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomTouchHelper helper = imgView.mTouchHelper;
        assertEquals(PinchToZoomTouchHelper.DEFAULT_PAN_THRESHOLD, helper.mPanThreshold);
    }

    public void testResolveAttrsPinAxesSmallerThanViewBounds() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.pin_axes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertFalse(helper.mPinAxesSmallerThanViewBounds);
    }

    public void testResolveAttrsPinAxesSmallerThanViewBoundsDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertTrue(helper.mPinAxesSmallerThanViewBounds);
    }

    public void testResolveAttrsTransExtraLeft() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.extra_trans);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(1, helper.mTranslationExtra.left);
    }

    public void testResolveAttrsTransExtraLeftDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mTranslationExtra.left);
    }

    public void testResolveAttrsTransExtraTop() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.extra_trans);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(2, helper.mTranslationExtra.top);
    }

    public void testResolveAttrsTransExtraTopDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mTranslationExtra.top);
    }

    public void testResolveAttrsTransExtraRight() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.extra_trans);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(3, helper.mTranslationExtra.right);
    }

    public void testResolveAttrsTransExtraRightDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mTranslationExtra.right);
    }

    public void testResolveAttrsTransExtraBottom() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.extra_trans);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(4, helper.mTranslationExtra.bottom);
    }

    public void testResolveAttrsTransExtraBottomDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mTranslationExtra.bottom);
    }

    public void testResolveAttrsCropToPadding() {
        // Test is only valid for SDK 16 and up
        if (Build.VERSION.SDK_INT < 16) {
            return;
        }

        PinchToZoomImageView imgView = inflateFromResId(R.layout.crop_to_padding);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertTrue(helper.mCropToPadding);
    }

    // This test may fail on systems < level 16
    public void testResolveAttrsCropToPaddingDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertFalse(helper.mCropToPadding);
    }

    public void testResolveAttrsPaddingLeft() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.padding);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(5, helper.mPadding.left);
    }

    public void testResolveAttrsPaddingLeftDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mPadding.left);
    }

    public void testResolveAttrsPaddingTop() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.padding);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(3, helper.mPadding.top);
    }

    public void testResolveAttrsPaddingTopDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mPadding.top);
    }

    public void testResolveAttrsPaddingRight() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.padding);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(4, helper.mPadding.right);
    }

    public void testResolveAttrsPaddingRightDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mPadding.right);
    }

    public void testResolveAttrsPaddingBottom() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.padding);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(2, helper.mPadding.bottom);
    }

    public void testResolveAttrsPaddingBottomDefault() {
        PinchToZoomImageView imgView = inflateFromResId(R.layout.no_attributes);
        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(0, helper.mPadding.bottom);
    }

    public void testSetUpClickable() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        assertTrue(imgView.isClickable());
    }

    public void testSetUpScaleType() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        assertEquals(ImageView.ScaleType.MATRIX, imgView.getScaleType());
    }

    public void testSetUpTouchHelper() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        assertNotNull(imgView.mTouchHelper);
    }

    public void testSetUpMatrixHelper() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        assertNotNull(imgView.mMatrixHelper);
    }

    public void testSetScaleType() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        try {
            imgView.setScaleType(ImageView.ScaleType.MATRIX);
        } catch (IllegalStateException e) {
            fail("PinchToZoomImageView should allow the user to set scale type to MATRIX.");
        }
    }

    public void testSetScaleTypeXml() {
        try {
            inflateFromResId(R.layout.scale_type_matrix);
        } catch (InflateException e) {
            if (e.getCause().getCause() instanceof IllegalStateException) {
                fail("PinchToZoomImageView should allow the user to set scale type to MATRIX.");
            } else {
                throw e;
            }
        }
    }

    public void testSetScaleTypeIllegal() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        try {
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            fail("PinchToZoomImageView should not allow the user to set scale type to anything " +
                    "other than MATRIX.");
        } catch (IllegalArgumentException e) {
            // This is expected in this test.
        }
    }

    public void testSetScaleTypeXmlIllegal() {
        try {
            inflateFromResId(R.layout.scale_type_center_crop);
            fail("PinchToZoomImageView should not allow the user to set scale type to anything " +
                    "other than MATRIX.");
        } catch (InflateException e) {
            if (e.getCause().getCause() instanceof IllegalArgumentException) {
                // This is expected in this test
            } else {
                throw e;
            }
        }
    }

    public void testSetOnTouchListener() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setOnTouchListener(mEmptyTouchListener);
        assertNotNull(imgView.mTouchHelper.mOnTouchListener);
    }

    public void testSetOnTouchListenerNull() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setOnTouchListener(mEmptyTouchListener);
        imgView.setOnTouchListener(null);
        assertNull(imgView.mTouchHelper.mOnTouchListener);
    }

    public void testSetOnClickListener() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setOnClickListener(mEmptyClickListener);
        assertNotNull(imgView.mTouchHelper.mOnClickListener);
    }

    public void testSetOnClickListenerNull() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setOnClickListener(mEmptyClickListener);
        imgView.setOnClickListener(null);
        assertNull(imgView.mTouchHelper.mOnClickListener);
    }

    public void testOnMeasureInitialScale() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setImageResource(R.drawable.octopus);

        final int viewWidth = 2000;
        final int viewHeight = 2000;
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight,
                View.MeasureSpec.EXACTLY);

        imgView.measure(widthSpec, heightSpec);

        assertFalse(imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor == 1.0f);
    }

    public void testOnMeasureCheckTranslationBounds() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mMatrixHelper = new PinchToZoomMatrixHelper(imgView) {
            @Override
            void checkTranslationBounds(int width, int height) {
                latch.countDown();
            }
        };
        imgView.mRequiresTranslationBoundsCheck = true;
        imgView.measure(0, 0);
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onMeasure must call PinchToZoomMatrixHelper.checkTranslationBounds", 0,
                latch.getCount());
    }

    public void testOnMeasureCheckTranslationBoundsUnset() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mRequiresTranslationBoundsCheck = true;
        imgView.measure(0, 0);
        assertFalse(imgView.mRequiresTranslationBoundsCheck);
    }

    public void testOnMeasureNoImage() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());

        final int viewWidth = 2032;
        final int viewHeight = 2032;
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight,
                View.MeasureSpec.EXACTLY);

        imgView.measure(widthSpec, heightSpec);

        assertEquals(1.0f, imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor);
    }

    public void testOnMeasureCaptureDrawableState() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext()) {
            @Override
            protected void captureDrawableState() {
                latch.countDown();
            }
        };
        imgView.setImageResource(R.drawable.octopus);

        final int viewWidth = 2032;
        final int viewHeight = 2032;
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight,
                View.MeasureSpec.EXACTLY);

        imgView.measure(widthSpec, heightSpec);

        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onMeasure should call captureDrawableState when zoom level is 1.0", 0,
                latch.getCount());
    }

    public void testSetPaddingLeft() {
        final int padding = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPadding(padding, 0, 0, 0);

        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(padding, helper.mPadding.left);
    }

    public void testSetPaddingTop() {
        final int padding = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPadding(0, padding, 0, 0);

        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(padding, helper.mPadding.top);
    }

    public void testSetPaddingRight() {
        final int padding = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPadding(0, 0, padding, 0);

        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(padding, helper.mPadding.right);
    }

    public void testSetPaddingBottom() {
        final int padding = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPadding(0, 0, 0, padding);

        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(padding, helper.mPadding.bottom);
    }

    public void testSetPaddingRelativeLeft() {
        // Test is only valid for SDK 17 and up
        if (Build.VERSION.SDK_INT < 17) {
            return;
        }

        final int padding = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPaddingRelative(padding, 0, 0, 0);

        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(padding, helper.mPadding.left);
    }

    public void testSetPaddingRelativeTop() {
        // Test is only valid for SDK 17 and up
        if (Build.VERSION.SDK_INT < 17) {
            return;
        }

        final int padding = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPaddingRelative(0, padding, 0, 0);

        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(padding, helper.mPadding.top);
    }

    public void testSetPaddingRelativeRight() {
        // Test is only valid for SDK 17 and up
        if (Build.VERSION.SDK_INT < 17) {
            return;
        }

        final int padding = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPaddingRelative(0, 0, padding, 0);

        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(padding, helper.mPadding.right);
    }

    public void testSetPaddingRelativeBottom() {
        // Test is only valid for SDK 17 and up
        if (Build.VERSION.SDK_INT < 17) {
            return;
        }

        final int padding = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPaddingRelative(0, 0, 0, padding);

        PinchToZoomMatrixHelper helper = imgView.mMatrixHelper;
        assertEquals(padding, helper.mPadding.bottom);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void testSetCropToPadding() {
        // Test is only valid for SDK 17 and up
        if (Build.VERSION.SDK_INT < 16) {
            return;
        }

        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setCropToPadding(true);
        assertTrue(imgView.getCropToPadding());
    }

    public void testSetImageResource() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor = 2f;
        imgView.setImageResource(R.drawable.octopus);

        assertTrue("Scale factor should be reset after setting a source image.",
                imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor == 1f);
    }

    public void testSetImageURI() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor = 2f;
        Uri path = Uri.parse(
                "android.resource://" + getContext().getPackageName() + "/drawable/octopus");
        imgView.setImageURI(path);

        assertTrue("Scale factor should be reset after setting a source image.",
                imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor == 1f);
    }

    public void testSetImageDrawable() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor = 2f;
        Drawable d = getContext().getResources().getDrawable(R.drawable.octopus);
        imgView.setImageDrawable(d);

        assertTrue("Scale factor should be reset after setting a source image.",
                imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor == 1f);
    }

    public void testResetMatrixHelper() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor = 2f;
        imgView.reset();

        assertTrue("PinchToZoomMatrixHelper should be reset after setting a source image.",
                imgView.mMatrixHelper.mScaleHelper.mCurrentScaleFactor == 1f);
    }

    public void testResetTouchHelper() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mTouchHelper.mFirstTouch = new PointF(1, 1);
        imgView.reset();

        assertNull("PinchToZoomTouchHelper should be reset after setting a source image.",
                imgView.mTouchHelper.mFirstTouch);
    }

    public void testCaptureDrawableStateSourceBitmapWidth() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setImageResource(R.drawable.octopus);
        imgView.captureDrawableState();

        // R.drawable.octopus is 1016 pixels wide
        assertEquals(getDpFromPx(1016), imgView.mMatrixHelper.mSrcBitmapSize.x);
    }

    public void testCaptureDrawableStateSourceBitmapHeight() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setImageResource(R.drawable.octopus);
        imgView.captureDrawableState();

        // R.drawable.octopus is 673 pixels wide
        assertEquals(getDpFromPx(673), imgView.mMatrixHelper.mSrcBitmapSize.y);
    }

    public void testSetTranslationExtraLeft() {
        final int translationExtra = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setTranslationExtra(translationExtra, 0, 0, 0);
        assertEquals(translationExtra, imgView.mMatrixHelper.mTranslationExtra.left);
    }

    public void testSetTranslationExtraTop() {
        final int translationExtra = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setTranslationExtra(0, translationExtra, 0, 0);
        assertEquals(translationExtra, imgView.mMatrixHelper.mTranslationExtra.top);
    }

    public void testSetTranslationExtraRight() {
        final int translationExtra = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setTranslationExtra(0, 0, translationExtra, 0);
        assertEquals(translationExtra, imgView.mMatrixHelper.mTranslationExtra.right);
    }

    public void testSetTranslationExtraBottom() {
        final int translationExtra = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setTranslationExtra(0, 0, 0, translationExtra);
        assertEquals(translationExtra, imgView.mMatrixHelper.mTranslationExtra.bottom);
    }

    public void testSetBitmapMinimumSizePixelsWidth() {
        final int size = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setBitmapMinimumSizePixels(size, 1);
        assertEquals(size, imgView.mMatrixHelper.mBitmapMinSize.x);
    }

    public void testSetBitmapMinimumSizePixelsHeight() {
        final int size = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setBitmapMinimumSizePixels(1, size);
        assertEquals(size, imgView.mMatrixHelper.mBitmapMinSize.y);
    }

    public void testSetBitmapMaximumSizePixelsWidth() {
        final int size = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setBitmapMaximumSizePixels(size, 1);
        assertEquals(size, imgView.mMatrixHelper.mBitmapMaxSize.x);
    }

    public void testSetBitmapMaximumSizePixelsHeight() {
        final int size = 5;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setBitmapMaximumSizePixels(1, size);
        assertEquals(size, imgView.mMatrixHelper.mBitmapMaxSize.y);
    }

    public void testSetPanThreshold() {
        final int threshold = 22;
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPanThreshold(threshold);
        assertEquals(threshold, imgView.mTouchHelper.mPanThreshold);
    }

    public void testPinAxesSmallerThanViewBounds() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.setPinAxesSmallerThanViewBounds(false);
        assertFalse(imgView.mMatrixHelper.mPinAxesSmallerThanViewBounds);
    }

    public void testOnPinchToZoom() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mMatrixHelper = new PinchToZoomMatrixHelper(imgView) {
            @Override
            public void scale(int viewMeasuredWidth, int viewMeasuredHeight,
                              float desiredScaleFactor, float focusX, float focusY) {
                latch.countDown();
            }
        };
        imgView.onPinchToZoom(0f, 0, 0);

        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals(
                "PinchToZoomImageView.onPinchToZoom() should call PinchToZoomMatrixHelper.scale()",
                0, latch.getCount());
    }

    public void testOnPan() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mMatrixHelper = new PinchToZoomMatrixHelper(imgView) {
            @Override
            public void translate(int focusX, int focusY, float dx, float dy) {
                latch.countDown();
            }
        };
        imgView.onPan(0f, 0f);

        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals(
                "PinchToZoomImageView.onPan() should call PinchToZoomMatrixHelper.translate()",
                0, latch.getCount());
    }

    public void testOnMatrixChanged() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext()) {
            @Override
            public void setImageMatrix(Matrix matrix) {
                latch.countDown();
            }
        };
        imgView.onMatrixChanged(null);

        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("PinchToZoomImageView.onMatrixChanged() should call " +
                "PinchToZoomImageView.setImageMatrix()", 0, latch.getCount());
    }

    public void testOnSaveInstanceState() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        Parcelable instanceState = imgView.onSaveInstanceState();
        assertNotNull(instanceState);
    }

    public void testOnSaveInstanceStateMatrixHelper() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mMatrixHelper = new PinchToZoomMatrixHelper(imgView) {
            @Override
            void onSaveInstanceState(PinchToZoomSavedState state) {
                latch.countDown();
            }
        };
        imgView.onSaveInstanceState();
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onSaveInstanceState must call PinchToZoomMatrixHelper.onSaveInstanceState", 0,
                latch.getCount());
    }

    public void testOnSaveInstanceStateTouchHelper() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mTouchHelper = new PinchToZoomTouchHelper(getContext(), imgView) {
            @Override
            void onSaveInstanceState(PinchToZoomSavedState state) {
                latch.countDown();
            }
        };
        imgView.onSaveInstanceState();
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onSaveInstanceState must call PinchToZoomTouchHelper.onSaveInstanceState", 0,
                latch.getCount());
    }

    public void testOnRestoreInstanceStateRequiresTranslationBoundsCheck() {
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.onRestoreInstanceState(imgView.onSaveInstanceState());
        assertTrue(imgView.mRequiresTranslationBoundsCheck);
    }

    public void testOnRestoreInstanceStateMatrixHelper() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mMatrixHelper = new PinchToZoomMatrixHelper(imgView) {
            @Override
            void onRestoreInstanceState(PinchToZoomSavedState state) {
                latch.countDown();
            }
        };
        imgView.onRestoreInstanceState(imgView.onSaveInstanceState());
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onSaveInstanceState must call PinchToZoomMatrixHelper.onRestoreInstanceState",
                0, latch.getCount());
    }

    public void testOnRestoreInstanceStateTouchHelper() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
        imgView.mTouchHelper = new PinchToZoomTouchHelper(getContext(), imgView) {
            @Override
            void onRestoreInstanceState(PinchToZoomSavedState state) {
                latch.countDown();
            }
        };
        imgView.onRestoreInstanceState(imgView.onSaveInstanceState());
        latch.await(100, TimeUnit.MILLISECONDS);
        assertEquals("onSaveInstanceState must call PinchToZoomTouchHelper.onRestoreInstanceState",
                0, latch.getCount());
    }

    public void testOnRestoreInstanceStateIllegal() {
        try {
            PinchToZoomImageView imgView = new PinchToZoomImageView(getContext());
            imgView.onRestoreInstanceState(new Rect());
            fail("onRestoreInstanceState should not allow types other than PinchToZoomSavedState");
        } catch (IllegalArgumentException e) {
            // This is supposed to happen
        }
    }

    private int getDpFromPx(int px) {
        return Math.round(px * getContext().getResources().getDisplayMetrics().density);
    }

    private PinchToZoomImageView inflateFromResId(int resourceId) {
        View inflated = LayoutInflater.from(getContext()).inflate(resourceId, null, false);
        if (inflated instanceof PinchToZoomImageView) {
            return (PinchToZoomImageView) inflated;
        }
        return null;
    }
}
