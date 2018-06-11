package team.uptech.motionviews.viewmodel;

public class TextLayer extends Layer {

    private String text;
    private Font font;

    public TextLayer() {
    }

    @Override
    protected void reset() {
        super.reset();
        this.text = "";
        this.font = new Font();
    }

    @Override
    public float getMaxScale() {
        return team.uptech.motionviews.widget.Interfaces.Limits.MAX_SCALE;
    }

    @Override
    public float getMinScale() {
        return team.uptech.motionviews.widget.Interfaces.Limits.MIN_SCALE;
    }

    @Override
    public float initialScale() {
        return team.uptech.motionviews.widget.Interfaces.Limits.INITIAL_SCALE;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }
}