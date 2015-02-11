package com.jameskelso.android.widget;

import junit.framework.TestCase;

/**
 * Created by jkelso on 2/11/15.
 */
public class PinchToZoomMatrixStateTest extends TestCase {
    private static final float SCALE_FACTOR = 5.0f;
    private static final float SCALE_FOCUS_X = 1.0f;
    private static final float SCALE_FOCUS_Y = 50.0f;
    private static final float TRANSLATION_X = 25.0f;
    private static final float TRANSLATION_Y = 75.0f;
    private static final float SCALED_BMP_WIDTH = 500.0f;
    private static final float SCALED_BMP_HEIGHT = 1000.0f;

    private static final int VIEW_WIDTH = 300;
    private static final int VIEW_HEIGHT = 600;
    private static final int SRC_BITMAP_WIDTH = 100;
    private static final int SRC_BITMAP_HEIGHT = 200;
    private static final int MAX_BITMAP_WIDTH = 1000;
    private static final int MAX_BITMAP_HEIGHT = 2000;
    private static final int MIN_BITMAP_WIDTH = 51;
    private static final int MIN_BITMAP_HEIGHT = 102;
    private static final int PADDING_LEFT = 50;
    private static final int PADDING_TOP = 76;
    private static final int PADDING_RIGHT = 26;
    private static final int PADDING_BOTTOM = 5;
    private static final int TRANS_EXTRA_LEFT = 1;
    private static final int TRANS_EXTRA_TOP = 2;
    private static final int TRANS_EXTRA_RIGHT = 3;
    private static final int TRANS_EXTRA_BOTTOM = 4;

    private static final boolean CROP_TO_PADDING = true;
    private static final boolean PIN_AXES = true;

