package com.sagi314.gradientscreator.customviews.colorpickerdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.sagi314.gradientscreator.utils.staticutils.Validators;
import com.sagi314.roundcolorpicker.RoundColorPicker;
import com.sagi314.gradientscreator.utils.staticutils.ColorUtils;
import com.sagi314.gradientscreator.R;
import com.sagi314.gradientscreator.customviews.addons.edittext.adapters.TextWatcherAdapter;
import com.sagi314.gradientscreator.customviews.addons.edittext.inputfilters.Hexadecimal;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerDialog extends Dialog
{
    private static final int MAX_HEXADECIMAL_TEXT_LENGTH = 6;

    private final List<OnColorSelectedListener> onColorSelectedListeners = new ArrayList<>();

    private RoundColorPicker colorPicker;

    private EditText hexadecimalEditText;

    private View okButton;
    private View cancelButton;

    //todo consider removing "tag" and let the user handle it themself
    private String tag = "";

    private boolean doNotSetColorFromTextOneTime = false;

    public ColorPickerDialog(@NonNull Context context, int color)
    {
        this(context);

        selectColor(color, true);
    }

    public ColorPickerDialog(@NonNull Context context)
    {
        super(context);
        setContentView(R.layout.dialog_round_color_picker);

        init();
    }

    private void findViews()
    {
        this.colorPicker = findViewById(R.id.colorPicker);

        this.hexadecimalEditText = findViewById(R.id.hexadecimalColorCode);

        this.okButton = findViewById(R.id.okButton);
        this.cancelButton = findViewById(R.id.cancelButton);
    }

    //region INIT METHODS
    private void init()
    {
        removeDialogBackground();
        findViews();

        hexadecimalEditText.setText(ColorUtils.colorToHex(colorPicker.getSelectedColor()).substring(1));
        setHexadecimalEditTextFilters();
        setOnHexadecimalTextChanged();

        setOnOkButtonClickListener();
        setOnCancelButtonClickListener();

        setOnColorChangedListener();
    }

    private void removeDialogBackground()
    {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setHexadecimalEditTextFilters()
    {
        var filters = new InputFilter[]
                {
                        new Hexadecimal(),
                        new InputFilter.LengthFilter(MAX_HEXADECIMAL_TEXT_LENGTH),
                        new InputFilter.AllCaps(),
                };

        hexadecimalEditText.setFilters(filters);
    }

    private void setOnHexadecimalTextChanged()
    {
        hexadecimalEditText.addTextChangedListener(new TextWatcherAdapter()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (doNotSetColorFromTextOneTime)
                {
                    doNotSetColorFromTextOneTime = false;
                }
                else
                {
                    var color = ColorUtils.hexToColor(s.toString(), false);

                    selectColor(color, false);
                }
            }
        });
    }

    private void setOnOkButtonClickListener()
    {
        okButton.setOnClickListener(v ->
        {
            onColorSelectedListeners.forEach(listener ->
                    listener.onColorSelected(colorPicker.getSelectedColor(), tag));

            dismiss();
        });
    }

    private void setOnCancelButtonClickListener()
    {
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void setOnColorChangedListener()
    {
        colorPicker.setOnColorChangedListener(color ->
        {
            var hexColor = ColorUtils.colorToHex(color);

            doNotSetColorFromTextOneTime = true;

            hexadecimalEditText.setText(hexColor);
        });
    }
    //endregion

    //region SETTERS
    public void addOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener)
    {
        Validators.requireNotNull(onColorSelectedListener, "can't add a null listener");

        this.onColorSelectedListeners.add(onColorSelectedListener);
    }

    public void selectColor(int color, boolean sendColorChangedEvent)
    {
        colorPicker.selectColor(color, sendColorChangedEvent);
    }

    public void selectColor(int color)
    {
        colorPicker.selectColor(color, true);
    }
    //endregion

    //region GETTERS
    public int getSelectedColor()
    {
        return colorPicker.getSelectedColor();
    }

    public String getTag()
    {
        return tag;
    }
    //endregion

    public void show(String tag)
    {
        this.tag = tag;

        show();
    }

    public void show(int color, String tag)
    {
        selectColor(color);

        show(tag);
    }

    public interface OnColorSelectedListener
    {
        void onColorSelected(int color, String tag);
    }
}