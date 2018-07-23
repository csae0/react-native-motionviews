package at.csae0.reactnative.model;

import android.support.annotation.Nullable;

import at.csae0.reactnative.utils.CONFIG_TYPE;

public abstract class ScreenConfig extends Config {

    private CONFIG_TYPE screenType;

    public ScreenConfig (CONFIG_TYPE id, @Nullable Boolean enabled, @Nullable String screenType) {
        super(id, enabled);

        setScreenType(screenType);
    }

    public CONFIG_TYPE getScreenType() {
        return screenType;
    }

    public void setScreenType(@Nullable CONFIG_TYPE screenType) {
        if (screenType != null) {
            setScreenType(screenType.getName());
        } else {
            String s = null;
            setScreenType(s);
        }
    }

    public void setScreenType(@Nullable String screenType) {
        this.screenType = null;
        if (screenType != null) {
            this.screenType = CONFIG_TYPE.get(screenType);
        }
        if (this.screenType == null) {
            this.screenType = CONFIG_TYPE.ALL_SCREENS;
        }
    }

    public boolean isScreenType(CONFIG_TYPE screenType) {
        return this.screenType == screenType || this.screenType == CONFIG_TYPE.ALL_SCREENS;
    }
}
