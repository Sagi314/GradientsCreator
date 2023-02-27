package com.sbmgames.gradientscreator.customviews.widthbylettersedittext;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sbmgames.gradientscreator.R;

public class WidthByLettersEditText extends androidx.appcompat.widget.AppCompatEditText
{
    private static final int NO_LETTERS_COUNT = -1;

    private char calculateByLetter;
    private int lettersCount;

    public WidthByLettersEditText(@NonNull Context context)
    {
        this(context, null);
    }

    public WidthByLettersEditText(@NonNull Context context, char calculateByLetter, int lettersCount)
    {
        this(context);

        setCalculateByLetter(calculateByLetter);
        setLettersCount(lettersCount);
    }

    public WidthByLettersEditText(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        var attributes = getContext().obtainStyledAttributes(attrs, R.styleable.WidthByLettersEditText);

        lettersCount = attributes.getInt(R.styleable.WidthByLettersEditText_letters_count, NO_LETTERS_COUNT);

        var calcByLetterAttr = attributes.getString(R.styleable.WidthByLettersEditText_calculate_by_letter);
        calculateByLetter = calcByLetterAttr == null ? 'M' : calcByLetterAttr.charAt(0);

        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (lettersCount != NO_LETTERS_COUNT)
        {
            var letterWidth = getPaint().measureText("" + calculateByLetter);

            //we want the underline to go all the way under the text, so we add one to the letters count.
            //but if we add one, there will be enough space for an extra letter.
            //so we are decreasing by one pixel so it won't have enough space for the extra.
            var width = (lettersCount + 1) * letterWidth - 1;
            getLayoutParams().width = (int) width;
        }
    }

    //region GETTERS
    public char getCalculateByLetter()
    {
        return calculateByLetter;
    }

    public int getLettersCount()
    {
        return lettersCount;
    }
    //endregion

    //region SETTERS
    public void setCalculateByLetter(char calculateByLetter)
    {
        this.calculateByLetter = calculateByLetter;
    }

    public void setLettersCount(int lettersCount)
    {
        this.lettersCount = Math.max(lettersCount, NO_LETTERS_COUNT);
    }
    //endregion
}