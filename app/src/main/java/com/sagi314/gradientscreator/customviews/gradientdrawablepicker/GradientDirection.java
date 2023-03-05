package com.sagi314.gradientscreator.customviews.gradientdrawablepicker;

import android.graphics.drawable.GradientDrawable.Orientation;

public enum GradientDirection
{
    BR_TL(Orientation.BR_TL, 1, 1),
    BOTTOM_TOP(Orientation.BOTTOM_TOP, 0.5f, 1),
    BL_TR(Orientation.BL_TR, 0, 1),
    RIGHT_LEFT(Orientation.RIGHT_LEFT, 1, 0.5f),
    CENTER(null, 0.5f, 0.5f),
    LEFT_RIGHT(Orientation.LEFT_RIGHT, 0, 0.5f),
    TR_BL(Orientation.TR_BL, 1, 0),
    TOP_BOTTOM(Orientation.TOP_BOTTOM, 0.5f, 0),
    TL_BR(Orientation.TL_BR, 0, 0);

    private final float centerX;
    private final float centerY;
    private final Orientation gradientOrientation;

    GradientDirection(Orientation gradientOrientation, float centerX, float centerY)
    {
        this.gradientOrientation = gradientOrientation;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public float getCenterX()
    {
        return centerX;
    }

    public float getCenterY()
    {
        return centerY;
    }

    public Orientation getGradientOrientation()
    {
        return gradientOrientation;
    }
}
