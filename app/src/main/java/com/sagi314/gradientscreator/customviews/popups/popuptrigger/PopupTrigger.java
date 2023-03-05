package com.sagi314.gradientscreator.customviews.popups.popuptrigger;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.sagi314.gradientscreator.R;
import com.sagi314.gradientscreator.customviews.movablepopupwindow.MovablePopupWindow;
import com.sagi314.gradientscreator.utils.staticutils.Validators;

import java.util.ArrayList;
import java.util.List;

public abstract class PopupTrigger extends View
{
    protected abstract void onItemClick(int itemPosition);

    protected MovablePopupWindow popupWindow;

    private final List<OnDismissListener> onDismissListeners = new ArrayList<>();

    private boolean closeOnItemClick;

    private float disabledAlpha;

    //region CONSTRUCTORS VARIATIONS
    protected PopupTrigger(Context context)
    {
        this(context, null);
    }

    protected PopupTrigger(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    protected PopupTrigger(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }
    //endregion

    //MAIN CONSTRUCTOR - any constructor should call this constructor eventually.
    protected PopupTrigger(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initPopupWindow();
        setFromAttrs(attrs, defStyleAttr, defStyleRes);
        setOnClickListener();
        setOnPopupDismissListener();
    }

    //region INIT
    private void initPopupWindow()
    {
        popupWindow = new MovablePopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setFromAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        var attributes = getContext().obtainStyledAttributes(attrs, R.styleable.PopupTrigger, defStyleAttr, defStyleRes);

        setPopupWidth(attributes.getLayoutDimension(R.styleable.PopupTrigger_popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        setPopupHeight(attributes.getLayoutDimension(R.styleable.PopupTrigger_popupHeight, ViewGroup.LayoutParams.WRAP_CONTENT));
        setPopupViewFromAttributes(attributes, R.styleable.PopupTrigger_popupView);
        setPopupLocation(attributes.getInt(R.styleable.PopupTrigger_popupLocation, 0));
        setCloseOnOutsideClick(attributes.getBoolean(R.styleable.PopupTrigger_closeOnOutsideClick, true));
        setDisabledAlpha(attributes.getFloat(R.styleable.PopupTrigger_disabledAlpha, 0.3f));
        setEnabled(attributes.getBoolean(R.styleable.PopupTrigger_android_enabled, true));
        setCloseOnItemClick(attributes.getBoolean(R.styleable.PopupTrigger_closeOnItemClick, true));

        attributes.recycle();
    }

    private void setOnClickListener()
    {
        setOnClickListener(ignoredView -> setOpen(!isOpen()));
    }

    private void setOnPopupDismissListener()
    {
        popupWindow.setOnDismissListener(this::notifyPopupDismissed);

        addOnDismissListener(this::onPopupDismissed);
    }
    //endregion

    //region GETTERS
    public int getPopupLocation()
    {
        return popupWindow.getLocation();
    }

    public boolean isCloseOnOutSideClick()
    {
        return popupWindow.isOutsideTouchable();
    }

    public boolean isCloseOnItemClick()
    {
        return closeOnItemClick;
    }

    public boolean isOpen()
    {
        return popupWindow.isShowing();
    }

    public float getDisabledAlpha()
    {
        return disabledAlpha;
    }

    public boolean isItemEnabled(int itemPosition)
    {
        var item = getItemAt(itemPosition);

        return isViewEnabled(item);
    }

    public int getPopupWidth()
    {
        return popupWindow.getWidth();
    }

    public int getPopupHeight()
    {
        return popupWindow.getHeight();
    }

    public int getPopupItemsCount()
    {
        var popupContentView = popupWindow.getContentView();
        if (!(popupContentView instanceof ViewGroup))
        {
            return 0;
        }

        return ((ViewGroup) popupWindow.getContentView()).getChildCount();
    }

    @Override
    public boolean isEnabled()
    {
        return isViewEnabled(this);
    }
    //endregion

    //region SETTERS
    public void setPopupLocation(int popupLocation)
    {
        popupWindow.setLocation(popupLocation);
    }

    public void setCloseOnOutsideClick(boolean closeOnOutsideClick)
    {
        popupWindow.setOutsideTouchable(closeOnOutsideClick);

        if (closeOnOutsideClick && isOpen())
        {
            pauseClicksDetection();
        }
    }

    public void setPopupView(View popupView)
    {
        var allowedPopupViewType = Validators.requireNotNull(getAllowedPopupViewType(), "make 'getAllowedPopupViewType()' return a non-null Class");
        Validators.requireOfClass(popupView, allowedPopupViewType, "'popupView' must be instance of " + allowedPopupViewType.getName());

        popupWindow.setContentView(popupView);

        setItemsListeners(popupView);
    }

    public void addOnDismissListener(OnDismissListener onDismissListener)
    {
        Validators.illegalNull(onDismissListener, "can't add a null listener");

        onDismissListeners.add(onDismissListener);
    }

    public void removeOnDismissListener(OnDismissListener onDismissListener)
    {
        Validators.illegalNull(onDismissListener, "can't remove a null listener");

        onDismissListeners.remove(onDismissListener);
    }

    public void setCloseOnItemClick(boolean closeOnItemClick)
    {
        this.closeOnItemClick = closeOnItemClick;
    }

    public void setOpen(boolean open)
    {
        if (open)
        {
            popupWindow.showAsDropDown(this);

            pauseClicksDetection();
        }
        else
        {
            popupWindow.dismiss();
        }
    }

    public void setItemsEnabled(boolean enabled, int... itemsPositions)
    {
        Validators.requireNotNull(itemsPositions, "'itemsPositions can't be null");

        for (int itemPosition : itemsPositions)
        {
            var item = getItemAt(itemPosition);

            if (item != null)
            {
                setViewEnabled(item, enabled);
            }
        }
    }

    public void setDisabledAlpha(float disabledAlpha)
    {
        this.disabledAlpha = disabledAlpha;
    }

    public void setPopupWidth(int width)
    {
        popupWindow.setWidth(width);
    }

    public void setPopupHeight(int height)
    {
        popupWindow.setHeight(height);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        setViewEnabled(this, enabled);
    }
    //endregion

    //region PRIVATE HELPERS
    private void setItemsListeners(View popupView)
    {
        if (popupView instanceof ViewGroup)
        {
            var popupLayout = (ViewGroup) popupView;

            for (int i = 0; i < popupLayout.getChildCount(); i++)
            {
                var item = popupLayout.getChildAt(i);

                final int itemPosition = i;
                item.setOnClickListener(ignoredView -> itemClicked(itemPosition));
            }
        }
    }

    private void setPopupViewFromAttributes(TypedArray attributes, int viewResourceIndex)
    {
        var viewResource = attributes.getResourceId(viewResourceIndex, 0);
        if (viewResource == 0)
        {
            throw new IllegalArgumentException("'popupView' XML property is missing");
        }

        var inflatedView = LayoutInflater.from(getContext()).inflate(viewResource, null, false);
        setPopupView(inflatedView);
    }

    private void closePopupIfNeeded()
    {
        if (closeOnItemClick)
        {
            setOpen(false);
        }
    }
    //endregion

    //region LISTENERS NOTIFIERS
    private void notifyPopupDismissed()
    {
        onDismissListeners.forEach(OnDismissListener::onDismiss);
    }
    //endregion

    private void itemClicked(int itemPosition)
    {
        onItemClick(itemPosition);

        closePopupIfNeeded();
    }

    private void onPopupDismissed()
    {
        postDelayed(this::resumeClicksDetection, 200);
    }

    private void pauseClicksDetection()
    {
        setClickable(false);
    }

    private void resumeClicksDetection()
    {
        setClickable(true);
    }

    protected View getItemAt(int position)
    {
        var popupView = popupWindow.getContentView();

        if (popupView instanceof ViewGroup)
        {
            var popupLayout = (ViewGroup) popupView;

            if (0 <= position && position < popupLayout.getChildCount())
            {
                return popupLayout.getChildAt(position);
            }
        }

        return null;
    }

    protected void setViewEnabled(View view, boolean enabled)
    {
        Validators.requireNotNull(view, "view can't be null");

        view.setAlpha(enabled ? 1 : getDisabledAlpha());
        view.setClickable(enabled);
    }

    protected boolean isViewEnabled(View view)
    {
        Validators.requireNotNull(view, "'view' can't be null");

        return view.isClickable() && view.getAlpha() != getDisabledAlpha();
    }

    protected Class<? extends View> getAllowedPopupViewType()
    {
        return View.class;
    }

    public interface OnDismissListener
    {
        void onDismiss();
    }
}