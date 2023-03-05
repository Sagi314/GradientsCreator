package com.sagi314.gradientscreator.customviews.addons.itemtouchhelpers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ItemTouchHelperCallbackAdapter extends ItemTouchHelper.SimpleCallback
{
    protected ItemTouchHelperCallbackAdapter(int dragDirs, int swipeDirs)
    {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
    {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
    {

    }
}
