package com.sagi314.gradientscreator.utils.staticutils;

import android.graphics.Color;

public class ColorUtils
{
    public static float getBrightness(int color)
    {
        float[] hsv = new float[3];

        Color.colorToHSV(color, hsv);
        return hsv[2];
    }

    public static String colorToHex(int color)
    {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static int hexToColor(String hexColor, boolean hasAlpha)
    {
        int maxChars = hasAlpha ? 8 : 6;

        //too long color
        if (hexColor.length() > maxChars)
        {
            var conditionalAddition = maxChars == 6 && hexColor.length() == 8 ? ". If you want an hexColor with alpha digits change hasAlpha to true" : "";

            throw new IllegalArgumentException("hexColor String length exceeded the maximum allowed of " + maxChars + conditionalAddition);
        }

        //if the hexColor's length is half the maxSize we will duplicate each char: "F00" -> "FF0000"
        if (hexColor.length() == maxChars / 2)
        {
            var builder = new StringBuilder();
            for (char c : hexColor.toCharArray())
            {
                builder.append(c).append(c);
            }

            hexColor = builder.toString();
        }

        //if its not half maxSize's length we will fill the missing chars with 'F's
        hexColor += ("F").repeat(maxChars - hexColor.length());

        return Color.parseColor("#" + hexColor);
    }

    //prevents creating instances
    private ColorUtils() { }
}
