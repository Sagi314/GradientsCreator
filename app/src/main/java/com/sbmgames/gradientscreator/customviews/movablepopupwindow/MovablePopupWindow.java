package com.sbmgames.gradientscreator.customviews.movablepopupwindow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.sbmgames.gradientscreator.R;

public class MovablePopupWindow extends PopupWindow
{
    private int location;

    //region CONSTRUCTORS
    public MovablePopupWindow(Context context)
    {
        super(context);
    }

    public MovablePopupWindow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MovablePopupWindow(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public MovablePopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MovablePopupWindow()
    {
    }

    public MovablePopupWindow(View contentView)
    {
        super(contentView);
    }

    public MovablePopupWindow(int width, int height)
    {
        super(width, height);
    }

    public MovablePopupWindow(View contentView, int width, int height)
    {
        super(contentView, width, height);
    }

    public MovablePopupWindow(View contentView, int width, int height, boolean focusable)
    {
        super(contentView, width, height, focusable);
    }
    //endregion

    @Override
    public void showAsDropDown(View anchorView)//simplify
    {
        getContentView().measure(0, 0);

        var contentView = getContentView();
        var contentWidth = getWidth() == ViewGroup.LayoutParams.WRAP_CONTENT ? contentView.getMeasuredWidth() : getWidth();
        var contentHeight = getHeight() == ViewGroup.LayoutParams.WRAP_CONTENT ? contentView.getMeasuredHeight() : getHeight();

        var anchorWidth = anchorView.getWidth();
        var anchorHeight = anchorView.getHeight();

        int xOffset = 0;
        int yOffset = 0;

        if (isLocationBitOn(Location.UP))
        {
            yOffset = -contentHeight - anchorHeight;
        }
        else if (isLocationBitOn(Location.CENTER_VERTICAL))
        {
            yOffset = (-contentHeight - anchorHeight) / 2;
        }

        if (isLocationBitOn(Location.LEFT))
        {
            xOffset = -contentWidth - anchorWidth;
        }
        else if (isLocationBitOn(Location.RIGHT))
        {
            xOffset = anchorWidth;
        }
        else if (isLocationBitOn(Location.CENTER_HORIZONTAL))
        {
            xOffset = anchorWidth / 2 - contentWidth / 2;
        }

        super.showAsDropDown(anchorView, xOffset, yOffset);
    }

    private boolean isLocationBitOn(int location)
    {
        return (this.location & location) == location;
    }

    public int getLocation()
    {
        return location;
    }

    public void setLocation(int location)
    {
        this.location = location;
    }

    public static ViewGroup inflatePopupLayout(Context context, TypedArray attributes, int layoutResourceIndex)
    {
        var layoutResource = attributes.getResourceId(layoutResourceIndex, 0);
        var inflatedLayout = LayoutInflater.from(context).inflate(layoutResource, null, false);

        if (inflatedLayout instanceof ViewGroup)
        {
            return (ViewGroup) inflatedLayout;
        }
        else
        {
            throw new IllegalArgumentException("Resource ID passed as @layoutResourceIndex not an ID of a ViewGroup");
        }
    }

    public static class Location
    {
        public static final int UP = 1;
        public static final int DOWN = 1<<1;
        public static final int CENTER_VERTICAL = 1<<2;
        public static final int LEFT = 1<<3;
        public static final int RIGHT = 1<<4;
        public static final int CENTER_HORIZONTAL = 1<<5;

        private Location() { }
    }
}
