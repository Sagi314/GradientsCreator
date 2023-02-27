package com.sbmgames.gradientscreator.customviews.addons.itemtouchhelpers;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ChangeItemPositionOnDrag extends ItemTouchHelperCallbackAdapter
{
    private ChangeItemPositionExecuter changeItemPositionExecuter;
    private boolean keepItemsInBounds = false;

    private ChangeItemPositionOnDrag(int dragDirs)
    {
        super(dragDirs, 0);
    }

    public static ChangeItemPositionOnDrag create(int dragDirections)
    {
        return new ChangeItemPositionOnDrag(dragDirections);
    }

    public ChangeItemPositionOnDrag keepItemsInBounds()
    {
        this.keepItemsInBounds = true;

        return this;
    }

    public ChangeItemPositionOnDrag executer(ChangeItemPositionExecuter changeItemPositionExecuter)
    {
        this.changeItemPositionExecuter = changeItemPositionExecuter;

        return this;
    }

    public void attach(RecyclerView recyclerView)
    {
        new ItemTouchHelper(this).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target)
    {
        var fromPosition = viewHolder.getAdapterPosition();
        var toPosition = target.getAdapterPosition();

        if (fromPosition == 0 && toPosition == -1)
        {
            return false;
        }

        changeItemPositionExecuter.changeItemPosition(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                            float dY, int actionState, boolean isCurrentlyActive)
    {
        if (keepItemsInBounds && actionState == ItemTouchHelper.ACTION_STATE_DRAG || actionState == ItemTouchHelper.ACTION_STATE_IDLE)
        {
            var itemView = viewHolder.itemView;

            var viewTop = itemView.getTop() + dY;
            var viewBottom = itemView.getBottom() + dY;

            var recyclerTop = recyclerView.getTop();
            var recyclerBottom = recyclerView.getBottom();

            if (viewTop < recyclerTop || viewBottom > recyclerBottom)
            {
                dY = 0;
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public interface ChangeItemPositionExecuter
    {
        void changeItemPosition(int fromPosition, int toPosition);
    }
}