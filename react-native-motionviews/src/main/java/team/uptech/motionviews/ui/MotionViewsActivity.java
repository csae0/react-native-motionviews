package team.uptech.motionviews.ui;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sketchView.SketchFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import at.csae0.reactnative.R;
import at.csae0.reactnative.RNMotionViewModule;
import at.csae0.reactnative.interfaces.ConfigActions;
import at.csae0.reactnative.interfaces.ConfigManagerActions;
import at.csae0.reactnative.model.ButtonConfig;
import at.csae0.reactnative.model.ButtonConfigs;
import at.csae0.reactnative.model.GeneralConfig;
import at.csae0.reactnative.utils.CONFIG_TYPE;
import at.csae0.reactnative.utils.ConfigManager;

import team.uptech.motionviews.utils.ConversionUtils;
import team.uptech.motionviews.utils.FontProvider;
import team.uptech.motionviews.utils.RessourceUtils;
import team.uptech.motionviews.viewmodel.Font;
import team.uptech.motionviews.viewmodel.Layer;
import team.uptech.motionviews.viewmodel.SketchLayer;
import team.uptech.motionviews.viewmodel.Stroke;
import team.uptech.motionviews.viewmodel.TextLayer;
import team.uptech.motionviews.widget.Interfaces.EditCallback;
import team.uptech.motionviews.widget.Interfaces.Limits;
import team.uptech.motionviews.widget.Interfaces.MotionViewCallback;
import team.uptech.motionviews.widget.MotionView;
import team.uptech.motionviews.widget.entity.ImageEntity;
import team.uptech.motionviews.widget.entity.MotionEntity;
import team.uptech.motionviews.widget.entity.SketchEntity;
import team.uptech.motionviews.widget.entity.TextEntity;

public class MotionViewsActivity extends AppCompatActivity implements EditCallback {

    private static final CONFIG_TYPE SCREEN_TYPE = CONFIG_TYPE.MAIN_SCREEN;
    public static final int SELECT_STICKER_REQUEST_CODE = 123;
    protected MotionView motionView;
    private String defaultText;
    private RelativeLayout buttons;
    private LinearLayout addButtons;
    private Button addText, addImage, addSketch, cancel, submit;
    private final MotionViewCallback motionViewCallback = new MotionViewCallback() {
        @Override
        public void onEntitySelected(@Nullable MotionEntity entity) {
        }
        @Override
        public void onEntityDoubleTap(@NonNull MotionEntity entity) {
        }
        @Override
        public void onEntitySingleTapConfirmed(@NonNull MotionEntity entity) {
            MotionEntity motionEntity = motionView.getSelectedEntity();

            if (motionEntity != null) {
                motionEntity.startEditing(getThis());
            }
        }
    };

    // Workaround to access this inside callback class
    private MotionViewsActivity getThis() {
        return this;
    }

    private FontProvider fontProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = this.getIntent().getExtras();
        Bundle options = bundle.getBundle(RNMotionViewModule.OPTIONS_ID);
        // Inject additional config
        Bundle injectedOptions = new Bundle();
        injectedOptions.putInt("sideLength", getResources().getDimensionPixelOffset(R.dimen.color_picker_height));

        this.fontProvider = new FontProvider(getResources());
        this.defaultText = "";

        addButtons(this.getApplicationContext());

