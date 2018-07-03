package team.uptech.motionviews.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import at.csae0.reactnative.R;
import at.csae0.reactnative.interfaces.ConfigActions;
import at.csae0.reactnative.interfaces.ConfigManagerActions;
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
import team.uptech.motionviews.widget.Interfaces.EditCallback;
import team.uptech.motionviews.widget.Interfaces.Limits;

/**
 * Transparent Dialog Fragment, with no title and no background
 * <p>
 * The fragment imitates capturing input from keyboard, but does not display anything
 * the result from input from the keyboard is passed through {@link EditCallback}
 * <p>
 * Activity that uses {@link TextEditorDialogFragment} must implement {@link EditCallback}
 * <p>
 * If Activity does not implement {@link EditCallback}, exception will be thrown at Runtime
 */

// TODO: create view instead of fragment (like SketchViewContainer) --> create super class (abstract?) because all subclasses will be kind of the same
public class TextEditorDialogFragment extends DialogFragment {

    private static final CONFIG_TYPE SCREEN_TYPE = CONFIG_TYPE.TEXT_ENTITY_SCREEN;

    public static final String ARG_TEXT = "editor_text_arg";
    public static final String ARG_SIZE = "editor_size_arg";
    public static final String ARG_COLOR = "editor_color_arg";
    public static final String ARG_TYPEFACE = "editor_typeface_arg";

    protected EditText editText;
    protected FontProvider fontProvider;

    private BoxedVertical boxedVertical;
    private Button colorPalette, cancel, clear, save;
    private PickerConfig pickerConfig;

    private EditCallback callback;

    /**
     * deprecated
     * use {@link TextEditorDialogFragment#getInstance(String, int, int, String)}
     */
    @Deprecated
    public TextEditorDialogFragment() {
        // empty, use getInstance
    }

