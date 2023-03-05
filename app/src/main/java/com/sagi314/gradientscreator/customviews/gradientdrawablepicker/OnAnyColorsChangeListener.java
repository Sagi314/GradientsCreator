package com.sagi314.gradientscreator.customviews.gradientdrawablepicker;

import com.sagi314.gradientscreator.models.ColorsList;

public class OnAnyColorsChangeListener implements ColorsList.ColorsListChangedListener
{
    private final Runnable onAnyChange;

    public OnAnyColorsChangeListener(Runnable onAnyChange)
    {
        this.onAnyChange = onAnyChange;
    }

    @Override
    public void onItemAdded(int position)
    {
        callOnAnyChangeEvent();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition)
    {
        callOnAnyChangeEvent();
    }

    @Override
    public void onItemRemoved(int position)
    {
        callOnAnyChangeEvent();
    }

    @Override
    public void onItemChanged(int position)
    {
        callOnAnyChangeEvent();
    }

    @Override
    public void refresh()
    {
        callOnAnyChangeEvent();
    }

    private void callOnAnyChangeEvent()
    {
        if (onAnyChange != null)
        {
            onAnyChange.run();
        }
    }
}