package team.uptech.motionviews.viewmodel;

public class SketchLayer extends Layer {

    private Stroke stroke;

    public SketchLayer() {
        reset();
    }

    @Override
    protected void reset() {
        super.reset();
        this.stroke = new Stroke();
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

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
}