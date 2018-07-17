package com.sketchView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import at.csae0.reactnative.R;
import at.csae0.reactnative.interfaces.ConfigActions;
import at.csae0.reactnative.interfaces.ConfigManagerActions;
import at.csae0.reactnative.interfaces.SetSizeAction;
import at.csae0.reactnative.model.ButtonConfig;
import at.csae0.reactnative.model.ButtonConfigs;
import at.csae0.reactnative.model.ColorConfig;
import at.csae0.reactnative.model.GeneralConfig;
import at.csae0.reactnative.model.PickerConfig;
import at.csae0.reactnative.model.SizeConfig;
import at.csae0.reactnative.utils.CONFIG_TYPE;
import at.csae0.reactnative.utils.ConfigManager;
import team.uptech.motionviews.utils.ConversionUtils;
import team.uptech.motionviews.utils.FontProvider;
import team.uptech.motionviews.utils.RessourceUtils;
import team.uptech.motionviews.utils.UIUtils;
import team.uptech.motionviews.widget.Interfaces.OnMoveGestureListener;
import team.uptech.motionviews.widget.Interfaces.SketchViewCallback;

/**
 * Created by keshav on 06/04/17.
 */

public class SketchViewContainer extends RelativeLayout {

    private static final int COLOR_PICKER_CONTAINER_ID = 11111;
    private static final CONFIG_TYPE SCREEN_TYPE = CONFIG_TYPE.SKETCH_ENTITY_SCREEN;

    private static SketchViewContainer instance = null;
    public SketchView sketchView;
    private SketchViewCallback sketchViewCallback;

    private LinearLayout colorPickerContainer;
    private BoxedVertical boxedVertical;
    private RelativeLayout buttons;
    private LinearLayout toolButtons;
    private Button cancel, clear, save, pen, eraser, circle, arrow, colorPalette;
    private Integer penTint, eraserTint, circleTint, arrowTint;
    private PickerConfig pickerConfig;

    private boolean colorPickerContainerEnabled;

    private static FontProvider fontProvider;
    /**
     * Constructor (allowing only one instance)
     * @param context
     */
    private SketchViewContainer(Context context) {
        super(context);

        colorPickerContainer = null;
        boxedVertical = null;
        buttons = null;
        cancel = clear = save = pen = eraser = circle = arrow = colorPalette = null;

        penTint = eraserTint = circleTint = arrowTint = null;

        pickerConfig = null;

        colorPickerContainerEnabled = true;
        createLayout(context);
        applyConfig();
    }

    public static SketchViewContainer getInstance (Context context) {
        if (instance == null) {
            instance = new SketchViewContainer(context);
        }
        return instance;
    }

