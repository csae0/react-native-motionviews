package com.sketchView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.almeros.android.multitouch.MoveGestureDetector;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.sketchView.tools.Blueprints.SketchTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import at.csae0.reactnative.R;
import team.uptech.motionviews.utils.ConversionUtils;
import team.uptech.motionviews.widget.Interfaces.Limits;
import team.uptech.motionviews.widget.Interfaces.OnMoveGestureListener;
import team.uptech.motionviews.widget.Interfaces.SketchViewCallback;
import team.uptech.motionviews.widget.entity.MotionEntity;

/**
 * Created by keshav on 06/04/17.
 */

public class SketchViewContainer extends RelativeLayout {

    private static SketchViewContainer instance = null;
    public SketchView sketchView;
    private SketchViewCallback sketchViewCallback;

    private LinearLayout colorPickerContainer;
    private BoxedVertical boxedVertical;
    private LinearLayout buttons;

    private boolean colorPickerContainerEnabled;
    /**
     * Constructor (allowing only one instance)
     * @param context
     */
    private SketchViewContainer(Context context) {
        super(context);

        colorPickerContainer = null;
        boxedVertical = null;
        buttons = null;

        colorPickerContainerEnabled = true;
        createLayout(context);
    }
    public static SketchViewContainer getInstance (Context context) {
        if (instance == null) {
            instance = new SketchViewContainer(context);
        }
        return instance;
    }
    /**
     * Create Views
     * @return
     */

    // TODO: Merge redundant logic for edit settings (TextEditorDialogFragment, SketchViewContainer, (ImageEntity edit screen))
    private void createLayout(Context context) {
        // Container layout
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
        setBackgroundColor(Color.TRANSPARENT);
        setClickable(true);
        setFitsSystemWindows(true);
//        ((Activity)vg.getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //TODO: needed ???

        // buttons
        addButtons(context);

        // Tool thickness slider
        addToolThicknessSlider(context);

        // Color selections
        addColorSelections(context);

        // SketchView (needs to be first because of values other views get from SketchView)
        addSketchView(context);

        // Update UI (to be on the safe side)
        invalidate();
    }

    // SketchView
    private void addSketchView (final Context context) {
        sketchView = SketchView.getInstance(context);
        sketchView.setOnTouchListener(new OnTouchListener() {
            MoveGestureDetector moveDetector = new MoveGestureDetector(context, new OnMoveGestureListener() {
                boolean visible = true;
                View[] views = new View[]{ colorPickerContainer, boxedVertical, buttons };
                @Override
                public boolean onMove(MoveGestureDetector detector) {
                    return false;
                }

                @Override
                public boolean onMoveBegin(MoveGestureDetector detector) {
                    if (visible) {
                        visible = false;
                        fadeOutViews(views, null);
                    }
                    return true;
                }

                @Override
                public void onMoveEnd(MoveGestureDetector detector) {
                    if (!visible) {
                        visible = true;
                        fadeInViews(views, null);
                    }
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                moveDetector.onTouchEvent(event);
                return false;
            }
        });
        addView(sketchView, 0);
    }

    // buttons
    private void addButtons (Context context) {
        int padding = getResources().getDimensionPixelOffset(R.dimen.padding);
        int height = getResources().getDimensionPixelOffset(R.dimen.color_picker_height);

        Button cancel = new Button(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, height);
        layoutParams.topMargin = padding;
        layoutParams.bottomMargin = padding;
        layoutParams.leftMargin = padding;
        layoutParams.rightMargin = padding;
        cancel.setLayoutParams(layoutParams);
        cancel.setText("CANCEL");
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sketchView != null) {
                    sketchView.clear(); // remove image so null is passed
                    sketchViewCallback.closeAndCreateEntity(null, sketchView.getCroppedImageBounds(), sketchView.getToolColor(), (int) sketchView.getToolThickness());
                }
            }
        });

        Button clear = new Button(context);
        clear.setLayoutParams(layoutParams);
        clear.setText("CLEAR");
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sketchView != null) {
                    sketchView.clear();
                }
            }
        });

        Button save = new Button(context);
        save.setLayoutParams(layoutParams);
        save.setText("SAVE");
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sketchView != null) {
                    sketchViewCallback.closeAndCreateEntity(sketchView.getImage(), sketchView.getCroppedImageBounds(), sketchView.getToolColor(), (int) sketchView.getToolThickness());
                    sketchView.clear();
                }
            }
        });

