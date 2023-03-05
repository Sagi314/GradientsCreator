package com.sagi314.gradientscreator.customviews.colorslistadapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagi314.gradientscreator.models.ColorsList;
import com.sagi314.gradientscreator.utils.staticutils.ColorUtils;
import com.sagi314.gradientscreator.R;
import com.sagi314.gradientscreator.utils.staticutils.Validators;

public class ColorsListAdapter extends RecyclerView.Adapter<MyViewHolder> implements ColorsList.ColorsListChangedListener
{
    private final ColorsList colorsList;

    private OnItemClickListener onItemClickListener;

    public ColorsListAdapter(ColorsList colorsList)
    {
        this.colorsList = Validators.requireNotNull(colorsList, "'colorList' can't be null");
    }

    public ColorsListAdapter(ColorsList colorsList, OnItemClickListener onItemClickListener)
    {
        this(colorsList);

        this.onItemClickListener = onItemClickListener;
    }

    //region OVERRIDDEN RecyclerView.Adapter METHODS
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_oval_color_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        var color = colorsList.get(position);

        initOvalColorView(holder, color);
    }

    @Override
    public int getItemCount()
    {
        return colorsList.size();
    }
    //endregion

    //region ITEM CLICK
    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    private void notifyItemClick(int position, int color)
    {
        if (onItemClickListener != null)
        {
            onItemClickListener.onItemClick(position, color);
        }
    }
    //endregion

    //region OVERRIDDEN Colors.ColorsChangedListener METHODS
    @Override
    public void onItemAdded(int position)
    {
        notifyItemInserted(position);
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition)
    {
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemRemoved(int position)
    {
        notifyItemRemoved(position);
    }

    @Override
    public void onItemChanged(int position)
    {
        notifyItemChanged(position);
    }

    @Override
    public void refresh()
    {
        notifyDataSetChanged();
    }
    //endregion

    private void initOvalColorView(MyViewHolder holder, int color)
    {
        var ovalColorView = holder.ovalColorView;

        ovalColorView.setFillColor(color);

        var strokeColor = ColorUtils.getBrightness(color) < 0.5f ? Color.WHITE : Color.BLACK;
        ovalColorView.setStrokeColor(strokeColor);

        ovalColorView.setOnClickListener(v -> notifyItemClick(holder.getAdapterPosition(), color));
    }

    public interface OnItemClickListener
    {
        void onItemClick(int position, int color);
    }
}