        motionView = findViewById(R.id.main_motion_view);
        motionView.setMotionViewCallback(motionViewCallback);
        motionView.setTrashButton((Button)findViewById(R.id.trash_button));
        // addSticker(R.drawable.pikachu_2, true);
//        addSketch(false);
        ConfigManager.create(options, injectedOptions);
        applyConfig(this.getApplicationContext());
    }

    private void applyConfig(final Context context) {
        if (ConfigManager.hasInstance()) {
            ConfigManager.getInstance().apply(new ConfigActions() {
                @Override
                public void applyGeneralConfig(GeneralConfig config) {
                    if (config.hasFontFamily() && fontProvider != null) {
                        fontProvider.addTypeface(config.getFontFamily(), config.getFontFamily());
                        fontProvider.setDefaultFontName(config.getFontFamily());
                    }

                    if (config.hasInitialText()) {
                       defaultText = config.getInitialText();
                    }

                    if (config.hasBackgroundDrawable()) {
                        RelativeLayout rootView = findViewById(R.id.activity_main);
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmapOptions.outWidth = rootView.getWidth();
                        Drawable backgroundDrawable = config.getBackgroundDrawable(getApplicationContext(), bitmapOptions);
                        rootView.setBackground(backgroundDrawable);
                    }
                }

                @Override
                public void applyColorConfig(ConfigManagerActions manager) {
                }

                @Override
                public void applySizeConfig(ConfigManagerActions manager) {
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
                                        case SAVE_BUTTON_CONFIG:
                                            tempButton = submit;
                                            defaultDrawable = RessourceUtils.getImageRessource("ic_save");
                                            break;
                                        case CREATE_SKETCH_CONFIG:
                                            tempButton = addSketch;
                                            defaultDrawable = RessourceUtils.getImageRessource("ic_touch");
                                            break;
                                        case CREATE_TEXT_CONFIG:
                                            tempButton = addText;
                                            defaultDrawable = RessourceUtils.getImageRessource("ic_text_field");
                                            break;
                                        case CREATE_STICKER_CONFIG:
                                            tempButton = addImage;
                                            defaultDrawable = RessourceUtils.getImageRessource("ic_add");
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

    // buttons
    private void addButtons(Context context) {
        buttons = null;
        addButtons = null;
        addText = addImage = addSketch = cancel = submit = null;

        int padding = getResources().getDimensionPixelOffset(R.dimen.padding);
        int height = getResources().getDimensionPixelOffset(R.dimen.color_picker_height);
        int marginVertical = getResources().getDimensionPixelOffset(R.dimen.slider_margin_vertical);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, height);
        layoutParams.topMargin = padding;
        layoutParams.bottomMargin = padding;
        layoutParams.leftMargin = padding;
        layoutParams.rightMargin = padding;

        cancel = new Button(context);
        cancel.setLayoutParams(layoutParams);
        cancel.setText("CANCEL");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(v);
            }
        });
        submit = new Button(context);
        submit.setLayoutParams(layoutParams);
        submit.setText("SUBMIT");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v);
            }
        });

        buttons = new RelativeLayout(context);
        buttons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        buttons.setPadding(padding, padding, padding, padding);

        LinearLayout editButtonsRight = new LinearLayout(context);
        RelativeLayout.LayoutParams containerLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        editButtonsRight.setLayoutParams(containerLayoutParams);
        editButtonsRight.addView(submit);

        LinearLayout editButtonsLeft = new LinearLayout(context);
        containerLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        editButtonsLeft.setLayoutParams(containerLayoutParams);
        editButtonsLeft.addView(cancel);

        addSketch = new Button(context);
        addSketch.setLayoutParams(layoutParams);
        addSketch.setText("SKETCH");
        addSketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSketch(v);
            }
        });

        addText = new Button(context);
        addText.setLayoutParams(layoutParams);
        addText.setText("TEXT");
        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextSticker(v);
            }
        });
        addImage = new Button(context);
        addImage.setLayoutParams(layoutParams);
        addImage.setText("IMAGE");
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSticker(v);
            }
        });

        addButtons = new LinearLayout(context);
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.topMargin = marginVertical;
        addButtons.setLayoutParams(layoutParams);
        addButtons.setGravity(Gravity.RIGHT);
        addButtons.setPadding(padding, padding, padding, padding);
        addButtons.setOrientation(LinearLayout.VERTICAL);
        addButtons.addView(addSketch);
        addButtons.addView(addText);
        addButtons.addView(addImage);

        buttons.addView(editButtonsRight);
        buttons.addView(editButtonsLeft);

        RelativeLayout rootView = findViewById(R.id.activity_main);
        rootView.addView(buttons);
        rootView.addView(addButtons);
    }

    public void cancel(View v) {
        // TODO: go back to react
        //        Intent intent = new Intent(v.getContext(), StickerSelectActivity.class);
        //        startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
    }
    public void submit(View v) {
        // TODO: go back to react and save image if exists
        //        Intent intent = new Intent(v.getContext(), StickerSelectActivity.class);
        //        startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
    }

    private void addSticker(final int stickerResId, final boolean visible) {
        motionView.post(new Runnable() {
            @Override
            public void run() {
                Layer layer = new Layer();
                Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);
                ImageEntity imageEntity = new ImageEntity(layer, pica, motionView.getWidth(), motionView.getHeight(), visible);
                motionView.addEntityAndPosition(imageEntity);

                imageEntity.startEditing(null); // Does nothing for imageEntity
            }
        });
    }

    public void addSticker(View v) {
        Intent intent = new Intent(v.getContext(), StickerSelectActivity.class);
        startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
    }

    public void addSketch(View v) {
        addSketch(false);
    }

    protected void addSketch(boolean visible) {
        SketchLayer sketchLayer = createSketchLayer();
        SketchEntity sketchEntity = new SketchEntity(sketchLayer, motionView.getWidth(), motionView.getHeight(), visible);
        motionView.addEntityAndPosition(sketchEntity);

        showButtons(false);
        sketchEntity.startEditing(this);

    }

    private SketchLayer createSketchLayer() {
        SketchLayer sketchLayer = new SketchLayer();
        Stroke stroke = new Stroke();
        stroke.setColor(Limits.INITIAL_SKETCH_COLOR);
        stroke.setSize(ConversionUtils.dpToPx(Limits.FONT_SIZE_INITIAL_DP));

        sketchLayer.setStroke(stroke);

        return sketchLayer;
    }

    public void addTextSticker(View v) {
        addTextSticker(false);
    }

    protected void addTextSticker(boolean visible) {
        TextLayer textLayer = createTextLayer();
        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(), motionView.getHeight(), fontProvider, visible);
        motionView.addEntityAndPosition(textEntity);

        showButtons(false);
        textEntity.startEditing(this);
    }

    private TextLayer createTextLayer() {
        TextLayer textLayer = new TextLayer();
        Font font = new Font();
        font.setColor(Limits.INITIAL_FONT_COLOR);
        font.setSize(ConversionUtils.dpToPx(Limits.FONT_SIZE_INITIAL_DP));
        font.setTypeface(fontProvider.getDefaultFontName());

        textLayer.setFont(font);
        textLayer.setText(defaultText);

        return textLayer;
    }

    public void showButtons (boolean show) {
        ViewGroup[] viewGroups = new ViewGroup[]{ buttons, addButtons };
        for(ViewGroup viewGroup: viewGroups) {
            if (viewGroup != null) {
                viewGroup.setEnabled(show);
                viewGroup.setAlpha(show ? 1.0f : 0.0f);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_STICKER_REQUEST_CODE) {
                if (data != null) {
                    int stickerId = data.getIntExtra(StickerSelectActivity.EXTRA_STICKER_ID, 0);
                    if (stickerId != 0) {
                        addSticker(stickerId, true);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public FontProvider getFontProvider() {
        return fontProvider;
    }

    // TODO: verschiebe das Interface in die MotionView (momentan wegen dem Fragment in der Main_Activity)
    @Override
    public void updateTextEntity(@Nullable String text, @Nullable Integer color, @Nullable Integer sizeInPixel, @Nullable Integer maxWidth) {
        showButtons(true);
        MotionEntity motionEntity = motionView.getSelectedEntity();
        if (motionEntity != null && motionEntity instanceof TextEntity) {
            if (text == null || text.length() == 0) {
                motionView.deleteSelectedEntity(); // includes invalidate
            } else {
                ((TextEntity) motionEntity).updateState(text, color, sizeInPixel, maxWidth);
                motionView.invalidate();
            }
        }
    }

    @Override
    public void updateSketchEntity(@Nullable Bitmap bitmap, @Nullable Rect position, @Nullable Integer color, @Nullable Integer sizeInPixel) {
        showButtons(true);
        MotionEntity motionEntity = motionView.getSelectedEntity();

        if (motionEntity != null && motionEntity instanceof SketchEntity) {
            if (bitmap == null) {
                motionView.deleteSelectedEntity(); // includes invalidate
                motionView.setHideAllEntities(false);
            } else {
                ((SketchEntity) motionEntity).updateState(bitmap, position, color, sizeInPixel);
                motionView.invalidate();
            }
        }
    }

    @Override
    public void cancelAction() {
        showButtons(true);
        motionView.setHideAllEntities(false);
        MotionEntity motionEntity = motionView.getSelectedEntity();
        motionEntity.setVisible(true);

        if (motionEntity != null) {
            if (motionEntity instanceof TextEntity){
                String text = ((TextEntity)motionEntity).getLayer().getText();
                if (text == null || text.length() == 0) {
                    motionView.deleteSelectedEntity(); // includes invalidate
                }
            }
        }
    }

    /**
     * Save image on device
     *
     * @return
     * @throws IOException
     */
//    public SketchFile saveToLocalCache() throws IOException {
//        if (sketchView != null) {
//            Bitmap viewBitmap = Bitmap.createBitmap(sketchView.getWidth(), sketchView.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(viewBitmap);
//            draw(canvas);
//
//            File cacheFile = File.createTempFile("sketch_", UUID.randomUUID().toString() + ".png");
//            FileOutputStream imageOutput = new FileOutputStream(cacheFile);
//            viewBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutput);
//
//            SketchFile sketchFile = new SketchFile();
//            sketchFile.localFilePath = cacheFile.getAbsolutePath();
//            ;
//            sketchFile.width = viewBitmap.getWidth();
//            sketchFile.height = viewBitmap.getHeight();
//            return sketchFile;
//        }
//        return null;
//    }
//    @Nullable
//    public String getBase64() {
//        if (sketchView != null) {
//            Bitmap viewBitmap = Bitmap.createBitmap(sketchView.getWidth(), sketchView.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(viewBitmap);
//            draw(canvas);
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            viewBitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream);
//            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
//        }
//        return null;
//    }
//    public boolean openSketchFile(String localFilePath) {
//        if (sketchView != null) {
//            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//            bitmapOptions.outWidth = sketchView.getWidth();
//            Bitmap bitmap = BitmapFactory.decodeFile(localFilePath, bitmapOptions);
//            if (bitmap != null) {
//                sketchView.setViewImage(bitmap);
//                return true;
//            }
//        }
//        return false;
//    }
}