    public static SketchViewContainer getInstance () {
        return instance;
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    /**
     * set config paramaters
     */
    private void applyConfig() {
        if (ConfigManager.hasInstance()) {
           ConfigManager.getInstance().apply(new ConfigActions() {
               @Override
               public void applyGeneralConfig(GeneralConfig config) {
                   if (config.hasFontFamily() && fontProvider != null) {
                       Typeface typeface = fontProvider.getTypeface(config.getFontFamily());

                       if (cancel != null) {
                           cancel.setTypeface(typeface);
                       }
                       if (clear != null) {
                           clear.setTypeface(typeface);
                       }
                       if (save != null) {
                           save.setTypeface(typeface);
                       }
                       if (pen != null) {
                           pen.setTypeface(typeface);
                       }
                       if (eraser != null) {
                           eraser.setTypeface(typeface);
                       }
                       if (circle != null) {
                           circle.setTypeface(typeface);
                       }
                       if (arrow != null) {
                           arrow.setTypeface(typeface);
                       }
                   }

                   if (config.hasInitialToolSelection()) {
                       setToolType(config.getInitialToolSelection().getInt());
                   }

                   if (config.hasImageBounds()) {
                       int[] bounds = config.getImageBounds();

                       RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) sketchView.getLayoutParams();
                       layoutParams.width = bounds[0];
                       layoutParams.height = bounds[1];
                       sketchView.setLayoutParams(layoutParams);
                   }
               }

               @Override
               public void applyColorConfig(ConfigManagerActions manager) {
                   ColorConfig config = (ColorConfig) manager.getScreenConfig(SCREEN_TYPE, COLOR);
                   int colorCircleDiameter = getResources().getDimensionPixelSize(R.dimen.color_circle_diameter);

                   if (config != null) {
                       if (config.hasInitialColor()) {
                           setToolColor(config.getInitialColor());
                       }
                       if (config.hasPickerConfig()) {
                           pickerConfig = config.getPickerconfig();
                       }
                       addDynamicColorSelections(colorPickerContainer, colorCircleDiameter, config.getColors());
                       return;
                   }
                   addDynamicColorSelections(colorPickerContainer, colorCircleDiameter, null);
               }

               @Override
               public void applySizeConfig(ConfigManagerActions manager) {
                   SizeConfig config = (SizeConfig) manager.getScreenConfig(SCREEN_TYPE, SIZE);
                   manager.configureSize(boxedVertical, config, new SetSizeAction() {
                       @Override
                       public void setSize(@Nullable Integer size) {
                           if (size != null) {
                               setToolThickness(size);
                           }
                       }
                   });
               }

               @Override
               public void applyButtonConfigs(ConfigManagerActions manager) {
                   ButtonConfigs configs = (ButtonConfigs) manager.getScreenConfig(SCREEN_TYPE, BUTTON);

                   if (configs != null && configs.hasButtonsConfig()) {
                       for (ButtonConfig config : configs.getButtonsConfig()) {
                           if (config != null) {
                               Button tempButton = null;
                               Drawable defaultDrawable = null;
                               if (config.getId() != null) {
                                   switch (config.getId()) {
                                       case CANCEL_BUTTON_CONFIG:
                                           tempButton = cancel;
                                           defaultDrawable = RessourceUtils.getImageRessource("ic_close");
                                           break;
                                       case CLEAR_BUTTON_CONFIG:
                                           tempButton = clear;
                                           defaultDrawable = RessourceUtils.getImageRessource("ic_trash");
                                           break;
                                       case SAVE_BUTTON_CONFIG:
                                           tempButton = save;
                                           defaultDrawable = RessourceUtils.getImageRessource("ic_check");
                                           break;
                                       case PEN_TOOL_CONFIG:
                                           tempButton = pen;
                                           defaultDrawable = RessourceUtils.getImageRessource("ic_pen_tool");
                                           penTint = config.getTintColor();
                                           break;
                                       case ERASE_TOOL_CONFIG:
                                           tempButton = eraser;
                                           defaultDrawable = RessourceUtils.getImageRessource("ic_erase_tool");
                                           eraserTint = config.getTintColor();
                                           break;
                                       case CIRCLE_TOOL_CONFIG:
                                           tempButton = circle;
                                           defaultDrawable = RessourceUtils.getImageRessource("ic_circle_tool");
                                           circleTint = config.getTintColor();
                                           break;
                                       case ARROW_TOOL_CONFIG:
                                           tempButton = arrow;
                                           defaultDrawable = RessourceUtils.getImageRessource("ic_arrow_tool");
                                           arrowTint = config.getTintColor();
                                           break;
                                   }
                                   manager.configureButton(tempButton, config, defaultDrawable);
                               }
                           }
                       }
                   }
               }
           });
        }
    }


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
                View[] views = new View[]{ colorPickerContainer, boxedVertical, buttons, toolButtons };
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


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        sketchView.setLayoutParams(layoutParams);
        addView(sketchView, 0);
    }

    // buttons
    private void addButtons (Context context) {
        int padding = getResources().getDimensionPixelOffset(R.dimen.padding);
        int height = getResources().getDimensionPixelOffset(R.dimen.color_picker_height);
        int marginVertical = getResources().getDimensionPixelOffset(R.dimen.slider_margin_vertical);
        cancel = new AppCompatButton(context);
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

        clear = new AppCompatButton(context);
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

        save = new AppCompatButton(context);
        save.setLayoutParams(layoutParams);
        save.setText("SAVE");
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sketchView != null) {
                    sketchViewCallback.closeAndCreateEntity(sketchView.getImage(), sketchView.getCroppedImageBounds(), sketchView.getToolColor(), (int) sketchView.getToolThickness());
                    if (sketchView != null) {
                        sketchView.clear();
                    }
                }
            }
        });

