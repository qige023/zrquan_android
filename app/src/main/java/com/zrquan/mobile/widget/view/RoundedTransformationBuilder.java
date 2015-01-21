/*
* Copyright (C) 2014 Vincent Mi
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.zrquan.mobile.widget.view;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

public final class RoundedTransformationBuilder {

    /** Image transformation. */
    public interface Transformation {
        /**
         * Transform the source bitmap into a new bitmap. If you create a new bitmap instance, you must
         * call {@link android.graphics.Bitmap#recycle()} on {@code source}. You may return the original
         * if no transformation is required.
         */
        Bitmap transform(Bitmap source);

        /**
         * Returns a unique key for the transformation, used for caching purposes. If the transformation
         * has parameters (e.g. size, scale factor, etc) then these should be part of the key.
         */
        String key();
    }

    //private final Resources mResources;
    private final DisplayMetrics mDisplayMetrics;

    private float mCornerRadius = 0;
    private boolean mOval = false;
    private float mBorderWidth = 0;
    private ColorStateList mBorderColor =
            ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;

    public RoundedTransformationBuilder() {
        mDisplayMetrics = Resources.getSystem().getDisplayMetrics();
    }

    public RoundedTransformationBuilder scaleType(ImageView.ScaleType scaleType) {
        mScaleType = scaleType;
        return this;
    }

    /**
     * set corner radius in px
     */
    public RoundedTransformationBuilder cornerRadius(float radiusPx) {
        mCornerRadius = radiusPx;
        return this;
    }

    /**
     * set corner radius in dip
     */
    public RoundedTransformationBuilder cornerRadiusDp(float radiusDp) {
        mCornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusDp, mDisplayMetrics);
        return this;
    }

    /**
     * set border width in px
     */
    public RoundedTransformationBuilder borderWidth(float widthPx) {
        mBorderWidth = widthPx;
        return this;
    }

    /**
     * set border width in dip
     */
    public RoundedTransformationBuilder borderWidthDp(float widthDp) {
        mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp, mDisplayMetrics);
        return this;
    }


    /**
     * set border color
     */
    public RoundedTransformationBuilder borderColor(int color) {
        mBorderColor = ColorStateList.valueOf(color);
        return this;
    }

    public RoundedTransformationBuilder borderColor(ColorStateList colors) {
        mBorderColor = colors;
        return this;
    }

    public RoundedTransformationBuilder oval(boolean oval) {
        mOval = oval;
        return this;
    }

    public Transformation build() {
        return new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                Bitmap transformed = RoundedDrawable.fromBitmap(source)
                        .setScaleType(mScaleType)
                        .setCornerRadius(mCornerRadius)
                        .setBorderWidth(mBorderWidth)
                        .setBorderColor(mBorderColor)
                        .setOval(mOval)
                        .toBitmap();
                if (!source.equals(transformed)) {
                    source.recycle();
                }
                return transformed;
            }

            @Override
            public String key() {
                return "r:" + mCornerRadius
                        + "b:" + mBorderWidth
                        + "c:" + mBorderColor
                        + "o:" + mOval;
            }
        };
    }
}
