package app.revanced.integrations.youtube.patches.utils;

import static app.revanced.integrations.shared.settings.preference.AbstractPreferenceFragment.showRestartDialog;
import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.runOnMainThreadDelayed;

import android.app.Activity;

import androidx.annotation.NonNull;

import app.revanced.integrations.shared.settings.BaseSettings;
import app.revanced.integrations.youtube.utils.ExtendedUtils;

@SuppressWarnings("unused")
public class InitializationPatch {

    /**
     * Some layouts that depend on litho do not load when the app is first installed.
     * (Also reproduced on unPatched YouTube)
     * <p>
     * To fix this, show the restart dialog when the app is installed for the first time.
     */
    public static void onCreate(@NonNull Activity mActivity) {
        if (BaseSettings.SETTINGS_INITIALIZED.get())
            return;
        runOnMainThreadDelayed(() -> showRestartDialog(mActivity, str("revanced_extended_restart_first_run"), 3500), 500);
        runOnMainThreadDelayed(() -> BaseSettings.SETTINGS_INITIALIZED.save(true), 1000);
    }

    public static void setExtendedUtils(@NonNull Activity mActivity) {
        ExtendedUtils.setApplicationLabel();
        ExtendedUtils.setSmallestScreenWidthDp();
        ExtendedUtils.setVersionName();
        ExtendedUtils.setPlayerFlyoutMenuAdditionalSettings();
    }
}