package com.sbmgames.gradientscreator.customviews.addons.itemtouchhelpers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class DeleteOnSwipe extends ItemTouchHelperCallbackAdapter
{
    private DeleteItemExecuter deleteItemExecuter;
    private RecyclerView recyclerView;

    private DeleteOnSwipe(int swipeDirs)
    {
        super(0, swipeDirs);
    }

    public static DeleteOnSwipe create(int swipeDirs)
    {
        return new DeleteOnSwipe(swipeDirs);
    }

    public DeleteOnSwipe executer(DeleteItemExecuter deleteItemExecuter)
    {
        this.deleteItemExecuter = deleteItemExecuter;

        return this;
    }

    public void attach(RecyclerView recyclerView)
    {
        setRecyclerView(recyclerView);

        new ItemTouchHelper(this).attachToRecyclerView(recyclerView);
    }

    private void setRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
    {
        var position = viewHolder.getAdapterPosition();

        var deleted = deleteItemExecuter.deleteItem(position);
        if (!deleted && recyclerView != null && recyclerView.getAdapter() != null)
        {
            recyclerView.getAdapter().notifyItemChanged(position);
        }
    }

    public interface DeleteItemExecuter
    {
        boolean deleteItem(int position);
    }
}