//        Button box = new AppCompatButton(context);
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

        buttons = new RelativeLayout(context);
        buttons.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        buttons.setPadding(padding, padding, padding, padding);

        LinearLayout editButtonsRight = new LinearLayout(context);
        RelativeLayout.LayoutParams containerLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        containerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        editButtonsRight.setLayoutParams(containerLayoutParams);
        editButtonsRight.addView(clear);
        editButtonsRight.addView(save);

        LinearLayout editButtonsLeft = new LinearLayout(context);
        containerLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        containerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        editButtonsLeft.setLayoutParams(containerLayoutParams);
        editButtonsLeft.addView(cancel);

        pen = new AppCompatButton(context);
        pen.setLayoutParams(layoutParams);
        pen.setText("PEN");
        pen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolType(SketchTool.TYPE_PEN);
            }
        });

        eraser = new AppCompatButton(context);
        eraser.setLayoutParams(layoutParams);
        eraser.setText("ERASE");
        eraser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolType(SketchTool.TYPE_ERASE);
            }
        });

        circle = new AppCompatButton(context);
        circle.setLayoutParams(layoutParams);
        circle.setText("CIRCLE");
        circle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolType(SketchTool.TYPE_CIRCLE);
            }
        });

        arrow = new AppCompatButton(context);
        arrow.setLayoutParams(layoutParams);
        arrow.setText("ARROW");
        arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolType(SketchTool.TYPE_ARROW);
            }
        });

        toolButtons = new LinearLayout(context);
        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.topMargin = marginVertical;
        toolButtons.setLayoutParams(layoutParams);
        toolButtons.setGravity(Gravity.RIGHT);
        toolButtons.setPadding(padding, padding, padding, padding);
        toolButtons.setOrientation(LinearLayout.VERTICAL);
        toolButtons.addView(pen);
        toolButtons.addView(eraser);
        toolButtons.addView(circle);
        toolButtons.addView(arrow);

        buttons.addView(editButtonsRight);
        buttons.addView(editButtonsLeft);

        addView(buttons);
        addView(toolButtons);
    }

    private void addToolThicknessSlider (Context context) {
        // boxed vertical seekbar
        boxedVertical = new BoxedVertical(context);

        int width = getResources().getDimensionPixelOffset(R.dimen.slider_width);
        int marginHorizontal = getResources().getDimensionPixelOffset(R.dimen.padding);
        int marginVertical = getResources().getDimensionPixelOffset(R.dimen.slider_margin_vertical);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.leftMargin = marginHorizontal;
        layoutParams.rightMargin = marginHorizontal;
        layoutParams.topMargin = marginVertical;
        layoutParams.bottomMargin = marginVertical;
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

    private void addColorSelections (Context context) {
        // LinearLayout
        colorPickerContainer = new LinearLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        colorPickerContainer.setId(COLOR_PICKER_CONTAINER_ID);
        colorPickerContainer.setLayoutParams(layoutParams);
        colorPickerContainer.setOrientation(LinearLayout.HORIZONTAL);
        int padding = getResources().getDimensionPixelSize(R.dimen.padding);
        colorPickerContainer.setPadding(padding, padding, padding, padding);

        // LinearLayout
        LinearLayout colorPickerSubContainer = new LinearLayout(context);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        colorPickerSubContainer.setLayoutParams(linearLayoutParams);
        colorPickerSubContainer.setGravity(Gravity.CENTER);

        // Button
        colorPalette = new AppCompatButton(context);
        int colorCircleDiameter = getResources().getDimensionPixelSize(R.dimen.color_circle_diameter);
        layoutParams = new RelativeLayout.LayoutParams(colorCircleDiameter, colorCircleDiameter);
        colorPalette.setLayoutParams(layoutParams);
         colorPalette.setBackground(getResources().getDrawable(R.drawable.ic_border_color));
        if (sketchView != null) {
            UIUtils.setButtonTint(colorPalette, ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(sketchView.getToolColor())));
            // colorPalette.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(sketchView.getToolColor()))); // TODO: solution for min API 21 needed
        }
        colorPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPalette(v);
            }
        });
        colorPickerSubContainer.addView(colorPalette);
        colorPickerContainer.addView(colorPickerSubContainer);

