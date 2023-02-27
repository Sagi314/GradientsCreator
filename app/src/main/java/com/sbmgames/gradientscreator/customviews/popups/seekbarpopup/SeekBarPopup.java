package com.sbmgames.gradientscreator.customviews.popups.seekbarpopup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sbmgames.gradientscreator.R;
import com.sbmgames.gradientscreator.customviews.popups.popuptrigger.PopupTrigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SeekBarPopup extends PopupTrigger
{
    private final List<OnProgressChangedListener> onProgressChangedListeners = new ArrayList<>();

    private final Paint numberPaint = new Paint();

    private SeekBar seekBar;

    private String textPrefix;
    private String textSuffix;

    //region CONSTRUCTORS VARIATIONS
    public SeekBarPopup(Context context)
    {
        this(context, null);
    }

    public SeekBarPopup(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SeekBarPopup(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }
    //endregion

    //MAIN CONSTRUCTOR - any constructor should call this constructor eventually.
    public SeekBarPopup(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initSeekBarListeners();
        initPaint();

        setFromAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void setFromAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        var attributes = getContext().obtainStyledAttributes(attrs, R.styleable.SeekBarPopup, defStyleAttr, defStyleRes);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            setMinProgress(attributes.getInt(R.styleable.SeekBarPopup_minProgress, 0));
        }
        setMaxProgress(attributes.getInt(R.styleable.SeekBarPopup_maxProgress, 0));
        setProgress(attributes.getInt(R.styleable.SeekBarPopup_progress, 0));

        setColor(attributes.getColor(R.styleable.SeekBarPopup_textColor, Color.BLACK));

        var spUnit = getContext().getResources().getDisplayMetrics().scaledDensity;
        setTextSize(attributes.getDimension(R.styleable.SeekBarPopup_textSize, 14 * spUnit));

        setTextPrefix(Objects.requireNonNullElse(attributes.getString(R.styleable.SeekBarPopup_textPrefix), ""));
        setTextSuffix(Objects.requireNonNullElse(attributes.getString(R.styleable.SeekBarPopup_textSuffix), ""));

        attributes.recycle();
    }

    //region INIT
    private void initSeekBarListeners()
    {
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                notifyProgressChanged();

                invalidate();
            }
        });
    }

    private void initPaint()
    {
        numberPaint.setTextAlign(Paint.Align.LEFT);
    }
    //endregion

    //region SETTERS
    public void setColor(int color)
    {
        numberPaint.setColor(color);
    }

    public void setTextSize(float textSize)
    {
        numberPaint.setTextSize(textSize);
    }

    public void setProgress(int progress)
    {
        if (seekBar != null)
        {
            seekBar.setProgress(progress);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setMinProgress(int minProgress)
    {
        seekBar.setMin(minProgress);
    }

    public void setMaxProgress(int maxProgress)
    {
        if (seekBar != null)
        {
            seekBar.setMax(maxProgress);
        }
    }

    public void setTextPrefix(String textPrefix)
    {
        this.textPrefix = textPrefix;
    }

    public void setTextSuffix(String textSuffix)
    {
        this.textSuffix = textSuffix;
    }

    public void addOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener)
    {
        onProgressChangedListeners.add(onProgressChangedListener);

        onProgressChangedListener.onProgressChanged(getProgress());
    }

    public void removeOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener)
    {
        onProgressChangedListeners.remove(onProgressChangedListener);
    }
    //endregion

    //region GETTERS
    public int getColor()
    {
        return numberPaint.getColor();
    }

    public float getTextSize()
    {
        return numberPaint.getTextSize();
    }

    public int getProgress()
    {
        return seekBar != null ? seekBar.getProgress() : 0;
    }

    public int getMaxProgress()
    {
        return seekBar != null ? seekBar.getMax() : 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getMinProgress()
    {
        return seekBar != null ? seekBar.getMin() : 0;
    }

    public String getTextPrefix()
    {
        return textPrefix;
    }

    public String getTextSuffix()
    {
        return textSuffix;
    }
    //endregion

    @Override
    protected final Class<? extends View> getAllowedPopupViewType()
    {
        return SeekBar.class;
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);

        var text = String.format(Locale.ENGLISH, "%s%d%s", textPrefix, getProgress(), textSuffix);
        var width = numberPaint.measureText(text);

        var textBounds = new Rect();
        numberPaint.getTextBounds(text, 0, text.length(), textBounds);

        canvas.drawText(text, (getWidth() - width) / 2, (getHeight() + textBounds.height()) / 2f, numberPaint);
    }

    @Override
    protected void onItemClick(int itemPosition)
    {
        //we don't have to do anything on seekbar click
    }

    @Override
    public void setPopupView(View popupView)
    {
        super.setPopupView(popupView);

        if (popupView instanceof SeekBar)
        {
            seekBar = (SeekBar) popupView;
        }
    }

    private void notifyProgressChanged()
    {
        var progress = getProgress();

        onProgressChangedListeners.forEach(listener -> listener.onProgressChanged(progress));
    }

    private interface OnSeekBarChangeListener extends SeekBar.OnSeekBarChangeListener
    {
        @Override
        default void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {

        }

        @Override
        default void onStartTrackingTouch(SeekBar seekBar)
        {

        }

        @Override
        default void onStopTrackingTouch(SeekBar seekBar)
        {

        }
    }

    public interface OnProgressChangedListener
    {
        void onProgressChanged(int progress);
    }
}
