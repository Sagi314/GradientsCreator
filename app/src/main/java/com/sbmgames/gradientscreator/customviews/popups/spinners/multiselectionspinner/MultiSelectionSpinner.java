package com.sbmgames.gradientscreator.customviews.popups.spinners.multiselectionspinner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.sbmgames.gradientscreator.R;
import com.sbmgames.gradientscreator.customviews.popups.popuptrigger.PopupTrigger;
import com.sbmgames.gradientscreator.utils.staticutils.ObjectsVerifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MultiSelectionSpinner extends PopupTrigger
{
    private final List<OnItemsSelectedListener> onItemsSelectedListeners = new ArrayList<>();
    private final List<OnItemsMarkedListener> onItemsMarkedListeners = new ArrayList<>();

    private TreeSet<Integer> selectedItemsIndexes = new TreeSet<>();
    private TreeSet<Integer> markedItemsIndexes = new TreeSet<>();

    private Integer okButtonIndex;
    private Integer cancelButtonIndex;

    //region CONSTRUCTORS VARIATIONS
    public MultiSelectionSpinner(Context context, Integer okButtonIndex, Integer cancelButtonIndex)
    {
        this(context);

        if (okButtonIndex != null && okButtonIndex.equals(cancelButtonIndex))
        {
            throw new IllegalArgumentException("okButton and cancelButton can't be the same button");
        }

        var itemsCount = ((ViewGroup) popupWindow.getContentView()).getChildCount();
        if (okButtonIndex != null)
        {
            ObjectsVerifiers.requireInRange(0, itemsCount, okButtonIndex, "'okButtonIndex' must be between 0 to items count");
        }
        if (cancelButtonIndex != null)
        {
            ObjectsVerifiers.requireInRange(0, itemsCount, cancelButtonIndex, "'cancelButtonIndex' must be between 0 to items count");
        }

        this.okButtonIndex = okButtonIndex;
        this.cancelButtonIndex = cancelButtonIndex;
    }

    public MultiSelectionSpinner(Context context)
    {
        this(context, null);
    }

    public MultiSelectionSpinner(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MultiSelectionSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }
    //endregion

    //MAIN CONSTRUCTOR - any constructor should call this constructor eventually.
    public MultiSelectionSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        setFromAttrs(attrs, defStyleAttr, defStyleRes);

        findMarkedItems();

        setOnOkButtonClick();
        setOnCancelButtonClick();
    }

    //region INIT
    private void setFromAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        var attributes = getContext().obtainStyledAttributes(attrs, R.styleable.MultiSelectionSpinner, defStyleAttr, defStyleRes);

        setCloseOnItemClick(attributes.getBoolean(R.styleable.MultiSelectionSpinner_closeOnItemClick, false));

        var ok = attributes.getInteger(R.styleable.MultiSelectionSpinner_okButtonIndex, -1);
        this.okButtonIndex = ObjectsVerifiers.ifEqualReturnAlternative(ok, -1, null);

        var cancel = attributes.getInteger(R.styleable.MultiSelectionSpinner_cancelButtonIndex, -1);
        this.cancelButtonIndex = ObjectsVerifiers.ifEqualReturnAlternative(cancel, -1, null);

        attributes.recycle();
    }

    private void findMarkedItems()
    {
        var popupLayout = (ViewGroup) popupWindow.getContentView();

        for (int i=0; i<popupLayout.getChildCount(); i++)
        {
            if (!Objects.equals(okButtonIndex, i) && !Objects.equals(cancelButtonIndex, i) && isItemMarked(i))
            {
                markedItemsIndexes.add(getActualItemIndex(i));
            }
        }
    }

    private void setOnOkButtonClick()
    {
        if (okButtonIndex != null)
        {
            getItemAt(okButtonIndex).setOnClickListener(this::onOkButtonClick);
        }
    }

    private void setOnCancelButtonClick()
    {
        if (cancelButtonIndex != null)
        {
            getItemAt(cancelButtonIndex).setOnClickListener(this::onCancelButtonClick);
        }
    }
    //endregion

    @Override
    protected void onItemClick(int itemPosition)
    {
        markUnmarkItem(itemPosition);
    }

    private void onOkButtonClick(View view)
    {
        notifyItemsSelected();

        setOpen(false);
    }

    private void onCancelButtonClick(View view)
    {
        setOpen(false);
    }

    //region LISTENERS NOTIFIERS
    private void notifyItemsSelected()
    {
        updateSelectedItemsIndexes();

        onItemsSelectedListeners.forEach(listener -> listener.onItemsSelected(getSelectedItemsIndexes()));
    }

    private void notifyItemsMarked()
    {
        onItemsMarkedListeners.forEach(listener -> listener.onItemsMarked(getSelectedItemsIndexes()));
    }
    //endregion

    //region GETTERS
    public int[] getSelectedItemsIndexes()
    {
        return toIntArray(selectedItemsIndexes);
    }

    public int[] getMarkedItemIndexes()
    {
        return toIntArray(markedItemsIndexes);
    }
    //endregion

    //region SETTERS
    public void setMarkedItemsIndexes(int... markedItemsIndexes)
    {
        this.markedItemsIndexes = Arrays.stream(markedItemsIndexes).boxed().collect(Collectors.toCollection(TreeSet::new));

        notifyItemsMarked();
    }

    public void setSelectedItemsIndexes(int... selectedItemsIndexes)
    {
        setMarkedItemsIndexes(selectedItemsIndexes);

        notifyItemsSelected();
    }

    public void addOnItemsSelectedListener(OnItemsSelectedListener onItemsSelectedListener)
    {
        onItemsSelectedListeners.add(onItemsSelectedListener);
    }

    public void removeOnItemsSelectedListener(OnItemsSelectedListener onItemsSelectedListener)
    {
        onItemsSelectedListeners.remove(onItemsSelectedListener);
    }

    public void addOnItemsMarkedListener(OnItemsMarkedListener onItemsMarkedListener)
    {
        onItemsMarkedListeners.add(onItemsMarkedListener);
    }

    public void removeOnItemsMarkedListener(OnItemsMarkedListener onItemsMarkedListener)
    {
        onItemsMarkedListeners.remove(onItemsMarkedListener);
    }
    //endregion

    private int getActualItemIndex(int itemPosition)
    {
        if (okButtonIndex != null && itemPosition > okButtonIndex)
        {
            itemPosition--;
        }
        if (cancelButtonIndex != null && itemPosition > cancelButtonIndex)
        {
            itemPosition--;
        }

        return itemPosition;
    }

    private void markUnmarkItem(int itemPosition)
    {
        setItemMarked(itemPosition, !isItemMarked(itemPosition));
    }

    private void setItemMarked(int itemPosition, boolean marked)
    {
        var item = getItemAt(itemPosition);

        item.setAlpha(marked ? 1 : getDisabledAlpha());

        var normalizedItemPosition = getActualItemIndex(itemPosition);

        if (marked)
        {
            markedItemsIndexes.add(normalizedItemPosition);
        }
        else
        {
            markedItemsIndexes.remove(normalizedItemPosition);
        }
    }

    private boolean isItemMarked(int itemPosition)
    {
        var item = getItemAt(itemPosition);

        return item.getAlpha() == 1;
    }

    private void updateSelectedItemsIndexes()
    {
        selectedItemsIndexes = markedItemsIndexes;
        markedItemsIndexes = new TreeSet<>(markedItemsIndexes);

        notifyItemsMarked();
    }

    private int[] toIntArray(Collection<Integer> collection)//todo create array utils?
    {
        return collection.stream().mapToInt(i -> i).toArray();
    }

    @Override
    protected Class<? extends View> getAllowedPopupViewType()
    {
        return ViewGroup.class;
    }

    //region LISTENERS INTERFACES
    public interface OnItemsSelectedListener
    {
        void onItemsSelected(int[] selectedItemsIndexes);
    }

    public interface OnItemsMarkedListener
    {
        void onItemsMarked(int[] markedItemsIndexes);
    }
    //endregion
}