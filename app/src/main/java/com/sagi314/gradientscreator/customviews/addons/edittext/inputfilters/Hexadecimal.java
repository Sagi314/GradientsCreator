package com.sagi314.gradientscreator.customviews.addons.edittext.inputfilters;

import android.text.InputFilter;
import android.text.Spanned;

public class Hexadecimal implements InputFilter
{
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd)
    {
        var result = new StringBuilder();

        for (int i = start; i < end; i++)
        {
            char c = source.charAt(i);

            //check that the char is hexadecimal - if yes, add it to the 'result' string.
            if (Character.digit(c, 16) != -1)
            {
                result.append(c);
            }
        }

        return result.toString();
    }
}