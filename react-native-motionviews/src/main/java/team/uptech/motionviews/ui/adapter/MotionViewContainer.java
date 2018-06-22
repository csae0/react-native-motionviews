package team.uptech.motionviews.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import team.uptech.motionviews.R;
import team.uptech.motionviews.ui.StickerSelectActivity;
import team.uptech.motionviews.utils.ConversionUtils;
import team.uptech.motionviews.utils.FontProvider;
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

public class MotionViewContainer extends LinearLayout { //        implements EditCallback {
//    public static final int SELECT_STICKER_REQUEST_CODE = 123;
//    protected MotionView motionView;
//    private FontProvider fontProvider;
//    private final MotionViewCallback motionViewCallback;

    public MotionViewContainer(Context context) {
        super(context);
//        this.addView(findViewById(R.layout.activity_main));
//        this.fontProvider = new FontProvider(getResources());
//
//        motionView = findViewById(R.id.main_motion_view);
//        motionView.setMotionViewCallback(motionViewCallback);
//        motionView.setTrashButton((Button)findViewById(R.id.trash_button));
//        addSticker(R.drawable.pikachu_2, true);
//        // addSketch(false);
//        // TODO: verschiebe das Interface in die MotionView (momentan wegen dem Fragment in der Main_Activity)
//        motionViewCallback = new MotionViewCallback() {
//            @Override
//            public void onEntitySelected(@Nullable MotionEntity entity) {
//            }
//            @Override
//            public void onEntityDoubleTap(@NonNull MotionEntity entity) {
//            }
//            @Override
//            public void onEntitySingleTapConfirmed(@NonNull MotionEntity entity) {
//                MotionEntity motionEntity = motionView.getSelectedEntity();
//
//                if (motionEntity != null) {
//                    motionEntity.startEditing(getThis());
//                }
//            }
//        };
    }

    // Workaround to access this inside callback class
//    private team.uptech.motionviews.ui.MainActivity getThis() {
//        return this;
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        this.fontProvider = new FontProvider(getResources());
//
//        motionView = findViewById(R.id.main_motion_view);
//        motionView.setMotionViewCallback(motionViewCallback);
//        motionView.setTrashButton((Button)findViewById(R.id.trash_button));
//        addSticker(R.drawable.pikachu_2, true);
//        // addSketch(false);
//    }

//    private void addSticker(final int stickerResId, final boolean visible) {
//        motionView.post(new Runnable() {
//            @Override
//            public void run() {
//                Layer layer = new Layer();
//                Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);
//                ImageEntity imageEntity = new ImageEntity(layer, pica, motionView.getWidth(), motionView.getHeight(), visible);
//                motionView.addEntityAndPosition(imageEntity);
//
//                imageEntity.startEditing(null); // Does nothing for imageEntity
//            }
//        });
//    }
//
//    public void addSticker(View v) {
//        Intent intent = new Intent(this, StickerSelectActivity.class);
//        startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
//    }
//
//    public void addSketch(View v) {
//        addSketch(false);
//    }
//
//    protected void addSketch(boolean visible) {
//        SketchLayer sketchLayer = createSketchLayer();
//        SketchEntity sketchEntity = new SketchEntity(sketchLayer, motionView.getWidth(), motionView.getHeight(), visible);
//        motionView.addEntityAndPosition(sketchEntity);
//
//        showButtons(false);
//        sketchEntity.startEditing(this);
//
//    }
//
//    private SketchLayer createSketchLayer() {
//        SketchLayer sketchLayer = new SketchLayer();
//        Stroke stroke = new Stroke();
//        stroke.setColor(Limits.INITIAL_SKETCH_COLOR);
//        stroke.setSize(ConversionUtils.dpToPx(Limits.FONT_SIZE_INITIAL_DP));
//
//        sketchLayer.setStroke(stroke);
//
//        return sketchLayer;
//    }
//
//    public void addTextSticker(View v) {
//        addTextSticker(false);
//    }
//
//    protected void addTextSticker(boolean visible) {
//        TextLayer textLayer = createTextLayer();
//        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(), motionView.getHeight(), fontProvider, visible);
//        motionView.addEntityAndPosition(textEntity);
//
//        showButtons(false);
//        textEntity.startEditing(this);
//    }
//
//    private TextLayer createTextLayer() {
//        TextLayer textLayer = new TextLayer();
//        Font font = new Font();
//        font.setColor(Limits.INITIAL_FONT_COLOR);
//        font.setSize(ConversionUtils.dpToPx(Limits.FONT_SIZE_INITIAL_DP));
//        font.setTypeface(fontProvider.getDefaultFontName());
//
//        textLayer.setFont(font);
//        textLayer.setText("DEBUG");
//
//        return textLayer;
//    }
//
//    public void showButtons (boolean show) {
//        ImageButton[] addButtons = { findViewById(R.id.main_add_text), findViewById(R.id.main_add_image), findViewById(R.id.main_add_sketch)};
//
//        for(ImageButton button : addButtons) {
//            if (button != null) {
//                button.setEnabled(show);
//                button.setAlpha(show ? 1.0f : 0.0f);
//            }
//        }
//
//
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_STICKER_REQUEST_CODE) {
//                if (data != null) {
//                    int stickerId = data.getIntExtra(StickerSelectActivity.EXTRA_STICKER_ID, 0);
//                    if (stickerId != 0) {
//                        addSticker(stickerId, true);
//                    }
//                }
//            }
//        }
//    }
//
//    @Nullable
//    @Override
//    public FontProvider getFontProvider() {
//        return fontProvider;
//    }
//
//    // TODO: verschiebe das Interface in die MotionView (momentan wegen dem Fragment in der Main_Activity)
//    @Override
//    public void updateEntity(@Nullable String text, @Nullable Integer color, @Nullable Integer sizeInPixel, @Nullable Integer maxWidth) {
//        showButtons(true);
//        MotionEntity motionEntity = motionView.getSelectedEntity();
//        if (motionEntity != null && motionEntity instanceof TextEntity) {
//            if (text == null || text.length() == 0) {
//                motionView.deleteSelectedEntity(); // includes invalidate
//            } else {
//                ((TextEntity) motionEntity).updateState(text, color, sizeInPixel, maxWidth);
//                motionView.invalidate();
//            }
//        }
//    }
//
//    @Override
//    public void updateEntity(@Nullable Bitmap bitmap, @Nullable Rect position, @Nullable Integer color, @Nullable Integer sizeInPixel) {
//        showButtons(true);
//        MotionEntity motionEntity = motionView.getSelectedEntity();
//
//        if (motionEntity != null && motionEntity instanceof SketchEntity) {
//            if (bitmap == null) {
//                motionView.deleteSelectedEntity(); // includes invalidate
//            } else {
//                ((SketchEntity) motionEntity).updateState(bitmap, position, color, sizeInPixel);
//                motionView.invalidate();
//            }
//        }
//    }
//
//    @Override
//    public void cancelAction() {
//        showButtons(true);
//        MotionEntity motionEntity = motionView.getSelectedEntity();
//        motionEntity.setVisible(true);
//
//        if (motionEntity != null) {
//            if (motionEntity instanceof TextEntity){
//                String text = ((TextEntity)motionEntity).getLayer().getText();
//                if (text == null || text.length() == 0) {
//                    motionView.deleteSelectedEntity(); // includes invalidate
//                }
//            }
//        }
//    }
}
