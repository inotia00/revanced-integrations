package app.revanced.integrations.music.patches.misc;

import app.revanced.integrations.music.settings.Settings;

@SuppressWarnings("unused")
public class CairoSplashAnimationPatch {

    public static boolean enableCairoSplashAnimation() {
        return Settings.ENABLE_CAIRO_SPLASH_ANIMATION.get();
    }
}
