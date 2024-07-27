package app.revanced.integrations.youtube.settings.preference;

import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.youtube.patches.utils.PatchStatus.SpoofClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import app.revanced.integrations.shared.settings.Setting;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.patches.misc.WatchHistoryPatch.WatchHistoryType;
import app.revanced.integrations.youtube.patches.misc.requests.PlayerRoutes.ClientType;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public class WatchHistoryStatusPreference extends Preference {

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, str) -> {
        // Because this listener may run before the ReVanced settings fragment updates SettingsEnum,
        // this could show the prior config and not the current.
        //
        // Push this call to the end of the main run queue,
        // so all other listeners are done and SettingsEnum is up to date.
        Utils.runOnMainThread(this::updateUI);
    };

    public WatchHistoryStatusPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WatchHistoryStatusPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WatchHistoryStatusPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchHistoryStatusPreference(Context context) {
        super(context);
    }

    private void addChangeListener() {
        Setting.preferences.preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    private void removeChangeListener() {
        Setting.preferences.preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        updateUI();
        addChangeListener();
    }

    @Override
    protected void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        removeChangeListener();
    }

    private void updateUI() {
        final ClientType clientTypeIOS = ClientType.IOS;
        final boolean spoofClientEnabled = SpoofClient() && Settings.SPOOF_CLIENT.get();
        final boolean containsClientTypeIOS =
                Settings.SPOOF_CLIENT_GENERAL.get() == clientTypeIOS ||
                        Settings.SPOOF_CLIENT_LIVESTREAM.get() == clientTypeIOS ||
                        Settings.SPOOF_CLIENT_SHORTS.get() == clientTypeIOS ||
                        Settings.SPOOF_CLIENT_FALLBACK.get() == clientTypeIOS;

        final WatchHistoryType watchHistoryType = Settings.WATCH_HISTORY_TYPE.get();
        final boolean blockWatchHistory = watchHistoryType == WatchHistoryType.BLOCK;
        final boolean replaceWatchHistory = watchHistoryType == WatchHistoryType.REPLACE;

        final String summaryTextKey;
        if (blockWatchHistory) {
            summaryTextKey = "revanced_watch_history_about_status_blocked";
        } else if (spoofClientEnabled && containsClientTypeIOS) {
            summaryTextKey = replaceWatchHistory
                    ? "revanced_watch_history_about_status_ios_replaced"
                    : "revanced_watch_history_about_status_ios_original";
        } else {
            summaryTextKey = replaceWatchHistory
                    ? "revanced_watch_history_about_status_android_replaced"
                    : "revanced_watch_history_about_status_android_original";
        }

        setSummary(str(summaryTextKey));
    }
}