    public static TextEditorDialogFragment getInstance(String text, int size, int color, String typefaceName) {
        @SuppressWarnings("deprecation")
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putString(ARG_SIZE, size+"");
        args.putString(ARG_COLOR, color+"");
        args.putString(ARG_TYPEFACE, typefaceName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof EditCallback) {
            this.callback = (EditCallback) activity;
            this.fontProvider = ((EditCallback) activity).getFontProvider();
        } else {
            throw new IllegalStateException(activity.getClass().getName()
                    + " must implement " + EditCallback.class.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.text_editor_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String text = "";
        int size = ConversionUtils.dpToPx(Limits.FONT_SIZE_INITIAL_DP);
        int color = Limits.INITIAL_FONT_COLOR;
        String typefaceName = "";

        if (args != null) {
            text = args.getString(ARG_TEXT);
            size = Integer.parseInt(args.getString(ARG_SIZE));
            color = Integer.parseInt(args.getString(ARG_COLOR));
            typefaceName = args.getString(ARG_TYPEFACE);
        }
        editText = view.findViewById(R.id.edit_text_view);

        DisplayMetrics displayMetrics = view.getResources().getDisplayMetrics();
        int padding = (int)(getResources().getDimension(R.dimen.padding) + 0.5f);
        int margin = (int)(getResources().getDimension(R.dimen.slider_width_and_padding) + 0.5f);
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        int maxWidth = (isPortrait ? displayMetrics.widthPixels : displayMetrics.heightPixels) - margin - padding;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) editText.getLayoutParams();
        layoutParams.width = maxWidth;
        if (isPortrait ) {
            layoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
        } else {
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }
        editText.setLayoutParams(layoutParams);

        // Slider values
        boxedVertical = view.findViewById(R.id.boxed_vertical);
        boxedVertical.setValue(ConversionUtils.pxToDp(size));

//        createColorSelections(view, color);
        addButtons(view);
        initListeners(view);

        initWithTextEntity(text);

        setTextColor(color);

        if (fontProvider != null) {
            Typeface typeface = fontProvider.getTypeface(!typefaceName.equals("") || typefaceName != null ? typefaceName : fontProvider.getDefaultFontName());
            editText.setTypeface(typeface);
        }

        applyConfig(view);
    }

    private void applyConfig(final View view) {
        if (ConfigManager.hasInstance()) {
            ConfigManager.getInstance().apply(new ConfigActions() {
                @Override
                public void applyGeneralConfig(GeneralConfig config) {
                    if (config.hasFontFamily() && fontProvider != null) {
                        Typeface typeface = fontProvider.getTypeface(config.getFontFamily());
                        if (editText != null && typeface != null) {
                            editText.setTypeface(typeface);
                        }

                        if (cancel != null) {
                            cancel.setTypeface(typeface);
                        }
                        if (clear != null) {
                            clear.setTypeface(typeface);
                        }
                        if (save != null) {
                            save.setTypeface(typeface);
                        }
                    }
                }

                @Override
                public void applyColorConfig(ConfigManagerActions manager) {
                    ColorConfig config = (ColorConfig) manager.getScreenConfig(SCREEN_TYPE, COLOR);
                    int colorCircleDiameter = getResources().getDimensionPixelSize(R.dimen.color_circle_diameter);

                    if (config != null) {
                        if (config.hasInitialColor()) {
                            setTextColor(config.getInitialColor());
                        }
                        if (config.hasPickerConfig()) {
                            pickerConfig = config.getPickerconfig();
                        }

                        createColorSelections(view, colorCircleDiameter, config.getColors());
                        return;
                    }

                    createColorSelections(view, colorCircleDiameter, null);
                }

                @Override
                public void applySizeConfig(ConfigManagerActions manager) {
                    SizeConfig config = (SizeConfig) manager.getScreenConfig(SCREEN_TYPE, SIZE);

                    if (config != null) {
                        if (config.hasBackgroundColor()) {
                            boxedVertical.setBackgroundColor(config.getBackgroundColor());
                        }
                        if (config.hasProgrssColor()) {
                            boxedVertical.setProgressPaint(config.getProgressColor());
                        }
                        if (config.hasMax()) {
                            boxedVertical.setMax(config.getMax());
                        }
                        if (config.hasMin()) {
                            boxedVertical.setMin(config.getMin());
                        }
                        if (config.hasStep()) {
                            boxedVertical.setStep(config.getStep());
                        }
                        if (config.hasInitialValue()) {
                            setTextSize(config.getInitialValue());
                        }
                    }
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
                                    }

                                    if (tempButton != null) {
                                        if (config.hasEnabled() && config.isEnabled()) {
                                            if (config.hasLabel() && !config.hasIcon()) {
                                                tempButton.setText(config.getLabel());
                                            }
                                            if (config.hasIcon() || (!config.hasLabel() && defaultDrawable != null)) {
                                                tempButton.setText("");
                                                tempButton.setBackground(config.hasIcon() ? config.getIcon() : defaultDrawable);
                                                ViewGroup.LayoutParams layoutParams = tempButton.getLayoutParams();
                                                layoutParams.width = layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.color_picker_height);
                                                tempButton.setLayoutParams(layoutParams);
                                                tempButton.setPadding(0, 0, 0, 0);
                                            }
                                            if (config.hasTint()) {
                                                tempButton.setBackgroundTintList(ColorStateList.valueOf(config.getTintColor()));
                                                tempButton.setTextColor(config.getTintColor());
                                            }
                                        } else if (tempButton.getParent() != null) {
                                            ((ViewGroup) tempButton.getParent()).removeView(tempButton);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    // buttons
    private void addButtons (View view) {
        Context context = view.getContext();
        int padding = getResources().getDimensionPixelOffset(R.dimen.padding);
        int height = getResources().getDimensionPixelOffset(R.dimen.color_picker_height);

        cancel = new Button(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, height);
        layoutParams.topMargin = padding;
        layoutParams.bottomMargin = padding;
        layoutParams.leftMargin = padding;
        layoutParams.rightMargin = padding;
        cancel.setLayoutParams(layoutParams);
        cancel.setText("CANCEL");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: new text entity with cancel
                if (callback != null) {
                    callback.cancelAction();
                }
                dismiss();
            }
        });

        clear = new Button(context);
        clear.setLayoutParams(layoutParams);
        clear.setText("CLEAR");
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText != null) {
                    editText.setText("");
                }
            }
        });

