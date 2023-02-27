package com.sbmgames.gradientscreator.customviews.colorslistadapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.sbmgames.gradientscreator.R;

//todo make able to adapt more shapes
//todo consider changing name to ColorView
public class OvalColorView extends View
{
    private final Paint fillColorPaint = new Paint();
    private final Paint strokeColorPaint = new Paint();

    public OvalColorView(Context context)
    {
        this(context, null);
    }

    public OvalColorView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        var attributes = context.obtainStyledAttributes(attrs, R.styleable.OvalColorView);

        setFillColor(attributes.getColor(R.styleable.OvalColorView_fill_color, Color.BLUE));
        setStrokeColor(attributes.getColor(R.styleable.OvalColorView_stroke_color, Color.BLACK));
        setStrokeWidth(attributes.getDimension(R.styleable.OvalColorView_stroke_width, 10));

        attributes.recycle();

        strokeColorPaint.setStyle(Paint.Style.STROKE);
        fillColorPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);

        canvas.drawOval(getDrawingRect(), fillColorPaint);
        canvas.drawOval(getDrawingRect(), strokeColorPaint);
    }

    private RectF getDrawingRect()
    {
        return new RectF(
                getHalfStrokeWidth(),
                getHalfStrokeWidth(),
                getWidth()  - getHalfStrokeWidth(),
                getHeight() - getHalfStrokeWidth());
    }

    private float getHalfStrokeWidth()
    {
        return getStrokeWidth() / 2.0f;
    }

    public float getStrokeWidth()
    {
        return strokeColorPaint.getStrokeWidth();
    }

    public int getStrokeColor()
    {
        return strokeColorPaint.getColor();
    }

    public int getFillColor()
    {
        return fillColorPaint.getColor();
    }

    public void setStrokeWidth(float strokeWidth)
    {
        strokeColorPaint.setStrokeWidth(strokeWidth);

        invalidate();
    }

    public void setStrokeColor(int strokeColor)
    {
        strokeColorPaint.setColor(strokeColor);

        invalidate();
    }

    public void setFillColor(int fillColor)
    {
        fillColorPaint.setColor(fillColor);

        invalidate();
    }
}