//        Button box = new Button(context);
//        box.setLayoutParams(layoutParams);
//        box.setText("BOX");
//        box.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sketchView != null) {
//                    sketchView.showBounds = !sketchView.showBounds;
//                }
//            }
//        });

        buttons = new LinearLayout(context);
        buttons.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        buttons.setOrientation(LinearLayout.VERTICAL);
        
        LinearLayout editButtons = new LinearLayout(context);
        editButtons.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        editButtons.setGravity(Gravity.RIGHT);
        editButtons.addView(cancel);
        editButtons.addView(clear);
        editButtons.addView(save);
//        buttons.addView(box);


        Button pen = new Button(context);
        pen.setLayoutParams(layoutParams);
        pen.setText("PEN");
        pen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolType(SketchTool.TYPE_PEN);
            }
        });

        Button eraser = new Button(context);
        eraser.setLayoutParams(layoutParams);
        eraser.setText("ERASE");
        eraser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolType(SketchTool.TYPE_ERASE);
            }
        });

        Button circle = new Button(context);
        circle.setLayoutParams(layoutParams);
        circle.setText("CIRCLE");
        circle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolType(SketchTool.TYPE_CIRCLE);
            }
        });

        Button arrow = new Button(context);
        arrow.setLayoutParams(layoutParams);
        arrow.setText("ARROW");
        arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolType(SketchTool.TYPE_ARROW);
            }
        });

        LinearLayout toolButtons = new LinearLayout(context);
        toolButtons.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        toolButtons.setGravity(Gravity.RIGHT);
        toolButtons.addView(pen);
        toolButtons.addView(eraser);
        toolButtons.addView(circle);
        toolButtons.addView(arrow);

        buttons.addView(editButtons);
        buttons.addView(toolButtons);
        addView(buttons);
    }
    private void addToolThicknessSlider (Context context) {
        // boxed vertical seekbar
        boxedVertical = new BoxedVertical(context);

        int width = getResources().getDimensionPixelOffset(R.dimen.slider_width);
        int paddingHorizontal = getResources().getDimensionPixelOffset(R.dimen.padding);
        int paddingVertical = getResources().getDimensionPixelOffset(R.dimen.color_picker_height_and_padding);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.leftMargin = paddingHorizontal;
        layoutParams.rightMargin = paddingHorizontal;
        layoutParams.topMargin = paddingVertical * 2;
        layoutParams.bottomMargin = paddingVertical;
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        boxedVertical.setLayoutParams(layoutParams);

        int seekbarBackgroundColor = getResources().getColor(R.color.color_background);
        int seekbarTextColor = getResources().getColor(R.color.color_text);
        int seekbarProgressColor = getResources().getColor(R.color.color_progress_dark);
        int maxThickness = getResources().getInteger(R.integer.max_thickness);
        int minThickness = getResources().getInteger(R.integer.min_thickness);
        int stepThickness = getResources().getInteger(R.integer.thickness_steps);
        int defaultThickness = getResources().getInteger(R.integer.initial_thickness);
        boxedVertical.setBackgroundColor(seekbarBackgroundColor);
        boxedVertical.setProgressPaint(seekbarProgressColor);
        boxedVertical.setTextPaint(seekbarTextColor);
        boxedVertical.setCornerRadius(20);
        boxedVertical.setImageEnabled(false);
        boxedVertical.setTextEnabled(false);
        boxedVertical.setMax(maxThickness);
        boxedVertical.setMin(minThickness);
        boxedVertical.setStep(stepThickness);
        boxedVertical.setTextBottomPadding(20);
        boxedVertical.setTextSize(12);
        boxedVertical.setTouchDisabled(true);
        boxedVertical.setValue((int)(sketchView != null ? sketchView.getToolThickness() : defaultThickness));
        boxedVertical.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, final int value) {
                if (sketchView != null) {
                    int textSizeDP = (int) sketchView.getToolThickness();
                    if (textSizeDP != value) {
                        sketchView.setToolThickness(value);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {
            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
            }
        });

        addView(boxedVertical);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addColorSelections (Context context) {
        // LinearLayout
        colorPickerContainer = new LinearLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        colorPickerContainer.setLayoutParams(layoutParams);
        colorPickerContainer.setOrientation(LinearLayout.HORIZONTAL);
        int padding = getResources().getDimensionPixelSize(R.dimen.padding);
        colorPickerContainer.setPadding(padding, padding, padding, padding); // TODO: find solution that works with react native

        // LinearLayout
        LinearLayout colorPickerSubContainer = new LinearLayout(context);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        colorPickerSubContainer.setLayoutParams(linearLayoutParams);
        colorPickerSubContainer.setGravity(Gravity.CENTER);

        // Button
        Button colorPalette = new Button(context);
        int colorCircleDiameter = getResources().getDimensionPixelSize(R.dimen.color_circle_diameter);
        layoutParams = new RelativeLayout.LayoutParams(colorCircleDiameter, colorCircleDiameter); // TODO: find solution that works with react native
        colorPalette.setLayoutParams(layoutParams);
        // colorPalette.setBackground(); // DRAWABLE ic_format_color_text // TODO: find solution that works with react native
        if (sketchView != null) {
            colorPalette.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(sketchView.getToolColor()))); // TODO: solution for min API 21 needed
        }
        colorPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPalette(v);
            }
        });
        colorPickerSubContainer.addView(colorPalette);
        colorPickerContainer.addView(colorPickerSubContainer);
        addDynamicColorSelections(colorPickerContainer, colorPalette, colorCircleDiameter);

        addView(colorPickerContainer);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addDynamicColorSelections (LinearLayout colorPickerContainer, final Button colorPalette, int colorCircleDiameter) {
        String[] colors = {"#000000", "#20BBFC", "#2DFD2F", "#FD28F9", "#EA212E", "#FD7E24", "#FFFA38", "#FFFFFF"};
        Context context = colorPickerContainer.getContext();
        for (String color: colors) {
            final int parsedColor = Color.parseColor(color);

            LinearLayout colorButtonContainer = new LinearLayout(context);
            colorButtonContainer.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            colorButtonContainer.setGravity(Gravity.CENTER);

            Button colorButton = new Button(context);
            colorButton.setLayoutParams(new LinearLayout.LayoutParams(colorCircleDiameter, colorCircleDiameter));
            colorButton.setBackgroundResource(R.drawable.circle);
            colorButton.getBackground().mutate().setTint(parsedColor); // min API 21 needed TODO: FIND SOLUTION
            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sketchView != null) {
                        sketchView.setToolColor(parsedColor);
                        colorPalette.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(parsedColor))); // min API 21 needed TODO: find solution
                    }
                }
            });
            colorButtonContainer.addView(colorButton);
            colorPickerContainer.addView(colorButtonContainer);
        }
    }
    private void openColorPalette(final View v) {
        ColorPickerDialogBuilder
                .with(v.getContext())
                .setTitle(R.string.select_color)
                .initialColor(sketchView != null ? sketchView.getToolColor() : Color.WHITE)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(15) // magic number
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if (sketchView != null) {
                            sketchView.setToolColor(selectedColor);
                            v.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(selectedColor))); // min API 21 needed TODO: find solution
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }
    /**
     * Setter and getter
     */
    public void setCallback(SketchViewCallback sketchViewCallback) {
        this.sketchViewCallback = sketchViewCallback;
    }

    public void setToolThickness (float thickness) {
        sketchView.setToolThickness(thickness);
        boxedVertical.setValue((int) thickness);
    }

    public void setToolType(int type) {
        if (sketchView != null) {
            sketchView.setToolType(type);
            setColorPickerContainerEnabled(type != SketchTool.TYPE_ERASE);
        }
    }

    public void setColorPickerContainerEnabled (boolean enabled) {
        View[] views = new View[]{ colorPickerContainer };
        if (enabled != colorPickerContainerEnabled) {
            colorPickerContainerEnabled = enabled;
            if (enabled) {
                fadeInViews(null, views);
            } else {
                fadeOutViews(null, views);
            }
        }
    }

    private void startMultipleAnimations (ArrayList<View> views, Animation animation) {
        for (View v: views) {
            if (v != null) {
                v.startAnimation(animation);
            }
        }
    }

    private void setMultipleAlpha (ArrayList<View> views, float alpha) {
        for (View v: views) {
            if (v != null) {
                v.setAlpha(alpha);
            }
        }
    }
    private void setMultipleEnabled (ArrayList<View> views, boolean enabled) {
        for (View v: views) {
            if (v != null) {
                if (v instanceof ViewGroup) {
                    ArrayList<View> children = new ArrayList<>();
                    ViewGroup vg = (ViewGroup)v;
                    int childCount = vg.getChildCount();
                    for (int index = 0; index < childCount; ++index) {
                        children.add(vg.getChildAt(index));
                    }
                    setMultipleEnabled(children, enabled);
                }
                v.setEnabled(enabled);
            }
        }
    }
    /**
     * Save image on device
     *
     * @return
     * @throws IOException
     */
    public SketchFile saveToLocalCache() throws IOException {
        if (sketchView != null) {
            Bitmap viewBitmap = Bitmap.createBitmap(sketchView.getWidth(), sketchView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(viewBitmap);
            draw(canvas);

            File cacheFile = File.createTempFile("sketch_", UUID.randomUUID().toString() + ".png");
            FileOutputStream imageOutput = new FileOutputStream(cacheFile);
            viewBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutput);

            SketchFile sketchFile = new SketchFile();
            sketchFile.localFilePath = cacheFile.getAbsolutePath();
            ;
            sketchFile.width = viewBitmap.getWidth();
            sketchFile.height = viewBitmap.getHeight();
            return sketchFile;
        }
        return null;
    }
    @Nullable
    public String getBase64() {
        if (sketchView != null) {
            Bitmap viewBitmap = Bitmap.createBitmap(sketchView.getWidth(), sketchView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(viewBitmap);
            draw(canvas);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            viewBitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream);
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        }
        return null;
    }
    public boolean openSketchFile(String localFilePath) {
        if (sketchView != null) {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.outWidth = sketchView.getWidth();
            Bitmap bitmap = BitmapFactory.decodeFile(localFilePath, bitmapOptions);
            if (bitmap != null) {
                sketchView.setViewImage(bitmap);
                return true;
            }
        }
        return false;
    }

    /**
     * cleanup
     */
    public void release () {
        if (sketchView != null) {
            sketchView.clear();
            sketchView = null;
        }
        sketchViewCallback = null;
        colorPickerContainer = null;
        boxedVertical = null;
        instance = null;
    }

    /**
     * Animation
     */
    public void fadeOutViews (@Nullable final View[] views, @Nullable final View[] removeViews) {
        final ArrayList<View> allViews = new ArrayList<>();
        if (views != null) {
            allViews.addAll(Arrays.asList(views));
        }
        if (removeViews != null) {
            allViews.addAll(Arrays.asList(removeViews));
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setMultipleEnabled(allViews, false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setMultipleAlpha(allViews, 0);
                if (removeViews != null) {
                    for (View view : removeViews) {
                        if (view != null && view.getParent() != null) {
                            removeView(view);
                        }
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startMultipleAnimations(allViews, alphaAnimation);
    }

    public void fadeInViews (@Nullable final View[] views, @Nullable final View[] addViews) {
        final ArrayList<View> allViews = new ArrayList<>();
        if (views != null) {
            allViews.addAll(Arrays.asList(views));
        }
        if (addViews != null) {
            allViews.addAll(Arrays.asList(addViews));
            for (View view : addViews) {
                if (view != null && view.getParent() == null) {
                    addView(view);
                }
            }
        }

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setMultipleAlpha(allViews, 1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setMultipleEnabled(allViews, true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startMultipleAnimations(allViews, alphaAnimation);
    }
}