//        addDynamicColorSelections(colorPickerContainer, colorPalette, colorCircleDiameter, null);

        addView(colorPickerContainer);
    }

    private void addDynamicColorSelections (LinearLayout colorPickerContainer, int colorCircleDiameter, @Nullable ArrayList<String> colors) {
        String[] defaultColors = {"#000000", "#20BBFC", "#2DFD2F", "#FD28F9", "#EA212E", "#FD7E24", "#FFFA38", "#FFFFFF"};
        Context context = colorPickerContainer.getContext();
        for (String color: (colors != null ? colors.toArray(new String[colors.size()]) : defaultColors)) {
            final int parsedColor = Color.parseColor(color);

            LinearLayout colorButtonContainer = new LinearLayout(context);
            colorButtonContainer.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            colorButtonContainer.setGravity(Gravity.CENTER);

            Button colorButton = new AppCompatButton(context);
            colorButton.setLayoutParams(new LinearLayout.LayoutParams(colorCircleDiameter, colorCircleDiameter));
            colorButton.setBackgroundResource(R.drawable.circle);
            UIUtils.setButtonTint(colorButton, ColorStateList.valueOf(parsedColor));
//            colorButton.getBackground().mutate().setTint(parsedColor); // min API 21 needed TODO: FIND SOLUTION
            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setToolColor(parsedColor);
                }
            });
            colorButtonContainer.addView(colorButton);
            colorPickerContainer.addView(colorButtonContainer);
        }
    }
    private void openColorPalette(final View v) {
        boolean hasPickerConfig = pickerConfig != null;
        ColorPickerDialogBuilder
                .with(v.getContext())
                .setTitle(hasPickerConfig && pickerConfig.hasPickerLabel() ? pickerConfig.getPickerLabel() : getResources().getString(R.string.select_color))
                .initialColor(sketchView != null ? sketchView.getToolColor() : hasPickerConfig && pickerConfig.hasInitialColor() ? pickerConfig.getInitialColor() : Color.WHITE)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(15) // magic number
                .setPositiveButton(hasPickerConfig && pickerConfig.hasSubmitText() ? pickerConfig.getSubmitText() : getResources().getString(R.string.ok), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        setToolColor(selectedColor);
                    }
                })
                .setNegativeButton(hasPickerConfig && pickerConfig.hasCancelText() ? pickerConfig.getCancelText() : getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
        if (sketchView != null) {
            sketchView.setToolThickness(thickness);
        }
        if (boxedVertical != null) {
            boxedVertical.setValue((int) thickness);
        }
    }

    public void setToolColor (int color) {
        if (sketchView != null) {
            sketchView.setToolColor(color);
            setToolSelectionIndicator(sketchView.getSelectedTool());
        }
        if (colorPalette != null) {
            UIUtils.setButtonTint(colorPalette, ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(color)));
//            colorPalette.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(color))); // min API 21 needed TODO: find solution
        }
    }

    public void setToolType(int type) {
        if (sketchView != null) {
            sketchView.setToolType(type);
            setToolSelectionIndicator(type);
            setColorPickerContainerEnabled(type != SketchTool.TYPE_ERASE);
        }
    }

    private void setToolSelectionIndicator (int type) {
        Button[] toolButtons = new Button[]{pen, eraser, circle, arrow};
        Integer[] toolButtonTints = new Integer[]{penTint, eraserTint, circleTint, arrowTint};
        for (int i = 0; i < toolButtons.length; i++) {
            if (toolButtons[i] != null) {
                if (toolButtonTints[i] != null) {
                    UIUtils.setButtonTint(toolButtons[i], ColorStateList.valueOf(toolButtonTints[i]));
//                    toolButtons[i].setBackgroundTintList(ColorStateList.valueOf(toolButtonTints[i]));
                    toolButtons[i].setTextColor(toolButtonTints[i]);
                } else {
                    UIUtils.setButtonTint(toolButtons[i], null);
//                    toolButtons[i].setBackgroundTintList(null);
                }
            }
        }

        ColorStateList colorStateList;
        switch (type) {
            case SketchTool.TYPE_PEN:
                colorStateList = createColorStateList(sketchView.getToolColor(), penTint);
                UIUtils.setButtonTint(pen, colorStateList);
//                pen.setBackgroundTintList(colorStateList);
                if (colorStateList != null) {
                    pen.setTextColor(colorStateList);
                }
                break;
            case SketchTool.TYPE_ERASE:
                colorStateList = createColorStateList(sketchView.getToolColor(), eraserTint);
                UIUtils.setButtonTint(eraser, colorStateList);
//                eraser.setBackgroundTintList(colorStateList);
                if (colorStateList != null) {
                    eraser.setTextColor(colorStateList);
                }
                break;
            case SketchTool.TYPE_CIRCLE:
                colorStateList = createColorStateList(sketchView.getToolColor(), circleTint);
                UIUtils.setButtonTint(circle, colorStateList);
//                circle.setBackgroundTintList(colorStateList);
                if (colorStateList != null) {
                    circle.setTextColor(colorStateList);
                }
                break;
            case SketchTool.TYPE_ARROW:
                colorStateList = createColorStateList(sketchView.getToolColor(), arrowTint);
                UIUtils.setButtonTint(arrow, colorStateList);
//                arrow.setBackgroundTintList(colorStateList);
                if (colorStateList != null) {
                    arrow.setTextColor(colorStateList);
                }
                break;
        }
    }

    @Nullable
    private ColorStateList createColorStateList(@Nullable Integer toolColor, @Nullable Integer baseTint) {
        Integer resultColor = null;
        if (toolColor != null && baseTint != null) {
            resultColor = ConversionUtils.getToolSelectionIndicatorColor(ConversionUtils.transformAlphaUpperTwoThirds(toolColor), baseTint);
        }

        if (resultColor != null) {
            return ColorStateList.valueOf(resultColor);
        }
        return null;
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
     * cleanup
     */
    public void release () {
        if (sketchView != null) {
            sketchView.destroy();
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

    public static void setFontProvider(@Nullable FontProvider fontProvider) {
        SketchViewContainer.fontProvider = fontProvider;
    }
}
