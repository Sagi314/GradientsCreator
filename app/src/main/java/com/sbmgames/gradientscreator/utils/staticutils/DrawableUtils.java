package com.sbmgames.gradientscreator.utils.staticutils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class DrawableUtils
{
    private DrawableUtils() { }

    public static Drawable deepCopy(Drawable drawable)
    {
        return drawable.getConstantState().newDrawable();
    }

    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height)
    {
        var drawableCopy = drawable.getConstantState().newDrawable().mutate();
        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawableCopy.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawableCopy.draw(canvas);

        return bitmap;
    }
}
