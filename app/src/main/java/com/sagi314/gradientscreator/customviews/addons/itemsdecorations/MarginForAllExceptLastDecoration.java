package com.sagi314.gradientscreator.customviews.addons.itemsdecorations;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sagi314.gradientscreator.utils.staticutils.Validators;

public class MarginForAllExceptLastDecoration extends RecyclerView.ItemDecoration
{
    public static final int NO_MARGIN = 0;

    private final int margin;

    public MarginForAllExceptLastDecoration(int margin)
    {
        this.margin = Validators.illegalBelow(margin, 0, "'margin' can't be smaller than 0. if you want without margin, consider using 'MO_MARGIN'");
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);

        var position = parent.getChildAdapterPosition(view);

        setViewMargin(parent, view, position != 0);
    }


    private void setViewMargin(RecyclerView recyclerView, View view, boolean needMargin)
    {
        if (view != null)
        {
            var layoutParams = view.getLayoutParams();

            if (layoutParams instanceof ViewGroup.MarginLayoutParams)
            {
                var marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                var marginToApply = needMargin ? margin : 0;

                if (getRecyclerViewOrientation(recyclerView) == LinearLayoutManager.VERTICAL)
                {
                    marginLayoutParams.topMargin = marginToApply;
                }
                else
                {
                    marginLayoutParams.leftMargin = marginToApply;
                }
            }
        }
    }

    private int getRecyclerViewOrientation(RecyclerView recyclerView)
    {
        var layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager)
        {
            return ((LinearLayoutManager) layoutManager).getOrientation();
        }

        return LinearLayoutManager.HORIZONTAL;
    }
}