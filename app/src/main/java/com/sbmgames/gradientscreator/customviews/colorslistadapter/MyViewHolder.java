package com.sbmgames.gradientscreator.customviews.colorslistadapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sbmgames.gradientscreator.R;

public class MyViewHolder extends RecyclerView.ViewHolder
{
    public final OvalColorView ovalColorView;

    public MyViewHolder(@NonNull View itemView)
    {
        super(itemView);

        ovalColorView = itemView.findViewById(R.id.ovalColorView);
    }
}