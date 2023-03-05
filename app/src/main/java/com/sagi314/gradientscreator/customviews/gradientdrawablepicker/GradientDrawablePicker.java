package com.sagi314.gradientscreator.customviews.gradientdrawablepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sagi314.gradientscreator.R;
import com.sagi314.gradientscreator.customviews.addons.itemsdecorations.MarginForAllExceptLastDecoration;
import com.sagi314.gradientscreator.customviews.addons.itemtouchhelpers.ChangeItemPositionOnDrag;
import com.sagi314.gradientscreator.customviews.addons.itemtouchhelpers.DeleteOnSwipe;
import com.sagi314.gradientscreator.customviews.colorpickerdialog.ColorPickerDialog;
import com.sagi314.gradientscreator.customviews.colorslistadapter.ColorsListAdapter;
import com.sagi314.gradientscreator.customviews.popups.spinners.sbmspinner.SbmSpinner;
import com.sagi314.gradientscreator.customviews.popups.seekbarpopup.SeekBarPopup;
import com.sagi314.gradientscreator.models.ColorsList;
import com.sagi314.gradientscreator.utils.staticutils.Validators;

import java.util.List;

public class GradientDrawablePicker extends FrameLayout
{
    public static final String DEFAULT_ERROR = null;

    private static final String DEFAULT_ERROR_MIN_ITEM_DELETION = "can't delete this color";
    private static final String DEFAULT_ERROR_MAX_ITEM_ADDITION = "can't add another color";

    private final View inflatedView;
    private final ColorPickerDialog colorPickerDialog;

    private final ColorsList colorsList = new ColorsList();
    private final GradientDrawable gradientDrawable = new GradientDrawable();

    private GradientDirectionsSpinnerViewHandler gradientDirectionsSpinnerViewHandler;
    private GradientTypesSpinnerViewHandler gradientTypesSpinnerViewHandler;
    private GradientRadiusPopupViewHandler gradientRadiusPopupViewHandler;

    private int lastColorClickedPosition;
    private boolean restoreStates;
    private boolean applyGradientAsBackground;
    private String minItemDeletionError;
    private String maxItemAdditionError;

    public GradientDrawablePicker(Context context)
    {
        this(context, null);
    }

    public GradientDrawablePicker(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        inflatedView = View.inflate(context, R.layout.item_gradient_drawable_picker, this);
        colorPickerDialog = new ColorPickerDialog(context);

        setFromAttributes(attrs);

        init();
    }

    private void setFromAttributes(AttributeSet attrs)
    {
        var attributes = getContext().obtainStyledAttributes(attrs, R.styleable.GradientDrawablePicker);

        setRestoreStates(attributes.getBoolean(R.styleable.GradientDrawablePicker_restoreState, true));

        setApplyGradientAsBackground(attributes.getBoolean(R.styleable.GradientDrawablePicker_applyDrawableAsBackground, true));

        setMinimumColors(attributes.getInt(R.styleable.GradientDrawablePicker_minimumColors, ColorsList.NO_MINIMUM_COUNT));
        setMaximumColors(attributes.getInt(R.styleable.GradientDrawablePicker_maximumColors, ColorsList.NO_MAXIMUM_COUNT));

        setMinItemDeletionError(attributes.getString(R.styleable.GradientDrawablePicker_minItemDeletionError));
        setMaxItemAdditionError(attributes.getString(R.styleable.GradientDrawablePicker_maxItemAdditionError));

        var defaultColorsId = attributes.getResourceId(R.styleable.GradientDrawablePicker_defaultColors, 0);
        colorsList.set(defaultColorsId == 0 ? new int[0] : getContext().getResources().getIntArray(defaultColorsId));

        attributes.recycle();
    }

    //region INIT
    private void init()
    {
        addViewsHandlers();

        addOnColorsChangedListener();
        addColorSelectedFromPickerListener();
    }

    private void addOnColorsChangedListener()
    {
        colorsList.addListener(new OnAnyColorsChangeListener(this::onColorsListChanged));
    }

    private void addViewsHandlers()
    {
        new AddColorButtonViewHandler();
        new ColorsListViewViewHandler();
        gradientDirectionsSpinnerViewHandler = new GradientDirectionsSpinnerViewHandler();
        gradientRadiusPopupViewHandler = new GradientRadiusPopupViewHandler();
        gradientTypesSpinnerViewHandler = new GradientTypesSpinnerViewHandler();
    }