    private PinchToZoomMatrixState mState;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mState = new PinchToZoomMatrixState();
        mState.scaleFactor = SCALE_FACTOR;
        mState.scaleFocusX = SCALE_FOCUS_X;
        mState.scaleFocusY = SCALE_FOCUS_Y;
        mState.translationX = TRANSLATION_X;
        mState.translationY = TRANSLATION_Y;
        mState.scaledBitmapWidth = SCALED_BMP_WIDTH;
        mState.scaledBitmapHeight = SCALED_BMP_HEIGHT;
        mState.viewWidth = VIEW_WIDTH;
        mState.viewHeight = VIEW_HEIGHT;
        mState.srcBitmapWidth = SRC_BITMAP_WIDTH;
        mState.srcBitmapHeight = SRC_BITMAP_HEIGHT;
        mState.maxBitmapWidth = MAX_BITMAP_WIDTH;
        mState.maxBitmapHeight = MAX_BITMAP_HEIGHT;
        mState.minBitmapWidth = MIN_BITMAP_WIDTH;
        mState.minBitmapHeight = MIN_BITMAP_HEIGHT;
        mState.paddingLeft = PADDING_LEFT;
        mState.paddingTop = PADDING_TOP;
        mState.paddingRight = PADDING_RIGHT;
        mState.paddingBottom = PADDING_BOTTOM;
        mState.translationExtraLeft = TRANS_EXTRA_LEFT;
        mState.translationExtraTop = TRANS_EXTRA_TOP;
        mState.translationExtraRight = TRANS_EXTRA_RIGHT;
        mState.translationExtraBottom = TRANS_EXTRA_BOTTOM;
        mState.cropToPadding = CROP_TO_PADDING;
        mState.pinAxesSmallerThanViewBounds = PIN_AXES;
    }

    public void testGetXAxisTranslationStateScaledBitmapDimensionSize() {
        assertEquals(SCALED_BMP_WIDTH, mState.getXAxisState().scaledBitmapDimensionSize);
    }

    public void testGetXAxisTranslationStateTranslation() {
        assertEquals(TRANSLATION_X, mState.getXAxisState().translation);
    }

    public void testGetXAxisTranslationStatePaddingStart() {
        assertEquals(PADDING_LEFT, mState.getXAxisState().paddingStart);
    }

    public void testGetXAxisTranslationStatePaddingEnd() {
        assertEquals(PADDING_RIGHT, mState.getXAxisState().paddingEnd);
    }

    public void testGetXAxisTranslationStateTranslationExtraStart() {
        assertEquals(TRANS_EXTRA_LEFT, mState.getXAxisState().translationExtraStart);
    }

    public void testGetXAxisTranslationStateTranslationExtraEnd() {
        assertEquals(TRANS_EXTRA_RIGHT, mState.getXAxisState().translationExtraEnd);
    }

    public void testGetXAxisTranslationStateViewDimensionSize() {
        assertEquals(VIEW_WIDTH, mState.getXAxisState().viewDimensionSize);
    }

    public void testGetXAxisTranslationStateCropToPadding() {
        mState.pinAxesSmallerThanViewBounds = false;
        assertEquals(CROP_TO_PADDING, mState.getXAxisState().cropToPadding);
    }

    public void testGetXAxisTranslationStatePinAxesSmallerThanViewBounds() {
        mState.cropToPadding = false;
        assertEquals(PIN_AXES, mState.getXAxisState().pinAxesSmallerThanViewBounds);
    }

    public void testGetYAxisTranslationStateScaledBitmapDimensionSize() {
        assertEquals(SCALED_BMP_HEIGHT, mState.getYAxisState().scaledBitmapDimensionSize);
    }

    public void testGetYAxisTranslationStateTranslation() {
        assertEquals(TRANSLATION_Y, mState.getYAxisState().translation);
    }

    public void testGetYAxisTranslationStatePaddingStart() {
        assertEquals(PADDING_TOP, mState.getYAxisState().paddingStart);
    }

    public void testGetYAxisTranslationStatePaddingEnd() {
        assertEquals(PADDING_BOTTOM, mState.getYAxisState().paddingEnd);
    }

    public void testGetYAxisTranslationStateTranslationExtraStart() {
        assertEquals(TRANS_EXTRA_TOP, mState.getYAxisState().translationExtraStart);
    }

    public void testGetYAxisTranslationStateTranslationExtraEnd() {
        assertEquals(TRANS_EXTRA_BOTTOM, mState.getYAxisState().translationExtraEnd);
    }

    public void testGetYAxisTranslationStateViewDimensionSize() {
        assertEquals(VIEW_HEIGHT, mState.getYAxisState().viewDimensionSize);
    }

    public void testGetYAxisTranslationStateCropToPadding() {
        mState.pinAxesSmallerThanViewBounds = false;
        assertEquals(CROP_TO_PADDING, mState.getYAxisState().cropToPadding);
    }

    public void testGetYAxisTranslationStatePinAxesSmallerThanViewBounds() {
        mState.cropToPadding = false;
        assertEquals(PIN_AXES, mState.getYAxisState().pinAxesSmallerThanViewBounds);
    }

    public void testAxisTranslationStateGetAdjustedViewSize() {
        PinchToZoomMatrixState.AxisState axis = mState.getXAxisState();
        final int viewDimensionWithoutPadding = VIEW_WIDTH - PADDING_LEFT - PADDING_RIGHT;
        assertEquals(viewDimensionWithoutPadding, axis.getAdjustedViewSize());
    }

    public void testAxisStateGetAdjustedViewSizeNoCropToPadding() {
        mState.cropToPadding = false;
        PinchToZoomMatrixState.AxisState axis = mState.getXAxisState();
        assertEquals(VIEW_WIDTH, axis.getAdjustedViewSize());
    }

    public void testAxisStateGetAdditionalTranslationForScaledBitmapSize() {
        // Turn off crop to padding since this calculation depends on adjusted view size
        mState.cropToPadding = false;
        PinchToZoomMatrixState.AxisState axis = mState.getXAxisState();
        final float additionalTranslation = SCALED_BMP_WIDTH - VIEW_WIDTH;
        assertEquals(additionalTranslation, axis.getAdditionalTranslationForScaledBitmapSize());
    }

    public void testAxisStateGetAdditionalTranslationForScaledBitmapSizeSmallerThanAdjustView() {
        // Turn off crop to padding since this calculation depends on adjusted view size
        mState.cropToPadding = false;
        mState.scaledBitmapWidth = VIEW_WIDTH;
        mState.viewWidth = Math.round(SCALED_BMP_WIDTH);
        PinchToZoomMatrixState.AxisState axis = mState.getXAxisState();
        assertEquals(0f, axis.getAdditionalTranslationForScaledBitmapSize());
    }
}
