package com.sagi314.gradientscreator.utils.staticutils;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Utility class for managing device's wallpapers
 */
public final class WallpaperUtils
{
    public enum Screen
    {
        HOME,
        LOCK,
        BOTH
    }

    private static final int HOME_SCREEN = WallpaperManager.FLAG_SYSTEM;
    private static final int LOCK_SCREEN = WallpaperManager.FLAG_LOCK;

    public static boolean trySaveToScreen(Context context, Bitmap bitmap, Screen screen)
    {
        Validators.illegalNull(context, "'context' can't be null");
        Validators.illegalNull(bitmap, "'bitmap' can't be null");
        Validators.illegalNull(screen, "'screen' can't be null");

        var wallpaperManager = WallpaperManager.getInstance(context);

        try
        {
            if (screen == Screen.HOME || screen == Screen.BOTH)
            {
                wallpaperManager.setBitmap(bitmap, null, true, HOME_SCREEN);
            }
            if (screen == Screen.LOCK || screen == Screen.BOTH)
            {
                wallpaperManager.setBitmap(bitmap, null, true, LOCK_SCREEN);
            }

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return false;
        }
    }

    //prevents creating instances
    private WallpaperUtils() { }
}