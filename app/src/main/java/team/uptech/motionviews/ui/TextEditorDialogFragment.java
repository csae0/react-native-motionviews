package team.uptech.motionviews.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
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

import team.uptech.motionviews.R;
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

    protected EditText editText;

    private OnTextLayerCallback callback;

    /**
     * deprecated
     * use {@link TextEditorDialogFragment#getInstance(String)}
     */
    @Deprecated
    public TextEditorDialogFragment() {
        // empty, use getInstance
    }

    public static TextEditorDialogFragment getInstance(String textValue) {
        @SuppressWarnings("deprecation")
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, textValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnTextLayerCallback) {
            this.callback = (OnTextLayerCallback) activity;
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
        if (args != null) {
            text = args.getString(ARG_TEXT);
        }

        createColorSelections(view);

        editText = (EditText) view.findViewById(R.id.edit_text_view);
        initWithTextEntity(text);
        initEditedTextColor();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (callback != null) {
                    callback.textChanged(s.toString());
                }
            }
        });

        view.findViewById(R.id.text_editor_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // exit when clicking on background
                dismiss();
            }
        });
    }

    private void createColorSelections (View context) {
        String[] colors = {"#000000", "#20BBFC", "#2DFD2F", "#FD28F9", "#EA212E", "#FD7E24", "#FFFA38", "#FFFFFF"};

        LinearLayout colorPicker = (LinearLayout) context.findViewById(R.id.color_picker);
        Button colorPallette = (Button) context.findViewById(R.id.color_pallette);
        colorPallette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPallette(v);
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
            colorButton.getBackground().mutate().setTint(parsedColor);
            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback == null) {
                        return;
                    }
                    editText.setTextColor(parsedColor);
                    callback.colorChanged(parsedColor);
                }
            });
            colorButtonContainer.addView(colorButton);
            colorPicker.addView(colorButtonContainer);
        }
        colorPicker.invalidate();
    }


    private void openColorPallette(View context) {
        if (callback == null) {
            return;
        }
        Integer initialColor = callback.currentTextColor();
        if (initialColor == null) {
            return;
        }

        ColorPickerDialogBuilder
                .with(context.getContext())
                .setTitle(R.string.select_color)
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(15) // magic number
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        editText.setTextColor(selectedColor);
                        callback.colorChanged(selectedColor);
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
    private void initEditedTextColor() {
        if (callback == null) {
           return;
        }
        Integer initialColor = callback.currentTextColor();
        if (initialColor == null) {
            return;
        }
        editText.setTextColor(initialColor);
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