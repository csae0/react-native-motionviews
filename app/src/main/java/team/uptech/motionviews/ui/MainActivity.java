package team.uptech.motionviews.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import team.uptech.motionviews.R;
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

public class MainActivity extends AppCompatActivity implements EditCallback {

    public static final int SELECT_STICKER_REQUEST_CODE = 123;

    protected MotionView motionView;

    // TODO: verschiebe das Interface in die MotionView (momentan wegen dem Fragment in der Main_Activity)
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
    private MainActivity getThis() {
        return this;
    }

    private FontProvider fontProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.fontProvider = new FontProvider(getResources());

        motionView = findViewById(R.id.main_motion_view);
        motionView.setMotionViewCallback(motionViewCallback);
        motionView.setTrashButton((Button)findViewById(R.id.trash_button));
        addSticker(R.drawable.pikachu_2, true);
        addSketch(true);
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
        Intent intent = new Intent(this, StickerSelectActivity.class);
        startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
    }

    public void addSketch(View v) {
        addSketch(false);
    }

    protected void addSketch(boolean visible) {
        SketchLayer sketchLayer = createSketchLayer();
        SketchEntity sketchEntity = new SketchEntity(sketchLayer, motionView.getWidth(), motionView.getHeight(), visible);
        motionView.addEntityAndPosition(sketchEntity);

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

        textEntity.startEditing(this);
    }

    private TextLayer createTextLayer() {
        TextLayer textLayer = new TextLayer();
        Font font = new Font();
        font.setColor(Limits.INITIAL_FONT_COLOR);
        font.setSize(ConversionUtils.dpToPx(Limits.FONT_SIZE_INITIAL_DP));
        font.setTypeface(fontProvider.getDefaultFontName());

        textLayer.setFont(font);
        textLayer.setText("DEBUG");

        return textLayer;
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
    public void updateEntity(@Nullable String text, @Nullable Integer color, @Nullable Integer sizeInPixel, @Nullable Integer maxWidth) {
        MotionEntity motionEntity = motionView.getSelectedEntity();
        if (motionEntity != null && motionEntity instanceof TextEntity) {
            ((TextEntity) motionEntity).updateState(text, color, sizeInPixel, maxWidth);
            motionView.invalidate();
        }
    }

    @Override
    public void updateEntity(Bitmap bitmap, @Nullable Integer color, @Nullable Integer sizeInPixel) {
        MotionEntity motionEntity = motionView.getSelectedEntity();
        if (motionEntity != null && motionEntity instanceof SketchEntity) {
            ((SketchEntity) motionEntity).updateState(bitmap, color, sizeInPixel);
            motionView.invalidate();
        }
    }
}
