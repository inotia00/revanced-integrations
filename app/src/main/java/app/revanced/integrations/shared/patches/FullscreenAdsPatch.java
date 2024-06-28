package app.revanced.integrations.shared.patches;

import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.hideViewBy0dpUnderCondition;
import static app.revanced.integrations.shared.utils.Utils.showToastShort;

import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import app.revanced.integrations.shared.settings.BaseSettings;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;

@SuppressWarnings("unused")
public class FullscreenAdsPatch {
    private static final boolean hideFullscreenAdsEnabled = BaseSettings.HIDE_FULLSCREEN_ADS.get();
    private static final boolean closeFullscreenAdsEnabled = BaseSettings.HIDE_FULLSCREEN_ADS_TYPE.get();
    private static final boolean closeDialog = hideFullscreenAdsEnabled && closeFullscreenAdsEnabled;
    private static final boolean disableDialog = hideFullscreenAdsEnabled && !closeFullscreenAdsEnabled;

    private static volatile long lastTimeClosedFullscreenAd;

    private static WeakReference<Button> buttonRef = new WeakReference<>(null);

    public static boolean disableFullscreenAds(int code) {
        if (!disableDialog) return false;

        final DialogType dialogType = DialogType.getDialogType(code);
        final String dialogName = dialogType.name();
        Logger.printDebug(() -> "DialogType: " + dialogName);

        // This method is also invoked in the 'Create new playlist' dialog,
        // in which case the DialogType is {@code DialogType.ALERT}.
        if (dialogType == DialogType.ALERT) return false;

        showToastShort(str("revanced_hide_fullscreen_ads_blocked_success", dialogName));
        return true;
    }

    public static void setCloseButton(final Button button) {
        if (!closeDialog) return;
        buttonRef = new WeakReference<>(button);
    }

    public static void closeFullscreenAds() {
        if (!closeDialog) return;

        Utils.runOnMainThreadDelayed(() -> {
            final Button button = buttonRef.get();
            if (button == null) return;
            button.callOnClick();

            final long currentTime = System.currentTimeMillis();
            if (currentTime - lastTimeClosedFullscreenAd < 10000) return;
            lastTimeClosedFullscreenAd = currentTime;

            showToastShort(str("revanced_hide_fullscreen_ads_closed_success"));
        }, 1000);
    }

    public static void hideFullscreenAds(View view) {
        hideViewBy0dpUnderCondition(
                hideFullscreenAdsEnabled,
                view
        );
    }

    private enum DialogType {
        NULL(0),
        ALERT(1),
        FULLSCREEN(2),
        LAYOUT_FULLSCREEN(3);

        private final int code;

        DialogType(int code) {
            this.code = code;
        }

        private static DialogType getDialogType(int code) {
            return Arrays.stream(values())
                    .filter(val -> code == val.code)
                    .findFirst()
                    .orElse(DialogType.NULL);
        }
    }

}