    private void addColorSelectedFromPickerListener()
    {
        colorPickerDialog.addOnColorSelectedListener(this::onColorPicked);
    }
    //endregion

    private void onColorsListChanged()
    {
        setGradientColors();
    }

    private void onColorPicked(int color, String tag)
    {
        if (tag.equals(PickerTag.ADD_COLOR))
        {
            colorsList.addToEnd(color);
        }
        else if (tag.equals(PickerTag.CHANGE_COLOR))
        {
            colorsList.setColorAt(color, lastColorClickedPosition);
        }
    }

    private void setGradientColors()
    {
        var colors = colorsList.getAll();

        if (colors.length == 0)
        {
            colors = new int[]{ Color.BLACK, Color.BLACK };
        }
        else if (colors.length == 1)
        {
            colors = new int[] { colors[0], colors[0] };
        }

        gradientDrawable.setColors(colors);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setGradientRadiusPercent(getGradientRadiusPercent());
    }

    //region PUBLIC GETTERS
    public boolean isRestoreStates()
    {
        return restoreStates;
    }

    public GradientDrawable getGradientDrawable()
    {
        return gradientDrawable;
    }

    public boolean isApplyGradientAsBackground()
    {
        return applyGradientAsBackground;
    }

    public String getMinItemDeletionError()
    {
        return minItemDeletionError;
    }

    public String getMaxItemAdditionError()
    {
        return maxItemAdditionError;
    }

    public int getSelectedTypePosition()
    {
        return gradientTypesSpinnerViewHandler.getSelectedPosition();
    }

    public int getSelectedDirectionPosition()
    {
        return gradientDirectionsSpinnerViewHandler.getSelectedPosition();
    }

    public int[] getColors()
    {
        return gradientDrawable.getColors();
    }

    public int getGradientRadiusPercent()
    {
        return gradientRadiusPopupViewHandler.getGradientRadiusPercent();
    }

    public float getGradientMaxRadius()
    {
        return gradientRadiusPopupViewHandler.getGradientMaxRadius();
    }
    //endregion

    //region PUBLIC SETTERS
    public void setRestoreStates(boolean restoreStates)
    {
        this.restoreStates = restoreStates;
    }

    public void setMinimumColors(int minimumColors)
    {
        colorsList.minimumCount(minimumColors);
    }

    public void setMaximumColors(int maximumColors)
    {
        colorsList.maximumCount(maximumColors);
    }

    public void setApplyGradientAsBackground(boolean applyGradientAsBackground)
    {
        this.applyGradientAsBackground = applyGradientAsBackground;

        setBackground(isApplyGradientAsBackground() ? gradientDrawable : null);
    }

    public void setMinItemDeletionError(String minItemDeletionError)
    {
        this.minItemDeletionError = minItemDeletionError == null ? DEFAULT_ERROR_MIN_ITEM_DELETION : minItemDeletionError;
    }

    public void setMaxItemAdditionError(String maxItemAdditionError)
    {
        this.maxItemAdditionError = maxItemAdditionError == null ? DEFAULT_ERROR_MAX_ITEM_ADDITION : maxItemAdditionError;
    }

    public void setGradientRadiusPercent(int percent)
    {


        gradientRadiusPopupViewHandler.setGradientRadiusPercent(percent);
    }

    public void setGradientMaxRadius(float maxRadius)
    {
        gradientRadiusPopupViewHandler.setGradientMaxRadius(maxRadius);
    }
    //endregion

