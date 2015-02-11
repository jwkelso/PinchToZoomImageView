package com.jameskelso.android.widget;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.test.AndroidTestCase;

/**
 * Created by jkelso on 2/19/15.
 */
public class PinchToZoomSavedStateTest extends AndroidTestCase {
    private static final int PAN_THRESHOLD = 24;
    private static final int TOUCH_MODE = PinchToZoomTouchHelper.STATE_PAN;
    private static final PointF FIRST_TOUCH = new PointF(1f, 2f);
    private static final PointF LAST_TOUCH = new PointF(3f, 4f);
    private static final boolean CROP_TO_PADDING = true;
    private static final boolean PIN_AXES = true;
    private static final Rect PADDING = new Rect(1, 2, 3, 4);
    private static final Rect TRANSLATION_EXTRA = new Rect(5, 6, 7, 8);
    private static final Point SRC_BITMAP_SIZE = new Point(5, 6);
    private static final Point BITMAP_MIN_SIZE = new Point(7, 8);
    private static final Point BITMAP_MAX_SIZE = new Point(9, 10);
    private static final Matrix MATRIX = new Matrix();
    private static final float CURRENT_SCALE_FACTOR = 32.5f;

    private PinchToZoomSavedState mState;
    private Parcel mParcel;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        MATRIX.postScale(2.5f, 2.5f, 0, 0);
        MATRIX.postTranslate(2, 2);

        mState = new PinchToZoomSavedState(new Bundle());
        mState.panThreshold = PAN_THRESHOLD;
        mState.touchMode = TOUCH_MODE;
        mState.firstTouch = FIRST_TOUCH;
        mState.lastTouch = LAST_TOUCH;
        mState.cropToPadding = CROP_TO_PADDING;
        mState.pinAxesSmallerThanViewBounds = PIN_AXES;
        mState.padding = PADDING;
        mState.translationExtra = TRANSLATION_EXTRA;
        mState.srcBitmapSize = SRC_BITMAP_SIZE;
        mState.bitmapMinSize = BITMAP_MIN_SIZE;
        mState.bitmapMaxSize = BITMAP_MAX_SIZE;
        mState.matrix = MATRIX;
        mState.currentScaleFactor = CURRENT_SCALE_FACTOR;

        mParcel = Parcel.obtain();
        mState.writeToParcel(mParcel, 0);
        mParcel.setDataPosition(0);
    }

    @Override
    public void tearDown() throws Exception {
        mState = null;
        mParcel = null;
        super.tearDown();
    }

    public void testConstructorFromParcelPanThreshold() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(PAN_THRESHOLD, state.panThreshold);
    }

    public void testConstructorFromParcelTouchMode() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(TOUCH_MODE, state.touchMode);
    }

    public void testConstructorFromParcelFirstTouch() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(FIRST_TOUCH, state.firstTouch);
    }

    public void testConstructorFromParcelLastTouch() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(LAST_TOUCH, state.lastTouch);
    }

    public void testConstructorFromParcelCropToPadding() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(CROP_TO_PADDING, state.cropToPadding);
    }

    public void testConstructorFromParcelPinAxesSmallerThanViewBounds() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(PIN_AXES, state.pinAxesSmallerThanViewBounds);
    }

    public void testConstructorFromParcelPadding() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(PADDING, state.padding);
    }

    public void testConstructorFromParcelTranslationExtra() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(TRANSLATION_EXTRA, state.translationExtra);
    }

    public void testConstructorFromParcelSrcBitmapSize() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(SRC_BITMAP_SIZE, state.srcBitmapSize);
    }

    public void testConstructorFromParcelBitmapMinSize() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(BITMAP_MIN_SIZE, state.bitmapMinSize);
    }

    public void testConstructorFromParcelBitmapMaxSize() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(BITMAP_MAX_SIZE, state.bitmapMaxSize);
    }

    public void testConstructorFromParcelMatrix() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(MATRIX, state.matrix);
    }

    public void testConstructorFromParcelCurrentScaleFactor() {
        PinchToZoomSavedState state = new PinchToZoomSavedState(mParcel);
        assertEquals(CURRENT_SCALE_FACTOR, state.currentScaleFactor);
    }

    public void testConstructorWithParcelable() {
        // Nothing to test here
    }

    public void testWriteToParcel() {
        // Test as part of testConstructorFromParcel tests
    }

    public void testCreatorCreateFromParcel() {
        PinchToZoomSavedState instance = PinchToZoomSavedState.CREATOR.createFromParcel(mParcel);
        assertNotNull(instance);
    }

    public void testCreatorNewArray() {
        final int arrayLength = 5;
        PinchToZoomSavedState[] instance = PinchToZoomSavedState.CREATOR.newArray(arrayLength);
        assertNotNull(instance);
    }

    public void testCreatorNewArrayLength() {
        final int arrayLength = 5;
        PinchToZoomSavedState[] instance = PinchToZoomSavedState.CREATOR.newArray(arrayLength);
        assertEquals(arrayLength, instance.length);
    }
}