        save = new Button(context);
        save.setLayoutParams(layoutParams);
        save.setText("SAVE");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.updateTextEntity(editText.getText().toString(), editText.getCurrentTextColor(), (int)editText.getTextSize(), editText.getWidth());
                }
                dismiss();
            }
        });

        RelativeLayout buttons = new RelativeLayout(context);
        buttons.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        buttons.setPadding(padding, padding, padding, padding);

        LinearLayout editButtonsRight = new LinearLayout(context);
        RelativeLayout.LayoutParams containerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        containerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        editButtonsRight.setLayoutParams(containerLayoutParams);
        editButtonsRight.addView(clear);
        editButtonsRight.addView(save);

        LinearLayout editButtonsLeft = new LinearLayout(context);
        containerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        containerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        editButtonsLeft.setLayoutParams(containerLayoutParams);
        editButtonsLeft.addView(cancel);

        buttons.addView(editButtonsLeft);
        buttons.addView(editButtonsRight);

        RelativeLayout rootContainer = view.findViewById(R.id.text_editor_root);
        rootContainer.addView(buttons);
    }

    private void initListeners (View view) {
        // Tap on background
        view.findViewById(R.id.text_editor_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // apply editText properties to textView
                if (callback != null) {
                    callback.updateTextEntity(editText.getText().toString(), editText.getCurrentTextColor(), (int)editText.getTextSize(), editText.getWidth());
                }
                // exit when clicking on background
                dismiss();
            }
        });

        // Text size change
        ((BoxedVertical)view.findViewById(R.id.boxed_vertical)).setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, final int value) {
                int textSizePixel = (int)editText.getTextSize();
                int valuePixel = ConversionUtils.dpToPx(value);
                if (textSizePixel != valuePixel) {
                    setTextSize(value);
                }
            }
            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {}
            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {}
        });
    }

    private void createColorSelections (View context, int colorCircleDiameter, @Nullable ArrayList<String> colors) {
        String[] defaultColors = {"#000000", "#20BBFC", "#2DFD2F", "#FD28F9", "#EA212E", "#FD7E24", "#FFFA38", "#FFFFFF"};

        LinearLayout colorPicker = context.findViewById(R.id.color_picker);
        colorPalette = context.findViewById(R.id.color_pallette);
        if (editText != null) {
            colorPalette.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(editText.getCurrentTextColor()))); // min API 21 needed
        }
        colorPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPalette(v);
            }
        });

        for (String color: (colors != null ? colors.toArray(new String[colors.size()]) : defaultColors)) {
            final int parsedColor = Color.parseColor(color);
            LinearLayout colorButtonContainer = new LinearLayout(context.getContext());
            colorButtonContainer.setLayoutParams(new LinearLayout.LayoutParams(0,  ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            colorButtonContainer.setGravity(Gravity.CENTER);

            Button colorButton = new Button(context.getContext());
            colorButton.setLayoutParams(new LinearLayout.LayoutParams(colorCircleDiameter, colorCircleDiameter));
            colorButton.setBackgroundResource(R.drawable.circle);
            colorButton.getBackground().mutate().setTint(parsedColor); // min API 21 needed
            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTextColor(parsedColor);
                }
            });
            colorButtonContainer.addView(colorButton);
            colorPicker.addView(colorButtonContainer);
        }
        colorPicker.invalidate();
    }

    private void openColorPalette(final View v) {
        boolean hasPickerConfig = pickerConfig != null;
        ColorPickerDialogBuilder
                .with(v.getContext())
                .setTitle(hasPickerConfig && pickerConfig.hasPickerLabel() ? pickerConfig.getPickerLabel() : getResources().getString(R.string.select_color))
                .initialColor(editText != null ? editText.getCurrentTextColor() : hasPickerConfig && pickerConfig.hasInitialColor() ? pickerConfig.getInitialColor() : Color.WHITE)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(15) // magic number
                .setPositiveButton(hasPickerConfig && pickerConfig.hasSubmitText() ? pickerConfig.getSubmitText() : getResources().getString(R.string.ok), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        setTextColor(selectedColor);
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

    private void initWithTextEntity(String text) {
        editText.setText(text);
        editText.post(new Runnable() {
            @Override
            public void run() {
                if (editText != null) {
                    Selection.setSelection(editText.getText(), editText.length());
                }
            }
        });
    }

    private void setTextSize (int size) {
        if (editText != null) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
        if (boxedVertical != null) {
            boxedVertical.setValue(size);
        }
    }

    private void setTextColor (int color) {
        if (editText != null) {
            editText.setTextColor(color);
            if (colorPalette != null) {
                colorPalette.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(editText.getCurrentTextColor()))); // min API 21 needed
            }
        }
    }

    private void setEditText(boolean gainFocus) {
        if (!gainFocus) {
            editText.clearFocus();
            editText.clearComposingText();
        }
        editText.setFocusableInTouchMode(gainFocus);
        editText.setFocusable(gainFocus);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // clearing memory on exit, cos manipulating with text uses bitmaps extensively
        // this does not frees memory immediately, but still can help
        System.gc();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onDetach() {
        // release links
        callback = null;
        editText = null;
        fontProvider = null;
        boxedVertical = null;
        colorPalette = cancel = clear = save = null;
        pickerConfig = null;

        super.onDetach();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // remove background
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                //set to adjust screen height automatically, when soft keyboard appears on screen
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        editText.post(new Runnable() {
            @Override
            public void run() {
                // force show the keyboard
                setEditText(true);
                editText.requestFocus();
                InputMethodManager ims = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                ims.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}