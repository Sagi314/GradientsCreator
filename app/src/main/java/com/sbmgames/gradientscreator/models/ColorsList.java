package com.sbmgames.gradientscreator.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ColorsList
{
    public static final int NO_MINIMUM_COUNT = -1;
    public static final int NO_MAXIMUM_COUNT = -1;

    private final List<Integer> colors = new ArrayList<>();
    private final List<ColorsListChangedListener> listeners = new ArrayList<>();

    private int minimumCount = NO_MINIMUM_COUNT;
    private int maximumCount = NO_MAXIMUM_COUNT;

    public ColorsList()
    {
        //called before colors added
    }

    public ColorsList(int... colors)
    {
        this();

        set(colors);
    }

    public ColorsList(List<Integer> colors)
    {
        this(intListToIntArrayWithoutNulls(colors));
    }

    public ColorsList(Integer... colors)
    {
        this(Arrays.asList(colors));
    }

    public ColorsList minimumCount(int minimumCount)
    {
        this.minimumCount = minimumCount;

        return this;
    }

    public ColorsList maximumCount(int maximumCount)
    {
        this.maximumCount = maximumCount;

        return this;
    }

    //region PUBLIC METHODS
    public void addListener(ColorsListChangedListener colorsListChangedListener)
    {
        if (colorsListChangedListener != null)
        {
            listeners.add(colorsListChangedListener);

            colorsListChangedListener.refresh();
        }
    }

    public boolean add(int color, int position)
    {
        throwIfNotInBounds(position, true);

        if (canAdd())
        {
            colors.add(position, color);

            notifyItemAdded(position);

            return true;
        }

        return false;
    }

    public boolean canAdd()
    {
        return colors.size() < maximumCount || maximumCount == NO_MAXIMUM_COUNT;
    }

    public boolean canRemove()
    {
        return colors.size() > minimumCount || minimumCount == NO_MINIMUM_COUNT;
    }

    public void addToStart(int color)
    {
        add(color, 0);
    }

    public void addToEnd(int color)
    {
        add(color, size());
    }

    public void move(int fromPosition, int toPosition)
    {
        throwIfNotInBounds(fromPosition, false);
        throwIfNotInBounds(toPosition, false);

        Collections.swap(colors, fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    public boolean remove(int position)
    {
        throwIfNotInBounds(position, false);

        if (canRemove())
        {
            colors.remove(position);

            notifyItemRemoved(position);

            return true;
        }

        return false;
    }

    public void setColorAt(int color, int position)
    {
        throwIfNotInBounds(position, false);

        colors.set(position, color);

        notifyItemChanged(position);
    }

    public int get(int position)
    {
        return colors.get(position);
    }

    public int[] getAll()
    {
        return colors.stream().mapToInt(i -> i).toArray();
    }

    public int size()
    {
        return colors.size();
    }

    public void set(int[] colors)
    {
        this.colors.clear();
        notifyListRefreshed();

        for (var color : colors)
        {
            addToEnd(color);
        }
    }

    public void set(List<Integer> colors)
    {
        set(intListToIntArrayWithoutNulls(colors));
    }

    public void set(Integer[] colors)
    {
        set(Arrays.asList(colors));
    }
    //endregion

    //region NOTIFIERS
    private void notifyItemAdded(int position)
    {
        listeners.forEach(listener -> listener.onItemAdded(position));
    }

    private void notifyItemMoved(int fromPosition, int toPosition)
    {
        listeners.forEach(listener -> listener.onItemMoved(fromPosition, toPosition));
    }

    private void notifyItemRemoved(int position)
    {
        listeners.forEach(listener -> listener.onItemRemoved(position));
    }

    private void notifyItemChanged(int position)
    {
        listeners.forEach(listener -> listener.onItemChanged(position));
    }

    private void notifyListRefreshed()
    {
        listeners.forEach(ColorsListChangedListener::refresh);
    }
    //endregion

    private static int[] intListToIntArrayWithoutNulls(List<Integer> list)
    {
        return Objects.requireNonNull(list)
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::valueOf)
                .toArray();
    }

    private void throwIfNotInBounds(int position, boolean allowSameAsSize)
    {
        var addToLength = allowSameAsSize ? 1 : 0;

        Objects.checkIndex(position, size() + addToLength);
    }

    public interface ColorsListChangedListener
    {
        void onItemAdded(int position);

        void onItemMoved(int fromPosition, int toPosition);

        void onItemRemoved(int position);

        void onItemChanged(int position);

        void refresh();
    }
}