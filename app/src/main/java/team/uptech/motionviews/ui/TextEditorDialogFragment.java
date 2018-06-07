package team.uptech.motionviews.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
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

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import team.uptech.motionviews.R;
import team.uptech.motionviews.utils.ConversionUtils;
import team.uptech.motionviews.utils.FontProvider;
import team.uptech.motionviews.widget.Interfaces.Limits;
import team.uptech.motionviews.widget.Interfaces.OnTextLayerCallback;
import team.uptech.motionviews.widget.entity.TextEntity;

/**
 * Transparent Dialog Fragment, with no title and no background
 * <p>
 * The fragment imitates capturing input from keyboard, but does not display anything
 * the result from input from the keyboard is passed through {@link OnTextLayerCallback}
 * <p>
 * Activity that uses {@link TextEditorDialogFragment} must implement {@link OnTextLayerCallback}
 * <p>
 * If Activity does not implement {@link OnTextLayerCallback}, exception will be thrown at Runtime
 */
public class TextEditorDialogFragment extends DialogFragment {

    public static final String ARG_TEXT = "editor_text_arg";
    public static final String ARG_SIZE = "editor_size_arg";
    public static final String ARG_COLOR = "editor_color_arg";
    public static final String ARG_TYPEFACE = "editor_typeface_arg";

    protected EditText editText;
    protected FontProvider fontProvider;

    private OnTextLayerCallback callback;

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
        if (activity instanceof OnTextLayerCallback) {
            this.callback = (OnTextLayerCallback) activity;
            this.fontProvider = ((OnTextLayerCallback) activity).getFontProvider();
        } else {
            throw new IllegalStateException(activity.getClass().getName()
                    + " must implement " + OnTextLayerCallback.class.getName());
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
//        editText.setMaxWidth();
        // Slider values
        BoxedVertical boxedVertical = view.findViewById(R.id.boxed_vertical);
        boxedVertical.setValue(ConversionUtils.pxToDp(size));

        createColorSelections(view, color);
        initListeners(view);

        initWithTextEntity(text);


        editText.setTextColor(color);
        if (fontProvider != null) {
            Typeface typeface = fontProvider.getTypeface(!typefaceName.equals("") || typefaceName != null ? typefaceName : fontProvider.getDefaultFontName());
            editText.setTypeface(typeface);
        }
    }

    private void initListeners (View view) {
        // Tap on background
        view.findViewById(R.id.text_editor_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // apply editText properties to textView
                if (callback != null) {
                    callback.multiTextChange(editText.getText().toString(), editText.getCurrentTextColor(), (int)editText.getTextSize(), editText.getWidth());
                }
                // exit when clicking on background
                dismiss();
            }
        });

        // Text change
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
//        editText.addTextChangedListener(new TextWatcher() {
//            int currentLineCount = 1;
//            @Override
//            public void onTextChanged(CharSequence text, int start, int before, int count) {
//                try {
//                    int lines = editText.getLineCount(); //TODO: WAS TUT ES ?????
//                    System.out.println("----LOG---- " + lines);
//                    if (lines > currentLineCount) {
//                        currentLineCount = lines;
//                        String textWithLineBreak = text.subSequence(0, start-1).toString() + "\n" + text.charAt(start);
//                        editText.setText(textWithLineBreak);
//                        editText.setSelection(textWithLineBreak.length());// set cursor to last word
//                    } else if (lines < currentLineCount && lines > 0) {
//                        currentLineCount = lines;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    System.out.println("");
//                }
//            }
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });

        // Text size change
        ((BoxedVertical)view.findViewById(R.id.boxed_vertical)).setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, final int value) {
                int textSizePixel = (int)editText.getTextSize();
                int valuePixel = ConversionUtils.dpToPx(value);
                if (textSizePixel != valuePixel) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, value);
                }
            }
            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {}
            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {}
        });
    }

    private void createColorSelections (View context, int currentColor) {
        String[] colors = {"#000000", "#20BBFC", "#2DFD2F", "#FD28F9", "#EA212E", "#FD7E24", "#FFFA38", "#FFFFFF"};

        LinearLayout colorPicker = context.findViewById(R.id.color_picker);
        final Button colorPalette = context.findViewById(R.id.color_pallette);
        colorPalette.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(currentColor))); // min API 21 needed
        colorPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPalette(v);
            }
        });
        for (String color: colors) {
            final int parsedColor = Color.parseColor(color);
            LinearLayout colorButtonContainer = new LinearLayout(context.getContext());
            colorButtonContainer.setLayoutParams(new LinearLayout.LayoutParams(0,  ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            colorButtonContainer.setGravity(Gravity.CENTER);
            int diameter = context.getResources().getDimensionPixelSize(R.dimen.color_circle_diameter);
            Button colorButton = new Button(context.getContext());
            colorButton.setLayoutParams(new LinearLayout.LayoutParams(diameter, diameter));
            colorButton.setBackgroundResource(R.drawable.circle);
            colorButton.getBackground().mutate().setTint(parsedColor); // min API 21 needed
            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback == null) {
                        return;
                    }
                    editText.setTextColor(parsedColor);
                    colorPalette.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(editText.getCurrentTextColor()))); // min API 21 needed
                }
            });
            colorButtonContainer.addView(colorButton);
            colorPicker.addView(colorButtonContainer);
        }
        colorPicker.invalidate();
    }


    private void openColorPalette(final View v) {
        ColorPickerDialogBuilder
                .with(v.getContext())
                .setTitle(R.string.select_color)
                .initialColor(editText.getCurrentTextColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(15) // magic number
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        editText.setTextColor(selectedColor);
                        v.setBackgroundTintList(ColorStateList.valueOf(ConversionUtils.transformAlphaUpperTwoThirds(selectedColor))); // min API 21 needed
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
        this.callback = null;
        this.editText = null;
        this.fontProvider = null;

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

                // remove dim
//                WindowManager.LayoutParams windowParams = window.getAttributes();
//                window.setDimAmount(0.0F);
//                window.setAttributes(windowParams);
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

    private void setEditText(boolean gainFocus) {
        if (!gainFocus) {
            editText.clearFocus();
            editText.clearComposingText();
        }
        editText.setFocusableInTouchMode(gainFocus);
        editText.setFocusable(gainFocus);
    }
}