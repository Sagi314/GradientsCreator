package com.sagi314.gradientscreator.customviews.popups.spinners.sbmspinner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.sagi314.gradientscreator.R;
import com.sagi314.gradientscreator.customviews.popups.popuptrigger.PopupTrigger;
import com.sagi314.gradientscreator.utils.staticutils.DrawableUtils;

import java.util.ArrayList;
import java.util.List;

public class SbmSpinner extends PopupTrigger
{
    private final List<OnItemSelectedListener> onItemSelectedListeners = new ArrayList<>();

    private final Drawable defaultBackground;

    private int selectedPosition;

    private boolean previewSelectedItem;
    private boolean hideSelectedItem;

    //region CONSTRUCTORS VARIATIONS
    public SbmSpinner(Context context)
    {
        this(context, null);
    }

    public SbmSpinner(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SbmSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }
    //endregion

    //MAIN CONSTRUCTOR - any constructor should call this constructor eventually.
    public SbmSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.defaultBackground = getBackground();

        setFromAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void setFromAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        var attributes = getContext().obtainStyledAttributes(attrs, R.styleable.SbmSpinner, defStyleAttr, defStyleRes);

        setSelectedPosition(attributes.getInt(R.styleable.SbmSpinner_selectedPosition, 0));
        setPreviewSelectedItem(attributes.getBoolean(R.styleable.SbmSpinner_previewSelectedItem, true));
        setHideSelectedItem(attributes.getBoolean(R.styleable.SbmSpinner_hideSelectedItem, false));

        attributes.recycle();
    }

    @Override
    protected void onItemClick(int itemPosition)
    {
        setSelectedPosition(itemPosition);
    }

    @Override
    protected Class<? extends View> getAllowedPopupViewType()
    {
        return ViewGroup.class;
    }

    //region GETTERS
    public int getSelectedPosition()
    {
        return selectedPosition;
    }

    public boolean isPreviewSelectedItem()
    {
        return previewSelectedItem;
    }

    public boolean isHideSelectedItem()
    {
        return hideSelectedItem;
    }
    //endregion

    //region SETTERS
    public void setSelectedPosition(int selectedPosition)
    {
        unhideSelectedItem();

        this.selectedPosition = selectedPosition;

        setPreviewBackgroundIfNeeded();
        hideSelectedItemIfNeeded();

        notifyItemSelected(selectedPosition);
    }

    public void setPreviewSelectedItem(boolean previewSelectedItem)
    {
        this.previewSelectedItem = previewSelectedItem;

        setPreviewBackgroundIfNeeded();
    }

    public void addOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener)
    {
        onItemSelectedListeners.add(onItemSelectedListener);

        onItemSelectedListener.onItemSelected(getSelectedPosition());
    }

    public void removeOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener)
    {
        onItemSelectedListeners.remove(onItemSelectedListener);
    }

    public void setHideSelectedItem(boolean hideSelectedItem)
    {
        this.hideSelectedItem = hideSelectedItem;

        if (!hideSelectedItem)
        {
            unhideSelectedItem();
        }
        else
        {
            hideSelectedItemIfNeeded();
        }
    }
    //endregion

    private void notifyItemSelected(int selectedPosition)
    {
        onItemSelectedListeners.forEach(listener -> listener.onItemSelected(selectedPosition));
    }

    private void unhideSelectedItem()
    {
        var selectedItem = getItemAt(selectedPosition);

        selectedItem.setVisibility(VISIBLE);
    }

    private void hideSelectedItemIfNeeded()
    {
        if (hideSelectedItem)
        {
            var selectedItem = getItemAt(selectedPosition);

            selectedItem.setVisibility(GONE);
        }
    }

    private void setPreviewBackgroundIfNeeded()
    {
        if (previewSelectedItem)
        {
            var selectedItem = getItemAt(selectedPosition);
            var selectedBackground = selectedItem.getBackground();

            setBackground(DrawableUtils.deepCopy(selectedBackground));
        }
        else
        {
            setBackground(defaultBackground);
        }
    }

    public interface OnItemSelectedListener
    {
        void onItemSelected(int position);
    }
}