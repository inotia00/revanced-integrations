package app.revanced.integrations.youtube.patches.utils;

import static app.revanced.integrations.shared.settings.preference.AbstractPreferenceFragment.showRestartDialog;
import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.runOnMainThreadDelayed;

import android.app.Activity;
import android.app.AlertDialog;

import androidx.annotation.NonNull;

import app.revanced.integrations.shared.settings.BaseSettings;
import app.revanced.integrations.shared.settings.BooleanSetting;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.utils.ExtendedUtils;

@SuppressWarnings("unused")
public class InitializationPatch {
    private static final BooleanSetting SETTINGS_INITIALIZED = BaseSettings.SETTINGS_INITIALIZED;
    private static final BooleanSetting SPOOF_APP_VERSION = Settings.SPOOF_APP_VERSION;
    private static final boolean hasRollingNumberIssue = Settings.SPOOF_APP_VERSION_TARGET.defaultValue.startsWith("19");

    /**
     * Some layouts that depend on litho do not load when the app is first installed.
     * (Also reproduced on unPatched YouTube)
     * <p>
     * To fix this, show the restart dialog when the app is installed for the first time.
     */
    public static void onCreate(@NonNull Activity mActivity) {
        if (SETTINGS_INITIALIZED.get()) {
            return;
        }
        showSpoofAppVersionDialog(mActivity);
        runOnMainThreadDelayed(() -> {
            if (hasRollingNumberIssue) {
                showSpoofAppVersionDialog(mActivity);
            } else {
                showRestartDialog(mActivity, str("revanced_extended_restart_first_run"), 3500);
            }
        }, 500);
        runOnMainThreadDelayed(() -> SETTINGS_INITIALIZED.save(true), 1000);
    }

    public static void setExtendedUtils(@NonNull Activity mActivity) {
        ExtendedUtils.setApplicationLabel();
        ExtendedUtils.setSmallestScreenWidthDp();
        ExtendedUtils.setVersionName();
        ExtendedUtils.setPlayerFlyoutMenuAdditionalSettings();
    }

    private static void showSpoofAppVersionDialog(@NonNull Activity mActivity) {
        new AlertDialog.Builder(mActivity)
                .setMessage(str("revanced_extended_restart_first_run_rolling_number"))
                .setPositiveButton(android.R.string.ok, (dialog, id)
                        -> Utils.runOnMainThreadDelayed(() -> {
                            SPOOF_APP_VERSION.save(true);
                            Utils.restartApp(mActivity);
                            }, 3500))
                .setNegativeButton(android.R.string.cancel, (dialog, id)
                        -> Utils.runOnMainThreadDelayed(() -> Utils.restartApp(mActivity), 3500))
                .setCancelable(false)
                .show();
    }
}