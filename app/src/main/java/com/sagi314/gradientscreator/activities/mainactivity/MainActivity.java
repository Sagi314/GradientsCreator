package com.sagi314.gradientscreator.activities.mainactivity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sagi314.gradientscreator.R;
import com.sagi314.gradientscreator.customviews.gradientdrawablepicker.GradientDrawablePicker;
import com.sagi314.gradientscreator.customviews.popups.spinners.multiselectionspinner.MultiSelectionSpinner;
import com.sagi314.gradientscreator.utils.staticutils.DrawableUtils;
import com.sagi314.gradientscreator.utils.staticutils.FilesUtils;
import com.sagi314.gradientscreator.utils.Flags;
import com.sagi314.gradientscreator.utils.staticutils.WallpaperUtils;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
//todo add doc
public class MainActivity extends AppCompatActivity
{
    private static final String DIRECTORY_NAME_GRADIENTS_CREATOR_IMAGES = "gradientsCreatorImages";

    private GradientDrawablePicker gradientDrawablePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeActivityFullScreen();

        init();
    }

    private void makeActivityFullScreen()
    {
        var window = getWindow();

        window.setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,  WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //we want the layout to look like this: [https://imgur.com/9u4Stia] - only with status bar overlaying the layout. no bottom bar, no bottom line. couldn't find a way to do it at the moment of writing this line (31/01/23) without using deprecated method.
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    //region INIT
    private void init()
    {
        initGradientDrawablePicker();

        setLayoutBackground();

        initSaveToFilesViewHandler();

        new SaveAsWallpaperSpinnerViewHandler();
    }

    private void initGradientDrawablePicker()
    {
        gradientDrawablePicker = findViewById(R.id.gradient_drawable_picker);

        var windowSize = getWindowSize();
        var biggerWindowSide = Math.max(windowSize.x, windowSize.y);
        gradientDrawablePicker.setGradientMaxRadius(biggerWindowSide);
    }

    private void setLayoutBackground()
    {
        var layout = findViewById(R.id.layout);

        layout.setBackground(gradientDrawablePicker.getGradientDrawable());
    }

    private void initSaveToFilesViewHandler()
    {
        var deviceImagesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        new SaveToFilesButtonViewHandler(deviceImagesFolder);
    }
    //endregion

    //region SHARED METHODS
    private Bitmap getGradientDrawableAsBitmap(int width, int height)
    {
        return DrawableUtils.drawableToBitmap(gradientDrawablePicker.getGradientDrawable(), width, height);
    }

    private void announce(String message)
    {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private Point getWindowSize()
    {
        var screenSize = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            var screenBounds = getSystemService(WindowManager.class).getCurrentWindowMetrics().getBounds();
            screenSize.widthPixels = screenBounds.width();
            screenSize.heightPixels = screenBounds.height();
        }
        else
        {
            getWindowManager().getDefaultDisplay().getRealMetrics(screenSize);//NOSONAR - using deprecated for lower api versions
        }

        return new Point(screenSize.widthPixels, screenSize.heightPixels);
    }
    //endregion

    private class SaveAsWallpaperSpinnerViewHandler
    {
        private static final int FLAG_HOME_SCREEN = 0;
        private static final int FLAG_LOCK_SCREEN = 1;

        private final MultiSelectionSpinner saveAsWallpaperSpinner;

        public SaveAsWallpaperSpinnerViewHandler()
        {
            saveAsWallpaperSpinner = findViewById(R.id.save_as_wallpaper_spinner);
            setOnSelectionListener();
        }

        private void setOnSelectionListener()
        {
            saveAsWallpaperSpinner.addOnItemsSelectedListener(this::onItemsSelected);
        }

        private void onItemsSelected(int[] selectedIndexes)
        {
            final var flags = new Flags(selectedIndexes);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() ->
            {
                if (flags.isAnythingOn())
                {
                    final var prefix = "Saved as ";
                    var message = new StringBuilder(prefix);

                    if (flags.isOn(FLAG_HOME_SCREEN) && saveGradientAsWallpaper(FLAG_HOME_SCREEN))
                    {
                        message.append("home screen");
                    }
                    if (flags.isOn(FLAG_LOCK_SCREEN) && saveGradientAsWallpaper(FLAG_LOCK_SCREEN))
                    {
                        if (!message.toString().equals(prefix))
                        {
                            message.append(" & as ");
                        }

                        message.append("lock screen");
                    }

                    announce(message.toString());
                }
            });
        }

        private boolean saveGradientAsWallpaper(int to)
        {
            var screenSize = getWindowSize();
            var bitmap = getGradientDrawableAsBitmap(screenSize.x, screenSize.y);

            boolean saved;

            switch (to)
            {
                case FLAG_HOME_SCREEN:
                    saved = WallpaperUtils.trySaveToScreen(MainActivity.this, bitmap, WallpaperUtils.Screen.HOME);
                    break;
                case FLAG_LOCK_SCREEN:
                    saved = WallpaperUtils.trySaveToScreen(MainActivity.this, bitmap, WallpaperUtils.Screen.LOCK);
                    break;
                default:
                    saved = false;
            }

            bitmap.recycle();

            return saved;
        }
    }

    private class SaveToFilesButtonViewHandler
    {
        private static final String GRADIENT_IMAGE_PREFIX = "gradient";
        private static final String GRADIENT_IMAGE_SUFFIX = ".jpg";

        private final View saveToFilesButton;
        private final File folderToSaveIn;

        public SaveToFilesButtonViewHandler(File folderToSaveIn)
        {
            this.folderToSaveIn = Objects.requireNonNull(folderToSaveIn, "'folderToSaveIn' can't be null");
            saveToFilesButton = findViewById(R.id.save_to_Files_button);

            makeSureFolderToSaveInExist();
            setOnButtonClickListener();
        }

        private void makeSureFolderToSaveInExist()
        {
            FilesUtils.createDirectoryAndPathEvenIfFileExists(folderToSaveIn, DIRECTORY_NAME_GRADIENTS_CREATOR_IMAGES);
        }

        private void setOnButtonClickListener()
        {
            saveToFilesButton.setOnClickListener(this::onButtonClick);
        }

        private void onButtonClick(View view)
        {
            saveDrawableToFiles();
        }

        private void saveDrawableToFiles()
        {
            var screenSize = getWindowSize();
            var bitmap = getGradientDrawableAsBitmap(screenSize.x,screenSize.y);

            var file = FilesUtils.createFileWithTimeStamp(folderToSaveIn, GRADIENT_IMAGE_PREFIX, GRADIENT_IMAGE_SUFFIX);

            if (FilesUtils.saveBitmapToFile(bitmap, Bitmap.CompressFormat.JPEG, file))
            {
                announce("Saved as image");
            }

            bitmap.recycle();
        }
    }
}