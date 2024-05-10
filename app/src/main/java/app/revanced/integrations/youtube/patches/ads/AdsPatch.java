package app.revanced.integrations.youtube.patches.ads;

import static app.revanced.integrations.shared.utils.Utils.hideViewBy0dpUnderCondition;

import android.view.View;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public class AdsPatch {
    private static final String DIALOG_DATA_EXCEPTION = "ALERT";
    private static final Boolean hideFullscreenAdsEnabled = Settings.HIDE_FULLSCREEN_ADS.get();
    private static final Boolean hideGeneralAdsEnabled = Settings.HIDE_GENERAL_ADS.get();
    private static final Boolean hideGetPremiumAdsEnabled = Settings.HIDE_GET_PREMIUM.get();
    private static final Boolean hideVideoAdsEnabled = Settings.HIDE_VIDEO_ADS.get();

    // region [Hide ads] patch

    /**
     * Injection point.
     * Hide the view, which shows ads in the homepage.
     *
     * @param view The view, which shows ads.
     */
    public static void hideAdAttributionView(View view) {
        hideViewBy0dpUnderCondition(hideGeneralAdsEnabled, view);
    }

    /**
     * Injection point.
     */
    public static boolean hideFullscreenAds(Object object) {
        final String dialogData = object.toString();
        Logger.printDebug(() -> dialogData);
        return hideFullscreenAdsEnabled && !Utils.containsAny(dialogData, DIALOG_DATA_EXCEPTION);
    }

    /**
     * Injection point.
     * Hide the view, which shows fullscreen ads in the homepage.
     *
     * @param view The view, which shows fullscreen ads.
     */
    public static void hideFullscreenAds(View view) {
        hideViewBy0dpUnderCondition(hideFullscreenAdsEnabled, view);
    }

    public static boolean hideGetPremium() {
        return hideGetPremiumAdsEnabled;
    }

    /**
     * Injection point.
     */
    public static boolean hideVideoAds() {
        return !hideVideoAdsEnabled;
    }

    /**
     * Injection point.
     * <p>
     * Only used by old clients.
     * It is presumed to have been deprecated, and if it is confirmed that it is no longer used, remove it.
     */
    public static boolean hideVideoAds(boolean original) {
        return !hideVideoAdsEnabled && original;
    }

    // endregion

}