    //region RESTORE STATE
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState()
    {
        if (restoreStates)
        {
            var bundle = new Bundle();

            bundle.putParcelable(BundleKeys.PARENT_STATE, super.onSaveInstanceState());

            bundle.putIntArray(BundleKeys.GRADIENT_COLORS, colorsList.getAll());
            bundle.putInt(BundleKeys.GRADIENT_TYPE, getSelectedTypePosition());
            bundle.putInt(BundleKeys.GRADIENT_DIRECTION, getSelectedDirectionPosition());
            bundle.putInt(BundleKeys.GRADIENT_RADIUS_PERCENT, getGradientRadiusPercent());

            bundle.putInt(BundleKeys.COLOR_PICKER_SELECTED_COLOR, colorPickerDialog.getSelectedColor());
            bundle.putBoolean(BundleKeys.COLOR_PICKER_IS_OPEN, colorPickerDialog.isShowing());
            bundle.putString(BundleKeys.COLOR_PICKER_PICKER_TAG, colorPickerDialog.getTag());

            return bundle;
        }

        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
       if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;

            if (restoreStates)
            {
                colorsList.set(bundle.getIntArray(BundleKeys.GRADIENT_COLORS));
                gradientTypesSpinnerViewHandler.setSelectedPosition(bundle.getInt(BundleKeys.GRADIENT_TYPE));
                gradientDirectionsSpinnerViewHandler.setSelectedPosition(bundle.getInt(BundleKeys.GRADIENT_DIRECTION));
                gradientRadiusPopupViewHandler.setGradientRadiusPercent(bundle.getInt(BundleKeys.GRADIENT_RADIUS_PERCENT));

                colorPickerDialog.selectColor(bundle.getInt(BundleKeys.COLOR_PICKER_SELECTED_COLOR));
                if (bundle.getBoolean(BundleKeys.COLOR_PICKER_IS_OPEN))
                {
                    colorPickerDialog.show(bundle.getString(BundleKeys.COLOR_PICKER_PICKER_TAG));
                }
            }

            var parcelable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                    bundle.getParcelable(BundleKeys.PARENT_STATE, Bundle.class):
                    bundle.getParcelable(BundleKeys.PARENT_STATE); //NOSONAR - using deprecated method for lower versions - no other way found
            super.onRestoreInstanceState(parcelable);
        }
    }
    //endregion

    //region VIEW HANDLERS
    /**
     * this class responsible for the {@link #colorsListView}.
     * <p>listens to the {@link #colorsList} changes and updates the items accordingly.</p>
     * <p>manages the deletion of an item and items rearrangement - notifies the {@link #colorsList} when it happens.</p>
     * <p>lets the user change color using {@link #colorPickerDialog} by clicking on the item.</p>
     * <p>add items decoration so the list will be better looking.</p>
     */
    private class ColorsListViewViewHandler
    {
        private final RecyclerView colorsListView;
        private final ColorsListAdapter colorsListViewAdapter;

        private ColorsListViewViewHandler()
        {
            colorsListView = inflatedView.findViewById(R.id.colors_list_recycler_view);
            colorsListViewAdapter = new ColorsListAdapter(colorsList, this::onColorClick);

            init();
        }

        //region INIT
        private void init()
        {
            setLayoutManager();
            setAdapter();
            applyItemDecorations();
            attachTouchHelpers();

            makeAdapterListenToColorsListChanges();
        }

        private void setLayoutManager()
        {
            colorsListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }

        private void setAdapter()
        {
            colorsListView.setAdapter(colorsListViewAdapter);
        }

        private void applyItemDecorations()
        {
            var itemsMargin = getResources().getDimensionPixelSize(R.dimen.color_circle_margin);
            colorsListView.addItemDecoration(new MarginForAllExceptLastDecoration(itemsMargin));
        }

        private void attachTouchHelpers()
        {
            attachDeleteOnSwipeTouchHelper();
            attachChangeColorPositionOnDragTouchHelper();
        }

        private void attachChangeColorPositionOnDragTouchHelper()
        {
            ChangeItemPositionOnDrag
                    .create(DRAG_CHANGE_POSITION_DIRECTIONS)
                    .executer(colorsList::move)
                    .keepItemsInBounds()
                    .attach(colorsListView);
        }

        private void attachDeleteOnSwipeTouchHelper()
        {
            DeleteOnSwipe
                    .create(DELETE_ITEM_ON_SWIPE_DIRECTIONS)
                    .executer(this::tryToDeleteColor)
                    .attach(colorsListView);
        }

        private void makeAdapterListenToColorsListChanges()
        {
            colorsList.addListener(colorsListViewAdapter);
        }
        //endregion

        private void onColorClick(int position, int color)
        {
            lastColorClickedPosition = position;

            colorPickerDialog.show(color, PickerTag.CHANGE_COLOR);
        }

        private void onColorDeletionFailed()
        {
            Toast.makeText(getContext(), minItemDeletionError, Toast.LENGTH_SHORT).show();
        }

        private boolean tryToDeleteColor(int position)
        {
            if (colorsList.remove(position))
            {
                return true;
            }

            onColorDeletionFailed();
            return false;
        }
    }

    private class AddColorButtonViewHandler
    {
        private final View addColorButton;

        private AddColorButtonViewHandler()
        {
            addColorButton = inflatedView.findViewById(R.id.add_color_button);

            setOnAddColorButtonClickListener();
        }

        private void setOnAddColorButtonClickListener()
        {
            addColorButton.setOnClickListener(this::onAddColorButtonClick);
        }

        private void onAddColorButtonClick(View view)
        {
            tryToOpenColorPickerDialog();
        }

        private void tryToOpenColorPickerDialog()
        {
            if (colorsList.canAdd())
            {
                colorPickerDialog.show(PickerTag.ADD_COLOR);
            }
            else
            {
                onColorAdditionFailed();
            }
        }

        private void onColorAdditionFailed()
        {
            Toast.makeText(getContext(), maxItemAdditionError, Toast.LENGTH_SHORT).show();
        }
    }

    private class GradientTypesSpinnerViewHandler
    {
        private final SbmSpinner gradientTypesSpinner;

        private GradientTypesSpinnerViewHandler()
        {
            this.gradientTypesSpinner = inflatedView.findViewById(R.id.gradient_types_spinner);

            init();
        }

        private void init()
        {
            setOnTypeSelectedListener();

            //needed in order to make directionsSpinner's center enabled/disabled.
            //because we added some functionality to the selection process.
            //check onTypeSelected(int) to see the added functionality.
            setSelectedPosition(getSelectedPosition());
        }

        private void setOnTypeSelectedListener()
        {
            gradientTypesSpinner.addOnItemSelectedListener(this::onTypeSelected);
        }

        private void onTypeSelected(int position)
        {
            var type = GRADIENT_TYPES.get(position);

            setGradientType(type);
            enableDisableCenterDirection(type);
            enableDisableGradientRadius(type);
        }

        private void enableDisableCenterDirection(int gradientType)
        {
            if (gradientDirectionsSpinnerViewHandler != null)
            {
                boolean enableFromCenterDirection = gradientType != GradientDrawable.LINEAR_GRADIENT;

                gradientDirectionsSpinnerViewHandler.setCenterEnabled(enableFromCenterDirection);
            }
        }

        private void enableDisableGradientRadius(int gradientType)
        {
            if (gradientRadiusPopupViewHandler != null)
            {
                boolean enableGradientRadius = gradientType == GradientDrawable.RADIAL_GRADIENT;

                gradientRadiusPopupViewHandler.setEnabled(enableGradientRadius);
            }
        }

        private void setGradientType(int gradientType)
        {
            gradientDrawable.setGradientType(gradientType);
        }

        public void setSelectedPosition(Integer position)
        {
            gradientTypesSpinner.setSelectedPosition(position);
        }

        public Integer getSelectedPosition()
        {
            return gradientTypesSpinner.getSelectedPosition();
        }
    }

    private class GradientRadiusPopupViewHandler
    {
        private static final float BY_VIEW_SIZE = -1;

        private float maxRadius = BY_VIEW_SIZE;

        private final SeekBarPopup gradientRadiusPopup;

        private GradientRadiusPopupViewHandler()
        {
            this.gradientRadiusPopup = inflatedView.findViewById(R.id.gradientRadiusPopup);

            init();
        }

        private void init()
        {
            setOnRadiusChangedListener();
        }

        private void setOnRadiusChangedListener()
        {
            gradientRadiusPopup.addOnProgressChangedListener(this::onRadiusChanged);
        }

        private void onRadiusChanged(int progress)
        {
            var radius = getGradientMaxRadius() * progress;

            setGradientRadius(radius);
        }

        private float getGradientMaxRadius()
        {
            var maxRadiusSize = maxRadius;

            if (maxRadius == BY_VIEW_SIZE)
            {
                maxRadiusSize = Math.max(getMeasuredWidth(), getMeasuredHeight());
            }

            return maxRadiusSize * 0.01f;
        }

        private void setGradientRadius(float radius)
        {
            gradientDrawable.setGradientRadius(radius);
        }

        public void setEnabled(boolean enabled)
        {
            gradientRadiusPopup.setEnabled(enabled);
        }

        public void setGradientMaxRadius(float maxRadius)
        {
            Validators.illegalBelow(maxRadius, 0, "'maxRadius' can't be < 0");

            this.maxRadius = maxRadius;
        }

        public void setGradientRadiusPercent(int percent)
        {
            Validators.requireInRange(0, 101, percent, "'percent' must be between 0 to 100");

            gradientRadiusPopup.setProgress(percent);

            onRadiusChanged(percent);
        }

        public int getGradientRadiusPercent()
        {
            return gradientRadiusPopup.getProgress();
        }
    }

    private class GradientDirectionsSpinnerViewHandler
    {
        private final Integer defaultPosition;

        private final SbmSpinner gradientDirectionsSpinner;

        private GradientDirectionsSpinnerViewHandler()
        {
            this.gradientDirectionsSpinner = inflatedView.findViewById(R.id.gradient_directions_spinner);

            this.defaultPosition = getSelectedPosition();

            init();
        }

        private void init()
        {
            setOnDirectionSelectedListener();
        }

        private void setOnDirectionSelectedListener()
        {
            gradientDirectionsSpinner.addOnItemSelectedListener(this::onDirectionSelected);
        }

        private void onDirectionSelected(int position)
        {
            var direction = DIRECTIONS.get(position);

            setGradientDirection(direction);
        }

        private void setGradientDirection(GradientDirection direction)
        {
            var gradientOrientation = direction.getGradientOrientation();

            var gradientCenterX = direction.getCenterX();
            var gradientCenterY = direction.getCenterY();

            gradientDrawable.setOrientation(gradientOrientation);
            gradientDrawable.setGradientCenter(gradientCenterX, gradientCenterY);
        }

        public void setSelectedPosition(Integer position)
        {
            gradientDirectionsSpinner.setSelectedPosition(position);
        }

        public Integer getSelectedPosition()
        {
            return gradientDirectionsSpinner.getSelectedPosition();
        }

        public void setCenterEnabled(boolean enabled)
        {
            var centerPosition = DIRECTIONS.indexOf(GradientDirection.CENTER);

            gradientDirectionsSpinner.setItemsEnabled(enabled, centerPosition);

            if (!enabled && getSelectedPosition() == centerPosition)
            {
                setSelectedPosition(defaultPosition);
            }
        }
    }
    //endregion

    //region CONSTANTS
    private static class BundleKeys
    {
        public static final String PARENT_STATE = "parentState";

        public static final String GRADIENT_COLORS = "gradientColors";
        public static final String GRADIENT_TYPE = "gradientType";
        public static final String GRADIENT_DIRECTION = "gradientDirection";
        public static final String GRADIENT_RADIUS_PERCENT = "gradientRadiusPercent";
        public static final String COLOR_PICKER_SELECTED_COLOR = "colorPickerSelectedColor";

        public static final String COLOR_PICKER_IS_OPEN = "colorPickerIsOpen";
        public static final String COLOR_PICKER_PICKER_TAG = "colorPickerPickerTag";
    }

    private static class PickerTag
    {
        public static final String ADD_COLOR = "addColor";
        public static final String CHANGE_COLOR = "changeColor";
    }

    //DO NOT CHANGE THE ORDER OF THE ITEMS (unless you change the spinner order) - it will assign incorrect direction to the spinner items.
    private static final List<Integer> GRADIENT_TYPES = List.of(
            GradientDrawable.LINEAR_GRADIENT,
            GradientDrawable.RADIAL_GRADIENT,
            GradientDrawable.SWEEP_GRADIENT
    );

    //DO NOT CHANGE THE ORDER OF THE ITEMS (unless you change the spinner order) - it will assign incorrect direction to the spinner items.
    private static final List<GradientDirection> DIRECTIONS = List.of
            (
                    GradientDirection.BR_TL,
                    GradientDirection.BOTTOM_TOP,
                    GradientDirection.BL_TR,
                    GradientDirection.RIGHT_LEFT,
                    GradientDirection.CENTER,
                    GradientDirection.LEFT_RIGHT,
                    GradientDirection.TR_BL,
                    GradientDirection.TOP_BOTTOM,
                    GradientDirection.TL_BR
            );

    private static final int DRAG_CHANGE_POSITION_DIRECTIONS = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    private static final int DELETE_ITEM_ON_SWIPE_DIRECTIONS = ItemTouchHelper.RIGHT;
    //endregion
}