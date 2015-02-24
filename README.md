# PinchToZoomImageView
An ImageView that allows for scaling and panning an image using pinch, stretch, and pan gestures.
Since this widget controls its own scaling and translation, ```setScaleType(ImageView.ScaleType)```
with a scale type of anything other than ```ImageView.ScaleType.MATRIX``` is disallowed.

![alt text](default.gif?raw=true "Default PinchToZoomImageView in operation")

```
<com.jameskelso.android.widget.AspectRatioImageView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

***

The total amount an image bay be zoomed in or zoomed out is controlled by
```setBitmapMinimumSizePixels(int, int)``` and ```setBitmapMaximumSizePixels(int, int)```. These
attributes may be set in code or in layout XML. If no minimum is set, the minimum will default to
25% of the original size of the bitmap. If no maximum is set, the maximum will default to 200% of
the original size of the bitmap.

```
<com.jameskelso.android.widget.AspectRatioImageView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:minBitmapWidth="500px"
    app:minBitmapHeight="1000px"
    app:maxBitmapWidth="500px"
    app:maxBitmapHeight="1000px"/>
```

***

Any axis that is smaller than the bounds of the view can be pinned to center automatically using
```setPinAxesSmallerThanViewBounds(boolean)```. This means that if the current scaled width or
scaled height of the image is smaller than the width or height of the view, that axis will be
centered within the view and will not be able to be panned. If this value is not set, it is enabled
by default.

![alt text](nopinaxes.gif?raw=true
    "PinchToZoomImageView with setPinAxesSmallerThanViewBounds() set to false")

```
<com.jameskelso.android.widget.AspectRatioImageView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:pinAxesSmallerThanBounds="false"/>
```

***

The source image can be allowed to pan outside the bounds of the view using
```setTranslationExtra(int, int, int, int)```. Translation extra works similarly to negative padding
in that the bounds of the allowed image translation are adjusted to be larger than the current
bounds of the view. Note that negative values are disallowed. Instead of using a negative value, use
```setPadding(int, int, int, int)``` combined with ```setCropToPadding(boolean)```.

![alt text](extra.gif?raw=true
    "PinchToZoomImageView with setTranslationExtra set to 50dp all around.")

```
<com.jameskelso.android.widget.AspectRatioImageView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:transExtraLeft="50dp"
    app:transExtraTop="50dp"
    app:transExtraRight="50dp"
    app:transExtraBottom="50dp"/>
```

![alt text](padding.gif?raw=true "PinchToZoomImageView with setPadding set to 50dp all around.")

```
<com.jameskelso.android.widget.AspectRatioImageView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="50dp"
    android:cropToPadding="true"/>
```

***

The threshold that determines the difference between a tap on the view and a dragging gesture is
controlled by ```setPanThreshold(int)```. As the pan threshold increases, the user will need to drag
further on the view to initiate panning on the image. If this value is not set, it defaults to a
reasonable value.

```
<com.jameskelso.android.widget.AspectRatioImageView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:panThreshold="4"/>
```

***

##Usage
<pre>repositories {
    maven { url "https://github.com/jwkelso/android-maven/raw/master/" }
}

dependencies {
    compile 'com.jameskelso:pinch-to-zoom-imageview:1.0.0'
}
</pre>
