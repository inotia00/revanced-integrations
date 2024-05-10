package app.revanced.integrations.music.patches.ads;

import static app.revanced.integrations.shared.utils.Utils.hideViewBy0dpUnderCondition;

import android.view.View;

import app.revanced.integrations.music.settings.Settings;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;

@SuppressWarnings("unused")
public class FullscreenAdsPatch {
    private static final String DIALOG_DATA_EXCEPTION = "ALERT";
    private static final Boolean hideFullscreenAdsEnabled = Settings.HIDE_FULLSCREEN_ADS.get();

    public static boolean hideFullscreenAds(Object object) {
        final String dialogData = object.toString();
        Logger.printDebug(() -> dialogData);
        return hideFullscreenAdsEnabled && !Utils.containsAny(dialogData, DIALOG_DATA_EXCEPTION);
    }

    public static void hideFullscreenAds(View view) {
        hideViewBy0dpUnderCondition(
                hideFullscreenAdsEnabled,
                view
        );
    }

    //
    // public static void confirmDialog(final Button button) {
    //     if (Settings.HIDE_FULLSCREEN_ADS.get() && button != null) {
    //         button.setSoundEffectsEnabled(false);
    //         button.performClick();
    //     }
    // }

}