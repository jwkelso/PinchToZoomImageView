package com.jameskelso.android.widget;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.annotation.NonNull;

/**
 * A class for managing the instance state of a {@link PinchToZoomImageView}.
 */
public class PinchToZoomSavedState extends Preference.BaseSavedState {
    public static final Parcelable.Creator<PinchToZoomSavedState> CREATOR = new Parcelable
            .Creator<PinchToZoomSavedState>() {
        public PinchToZoomSavedState createFromParcel(Parcel in) {
            return new PinchToZoomSavedState(in);
        }

        public PinchToZoomSavedState[] newArray(int size) {
            return new PinchToZoomSavedState[size];
        }
    };

    /**
     * Saved state for {@link PinchToZoomTouchHelper}
     */
    int panThreshold;
    int touchMode;
    PointF firstTouch;
    PointF lastTouch;

    /**
     * Saved state for {@link PinchToZoomMatrixHelper}
     */
    boolean cropToPadding;
    boolean pinAxesSmallerThanViewBounds;
    Rect padding;
    Rect translationExtra;
    Point srcBitmapSize;
    Point bitmapMinSize;
    Point bitmapMaxSize;
    Matrix matrix;

    /**
     * Saved state for {@link PinchToZoomScaleHelper}
     */
    float currentScaleFactor;

    /**
     * Constructor used when reading from a parcel. Reads the state of the superclass.
     *
     * @param source The parcel containing the instance state of the class.
     */
    public PinchToZoomSavedState(Parcel source) {
        super(source);

        panThreshold = source.readInt();
        touchMode = source.readInt();
        firstTouch = source.readParcelable(null);
        lastTouch = source.readParcelable(null);

        cropToPadding = source.readByte() != 0;
        pinAxesSmallerThanViewBounds = source.readByte() != 0;
        padding = source.readParcelable(null);
        translationExtra = source.readParcelable(null);
        srcBitmapSize = source.readParcelable(null);
        bitmapMinSize = source.readParcelable(null);
        bitmapMaxSize = source.readParcelable(null);

        float[] values = new float[9];
        source.readFloatArray(values);
        matrix = new Matrix();
        matrix.setValues(values);

        currentScaleFactor = source.readFloat();
    }

    /**
     * Constructor called when creating a SavedState object
     *
     * @param superState The state of the superclass of this view
     */
    public PinchToZoomSavedState(Parcelable superState) {
        super(superState);
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        super.writeToParcel(out, flags);

        out.writeInt(panThreshold);
        out.writeInt(touchMode);
        out.writeParcelable(firstTouch, 0);
        out.writeParcelable(lastTouch, 0);

        out.writeByte((byte) (cropToPadding ? 1 : 0));
        out.writeByte((byte) (pinAxesSmallerThanViewBounds ? 1 : 0));
        out.writeParcelable(padding, 0);
        out.writeParcelable(translationExtra, 0);
        out.writeParcelable(srcBitmapSize, 0);
        out.writeParcelable(bitmapMinSize, 0);
        out.writeParcelable(bitmapMaxSize, 0);

        float[] values = new float[9];
        matrix.getValues(values);
        out.writeFloatArray(values);

        out.writeFloat(currentScaleFactor);
    }
}
