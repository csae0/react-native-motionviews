package team.uptech.motionviews.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import team.uptech.motionviews.BuildConfig;
import team.uptech.motionviews.R;
import team.uptech.motionviews.utils.ConversionUtils;
import team.uptech.motionviews.utils.FontProvider;
import team.uptech.motionviews.viewmodel.Font;
import team.uptech.motionviews.viewmodel.Layer;
import team.uptech.motionviews.viewmodel.TextLayer;
import team.uptech.motionviews.widget.Interfaces.Limits;
import team.uptech.motionviews.widget.Interfaces.MotionViewCallback;
import team.uptech.motionviews.widget.Interfaces.OnTextLayerCallback;
import team.uptech.motionviews.widget.MotionView;
import team.uptech.motionviews.widget.entity.ImageEntity;
import team.uptech.motionviews.widget.entity.MotionEntity;
import team.uptech.motionviews.widget.entity.TextEntity;

public class MainActivity extends AppCompatActivity implements OnTextLayerCallback {

    public static final int SELECT_STICKER_REQUEST_CODE = 123;

    protected MotionView motionView;
    private final MotionViewCallback motionViewCallback = new MotionViewCallback() {
        @Override
        public void onEntitySelected(@Nullable MotionEntity entity) {
        }

        @Override
        public void onEntityDoubleTap(@NonNull MotionEntity entity) {
        }
        @Override
        public void onEntitySingleTapConfirmed(@NonNull MotionEntity entity) {
            startTextEntityEditing();
        }
    };
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
    }

    private void addSticker(final int stickerResId, final boolean visible) {
        motionView.post(new Runnable() {
            @Override
            public void run() {
                Layer layer = new Layer();
                Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);

                ImageEntity entity = new ImageEntity(layer, pica, motionView.getWidth(), motionView.getHeight(), visible);

                motionView.addEntityAndPosition(entity);
            }
        });
    }

    private void startTextEntityEditing() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.setVisible(false);
            TextLayer textLayer = textEntity.getLayer();

            String text = textLayer.getText();
            int size = (int)(textLayer.getFont().getSize() + 0.5f);
            int color = textLayer.getFont().getColor();
            String typefaceName = textLayer.getFont().getTypeface();
            TextEditorDialogFragment fragment = TextEditorDialogFragment.getInstance(text, size, color, typefaceName);
            fragment.show(getFragmentManager(), TextEditorDialogFragment.class.getName());
        }
    }

    @Nullable
    private TextEntity currentTextEntity() {
        if (motionView != null && motionView.getSelectedEntity() instanceof TextEntity) {
            return ((TextEntity) motionView.getSelectedEntity());
        } else {
            return null;
        }
    }

    public void addSticker(View v) {
        Intent intent = new Intent(this, StickerSelectActivity.class);
        startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
    }
    public void addTextSticker(View v) {
        addTextSticker(false);
    }

    protected void addTextSticker(boolean visible) {
        TextLayer textLayer = createTextLayer();
        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(),
                motionView.getHeight(), fontProvider, visible);
        motionView.addEntityAndPosition(textEntity);

        // move text sticker up so that its not hidden under keyboard
        PointF center = textEntity.absoluteCenter();
        center.y = center.y * 0.5F;
        textEntity.moveCenterTo(center);

        // redraw
        motionView.invalidate();

        startTextEntityEditing();
    }

    private TextLayer createTextLayer() {
        TextLayer textLayer = new TextLayer();
        Font font = new Font();

        font.setColor(Limits.INITIAL_FONT_COLOR);
        font.setSize(ConversionUtils.dpToPx(Limits.FONT_SIZE_INITIAL_DP));
        font.setTypeface(fontProvider.getDefaultFontName());

        textLayer.setFont(font);

        if (BuildConfig.DEBUG) {
            textLayer.setText("DEBUG");
        }

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

    @Override
    public void multiTextChange(@Nullable String text, @Nullable Integer color, @Nullable Integer sizeInPixel,  @Nullable Integer maxWidth) {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextLayer textLayer = textEntity.getLayer();
            Font font = textLayer.getFont();

            // Set text
            if (text != null && text.length() > 0 && !text.equals(textLayer.getText())) {
                textLayer.setText(text);
            }
            // Set color
            if (color != null && color != font.getColor()) {
                font.setColor(color);
            }
            // Set size
            if (sizeInPixel != null && sizeInPixel > 0 && sizeInPixel != font.getSize()) {
                font.setSize((float)sizeInPixel);
            }
            // Set maxWidth
            if (maxWidth != null && maxWidth > 0 && textEntity.getMaxWidth() != maxWidth) {
                textEntity.setMaxWidth(maxWidth);
            }
            textEntity.setVisible(true);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    @Nullable
    @Override
    public FontProvider getFontProvider() {
        return fontProvider;
    }
}
